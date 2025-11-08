package com.runanywhere.startup_hackathon20.data.api

import com.runanywhere.startup_hackathon20.BuildConfig

/**
 * Centralized API Keys Configuration
 * 
 * All API keys are stored in BuildConfig (from build.gradle.kts)
 * This object provides easy access to API keys throughout the app.
 * 
 * To add your API keys:
 * 1. Open app/build.gradle.kts
 * 2. Replace "YOUR_*_API_KEY" with your actual keys
 * 3. Rebuild the project
 */
object ApiKeys {
    // Google APIs
    val GOOGLE_MAPS_API_KEY: String = BuildConfig.GOOGLE_MAPS_API_KEY
    val GOOGLE_PLACES_API_KEY: String = BuildConfig.GOOGLE_PLACES_API_KEY
    
    // Image APIs
    val UNSPLASH_ACCESS_KEY: String = BuildConfig.UNSPLASH_ACCESS_KEY
    
    // AI APIs
    val OPENAI_API_KEY: String = BuildConfig.OPENAI_API_KEY
    
    /**
     * Check if API keys are configured (not placeholder values)
     */
    fun areKeysConfigured(): Boolean {
        return GOOGLE_MAPS_API_KEY.isNotBlank() &&
               GOOGLE_PLACES_API_KEY.isNotBlank() &&
               UNSPLASH_ACCESS_KEY.isNotBlank() &&
               OPENAI_API_KEY.isNotBlank()
    }
}

