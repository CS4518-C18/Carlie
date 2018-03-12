package com.harryliu.carlie.services

import android.content.Context
import com.harryliu.carlie.firebaseModels.LocationModel
import com.harryliu.carlie.firebaseModels.RealTimeValue

/**
 * @author Harry Liu
 * @version Mar 2, 2018
 */
object ShuttleService {
    fun startLocationUpdates(context: Context, shuttleLocationValue: RealTimeValue<LocationModel>) {
        val shuttleLocation = shuttleLocationValue.getValue()
        LocationService.requestLocationUpdates(context, 1000, { location, _, _ ->
            shuttleLocation.latitude = location.latitude
            shuttleLocation.longitude = location.longitude
        })
    }
}