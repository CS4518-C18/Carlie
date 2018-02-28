package com.harryliu.carlie.firebaseModels

/**
 * @author Harry Liu
 *
 * @version Feb 26, 2018
 */

class LocationModel : FirebaseModel {
    var latitude: Double? = null
    var longitude: Double? = null

    constructor()

    constructor(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    override fun toMap(): Map<String, Any> {
        return hashMapOf(
                "latitude" to latitude!!,
                "longitude" to longitude!!
        )
    }


    override fun toString(): String {
        return """
            LocationModel
                latitude=$latitude
                longitude=$longitude
                """
    }

    override fun updatePropertyListeners(): Map<String, Pair<IsPropertyUpdatedChecker, UpdatePropertyOperation>> {
        return mapOf()
    }
}