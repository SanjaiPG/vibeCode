# GoQuest - AI-Powered Travel Planning Application

<div align="center">

![GoQuest](https://img.shields.io/badge/GoQuest-Travel%20Planner-blue?style=for-the-badge)
![Android](https://img.shields.io/badge/Android-24%2B-green?style=for-the-badge&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple?style=for-the-badge&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-blue?style=for-the-badge&logo=jetpackcompose)

**An intelligent travel companion that transforms how people plan and experience their journeys**

[Features](#key-features) • [Installation](#installation) • [Documentation](#documentation) • [License](#license)

</div>

---

---

- [Overview](#overview)
- [The Problem We Solve](#the-problem-we-solve)
- [Real-World Use Cases](#real-world-use-cases)
- [Key Features](#key-features)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
 
---

## Hackathon Submission

This project was developed as a hackathon submission for **VibeState '25** (Guru Gobind Singh Indraprastha University). For details about the hackathon, see:

https://unstop.com/hackathons/vibe-with-perplexity-guru-gobind-singh-indraprastha-university-ggsipu-delhi-1577987

---

## Team

- Sanjai P G 
- Sarvanthikha SR
- Sanjjiiev S
- Eshanaa Ajith K

---

## Overview

**GoQuest** is a next-generation Android travel planning application that leverages on-device AI to
create personalized travel itineraries. Built with modern Android development practices using
Jetpack Compose and Material Design 3, GoQuest combines beautiful UI/UX with powerful AI
capabilities to deliver an exceptional travel planning experience.

The app integrates **on-device Large Language Models (LLMs)** via the RunAnywhere SDK, ensuring
privacy-focused, offline-capable intelligent assistance without relying on cloud-based AI services.

### Why GoQuest?

Traditional travel planning is:

- **Time-consuming** - Hours spent researching destinations, accommodations, and activities
- **Fragmented** - Information scattered across multiple websites and apps
- **Expensive** - Paid travel agents and planning services
- **Generic** - One-size-fits-all recommendations that don't match personal preferences

**GoQuest solves these problems by providing:**

- AI-powered personalized itineraries in minutes
- Unified platform for discovery, planning, and management
- Free intelligent travel assistance
- Customized recommendations based on your preferences, budget, and travel style

---

## The Problem We Solve

### For Individual Travelers

**Challenge:** Planning a trip requires juggling multiple platforms—search engines, review sites,
booking platforms, and mapping tools. It's overwhelming and time-intensive.

**GoQuest Solution:** All-in-one platform that discovers destinations, generates AI-optimized
itineraries, provides local insights, and manages your travel plans in a single, beautiful
interface.

### For Budget-Conscious Travelers

**Challenge:** Staying within budget while maximizing experiences is difficult without expert
knowledge.

**GoQuest Solution:** AI algorithms consider your budget constraints and suggest optimal routes,
accommodations, and activities that provide the best value.

### For Adventure Seekers

**Challenge:** Finding authentic, off-the-beaten-path experiences beyond tourist traps.

**GoQuest Solution:** AI trained on diverse travel data suggests unique destinations and activities
tailored to your adventure preferences.

### For Spontaneous Travelers

**Challenge:** Last-minute planning often results in suboptimal choices or missed opportunities.

**GoQuest Solution:** Instant AI-generated itineraries with real-time optimization, even in offline
mode using on-device AI.

---

## Real-World Use Cases

### 1. **Family Vacation Planning**

> *Scenario:* A family of four wants to visit Japan for 10 days with a $5,000 budget, focusing on
> cultural experiences and kid-friendly activities.

**How GoQuest Helps:**

- Generates day-by-day itinerary with family-friendly destinations
- Balances cultural sites with entertainment for children
- Optimizes budget allocation across accommodation, food, and activities
- Provides transport recommendations (trains, buses) suitable for families

---

### 2. **Solo Backpacking Adventure**

> *Scenario:* A solo traveler wants to explore Southeast Asia for 30 days on a shoestring budget.

**How GoQuest Helps:**

- Creates flexible, budget-optimized routes across multiple countries
- Suggests hostels, local eateries, and free attractions
- Provides safety tips and solo-traveler-friendly recommendations
- Generates backup plans for spontaneous route changes

---

### 3. **Business Trip Extension**

> *Scenario:* A professional has a 3-day business conference in Barcelona and wants to extend the
> stay for 2 days of leisure.

**How GoQuest Helps:**

- Quick 2-day itinerary generation focused on efficiency
- Highlights must-see attractions within limited time
- Recommends restaurants and experiences near conference hotel
- Exports plan to PDF for easy reference during travel

---

### 4. **Honeymoon Planning**

> *Scenario:* A couple wants a romantic 14-day honeymoon in Europe with luxury accommodations and
> memorable experiences.

**How GoQuest Helps:**

- Curates romantic destinations and experiences
- Suggests high-end dining, scenic viewpoints, and couple activities
- Creates seamless multi-city itinerary with optimal travel times
- Provides detailed cost breakdown for premium experiences

---

### 5. **Educational Student Trip**

> *Scenario:* A history student wants to visit historical sites across India during summer break.

**How GoQuest Helps:**

- Plans educational routes connecting historical landmarks
- Provides context and background on destinations
- Optimizes for student-friendly budget accommodations and transport
- Suggests museums, heritage sites, and cultural experiences

---

## Key Features

### **Destination Discovery**

- Beautiful card-based interface showcasing popular destinations
- High-quality images and key information (currency, location, ratings)
- Interactive search with real-time filtering
- Curated "Famous Destinations" recommendations

### **AI-Powered Itinerary Generation**

- On-device LLM ensures privacy and offline capability
- Personalized plans based on:
    - Origin and destination
    - Travel dates and duration
    - Number of travelers
    - Budget constraints
    - Food preferences (Veg/Non-Veg/Both)
    - Transportation mode (Flight/Train/Bus/Car/Bike)
- Day-by-day breakdowns with timing, activities, and costs

### **Interactive AI Chatbot**

- Natural language conversation with AI travel assistant
- Modify and refine itineraries through chat
- Get instant answers to travel queries
- Context-aware responses based on your preferences

### **Plan Management**

- Save itineraries to Cart for later access
- Wishlist favorite destinations
- Export plans as PDF or text files
- Share itineraries with friends and family

### **Visual Exploration**

- Integrated Google Maps for destination visualization
- Location-based attraction discovery
- Interactive map previews on home screen

### **User Authentication**

- Firebase Authentication integration
- Email/password and Google Sign-In support
- Secure user profile management
- Cloud-synced preferences and saved plans

---

## Technology Stack

### **Frontend**

- **Kotlin** - Modern, concise, and safe programming language
- **Jetpack Compose** - Declarative UI toolkit for building native Android UI
- **Material Design 3** - Latest design system for beautiful, accessible interfaces

### **Backend & Services**

- **Firebase Authentication** - Secure user authentication
- **Cloud Firestore** - NoSQL cloud database for storing user data
- **Firebase Storage** - Cloud storage for user-uploaded content

### **AI & Machine Learning**

- **RunAnywhere SDK** - On-device AI inference framework
- **Llama.cpp** - Efficient LLM runtime with CPU optimization
- **On-device LLMs** - Privacy-focused, offline-capable AI models

### **APIs & Integrations**

- **Google Maps SDK** - Interactive maps and location services
- **Google Places API** - Destination data and place details
- **Unsplash API** - High-quality destination images
- **Coil** - Fast image loading and caching

---

## Getting Started

### **Prerequisites**

- **Android Studio** Iguana (2023.2.1) or later
- **JDK** 17 or higher
- **Android SDK** with API level 24+ (Android 7.0+)
- **Gradle** 8.13 or higher
- **Git** for version control

### **Installation**

#### 1. **Clone the Repository**

```bash
git clone https://github.com/SanjaiPG/GoQuest.git
cd GoQuest
```

#### 2. **Configure API Keys**

Create a `local.properties` file in the project root:

```properties
# Google Maps & Places
GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here
GOOGLE_PLACES_API_KEY=your_google_places_api_key_here

# Unsplash (for destination images)
UNSPLASH_ACCESS_KEY=your_unsplash_access_key_here
```

> **Note:** See [API Configuration](#api-configuration) section for detailed instructions on
> obtaining these keys.

#### 3. **Add Firebase Configuration**

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add an Android app with package name `com.runanywhere.startup_hackathon20`
3. Download `google-services.json`
4. Place it in `app/` directory

#### 4. **Download AI Models** (Optional for offline AI)

Place LLM model files (`.gguf` format) in:

```
app/src/main/assets/models/
```

#### 5. **Build and Run**

```click **Run** ▶️ in Android Studio.```

---

## Project Structure

```
GoQuest/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/runanywhere/
│   │   │   │   ├── startup_hackathon20/
│   │   │   │   │   ├── MainActivity.kt          # Entry point + Chat Screen
│   │   │   │   │   ├── ChatViewModel.kt         # AI chat logic
│   │   │   │   │   └── MyApplication.kt         # App initialization
│   │   │   │   ├── ui/
│   │   │   │   │   ├── AppRoot.kt              # Main navigation
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── LoginScreen.kt      # Authentication
│   │   │   │   │   │   ├── HomeScreen.kt       # Main dashboard
│   │   │   │   │   │   ├── SearchResultsScreen.kt
│   │   │   │   │   │   ├── DestinationScreen.kt
│   │   │   │   │   │   ├── MakePlanScreen.kt   # Itinerary form
│   │   │   │   │   │   ├── PlanResultScreen.kt # Final plan display
│   │   │   │   │   │   └── LikedScreens.kt     # Wishlist & Cart
│   │   │   │   │   ├── navigation/
│   │   │   │   │   │   └── AppNav.kt           # Routes & nav items
│   │   │   │   │   ├── components/             # Reusable UI components
│   │   │   │   │   └── theme/                  # Material 3 theming
│   │   │   │   └── data/
│   │   │   │       ├── Repository.kt           # Data management
│   │   │   │       ├── model/                  # Data models
│   │   │   │       └── api/                    # API services
│   │   │   ├── res/                            # Resources (layouts, drawables)
│   │   │   └── AndroidManifest.xml
│   ├── libs/                                    # RunAnywhere SDK AARs
│   │   ├── RunAnywhereKotlinSDK-release.aar
│   │   └── runanywhere-llm-llamacpp-release.aar
│   └── build.gradle.kts                         # App-level build config
├── gradle/
│   └── libs.versions.toml                       # Version catalog
├── build.gradle.kts                             # Project-level build config
├── settings.gradle.kts
├── local.properties                             # API keys (not in git)
├── README.md                                    # This file
└── LICENSE                                      # Apache 2.0 License
```

---

## API Configuration

### **1. Google Maps API**

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable **Maps SDK for Android** and **Places API**
4. Navigate to **APIs & Services → Credentials**
5. Create **API Key**
6. Restrict key to Android apps with your package name
7. Add to `local.properties`:
   ```properties
   GOOGLE_MAPS_API_KEY=AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
   GOOGLE_PLACES_API_KEY=AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
   ```

### **2. Unsplash API**

1. Sign up at [Unsplash Developers](https://unsplash.com/developers)
2. Create a new application
3. Copy **Access Key**
4. Add to `local.properties`:
   ```properties
   UNSPLASH_ACCESS_KEY=your_access_key_here
   ```

### **3. Firebase Setup**

1. Create project at [Firebase Console](https://console.firebase.google.com/)
2. Add Android app
3. Register package name: `com.runanywhere.startup_hackathon20`
4. Download `google-services.json` → place in `app/`
5. Enable **Authentication** (Email/Password & Google Sign-In)
6. Enable **Cloud Firestore** (Start in production mode)
7. Enable **Storage** (for profile pictures)

---

## App Screens

### **1. Login Screen**

- Email/password authentication
- Google Sign-In integration
- Register new user
- Beautiful gradient background

<div style="display: flex; gap: 10px;">
  <img src="assets/1.jpg" width="300" height="600" />
  <img src="assets/2.jpg" width="300" height="600" />
</div>

### **2. Home Screen**

- Welcome message with user profile
- Search bar for destinations
- Interactive Google Maps preview
- Famous destinations cards (scrollable)
- Bottom navigation (Home, Wishlist, Cart, Chatbot)

<div style="display: flex; gap: 10px;">
  <img src="assets/3.jpg" width="300" height="600" />
  <img src="assets/4.jpg" width="300" height="600" />
  <img src="assets/5.jpg" width="300" height="600" />
</div>

### **3. Search Results Screen**

- Filtered destination results
- Result count and search query display
- Empty state handling
- Click to view details

### **4. Destination Details Screen**

- Hero image with gradient overlay
- Rating and review stats
- Currency and location information
- "About" section with description
- Popular reviews section
- **"Make a Plan"** CTA button

<div style="display: flex; gap: 10px;">
  <img src="assets/7.jpg" width="300" height="600" />
  <img src="assets/8.jpg" width="300" height="600" />
</div>

### **5. Make a Plan Screen**

- Comprehensive travel planning form:
    - **From:** Starting location
    - **To:** Destination (auto-filled)
    - **Start Date:** Date picker
    - **Nights:** Duration selector
    - **People:** Number of travelers
    - **Budget:** Budget in rupees
    - **Food Category:** Veg/Non-Veg/Both dropdown
    - **Transport Mode:** Flight/Train/Bus/Car/Bike
- **Generate Itinerary** button
- Form validation

<img src="assets/9.jpg" width="300" height="600" />


### **6. Chat Screen (AI Assistant)**

- Welcome message and quick references
- AI model management:
    - Download models
    - Load/unload models
    - Model selector dialog
- Chat interface:
    - User and AI message bubbles
    - Typing indicators
    - Auto-scroll to latest message
- Plan confirmation workflow
- Travel plan display cards with:
    - Destination images
    - Trip details (dates, budget, travelers)
    - Itinerary text
    - Top attractions cards

<img src="assets/13.jpg" width="300" height="600" />


### **7. Plan Result Screen**

- Success header with checkmark
- "Your Itinerary is Ready!" message
- Full markdown-formatted plan
- Export options:
    - Export as PDF
    - Export as Text File
- Share button
- Save to Cart button

<div style="display: flex; gap: 10px;">
  <img src="assets/10.jpg" width="300" height="600" />
  <img src="assets/11.jpg" width="300" height="600" />
</div>

### **8. Wishlist Screen**

- Saved favorite destinations
- Remove from wishlist
- Quick access to destination details

<img src="assets/6.jpg" width="300" height="600" />

### **9. Cart Screen**

- Saved travel plans
- View/edit saved itineraries
- Delete plans

<img src="assets/12.jpg" width="300" height="600" />


---

## AI Integration

### **On-Device AI with RunAnywhere SDK**

GoQuest uses the **RunAnywhere SDK** for privacy-focused, offline-capable AI:

#### **Key Features:**

- ✅ **Privacy-First:** All AI processing happens on-device
- ✅ **Offline Capable:** Generate itineraries without internet
- ✅ **Low Latency:** No network round-trips for AI responses
- ✅ **Cost-Effective:** No API fees for AI inference
- ✅ **Multiple Models:** Support for various LLM sizes and capabilities

#### **Supported AI Models:**

| Model        | Size  | Speed | Quality | Use Case                                |
|--------------|-------|-------|---------|-----------------------------------------|
| Llama-3.2-1B | 1.2GB | ⚡⚡⚡   | ⭐⭐⭐     | Quick queries, fast responses           |
| Phi-3.5-mini | 2.3GB | ⚡⚡    | ⭐⭐⭐⭐    | Balanced performance                    |
| Gemma-2-2B   | 1.6GB | ⚡⚡    | ⭐⭐⭐⭐    | High-quality itineraries                |
| Llama-3-8B   | 4.7GB | ⚡     | ⭐⭐⭐⭐⭐   | Best quality (requires high-end device) |

#### **AI Capabilities:**

1. **Itinerary Generation**
    - Creates detailed day-by-day travel plans
    - Considers budget, preferences, and constraints
    - Optimizes routes and timing

2. **Conversational Refinement**
    - Natural language plan modifications
    - Answers travel questions
    - Provides local insights

3. **Context Awareness**
    - Remembers conversation history
    - Maintains plan context
    - Personalized recommendations

#### **Implementation Example:**

```kotlin
// Initialize RunAnywhere SDK
RunAnywhere.initialize(
    apiKey = "your_api_key",
    baseURL = "https://api.runanywhere.ai"
)

// Register LlamaCpp module
LlamaCppModule.register()

// Load AI model
val model = viewModel.loadModel("llama-3.2-1b-instruct")

// Generate itinerary
val prompt = """
    Create a travel itinerary from ${from} to ${to}
    Duration: ${nights} nights
    Budget: ₹${budget}
    Travelers: ${people}
    Preferences: ${foodCategory}, ${transport}
"""

val response = model.generate(prompt)
```

---