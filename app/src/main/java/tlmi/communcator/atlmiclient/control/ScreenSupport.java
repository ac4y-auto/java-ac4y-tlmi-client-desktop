package tlmi.communcator.atlmiclient.control;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class ScreenSupport {

    TextView textView;

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public void setupTextView(View view) {

        setTextView((TextView) view); // findViewById(R.id.textView)
    }


    Button startButton;

    public Button getStartButton() {
        return startButton;
    }

    public void setStartButton(Button startButton) {
        this.startButton = startButton;
    }

    public void bindStartButton(View view){
        setStartButton((Button) view);
    }


    Button stopButton;

    public Button getStopButton() {
        return stopButton;
    }

    public void setStopButton(Button stopButton) {
        this.stopButton = stopButton;
    }

    public void setupStopButton(View view){
        setStopButton((Button) view); // findViewById(R.id.stopButton)
    }


    public ListView getPartnerList() {
        return partnerList;
    }

    public void setPartnerList(ListView partnerList) {
        this.partnerList = partnerList;
    }

    ListView partnerList;

    public void bindPartnerList(View view) {

        setPartnerList((ListView) view);
    }


    public ListView getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(ListView chatHistory) {
        this.chatHistory = chatHistory;
    }

    ListView chatHistory;

    public void bindChatHistory(View view) {

        setChatHistory((ListView) view);
    }

/*
    public View getLog() {
        return log;
    }

    public void setLog(View log) {
        this.log = log;
    }

    View log;

    public void bindLog(View view) {

        setLog((View) view);

    }
*/


    public ListView getKeyValueListView() {
        return keyValueListView;
    }

    public void setKeyValueListView(ListView keyValueListView) {
        this.keyValueListView = keyValueListView;
    }

    public void bindKeyValueListView(View view) {

        setKeyValueListView((ListView) view);
    }

    protected ListView keyValueListView;


    public LinearLayout getKeyValueListViewBottomSheet() {
        return keyValueListViewBottomSheet;
    }

    public void setKeyValueListViewBottomSheet(LinearLayout keyValueListViewBottomSheet) {
        this.keyValueListViewBottomSheet = keyValueListViewBottomSheet;
    }

    public void bindKeyValueListViewBottomSheet(View view) {

        setKeyValueListViewBottomSheet((LinearLayout) view);
    }

    LinearLayout keyValueListViewBottomSheet;




    public TextView getSelfName() {
        return selfName;
    }

    public void setSelfName(TextView selfName) {
        this.selfName = selfName;
    }

    public void bindSelfName(View view) {

        setSelfName((TextView) view);
    }

    TextView selfName;



    public void bindSelfAvatar(View view) {

        setSelfAvatar((ImageView) view);
    }

    public ImageView getSelfAvatar() {
        return selfAvatar;
    }

    public void setSelfAvatar(ImageView selfAvatar) {
        this.selfAvatar = selfAvatar;
    }

    ImageView selfAvatar;



    public TextView getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(TextView partnerName) {
        this.partnerName = partnerName;
    }

    public void bindPartnerName(View view) {

        setPartnerName((TextView) view);
    }

    TextView partnerName;




    public ImageView getPartnerAvatar() {
        return partnerAvatar;
    }

    public void setPartnerAvatar(ImageView partnerAvatar) {
        this.partnerAvatar = partnerAvatar;
    }

    public void bindPartnerAvatar(View view) {

        setPartnerAvatar((ImageView) view);
    }

    ImageView partnerAvatar;





    public void bindConnection(View view) {

        this.connection = (ImageView) view;
    }

    public ImageView connection;




    public void bindLog(View view) {this.log = (ListView) view;}

    public ListView log;
    


/*
    createRecognition(screenSupport.recognition);
    createSynthesizer(getBinder().synthesizer);
    createInternet(getBinder().internet);
    createWebsocket(getBinder().websocket);
    createLogin(getBinder().login);
*/

    public void bindRecognition(View view) {this.recognition = (TextView) view;}
    public void bindSynthesizer(View view) {this.synthesizer = (TextView) view;}
    public void bindInternet(View view) {this.internet = (TextView) view;}
    public void bindWebsocket(View view) {this.websocket = (TextView) view;}
    public void bindLogin(View view) {this.login = (TextView) view;}

    public TextView recognition;
    public TextView synthesizer;
    public TextView internet;
    public TextView websocket;
    public TextView login;


} // ScreenSupport