package tlmi.communcator.atlmiclient;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.UUID;

import ac4y.command.domain.Ac4yCommand;
import ac4y.command.message.domain.Ac4yCMDMessage;
import ac4y.command.service.domain.Ac4yCMDServiceResponse;
import ac4y.base.domain.Ac4y;
import ac4y.gate.service.client.Ac4yGateServiceClient;
import ac4y.gate.service.domain.GateInsertUserRequest;
import ac4y.gate.service.domain.GateInsertUserResponse;
import ac4y.gate.service.domain.GateLoginRequest;
import ac4y.gate.service.domain.GateLoginResponse;
import tlmi.communcator.atlmiclient.command.domain.TlmiCMDInvitation;
import tlmi.communcator.atlmiclient.command.domain.TlmiCMDInvitationAccept;
import tlmi.communcator.atlmiclient.command.domain.TlmiMessage;
import tlmi.communcator.atlmiclient.utility.ScreenMessageHandler;
import tlmi.communcator.atlmiclient.view.IMainView;
import tlmi.service.client.TlmiServiceClient;
import tlmi.service.domain.Text2TextRequest;
import tlmi.service.domain.Text2TextResponse;
import tlmi.user.domain.TlmiTranslateUser;
import tlmi.user.service.client.TlmiUserServiceClient;
import tlmi.user.service.domain.GetAllTranslateUsersResponse;
import tlmi.user.service.domain.GetTranslateUserByNameRequest;
import tlmi.user.service.domain.GetTranslateUserByNameResponse;
import tlmi.user.service.domain.InsertUserRequest;
import tlmi.user.service.domain.InsertUserResponse;

public class MainController {

    private static final Logger LOG = LogManager.getLogger(MainController.class);

    private final IMainView view;
    private final AppEnvironmentVariableHandler env;
    private final ScreenMessageHandler screenMessageHandler;
    private final ServerConfig serverConfig;
    private WebSocketClient webSocketClient;

    public MainController(IMainView view) {
        this(view, false, ServerConfig.production());
    }

    public MainController(IMainView view, boolean cliMode) {
        this(view, cliMode, ServerConfig.production());
    }

    public MainController(IMainView view, boolean cliMode, ServerConfig serverConfig) {
        this.view = view;
        this.env = new AppEnvironmentVariableHandler();
        this.screenMessageHandler = new ScreenMessageHandler(cliMode);
        this.serverConfig = serverConfig;
        LOG.trace("ServerConfig: {}", serverConfig);
    }

    public void initialize() {
        LOG.trace(">>> initialize() START");

        // Check internet
        LOG.trace("step 1: checking server connection...");
        if (checkInternet()) {
            view.setInternetStatus(true);
            trace("server is reachable");
        } else {
            view.setInternetStatus(false);
            trace("server is not reachable");
            screenMessageHandler.errorNotifying("Cannot reach server: " + serverConfig.getGateServiceUrl());
        }

        // Set user ID and locale
        LOG.trace("step 2: setting up user identity...");
        env.getUserId().getSet(UUID.randomUUID().toString());
        LOG.trace("userId generated: {}", env.getUserId().get());

        Locale locale = Locale.getDefault();
        trace("locale.getCountry:" + locale.getCountry());
        trace("locale.getLanguage:" + locale.getLanguage());

        env.getUserLanguage().set(locale.getLanguage());
        env.getUserCountry().set(locale.getCountry());
        env.disableTextToSpeechDisabler();

        // Check/create user on user service (graceful — may not be available)
        LOG.trace("step 3: checking if user exists on user service...");
        GetTranslateUserByNameResponse checkResponse =
                tryGetTranslateUserByName(new GetTranslateUserByNameRequest(env.getUserId().get()));

        if (checkResponse.itWasFailed()) {
            LOG.trace("step 3a: user not found on user service (or service unavailable), creating on gate...");
            TlmiTranslateUser tlmiTranslateUser = new TlmiTranslateUser();
            tlmiTranslateUser.setName(env.getUserId().get());
            tlmiTranslateUser.setPassword("1");

            // Try user service insert (may fail if not available)
            tryInsertUser(new InsertUserRequest(tlmiTranslateUser));
            // Gate insert (should work on localhost)
            tryGateInsertUser(new GateInsertUserRequest(tlmiTranslateUser.getName(), tlmiTranslateUser.getPassword()));
        }

        LOG.trace("step 3b: ensuring gate registration...");
        tryGateInsertUser(new GateInsertUserRequest(env.getUserId().get(), "1"));

        // Login
        LOG.trace("step 4: logging in via gate...");
        GateLoginResponse loginResponse = tryLogin(new GateLoginRequest(env.getUserId().get(), "1"));

        if (loginResponse.getResult().itWasSuccessful()) {
            view.setLoginStatus(true);
            trace("login successed!");
        } else {
            view.setLoginStatus(false);
            trace("you dont have permission to login");
            LOG.trace(">>> initialize() STOPPED (login failed)");
            return;
        }

        // Connect WebSocket
        LOG.trace("step 5: connecting websocket...");
        connectWebSocket();

        // Load partners (graceful — user service may not be available)
        LOG.trace("step 6: loading partners...");
        loadPartners();

        // Load self info (graceful — user service may not be available)
        LOG.trace("step 7: loading self info...");
        loadSelfInfo();

        // Setup chat send action
        LOG.trace("step 8: setting up chat send action...");
        view.setSendAction(text -> {
            sendTlmiMessage(text);
            view.displayMessage(text, false);
        });

        trace("start!");
        LOG.trace(">>> initialize() COMPLETE");
    }

