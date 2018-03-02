package com.harryliu.carlie.services

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent


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
            for (geofence in triggeringGeofences) {
                val id = geofence.requestId
                GeofenceManager.mEnterGeofenceCallbackList[geofence.requestId]?.invoke(id)
            }
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Exit geofence
            for (geofence in triggeringGeofences) {
                val id = geofence.requestId
                GeofenceManager.mExitGeofenceCallbackList[geofence.requestId]?.invoke(id)
            }
        } else {
            // Log the error.
        }
    }
}