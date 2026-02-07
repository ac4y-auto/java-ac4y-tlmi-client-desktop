package tlmi.communcator.atlmiclient.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import static android.speech.SpeechRecognizer.ERROR_NO_MATCH;

import android.content.BroadcastReceiver;
import android.support.v4.os.ConfigurationCompat;

import java.util.Locale;

public class Ac4ySpeechRecognizer extends Ac4ySpeechRecognizerAlgebra implements RecognitionListener {

    public Ac4ySpeechRecognizer(Context context){

        //AudioManager audioManager=(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,  AudioManager.FLAG_SHOW_UI);
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_SHOW_UI)

        ///audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 1);

        //audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);

/*
        Intent intent = new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
        LangBroadcastReceiver myBroadcastReceiver = new LangBroadcastReceiver(this, data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
        sendOrderedBroadcast(intent, null, myBroadcastReceiver, null, Activity.RESULT_OK, null, null);
*/



        setSpeechRecognizer(SpeechRecognizer.createSpeechRecognizer(context));
        getSpeechRecognizer().setRecognitionListener(this);

/*
        setRecognizerIntent(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));

        ///getRecognizerIntent().putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 10000);

        getRecognizerIntent().putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        getRecognizerIntent().putExtra("android.speech.extra.DICTATION_MODE", true);
        getRecognizerIntent().putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
*/


        Locale locale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);

        String myLanguage= locale.getLanguage()+"_"+locale.getCountry();

        setRecognizerIntent(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
        getRecognizerIntent().putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        getRecognizerIntent().putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
        getRecognizerIntent().putExtra(RecognizerIntent.EXTRA_LANGUAGE, myLanguage);
        getRecognizerIntent().putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, myLanguage);
        getRecognizerIntent().putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, myLanguage);
        getRecognizerIntent().putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);

        onCreateSuccess();


    }



    public void start()
    {
        getSpeechRecognizer().startListening(getRecognizerIntent());
    }

    public void stop()
    {
        getSpeechRecognizer().stopListening();
    }


    public void onCreateSuccess(){

    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        if (error==ERROR_NO_MATCH)
            start();
    }

    @Override
    public void onResults(Bundle results) {
        start();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

} // Ac4ySpeechRecognizer