package com.harryliu.carlie.services.dataServices;

import com.google.firebase.database.DatabaseReference
import com.harryliu.carlie.firebaseModels.RealTimeValue
import com.harryliu.carlie.firebaseModels.TripModel

/**
 * @author Harry Liu
 * @version Feb 16, 2018
 */

object TripService {

    var mCurrentTrip: TripModel? = null

    var mTripList: ArrayList<TripModel>? = null

    private val mTripsRef = RealTimeDatabaseService.getRootRef().child("trips")

    fun getTripsRef():  DatabaseReference {
        return mTripsRef
    }

}
