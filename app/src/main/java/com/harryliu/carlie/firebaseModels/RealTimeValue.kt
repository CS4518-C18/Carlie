package com.harryliu.carlie.firebaseModels

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * @author Harry Liu
 *
 * @version Feb 26, 2018
 */

class RealTimeValue<T : FirebaseModel>(private val ref: DatabaseReference?, private val valueType: Class<T>?, private var currentValue: T?) {
    val onChange = PublishSubject.create<T>()

    fun onDownStream(): Observable<T> {
        return Observable.create { subscriber ->
            ref!!.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("Attribute", "downStream: onCancelled")
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    currentValue = dataSnapshot.getValue(valueType!!)!!
                    onChange.onNext(currentValue!!)
                    subscriber.onNext(currentValue!!)
                }
            })
        }
    }

    fun next(value: T) {
        currentValue = value

        ref!!.updateChildren(
                hashMapOf<String, Any>(
                        "/" to value.toMap()
                )
        )
        onChange.onNext(currentValue!!)
    }

    fun getValue(): T {
        return this.currentValue!!
    }
}