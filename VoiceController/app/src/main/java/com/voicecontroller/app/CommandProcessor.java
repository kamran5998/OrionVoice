package com.voicecontroller.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.util.HashMap;
import java.util.Map;

public class CommandProcessor {
    
    private Context context;
    private Map<String, String> appPackages;
    
    public CommandProcessor(Context context) {
        this.context = context;
        initializeAppPackages();
    }
    
    private void initializeAppPackages() {
        appPackages = new HashMap<>();
        appPackages.put("whatsapp", "com.whatsapp");
        appPackages.put("facebook", "com.facebook.katana");
        appPackages.put("instagram", "com.instagram.android");
        appPackages.put("youtube", "com.google.android.youtube");
        appPackages.put("chrome", "com.android.chrome");
        appPackages.put("gmail", "com.google.android.gm");
        appPackages.put("maps", "com.google.android.apps.maps");
        appPackages.put("calculator", "com.google.android.calculator");
        appPackages.put("calendar", "com.google.android.calendar");
        appPackages.put("contacts", "com.google.android.contacts");
        appPackages.put("gallery", "com.google.android.apps.photos");
        appPackages.put("music", "com.google.android.music");
        appPackages.put("play store", "com.android.vending");
    }
    
    public String processCommand(String command) {
        command = command.toLowerCase().trim();
        
        // App launch commands
        if (command.contains("open") || command.contains("kholo") || command.contains("chalu karo")) {
            return handleAppLaunch(command);
        }
        
        // Phone call commands
        if (command.contains("call") || command.contains("phone") || command.contains("call karo")) {
            return handlePhoneCall(command);
        }
        
        // SMS commands
        if (command.contains("message") || command.contains("sms") || command.contains("text bhejo")) {
            return handleSMS(command);
        }
        
        // Navigation commands
        if (command.contains("back") || command.contains("wapas")) {
            return handleBack();
        }
        
        if (command.contains("home") || command.contains("ghar")) {
            return handleHome();
        }
        
        if (command.contains("recent") || command.contains("recent apps")) {
            return handleRecentApps();
        }
        
        // Screenshot commands
        if (command.contains("screenshot") || command.contains("photo lo")) {
            return handleScreenshot();
        }
        
        // Camera commands
        if (command.contains("camera") || command.contains("photo") || command.contains("picture")) {
            return handleCamera();
        }
        
        // Settings commands
        if (command.contains("settings") || command.contains("setting")) {
            return handleSettings(command);
        }
        
        // Web search commands
        if (command.contains("search") || command.contains("google") || command.contains("dhundo")) {
            return handleWebSearch(command);
        }
        
        // Volume commands
        if (command.contains("volume") || command.contains("awaz")) {
            return handleVolume(command);
        }
        
        // Brightness commands
        if (command.contains("brightness") || command.contains("roshni")) {
            return handleBrightness(command);
        }
        
        // Call handling commands
        if (command.contains("call receive") || command.contains("call uthao") || command.contains("answer")) {
            return handleAnswerCall();
        }
        
        if (command.contains("call reject") || command.contains("call cut") || command.contains("call band")) {
            return handleRejectCall();
        }
        
        if (command.contains("call end") || command.contains("call khatam") || command.contains("hang up")) {
            return handleEndCall();
        }
        
        if (command.contains("speaker on") || command.contains("speaker chalu")) {
            return handleSpeakerOn();
        }
        
        if (command.contains("speaker off") || command.contains("speaker band")) {
            return handleSpeakerOff();
        }
        
        return "Samajh nahi aaya. Kripya fir se boliye.";
    }
    
    private String handleAppLaunch(String command) {
        VoiceAccessibilityService service = VoiceAccessibilityService.instance;
        if (service == null) {
            return "Accessibility service enable nahi hai";
        }
        
        for (Map.Entry<String, String> entry : appPackages.entrySet()) {
            if (command.contains(entry.getKey())) {
                if (service.launchApp(entry.getValue())) {
                    return entry.getKey() + " khol diya";
                } else {
                    return entry.getKey() + " nahi khul saka";
                }
            }
        }
        
        return "App nahi mila";
    }
    
    private String handlePhoneCall(String command) {
        VoiceAccessibilityService service = VoiceAccessibilityService.instance;
        if (service == null) {
            return "Accessibility service enable nahi hai";
        }
        
        // Extract phone number from command
        String phoneNumber = extractPhoneNumber(command);
        if (phoneNumber != null) {
            if (service.makePhoneCall(phoneNumber)) {
                return "Call kar raha hun " + phoneNumber + " ko";
            } else {
                return "Call nahi kar saka";
            }
        }
        
        return "Phone number nahi mila";
    }
    
    private String handleSMS(String command) {
        VoiceAccessibilityService service = VoiceAccessibilityService.instance;
        if (service == null) {
            return "Accessibility service enable nahi hai";
        }
        
        // Extract phone number and message
        String phoneNumber = extractPhoneNumber(command);
        String message = extractMessage(command);
        
        if (phoneNumber != null && message != null) {
            if (service.sendSMS(phoneNumber, message)) {
                return "Message bhej diya";
            } else {
                return "Message nahi bhej saka";
            }
        }
        
        return "Phone number ya message nahi mila";
    }
    
