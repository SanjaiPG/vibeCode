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
import com.runanywhere.data.DI
import com.runanywhere.data.model.Plan
import com.runanywhere.data.model.PlanForm
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
    val itinerary: String,
    val imageUrl: String? = null,
    val attractions: List<AttractionCardData> = emptyList()
)

data class AttractionCardData(
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    val rating: Double = 4.5,
    val duration: String = "2-3 hours",
    val cost: String = "$$",
    val category: String = ""
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

    private val _isModelLoading = MutableStateFlow(false)
    val isModelLoading: StateFlow<Boolean> = _isModelLoading

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
        if (!hasStartedLoading) {
            Log.i("ChatViewModel", "Chat tab opened - starting auto model load...")
            manualLoadModel()
        } else {
            Log.i("ChatViewModel", "Model loading already started")
        }
    }

    private fun autoLoadBestModel() {
        viewModelScope.launch {
            try {
                _isModelLoading.value = true
                _statusMessage.value = "Scanning for AI models..."
                delay(500) // Reduced delay

                val allModels = listAvailableModels()
                // Filter to only show Qwen models
                val models = allModels.filter { it.name.contains("qwen", ignoreCase = true) }
                _availableModels.value = models

                if (models.isEmpty()) {
                    _statusMessage.value = "No Qwen models available. Please check SDK setup."
                    Log.e("ChatViewModel", "No Qwen models found")
                    _isModelLoading.value = false
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
                        _isModelLoading.value = false
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error in autoLoadBestModel: ${e.message}", e)
                _statusMessage.value = "Error loading AI: ${e.message}"
                _isModelLoading.value = false
            }
        }
    }

    private suspend fun loadModelOptimized(modelId: String) {
        try {
            _isModelLoading.value = true
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
        } finally {
            _isModelLoading.value = false
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
                // Ultra-concise prompt for 3x faster generation
                val prompt =
                    """Plan: ${form.from} → ${form.to} | ${form.nights}N | ${form.people}p | ₹${form.budget}
                    |Start: ${form.startDate}
                    |
                    |Day-by-day (morning/afternoon/evening):
                    |Hotels (name, price, rating):
                    |Transport tips:
                    |Must-see (top 3):
                    |Food to try:
                    |Budget breakdown:
                    |Keep brief.""".trimMargin()

                var aiItinerary = ""
                var tokenCount = 0
                val MAX_TOKENS = 300 // Limit to 300 tokens for faster generation (10-15 seconds)

                RunAnywhere.generateStream(prompt).collect { token ->
                    if (tokenCount >= MAX_TOKENS) {
                        // Stop after 300 tokens to keep generation fast
                        return@collect
                    }
                    aiItinerary += token
                    tokenCount++
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
                DI.repo.addPlan(plan)

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
                    "Plan: ${form.from} to ${form.to}, ${form.nights}N, ${form.people} people, ₹${form.budget}"
                _messages.value += ChatMessage(formSummary, isUser = true)

                // Ultra-concise prompt for 3x faster generation  
                val prompt =
                    """Plan: ${form.from} → ${form.to} | ${form.nights}N | ${form.people}p | ₹${form.budget}
                    |Start: ${form.startDate}
                    |
                    |Day-by-day (morning/afternoon/evening):
                    |Hotels (name, price, rating):
                    |Transport tips:
                    |Must-see (top 3):
                    |Food to try:
                    |Budget breakdown:
                    |Keep brief.""".trimMargin()

                var aiItinerary = ""
                var tokenCount = 0
                val MAX_TOKENS = 300 // Limit to 300 tokens for faster generation

                RunAnywhere.generateStream(prompt).collect { token ->
                    if (tokenCount >= MAX_TOKENS) {
                        // Stop after 300 tokens
                        return@collect
                    }
                    aiItinerary += token
                    tokenCount++

                    // Update UI every 3 tokens for faster visual feedback (was 5)
                    if (tokenCount % 3 == 0) {
                        val currentMessages = _messages.value.toMutableList()
                        val planData = PlanDisplayData(
                            from = form.from,
                            to = form.to,
                            startDate = form.startDate,
                            nights = form.nights,
                            people = form.people,
                            budget = form.budget,
                            itinerary = aiItinerary,
                            imageUrl = null,
                            attractions = emptyList()
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
                DI.repo.addPlan(plan)

                val finalPlanData = PlanDisplayData(
                    from = form.from,
                    to = form.to,
                    startDate = form.startDate,
                    nights = form.nights,
                    people = form.people,
                    budget = form.budget,
                    itinerary = aiItinerary,
                    imageUrl = null,
                    attractions = emptyList()
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

    fun generatePlanWithAPIs(form: PlanForm, onComplete: (String) -> Unit) {
        // Use the existing on-device model generation
        // This provides better privacy and works offline
        if (_currentModelId.value == null) {
            _statusMessage.value = "AI model is loading, please wait..."
            Log.w("ChatViewModel", "Attempted to generate plan without loaded model")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "AI is creating your itinerary..."

            try {
                // Add form details as user message in chat
                val formSummary =
                    "Plan: ${form.from} to ${form.to}, ${form.nights}N, ${form.people} people, ₹${form.budget}"
                _messages.value += ChatMessage(formSummary, isUser = true)

                // Ultra-concise prompt for 3x faster generation  
                val prompt =
                    """Plan: ${form.from} → ${form.to} | ${form.nights}N | ${form.people}p | ₹${form.budget}
                    |Start: ${form.startDate}
                    |
                    |Day-by-day (morning/afternoon/evening):
                    |Hotels (name, price, rating):
                    |Transport tips:
                    |Must-see (top 3):
                    |Food to try:
                    |Budget breakdown:
                    |Keep brief.""".trimMargin()

                var aiItinerary = ""
                var tokenCount = 0
                val MAX_TOKENS = 400 // Increased to 400 for more detailed plans

                _statusMessage.value = "Generating day-by-day itinerary..."

                RunAnywhere.generateStream(prompt).collect { token ->
                    if (tokenCount >= MAX_TOKENS) {
                        return@collect
                    }
                    aiItinerary += token
                    tokenCount++

                    // Update UI every 5 tokens for visual feedback
                    if (tokenCount % 5 == 0) {
                        _statusMessage.value = "Generated ${tokenCount} words..."

                        val currentMessages = _messages.value.toMutableList()
                        val planData = PlanDisplayData(
                            from = form.from,
                            to = form.to,
                            startDate = form.startDate,
                            nights = form.nights,
                            people = form.people,
                            budget = form.budget,
                            itinerary = aiItinerary,
                            imageUrl = null,
                            attractions = emptyList()
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

                // Save plan to repository
                val title = "${form.from} → ${form.to} (${form.nights} nights)"
                val id = java.util.UUID.randomUUID().toString()
                val plan = Plan(
                    id = id,
                    title = title,
                    markdownItinerary = aiItinerary,
                    destinationId = form.to
                )
                DI.repo.addPlan(plan)

                // Final update with complete plan in chat
                val finalPlanData = PlanDisplayData(
                    from = form.from,
                    to = form.to,
                    startDate = form.startDate,
                    nights = form.nights,
                    people = form.people,
                    budget = form.budget,
                    itinerary = aiItinerary,
                    imageUrl = null,
                    attractions = emptyList()
                )

                val currentMessages = _messages.value.toMutableList()
                if (currentMessages.lastOrNull()?.isPlan == true) {
                    currentMessages[currentMessages.lastIndex] =
                        ChatMessage("", isUser = false, isPlan = true, planData = finalPlanData)
                    _messages.value = currentMessages
                }

                // Add success notification message directing to My Plans
                _messages.value += ChatMessage(
                    "✅ Your travel plan has been created successfully!\n\n" +
                            "📋 Go to the \"Plans\" tab at the bottom to view your complete itinerary.\n\n" +
                            "💡 Tip: You can also like your favorite plans to access them quickly!",
                    isUser = false
                )

                _statusMessage.value = "Plan saved! Check 'Plans' tab to view it."
                Log.i("ChatViewModel", "✓ Plan generated and saved: $id")
                onComplete(id)
            } catch (e: Exception) {
                _statusMessage.value = "Error generating plan: ${e.message}"
                _messages.value += ChatMessage(
                    "❌ Error generating plan: ${e.message}\n\nPlease try again.",
                    isUser = false
                )
                Log.e("ChatViewModel", "Error in generatePlanWithAPIs: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Add system/AI greeting message
    fun addSystemMessage(text: String) {
        _messages.value += ChatMessage(text, isUser = false)
    }
}
