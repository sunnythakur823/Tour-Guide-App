# Project Setup Instructions

Follow these steps to set up and run the Tour Guide App in Android Studio.

## Prerequisites
1.  **Android Studio**: Download and install the latest version.
2.  **Google Account**: Required for generating a Google Maps API key.

## Installation Steps
1.  **Open the Project**: Launch Android Studio and select "Open" to navigate to the `TourGuideApp` root folder.
2.  **Gradle Sync**: Wait for the project to sync. This will automatically download all required dependencies (Room, Glide, Google Maps, etc.).
3.  **Google Maps API Key**:
    - Go to the [Google Cloud Console](https://console.cloud.google.com/).
    - Create a new project or select an existing one.
    - Enable the **Maps SDK for Android**.
    - Go to **Credentials** and click "Create Credentials" -> "API Key".
    - Copy the generated API Key.
4.  **Configure the API Key**:
    - Open `app/src/main/AndroidManifest.xml`.
    - Find the `<meta-data>` tag with `android:name="com.google.android.geo.API_KEY"`.
    - Replace `YOUR_API_KEY_HERE` with your actual API key.
5.  **Add Images**:
    - Download or provide images for the tourist spots.
    - Rename them to: `rock_garden.jpg`, `tagore_hill.jpg`, `jagannath_temple.jpg`, `hundru_falls.jpg`, `birsa_zoo.jpg`, `patratu_valley.jpg`, `ranchi_lake.jpg`, and `sun_temple.jpg`.
    - Place these images in the `app/src/main/res/drawable/` directory.
    - *Note: The app is currently configured to load these by resource name. If images are missing, a placeholder will be shown.*
6.  **Run the App**: Click the "Run" button (green play icon) in Android Studio to install the app on an Emulator or a physical Android device.

## Troubleshooting
- **Database Issues**: If you change the database schema, I have enabled `fallbackToDestructiveMigration()` which will reset the database.
- **Map not loading**: Ensure the API key is correct and has "Maps SDK for Android" enabled in the Google Cloud Console.
- **SSL Handshake Error**: If images don't load from URLs, ensure your device/emulator date and time are correct.
