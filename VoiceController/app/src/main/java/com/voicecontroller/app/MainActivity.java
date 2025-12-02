package com.voicecontroller.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Build;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    private TextToSpeech textToSpeech;
    private Button startListeningButton;
    private TextView statusText;
    private CommandProcessor commandProcessor;
    private boolean isListening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeViews();
        requestPermissions();
        initializeSpeechRecognizer();
        initializeTextToSpeech();
        commandProcessor = new CommandProcessor(this);
        
        checkAccessibilityPermission();
        startBackgroundService();
    }

    private void initializeViews() {
        startListeningButton = findViewById(R.id.startListeningButton);
        statusText = findViewById(R.id.statusText);
        
        startListeningButton.setOnClickListener(v -> toggleListening());
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.RECORD_AUDIO}, 
                REQUEST_RECORD_AUDIO_PERMISSION);
        }
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
            public void onReadyForSpeech(Bundle params) {
                statusText.setText("Sunne ke liye ready...");
            }

            @Override
            public void onBeginningOfSpeech() {
                statusText.setText("Sun raha hun...");
            }

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                statusText.setText("Processing...");
            }

            @Override
            public void onError(int error) {
                statusText.setText("Error: " + getErrorText(error));
                isListening = false;
                startListeningButton.setText("Start Listening");
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> voiceResults = results.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION);
                if (voiceResults != null && !voiceResults.isEmpty()) {
                    String command = voiceResults.get(0);
                    statusText.setText("Command: " + command);
                    processVoiceCommand(command);
                }
                isListening = false;
                startListeningButton.setText("Start Listening");
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> partialVoiceResults = partialResults.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION);
                if (partialVoiceResults != null && !partialVoiceResults.isEmpty()) {
                    statusText.setText("Partial: " + partialVoiceResults.get(0));
                }
            }

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

    private void toggleListening() {
        if (isListening) {
            speechRecognizer.stopListening();
            isListening = false;
            startListeningButton.setText("Start Listening");
            statusText.setText("Stopped listening");
        } else {
            speechRecognizer.startListening(recognizerIntent);
            isListening = true;
            startListeningButton.setText("Stop Listening");
        }
    }

    private void processVoiceCommand(String command) {
        String response = commandProcessor.processCommand(command);
        speak(response);
    }

    public void speak(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void checkAccessibilityPermission() {
        if (!isAccessibilityServiceEnabled()) {
            Toast.makeText(this, "Accessibility Service enable karo", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }
    }

    private boolean isAccessibilityServiceEnabled() {
        String service = getPackageName() + "/" + VoiceAccessibilityService.class.getCanonicalName();
        String enabledServices = Settings.Secure.getString(getContentResolver(),
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return enabledServices != null && enabledServices.contains(service);
    }

    private String getErrorText(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO: return "Audio error";
            case SpeechRecognizer.ERROR_CLIENT: return "Client error";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS: return "Permission nahi hai";
            case SpeechRecognizer.ERROR_NETWORK: return "Network error";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT: return "Network timeout";
            case SpeechRecognizer.ERROR_NO_MATCH: return "Koi match nahi mila";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY: return "Recognizer busy hai";
            case SpeechRecognizer.ERROR_SERVER: return "Server error";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT: return "Speech timeout";
            default: return "Unknown error";
        }
    }

    private void startBackgroundService() {
        Intent serviceIntent = new Intent(this, VoiceListenerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    @Override
    protected void onDestroy() {
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