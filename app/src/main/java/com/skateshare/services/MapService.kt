// Credit to Philipp Lackner for much of the code in this class.
// Modifications were made to suit my needs, but the core logic is his.
// Source: https://www.youtube.com/playlist?list=PLQkwcJG4YTCQ6emtoqSZS2FVwZR9FT3BV

package com.skateshare.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.*
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.skateshare.R
import com.skateshare.db.LocalRoutesDao
import com.skateshare.misc.*
import com.skateshare.misc.PermissionsUtil.hasLocationPermissions
import com.skateshare.models.ReverseGeocodeLocation
import com.skateshare.models.Route
import com.skateshare.repostitories.createReverseGeocoder
import com.skateshare.services.MapHelper.calculateAvgSpeed
import com.skateshare.services.MapHelper.formatTime
import com.skateshare.services.MapHelper.metersToStandardUnits
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

typealias Polyline = MutableList<LatLng>

@AndroidEntryPoint
class MapService : LifecycleService() {

    @Inject lateinit var pendingIntent: PendingIntent
    @Inject @Named("notificationBuilder") lateinit var notificationBuilder: NotificationCompat.Builder
    @Inject @Named("warningBuilder") lateinit var warningBuilder: NotificationCompat.Builder
    @Inject lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    @Inject lateinit var localRoutesDao: LocalRoutesDao

    private val elapsedSeconds = MutableLiveData<Long>()
    private var timerEnabled = false
    private var startTime = 0L
    private var prevSecondTime = 0L
    private lateinit var lastLocation: Location

    companion object {
        val distanceMeters = MutableLiveData<Double>()
        val elapsedMilliseconds = MutableLiveData<Long>()
        val routeData = MutableLiveData<Polyline>()
        val speedData = MutableLiveData<MutableList<Float>>()           // Speed in m/s
        val accuracyData = MutableLiveData<MutableList<Float>>()
        val isTracking = MutableLiveData<Boolean>()
        val errorMessage = MutableLiveData<String?>()
    }

