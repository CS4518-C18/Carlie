package com.harryliu.carlie.firebaseModels

import android.util.Log
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import io.reactivex.subjects.PublishSubject
import java.util.*

/**
 * @author Harry Liu
 *
 * @version Feb 26, 2018
 */

@IgnoreExtraProperties
abstract class FireBaseModel {
    var id = UUID.randomUUID().toString()

    @Exclude
    var updateProperty: ((name: String, value: Any) -> Unit)? = null

    @Exclude
    protected fun <T : FireBaseModel> updatePropertyValue(newValue: Map<String, Any>, newModel: T, propertyValue: RealTimeValue<T>) {
        val map = propertyValue.getValue().updatePropertyListeners()
        newValue.entries.forEach { newValueEntry ->

            val propertyName = newValueEntry.key

            val callbackEntry = map[propertyName]
            if (callbackEntry != null) {

                val isPropertyUpdated = callbackEntry.first
                val updatePropertyOperation = callbackEntry.second

                if (isPropertyUpdated(propertyName))
                    updatePropertyOperation(newValueEntry.value)
            }
        }
        propertyValue.onChange.onNext(newModel)
    }

    @Exclude
    protected fun <T : FireBaseModel> updatePropertyValueMap(modelClass: Class<T>, newValueMap: Map<String, Map<String, Any>>, newModelMap: HashMap<String, T>, propertyValueMap: HashMap<String, RealTimeValue<T>>) {
        Log.d("updatePropertyValueMap", "map $newValueMap")
        newValueMap.entries.forEach { entry ->

            val newValue = entry.value
            var model: T? = newModelMap[entry.key]
            var propertyValue: RealTimeValue<T>? = propertyValueMap[entry.key]

            if (!propertyValueMap.containsKey(entry.key)) {
                model = modelClass.newInstance()
                propertyValue = RealTimeValue(model)
            }

            Log.d("updatePropertyValueMap", "newValue $newValue")

            model?.updatePropertyValue(newValue, model, propertyValue!!)
            newModelMap[entry.key] = model!!
            propertyValueMap[entry.key] = propertyValue!!

//            Log.d("updatePropertyValue", newValueMap.toString())
//            Log.d("updatePropertyKey", propertyValueMap.keys.toString())
//            entry
//            val value =
//            val map = .updatePropertyListeners()

//            if(propertyValueMap.containsKey())
        }

//        val map = propertyValue.getValue().updatePropertyListeners()
//        newValue.entries.forEach { newValueEntry ->
//
//            val propertyName = newValueEntry.key
//
//            val callbackEntry = map[propertyName]
//            if (callbackEntry != null) {
//
//                val isPropertyUpdated = callbackEntry.first
//                val updatePropertyOperation = callbackEntry.second
//
//                if(isPropertyUpdated(propertyName))
//                    updatePropertyOperation(newValueEntry.value)
//            }
//        }
//        propertyValue.onChange.onNext(newModel)
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