    private String handleBack() {
        VoiceAccessibilityService service = VoiceAccessibilityService.instance;
        if (service != null && service.pressBack()) {
            return "Back button daba diya";
        }
        return "Back nahi kar saka";
    }
    
    private String handleHome() {
        VoiceAccessibilityService service = VoiceAccessibilityService.instance;
        if (service != null && service.pressHome()) {
            return "Home screen pe aa gaye";
        }
        return "Home nahi ja saka";
    }
    
    private String handleRecentApps() {
        VoiceAccessibilityService service = VoiceAccessibilityService.instance;
        if (service != null && service.openRecentApps()) {
            return "Recent apps khol diye";
        }
        return "Recent apps nahi khul sake";
    }
    
    private String handleScreenshot() {
        VoiceAccessibilityService service = VoiceAccessibilityService.instance;
        if (service != null && service.takeScreenshot()) {
            return "Screenshot le liya";
        }
        return "Screenshot nahi le saka";
    }
    
    private String handleCamera() {
        VoiceAccessibilityService service = VoiceAccessibilityService.instance;
        if (service != null && service.openCamera()) {
            return "Camera khol diya";
        }
        return "Camera nahi khul saka";
    }
    
    private String handleSettings(String command) {
        VoiceAccessibilityService service = VoiceAccessibilityService.instance;
        if (service == null) {
            return "Accessibility service enable nahi hai";
        }
        
        if (command.contains("wifi")) {
            if (service.openWifiSettings()) {
                return "WiFi settings khol diye";
            }
        } else if (command.contains("bluetooth")) {
            if (service.openBluetoothSettings()) {
                return "Bluetooth settings khol diye";
            }
        } else {
            if (service.openSettings()) {
                return "Settings khol diye";
            }
        }
        
        return "Settings nahi khul sake";
    }
    
    private String handleWebSearch(String command) {
        String searchQuery = extractSearchQuery(command);
        if (searchQuery != null) {
            try {
                Intent searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
                searchIntent.putExtra("query", searchQuery);
                searchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(searchIntent);
                return "Search kar raha hun: " + searchQuery;
            } catch (Exception e) {
                return "Search nahi kar saka";
            }
        }
        return "Search query nahi mili";
    }
    
    private String handleVolume(String command) {
        if (command.contains("up") || command.contains("badha") || command.contains("increase")) {
            return "Volume badhane ki koshish kar raha hun";
        } else if (command.contains("down") || command.contains("kam") || command.contains("decrease")) {
            return "Volume kam karne ki koshish kar raha hun";
        }
        return "Volume command samajh nahi aaya";
    }
    
    private String handleBrightness(String command) {
        if (command.contains("up") || command.contains("badha") || command.contains("increase")) {
            return "Brightness badhane ki koshish kar raha hun";
        } else if (command.contains("down") || command.contains("kam") || command.contains("decrease")) {
            return "Brightness kam karne ki koshish kar raha hun";
        }
        return "Brightness command samajh nahi aaya";
    }
    
    private String extractPhoneNumber(String command) {
        // Simple phone number extraction
        String[] words = command.split(" ");
        for (String word : words) {
            if (word.matches("\\d{10}")) {
                return word;
            }
        }
        return null;
    }
    
    private String extractMessage(String command) {
        // Extract message after "message" keyword
        int messageIndex = command.indexOf("message");
        if (messageIndex != -1 && messageIndex + 7 < command.length()) {
            return command.substring(messageIndex + 7).trim();
        }
        return null;
    }
    
    private String extractSearchQuery(String command) {
        // Extract search query after "search" keyword
        int searchIndex = command.indexOf("search");
        if (searchIndex != -1 && searchIndex + 6 < command.length()) {
            return command.substring(searchIndex + 6).trim();
        }
        
        searchIndex = command.indexOf("dhundo");
        if (searchIndex != -1 && searchIndex + 6 < command.length()) {
            return command.substring(searchIndex + 6).trim();
        }
        
        return null;
    }
    
    private String handleAnswerCall() {
        VoiceAccessibilityService service = VoiceAccessibilityService.instance;
        if (service != null && service.answerCall()) {
            return "Call receive kar diya";
        }
        return "Call receive nahi kar saka";
    }
    
    private String handleRejectCall() {
        VoiceAccessibilityService service = VoiceAccessibilityService.instance;
        if (service != null && service.rejectCall()) {
            return "Call reject kar diya";
        }
        return "Call reject nahi kar saka";
    }
    
    private String handleEndCall() {
        VoiceAccessibilityService service = VoiceAccessibilityService.instance;
        if (service != null && service.endCall()) {
            return "Call end kar diya";
        }
        return "Call end nahi kar saka";
    }
    
    private String handleSpeakerOn() {
        VoiceAccessibilityService service = VoiceAccessibilityService.instance;
        if (service != null && service.toggleSpeaker()) {
            return "Speaker on kar diya";
        }
        return "Speaker on nahi kar saka";
    }
    
    private String handleSpeakerOff() {
        VoiceAccessibilityService service = VoiceAccessibilityService.instance;
        if (service != null && service.toggleSpeaker()) {
            return "Speaker off kar diya";
        }
        return "Speaker off nahi kar saka";
    }
}