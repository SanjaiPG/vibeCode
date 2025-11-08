package com.runanywhere.startup_hackathon20

import com.runanywhere.startup_hackathon20.data.firebase.FirebaseAuthManager

import android.app.Application
import android.util.Log
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.data.models.SDKEnvironment
import com.runanywhere.sdk.public.extensions.addModelFromURL
import com.runanywhere.sdk.llm.llamacpp.LlamaCppServiceProvider
import com.runanywhere.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyApplication : Application() {
    // 2. ADD A COMPANION OBJECT
    companion object {
        lateinit var authManager: FirebaseAuthManager
            private set
    }

    override fun onCreate() {
        super.onCreate()
        authManager = FirebaseAuthManager(applicationContext)

        // Initialize Repository for persistent storage
        Log.i("MyApp", "Initializing Repository with SharedPreferences...")
        Repository.initialize(applicationContext)
        Log.i("MyApp", "✓ Repository initialized successfully")

        Log.i("MyApp", "Application onCreate - Starting SDK initialization")

        // Initialize SDK asynchronously
        GlobalScope.launch(Dispatchers.IO) {
            initializeSDK()
        }
    }

    private suspend fun initializeSDK() {
        try {
            Log.i("MyApp", "Step 1: Initializing SDK...")

            // Step 1: Initialize SDK
            RunAnywhere.initialize(
                context = this@MyApplication,
                apiKey = "dev",  // Any string works in dev mode
                environment = SDKEnvironment.DEVELOPMENT
            )

            Log.i("MyApp", "Step 1: ✓ SDK initialized successfully")

            Log.i("MyApp", "Step 2: Registering LLM Service Provider...")

            // Step 2: Register LLM Service Provider
            LlamaCppServiceProvider.register()

            Log.i("MyApp", "Step 2: ✓ LLM Service Provider registered")

            Log.i("MyApp", "Step 3: Registering models...")

            // Step 3: Register Models
            registerModels()

            Log.i("MyApp", "Step 3: ✓ Models registered")

            Log.i("MyApp", "Step 4: Scanning for downloaded models...")

            // Step 4: Scan for previously downloaded models
            RunAnywhere.scanForDownloadedModels()

            Log.i("MyApp", "Step 4: ✓ Scan complete")

            Log.i("MyApp", "✓✓✓ SDK initialization completed successfully ✓✓✓")

        } catch (e: Exception) {
            Log.e("MyApp", "❌ SDK initialization failed: ${e.message}")
            Log.e("MyApp", "Stack trace:", e)
            e.printStackTrace()
        }
    }

    private suspend fun registerModels() {
        try {
            // Smaller model - faster download for testing (119 MB)
            Log.i("MyApp", "Registering SmolLM2 360M model...")
            addModelFromURL(
                url = "https://huggingface.co/prithivMLmods/SmolLM2-360M-GGUF/resolve/main/SmolLM2-360M.Q8_0.gguf",
                name = "SmolLM2 360M Q8_0",
                type = "LLM"
            )
            Log.i("MyApp", "✓ SmolLM2 360M registered")

            // Medium-sized model - better quality (374 MB)
            Log.i("MyApp", "Registering Qwen 2.5 0.5B model...")
            addModelFromURL(
                url = "https://huggingface.co/Triangle104/Qwen2.5-0.5B-Instruct-Q6_K-GGUF/resolve/main/qwen2.5-0.5b-instruct-q6_k.gguf",
                name = "Qwen 2.5 0.5B Instruct Q6_K",
                type = "LLM"
            )
            Log.i("MyApp", "✓ Qwen 2.5 0.5B registered")

        } catch (e: Exception) {
            Log.e("MyApp", "❌ Model registration failed: ${e.message}")
            e.printStackTrace()
        }
    }
}
