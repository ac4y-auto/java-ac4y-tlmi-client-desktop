package tlmi.communcator.atlmiclient.utility;

import android.content.Intent;
import android.speech.SpeechRecognizer;

public class Ac4ySpeechRecognizerAlgebra {


//    private static final int REQUEST_RECORD_PERMISSION = 100;

    public SpeechRecognizer getSpeechRecognizer() {
        return speechRecognizer;
    }

    public void setSpeechRecognizer(SpeechRecognizer speechRecognizer) {
        this.speechRecognizer = speechRecognizer;
    }

    private Intent recognizerIntent;



    public Intent getRecognizerIntent() {
        return recognizerIntent;
    }

    public void setRecognizerIntent(Intent recognizerIntent) {
        this.recognizerIntent = recognizerIntent;
    }

    private SpeechRecognizer speechRecognizer;


}