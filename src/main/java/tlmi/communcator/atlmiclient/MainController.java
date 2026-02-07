package tlmi.communcator.atlmiclient;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.UUID;

import javax.swing.SwingUtilities;

import ac4y.base.domain.Ac4y;
import ac4y.command.domain.Ac4yCommand;
import ac4y.command.message.domain.Ac4yCMDMessage;
import ac4y.command.service.domain.Ac4yCMDServiceResponse;
import ac4y.gate.service.client.Ac4yGateServiceClient;
import ac4y.gate.service.domain.GateInsertUserRequest;
import ac4y.gate.service.domain.GateInsertUserResponse;
import ac4y.gate.service.domain.GateLoginRequest;
import ac4y.gate.service.domain.GateLoginResponse;
import tlmi.communcator.atlmiclient.command.domain.TlmiCMDInvitation;
import tlmi.communcator.atlmiclient.command.domain.TlmiCMDInvitationAccept;
import tlmi.communcator.atlmiclient.command.domain.TlmiMessage;
import tlmi.communcator.atlmiclient.model.ChatEvent;
import tlmi.communcator.atlmiclient.ui.ImageUtil;
import tlmi.communcator.atlmiclient.ui.MainFrame;
import tlmi.communcator.atlmiclient.utility.ScreenMessageHandler;
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

    private final MainFrame frame;
    private final AppEnvironmentVariableHandler env;
    private final ScreenMessageHandler screenMessageHandler;
    private WebSocketClient webSocketClient;

    public MainController(MainFrame frame) {
        this.frame = frame;
        this.env = new AppEnvironmentVariableHandler();
        this.screenMessageHandler = new ScreenMessageHandler();
    }

    public void initialize() {
        // Check internet
        if (checkInternet()) {
            frame.getStatusBar().internetLive();
            trace("you have internet connection");
        } else {
            frame.getStatusBar().internetError();
            trace("you do not have internet connection");
            screenMessageHandler.errorNotifying("Connect to the internet!");
        }

        // Set user ID and locale
        env.getUserId().getSet(UUID.randomUUID().toString());

        Locale locale = Locale.getDefault();
        trace("locale.getCountry:" + locale.getCountry());
        trace("locale.getLanguage:" + locale.getLanguage());

        env.getUserLanguage().set(locale.getLanguage());
        env.getUserCountry().set(locale.getCountry());
        env.disableTextToSpeechDisabler();

        // Check/create user
        GetTranslateUserByNameResponse checkResponse =
                tryGetTranslateUserByName(new GetTranslateUserByNameRequest(env.getUserId().get()));

        if (checkResponse.itWasFailed()) {
            TlmiTranslateUser tlmiTranslateUser = new TlmiTranslateUser();
            tlmiTranslateUser.setName(env.getUserId().get());
            tlmiTranslateUser.setPassword("1");

            tryInsertUser(new InsertUserRequest(tlmiTranslateUser));
            tryGateInsertUser(new GateInsertUserRequest(tlmiTranslateUser.getName(), tlmiTranslateUser.getPassword()));
        }

        tryGateInsertUser(new GateInsertUserRequest(env.getUserId().get(), "1"));

        // Login
        GateLoginResponse loginResponse = tryLogin(new GateLoginRequest(env.getUserId().get(), "1"));

        if (loginResponse.getResult().itWasSuccessful()) {
            frame.getStatusBar().loginLive();
            trace("login successed!");
        } else {
            frame.getStatusBar().loginError();
            trace("you dont have permission to login");
            return;
        }

        // Connect WebSocket
        connectWebSocket();

        // Load partners
        loadPartners();

        // Load self info
        loadSelfInfo();

        // Setup chat send action
        frame.getChatPanel().setSendAction(e -> {
            String text = frame.getChatPanel().getInputText().trim();
            if (!text.isEmpty()) {
                sendTlmiMessage(text);
                frame.getChatPanel().addMessage(new ChatEvent(text, false));
                frame.getChatPanel().clearInput();
            }
        });

        trace("start!");
    }

    private boolean checkInternet() {
        try {
            java.net.URL url = new java.net.URL("https://client.ac4y.com");
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000);
            connection.connect();
            connection.disconnect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void loadPartners() {
        GetAllTranslateUsersResponse allUsersResponse =
                new TlmiUserServiceClient("https://client.ac4y.com").getAllTranslateUsers();

        for (TlmiTranslateUser partner : allUsersResponse.list) {
            if (!partner.getName().equals(env.getUserId().get()) && partner.getAvatar() != null) {
                frame.getPartnerListPanel().addPartner(partner);
            }
        }

        // Partner click handler
        frame.getPartnerListPanel().getList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                TlmiTranslateUser user = frame.getPartnerListPanel().getList().getSelectedValue();
                if (user != null) {
                    env.getPartnerId().set(user.getName());
                    env.getPartnerAvatar().set(user.getAvatar());

                    sendAc4yObjectAsMessage(
                            new TlmiCMDInvitation(
                                    env.getUserId().get(),
                                    env.getUserLanguage().get()
                            )
                    );

                    frame.showPanel(MainFrame.CARD_CHAT);
                }
            }
        });
    }

    private void loadSelfInfo() {
        GetTranslateUserByNameResponse response =
                tryGetTranslateUserByName(new GetTranslateUserByNameRequest(env.getUserId().get()));

        if (response.itWasSuccessful()) {
            env.getUserAvatar().set(response.getObject().getAvatar());

            frame.setSelfName(
                    response.getObject().getHumanName()
                            + " (" + env.getUserLanguage().get() + ")"
            );

            frame.setSelfAvatar(
                    ImageUtil.fromBase64(env.getUserAvatar().get())
            );
        }
    }

    // --- WebSocket ---

    private void connectWebSocket() {
        URI uri;
        try {
            String user = env.getUserId().get();
            uri = new URI("wss://www.ac4y.com:2222/" + user);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                frame.getStatusBar().websocketLive();
                trace("WS open");
            }

            @Override
            public void onMessage(String s) {
                SwingUtilities.invokeLater(() -> processMessage(s));
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                trace("WS closed: " + s);
            }

            @Override
            public void onError(Exception e) {
                frame.getStatusBar().websocketError();
                SwingUtilities.invokeLater(() ->
                        screenMessageHandler.errorNotifying(e.getMessage())
                );
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
                            System.out.println("no legal command!");
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
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

        frame.showPanel(MainFrame.CARD_CHAT);

        GetTranslateUserByNameResponse response =
                tryGetTranslateUserByName(new GetTranslateUserByNameRequest(invitation.getPartner()));

        if (response.itWasSuccessful()) {
            env.getPartnerAvatar().set(response.getObject().getAvatar());

            frame.setPartnerAvatar(
                    ImageUtil.fromBase64(env.getPartnerAvatar().get())
            );

            frame.setConnected();
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

            frame.setPartnerAvatar(
                    ImageUtil.fromBase64(env.getPartnerAvatar().get())
            );

            frame.setConnected();
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
        frame.getChatPanel().addMessage(new ChatEvent(command.getMessage(), true));
        command.process();
    }

    // --- Service calls ---

    public GetTranslateUserByNameResponse tryGetTranslateUserByName(GetTranslateUserByNameRequest request) {
        GetTranslateUserByNameResponse response =
                new TlmiUserServiceClient("https://client.ac4y.com").getTranslateUserByName(request);
        if (response.itWasFailed())
            screenMessageHandler.errorNotifying(response.getResult().getDescription());
        return response;
    }

    public InsertUserResponse tryInsertUser(InsertUserRequest request) {
        InsertUserResponse response =
                new TlmiUserServiceClient("https://client.ac4y.com").insertUser(request);
        if (response.itWasFailed())
            screenMessageHandler.errorNotifying(response.getResult().getDescription());
        return response;
    }

    public GateInsertUserResponse tryGateInsertUser(GateInsertUserRequest request) {
        GateInsertUserResponse response =
                new Ac4yGateServiceClient("https://gate.ac4y.com").insertUser(request);
        if (response.itWasFailed())
            screenMessageHandler.errorNotifying(response.getResult().getDescription());
        return response;
    }

    public Text2TextResponse tryText2Text(Text2TextRequest request) {
        Text2TextResponse response =
                new TlmiServiceClient("https://api.ac4y.com").text2text(request);
        if (response.itWasFailed())
            screenMessageHandler.errorNotifying(response.getResult().getDescription());
        return response;
    }

    public GateLoginResponse tryLogin(GateLoginRequest request) {
        GateLoginResponse response =
                new Ac4yGateServiceClient("https://gate.ac4y.com").login(request);
        if (response.itWasFailed())
            screenMessageHandler.errorNotifying(response.getResult().getDescription());
        return response;
    }

    // --- Logging ---

    public void trace(String message) {
        System.out.println(message);
        frame.getLogPanel().addLog(message);
    }

}
