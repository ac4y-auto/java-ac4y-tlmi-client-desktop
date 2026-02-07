package tlmi.communcator.atlmiclient.utility;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class Ac4ySpeechSynthesizer extends Ac4ySpeechSynthesizerAlgebra {

    public Ac4ySpeechSynthesizer(Context context){

        setTextToSpeech(new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status != TextToSpeech.SUCCESS)
                    onCreateError("TextToSpeech initialization failed");
                else
                    onCreateSuccess();

            }
        }));

    }

    public Ac4ySpeechSynthesizer(Context context, final String language, final String country){

        setTextToSpeech(new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {

                    int result = getTextToSpeech().setLanguage(new Locale(language, country));

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                        onCreateError("This language is not supported");
                    else {
                        setWorks(true);
                        onCreateSuccess();
                    }


                }
                else
                    onCreateError("TextToSpeech initialization failed");

            }
        }));

    }

    public void onCreateError(String information){
        System.out.println("parent.onCreateError");
    }

    public void onCreateSuccess(){}

    public void speak(String text){
        Bundle bundle = new Bundle();
        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
        getTextToSpeech().speak(text, TextToSpeech.QUEUE_FLUSH, bundle, null);
    }

}