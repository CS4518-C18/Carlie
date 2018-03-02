package com.harryliu.carlie.services

import android.app.IntentService
import com.mapzen.android.lost.api.Geofence
import com.mapzen.android.lost.api.GeofencingEvent
import android.content.Intent
import android.util.Log


/**
 * @author Haofan Zhang
 */
class GeofenceTransitionsIntentService: IntentService("GeofenceTransitionsIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            println("intent error")
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition
        val triggeringGeofences = geofencingEvent.triggeringGeofences

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // Enter geofence
            println("entered something")
            for (geofence in triggeringGeofences) {
                val id = geofence.requestId
                println("exiting $id geofence")
                GeofenceManager.mEnterGeofenceCallbackList[geofence.requestId]?.invoke(id)
            }
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Exit geofence
            println("exited something")
            for (geofence in triggeringGeofences) {
                val id = geofence.requestId
                println("exiting $id geofence")
                GeofenceManager.mExitGeofenceCallbackList[geofence.requestId]?.invoke(id)
            }
        } else {
            // Log the error.
        }
    }
}