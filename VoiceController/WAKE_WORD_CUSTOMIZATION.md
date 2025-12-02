# Wake Word Customization Guide

## Current Wake Word: "Jarvis"

### How to Change Wake Word:

1. **Open File**: `VoiceListenerService.java`
2. **Find Line**: `private static final String WAKE_WORD = "jarvis";`
3. **Change to Your Choice**:
   ```java
   private static final String WAKE_WORD = "friday";     // For Friday
   private static final String WAKE_WORD = "alexa";      // For Alexa
   private static final String WAKE_WORD = "google";     // For Google
   private static final String WAKE_WORD = "siri";       // For Siri
   private static final String WAKE_WORD = "computer";   // For Computer
   ```

### Popular AI Assistant Names:
- **Jarvis** (Iron Man's AI)
- **Friday** (Iron Man's second AI)
- **Cortana** (Microsoft's AI)
- **Bixby** (Samsung's AI)
- **Computer** (Star Trek style)

### Custom Hindi Names:
```java
private static final String WAKE_WORD = "राज";        // Raj
private static final String WAKE_WORD = "मित्र";       // Mitr (Friend)
private static final String WAKE_WORD = "सहायक";      // Sahayak (Helper)
```

### Multiple Wake Words Support:
Current code supports:
- Main wake word (customizable)
- "hey voice" (backup)
- Hindi equivalent

### Response Customization:
Change response in same file:
```java
speak("Yes sir, I'm listening");     // Current
speak("How can I help you?");        // Alternative
speak("Ready for command");          // Alternative
speak("आपकी सेवा में हाजिर");          // Hindi
```

### Usage Examples:
```
You: "Jarvis"
AI: "Yes sir, I'm listening"
You: "WhatsApp kholo"
AI: "WhatsApp khol diya"
```

### Steps to Apply Changes:
1. Edit the wake word in code
2. Update notification text
3. Update UI help text
4. Rebuild APK
5. Install updated version

**Note**: Choose a unique word that's easy to pronounce and unlikely to be said accidentally in normal conversation.