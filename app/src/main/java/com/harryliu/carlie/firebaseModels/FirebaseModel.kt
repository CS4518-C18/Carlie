package com.harryliu.carlie.firebaseModels

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import io.reactivex.subjects.PublishSubject

/**
 * @author Harry Liu
 *
 * @version Feb 26, 2018
 */

@IgnoreExtraProperties
abstract class FireBaseModel {
    var id: String? = null

    @Exclude
    var isPulling = false

    @Exclude
    var updateProperty: ((name: String, value: Any) -> Unit)? = null

    @Exclude
    protected fun <T: FireBaseModel> updatePropertyValue(newValue: Map<String, Any>, newModel: T, propertyValue: RealTimeValue<T>) {
        val map = propertyValue.getValue().updatePropertyListeners()
        newValue.entries.forEach { newValueEntry ->

            val propertyName = newValueEntry.key

            val callbackEntry = map[propertyName]
            if (callbackEntry != null) {

                val isPropertyUpdated = callbackEntry.first
                val updatePropertyOperation = callbackEntry.second

                if(isPropertyUpdated(propertyName))
                    updatePropertyOperation(newValueEntry.value)
            }
        }
        propertyValue.onChange.onNext(newModel)
    }

    @Exclude
    val onAttributeChange = PublishSubject.create<Pair<String, Any?>>()

    @Exclude
    abstract fun toMap(): Map<String, Any>

    @Exclude
    abstract fun updatePropertyListeners(): Map<String, Pair<IsPropertyUpdatedChecker, UpdatePropertyOperation>>
}


typealias IsPropertyUpdatedChecker = (Any) -> Boolean
typealias UpdatePropertyOperation = (Any) -> Unit