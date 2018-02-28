package com.harryliu.carlie.services.dataServices

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/**
 * @author Yang Liu
 *
 * @version Feb 26, 2018
 */
object RealTimeDatabaseService {
    private val databaseReference = FirebaseDatabase.getInstance().reference

    fun getRootRef(): DatabaseReference {
        return databaseReference
    }
}