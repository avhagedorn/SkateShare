# 🤙 SkateShare

An electric skateboarding app for users to record, share, and view others' routes!

### Built using ###

Kotlin, Firebase, Dagger, the Google Maps APK, Room, and ❤️.

### Features ###

* A global map of shared routes utilizing geohashing for querying.
* GPS recording to track your route, speed, and more.
* Detailed information about private routes, with graphs to visualize speeds.
* Reverse geocoding to pinpoint the location of the route when shared.
* An Instagram-esque feed of images, gifs, and routes powered by Firebase.
* MVVM architecture and dependency injection for ease of future development.

### Downloading and Running ###

1. Clone the project with the following:

        git clone https://github.com/avhagedorn/SkateShare.git
    
2. Make sure you have [Android Studio](https://developer.android.com/studio) installed.

3. Open the project using Android Studio. When prompted to sync Gradle files, press "Sync Now".

4. If needed, get a [Google Maps API Key](https://developers.google.com/maps). To use the API key, paste the following into local.properties:

        MAPS_API_KEY=[YOUR API KEY HERE]

5. Build and run the app using Android Studio.

### Future Plans ###

1. Add VESC BLE support to log raw ESC data.

2. Add "like" support to posts.
