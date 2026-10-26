# Tour Guide App - Ranchi, Jharkhand

A comprehensive Android application built for BCA Mobile Application Development project. This app helps users discover tourist attractions, food spots, petrol pumps, and stay options in Ranchi, Jharkhand.

## Features
- **Splash Screen**: Professional app entry with logo.
- **Dynamic List**: Explore categories with real-time search and filter chips.
- **Dual Filtering**: Filter by type (Tourist Spot, Food, Petrol, Stay) and sub-category (Nature, Historical, Picnic, etc.).
- **Place Details**: Full image, detailed description, ratings, and practical info (timings, fees).
- **Google Maps Integration**: View the location on a map with a marker and open it in the Google Maps app for navigation.
- **Favorites System**: Bookmark your favorite spots for quick access.
- **Offline Support**: Local persistence using Room Database.

## Tech Stack
- **Language**: Java
- **Database**: Room Persistence Library
- **UI Components**: RecyclerView, CardView, Material ChipGroups, BottomNavigationView
- **Libraries**: Glide (Image loading), Google Maps SDK
- **Architecture**: Model-View-Controller (MVC)

## Folder Structure
- `com.example.tourguideapp.models`: Data entities.
- `com.example.tourguideapp.database`: Room database configuration and DAOs.
- `com.example.tourguideapp.adapters`: RecyclerView adapters.
- `com.example.tourguideapp.root`: Activity classes for screens.

## Setup Instructions
1. **Clone/Open**: Open the project in Android Studio.
2. **Google Maps API Key**:
   - Go to [Google Cloud Console](https://console.cloud.google.com/).
   - Enable "Maps SDK for Android".
   - Create an API Key and restrict it if needed.
   - Open `app/src/main/AndroidManifest.xml`.
   - Replace `YOUR_API_KEY_HERE` with your actual key.
3. **Build & Run**: Sync Gradle and run on an emulator or physical device.

## Submission Details
- **Project Name**: Tour Guide App
- **Course**: BCA Mobile Application Development
- **City**: Ranchi, Jharkhand
