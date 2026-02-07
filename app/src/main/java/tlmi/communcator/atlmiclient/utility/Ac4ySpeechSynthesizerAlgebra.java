package tlmi.communcator.atlmiclient.utility;

import android.speech.tts.TextToSpeech;

public class Ac4ySpeechSynthesizerAlgebra {

    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }

    public void setTextToSpeech(TextToSpeech textToSpeech) {
        this.textToSpeech = textToSpeech;
    }

    private TextToSpeech textToSpeech;


    public boolean isWorks() {
        return works;
    }

    public void setWorks(boolean works) {
        this.works = works;
    }

    private boolean works = false;




}
