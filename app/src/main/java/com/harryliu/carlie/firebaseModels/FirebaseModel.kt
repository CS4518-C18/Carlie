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
    protected fun <T : FireBaseModel> updatePropertyValueMap(modelClass: Class<T>, newValueMap: Map<String, Map<String, Any>>, newModelMap: HashMap<String, T>, propertyValueMap: HashMap<String, RealTimeValue<T>>): ChildMapChanges<T> {
        Log.d("updatePropertyValueMap", "map $newValueMap")

        val addModels = mutableListOf<Pair<String, Pair<T, RealTimeValue<T>>>>()
        val updatedModels = mutableListOf<Pair<String, Pair<T, RealTimeValue<T>>>>()
        val deletedModels = mutableListOf<Pair<String, Pair<T, RealTimeValue<T>>>>()

        newValueMap.entries.forEach { entry ->

            val newValue = entry.value
            var model: T? = newModelMap[entry.key]
            var propertyValue: RealTimeValue<T>? = propertyValueMap[entry.key]

            if (!propertyValueMap.containsKey(entry.key)) {
                model = modelClass.newInstance()
                propertyValue = RealTimeValue(model)
                addModels.add(Pair(entry.key, Pair(model, propertyValue)))
            } else
                updatedModels.add(Pair(entry.key, Pair(model!!, propertyValue!!)))

            Log.d("updatePropertyValueMap", "newValue $newValue")

            model?.updatePropertyValue(newValue, model, propertyValue)
            newModelMap[entry.key] = model!!
            propertyValueMap[entry.key] = propertyValue
        }

        propertyValueMap.entries.forEach { entry ->
            if (!newValueMap.containsKey(entry.key))
                deletedModels.add(Pair(entry.key, Pair(newModelMap[entry.key]!!, propertyValueMap[entry.key]!!)))
            newModelMap.remove(entry.key)
            propertyValueMap.remove(entry.key)
        }

        return ChildMapChanges(addModels, updatedModels, deletedModels)
    }

    @Exclude
    abstract fun toMap(): Map<String, Any>

    @Exclude
    abstract fun updatePropertyListeners(): Map<String, Pair<IsPropertyUpdatedChecker, UpdatePropertyOperation>>
}


typealias IsPropertyUpdatedChecker = (Any) -> Boolean
typealias UpdatePropertyOperation = (Any) -> Unit