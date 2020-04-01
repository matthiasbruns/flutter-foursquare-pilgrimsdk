package com.matthiasbruns.flutter_foursquare_pilgrimsdk

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.annotation.NonNull
import com.foursquare.pilgrim.CurrentLocation
import io.flutter.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

/** FlutterFoursquarePilgrimsdkPlugin */
class FlutterFoursquarePilgrimsdkPlugin() : FlutterPlugin, MethodCallHandler, ActivityAware {

    private var context: Context? = null
    private var activity: Activity? = null

    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    companion object {
        private val plugin: FlutterFoursquarePilgrimsdkPlugin by lazy {
            FlutterFoursquarePilgrimsdkPlugin()
        }

        @JvmStatic
        fun registerWith(registrar: Registrar) {

            val channel = MethodChannel(registrar.messenger(), "flutter_foursquare_pilgrimsdk")

            channel.setMethodCallHandler(plugin)
        }
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        plugin.context = flutterPluginBinding.getApplicationContext()

        val channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "flutter_foursquare_pilgrimsdk")
        channel.setMethodCallHandler(plugin)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        try {
            when (call.method) {
                PilgrimSDKMethods.init -> initPilgrimSDK(call, result)
                PilgrimSDKMethods.start -> startPilgrimSDK(call, result)
                PilgrimSDKMethods.getCurrentLocation -> getCurrentLocation(result)

                else -> result.notImplemented()
            }
        } catch (e: Exception) {
            result.error(PilgrimSDKErrors.UNHANDLED_EXCEPTION, e.message, e)
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {

    }

    override fun onDetachedFromActivity() {
    }

    override fun onReattachedToActivityForConfigChanges(p0: ActivityPluginBinding) {
    }

    override fun onAttachedToActivity(p0: ActivityPluginBinding) {
        plugin.activity = p0.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {

    }

    private fun initPilgrimSDK(@NonNull call: MethodCall, @NonNull result: Result) {
        val clientId: String? = call.argument("clientId")
        val clientSecret: String? = call.argument("clientSecret")

        if (clientId == null) {
            result.error(PilgrimSDKErrors.MISSING_CONFIG, "clientId is null", null)
            return
        }
        if (clientSecret == null) {
            result.error(PilgrimSDKErrors.MISSING_CONFIG, "clientSecret is null", null)
            return
        }

        PilgrimWrapper.init(plugin.context!!, PilgrimConfig(
                clientId = clientId,
                clientSecret = clientSecret
        ))

        result.success(null)
    }

    private fun startPilgrimSDK(@NonNull call: MethodCall, @NonNull result: Result) {
        plugin.activity?.also {
            PilgrimWrapper.instance.start(it)
            result.success(null)
        } ?: run {
            result.error(PilgrimSDKErrors.ACTIVITY_NULL, "No Activity was found", null)
        }

    }

    private fun getCurrentLocation(@NonNull result: Result) {
        PilgrimWrapper.instance.getCurrentLocation(plugin.context!!) { currentLocationResult, error ->
            if (error != null) {
                result.error(PilgrimSDKErrors.CURRENT_LOCATION, error.message, null)
            }
            
            if (currentLocationResult != null && currentLocationResult.isOk) {
                val currentLocation: CurrentLocation = currentLocationResult.result
                Log.d("PilgrimSdk", "Currently at ${currentLocation.currentPlace} and inside ${currentLocation.matchedGeofences.size} geofence(s)")
                result.success("${currentLocation.currentPlace}")
            } else {
                val errorMessage = currentLocationResult?.err?.message
                        ?: "Unhandled error while CURRENT_LOCATION"

                Log.e("PilgrimSdk", errorMessage, currentLocationResult?.err ?: Exception())

                result.error(PilgrimSDKErrors.CURRENT_LOCATION, errorMessage, currentLocationResult?.err)
            }
        }
    }
}
