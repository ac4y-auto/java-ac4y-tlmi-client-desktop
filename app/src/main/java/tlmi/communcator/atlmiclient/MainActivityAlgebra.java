package tlmi.communcator.atlmiclient;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import tlmi.communcator.atlmiclient.control.ChatHistoryViewHandler;
import tlmi.communcator.atlmiclient.control.KeyValueAdapter;
import tlmi.communcator.atlmiclient.control.ObjectListViewGenericHandler;
import tlmi.communcator.atlmiclient.control.ObjectListViewHandler;
import tlmi.communcator.atlmiclient.control.ReverseLogAdapter;
import tlmi.communcator.atlmiclient.control.ScreenSupport;
import tlmi.communcator.atlmiclient.indicator.TlmiVisualIndicatorBar;
import tlmi.communcator.atlmiclient.utility.Ac4yScreenMessageHandler;
import tlmi.communcator.atlmiclient.utility.Ac4ySpeechRecognizer;
import tlmi.communcator.atlmiclient.utility.Ac4ySpeechSynthesizer;

public class MainActivityAlgebra extends AppCompatActivity {




    private AppEnvironmentVariableHandler appEnvironmentVariableHandler;

    public AppEnvironmentVariableHandler getAppEnvironmentVariableHandler() {
        return appEnvironmentVariableHandler;
    }

    public void setAppEnvironmentVariableHandler(AppEnvironmentVariableHandler appEnvironmentVariableHandler) {
        this.appEnvironmentVariableHandler = appEnvironmentVariableHandler;
    }

    public void createAppEnvironmentVariableHandler(){
        setAppEnvironmentVariableHandler(new AppEnvironmentVariableHandler(getApplicationContext()));
    }


    public TlmiVisualIndicatorBar getVisualBarIndicator() {
        return visualBarIndicator;
    }

    public void setVisualBarIndicator(TlmiVisualIndicatorBar visualBarIndicator) {
        this.visualBarIndicator = visualBarIndicator;
    }

    public void createVisualBarIndicator(){
        setVisualBarIndicator(new TlmiVisualIndicatorBar(getApplicationContext(), getScreenSupport()));
    }

    TlmiVisualIndicatorBar visualBarIndicator;



    public Ac4yScreenMessageHandler getScreenMessageHandler() {
        return screenMessageHandler;
    }

    public void setScreenMessageHandler(Ac4yScreenMessageHandler screenMessageHandler) {
        this.screenMessageHandler = screenMessageHandler;
    }

    public void createScreenMessageHandler(Context context){
        setScreenMessageHandler(new Ac4yScreenMessageHandler(context));
    }

    Ac4yScreenMessageHandler screenMessageHandler;



    public ObjectListViewGenericHandler getObjectListViewGenericHandler() {
        return objectListViewGenericHandler;
    }

    public void setObjectListViewGenericHandler(ObjectListViewGenericHandler objectListViewGenericHandler) {
        this.objectListViewGenericHandler = objectListViewGenericHandler;
    }

    private ObjectListViewGenericHandler objectListViewGenericHandler;


    public ChatHistoryViewHandler getChatHistoryListViewHandler() {
        return chatHistoryListViewHandler;
    }

    public void setChatHistoryListViewHandler(ChatHistoryViewHandler chatHistoryListViewHandler) {
        this.chatHistoryListViewHandler = chatHistoryListViewHandler;
    }

    private ChatHistoryViewHandler chatHistoryListViewHandler;



    public ObjectListViewHandler getObjectListViewHandler() {
        return objectListViewHandler;
    }

    public void setObjectListViewHandler(ObjectListViewHandler objectListViewHandler) {
        this.objectListViewHandler = objectListViewHandler;
    }

    private ObjectListViewHandler objectListViewHandler;






    public ScreenSupport getScreenSupport() {
        return screenSupport;
    }

    public void setScreenSupport(ScreenSupport screenSupport) {
        this.screenSupport = screenSupport;
    }

    public void createScreenSupport(){
        setScreenSupport(new ScreenSupport());
    }

    private ScreenSupport screenSupport;


    public KeyValueAdapter getKeyValueAdapter() {
        return keyValueAdapter;
    }

    public void setKeyValueAdapter(KeyValueAdapter keyValueAdapter) {
        this.keyValueAdapter = keyValueAdapter;
    }

    public void createKeyValueAdapter(Context context, int resource){
        setKeyValueAdapter(new KeyValueAdapter(context, resource));
    }



    private KeyValueAdapter keyValueAdapter;



    public Ac4ySpeechSynthesizer getSpeechSynthesizer() {
        return speechSynthesizer;
    }

    public void setSpeechSynthesizer(Ac4ySpeechSynthesizer speechSynthesizer) {
        this.speechSynthesizer = speechSynthesizer;
    }

    public void speak(String text){
        getSpeechSynthesizer().speak(text);
    }

    private Ac4ySpeechSynthesizer speechSynthesizer;





    public Ac4ySpeechRecognizer getSpeechRecognizer() {
        return speechRecognizer;
    }

    public void setSpeechRecognizer(Ac4ySpeechRecognizer speechRecognizer) {
        this.speechRecognizer = speechRecognizer;
    }

    private Ac4ySpeechRecognizer speechRecognizer;




    public ReverseLogAdapter getLogAdapter() {
        return logAdapter;
    }

    public void setLogAdapter(ReverseLogAdapter logAdapter) {
        this.logAdapter = logAdapter;
    }

    protected ReverseLogAdapter logAdapter;

}
