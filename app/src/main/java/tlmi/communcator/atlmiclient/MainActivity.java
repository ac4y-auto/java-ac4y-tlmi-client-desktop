package tlmi.communcator.atlmiclient;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.os.ConfigurationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

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
import tlmi.communcator.atlmiclient.control.ChatHistoryViewHandler;
import tlmi.communcator.atlmiclient.control.ObjectListViewHandler;
import tlmi.communcator.atlmiclient.control.ReverseLogAdapter;
import tlmi.communcator.atlmiclient.model.ChatEvent;
import tlmi.communcator.atlmiclient.model.KeyValue;
import tlmi.communcator.atlmiclient.model.LogEvent;
import tlmi.communcator.atlmiclient.utility.Ac4yImageHandler;
import tlmi.communcator.atlmiclient.utility.Ac4ySpeechRecognizer;
import tlmi.communcator.atlmiclient.utility.Ac4ySpeechSynthesizer;
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

import static android.speech.SpeechRecognizer.ERROR_NO_MATCH;
import static android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT;

public class MainActivity extends MainActivityAlgebra {

    //private ReverseLogAdapter logAdapter;

    private static final int REQUEST_RECORD_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        createScreenSupport();

        getScreenSupport().bindPartnerList(findViewById(R.id.partnerList));
        getScreenSupport().bindChatHistory(findViewById(R.id.chatHistory));
        getScreenSupport().bindKeyValueListView(findViewById(R.id.keyValueListView));
        getScreenSupport().bindLog(findViewById(R.id.log));
        getScreenSupport().bindKeyValueListViewBottomSheet(findViewById(R.id.key_value_listview_bottom_sheet));
        getScreenSupport().bindSelfName(findViewById(R.id.selfName));
        getScreenSupport().bindSelfAvatar(findViewById(R.id.selfAvatar));
        getScreenSupport().bindPartnerName(findViewById(R.id.partnerName));
        getScreenSupport().bindPartnerAvatar(findViewById(R.id.partnerAvatar));
        getScreenSupport().bindConnection(findViewById(R.id.connection));

        getScreenSupport().bindRecognition(findViewById(R.id.recognition));
        getScreenSupport().bindSynthesizer(findViewById(R.id.synthesizer));
        getScreenSupport().bindInternet(findViewById(R.id.internet));
        getScreenSupport().bindWebsocket(findViewById(R.id.websocket));
        getScreenSupport().bindLogin(findViewById(R.id.login));

        getScreenSupport().bindLog(findViewById(R.id.log));

        createAppEnvironmentVariableHandler();

        createVisualBarIndicator();
        createScreenMessageHandler(getApplicationContext());

        setLogAdapter(new ReverseLogAdapter(getApplicationContext(), R.layout.log_row, new ArrayList<Object>()));
        getScreenSupport().log.setAdapter(getLogAdapter());

        createKeyValueAdapter(getApplicationContext(), R.layout.key_value_row);
        getScreenSupport().getKeyValueListView().setAdapter(getKeyValueAdapter());

        setObjectListViewHandler(new ObjectListViewHandler(getApplicationContext(), (ListView) findViewById(R.id.partnerList)));
        setChatHistoryListViewHandler(new ChatHistoryViewHandler(getApplicationContext(), (ListView) findViewById(R.id.chatHistory), getAppEnvironmentVariableHandler()));

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        trace("start!");

        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_PERMISSION
        );

        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            getVisualBarIndicator().getInternet().live();
            trace("you have internet connection");
        }

        else {
            trace("you do not have internet connection");
            getVisualBarIndicator().getInternet().error();
            getScreenMessageHandler().errorNotifying("Connect to the internet!");
        }

