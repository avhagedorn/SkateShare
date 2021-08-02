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
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.skateshare.R
import com.skateshare.misc.TrackerUtil.hasLocationPermissions
import com.skateshare.views.profile.ProfileActivity
import java.util.*

typealias Polyline = MutableList<LatLng>

class MapService : LifecycleService() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        private val routeData = MutableLiveData<Polyline>()
        private val speedData = MutableLiveData<Float>()    // Speed in m/s
        private val timeData = MutableLiveData<Long>()      // Time in ms since start
        private val isTracking = MutableLiveData<Boolean>()
    }

    private val locationCallback = object: LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            if (isTracking.value!!) {
                result.locations.let { locations ->
                    for (location in locations) {
                        addLocation(location)
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
        isTracking.postValue(true)
    }

    // Handles communication with fragment intents
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            BEGIN_TRACKING -> {
                startForegroundService()
            }
            STOP_TRACKING -> {
                Log.i("1one", "Tracking stopped!")
                isTracking.postValue(false)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    // Updates tracking status and creates route recording notification
    private fun startForegroundService() {
        initializeLiveData()
        isTracking.postValue(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_baseline_add_24) // TODO: Add custom icon
            .setContentTitle("SkateShare")
            .setContentText("00:00:00")
            .setContentIntent(getActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
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

    // Sends the landing activity an intent to navigate to the route recording fragment
    private fun getActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, ProfileActivity::class.java).also {
            it.action = SHOW_RECORD_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}