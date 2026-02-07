package tlmi.communcator.atlmiclient.indicator;

import android.content.Context;
import android.view.View;

import tlmi.communcator.atlmiclient.control.ScreenSupport;

public class TlmiVisualIndicatorBar extends VisualIndicatorBarAlgebra {


    public TlmiVisualIndicatorBar(Context context, ScreenSupport screenSupport){

        setContext(context);

        createRecognition(screenSupport.recognition);
        createSynthesizer(screenSupport.synthesizer);
        createInternet(screenSupport.internet);
        createWebsocket(screenSupport.websocket);
        createLogin(screenSupport.login);

    }



    public RecognitionVisualIndicator getRecognition() {
        return recognition;
    }

    public void setRecognition(RecognitionVisualIndicator recognition) {
        this.recognition = recognition;
    }

    public void createRecognition(View indicator){
        setRecognition(new RecognitionVisualIndicator(getContext(), indicator));

    }

    private RecognitionVisualIndicator recognition;




    public VisualIndicator getSynthesizer() {
        return synthesizer;
    }

    public void setSynthesizer(VisualIndicator synthesizer) {
        this.synthesizer = synthesizer;
    }

    public void createSynthesizer(View indicator){
        setSynthesizer(new VisualIndicator(getContext(), indicator));

    }

    private VisualIndicator synthesizer;



    public VisualIndicator getInternet() {
        return internet;
    }

    public void setInternet(VisualIndicator internet) {
        this.internet = internet;
    }

    public void createInternet(View indicator){
        setInternet(new VisualIndicator(getContext(), indicator));

    }

    private VisualIndicator internet;




    public VisualIndicator getWebsocket() {
        return websocket;
    }

    public void setWebsocket(VisualIndicator websocket) {
        this.websocket = websocket;
    }

    public void createWebsocket(View indicator){
        setWebsocket(new VisualIndicator(getContext(), indicator));

    }

    private VisualIndicator websocket;



    public VisualIndicator getLogin() {
        return login;
    }

    public void setLogin(VisualIndicator login) {
        this.login = login;
    }

    public void createLogin(View indicator){
        setLogin(new VisualIndicator(getContext(), indicator));

    }

    private VisualIndicator login;
    




}
