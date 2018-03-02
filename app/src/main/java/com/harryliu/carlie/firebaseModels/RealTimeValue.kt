package com.harryliu.carlie.firebaseModels

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.harryliu.carlie.services.dataServices.RealTimeDatabaseService
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * @author Harry Liu
 *
 * @version Feb 26, 2018
 */

class RealTimeValue<T : FireBaseModel>
(private var currentValue: T) {


    val onChange = PublishSubject.create<T>()

    fun push(refs: List<String>): Observable<Unit> {
        return Observable.create { subscriber ->
            var count = refs.size
            refs.map { ref ->
                RealTimeDatabaseService.getRootRef()
                        .child(ref)
            }
                    .forEach { ref ->
                        Log.d("Ref", ref.key)
                        ref.setValue(currentValue.toMap(), { error, _ ->
                            if (error == null)
                                count--
                            if (count == 0)
                                subscriber.onNext(Unit)
                        })
                    }
        }
    }

    fun startSync(refs: List<String>) {

        currentValue.updateProperty = { name, value ->
            val map = hashMapOf<String, Any>()
            refs.forEach { ref ->
                map["$ref/$name"] = value
            }

            Log.d("RealTimeValue", "SET $name -> $value = $map")
            RealTimeDatabaseService.getRootRef().updateChildren(map)
        }

        refs.map { ref ->
            RealTimeDatabaseService.getRootRef()
                    .child(ref)
        }.forEach { ref ->
            currentValue.updatePropertyListeners()
                    .entries
                    .forEach { entry ->
                        val propertyName = entry.key

                        val isPropertyUpdated = entry.value.first
                        val updatePropertyOperation = entry.value.second

                        ref.child(propertyName)
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(error: DatabaseError) {
                                        Log.d("Attribute", "downStream: onCancelled")
                                    }

                                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                                        val newValue = dataSnapshot.value

                                        if (newValue != null && isPropertyUpdated(newValue)) {
                                            Log.d("RealTimeValue", "GET ${ref.key} $propertyName <- $newValue")
                                            updatePropertyOperation(newValue)
                                            onChange.onNext(currentValue)
                                        }
                                    }
                                })
                        entry.key
                    }
        }
    }

    fun getValue(): T {
        return this.currentValue
    }
}