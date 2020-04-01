package com.matthiasbruns.flutter_foursquare_pilgrimsdk

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.foursquare.pilgrim.*

data class PilgrimConfig(
        internal val clientId: String,
        internal val clientSecret: String
)

class PilgrimWrapper private constructor(
        private val context: Context,
        private val config: PilgrimConfig) {

    companion object {
        private const val REQUEST_LOCATION = 123
        private lateinit var _instance: PilgrimWrapper

        val instance
            get() = _instance

        fun init(context: Context, config: PilgrimConfig): PilgrimWrapper {
            _instance = PilgrimWrapper(context, config)
            return _instance
        }
    }

    private val pilgrimNotificationHandler = object : PilgrimNotificationHandler() {
        // Primary visit handler
        override fun handleVisit(context: Context, notification: PilgrimSdkVisitNotification) {
            val visit = notification.visit
            val venue = visit.venue
            Log.d("PilgrimSdk", visit.toString())
        }

        // Optional: If visit occurred while in Doze mode or without network connectivity
        override fun handleBackfillVisit(context: Context, notification: PilgrimSdkBackfillNotification) {
            val visit = notification.visit
            val venue = visit.venue
            Log.d("PilgrimSdk", visit.toString())
        }

        // Optional: If visit occurred by triggering a geofence
        override fun handleGeofenceEventNotification(context: Context, notification: PilgrimSdkGeofenceEventNotification) {
            super.handleGeofenceEventNotification(context, notification)
            // Process the geofence events however you'd like. Here we loop through the potentially multiple geofence events and handle them individually:
            notification.geofenceEvents.forEach { geofenceEvent ->
                Log.d("PilgrimSdk", geofenceEvent.toString())
            }
        }
    }

    init {
        PilgrimSdk.with(
                PilgrimSdk.Builder(context)
                        .consumer(config.clientId, config.clientSecret)
                        .notificationHandler(pilgrimNotificationHandler)
                        .logLevel(LogLevel.DEBUG)
        )
    }


    fun start(activity: Activity) {
        requestLocation(activity)
//        PilgrimSdk.start(context.applicationContext)
    }

    private fun requestLocation(activity: Activity) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||

                ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||

                ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,

                            android.Manifest.permission.ACCESS_COARSE_LOCATION,

                            android.Manifest.permission.BLUETOOTH,

                            android.Manifest.permission.BLUETOOTH_ADMIN
                    ), REQUEST_LOCATION)

        } else {
            Log.d("PilgrimSdk", "Location permissions available, starting location");
            // LocationManager.enableLocationSupport(activity.applicationContext)

        }
    }

    fun getCurrentLocation(context: Context, callback: (result: Result<CurrentLocation, Exception>?, error: Exception?) -> Unit) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Thread(Runnable {
                val currentLocationResult: Result<CurrentLocation, Exception> = PilgrimSdk.get().currentLocation
                callback(currentLocationResult, null)
            }).start()

        } else {
            callback(null, Exception("ACCESS_FINE_LOCATION is missing"))

        }
    }
}