///        boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;


        getAppEnvironmentVariableHandler().getUserId().getSet(UUID.randomUUID().toString());

        //TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        Locale locale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);

        trace("locale.getCountry:"+locale.getCountry());
        trace("locale.getLanguage:"+locale.getLanguage());

        getAppEnvironmentVariableHandler().getUserLanguage().set(locale.getLanguage());
        getAppEnvironmentVariableHandler().getUserCountry().set(locale.getCountry());

        getAppEnvironmentVariableHandler().disableTextToSpeechDisabler();

/*
        //String user = "afe2a6f1-280a-413f-a1a5-006e4aec1e4d"; // Alcatel
        //String user = "5c22789d-3542-4703-ab20-80a23f47a224"; // Nokia

        getAppEnvironmentVariableHandler().getUserId().set(user);
*/

        getAppEnvironmentVariableHandler().disableTextToSpeechDisabler();


        GetTranslateUserByNameResponse checkGetTranslateUserByNameResponse =
                tryGetTranslateUserByName (
                        new GetTranslateUserByNameRequest(getAppEnvironmentVariableHandler().getUserId().get())
                );

        if (checkGetTranslateUserByNameResponse.itWasFailed()){

            TlmiTranslateUser tlmiTranslateUser = new TlmiTranslateUser();
            tlmiTranslateUser.setName(getAppEnvironmentVariableHandler().getUserId().get());
            tlmiTranslateUser.setPassword("1");

            InsertUserResponse insertUserResponse = tryInsertUser (
                    new InsertUserRequest (tlmiTranslateUser)
            );

            GateInsertUserResponse gateInsertUserResponse = tryGateInsertUser (
                    new GateInsertUserRequest (tlmiTranslateUser.getName(), tlmiTranslateUser.getPassword())
            );

        }

        GateInsertUserResponse gateInsertUserResponse = tryGateInsertUser (
                new GateInsertUserRequest (
                        getAppEnvironmentVariableHandler().getUserId().get()
                        ,"1")
        );

        GateLoginResponse loginResponse = tryLogin (new GateLoginRequest(getAppEnvironmentVariableHandler().getUserId().get(),"1"));

        if (loginResponse.getResult().itWasSuccessful()) {
            getVisualBarIndicator().getLogin().live();
            trace("login successed!");
        }
        else {
            getVisualBarIndicator().getLogin().error();
            trace("you dont have permission to login");
            return;
        }

        connectWebSocket();

        createSpeechSynthesizer();
        createSpeechRecognizer();

        ///getSpeechRecognizer().start();


        GetAllTranslateUsersResponse getAllTranslateUsersResponse = new TlmiUserServiceClient("https://client.ac4y.com").getAllTranslateUsers();

        showPartnerList();

        for(TlmiTranslateUser partner : getAllTranslateUsersResponse.list){
            //((TextView) findViewById(R.id.textView)).setText(((TextView) findViewById(R.id.textView)).getText()+"\n"+user.getGUID());
            if (!partner.getName().equals(getAppEnvironmentVariableHandler().getUserId().get()) && partner.getAvatar()!=null)
                getObjectListViewHandler().addNewItem(partner);

        }

        getObjectListViewHandler().getListview().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                TlmiTranslateUser user = (TlmiTranslateUser) getObjectListViewHandler().getList().get(position);

                getAppEnvironmentVariableHandler().getPartnerId().set(user.getName());
                getAppEnvironmentVariableHandler().getPartnerAvatar().set(user.getAvatar());

                sendAc4yObjectAsMessage(
                        new TlmiCMDInvitation(
                                getAppEnvironmentVariableHandler().getUserId().get()
                                ,getAppEnvironmentVariableHandler().getUserLanguage().get()
                        )
                );

                showChatHistory();

            }

        });

        GetTranslateUserByNameResponse getTranslateUserByNameResponse =
                tryGetTranslateUserByName (
                        new GetTranslateUserByNameRequest(getAppEnvironmentVariableHandler().getUserId().get())
                );

        if (getTranslateUserByNameResponse.itWasSuccessful()) {

            getAppEnvironmentVariableHandler().getUserAvatar().set(getTranslateUserByNameResponse.getObject().getAvatar());
/*
            if (!getTranslateUserByNameResponse.getObject().getLanguage().isEmpty()) {

                getAppEnvironmentVariableHandler().getUserLanguage().set(getTranslateUserByNameResponse.getObject().getLanguage().split("-")[0]);
                getAppEnvironmentVariableHandler().getUserCountry().set(getTranslateUserByNameResponse.getObject().getLanguage().split("-")[1]);

            }
*/
            getScreenSupport().getSelfName().setText(
                    getTranslateUserByNameResponse.getObject().getHumanName()
                            +" ("+getAppEnvironmentVariableHandler().getUserLanguage().get()+")"
            );

            getScreenSupport().getSelfAvatar().setImageBitmap(
                    new Ac4yImageHandler().getBitmapFromString(
                            getAppEnvironmentVariableHandler().getUserAvatar().get()
                    )
            );

        }


        //createScreenSupport();
