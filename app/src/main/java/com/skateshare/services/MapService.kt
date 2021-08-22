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
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.skateshare.R
import com.skateshare.db.LocalRoutesDao
import com.skateshare.misc.*
import com.skateshare.misc.PermissionsUtil.hasLocationPermissions
import com.skateshare.models.Route
import com.skateshare.services.MapHelper.calculateAvgSpeed
import com.skateshare.services.MapHelper.formatTime
import com.skateshare.services.MapHelper.metersToStandardUnits
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    private lateinit var notificationManager: NotificationManager
    private var _locationCallback: LocationCallback? = null
    private val locationCallback: LocationCallback get() = _locationCallback!!
    private var timerEnabled = false
    private var startTime = 0L
    private var prevSecondTime = 0L

    companion object {
        val warning = MutableLiveData<Int>()
        val distanceMeters = MutableLiveData<Double>()
        val elapsedSeconds = MutableLiveData<Long>()
        val routeData = MutableLiveData<Polyline>()
        val speedData = MutableLiveData<MutableList<Float>>()   // Speed in m/s
        val isTracking = MutableLiveData<Boolean>()
        val errorMessage = MutableLiveData<String?>()
    }

    init {
        _locationCallback = MyLocationCallback()
    }

    @SuppressLint("VisibleForTests")
    override fun onCreate() {
        super.onCreate()
        // fusedLocationProviderClient = FusedLocationProviderClient(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        warning.observe(this, { message ->
            when (message) {
                NO_GPS -> {
                    createWarningChannel(notificationManager)
                    notificationManager.notify(WARNING_ID, warningBuilder.build())
                    resetWarning()
                }
                HAS_GPS -> {
                    notificationManager.cancel(WARNING_ID)
                    resetWarning()
                }
            }
        })

        isTracking.observe(this, { trackingStatus ->
            updateLocationTracking(trackingStatus)
        })
    }

    private fun initializeLiveData() {
        routeData.postValue(mutableListOf<LatLng>())
        speedData.postValue(mutableListOf<Float>())
        isTracking.postValue(true)
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
            STOP_TRACKING -> { stopForegroundService() }
            RESET_ERROR_MESSAGE -> { resetErrorMessage() }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()
        timerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                if (System.currentTimeMillis() >= prevSecondTime+1000L) {
                    elapsedSeconds.postValue(
                        ((System.currentTimeMillis() - startTime)/1000).toLong())
                    prevSecondTime = System.currentTimeMillis()
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
        }
    }

    // Updates tracking status and creates route recording notification
    private fun startForegroundService() {
        initializeLiveData()
        isTracking.postValue(true)

        createNotificationChannel(notificationManager)
        startForeground(NOTIFICATION_ID, notificationBuilder.build())

        elapsedSeconds.observe(this, { time ->
            time?.let{
                val notification = notificationBuilder
                    .setContentText(formatTime(time))
                    .setProgress(0, 0, false)
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        })
    }

    // Stops recording route
    private fun stopForegroundService() {
        notificationManager.cancelAll()
        isTracking.postValue(false)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                saveRoute()
                stopForeground(true)
                stopSelf()
            } catch (e: Exception) {
                stopForeground(true)
                stopSelf()
            }
        }
    }

    private suspend fun saveRoute() {
        updateNotification(R.string.converting_coordinates, 15)

        val lats = mutableListOf<Double>()
        val lngs = mutableListOf<Double>()
        routeData.value!!.forEach {
            lats.add(it.latitude)
            lngs.add(it.longitude)
        }

        updateNotification(R.string.smoothing_route, 45)
        val newLats = bSpline(lats)
        val newLngs = bSpline(lngs)
        lats.clear()
        lngs.clear()

        updateNotification(R.string.saving_to_database, 75)
        try {
            insertRoute(newLats, newLngs)
        } catch (e: Exception) {
            errorMessage.postValue(e.message)
        }
        updateAvgSpeed()
    }

    private suspend fun updateAvgSpeed() {
        val kmh = localRoutesDao.getAvgSpeedKm()
        applicationContext.getSharedPreferences("userData", Context.MODE_PRIVATE).edit()
            .putFloat("avgSpeedKm", kmh)
            .putFloat("avgSpeedMi", kmh*0.6213712f)
            .apply()
    }

    private fun updateNotification(messageId: Int, progress: Int) {
        val notification = notificationBuilder
            .setContentText(getString(messageId))
            .setProgress(100, progress, false)
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    private fun resetWarning() {
        warning.postValue(-1)
    }

    private fun resetErrorMessage() {
        errorMessage.postValue(null)
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