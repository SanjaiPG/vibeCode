package com.runanywhere.startup_hackathon20

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.data.DI
import com.runanywhere.data.model.Plan
import com.runanywhere.data.model.PlanForm
import com.runanywhere.sdk.models.ModelInfo
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.extensions.listAvailableModels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

// Data classes
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isPlan: Boolean = false,
    val planData: PlanDisplayData? = null
)

data class AttractionCard(
    val title: String,
    val rating: Double,
    val duration: String,
    val cost: String,
    val description: String,
    val imageUrl: String?,
    val category: String = "" // Day 1, Day 2, etc.
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
    val attractions: List<AttractionCard> = emptyList() // New: List of attraction cards
)

class ChatViewModel : ViewModel() {

    // Use BuildConfig for API keys
    private val OPENAI_API_KEY = BuildConfig.OPENAI_API_KEY
    private val UNSPLASH_ACCESS_KEY = BuildConfig.UNSPLASH_ACCESS_KEY

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isModelLoading = MutableStateFlow(false)
    val isModelLoading: StateFlow<Boolean> = _isModelLoading

    private val _availableModels = MutableStateFlow<List<ModelInfo>>(emptyList())
    val availableModels: StateFlow<List<ModelInfo>> = _availableModels

    private val _downloadProgress = MutableStateFlow<Float?>(null)
    val downloadProgress: StateFlow<Float?> = _downloadProgress

    private val _currentModelId = MutableStateFlow<String?>(null)
    val currentModelId: StateFlow<String?> = _currentModelId

    private val _statusMessage = MutableStateFlow<String>("Ready to generate plans")
    val statusMessage: StateFlow<String> = _statusMessage

    private var hasAutoLoadedOnce = false
    private var isLoadingInProgress = false

    // Fetch image from Unsplash
    private suspend fun fetchDestinationImage(destination: String): String? =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Log.d("ChatViewModel", "Fetching image for: $destination")
                val encodedDestination = java.net.URLEncoder.encode(destination, "UTF-8")
                val url =
                    "https://api.unsplash.com/search/photos?query=$encodedDestination&per_page=1&orientation=landscape"

