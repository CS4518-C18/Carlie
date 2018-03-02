package com.harryliu.carlie.firebaseModels

import java.util.*

/**
 * @author Harry Liu
 *
 * @version Feb 26, 2018
 */

class LocationModel(): FireBaseModel() {
    var latitude: Double? = null
    var longitude: Double? = null

    constructor(latitude: Double, longitude: Double): this() {
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
        return mapOf(
                "latitude" to Pair(
                        { newValue -> newValue != latitude },
                        { newValue -> latitude = newValue as Double }
                ),
                "longitude" to Pair(
                        { newValue -> newValue != longitude },
                        { newValue -> longitude = newValue as Double }
                )
        )
    }
}