package tlmi.communcator.atlmiclient.indicator;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

public class RecognitionVisualIndicator extends VisualIndicator {


    public RecognitionVisualIndicator(Context context, View indicator){
        super(context, indicator);
    }

    public void setValue(int resource){
        //getIndicator().setImageDrawable(getContext().getDrawable(resource));
    }

    protected static int WAITING_4_SPEECH = Color.parseColor("#2196F3");
    protected static int UNDER_SPEECH = Color.parseColor("#4CAF50");
    protected static int PROCESS_SPEECH = Color.parseColor("#2196F3");

    //protected static int AFTER_SPEECH = Color.parseColor("#FF9800");
    protected static int AFTER_SPEECH = Color.parseColor("#F7C329");
    protected static int END_SPEECH = Color.parseColor("#FF9800");



    public void waiting4Speech(){
        setColor(WAITING_4_SPEECH);
    }

    public void endSpeech(){setColor(END_SPEECH);}

    public void underSpeech(){
        setColor(UNDER_SPEECH);
    }

    public void afterSpeech(){
        setColor(AFTER_SPEECH);
    }

    public void processSpeech(){
        setColor(PROCESS_SPEECH);
    }


    public boolean afterSpeechState(){
        return getState()==AFTER_SPEECH;
    }
    public boolean underSpeechState(){
        return getState()==UNDER_SPEECH;
    }



    public long getLastChange() {
        return lastChange;
    }

    public void setLastChange(long lastChange) {
        this.lastChange = lastChange;
    }

    public void storeLastChange(){
        setLastChange(System.currentTimeMillis());
    }

    public long sinceLastChange(){
        return System.currentTimeMillis() - getLastChange();
    }

    public boolean earlyChange(){return sinceLastChange()<300;}
    public boolean stabilChange(){return sinceLastChange()>500;}

    protected long lastChange = 0;


}