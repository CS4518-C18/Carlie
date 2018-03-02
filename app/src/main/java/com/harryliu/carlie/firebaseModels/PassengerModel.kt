package com.harryliu.carlie.firebaseModels

/**
 * @author Haofan Zhang
 * @version 2/22/18
 */

class PassengerModel() : FireBaseModel() {
    var uid: String? = null
    var phone: String? = null
    var name: String? = null
    var type: String? = null

    constructor(uid: String, phone: String, name: String, type: String) : this() {
        this.uid = uid
        this.phone = phone
        this.name = name
        this.type = type
    }

    override fun toMap(): Map<String, Any> {
        return mapOf(
                "uid" to uid!!,
                "phone" to phone!!,
                "name" to name!!,
                "type" to type!!
        )
    }

    override fun toString(): String {
        return """
            PassengerModel
                uid=$uid
                phone=$phone
                name=$name
                type=$type
            """
    }

    override fun updatePropertyListeners(): Map<String, Pair<IsPropertyUpdatedChecker, UpdatePropertyOperation>> {
        return mapOf()
    }
}