    private boolean checkInternet() {
        String healthUrl = serverConfig.getHealthCheckUrl();
        LOG.trace("checkInternet: GET {}", healthUrl);
        try {
            java.net.URL url = new java.net.URL(healthUrl);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.connect();
            int code = connection.getResponseCode();
            connection.disconnect();
            LOG.trace("checkInternet: OK (HTTP {})", code);
            return code >= 200 && code < 400;
        } catch (Exception e) {
            LOG.trace("checkInternet: FAILED ({})", e.getMessage());
            return false;
        }
    }

    private void loadPartners() {
        LOG.trace("loadPartners: fetching all users from {}", serverConfig.getUserServiceUrl());
        try {
            GetAllTranslateUsersResponse allUsersResponse =
                    new TlmiUserServiceClient(serverConfig.getUserServiceUrl()).getAllTranslateUsers();

            if (allUsersResponse.list == null) {
                LOG.trace("loadPartners: user service returned null list (service may not support this endpoint)");
                trace("partner list not available (user service not running)");
                setupPartnerSelectionListener();
                return;
            }

            int count = 0;
            for (TlmiTranslateUser partner : allUsersResponse.list) {
                if (partner.getName() != null && !partner.getName().equals(env.getUserId().get()) && partner.getAvatar() != null) {
                    view.addPartner(partner.getName(), partner.getHumanName(), partner.getAvatar());
                    count++;
                }
            }
            LOG.trace("loadPartners: {} partners added", count);
        } catch (Exception e) {
            LOG.trace("loadPartners: FAILED ({})", e.getMessage());
            trace("partner list not available: " + e.getMessage());
        }

        setupPartnerSelectionListener();
    }

    private void setupPartnerSelectionListener() {
        view.setPartnerSelectionListener(partnerData -> {
            String name = partnerData[0];
            String avatar = partnerData[2];

            env.getPartnerId().set(name);
            env.getPartnerAvatar().set(avatar);

            sendAc4yObjectAsMessage(
                    new TlmiCMDInvitation(
                            env.getUserId().get(),
                            env.getUserLanguage().get()
                    )
            );

            view.showPanel("chat");
        });
    }

    private void loadSelfInfo() {
        LOG.trace("loadSelfInfo: fetching own profile from {}", serverConfig.getUserServiceUrl());
        try {
            GetTranslateUserByNameResponse response =
                    tryGetTranslateUserByName(new GetTranslateUserByNameRequest(env.getUserId().get()));

            if (response.itWasSuccessful()) {
                env.getUserAvatar().set(response.getObject().getAvatar());

                view.setSelfName(
                        response.getObject().getHumanName()
                                + " (" + env.getUserLanguage().get() + ")"
                );

                view.setSelfAvatar(env.getUserAvatar().get());
                LOG.trace("loadSelfInfo: profile loaded");
            } else {
                LOG.trace("loadSelfInfo: failed to load profile (user service may not be available)");
                view.setSelfName(env.getUserId().get() + " (" + env.getUserLanguage().get() + ")");
            }
        } catch (Exception e) {
            LOG.trace("loadSelfInfo: FAILED ({})", e.getMessage());
            view.setSelfName(env.getUserId().get() + " (" + env.getUserLanguage().get() + ")");
        }
    }

    // --- WebSocket ---

    private void connectWebSocket() {
        URI uri;
        try {
            String wsUri = serverConfig.getWebsocketUri(env.getUserId().get());
            uri = new URI(wsUri);
            LOG.trace("connectWebSocket: URI={}", uri);
        } catch (URISyntaxException e) {
            LOG.error("connectWebSocket: invalid URI", e);
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                view.setWebsocketStatus(true);
                trace("WS open");
            }

            @Override
            public void onMessage(String s) {
                processMessage(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                trace("WS closed: " + s);
            }

            @Override
            public void onError(Exception e) {
                view.setWebsocketStatus(false);
                screenMessageHandler.errorNotifying(e.getMessage());
            }
        };
        webSocketClient.connect();
    }

