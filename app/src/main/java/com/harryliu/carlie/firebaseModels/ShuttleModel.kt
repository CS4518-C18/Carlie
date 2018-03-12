package com.harryliu.carlie.firebaseModels

import com.google.firebase.database.Exclude
import io.reactivex.subjects.PublishSubject

/**
 * @author Harry Liu
 *
 * @version Mar 1, 2018
 */

class ShuttleModel : FireBaseModel {

    var currentLocation: LocationModel? = null

    var trips = hashMapOf<String, TripModel>()


    @Exclude
    var onTripsMapChange = PublishSubject.create<ChildMapChanges<TripModel>>()

    @Exclude
    var mCurrentLocationValue: RealTimeValue<LocationModel>? = null

    @Exclude
    var mTrips = hashMapOf<String, RealTimeValue<TripModel>>()

    constructor() : super() {
        setCurrentLocationValue(RealTimeValue(LocationModel()))
    }

    private fun setCurrentLocationValue(currentLocationValue: RealTimeValue<LocationModel>) {
        mCurrentLocationValue = currentLocationValue
        currentLocation = currentLocationValue.getValue()
        updateProperty?.invoke("currentLocation", currentLocation!!)
        mCurrentLocationValue?.onChange!!
                .subscribe { location ->
                    currentLocation = location
                }
    }

    fun addTrip(passengerId: String, tripValue: RealTimeValue<TripModel>) {
        trips[passengerId] = tripValue.getValue()
        mTrips[passengerId] = tripValue

        tripValue.onChange.subscribe { trip ->
            trips[passengerId] = trip
        }
        updateProperty?.invoke("trips", trips)
    }

    private fun tripsToMap(): Map<String, Map<String, Any>> {
        val map = hashMapOf<String, Map<String, Any>>()
        trips.entries
                .forEach { entry -> map[entry.key] = entry.value.toMap() }
        return map
    }

    override fun toMap(): Map<String, Any> {
        return mapOf(
                "currentLocation" to currentLocation!!.toMap(),
                "trips" to tripsToMap()
        )
    }

    override fun toString(): String {
        return """
            ShuttleModel
                currentLocation=$currentLocation
                trips=$trips
                """
    }

    override fun updatePropertyListeners(): Map<String, Pair<IsPropertyUpdatedChecker, UpdatePropertyOperation>> {
        return mapOf(
                "currentLocation" to Pair(
                        { newValue -> newValue != currentLocation!!.toMap() },
                        { newValue ->
                            updatePropertyValue(newValue as Map<String, Any>, currentLocation!!, mCurrentLocationValue!!)
                        }),
                "trips" to Pair(
                        { newValue -> newValue != tripsToMap() },
                        { newValue ->
                            val childMapChanges = updatePropertyValueMap(TripModel::class.java, newValue as Map<String, Map<String, Any>>, trips, mTrips)
                            onTripsMapChange.onNext(childMapChanges)
                        })
        )

    }
}
