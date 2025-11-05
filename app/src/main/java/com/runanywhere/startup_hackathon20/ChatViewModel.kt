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
    val isUser: Boolean,
    val isPlan: Boolean = false,
    val planData: PlanDisplayData? = null
)

data class PlanDisplayData(
    val from: String,
    val to: String,
    val startDate: String,
    val nights: Int,
    val people: Int,
    val budget: Int,
    val itinerary: String
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

    private val _statusMessage = MutableStateFlow<String>("Tap settings to load AI model")
    val statusMessage: StateFlow<String> = _statusMessage

    private var hasStartedLoading = false

    // Manual model loading - call this explicitly when user wants to load
    fun manualLoadModel() {
        if (hasStartedLoading) return
        hasStartedLoading = true
        Log.i("ChatViewModel", "Manual model load requested...")
        autoLoadBestModel()
    }

    // Call this when user opens chat tab for the first time
    fun startModelLoading() {
        // Removed auto-loading - now requires manual trigger
        Log.i("ChatViewModel", "Chat tab opened - waiting for manual model load...")
    }

    private fun autoLoadBestModel() {
        viewModelScope.launch {
            try {
                _statusMessage.value = "Scanning for AI models..."
                delay(500) // Reduced delay

                val allModels = listAvailableModels()
                // Filter to only show Qwen models
                val models = allModels.filter { it.name.contains("qwen", ignoreCase = true) }
                _availableModels.value = models

                if (models.isEmpty()) {
                    _statusMessage.value = "No Qwen models available. Please check SDK setup."
                    Log.e("ChatViewModel", "No Qwen models found")
                    return@launch
                }

                Log.i(
                    "ChatViewModel",
                    "Found ${models.size} Qwen models: ${models.map { it.name }}"
                )

                // Find the smallest/fastest model that's already downloaded
                val downloadedModel = models.firstOrNull { it.isDownloaded }

                if (downloadedModel != null) {
                    Log.i("ChatViewModel", "Found downloaded model: ${downloadedModel.name}")
                    _statusMessage.value = "Loading AI model..."
                    loadModelOptimized(downloadedModel.id)
                } else {
                    // Auto-download and load the first available model (usually the smallest)
                    val bestModel = models.first()
                    Log.i("ChatViewModel", "Auto-downloading model: ${bestModel.name}")
                    _statusMessage.value = "Downloading AI model (first time only)..."

                    try {
                        RunAnywhere.downloadModel(bestModel.id).collect { progress ->
                            _downloadProgress.value = progress
                            _statusMessage.value = "Downloading: ${(progress * 100).toInt()}%"
                        }
                        _downloadProgress.value = null

                        Log.i("ChatViewModel", "Download complete, loading model...")
                        _statusMessage.value = "Loading AI model..."
                        delay(300) // Reduced delay
                        loadModelOptimized(bestModel.id)
                    } catch (e: Exception) {
                        Log.e("ChatViewModel", "Auto-download failed: ${e.message}", e)
                        _statusMessage.value = "Download failed. Tap settings to retry."
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error in autoLoadBestModel: ${e.message}", e)
                _statusMessage.value = "Error loading AI: ${e.message}"
            }
        }
    }

    private suspend fun loadModelOptimized(modelId: String) {
        try {
            Log.i("ChatViewModel", "Loading model: $modelId")
            _statusMessage.value = "Loading AI (this may take 10-30 seconds)..."

            val success = RunAnywhere.loadModel(modelId)

            if (success) {
                _currentModelId.value = modelId
                _statusMessage.value = ""
                Log.i("ChatViewModel", "✓ Model loaded successfully and ready!")
            } else {
                _statusMessage.value = "Failed to load model. Please restart the app."
                Log.e("ChatViewModel", "Failed to load model (returned false)")
            }
        } catch (e: Exception) {
            Log.e("ChatViewModel", "Error loading model: ${e.message}", e)
            _statusMessage.value = "Error: ${e.message}"
        }
    }

    private fun loadAvailableModels() {
        viewModelScope.launch {
            try {
                val allModels = listAvailableModels()
                // Filter to only show Qwen models
                val models = allModels.filter { it.name.contains("qwen", ignoreCase = true) }
                _availableModels.value = models
                _statusMessage.value = if (models.isEmpty()) {
                    "No Qwen models available"
                } else {
                    "Found ${models.size} Qwen model(s)"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
                Log.e("ChatViewModel", "Error loading models: ${e.message}", e)
            }
        }
    }

    fun downloadModel(modelId: String) {
        viewModelScope.launch {
            try {
                _statusMessage.value = "Downloading model..."
                RunAnywhere.downloadModel(modelId).collect { progress ->
                    _downloadProgress.value = progress
                    _statusMessage.value = "Downloading: ${(progress * 100).toInt()}%"
                }
                _downloadProgress.value = null
                _statusMessage.value = "Download complete!"

                // Auto-load after download
                delay(500)
                loadModelOptimized(modelId)
            } catch (e: Exception) {
                _statusMessage.value = "Download failed: ${e.message}"
                _downloadProgress.value = null
            }
        }
    }

    fun loadModel(modelId: String) {
        viewModelScope.launch {
            loadModelOptimized(modelId)
        }
    }

    fun sendMessage(text: String) {
        if (_currentModelId.value == null) {
            _statusMessage.value = "AI is still loading, please wait..."
            Log.w("ChatViewModel", "Attempted to send message without loaded model")
            return
        }

        Log.i("ChatViewModel", "Sending message: $text")

        // Add user message
        _messages.value += ChatMessage(text, isUser = true)

        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Generate response with streaming for faster perceived speed
                var assistantResponse = ""
                var tokenCount = 0

                RunAnywhere.generateStream(text).collect { token ->
                    assistantResponse += token
                    tokenCount++

                    // Update UI every token for instant streaming (faster response feel)
                    val currentMessages = _messages.value.toMutableList()
                    if (currentMessages.lastOrNull()?.isUser == false && currentMessages.lastOrNull()?.isPlan == false) {
                        currentMessages[currentMessages.lastIndex] =
                            ChatMessage(assistantResponse, isUser = false)
                    } else {
                        currentMessages.add(ChatMessage(assistantResponse, isUser = false))
                    }
                    _messages.value = currentMessages
                }

                // Final update to ensure we have the complete message
                val currentMessages = _messages.value.toMutableList()
                if (currentMessages.lastOrNull()?.isUser == false && currentMessages.lastOrNull()?.isPlan == false) {
                    currentMessages[currentMessages.lastIndex] =
                        ChatMessage(assistantResponse, isUser = false)
                    _messages.value = currentMessages
                }

                Log.i("ChatViewModel", "✓ Response generated successfully")
            } catch (e: Exception) {
                _messages.value += ChatMessage("Error: ${e.message}", isUser = false)
                Log.e("ChatViewModel", "Error generating response: ${e.message}", e)
            }

            _isLoading.value = false
        }
    }

    fun refreshModels() {
        Log.i("ChatViewModel", "Manual refresh requested...")
        _statusMessage.value = "Refreshing models..."
        loadAvailableModels()
    }

    // Generate a travel plan using AI WITHOUT displaying in chatbot
    // This is used when navigating directly to plan result screen
    fun generatePlanDirect(form: PlanForm, onComplete: (String) -> Unit) {
        if (_currentModelId.value == null) {
            _statusMessage.value = "Please load the model first."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Generate AI-powered itinerary silently (no chat messages)
                val prompt = """Create a detailed travel itinerary for the following trip:
                    |From: ${form.from}
                    |To: ${form.to}
                    |Start Date: ${form.startDate}
                    |Duration: ${form.nights} nights
                    |Number of People: ${form.people}
                    |Budget: ₹${form.budget}
                    |
                    |Please provide:
                    |1. Daily itinerary with activities, timings, and estimated costs
                    |2. Recommended accommodations
                    |3. Transportation suggestions
                    |4. Must-visit attractions
                    |5. Food recommendations
                    |6. Budget breakdown
                    |7. Travel tips and important notes
                    |
                    |Format the response clearly with day-wise breakdown.""".trimMargin()

                var aiItinerary = ""

                RunAnywhere.generateStream(prompt).collect { token ->
                    aiItinerary += token
                }

                // Save plan with complete itinerary
                val title = "${form.from} → ${form.to} (${form.nights} nights)"
                val id = java.util.UUID.randomUUID().toString()
                val plan = Plan(
                    id = id,
                    title = title,
                    markdownItinerary = aiItinerary,
                    destinationId = form.to
                )
                DI.repo.savePlan(plan)

                _statusMessage.value = "Plan generated successfully!"
                onComplete(id)
            } catch (e: Exception) {
                _statusMessage.value = "Error generating plan: ${e.message}"
                Log.e("ChatViewModel", "Error generating plan: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Generate a travel plan using AI and display it in the chatbot with unique UI
    fun generatePlanFromForm(form: PlanForm, onComplete: (String) -> Unit) {
        if (_currentModelId.value == null) {
            _statusMessage.value = "Please load the model first."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Add form details as user message
                val formSummary =
                    "Generate a detailed ${form.nights}-night travel plan from ${form.from} to ${form.to}, starting ${form.startDate}, for ${form.people} people with a budget of ₹${form.budget}"
                _messages.value += ChatMessage(formSummary, isUser = true)

                // Generate AI-powered itinerary
                val prompt = """Create a detailed travel itinerary for the following trip:
                    |From: ${form.from}
                    |To: ${form.to}
                    |Start Date: ${form.startDate}
                    |Duration: ${form.nights} nights
                    |Number of People: ${form.people}
                    |Budget: ₹${form.budget}
                    |
                    |Please provide:
                    |1. Daily itinerary with activities, timings, and estimated costs
                    |2. Recommended accommodations
                    |3. Transportation suggestions
                    |4. Must-visit attractions
                    |5. Food recommendations
                    |6. Budget breakdown
                    |7. Travel tips and important notes
                    |
                    |Format the response clearly with day-wise breakdown.""".trimMargin()

                var aiItinerary = ""
                var tokenCount = 0

                RunAnywhere.generateStream(prompt).collect { token ->
                    aiItinerary += token
                    tokenCount++

                    // Update UI every 3 tokens for faster plan generation
                    if (tokenCount % 3 == 0) {
                        val currentMessages = _messages.value.toMutableList()
                        val planData = PlanDisplayData(
                            from = form.from,
                            to = form.to,
                            startDate = form.startDate,
                            nights = form.nights,
                            people = form.people,
                            budget = form.budget,
                            itinerary = aiItinerary
                        )

                        if (currentMessages.lastOrNull()?.isPlan == true) {
                            currentMessages[currentMessages.lastIndex] =
                                ChatMessage("", isUser = false, isPlan = true, planData = planData)
                        } else {
                            currentMessages.add(
                                ChatMessage(
                                    "",
                                    isUser = false,
                                    isPlan = true,
                                    planData = planData
                                )
                            )
                        }
                        _messages.value = currentMessages
                    }
                }

                // Final update with complete plan
                val title = "${form.from} → ${form.to} (${form.nights} nights)"
                val id = java.util.UUID.randomUUID().toString()
                val plan = Plan(
                    id = id,
                    title = title,
                    markdownItinerary = aiItinerary,
                    destinationId = form.to
                )
                DI.repo.savePlan(plan)

                val finalPlanData = PlanDisplayData(
                    from = form.from,
                    to = form.to,
                    startDate = form.startDate,
                    nights = form.nights,
                    people = form.people,
                    budget = form.budget,
                    itinerary = aiItinerary
                )

                val currentMessages = _messages.value.toMutableList()
                if (currentMessages.lastOrNull()?.isPlan == true) {
                    currentMessages[currentMessages.lastIndex] =
                        ChatMessage("", isUser = false, isPlan = true, planData = finalPlanData)
                    _messages.value = currentMessages
                }

                _statusMessage.value = "Plan generated successfully!"
                onComplete(id)
            } catch (e: Exception) {
                _statusMessage.value = "Error generating plan: ${e.message}"
                _messages.value += ChatMessage(
                    "Error generating plan: ${e.message}",
                    isUser = false
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}
