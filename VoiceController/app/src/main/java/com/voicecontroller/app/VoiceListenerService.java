package com.voicecontroller.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import androidx.core.app.NotificationCompat;
import java.util.ArrayList;
import java.util.Locale;

public class VoiceListenerService extends Service implements TextToSpeech.OnInitListener {
    
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "VoiceControllerChannel";
    private static final String WAKE_WORD = "orion"; // Custom wake word - Orion
    
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    private TextToSpeech textToSpeech;
    private CommandProcessor commandProcessor;
    private boolean isListening = false;
    private boolean isWakeWordMode = true;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        initializeSpeechRecognizer();
        initializeTextToSpeech();
        commandProcessor = new CommandProcessor(this);
        startForeground(NOTIFICATION_ID, createNotification());
        startListening();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Service restart ho jaye agar kill ho jaye
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Voice Controller Service",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Background voice listening service");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Voice Controller Active")
            .setContentText("Say 'Orion' to give commands")
            .setSmallIcon(R.drawable.ic_mic)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build();
    }

    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, 
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {
                // Error ke baad restart karo
                restartListening();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> voiceResults = results.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION);
                if (voiceResults != null && !voiceResults.isEmpty()) {
                    String command = voiceResults.get(0).toLowerCase();
                    
                    if (isWakeWordMode) {
                        // Wake word check karo
                        if (command.contains(WAKE_WORD) || command.contains("ओरियन") || command.contains("hey voice")) {
                            isWakeWordMode = false;
                            speak("Yes sir, I'm listening");
                            updateNotification("Listening for command...");
                        }
                    } else {
                        // Actual command process karo
                        processVoiceCommand(command);
                        isWakeWordMode = true;
                        updateNotification("Say 'Orion' to give commands");
                    }
                }
                restartListening();
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(new Locale("hi", "IN"));
        }
    }

    private void startListening() {
        if (!isListening) {
            speechRecognizer.startListening(recognizerIntent);
            isListening = true;
        }
    }

    private void restartListening() {
        isListening = false;
        // 1 second baad restart karo
        new android.os.Handler().postDelayed(() -> startListening(), 1000);
    }

    private void processVoiceCommand(String command) {
        String response = commandProcessor.processCommand(command);
        speak(response);
    }

    private void speak(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void updateNotification(String text) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Voice Controller Active")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_mic)
            .setOngoing(true)
            .build();
        
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}