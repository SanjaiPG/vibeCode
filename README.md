# Travel Planner App - UI Documentation

## 🌍 Overview

A beautiful, modern Android travel planning application built with Jetpack Compose and Material
Design 3. This app helps users discover destinations, plan trips with AI assistance, and manage
their travel wishlists.

---

## 📱 App Structure - 7 Pages

### **Page 1: Login Screen** (`LoginScreen.kt`)

- **Purpose**: User authentication
- **Features**:
    - Email and password input fields
    - Toggle between Login and Register modes
    - Modern gradient background
    - Password visibility toggle
    - Beautiful card-based layout

---

### **Page 2: Home Screen** (`HomeScreen.kt`)

- **Purpose**: Main landing page after login
- **Features**:
    - **User Profile Section**:
        - Welcome message "Hello, Traveler! 👋"
        - Profile avatar in top-right corner
    - **Search Bar**: Search for destinations
    - **Map Preview**: Visual map display at the top
    - **Famous Destinations Cards**:
        - Scrollable list of popular destinations
        - Each card shows: Name, Country, Currency
        - Click to view destination details
- **Bottom Navigation**: Home (active), Wishlist, Cart, Chatbot

---

### **Page 3: Search Results Screen** (`SearchResultsScreen.kt`)

- **Purpose**: Display filtered search results
- **Features**:
    - Back button to return to home
    - Search query display
    - Results count
    - List of matching destinations
    - Empty state when no results found
    - Each result shows: Image placeholder, Name, Location, Currency

---

### **Page 4: Destination Details Screen** (`DestinationScreen.kt`)

- **Purpose**: Detailed view of a specific destination
- **Features**:
    - **Hero Image Section**:
        - Large gradient background with destination name
        - Location info
    - **Rating & Info Card**:
        - Star rating (4.8/5.0)
        - Review count (2,453 reviews)
        - Currency information
    - **About Section**: Description of the destination
    - **Popular Reviews**:
        - User avatars
        - 5-star ratings
        - Review text
    - **"Make a Plan" Button**: Large, prominent CTA button
- **Bottom Navigation**: Visible on all pages except login

---

### **Page 5: Make a Plan Screen** (`MakePlanScreen.kt`)

- **Purpose**: Form to create a travel itinerary
- **Form Fields**:
    - **From**: Starting location (manual entry)
    - **To**: Destination (auto-filled from selected destination)
    - **Start Date**: Date picker input
    - **Nights**: Number of nights
    - **People**: Number of travelers
    - **Budget**: Budget in ₹
    - **Food Category**: Dropdown (Veg / Non-Veg / Both)
    - **Mode of Transport**: Dropdown (Flight ✈️ / Train 🚂 / Bus 🚌 / Car 🚗 / Bike 🏍️)
- **Generate Button**: Creates AI-powered itinerary
- **Validation**: All required fields must be filled

---

### **Page 6: Chat Screen** (`MainActivity.kt` - ChatScreen)

- **Purpose**: AI chatbot for travel queries and plan confirmation
- **Features**:
    - **AI Model Management**:
        - Download and load LLM models
        - Model selector interface
        - Progress indicators
    - **Chat Interface**:
        - Message bubbles (User vs AI)
        - Send message input field
        - Auto-scroll to latest message
    - **Plan Confirmation**:
        - AI asks for confirmation
        - User can request changes
        - Link to edit form (Page 5)
        - Upon "Yes", redirects to Page 7
- **Bottom Navigation**: Visible

---

### **Page 7: Plan Result Screen** (`PlanResultScreen.kt`)

- **Purpose**: Display final AI-generated travel plan
- **Features**:
    - **Success Header**:
        - Checkmark icon ✓
        - "Your Itinerary is Ready!" message
        - Plan title
    - **Itinerary Display**:
        - Full markdown-formatted travel plan
        - Day-by-day breakdown
    - **Export Button**:
        - Large, prominent button
        - Dialog with export options:
            - 📄 Export as PDF
            - 📝 Export as Text File
    - **Action Buttons**:
        - Share button
        - Save to Cart button
- **Bottom Navigation**: Visible

---

## 🎨 Bottom Navigation (4 Tabs)

Present on all pages **except Login Screen**:

1. **🏠 Home** - Main dashboard with destinations
2. **❤️ Wishlist** - Saved favorite destinations
3. **🛒 Cart** - Saved travel plans
4. **💬 Chatbot** - AI assistant for travel help

---

## 🎯 Key Features

### Design Highlights

- ✨ Modern Material Design 3
- 🎨 Gradient backgrounds and cards
- 🌈 Consistent color scheme
- 📱 Responsive layouts
- 🎭 Beautiful animations and transitions
- 🔤 Clean typography

### User Experience

- Smooth navigation flow
- Auto-filled destination in planning form
- Empty states with helpful messages
- Loading indicators
- Form validation
- Profile management

### AI Integration

- On-device LLM models
- Download and load AI models
- Generate personalized itineraries
- Interactive chat for modifications
- Plan confirmation workflow

---

## 📂 File Structure

```
app/src/main/java/com/runanywhere/
├── ui/screens/
│   ├── LoginScreen.kt          # Page 1: Login
│   ├── HomeScreen.kt            # Page 2: Home with search & destinations
│   ├── SearchResultsScreen.kt  # Page 3: Search results listing
│   ├── DestinationScreen.kt    # Page 4: Destination details
│   ├── MakePlanScreen.kt       # Page 5: Plan creation form
│   ├── PlanResultScreen.kt     # Page 7: Final plan with export
│   └── LikedScreens.kt         # Wishlist & Cart screens
├── ui/
│   └── AppRoot.kt              # Main navigation controller
├── ui/navigation/
│   └── AppNav.kt               # Route definitions & bottom nav items
└── startup_hackathon20/
    └── MainActivity.kt          # Page 6: Chat screen
```

---

## 🚀 Navigation Flow

```
Login Screen (Page 1)
    ↓
Home Screen (Page 2)
    ↓
[Search] → Search Results (Page 3)
    ↓
[Click Card] → Destination Details (Page 4)
    ↓
[Make a Plan] → Plan Form (Page 5)
    ↓
[Generate] → Chat/Confirmation (Page 6)
    ↓
[Confirm Yes] → Final Plan (Page 7)
    ↓
[Export] → PDF/TXT Download
```

---

## 🎨 Color Scheme

- **Primary**: Travel theme blue/gradient
- **Secondary**: Accent colors for highlights
- **Tertiary**: Additional accent colors
- **Surface**: Card backgrounds
- **Background**: Screen backgrounds
- **Containers**: Various shades for sections

---

## 📝 Notes

- **Backend**: UI only - no backend logic modified
- **Data**: Uses mock data from `TravelRepository.kt`
- **AI Models**: RunAnywhere SDK with Llama.cpp
- **Export**: PDF/TXT export functions marked as TODO for implementation

---

## 🛠️ Technologies Used

- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material Design 3** - Design system
- **Compose Navigation** - Screen routing
- **StateFlow** - State management
- **ViewModels** - Architecture components

---

Built with ❤️ for travelers around the world! ✈️🌍