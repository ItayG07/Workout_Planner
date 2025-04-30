package com.example.workoutplanner;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Locale;

public class TextToSpeechService extends Service implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private String textToSpeak;

    @Override
    public void onCreate() {
        super.onCreate();
        tts = new TextToSpeech(this, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Retrieve the text to be spoken from the Intent
        textToSpeak = intent.getStringExtra("text");

        // If TTS is already initialized, speak the text
        if (tts != null && textToSpeak != null) {
            speak(textToSpeak);
        }

        // If the service is killed, restart it with the same intent
        return START_STICKY;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported or missing data");
            } else {
                // Speak the text once the TTS engine is ready
                if (textToSpeak != null) {
                    speak(textToSpeak);
                }
            }
        } else {
            Log.e("TTS", "Initialization failed");
        }
    }

    private void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // We won't be binding this service to any component
        return null;
    }
}