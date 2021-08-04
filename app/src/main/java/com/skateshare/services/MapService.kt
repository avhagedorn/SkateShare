// Credit to Philipp Lackner for much of the code in this class.
// Modifications were made to suit my needs, but the core logic is his.
// Source: https://www.youtube.com/playlist?list=PLQkwcJG4YTCQ6emtoqSZS2FVwZR9FT3BV

package com.skateshare.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.skateshare.R
import com.skateshare.db.LocalRoutesDao
import com.skateshare.misc.TrackerUtil.hasLocationPermissions
import com.skateshare.models.Route
import com.skateshare.services.MapHelper.formatTime
import com.skateshare.services.MapHelper.metersToStandardUnits
import com.skateshare.views.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>

@AndroidEntryPoint
class MapService : LifecycleService() {

    @Inject lateinit var pendingIntent: PendingIntent
    @Inject lateinit var notificationBuilder: NotificationCompat.Builder
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
        val elevationData = MutableLiveData<MutableList<Double>>()      // Altitude in m
        val isTracking = MutableLiveData<Boolean>()
        val errorMessage = MutableLiveData<String?>()
    }

    private val locationCallback = object: LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            if (isTracking.value!!) {
                result.locations.let { locations ->
                    for (location in locations) {
                        addDistance(location)
                        addLocation(location)
                        addAltitude(location.altitude)
                        addSpeed(location.speed)
                    }
                }
            }
            super.onLocationResult(result)
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, Observer { trackingStatus ->
            updateLocationTracking(trackingStatus)
        })
    }

    private fun initializeLiveData() {
        routeData.postValue(mutableListOf<LatLng>())
        speedData.postValue(mutableListOf<Float>())
        elevationData.postValue(mutableListOf<Double>())
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

        elapsedSeconds.observe(this, Observer { time ->
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
        notificationManager.cancel(NOTIFICATION_ID)

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
            stopForeground(false)
        }
    }

    private suspend fun saveRoute() {
        localRoutesDao.deleteALl() // TODO: THIS IS TEMPORARY FOR TESTING, REMOVE THIS SOON
        Log.i("1one", localRoutesDao.routesByDate(10, 0).first().lat_path.size.toString())
        Log.i("1one", localRoutesDao.routesByDate(10, 0).first().lat_path.toString())

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val startTime = System.currentTimeMillis()

        val lats = mutableListOf<Double>()
        val lngs = mutableListOf<Double>()
        updateNotification(notificationManager, R.string.converting_coordinates, 15)

        routeData.value!!.forEach {
            lats.add(it.latitude)
            lngs.add(it.longitude)
        }

        val coordTime = System.currentTimeMillis()

        updateNotification(notificationManager, R.string.smoothing_route, 45)
        val newLats = bSpline(lats)
        val newLngs = bSpline(lngs)
        lats.clear()
        lngs.clear()

        val smoothingTime = System.currentTimeMillis()

        updateNotification(notificationManager, R.string.saving_to_database, 75)
        insertRoute(newLats, newLngs)

        val insertTime = System.currentTimeMillis()

        val postedRoute = localRoutesDao.routesByDate(10, 0).first()

        Log.i("1one", "Coordinate conversion time: ${coordTime - startTime}ms")
        Log.i("1one", "B-Spline smoothing time: ${smoothingTime - startTime}ms")
        Log.i("1one", "Database insertion time: ${insertTime - startTime}ms")
        Log.i("1one", "Total time: ${System.currentTimeMillis() - startTime}ms")

        Log.i("1one", postedRoute.toString())
        Log.i("1one", postedRoute.speed.size.toString())
    }

    private fun updateNotification(manager: NotificationManager, messageId: Int, progress: Int) {
        val notification = notificationBuilder
            .setContentText(getString(messageId))
            .setProgress(100, progress, false)
        manager.notify(NOTIFICATION_ID, notification.build())
    }

    private suspend fun insertRoute(lats: MutableList<Double>, lngs: MutableList<Double>) {
        val distances = metersToStandardUnits(distanceMeters.value!!)
        localRoutesDao.insert(
            Route(
                time_start = startTime,
                duration = System.currentTimeMillis() - startTime,
                length_km = distances[UNIT_KILOMETERS]!!,
                length_mi = distances[UNIT_MILES]!!,
                altitude = elevationData.value!!,
                speed = speedData.value!!,
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

    private fun addAltitude(altitude: Double) {
        elevationData.value?.apply {
            add(altitude)
            elevationData.postValue(this)
        }
    }

    private fun addSpeed(speed: Float) {
        speedData.value?.apply {
            add(speed)
            speedData.postValue(this)
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
}