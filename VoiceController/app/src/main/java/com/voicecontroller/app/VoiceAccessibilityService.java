package com.voicecontroller.app;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelecomManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

public class VoiceAccessibilityService extends AccessibilityService {
    
    public static VoiceAccessibilityService instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Handle accessibility events if needed
    }

    @Override
    public void onInterrupt() {
        // Handle interruption
    }

    // App launch karne ke liye
    public boolean launchApp(String packageName) {
        try {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launchIntent);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Text click karne ke liye
    public boolean clickOnText(String text) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText(text);
            for (AccessibilityNodeInfo node : nodes) {
                if (node.isClickable()) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            }
        }
        return false;
    }

    // Back button press
    public boolean pressBack() {
        return performGlobalAction(GLOBAL_ACTION_BACK);
    }

    // Home button press
    public boolean pressHome() {
        return performGlobalAction(GLOBAL_ACTION_HOME);
    }

    // Recent apps
    public boolean openRecentApps() {
        return performGlobalAction(GLOBAL_ACTION_RECENTS);
    }

    // Notifications panel
    public boolean openNotifications() {
        return performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS);
    }

    // Quick settings
    public boolean openQuickSettings() {
        return performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS);
    }

    // Screenshot lene ke liye
    public boolean takeScreenshot() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
        }
        return false;
    }

    // Phone call karne ke liye
    public boolean makePhoneCall(String phoneNumber) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(callIntent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // SMS bhejne ke liye
    public boolean sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Camera open karne ke liye
    public boolean openCamera() {
        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(cameraIntent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Settings open karne ke liye
    public boolean openSettings() {
        try {
            Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(settingsIntent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // WiFi settings
    public boolean openWifiSettings() {
        try {
            Intent wifiIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            wifiIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(wifiIntent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Bluetooth settings
    public boolean openBluetoothSettings() {
        try {
            Intent bluetoothIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            bluetoothIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(bluetoothIntent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Incoming call answer karne ke liye
    public boolean answerCall() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
                if (telecomManager != null) {
                    telecomManager.acceptRingingCall();
                    return true;
                }
            } else {
                // Older versions ke liye accessibility action
                return performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Incoming call reject karne ke liye
    public boolean rejectCall() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
                if (telecomManager != null) {
                    telecomManager.endCall();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Call end karne ke liye
    public boolean endCall() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
                if (telecomManager != null) {
                    telecomManager.endCall();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Speaker on/off karne ke liye
    public boolean toggleSpeaker() {
        try {
            // Audio manager se speaker toggle kar sakte hain
            android.media.AudioManager audioManager = (android.media.AudioManager) getSystemService(AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.setSpeakerphoneOn(!audioManager.isSpeakerphoneOn());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Coordinate pe click karne ke liye
    public boolean clickAtCoordinate(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Path clickPath = new Path();
            clickPath.moveTo(x, y);
            
            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(clickPath, 0, 100));
            
            return dispatchGesture(gestureBuilder.build(), null, null);
        }
        return false;
    }

    // Swipe gesture
    public boolean swipe(int startX, int startY, int endX, int endY) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Path swipePath = new Path();
            swipePath.moveTo(startX, startY);
            swipePath.lineTo(endX, endY);
            
            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 500));
            
            return dispatchGesture(gestureBuilder.build(), null, null);
        }
        return false;
    }

    // Text input karne ke liye
    public boolean inputText(String text) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            AccessibilityNodeInfo editText = findEditText(rootNode);
            if (editText != null) {
                editText.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                editText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, 
                    android.os.Bundle.forPair(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text));
                return true;
            }
        }
        return false;
    }

    private AccessibilityNodeInfo findEditText(AccessibilityNodeInfo node) {
        if (node == null) return null;
        
        if (node.getClassName() != null && 
            node.getClassName().toString().contains("EditText")) {
            return node;
        }
        
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            AccessibilityNodeInfo result = findEditText(child);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}