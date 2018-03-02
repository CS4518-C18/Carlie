package com.harryliu.carlie.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest


/**
 * @author Haofan Zhang
 */
class GeofenceManager (context: Context) {

    companion object {
        //val mGeofenceList: HashMap<String, Geofence> = HashMap()
        val mEnterGeofenceCallbackList: HashMap<String, (String) -> Unit> = HashMap()
        val mExitGeofenceCallbackList: HashMap<String, (String) -> Unit> = HashMap()
    }

    private val GEOFENCE_RADIUS = 1000f
    private val GEOFENCE_TIMEOUT = Geofence.NEVER_EXPIRE

    private var mContext: Context = context
    private val mGeofencingClient: GeofencingClient = LocationServices.getGeofencingClient(mContext)


    fun addGeofence(id: String, lat: Double, lng: Double,
                    enterCallback: (String) -> Unit, exitCallback: (String) -> Unit){
        val newGeofence = Geofence.Builder()
                .setRequestId(id)
                .setExpirationDuration(GEOFENCE_TIMEOUT)
                .setCircularRegion(lat, lng, GEOFENCE_RADIUS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        //mGeofenceList[id] = newGeofence
        mEnterGeofenceCallbackList[id] = enterCallback
        mExitGeofenceCallbackList[id] = exitCallback
        try {
            mGeofencingClient.addGeofences(getGeofencingRequest(newGeofence), getGeofencePendingIntent())
                    .addOnSuccessListener { println("add geofence success") }
                    .addOnFailureListener { println("add geofence fail") }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }


    fun removeGeofence(id: String) {
        mGeofencingClient.removeGeofences(listOf(id))
                .addOnSuccessListener { println("remove geofence success") }
                .addOnFailureListener { println("remove geofence fail") }
        mEnterGeofenceCallbackList.remove(id)
        mExitGeofenceCallbackList.remove(id)
    }


    /***
     * Creates a GeofencingRequest object using the mGeofenceList ArrayList of Geofences
     * Used by `#registerGeofences`
     *
     * @return the GeofencingRequest object
     */
    private fun getGeofencingRequest(geofence: Geofence): GeofencingRequest {
        val builder = GeofencingRequest.Builder()
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        builder.addGeofence(geofence)
        return builder.build()
    }

    private fun getGeofencePendingIntent(): PendingIntent {
        val intent = Intent(mContext, GeofenceTransitionsIntentService::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        val newGeofencePendingIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return newGeofencePendingIntent
    }
}