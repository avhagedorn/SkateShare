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

### Screenshots ###
<img src="https://github.com/avhagedorn/SkateShare/assets/66842958/d1f6ecd6-8a44-487b-9783-b5203cdea0c0" width=250></img>
<img src="https://github.com/avhagedorn/SkateShare/assets/66842958/c98d02f3-3df3-4c0c-8b47-c142a3eb94a6" width=250></img>
<img src="https://github.com/avhagedorn/SkateShare/assets/66842958/08e90a9e-47d1-4ab6-9511-228236f1020f" width=250></img>
<img src="https://github.com/avhagedorn/SkateShare/assets/66842958/0fca10c5-4591-49d5-b357-ce2e9962d810" width=250></img>
<img src="https://github.com/avhagedorn/SkateShare/assets/66842958/2de183ce-9c54-4cb5-9fc6-a26c7697baee" width=250></img>


### Future Plans ###

1. Add VESC BLE support to log raw ESC data.

2. Add "like" support to posts.
