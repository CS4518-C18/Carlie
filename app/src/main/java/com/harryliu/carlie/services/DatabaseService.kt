package com.harryliu.carlie.services

import com.google.firebase.database.*
import com.harryliu.carlie.firebaseModels.PassengerModel


/**
 * @author Haofan Zhang
 */
class DatabaseService {

    companion object {

        private val mRef: DatabaseReference = FirebaseDatabase.getInstance().reference
        const val ADD: Int = 0
        const val REMOVE: Int = 1
        const val CHANGE: Int = 2
        const val MOVE: Int = 3

        /**
         * get the user as a passenger object
         * @param uid: uid of passenger, same as the uid of firebase user
         * @param callback: callback function
         */
        fun getUser(uid: String,
                    callback: (PassengerModel?) -> Unit) {
            val userRef: DatabaseReference = mRef.child("users").child(uid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(PassengerModel::class.java)
                    callback(user)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }


        /**
         * store the user in firebase
         * @param user: passenger object
         */
        fun storeUser(user: PassengerModel) {
            mRef.child("users").child(user.uid).setValue(user)
        }


        fun bindTripList(callback: (DataSnapshot?, Int) -> Unit) {
            val listRef: DatabaseReference = mRef.child("trips")
            listRef.addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                }

                override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                    callback(p0, ADD)
                }

                override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                    callback(p0, CHANGE)
                }

                override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                    callback(p0, MOVE)
                }

                override fun onChildRemoved(p0: DataSnapshot?) {
                    callback(p0, REMOVE)
                }
            })
        }

//        fun addTripToList(trip: Trip) {
//            val listRef: DatabaseReference = mRef.child("trips").child(trip.passenger.uid)
//            listRef.setValue(trip)
//        }
//
//        fun removeTripFromList(passengerId: String) {
//            val listRef: DatabaseReference = mRef.child("trips").child(passengerId)
//            listRef.removeValue()
//        }
//
//        fun getTripFromList (passengerId: String, callback: (Trip?) -> Unit) {
//            val listRef: DatabaseReference = mRef.child("trips").child(passengerId)
//            listRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onCancelled(p0: DatabaseError?) {
//                }
//                override fun onDataChange(p0: DataSnapshot?) {
//                    if (p0 != null) {
//                        callback(p0.getValue(Trip::class.java))
//                    } else {
//                        callback(null)
//                    }
//                }
//            })
//        }
//
//        fun logTrip(trip: Trip) {
//            val logRef: DatabaseReference = mRef.child("log")
//            logRef.child(trip.uid).setValue(trip)
//            /*
//            val pushedPostRef = logRef.push()
//            val logKey = pushedPostRef.key
//            */
//        }
    }
}