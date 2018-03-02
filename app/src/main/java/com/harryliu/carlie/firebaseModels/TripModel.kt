package com.harryliu.carlie.firebaseModels

import android.util.Log
import com.google.firebase.database.Exclude


/**
 * @author Harry Liu
 *
 * @version Feb 26, 2018
 */

class TripModel : FireBaseModel {

    var passengerId: String? = null
        set(value) {
            if (value != null)
                updateProperty?.invoke("passengerId", value)
            field = value
        }

    var pickupLocation: LocationModel? = null
    var dropOffLocation: LocationModel? = null

    var shuttleId: String? = null
        set(value) {
            if (value != null)
                updateProperty?.invoke("shuttleId", value)
            field = value
        }
    var shuttleEntered: Boolean = false
        set(value) {
            updateProperty?.invoke("shuttleEntered", value)
            field = value
        }
    var passengerLeft: Boolean = false
        set(value) {
            updateProperty?.invoke("passengerLeft", value)
            field = value
        }
    var cancel: Boolean = false
        set(value) {
            updateProperty?.invoke("cancel", value)
            field = value
        }

    var pickupTime: Long = -1
        set(value) {
            updateProperty?.invoke("pickupTime", value)
            field = value
        }

    @Exclude
    var mPickupLocationValue: RealTimeValue<LocationModel>? = null

    @Exclude
    var mDropOffLocationValue: RealTimeValue<LocationModel>? = null

    constructor() : super() {
        Log.d("TripModel", "Init")
        val initialLocation = LocationModel()
        initialLocation.latitude = 0.0
        initialLocation.longitude = 0.0

        setPickupLocationValue(RealTimeValue(initialLocation))
        setDropOffLocationValue(RealTimeValue(initialLocation))
    }

    fun setDropOffLocationValue(dropOffLocationValue: RealTimeValue<LocationModel>) {
        mDropOffLocationValue = dropOffLocationValue
        dropOffLocation = mDropOffLocationValue?.getValue()
        mDropOffLocationValue?.onChange!!
                .subscribe { value ->
                    dropOffLocation = value
//                    onAttributeChange.onNext(Pair("dropOffLocation", value))
                }
        updateProperty?.invoke("dropOffLocation", dropOffLocation!!)
    }

    fun setPickupLocationValue(pickupLocationValue: RealTimeValue<LocationModel>) {
        mPickupLocationValue = pickupLocationValue
        pickupLocation = mPickupLocationValue?.getValue()
        mPickupLocationValue?.onChange!!
                .subscribe { value ->
                    pickupLocation = value
//                    onAttributeChange.onNext(Pair("pickupLocation", value))
                }
        updateProperty?.invoke("pickupLocation", pickupLocation!!)
    }

    override fun toMap(): Map<String, Any> {
        return hashMapOf(
                "passengerId" to passengerId!!,
                "pickupLocation" to pickupLocation!!.toMap(),
                "dropOffLocation" to dropOffLocation!!.toMap(),
                "shuttleId" to shuttleId!!,
                "shuttleEntered" to shuttleEntered,
                "passengerLeft" to passengerLeft,
                "cancel" to cancel,
                "pickupTime" to pickupTime
        )
    }

    override fun toString(): String {
        return """
            TripModel
                passengerId=$passengerId
                pickupLocation=$pickupLocation
                dropOffLocation=$dropOffLocation
                shuttleId=$shuttleId
                shuttleEntered=$shuttleEntered
                passengerLeft=$passengerLeft
                cancel=$cancel
                pickupTime=$pickupTime
                """
    }

    override fun updatePropertyListeners(): Map<String, Pair<IsPropertyUpdatedChecker, UpdatePropertyOperation>> {
        return mapOf(
                "passengerId" to Pair(
                        { newValue -> newValue != passengerId },
                        { newValue -> passengerId = newValue as String }
                ),
                "pickupLocation" to Pair(
                        { newValue -> newValue != pickupLocation!!.toMap() },
                        { newValue ->
                            updatePropertyValue(newValue as Map<String, Any>, pickupLocation!!, mPickupLocationValue!!)
                        }),
                "dropOffLocation" to Pair(
                        { newValue -> newValue != dropOffLocation!!.toMap() },
                        { newValue ->
                            updatePropertyValue(newValue as Map<String, Any>, dropOffLocation!!, mDropOffLocationValue!!)
                        }),
                "shuttleId" to Pair(
                        { newValue -> newValue != shuttleId },
                        { newValue -> shuttleId = newValue as String }
                ),
                "shuttleEntered" to Pair(
                        { newValue -> newValue != shuttleEntered },
                        { newValue -> shuttleEntered = newValue as Boolean }
                ),
                "passengerLeft" to Pair(
                        { newValue -> newValue != passengerLeft },
                        { newValue -> passengerLeft = newValue as Boolean }
                ),
                "cancel" to Pair(
                        { newValue -> newValue != cancel },
                        { newValue -> cancel = newValue as Boolean }
                ),
                "pickupTime" to Pair(
                        { newValue -> newValue != pickupTime },
                        { newValue -> pickupTime = newValue as Long }
                )
        )
    }
}