/*



        //getScreenSupport().setupTextView(findViewById(R.id.textView));


        trace("binded");

        trace("user:"+getAppEnvironmentVariableHandler().getUserId().get());
        trace("textToSpeechDisabler:"+getAppEnvironmentVariableHandler().getTextToSpeechDisabler().get());
        ///trace("partner:"+getAppEnvironmentVariableHandler().getPartnerId().get());


        if (!getAppEnvironmentVariableHandler().isTextToSpeechDisabler())
            setupRecognizer();
        else
            trace("has no TTS feature");

        connectWebSocket();
*/
    } // onCreate



    public void goneAllLists(){

        getScreenSupport().getPartnerList().setVisibility(View.GONE);
        getScreenSupport().getChatHistory().setVisibility(View.GONE);
        getScreenSupport().log.setVisibility(View.GONE);
        getScreenSupport().getKeyValueListView().setVisibility(View.GONE);

    }

    public void showPartnerList(){
        goneAllLists();
        getScreenSupport().getPartnerList().setVisibility(View.VISIBLE);
    }

    public void showChatHistory(){
        goneAllLists();
        getScreenSupport().getChatHistory().setVisibility(View.VISIBLE);
    }

    public void showLog(){
        goneAllLists();
        getScreenSupport().log.setVisibility(View.VISIBLE);
    }

    public void showKeyValueListView(){
        goneAllLists();
        getScreenSupport().getKeyValueListView().setVisibility(View.VISIBLE);
    }



    public GetTranslateUserByNameResponse tryGetTranslateUserByName (GetTranslateUserByNameRequest request){

        GetTranslateUserByNameResponse response =
                new TlmiUserServiceClient("https://client.ac4y.com").getTranslateUserByName(request);

        if (response.itWasFailed())
            getScreenMessageHandler().errorNotifying(response.getResult().getDescription());

        return response;

    } // tryGetTranslateUserByName

    public InsertUserResponse tryInsertUser (InsertUserRequest request){

        InsertUserResponse response =
                new TlmiUserServiceClient("https://client.ac4y.com").insertUser(request);

        if (response.itWasFailed())
            getScreenMessageHandler().errorNotifying(response.getResult().getDescription());

        return response;

    } // tryInsertUser

    public GateInsertUserResponse tryGateInsertUser (GateInsertUserRequest request){

        GateInsertUserResponse response =
                new Ac4yGateServiceClient("https://gate.ac4y.com").insertUser(request);

        if (response.itWasFailed())
            getScreenMessageHandler().errorNotifying(response.getResult().getDescription());

        return response;

    } // tryInsertUser

    public Text2TextResponse tryText2Text (Text2TextRequest request){

        Text2TextResponse response  =
                new TlmiServiceClient("https://api.ac4y.com").text2text(request);

        if (response.itWasFailed())
            getScreenMessageHandler().errorNotifying(response.getResult().getDescription());

        return response;

    } // tryText2Text


    public GateLoginResponse tryLogin (GateLoginRequest request){

        GateLoginResponse response =
                new Ac4yGateServiceClient("https://gate.ac4y.com").login(request);

        if (response.itWasFailed())
            getScreenMessageHandler().errorNotifying(response.getResult().getDescription());

        return response;

    } // tryLogin


    private WebSocketClient mWebSocketClient;

    private void connectWebSocket() {
        URI uri;
        try {

            String user = getAppEnvironmentVariableHandler().getUserId().get();
            System.out.println(user);

            uri = new URI("wss://www.ac4y.com:2222/"+user);

            //uri = new URI("wss://wss.ac4y.com/"+user);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {

            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                getVisualBarIndicator().getWebsocket().live();
                Log.i("Websocket", "Opened");
                ///mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
                //this.send("messsage from Android client");
                //sendMessage();
                //sendMessage(getAppEnvironmentVariableHandler().getPartnerId().get(), "hello partner");
                trace("WS open");


            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processMessage(message);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {

                final String errorMessage = e.getMessage();
                getVisualBarIndicator().getWebsocket().error();
                //Log.i("Websocket", "Error " + e.getMessage());
                //trace("WS error ("+ +")");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getScreenMessageHandler().errorNotifying(errorMessage);
                    }
                });

            }
        };
        mWebSocketClient.connect();
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.under_the_hood, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()) {

            case R.id.startMenuItem: {
                getSpeechRecognizer().start();
                break;
            }

            case R.id.stopMenuItem: {
                getSpeechRecognizer().stop();
                break;
            }

            case R.id.partnersMenuItem: {
                showPartnerList();
                break;
            }

            case R.id.chatHistoryMenuItem: {
                showChatHistory();
                break;
            }

            case R.id.logMenuItem: {
                showLog();
                break;
            }

            case R.id.showEnvironmentVariables: {
                //BottomSheetBehavior.from(getScreenSupport().getKeyValueListViewBottomSheet()).setState(BottomSheetBehavior.STATE_EXPANDED);
                getKeyValueAdapter().clear();
                fillEnvironmentVariablesListView();
                showKeyValueListView();
                break;
            }

            default:  {
                break;
            }

        } // case

        return true;
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {

                case R.id.startMenuItem: {
                    getSpeechRecognizer().start();
                    break;
                }

                case R.id.stopMenuItem: {
                    getSpeechRecognizer().stop();
                    break;
                }

                case R.id.partnersMenuItem: {
                    showPartnerList();
                    break;
                }

                case R.id.chatHistoryMenuItem: {
                    showChatHistory();
                    break;
                }

                case R.id.logMenuItem: {
                    showLog();
                    break;
                }

                default:  {
                    break;
                }

            } // case

            return false;
        }
    };



    public void sendAc4yObjectAsMessage(Ac4y ac4y){

        Ac4yCMDMessage message = new Ac4yCMDMessage();

        message.getRequest().setSender(getAppEnvironmentVariableHandler().getUserId().get());

        trace("send object to partner:"+getAppEnvironmentVariableHandler().getPartnerId().get());
        //trace("sended object:"+ac4y.getAc4yIdentification().getAsJson());

        message.getRequest().setAddressee(getAppEnvironmentVariableHandler().getPartnerId().get());
        message.getRequest().setBody(ac4y.getAsJson());

        mWebSocketClient.send(message.getAsJson());

    } // sendAc4yObjectAsMessage

    public void sendTlmiMessage(String message){

        TlmiMessage tlmiMessage = new TlmiMessage();

        if (!(getAppEnvironmentVariableHandler().getUserLanguage().get().equals(getAppEnvironmentVariableHandler().getPartnerLanguage().get()))) {

            trace("translate!");

            trace("partner:"+getAppEnvironmentVariableHandler().getPartnerLanguage().get());
            trace("user:"+getAppEnvironmentVariableHandler().getUserLanguage().get());

            Text2TextResponse text2TextResponse =
                    tryText2Text(
                            new Text2TextRequest(
                                    message
                                    ,getAppEnvironmentVariableHandler().getPartnerLanguage().get()
                                    ,getAppEnvironmentVariableHandler().getUserLanguage().get()
                            )
                    );

            if (text2TextResponse.itWasSuccessful()) {
                message = text2TextResponse.getObject();
                trace ("send translated message:" + message);
            }

        }

        tlmiMessage.setMessage(message);

        sendAc4yObjectAsMessage(tlmiMessage);

        trace ("send message:" +message);

        //getChatHistoryListViewHandler().addNewItem(new ChatEvent(tlmiMessage.getMessage(), true));

    } // sendTlmiMessage

    public void processMessage(String message){

        //trace(message);
        Ac4yCommand ac4yCommand = (Ac4yCommand) new Gson().fromJson(message, Ac4yCommand.class);
        trace("command:"+ac4yCommand.getCommandName());

        if (ac4yCommand.getCommandName().equals("MESSAGE")){

            try{

//              Ac4yCMDMessage ac4yCMDMessage = (Ac4yCMDMessage) new Gson().fromJson(message, Ac4yCMDMessage.class);
                Ac4yCMDMessage ac4yCMDMessage = (Ac4yCMDMessage) new Ac4yCMDMessage().getFromJson(message);

                trace("got message from:"+ac4yCMDMessage.getRequest().getSender());
                //String jsonInput = "{\"access_token\": \"abcdefgh\"}";

                if (ac4yCMDMessage.getRequest().getBody()!=null) {

                    JsonElement jsonElement = new JsonParser().parse(ac4yCMDMessage.getRequest().getBody());

                    if (jsonElement.isJsonPrimitive())
                        trace("only message:"+ac4yCMDMessage.getRequest().getBody());
                    else {

                        JsonObject rootJsonObject = (JsonObject) jsonElement.getAsJsonObject();

                        if (rootJsonObject.has("commandName")) {
                            JsonElement commandNameMember = rootJsonObject.get("commandName");
                            String commandName = commandNameMember.getAsString();
                            trace("commandName:"+commandName);

                            if (commandName.equals("MESSAGE")){
                                processMessage(ac4yCMDMessage.getRequest().getBody());
                            }
                            else if (commandName.equals("TEST")){
                                //Test command = (Test) new Test().getFromJson(message);
                                //command.process();
                            }
                            else if (commandName.equals("TLMICMDINVITATION")){

                                TlmiCMDInvitation tlmiCMDInvitation = (TlmiCMDInvitation) new TlmiCMDInvitation().getFromJson(ac4yCMDMessage.getRequest().getBody());

                                trace("invitation arrived:"+ tlmiCMDInvitation.getPartner());

                                getAppEnvironmentVariableHandler().getPartnerId().set(tlmiCMDInvitation.getPartner());
                                getAppEnvironmentVariableHandler().getPartnerLanguage().set(tlmiCMDInvitation.getLanguage());

                                sendAc4yObjectAsMessage(
                                        new TlmiCMDInvitationAccept(
                                                getAppEnvironmentVariableHandler().getUserId().get()
                                                ,getAppEnvironmentVariableHandler().getUserLanguage().get()
                                        )
                                );

                                showChatHistory();

                                GetTranslateUserByNameResponse getTranslateUserByNameResponse =
                                        tryGetTranslateUserByName (
                                                new GetTranslateUserByNameRequest(tlmiCMDInvitation.getPartner())
                                        );

                                if (getTranslateUserByNameResponse.itWasSuccessful()) {

                                    getAppEnvironmentVariableHandler().getPartnerAvatar().set(getTranslateUserByNameResponse.getObject().getAvatar());
/*
                                    if (!getTranslateUserByNameResponse.getObject().getLanguage().isEmpty()) {

                                        getAppEnvironmentVariableHandler().getPartnerLanguage().set(getTranslateUserByNameResponse.getObject().getLanguage().split("-")[0]);
                                        getAppEnvironmentVariableHandler().getPartnerCountry().set(getTranslateUserByNameResponse.getObject().getLanguage().split("-")[1]);

                                    }

                                    getScreenSupport().getPartnerName().setText(
                                            getTranslateUserByNameResponse.getObject().getHumanName()
                                                    +" ("+getAppEnvironmentVariableHandler().getPartnerLanguage().get()+")"
                                    );
*/
                                    getScreenSupport().getPartnerAvatar().setImageBitmap(
                                            new Ac4yImageHandler().getBitmapFromString(
                                                    getAppEnvironmentVariableHandler().getPartnerAvatar().get()
                                            )
                                    );

                                    connect();

                                    getSpeechRecognizer().start();

                                }


                            }
                            else if (commandName.equals("TLMICMDINVITATIONACCEPT")){

                                TlmiCMDInvitationAccept tlmiCMDInvitationAccept =
                                        (TlmiCMDInvitationAccept) new TlmiCMDInvitationAccept().getFromJson(ac4yCMDMessage.getRequest().getBody());

                                trace("invitation accept arrived from:"+ tlmiCMDInvitationAccept.getPartner());

                                getAppEnvironmentVariableHandler().getPartnerId().set(tlmiCMDInvitationAccept.getPartner());
                                getAppEnvironmentVariableHandler().getPartnerLanguage().set(tlmiCMDInvitationAccept.getLanguage());

                                GetTranslateUserByNameResponse getTranslateUserByNameResponse =
                                        tryGetTranslateUserByName (
                                                new GetTranslateUserByNameRequest(tlmiCMDInvitationAccept.getPartner())
                                        );

                                if (getTranslateUserByNameResponse.itWasSuccessful()) {

                                    getAppEnvironmentVariableHandler().getPartnerAvatar().set(getTranslateUserByNameResponse.getObject().getAvatar());
/*
                                    if (!getTranslateUserByNameResponse.getObject().getLanguage().isEmpty()) {

                                        getAppEnvironmentVariableHandler().getPartnerLanguage().set(getTranslateUserByNameResponse.getObject().getLanguage().split("-")[0]);
                                        getAppEnvironmentVariableHandler().getPartnerCountry().set(getTranslateUserByNameResponse.getObject().getLanguage().split("-")[1]);

                                    }

                                    getScreenSupport().getPartnerName().setText(
                                            getTranslateUserByNameResponse.getObject().getHumanName()
                                                    +" ("+getAppEnvironmentVariableHandler().getPartnerLanguage().get()+")"
                                    );
                                    */
                                    getScreenSupport().getPartnerAvatar().setImageBitmap(
                                            new Ac4yImageHandler().getBitmapFromString(
                                                    getAppEnvironmentVariableHandler().getPartnerAvatar().get()
                                            )
                                    );

                                    connect();

                                    getSpeechRecognizer().start();

                                }

                            }
                            else if (commandName.equals("SERVICERESPONSE")){

                                //Ac4yServiceResponse command = (Ac4yServiceResponse) new Gson().fromJson(ac4yCMDMessage.getRequest().getBody(), Ac4yServiceResponse.class);
                                String commandInString = ac4yCMDMessage.getRequest().getBody();
                                //Ac4yServiceResponse command = (Ac4yServiceResponse) new Ac4yServiceResponse().getFromJson(ac4yCMDMessage.getRequest().getBody());
                                Ac4yCMDServiceResponse command = (Ac4yCMDServiceResponse) new Ac4yCMDServiceResponse().getFromJson(ac4yCMDMessage.getRequest().getBody());
                                //Ac4yCMDServiceResponse command = (Ac4yCMDServiceResponse) new Gson().fromJson(message, Ac4yCMDServiceResponse.class);
                                Ac4yCMDServiceResponse ac4yCMDServiceResponse = new Ac4yCMDServiceResponse();
                                trace("service response * "
                                        + " code:" + command.getRequest().getResponse().getResult().getCode()
                                        + " message:" + command.getRequest().getResponse().getResult().getMessage()
                                        + " description:" + command.getRequest().getResponse().getResult().getDescription()
                                );

                                if (command.getRequest().getResponse().getResult().itWasFailed())
                                    getScreenMessageHandler().errorNotifying(command.getRequest().getResponse().getResult().getDescription());

                                //trace("service response:"+ command.getAsJson() );
                            }
                            else if (commandName.equals("TLMIMESSAGE")){

                                TlmiMessage command = (TlmiMessage) new TlmiMessage().getFromJson(ac4yCMDMessage.getRequest().getBody());
                                trace("message arrived:"+ command.getMessage());
                                getChatHistoryListViewHandler().addNewItem(new ChatEvent(command.getMessage(), true));
                                if (!getAppEnvironmentVariableHandler().isTextToSpeechDisabler()) {
                                    speak(command.getMessage());
                                    trace("speak!");
                                }


                                command.process();
                            }
                            else {
                                trace("nothing to do:"+commandName);
                                //trace(message);
                            }

                        }

                        else
                            System.out.println("no legal command!");
                    }

                }

                //Gson gson = new Gson();
//                                    Object ac4yCMDMessage1 = new Gson().fromJson(rooJsonObject, Ac4yCMDMessage.class);
//                                    Object ac4yCMDMessage2 = new Gson().fromJson(jsonElement, Ac4yCMDMessage.class);
//                                    Object ac4yCMDMessage3 = new Gson().fromJson(jsonObject, Ac4yCMDMessage.class);

                int a=1;
                //String value = jsonElement.getAsJsonObject().get("access_token").getAsString();
                //System.out.println(value);

                //Ac4yCMDMessage ac4yCMDMessage = (Ac4yCMDMessage) new Ac4yCMDMessage().getFromJson(message);
                //System.out.println("message body:"+ac4yCMDMessage.getRequest().getBody());

            } catch(Exception exception) {
                exception.printStackTrace();
            }

        }

    }


    public void trace(String message){
        //System.out.println(message);
        //getScreenSupport().getTextView().setText(message+"\n"+getScreenSupport().getTextView().getText());
        //getObjectListViewGenericHandler().add(new LogEvent(message));
        getLogAdapter().add(new LogEvent(message));
    }




    public void createSpeechRecognizer(){

        setSpeechRecognizer(new Ac4ySpeechRecognizer(getApplicationContext()) {

            @Override
            public void onCreateSuccess(){
                getVisualBarIndicator().getRecognition().live();
            }


            @Override
            public void onReadyForSpeech(Bundle params) {
                //getVisualBarIndicator().getRecognition().waiting4Speech();
                trace("onReadyForSpeech");
            }

            @Override
            public void onBeginningOfSpeech() {
                getVisualBarIndicator().getRecognition().underSpeech();
                trace("onBeginningOfSpeech");
            }

            @Override
            public void onRmsChanged(float rmsdB) {

                //log("onRmsChanged");
//                log(Float.toString(rmsdB));


                //log("state:"+Integer.toString(getVisualBarIndicator().getRecognition().getState()));
                //log(Integer.toString(Color.parseColor("#4CAF50")));

                if (rmsdB > 0) {

                    //log(Float.toString(rmsdB));

                    if (getVisualBarIndicator().getRecognition().afterSpeechState() && !getVisualBarIndicator().getRecognition().earlyChange()) {
                        getVisualBarIndicator().getRecognition().storeLastChange();
                        getVisualBarIndicator().getRecognition().underSpeech();
                        //trace("újra beszél "+Float.toString(rmsdB));
                    }

                } else {

                    if (getVisualBarIndicator().getRecognition().afterSpeechState()) {

                        //log(Long.toString(System.currentTimeMillis()-getVisualBarIndicator().getRecognition().getLastChange()));

                        if (getVisualBarIndicator().getRecognition().stabilChange()) {
                            //trace("nézzük mit mondott "+Float.toString(rmsdB));
                            //stop();
                        }

                    } else {

                        if (getVisualBarIndicator().getRecognition().underSpeechState() && !getVisualBarIndicator().getRecognition().earlyChange()) {

                            //trace("újra csendben van "+Float.toString(rmsdB));

                            getVisualBarIndicator().getRecognition().storeLastChange();
                            getVisualBarIndicator().getRecognition().afterSpeech();

                        }

                    }

                }

            } // onRmsChanged
            /*
                        @Override
                        public void onBufferReceived(byte[] buffer) {
                            getScreenMessageHandler().message("onBufferReceived");
                        }
            */
            @Override
            public void onEndOfSpeech() {
                //getVisualBarIndicator().getRecognition().endSpeech();
                trace("onEndOfSpeech");
            }

            @Override
            public void onError(int error) {

                trace("STT error:"+Integer.toString(error));

                getVisualBarIndicator().getRecognition().error();
                //getScreenSupport().getError().setText(Integer.toString(error));

                if (error==ERROR_NO_MATCH ) { // || error==ERROR_SPEECH_TIMEOUT
                    start();
                }

/*
                if (error==ERROR_NO_MATCH || error==ERROR_SPEECH_TIMEOUT || error==ERROR_CLIENT || error==ERROR_RECOGNIZER_BUSY) {
                    stop();
                    getScreenSupport().getError().setText("továbbáltam ("+Integer.toString(error)+")");
                    //getVisualBarIndicator().getRecognition().waiting4Speech();
                    //System.out.println("restart");
                    start();
                }
*/
            }

            @Override
            public void onResults(Bundle results) {
                trace("onResult");
                stop();
                //getVisualBarIndicator().getRecognition().processSpeech();
                String speech=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);

                trace("recognized:"+speech);

                sendTlmiMessage(speech);
                getChatHistoryListViewHandler().addNewItem(new ChatEvent(speech, false));

                //getScreenSupport().getConsole().setText(speech);
                //super.onResults(results);
                start();

            }
/*
            @Override
            public void onPartialResults(Bundle partialResults) {
                log("onPartialResults");
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                log("onEvent");
            }
*/
        });

    }


    public void createSpeechSynthesizer(){

        setSpeechSynthesizer(new Ac4ySpeechSynthesizer(getApplicationContext()) {
        //setSpeechSynthesizer(new Ac4ySpeechSynthesizer(getApplicationContext(),"hu", "HU") {

            @Override
            public void onCreateSuccess(){
                getVisualBarIndicator().getSynthesizer().live();
                //getScreenMessageHandler().message("kész!");
                speak("start");
            }

            @Override
            public void onCreateError(String information){
                getVisualBarIndicator().getSynthesizer().error();
                getScreenMessageHandler().errorNotifying(information);
            }

        });

    } // createSpeechSynthesizer


    public void connect(){
        getScreenSupport().connection.setImageDrawable(getDrawable(R.drawable.connected));
    }

    public void disconnect(){
        getScreenSupport().connection.setImageDrawable(getDrawable(R.drawable.disconnected));
    }

    public void fillEnvironmentVariablesListView(){

        Map<String, String> map = (Map<String, String>) getAppEnvironmentVariableHandler().getStore().getAll();

        if(!map.isEmpty()){

            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();

            while(iterator.hasNext()){

                Map.Entry item = (Map.Entry)iterator.next();

                String value = "<too big>";

                if (item.getValue().toString().length()<100)
                    value = item.getValue().toString();

                getKeyValueAdapter().add(new KeyValue(item.getKey().toString(), value));

            }
        }

    } // fillEnvironmentVariablesListView


}