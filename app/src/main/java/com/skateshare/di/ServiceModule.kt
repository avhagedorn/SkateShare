package com.skateshare.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.skateshare.R
import com.skateshare.misc.CHANNEL_ID
import com.skateshare.misc.SHOW_RECORD_FRAGMENT
import com.skateshare.misc.STOP_TRACKING
import com.skateshare.misc.WARNING_CHANNEL_ID
import com.skateshare.services.MapService
import com.skateshare.views.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Named

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun getFusedLocationProviderClient(
        @ApplicationContext context: Context
    ) = FusedLocationProviderClient(context)

    @ServiceScoped
    @Provides
    fun getPendingIntent(
        @ApplicationContext context: Context,
    ): PendingIntent = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java).also {
            it.action = SHOW_RECORD_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    @ServiceScoped
    @Provides
    @Named("notificationBuilder")
    fun getOngoingNotification(
        @ApplicationContext context: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(context, CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_baseline_add_24)
        .setContentTitle(context.getString(R.string.app_name))
        .setContentText("00:00:00")
        .addAction(
            R.drawable.ic_baseline_stop_circle_24, context.getString(R.string.finish),
            PendingIntent.getService(
                context,
                1,
                Intent(context, MapService::class.java).also {
                    it.action = STOP_TRACKING
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        .setContentIntent(pendingIntent)

    @ServiceScoped
    @Provides
    @Named("warningBuilder")
    fun getWarningNotification(
        @ApplicationContext context: Context
    ) = NotificationCompat.Builder(context, WARNING_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_baseline_warning_24)
        .setContentTitle("SkateShare")
        .setStyle(NotificationCompat.BigTextStyle()
            .bigText(context.getString(R.string.gps_warning)))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(
            PendingIntent.getActivity(
                context,
                0,
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        .setAutoCancel(true)
}