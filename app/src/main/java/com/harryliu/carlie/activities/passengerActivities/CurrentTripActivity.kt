package com.harryliu.carlie.activities.passengerActivities

import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.harryliu.carlie.R.layout.activity_current_trip
import com.harryliu.carlie.firebaseModels.LocationModel
import com.harryliu.carlie.firebaseModels.RealTimeValue
import com.harryliu.carlie.firebaseModels.TripModel
import com.harryliu.carlie.services.dataServices.TripService

/**
 * @author Haofan Zhang
 */
class CurrentTripActivity : AppCompatActivity() {
    var distance: Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_current_trip)

        val currentTrip: TripModel = TripService.mCurrentTrip!!

        val shuttleLocation = RealTimeValue(LocationModel())
        val pickupLocation = currentTrip.pickupLocation

        Log.d("CurrentTripActivity", currentTrip.shuttleId)

        val currentLocRefs = listOf("/shuttles/${currentTrip.shuttleId}/location/")

        shuttleLocation.onChange.subscribe { newLocation ->
            if(newLocation.latitude != null && newLocation.longitude != null) {
                val sl = Location("shuttle")
                sl.latitude = newLocation.latitude!!
                sl.longitude = newLocation.longitude!!
                val pl = Location("pickup")
                pl.latitude = pickupLocation!!.latitude!!
                pl.longitude = pickupLocation.longitude!!
                distance = sl.distanceTo(pl)
                Log.d("CurrentTripActivity", "$distance")
            }
        }

        shuttleLocation.startSync(currentLocRefs)

    }


}