                val request = Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Client-ID $UNSPLASH_ACCESS_KEY")
                    .build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(response.body?.string() ?: "")
                    val results = jsonResponse.getJSONArray("results")
                    if (results.length() > 0) {
                        val firstImage = results.getJSONObject(0)
                        val urls = firstImage.getJSONObject("urls")
                        val imageUrl = urls.getString("regular")
                        Log.d("ChatViewModel", "✓ Image fetched: $imageUrl")
                        imageUrl
                    } else {
                        Log.w("ChatViewModel", "No images found for: $destination")
                        null
                    }
                } else {
                    Log.e("ChatViewModel", "Unsplash API error: ${response.code}")
                    null
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error fetching image: ${e.message}", e)
                null
            }
        }

    // Generate structured itinerary with attractions using OpenAI API
    private suspend fun generateStructuredItinerary(form: PlanForm): Pair<String, List<AttractionCard>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Log.d("ChatViewModel", "Generating structured itinerary with OpenAI...")

                val prompt = """
                Create a detailed travel itinerary for ${form.to} from ${form.from}.
                Duration: ${form.nights} nights (${form.nights + 1} days)
                People: ${form.people}, Budget: ₹${form.budget}
                Start Date: ${form.startDate}
                
                Provide a JSON response with:
                1. "summary": Brief overview text
                2. "attractions": Array of top attractions/activities with:
                   - "title": Name of place/activity
                   - "rating": Number 1-5
                   - "duration": Time needed (e.g., "2-3 hours")
                   - "cost": Price indicator ($$, $$$)
                   - "description": Brief description (50 words max)
                   - "day": Which day to visit (Day 1, Day 2, etc.)
                
                Return ONLY valid JSON, no markdown formatting.
            """.trimIndent()

                val jsonBody = JSONObject().apply {
                    put("model", "gpt-3.5-turbo")
                    put("messages", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "system")
                            put(
                                "content",
                                "You are a travel expert. Return only valid JSON format."
                            )
                        })
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", prompt)
                        })
                    })
                    put("max_tokens", 2000)
                    put("temperature", 0.7)
                }

                val requestBody = jsonBody.toString()
                    .toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer $OPENAI_API_KEY")
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    val jsonResponse = JSONObject(responseBody)
                    val choices = jsonResponse.getJSONArray("choices")
                    if (choices.length() > 0) {
                        val message = choices.getJSONObject(0).getJSONObject("message")
                        var content = message.getString("content").trim()

                        // Clean markdown formatting if present
                        content = content.replace("```json", "").replace("```", "").trim()

                        try {
                            val planJson = JSONObject(content)
                            val summary =
                                planJson.optString("summary", "Your travel plan for ${form.to}")
                            val attractionsArray = planJson.optJSONArray("attractions")

                            val attractions = mutableListOf<AttractionCard>()
                            if (attractionsArray != null) {
                                for (i in 0 until attractionsArray.length()) {
                                    val attr = attractionsArray.getJSONObject(i)

                                    // Fetch image for each attraction
                                    val imageUrl =
                                        fetchDestinationImage("${attr.getString("title")} ${form.to}")

                                    attractions.add(
                                        AttractionCard(
                                            title = attr.getString("title"),
                                            rating = attr.optDouble("rating", 4.5),
                                            duration = attr.optString("duration", "2-3 hours"),
                                            cost = attr.optString("cost", "$$"),
                                            description = attr.getString("description"),
                                            imageUrl = imageUrl,
                                            category = attr.optString("day", "Day 1")
                                        )
                                    )
                                }
                            }

                            Log.d("ChatViewModel", "✓ Generated ${attractions.size} attractions")
                            Pair(summary, attractions)
                        } catch (e: Exception) {
                            Log.e("ChatViewModel", "JSON parsing error: ${e.message}")
                            Pair("Generated plan for ${form.to}", emptyList())
                        }
                    } else {
                        Log.e("ChatViewModel", "No response from OpenAI")
                        Pair("Error: No response from AI", emptyList())
                    }
                } else {
                    val errorBody = response.body?.string() ?: "Unknown error"
                    Log.e("ChatViewModel", "OpenAI API error: ${response.code} - $errorBody")
                    Pair("Error generating itinerary: API returned ${response.code}", emptyList())
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error calling OpenAI: ${e.message}", e)
                Pair("Error: ${e.message}", emptyList())
            }
        }

    // Main function to generate plan using APIs
    fun generatePlanWithAPIs(form: PlanForm, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.i("ChatViewModel", "Starting plan generation for ${form.from} → ${form.to}")

                // Add user form summary to chat
                val formSummary =
                    "📝 Planning trip: ${form.from} → ${form.to}, ${form.nights} nights, ${form.people} people, ₹${form.budget}"
                _messages.value += ChatMessage(formSummary, isUser = true)

                // Show initial plan card with loading state
                val initialPlanData = PlanDisplayData(
                    from = form.from,
                    to = form.to,
                    startDate = form.startDate,
                    nights = form.nights,
                    people = form.people,
                    budget = form.budget,
                    itinerary = "🔄 Generating your personalized itinerary with AI...\n\nThis may take 30-60 seconds. Please wait...",
                    imageUrl = null,
                    attractions = emptyList()
                )
                _messages.value += ChatMessage(
                    "",
                    isUser = false,
                    isPlan = true,
                    planData = initialPlanData
                )

                // Step 1: Fetch main destination image
                _statusMessage.value = "📸 Fetching destination images..."
                val mainImageUrl = fetchDestinationImage(form.to)

                // Step 2: Generate structured itinerary with attractions
                _statusMessage.value = "✨ AI is creating your itinerary with attraction cards..."
                val (summary, attractions) = generateStructuredItinerary(form)

                // Step 3: Update plan card with complete data
                val finalPlanData = PlanDisplayData(
                    from = form.from,
                    to = form.to,
                    startDate = form.startDate,
                    nights = form.nights,
                    people = form.people,
                    budget = form.budget,
                    itinerary = summary,
                    imageUrl = mainImageUrl,
                    attractions = attractions
                )

                val currentMessages = _messages.value.toMutableList()
                if (currentMessages.lastOrNull()?.isPlan == true) {
                    currentMessages[currentMessages.lastIndex] =
                        ChatMessage("", isUser = false, isPlan = true, planData = finalPlanData)
                    _messages.value = currentMessages
                }

                // Step 4: Save plan to database
                val title = "${form.from} → ${form.to} (${form.nights} nights)"
                val id = java.util.UUID.randomUUID().toString()
                val plan = Plan(
                    id = id,
                    title = title,
                    markdownItinerary = summary,
                    destinationId = form.to
                )
                DI.repo.addPlan(plan)

                _statusMessage.value = "✅ Plan generated successfully!"
                Log.i(
                    "ChatViewModel",
                    "✓ Plan generation complete: $id with ${attractions.size} attractions"
                )
                onComplete(id)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error generating plan: ${e.message}", e)
                _statusMessage.value = "❌ Error: ${e.message}"
                _messages.value += ChatMessage(
                    "❌ Error generating plan: ${e.message}\n\nPlease check your internet connection and API keys.",
                    isUser = false
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Chat message handler (using OpenAI)
    fun sendMessage(text: String) {
        _messages.value += ChatMessage(text, isUser = true)

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = generateChatResponseWithOpenAI(text)
                _messages.value += ChatMessage(response, isUser = false)
            } catch (e: Exception) {
                _messages.value += ChatMessage("❌ Error: ${e.message}", isUser = false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun generateChatResponseWithOpenAI(message: String): String =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val jsonBody = JSONObject().apply {
                    put("model", "gpt-3.5-turbo")
                    put("messages", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "system")
                            put(
                                "content",
                                "You are a helpful travel assistant. Provide concise, practical travel advice and recommendations."
                            )
                        })
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", message)
                        })
                    })
                    put("max_tokens", 500)
                    put("temperature", 0.7)
                }

                val request = Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer $OPENAI_API_KEY")
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(response.body?.string() ?: "")
                    jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                } else {
                    "❌ Error: Unable to get response from AI"
                }
            } catch (e: Exception) {
                "❌ Error: ${e.message}"
            }
        }

    // Model loading functions (keep for local model option)
    fun manualLoadModel() {
        if (isLoadingInProgress) {
            Log.w("ChatViewModel", "Model loading already in progress")
            return
        }
        Log.i("ChatViewModel", "Manual model load requested")
        isLoadingInProgress = true
        autoLoadBestModel()
    }

    fun startModelLoading() {
        // Don't auto-load model anymore since we're using APIs
        Log.i("ChatViewModel", "Model loading skipped - using API generation")
        _statusMessage.value = "Ready to generate plans with AI"
    }

    private fun autoLoadBestModel() {
        viewModelScope.launch {
            _isModelLoading.value = true
            try {
                _statusMessage.value = "Scanning for AI models..."
                delay(500)

                val allModels = listAvailableModels()
                val models = allModels.filter { it.name.contains("qwen", ignoreCase = true) }
                _availableModels.value = models

                if (models.isEmpty()) {
                    _statusMessage.value = "No local models available. Using API generation."
                    _isModelLoading.value = false
                    isLoadingInProgress = false
                    return@launch
                }

                val downloadedModel = models.firstOrNull { it.isDownloaded }

                if (downloadedModel != null) {
                    _statusMessage.value = "Loading ${downloadedModel.name}..."
                    loadModelOptimized(downloadedModel.id)
                } else {
                    _statusMessage.value = "Local model not downloaded. Using API generation."
                    _isModelLoading.value = false
                    isLoadingInProgress = false
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error: ${e.message}", e)
                _statusMessage.value = "Using API generation"
            } finally {
                _isModelLoading.value = false
                isLoadingInProgress = false
            }
        }
    }

    private suspend fun loadModelOptimized(modelId: String) {
        _isModelLoading.value = true
        try {
            _statusMessage.value = "Loading local AI model..."
            val success = withTimeoutOrNull(60_000L) {
                RunAnywhere.loadModel(modelId)
            }

            when (success) {
                true -> {
                    _currentModelId.value = modelId
                    _statusMessage.value = "✅ Local model loaded!"
                }
                null -> _statusMessage.value = "Model load timed out. Using API generation."
                else -> _statusMessage.value = "Failed to load local model. Using API generation."
            }
        } catch (e: Exception) {
            Log.e("ChatViewModel", "Error: ${e.message}", e)
            _statusMessage.value = "Using API generation"
        } finally {
            _isModelLoading.value = false
            isLoadingInProgress = false
        }
    }

    fun refreshModels() {
        viewModelScope.launch {
            try {
                val allModels = listAvailableModels()
                _availableModels.value = allModels.filter {
                    it.name.contains("qwen", ignoreCase = true)
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error refreshing models: ${e.message}", e)
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

    fun addSystemMessage(text: String) {
        _messages.value += ChatMessage(text, isUser = false)
    }
}