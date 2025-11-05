package com.runanywhere.startup_hackathon20

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.extensions.listAvailableModels
import com.runanywhere.sdk.models.ModelInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.runanywhere.startup_hackathon20.data.DI
import com.runanywhere.startup_hackathon20.data.model.Plan
import com.runanywhere.startup_hackathon20.data.model.PlanForm
import android.util.Log

// Simple Message Data Class
data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

// ViewModel
class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _availableModels = MutableStateFlow<List<ModelInfo>>(emptyList())
    val availableModels: StateFlow<List<ModelInfo>> = _availableModels

    private val _downloadProgress = MutableStateFlow<Float?>(null)
    val downloadProgress: StateFlow<Float?> = _downloadProgress

    private val _currentModelId = MutableStateFlow<String?>(null)
    val currentModelId: StateFlow<String?> = _currentModelId

    private val _statusMessage = MutableStateFlow<String>("Initializing SDK...")
    val statusMessage: StateFlow<String> = _statusMessage

    init {
        Log.i("ChatViewModel", "ViewModel initialized, waiting for SDK...")
        // Wait a bit for SDK to initialize, then load models
        viewModelScope.launch {
            delay(2000) // Wait 2 seconds for SDK initialization
            loadAvailableModelsWithRetry()
        }
    }

    private fun loadAvailableModelsWithRetry() {
        viewModelScope.launch {
            var attempts = 0
            val maxAttempts = 5

            while (attempts < maxAttempts) {
                attempts++
                try {
                    Log.i("ChatViewModel", "Attempt $attempts: Fetching available models...")
                    _statusMessage.value = "Loading models (attempt $attempts)..."

                    val models = listAvailableModels()

                    if (models.isNotEmpty()) {
                        _availableModels.value = models
                        _statusMessage.value =
                            "Found ${models.size} models. Tap 'Models' to download."
                        Log.i(
                            "ChatViewModel",
                            "✓ Found ${models.size} models: ${models.map { it.name }}"
                        )
                        return@launch // Success! Exit the retry loop
                    } else {
                        Log.w("ChatViewModel", "No models found on attempt $attempts")
                        if (attempts < maxAttempts) {
                            _statusMessage.value = "Waiting for SDK... (${attempts}/${maxAttempts})"
                            delay(2000) // Wait 2 seconds before retry
                        } else {
                            _statusMessage.value = "No models found. Try refreshing."
                            Log.e("ChatViewModel", "❌ No models found after $maxAttempts attempts")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(
                        "ChatViewModel",
                        "❌ Error loading models (attempt $attempts): ${e.message}",
                        e
                    )
                    if (attempts < maxAttempts) {
                        _statusMessage.value = "Retrying... (${attempts}/${maxAttempts})"
                        delay(2000)
                    } else {
                        _statusMessage.value = "Error: ${e.message}. Tap 'Models' to retry."
                    }
                }
            }
        }
    }

    private fun loadAvailableModels() {
        viewModelScope.launch {
            try {
                Log.i("ChatViewModel", "Fetching available models...")
                _statusMessage.value = "Loading models..."

                val models = listAvailableModels()
                _availableModels.value = models

                _statusMessage.value = if (models.isEmpty()) {
                    "No models found. SDK may still be initializing..."
                } else {
                    "Found ${models.size} models. Tap 'Models' to download."
                }
                Log.i("ChatViewModel", "✓ Found ${models.size} models: ${models.map { it.name }}")
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
                Log.e("ChatViewModel", "❌ Error loading models: ${e.message}", e)
            }
        }
    }

    fun downloadModel(modelId: String) {
        viewModelScope.launch {
            try {
                Log.i("ChatViewModel", "Starting download for model: $modelId")
                _statusMessage.value = "Downloading model..."
                _downloadProgress.value = 0f

                RunAnywhere.downloadModel(modelId).collect { progress ->
                    _downloadProgress.value = progress
                    val percent = (progress * 100).toInt()
                    _statusMessage.value = "Downloading: $percent%"
                    Log.d("ChatViewModel", "Download progress: $percent%")
                }

                _downloadProgress.value = null
                _statusMessage.value = "Download complete! Tap 'Load' to use it."
                Log.i("ChatViewModel", "✓ Download complete for model: $modelId")

                // Refresh model list to update downloaded status
                delay(500)
                loadAvailableModels()
            } catch (e: Exception) {
                _statusMessage.value = "Download failed: ${e.message}"
                _downloadProgress.value = null
                Log.e("ChatViewModel", "❌ Download failed: ${e.message}", e)
            }
        }
    }

    fun loadModel(modelId: String) {
        viewModelScope.launch {
            try {
                Log.i("ChatViewModel", "Attempting to load model: $modelId")
                _statusMessage.value = "Loading model (this may take 10-30 seconds)..."
                _isLoading.value = true

                val success = RunAnywhere.loadModel(modelId)

                if (success) {
                    _currentModelId.value = modelId
                    _statusMessage.value = "✓ Model ready! You can chat now."
                    Log.i("ChatViewModel", "✓ Model loaded successfully: $modelId")
                } else {
                    _statusMessage.value = "Failed to load model. Try closing other apps."
                    Log.e("ChatViewModel", "❌ Failed to load model (returned false): $modelId")
                }
            } catch (e: Exception) {
                _statusMessage.value = "Load error: ${e.message}"
                Log.e("ChatViewModel", "❌ Error loading model: ${e.message}", e)
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendMessage(text: String) {
        if (_currentModelId.value == null) {
            _statusMessage.value = "⚠️ Please load a model first!"
            Log.w("ChatViewModel", "⚠️ Attempted to send message without loaded model")
            return
        }

        Log.i("ChatViewModel", "Sending message: $text")

        // Add user message
        _messages.value += ChatMessage(text, isUser = true)

        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Generate response with streaming
                var assistantResponse = ""
                RunAnywhere.generateStream(text).collect { token ->
                    assistantResponse += token

                    // Update assistant message in real-time
                    val currentMessages = _messages.value.toMutableList()
                    if (currentMessages.lastOrNull()?.isUser == false) {
                        currentMessages[currentMessages.lastIndex] =
                            ChatMessage(assistantResponse, isUser = false)
                    } else {
                        currentMessages.add(ChatMessage(assistantResponse, isUser = false))
                    }
                    _messages.value = currentMessages
                }
                _statusMessage.value = "✓ Model ready! You can chat now."
                Log.i("ChatViewModel", "✓ Response generated successfully")
            } catch (e: Exception) {
                _messages.value += ChatMessage("Error: ${e.message}", isUser = false)
                _statusMessage.value = "Error generating response: ${e.message}"
                Log.e("ChatViewModel", "❌ Error generating response: ${e.message}", e)
            }

            _isLoading.value = false
        }
    }

    fun refreshModels() {
        Log.i("ChatViewModel", "Manual refresh requested...")
        _statusMessage.value = "Refreshing models..."
        loadAvailableModels()
    }

    // Create a simple placeholder plan from a form and save it to the repository.
    // This provides the integration point used by MakePlanScreen.
    fun generatePlanFromForm(form: PlanForm, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val title = "${form.from} → ${form.to} (${form.nights} nights)"
                val markdown = buildString {
                    appendLine("# $title")
                    appendLine()
                    appendLine("**Start date:** ${form.startDate}")
                    appendLine("**People:** ${form.people}")
                    appendLine("**Budget:** ${form.budget}")
                    appendLine()
                    for (i in 1..form.nights) {
                        appendLine("## Day $i")
                        appendLine("- Sample activity for day $i")
                        appendLine()
                    }
                    appendLine("\n_Generated plan (placeholder)_")
                }

                val id = java.util.UUID.randomUUID().toString()
                val plan = Plan(id = id, title = title, markdownItinerary = markdown, destinationId = form.to)
                DI.repo.savePlan(plan)
                _statusMessage.value = "Plan generated"
                onComplete(id)
            } catch (e: Exception) {
                _statusMessage.value = "Error generating plan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
