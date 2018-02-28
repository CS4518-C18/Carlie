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
abstract class FirebaseModel {
    var id: String? = null

    @Exclude
    var updateProperty: ((name: String, value: Any) -> Unit)? = null

    val onAttributeChange = PublishSubject.create<Pair<String, Any?>>()

    @Exclude
    abstract fun toMap(): Map<String, Any>

    @Exclude
    abstract fun updatePropertyListeners(): Map<String, Pair<IsPropertyUpdatedChecker, UpdatePropertyOperation>>
}


typealias IsPropertyUpdatedChecker = (Any) -> Boolean
typealias UpdatePropertyOperation = (Any) -> Unit