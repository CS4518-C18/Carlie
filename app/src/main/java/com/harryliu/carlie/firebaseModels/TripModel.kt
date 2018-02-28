package com.harryliu.carlie.firebaseModels


/**
 * @author Harry Liu
 *
 * @version Feb 26, 2018
 */

class TripModel : FirebaseModel {

    var passengerId: String? = null

    var pickupLocation: LocationModel? = null
    var dropOffLocation: LocationModel? = null
    var shuttleId: String? = null
    var shuttleEntered: Boolean = false
    var passengerLeaved: Boolean = false

    constructor()

    constructor(passenger: String, pickupLocationValue: RealTimeValue<LocationModel>, dropOffLocationValue: RealTimeValue<LocationModel>, shuttleId: String) {
        this.passengerId = passenger
        this.pickupLocation = pickupLocationValue.getValue()
        this.dropOffLocation = dropOffLocationValue.getValue()
        this.shuttleId = shuttleId

        pickupLocationValue.onChange
                .subscribe { value ->
                    pickupLocation = value
                    onAttributeChange.onNext(Pair("pickupLocation", value))
                }
        dropOffLocationValue.onChange
                .subscribe { value ->
                    dropOffLocation = value
                    onAttributeChange.onNext(Pair("dropOffLocation", value))
                }
    }

    override fun toMap(): Map<String, Any> {
        return hashMapOf(
                "pickupLocation" to pickupLocation!!.toMap(),
                "dropOffLocation" to dropOffLocation!!.toMap(),
                "passengerId" to passengerId!!,
                "shuttleId" to shuttleId!!,
                "shuttleEntered" to shuttleEntered,
                "passengerLeaved" to passengerLeaved
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
                passengerLeaved=$passengerLeaved
                """
    }
}