    private val locationCallback = object: LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            if (isTracking.value!!) {
                result.locations.let { locations ->
                    for (location in locations) {
                        if (location.accuracy <= MAX_RADIUS_METERS) {
                            addDistance(location)
                            addLocation(location)
                            addAccuracy(location.accuracy)
                            addSpeed(location.speed)
                        }
                        Log.i("1one", location.accuracy.toString())
                    }
                }
            }
            super.onLocationResult(result)
        }

        override fun onLocationAvailability(p0: LocationAvailability) {
            super.onLocationAvailability(p0)
            if (!p0.isLocationAvailable) {
                Log.i("1one", "Disconnected!")
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                createWarningChannel(notificationManager)
                notificationManager.notify(WARNING_ID, warningBuilder.build())
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, { trackingStatus ->
            updateLocationTracking(trackingStatus)
        })
    }

    private fun initializeLiveData() {
        accuracyData.postValue(mutableListOf<Float>())
        routeData.postValue(mutableListOf<LatLng>())
        speedData.postValue(mutableListOf<Float>())
        isTracking.postValue(true)
        elapsedMilliseconds.postValue(0L)
        elapsedSeconds.postValue(0L)
        distanceMeters.postValue(0.0)
        errorMessage.postValue(null)
    }

    // Handles communication with fragment intents
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            BEGIN_TRACKING -> {
                startForegroundService()
                startTimer()
            }
            STOP_TRACKING -> {
                stopForegroundService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()
        timerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                val routeTime = System.currentTimeMillis() - startTime
                elapsedMilliseconds.postValue(routeTime)
                if (routeTime >= 1000L + prevSecondTime) {
                    elapsedSeconds.postValue(elapsedSeconds.value!! + 1)
                    prevSecondTime = routeTime
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
        }
    }

    // Updates tracking status and creates route recording notification
    private fun startForegroundService() {
        initializeLiveData()
        isTracking.postValue(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)
        startForeground(NOTIFICATION_ID, notificationBuilder.build())

        elapsedSeconds.observe(this, { time ->
            time?.let{
                val notification = notificationBuilder
                    .setContentText(formatTime(time * 1000L))
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        })
    }

    // Stops recording route
    private fun stopForegroundService() {
        isTracking.postValue(false)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = notificationBuilder
            .clearActions()
            .setContentText(getString(R.string.start_processing))
            .setProgress(100, 5, false)
        notificationManager.notify(NOTIFICATION_ID, notification.build())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                saveRoute()
            } catch (e: Exception) {
                Log.i("1one", e.message.toString())
            }
            stopForeground(true)
            notificationManager.cancelAll()
        }
    }

    private suspend fun saveRoute() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        updateNotification(notificationManager, R.string.converting_coordinates, 15)

        val lats = mutableListOf<Double>()
        val lngs = mutableListOf<Double>()
        routeData.value!!.forEach {
            lats.add(it.latitude)
            lngs.add(it.longitude)
        }

        updateNotification(notificationManager, R.string.smoothing_route, 45)
        val newLats = bSpline(lats)
        val newLngs = bSpline(lngs)
        lats.clear()
        lngs.clear()

        updateNotification(notificationManager, R.string.saving_to_database, 75)
        try {
            insertRoute(newLats, newLngs)
            updateAvgSpeed()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun updateAvgSpeed() {
        val kmh = localRoutesDao.getAvgSpeedKm()
        applicationContext.getSharedPreferences("userData", Context.MODE_PRIVATE).edit()
            .putFloat("avgSpeedKm", kmh)
            .putFloat("avgSpeedMi", kmh*0.6213712f)
            .apply()
    }

    private fun updateNotification(manager: NotificationManager, messageId: Int, progress: Int) {
        val notification = notificationBuilder
            .setContentText(getString(messageId))
            .setProgress(100, progress, false)
        manager.notify(NOTIFICATION_ID, notification.build())
    }

    private suspend fun insertRoute(lats: MutableList<Double>, lngs: MutableList<Double>) {
        val routeDuration = System.currentTimeMillis() - startTime
        val distances = metersToStandardUnits(distanceMeters.value!!)
        val speeds = calculateAvgSpeed(distanceMeters.value!!, routeDuration)

        localRoutesDao.insert(
            Route(
                time_start = startTime,
                duration = routeDuration,
                avg_speed_km = speeds[UNIT_KILOMETERS]!!,
                length_km = distances[UNIT_KILOMETERS]!!,
                avg_speed_mi = speeds[UNIT_MILES]!!,
                length_mi = distances[UNIT_MILES]!!,
                speed = speedData.value!!,
                accuracy = accuracyData.value!!,
                lat_start = lats[0],
                lng_start = lngs[0],
                lat_path = lats,
                lng_path = lngs
            )
        )
    }

    // Updates route recording status
    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (hasLocationPermissions(this)) {
                val request = LocationRequest().apply {
                    interval = LOGGING_INTERVAL
                    fastestInterval = FASTEST_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                    isWaitForAccurateLocation = true
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request, locationCallback, Looper.getMainLooper())
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    // Adds location to Polyline
    private fun addLocation(location: Location?) {
        location?.let { loc ->
            val newLatLng = LatLng(loc.latitude, loc.longitude)
            routeData.value?.apply {
                add(newLatLng)
                routeData.postValue(this)
            }
        }
    }

    private fun addSpeed(speed: Float) {
        speedData.value?.apply {
            add(speed)
            speedData.postValue(this)
        }
    }

    private fun addAccuracy(accuracy: Float) {
        accuracyData.value?.apply {
            add(accuracy)
            accuracyData.postValue(this)
        }
    }

    private fun addDistance(currentLocation: Location) {
        if (routeData.value!!.isNotEmpty())
            distanceMeters.postValue(
                distanceMeters.value!! + currentLocation.distanceTo(lastLocation))
        lastLocation = currentLocation
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun createWarningChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            WARNING_CHANNEL_ID,
            WARNING_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(channel)
    }
}