    // --- Message handling ---

    public void sendAc4yObjectAsMessage(Ac4y ac4y) {
        Ac4yCMDMessage message = new Ac4yCMDMessage();
        message.getRequest().setSender(env.getUserId().get());

        trace("send object to partner:" + env.getPartnerId().get());

        message.getRequest().setAddressee(env.getPartnerId().get());
        message.getRequest().setBody(ac4y.getAsJson());

        webSocketClient.send(message.getAsJson());
    }

    public void sendTlmiMessage(String message) {
        TlmiMessage tlmiMessage = new TlmiMessage();

        if (!(env.getUserLanguage().get().equals(env.getPartnerLanguage().get()))) {
            trace("translate!");
            trace("partner:" + env.getPartnerLanguage().get());
            trace("user:" + env.getUserLanguage().get());

            Text2TextResponse text2TextResponse =
                    tryText2Text(new Text2TextRequest(
                            message,
                            env.getPartnerLanguage().get(),
                            env.getUserLanguage().get()
                    ));

            if (text2TextResponse.itWasSuccessful()) {
                message = text2TextResponse.getObject();
                trace("send translated message:" + message);
            }
        }

        tlmiMessage.setMessage(message);
        sendAc4yObjectAsMessage(tlmiMessage);
        trace("send message:" + message);
    }

    public void processMessage(String message) {
        Ac4yCommand ac4yCommand = new Gson().fromJson(message, Ac4yCommand.class);
        trace("command:" + ac4yCommand.getCommandName());

        if (ac4yCommand.getCommandName().equals("MESSAGE")) {
            try {
                Ac4yCMDMessage ac4yCMDMessage = (Ac4yCMDMessage) new Ac4yCMDMessage().getFromJson(message);
                trace("got message from:" + ac4yCMDMessage.getRequest().getSender());

                if (ac4yCMDMessage.getRequest().getBody() != null) {
                    JsonElement jsonElement = JsonParser.parseString(ac4yCMDMessage.getRequest().getBody());

                    if (jsonElement.isJsonPrimitive()) {
                        trace("only message:" + ac4yCMDMessage.getRequest().getBody());
                    } else {
                        JsonObject rootJsonObject = jsonElement.getAsJsonObject();

                        if (rootJsonObject.has("commandName")) {
                            String commandName = rootJsonObject.get("commandName").getAsString();
                            trace("commandName:" + commandName);

                            if (commandName.equals("MESSAGE")) {
                                processMessage(ac4yCMDMessage.getRequest().getBody());
                            } else if (commandName.equals("TLMICMDINVITATION")) {
                                handleInvitation(ac4yCMDMessage);
                            } else if (commandName.equals("TLMICMDINVITATIONACCEPT")) {
                                handleInvitationAccept(ac4yCMDMessage);
                            } else if (commandName.equals("SERVICERESPONSE")) {
                                handleServiceResponse(ac4yCMDMessage);
                            } else if (commandName.equals("TLMIMESSAGE")) {
                                handleTlmiMessage(ac4yCMDMessage);
                            } else {
                                trace("nothing to do:" + commandName);
                            }
                        } else {
                            LOG.warn("no legal command in message body");
                        }
                    }
                }
            } catch (Exception exception) {
                LOG.error("processMessage error", exception);
            }
        }
    }

    private void handleInvitation(Ac4yCMDMessage ac4yCMDMessage) {
        TlmiCMDInvitation invitation = (TlmiCMDInvitation) new TlmiCMDInvitation().getFromJson(ac4yCMDMessage.getRequest().getBody());

        trace("invitation arrived:" + invitation.getPartner());

        env.getPartnerId().set(invitation.getPartner());
        env.getPartnerLanguage().set(invitation.getLanguage());

        sendAc4yObjectAsMessage(
                new TlmiCMDInvitationAccept(
                        env.getUserId().get(),
                        env.getUserLanguage().get()
                )
        );

        view.showPanel("chat");

        GetTranslateUserByNameResponse response =
                tryGetTranslateUserByName(new GetTranslateUserByNameRequest(invitation.getPartner()));

        if (response.itWasSuccessful()) {
            env.getPartnerAvatar().set(response.getObject().getAvatar());
            view.setPartnerAvatar(env.getPartnerAvatar().get());
            view.setConnected();
        }
    }

