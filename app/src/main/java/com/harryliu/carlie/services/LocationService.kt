package com.harryliu.carlie.services

import android.content.Context
import android.location.Location
import com.mapbox.services.android.telemetry.location.LocationEngineListener
import com.mapbox.services.android.telemetry.location.LocationEnginePriority
import com.mapbox.services.android.telemetry.location.LostLocationEngine

/**
 * @author Harry Liu
 *
 * @version Feb 16, 2018
 */

object LocationService {

    fun requestLocationUpdates(context: Context, onLocationChange: (location: Location) -> Unit): LostLocationEngine {
        val locationEngine = LostLocationEngine(context)
        locationEngine.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine.interval = 200

        locationEngine.addLocationEngineListener(object : LocationEngineListener {
            override fun onLocationChanged(location: Location) {
                onLocationChange(location)
            }

            override fun onConnected() {

                try {
                    locationEngine.requestLocationUpdates()
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
        })
        locationEngine.activate()
        return locationEngine
    }
}
