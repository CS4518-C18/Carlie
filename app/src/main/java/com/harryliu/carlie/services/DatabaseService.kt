package com.harryliu.carlie.services

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.harryliu.carlie.Passenger
import com.harryliu.carlie.Trip
import com.google.firebase.database.DatabaseReference



/**
 * @author Haofan Zhang
 */
class DatabaseService {

    companion object {

        /**
         * retrieve a database reference
         * @return Firebase database reference
         */
        fun getDatabaseReference () = FirebaseDatabase.getInstance().reference


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

        fun bindTripList (ref: DatabaseReference, callback: (DataSnapshot?) -> Unit) {
            val listRef:DatabaseReference = ref.child("trips").child("list")
            listRef.addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                }

                override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                    callback(p0)
                }

                override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                    callback(p0)
                }

                override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                    callback(p0)
                }

                override fun onChildRemoved(p0: DataSnapshot?) {
                    callback(p0)
                }
            })
        }

        fun getTripList (ref: DatabaseReference, callback: (Iterable<DataSnapshot>) -> Unit) {
            val tripsRef:DatabaseReference = ref.child("trips")
            tripsRef.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    callback(dataSnapshot.child("list").children)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
            tripsRef.child("trigger").setValue(0)
        }

        fun addTripList (ref: DatabaseReference, trip: Trip) {
            val listRef:DatabaseReference = ref.child("trips").child("list")
            /*
            val pushedPostRef = listRef.push()
            val tripKey = pushedPostRef.key
            */
            listRef.child(trip.uid).setValue(trip)
        }

        fun logTrip (ref: DatabaseReference, trip: Trip) {
            val logRef : DatabaseReference = ref.child("log")
            /*
            val pushedPostRef = logRef.push()
            val logKey = pushedPostRef.key
            */
            logRef.child(trip.uid).setValue(trip)
        }
    }
}