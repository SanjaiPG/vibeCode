# 🔧 AI Model Loading - Troubleshooting Guide

## 📱 How to Check Logs in Android Studio

1. **Open Logcat** in Android Studio (bottom panel)
2. **Filter by tag**: Type `MyApp` or `ChatViewModel` in the filter box
3. **Run your app** and watch for these messages:

### ✅ Expected Logs (Success):

```
MyApp: Application onCreate - Starting SDK initialization
MyApp: Step 1: Initializing SDK...
MyApp: Step 1: ✓ SDK initialized successfully
MyApp: Step 2: Registering LLM Service Provider...
MyApp: Step 2: ✓ LLM Service Provider registered
MyApp: Step 3: Registering models...
MyApp: Registering SmolLM2 360M model...
MyApp: ✓ SmolLM2 360M registered
MyApp: Registering Qwen 2.5 0.5B model...
MyApp: ✓ Qwen 2.5 0.5B registered
MyApp: Step 3: ✓ Models registered
MyApp: Step 4: Scanning for downloaded models...
MyApp: Step 4: ✓ Scan complete
MyApp: ✓✓✓ SDK initialization completed successfully ✓✓✓

ChatViewModel: ViewModel initialized, loading available models...
ChatViewModel: Fetching available models...
ChatViewModel: ✓ Found 2 models: [SmolLM2 360M Q8_0, Qwen 2.5 0.5B Instruct Q6_K]
```

---

## 🐛 Common Issues & Solutions

### ❌ Issue 1: "No models found"

**Symptoms:**

- Status shows "No models found. SDK may still be initializing..."
- Models button shows empty list

**Causes:**

1. SDK not initialized yet (takes 2-3 seconds)
2. Model registration failed
3. SDK initialization error

**Solutions:**

1. **Wait 5 seconds** after app opens, then tap "Models" button
2. Check Logcat for errors in `MyApp` tag
3. Try restarting the app
4. Tap "Models" button to refresh the list

---

### ❌ Issue 2: "Download failed"

**Symptoms:**

- Download starts but fails partway
- Error message appears

**Causes:**

1. No internet connection
2. Download URL blocked
3. Storage permission issue
4. Not enough storage space

**Solutions:**

1. Check internet connection (WiFi or mobile data)
2. Ensure you have at least **500MB free storage**
3. Grant storage permissions to the app
4. Try again on different network

---

### ❌ Issue 3: "Failed to load model"

**Symptoms:**

- Model downloaded successfully (✓ shown)
- But "Load" button doesn't work
- Status stays "No Model"

**Causes:**

1. Not enough RAM available
2. Model file corrupted
3. Another model already loaded
4. Device incompatibility

**Solutions:**

1. **Close other apps** to free memory
2. Re-download the model (delete and download again)
3. Try the **smaller model first** (SmolLM2 360M - 119MB)
4. Restart your device
5. Check device has at least **2GB RAM available**

---

### ❌ Issue 4: Models button shows "Loading models..."

**Symptoms:**

- Forever stuck on "Loading models..."
- No models appear

**Causes:**

1. SDK initialization not complete
2. SDK initialization failed
3. Timeout issue

**Solutions:**

1. Close the models popup
2. Wait 10 seconds
3. Open models popup again
4. Check Logcat for errors
5. Restart the app

---

## 🎯 Step-by-Step: How to Load a Model

### Step 1: Open AI Chat Tab

- Tap **"Chatbot"** in bottom navigation
- Wait 5 seconds for SDK to initialize

### Step 2: Open Models

- Tap **"Models"** button in header
- You should see 2 models:
    - SmolLM2 360M Q8_0 (119 MB) ⬅️ **Recommended for testing**
    - Qwen 2.5 0.5B Instruct Q6_K (374 MB)

### Step 3: Download Model

- Tap **"Download"** on SmolLM2 360M (smaller, faster)
- Watch progress bar (should take 1-5 minutes depending on connection)
- Wait for "Download complete!" message

### Step 4: Load Model

- After download completes, tap **"Load"** button
- Wait 3-10 seconds for "Model loaded! Ready to chat." message
- Status should change to "✓ Model Ready"

### Step 5: Start Chatting

- Close models popup
- Type a message
- Tap send icon
- Wait for AI response

---

## 📊 Model Sizes & Requirements

| Model | Size | RAM Needed | Speed | Quality |
|-------|------|------------|-------|---------|
| SmolLM2 360M | 119 MB | 1 GB | Fast | Basic |
| Qwen 2.5 0.5B | 374 MB | 2 GB | Medium | Good |

**Recommendation:** Start with **SmolLM2 360M** for testing!

---

## 🔍 How to Check Logcat

1. Open **Android Studio**
2. Click **Logcat** tab at bottom
3. In filter box, type: `tag:MyApp OR tag:ChatViewModel`
4. Run your app
5. Look for ✓ or ❌ symbols

**Green ✓ = Success**  
**Red ❌ = Error (read the message)**

---

## ⚡ Quick Fixes

### Models not appearing?

```
1. Wait 5 seconds
2. Tap "Models" button
3. Close and re-open models popup
```

### Download stuck?

```
1. Check internet
2. Close and re-open app
3. Try again
```

### Load fails?

```
1. Close other apps
2. Try smaller model (SmolLM2)
3. Restart device
```

### Chat not responding?

```
1. Check header shows "✓ Model Ready"
2. If not, reload model
3. Check Logcat for errors
```

---

## 📝 Debug Checklist

- [ ] App opened successfully
- [ ] Waited 5+ seconds for initialization
- [ ] Opened "Models" button
- [ ] See 2 models listed
- [ ] Downloaded a model (progress bar completed)
- [ ] Tapped "Load" button
- [ ] Status shows "✓ Model Ready"
- [ ] Can type and send messages

If ANY step fails, check Logcat for error messages!

---

## 🆘 Still Not Working?

1. **Clear app data:**
    - Settings → Apps → Your App → Storage → Clear Data
    - Restart app

2. **Check device requirements:**
    - Android 7.0+ (SDK 24+)
    - 2GB+ RAM
    - 500MB+ free storage
    - Internet connection

3. **Check Logcat for specific error:**
    - Look for lines with ❌
    - Share error message for help

4. **Try on different device/emulator:**
    - Some devices may have compatibility issues

---

## 📱 Testing on Emulator

If testing on Android Emulator:

- Use **system image with Google APIs**
- Allocate **at least 2GB RAM** to emulator
- Enable **internet in emulator settings**

---

## ✅ Success Indicators

You'll know it's working when:

1. Header shows **"✓ Model Ready"** (not "No Model")
2. Can see models when tapping "Models" button
3. Send button is **enabled** (not grayed out)
4. AI responds to your messages

---

**Good luck! 🚀**
