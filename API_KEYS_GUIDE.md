# 🔑 API Keys Configuration Guide

This guide explains what API keys you need and where to add them for all the enhanced features.

## 📋 Required API Keys

| Feature                | API Service                          | Free Tier                   | Where to Get                                                                              |
|------------------------|--------------------------------------|-----------------------------|-------------------------------------------------------------------------------------------|
| **Real-time Search**   | Google Places API                    | ✅ Yes ($200 credit/month)   | [Google Cloud Console](https://console.cloud.google.com/)                                 |
| **Destination Images** | Unsplash API                         | ✅ Yes (50 requests/hour)    | [Unsplash Developers](https://unsplash.com/developers)                                    |
| **AI Descriptions**    | Google Gemini                        | ✅ Yes (Free tier available) | [Google AI Studio](https://makersuite.google.com/app/apikey)                              |
| **Weather Data**       | OpenWeather API                      | ✅ Yes (1,000 calls/day)     | [OpenWeather](https://openweathermap.org/api)                                             |
| **Reviews**            | Google Places API (Reviews)          | ✅ Yes                       | Same as Places API                                                                        |
| **Attractions**        | Google Places API                    | ✅ Yes                       | Same as Places API                                                                        |
| **Social Hashtags**    | Instagram Basic Display / TikTok API | ⚠️ Limited                  | [Instagram](https://developers.facebook.com/) or [TikTok](https://developers.tiktok.com/) |
| **Visa Info**          | iVisaGuide API / Manual Database     | ⚠️ Premium                  | [iVisaGuide](https://www.ivisaguide.com/api) or Custom Database                           |

---

## 🚀 Quick Setup Steps

### Step 1: Add API Keys to `build.gradle.kts`

Open `app/build.gradle.kts` and update the `buildConfigField` section:

```kotlin
defaultConfig {
    // ... existing code ...
    
    buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"YOUR_GOOGLE_MAPS_API_KEY\"")
    buildConfigField("String", "GOOGLE_PLACES_API_KEY", "\"YOUR_GOOGLE_PLACES_API_KEY\"")
    buildConfigField("String", "UNSPLASH_ACCESS_KEY", "\"YOUR_UNSPLASH_ACCESS_KEY\"")
    buildConfigField("String", "OPENWEATHER_API_KEY", "\"YOUR_OPENWEATHER_API_KEY\"")
    buildConfigField("String", "GEMINI_API_KEY", "\"YOUR_GEMINI_API_KEY\"")
    
    manifestPlaceholders["MAPS_API_KEY"] = "YOUR_GOOGLE_MAPS_API_KEY"
}
```

### Step 2: Update `AndroidManifest.xml`

The Google Maps API key is already configured. Make sure it matches your key:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_GOOGLE_MAPS_API_KEY" />
```

**Note:** For Google Places API, you can use the same key as Google Maps API.

### Step 3: Update `DestinationApiService.kt`

Replace the mock implementations with actual API calls using the keys from `BuildConfig`.

---

## 📝 Detailed API Setup Instructions

### 1. Google Places API (for Search, Reviews, Attractions)

**Get API Key:**
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable "Places API" and "Maps SDK for Android"
4. Go to "Credentials" → "Create Credentials" → "API Key"
5. Restrict the key to "Places API" and "Maps SDK for Android" for security

**Add to code:**
- `build.gradle.kts`: `GOOGLE_PLACES_API_KEY`
- Use in `DestinationApiService.kt` for:
  - `searchDestinationsOnline()`
  - `fetchReviewsFromAPI()`
  - `fetchAttractions()`

**Free Tier:** $200 credit/month (≈ 28,000 requests)

---

### 2. Unsplash API (for Destination Images)

**Get API Key:**
1. Go to [Unsplash Developers](https://unsplash.com/developers)
2. Create an account
3. Create a new application
4. Copy your "Access Key"

**Add to code:**
- `build.gradle.kts`: `UNSPLASH_ACCESS_KEY`
- Use in `DestinationApiService.kt` for `getDestinationImageUrl()`

**Free Tier:** 50 requests/hour

**Example API Call:**
```kotlin
suspend fun getDestinationImageUrl(destinationName: String): String {
    val url = "https://api.unsplash.com/search/photos?query=$destinationName&client_id=${BuildConfig.UNSPLASH_ACCESS_KEY}"
    // Make HTTP request and return image URL
}
```

---

### 3. OpenWeather API (for Weather Data)

**Get API Key:**
1. Go to [OpenWeather](https://openweathermap.org/api)
2. Sign up for free account
3. Go to "API keys" section
4. Copy your API key

**Add to code:**
- `build.gradle.kts`: `OPENWEATHER_API_KEY`
- Use in `DestinationApiService.kt` for `fetchWeatherAndSeasonalInfo()`

**Free Tier:** 1,000 calls/day, 60 calls/minute

**Example API Call:**
```kotlin
suspend fun fetchWeatherAndSeasonalInfo(lat: Double, lng: Double): WeatherInfo {
    val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lng&appid=${BuildConfig.OPENWEATHER_API_KEY}&units=metric"
    // Make HTTP request and parse response
}
```

---

### 4. Google Gemini API (for AI Descriptions)

**Get API Key:**

1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Sign in with your Google account
3. Create a new API key
4. Copy the API key

**Add to code:**

- `build.gradle.kts`: `GEMINI_API_KEY`
- Use in `DestinationApiService.kt` for `generateAIDescription()`

**Free Tier:** Yes (generous free tier available)

---

### 5. Social Media APIs (Optional - for Hashtags)

**Instagram Basic Display API:**
1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Create an app
3. Get access token

**Note:** Social media APIs have strict limitations. Consider using mock data or a custom solution.

---

## 🔧 Implementation Example

Here's how to update `DestinationApiService.kt` to use real APIs:

```kotlin
import com.runanywhere.startup_hackathon20.BuildConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

object DestinationApiService {
    private val httpClient = HttpClient()
    
    suspend fun getDestinationImageUrl(destinationName: String): String = withContext(Dispatchers.IO) {
        try {
            val url = "https://api.unsplash.com/search/photos?query=$destinationName&client_id=${BuildConfig.UNSPLASH_ACCESS_KEY}&per_page=1"
            val response: UnsplashResponse = httpClient.get(url).body()
            response.results.firstOrNull()?.urls?.regular ?: ""
        } catch (e: Exception) {
            // Fallback to mock
            "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=600&fit=crop"
        }
    }
    
    suspend fun fetchWeatherAndSeasonalInfo(lat: Double, lng: Double): WeatherInfo = withContext(Dispatchers.IO) {
        try {
            val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lng&appid=${BuildConfig.OPENWEATHER_API_KEY}&units=metric"
            val response: WeatherResponse = httpClient.get(url).body()
            WeatherInfo(
                bestSeason = determineBestSeason(response.main.temp),
                avgTemp = "${response.main.temp.toInt()}°C",
                rainfallLevel = if (response.rain != null) "High" else "Moderate",
                currentTemp = response.main.temp,
                condition = response.weather.firstOrNull()?.main
            )
        } catch (e: Exception) {
            // Fallback to mock
            WeatherInfo("Spring & Autumn", "22°C", "Moderate")
        }
    }
    
    suspend fun generateAIDescription(destination: String, country: String): String = withContext(Dispatchers.IO) {
        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=${BuildConfig.GEMINI_API_KEY}"
            val response = httpClient.post(url) {
                header("Content-Type", "application/json")
                setBody(mapOf(
                    "contents" to listOf(mapOf(
                        "parts" to listOf(mapOf(
                            "text" to "Write a comprehensive but concise (3-4 sentences) travel guide description for $destination, $country."
                        ))
                    ))
                ))
            }
            // Parse response and return description
            // ...
        } catch (e: Exception) {
            // Fallback to mock
            generateMockDescription(destination, country)
        }
    }
}
```

---

## 🔒 Security Best Practices

1. **Never commit API keys to Git:**
   - Add `local.properties` to `.gitignore`
   - Store keys in `local.properties`:
     ```properties
     GOOGLE_PLACES_API_KEY=your_key_here
     UNSPLASH_ACCESS_KEY=your_key_here
     ```

2. **Use BuildConfig for keys:**
   - Keys are compiled into the app but not in source code
   - Consider using Android Keystore for production

3. **Restrict API keys:**
   - Google Cloud: Restrict by API and Android app package
   - Unsplash: Set rate limits
   - Gemini: Set usage limits

---

## 📦 Dependencies Already Added

The following dependencies are already in `build.gradle.kts`:
- ✅ `io.ktor:ktor-client-*` - For HTTP requests
- ✅ `com.squareup.retrofit2:retrofit` - Alternative HTTP client
- ✅ `com.squareup.okhttp3:okhttp` - HTTP client
- ✅ `io.coil-kt:coil-compose` - Image loading

---

## 🎯 Priority Order (What to Implement First)

1. **High Priority:**
   - ✅ Google Places API (Search, Reviews, Attractions)
   - ✅ Unsplash API (Images)

2. **Medium Priority:**
   - ✅ OpenWeather API (Weather)
   - ✅ Google Gemini API (AI Descriptions)

3. **Low Priority (Optional):**
   - Social Media APIs (Hashtags)
   - Visa API (Can use manual database)

---

## 🧪 Testing Without API Keys

The app currently works with **mock data** - all features function without API keys. You can:
- Test all UI features
- See how everything works
- Add API keys gradually as needed

---

## 📞 Need Help?

- **Google Places API:** [Documentation](https://developers.google.com/maps/documentation/places)
- **Unsplash API:** [Documentation](https://unsplash.com/documentation)
- **OpenWeather API:** [Documentation](https://openweathermap.org/api)
- **Google Gemini API:** [Documentation](https://ai.google.dev/docs)

---

**Note:** All API services have free tiers that should be sufficient for development and testing. Upgrade to paid plans only when you need higher limits.