    private void handleInvitationAccept(Ac4yCMDMessage ac4yCMDMessage) {
        TlmiCMDInvitationAccept accept =
                (TlmiCMDInvitationAccept) new TlmiCMDInvitationAccept().getFromJson(ac4yCMDMessage.getRequest().getBody());

        trace("invitation accept arrived from:" + accept.getPartner());

        env.getPartnerId().set(accept.getPartner());
        env.getPartnerLanguage().set(accept.getLanguage());

        GetTranslateUserByNameResponse response =
                tryGetTranslateUserByName(new GetTranslateUserByNameRequest(accept.getPartner()));

        if (response.itWasSuccessful()) {
            env.getPartnerAvatar().set(response.getObject().getAvatar());
            view.setPartnerAvatar(env.getPartnerAvatar().get());
            view.setConnected();
        }
    }

    private void handleServiceResponse(Ac4yCMDMessage ac4yCMDMessage) {
        Ac4yCMDServiceResponse command =
                (Ac4yCMDServiceResponse) new Ac4yCMDServiceResponse().getFromJson(ac4yCMDMessage.getRequest().getBody());

        trace("service response *"
                + " code:" + command.getRequest().getResponse().getResult().getCode()
                + " message:" + command.getRequest().getResponse().getResult().getMessage()
                + " description:" + command.getRequest().getResponse().getResult().getDescription()
        );

        if (command.getRequest().getResponse().getResult().itWasFailed()) {
            screenMessageHandler.errorNotifying(command.getRequest().getResponse().getResult().getDescription());
        }
    }

    private void handleTlmiMessage(Ac4yCMDMessage ac4yCMDMessage) {
        TlmiMessage command = (TlmiMessage) new TlmiMessage().getFromJson(ac4yCMDMessage.getRequest().getBody());
        trace("message arrived:" + command.getMessage());
        view.displayMessage(command.getMessage(), true);
        command.process();
    }

    // --- Service calls ---

    public GetTranslateUserByNameResponse tryGetTranslateUserByName(GetTranslateUserByNameRequest request) {
        LOG.trace("API CALL: TlmiUserServiceClient.getTranslateUserByName({}) -> {}", request.getName(), serverConfig.getUserServiceUrl());
        GetTranslateUserByNameResponse response =
                new TlmiUserServiceClient(serverConfig.getUserServiceUrl()).getTranslateUserByName(request);
        if (response.itWasFailed()) {
            LOG.trace("API RESULT: getTranslateUserByName FAILED");
            screenMessageHandler.errorNotifying(response.getResult().getDescription());
        } else {
            LOG.trace("API RESULT: getTranslateUserByName OK");
        }
        return response;
    }

    public InsertUserResponse tryInsertUser(InsertUserRequest request) {
        LOG.trace("API CALL: TlmiUserServiceClient.insertUser() -> {}", serverConfig.getUserServiceUrl());
        InsertUserResponse response =
                new TlmiUserServiceClient(serverConfig.getUserServiceUrl()).insertUser(request);
        if (response.itWasFailed()) {
            LOG.trace("API RESULT: insertUser FAILED");
            screenMessageHandler.errorNotifying(response.getResult().getDescription());
        } else {
            LOG.trace("API RESULT: insertUser OK");
        }
        return response;
    }

    public GateInsertUserResponse tryGateInsertUser(GateInsertUserRequest request) {
        LOG.trace("API CALL: Ac4yGateServiceClient.insertUser() -> {}", serverConfig.getGateServiceUrl());
        GateInsertUserResponse response =
                new Ac4yGateServiceClient(serverConfig.getGateServiceUrl()).insertUser(request);
        if (response.itWasFailed()) {
            LOG.trace("API RESULT: gateInsertUser FAILED");
            screenMessageHandler.errorNotifying(response.getResult().getDescription());
        } else {
            LOG.trace("API RESULT: gateInsertUser OK");
        }
        return response;
    }

    public Text2TextResponse tryText2Text(Text2TextRequest request) {
        LOG.trace("API CALL: TlmiServiceClient.text2text() -> {}", serverConfig.getTranslationServiceUrl());
        Text2TextResponse response =
                new TlmiServiceClient(serverConfig.getTranslationServiceUrl()).text2text(request);
        if (response.itWasFailed()) {
            LOG.trace("API RESULT: text2text FAILED");
            screenMessageHandler.errorNotifying(response.getResult().getDescription());
        } else {
            LOG.trace("API RESULT: text2text OK");
        }
        return response;
    }

    public GateLoginResponse tryLogin(GateLoginRequest request) {
        LOG.trace("API CALL: Ac4yGateServiceClient.login() -> {}", serverConfig.getGateServiceUrl());
        GateLoginResponse response =
                new Ac4yGateServiceClient(serverConfig.getGateServiceUrl()).login(request);
        if (response.itWasFailed()) {
            LOG.trace("API RESULT: login FAILED");
            screenMessageHandler.errorNotifying(response.getResult().getDescription());
        } else {
            LOG.trace("API RESULT: login OK");
        }
        return response;
    }

    // --- Logging ---

    public void trace(String message) {
        LOG.info(message);
        view.addLog(message);
    }

}
