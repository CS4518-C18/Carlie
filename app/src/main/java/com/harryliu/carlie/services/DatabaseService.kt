package com.harryliu.carlie.services

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.harryliu.carlie.activities.MainActivity
import com.harryliu.carlie.activities.passengerActivities.CurrentTripActivity


/**
 * @author Haofan Zhang
 */
class DatabaseService {

    companion object {

        /**
         * retrieve a database reference
         * @return Firebase database reference
         */
        fun getDatabaseReference () = FirebaseDatabase.getInstance().getReference()


        /**
         * get the user's phone number and execute callback with phone as param
         * @param user: FirebaseUser provided by authentication service
         * @param ref: Firebase database reference
         * @param callback: function which uses a string as param and returns void
         */
        fun getUserPhone (user: FirebaseUser,
                          ref: DatabaseReference,
                          callback: (String?) -> Unit) {
            val userRef:DatabaseReference = ref.child("users").child(user.uid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val phone:String? = dataSnapshot.child("phone").getValue(String::class.java)
                    callback(phone)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
            userRef.child("trigger").setValue(0)
        }


        /**
         * store the user's phone number
         * @param user: FirebaseUser provided by authentication service
         * @param ref: Firebase database reference
         * @param phone: user's phone number as a string
         */
        fun storeUserPhone (user: FirebaseUser, ref: DatabaseReference, phone: String) {
            ref.child("users").child(user.uid).child("phone").setValue(phone)
            ref.child("users").child(user.uid).child("type").setValue("student")
        }

        fun getUserType (user: FirebaseUser, ref: DatabaseReference, callback: (String?) -> Unit) {
            val userRef:DatabaseReference = ref.child("users").child(user.uid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val type:String? = dataSnapshot.child("type").getValue(String::class.java)
                    callback(type)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
            userRef.child("trigger").setValue(0)
        }
    }
}