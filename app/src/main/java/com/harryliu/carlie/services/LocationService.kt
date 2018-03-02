package com.harryliu.carlie.services

import android.content.Context
import android.location.Location
import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.location.LocationEngineListener
import com.mapbox.services.android.telemetry.location.LocationEnginePriority
import com.mapbox.services.android.telemetry.location.LostLocationEngine


/**
 * @author Harry Liu
 *
 * @version Feb 16, 2018
 */

object LocationService {

    fun requestLocationUpdates(context: Context, interval: Int = 200, onLocationChange: (
            location: Location,
            locationEngine: LocationEngine,
            locationEngineListener: LocationEngineListener) -> Unit): LocationEngine {
        val locationEngine = LostLocationEngine(context)
        locationEngine.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine.interval = interval

        locationEngine.addLocationEngineListener(object : LocationEngineListener {
            override fun onLocationChanged(location: Location) {
                onLocationChange(location, locationEngine, this)
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
