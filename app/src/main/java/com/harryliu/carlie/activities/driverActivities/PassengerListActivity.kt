package com.harryliu.carlie.activities.driverActivities

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.*
import com.google.firebase.database.DataSnapshot
import com.harryliu.carlie.R
import com.harryliu.carlie.firebaseModels.RealTimeValue
import com.harryliu.carlie.firebaseModels.ShuttleModel
import com.harryliu.carlie.firebaseModels.TripModel
import com.harryliu.carlie.services.*
import kotlinx.android.synthetic.main.activity_passenger_list.*
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.BaseSwipeAdapter
import com.daimajia.swipe.util.Attributes
import com.harryliu.carlie.adapters.PassengerListAdapter
import kotlinx.android.synthetic.main.activity_passenger_list.view.*


/**
 * @author Haofan Zhang
 * @version Feb 23, 2018
 */
class PassengerListActivity : AppCompatActivity() {

    private var geofenceManager : GeofenceManager? = null
    private var mListView: ListView? = null
    private val mActivity = this
    var firstTime = true

//    private val mAdapter: AppAdapter = AppAdapter(mTripList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passenger_list)

        val shuttle = ShuttleModel()
        val shuttleValue = RealTimeValue(shuttle)

        val tripList = passenger_list_view
        val currentLocRefs = listOf("/shuttles/shuttle1/")
        shuttleValue.startSync(currentLocRefs)


        geofenceManager = GeofenceManager(this)
        //DatabaseService.bindTripList(::updatePassengerList)
        ShuttleService.startLocationUpdates(this, "shuttle1")

        //
        shuttleValue.onChange.subscribe {newShuttle ->
            if (firstTime) {
                val plAdapter = PassengerListAdapter(this, shuttle)
                plAdapter.mode = Attributes.Mode.Single
                tripList.adapter = plAdapter
                plAdapter.notifyDataSetChanged()
                firstTime = false
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_common, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out_item -> {
                AuthenticationService.logOut(this)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /*
    private fun updatePassengerList(tripSnap: DataSnapshot?, mode: Int) {
        if (tripSnap != null) {
            Log.d("fuck", "something")
            val trip = tripSnap.getValue(TripModel::class.java)
            if (trip != null) {
                Log.d("fuck", "updated")
                when (mode) {
                    DatabaseService.ADD -> {

                        val tripValue = RealTimeValue(trip)
                        val tripRefs = listOf("/shuttles/${trip.shuttleId}/trips/${trip.passengerId}/")

                        tripValue.onChange.subscribe { newTrip ->
                            Log.d("fuck", newTrip.toString())
                            if (newTrip.passengerLeft) {
                                NotificationService.showNotification(this, trip.passengerId!!, "left", this)
                            }
                        }

                        tripValue.startSync(tripRefs)
                        mTripList[trip.passengerId!!] = tripValue

                        geofenceManager!!.addGeofence(
                                trip.passengerId!!,
                                trip.pickupLocation!!.latitude,
                                trip.pickupLocation!!.longitude,
                                ::enterPickupLocation,
                                ::leavePickupLocation)
                        //mAdapter.notifyDataSetChanged()
                    }

                    DatabaseService.REMOVE -> {
                        mTripList.remove(trip.passengerId)
                        geofenceManager!!.removeGeofence(trip.passengerId!!)
                        //mAdapter.notifyDataSetChanged()
                    }
                    DatabaseService.CHANGE -> {
                        Log.d("fuck", "changed")
                    }
                }
            }
        }
    }
    */

    private fun enterPickupLocation (id: String) {
        //mTripList[id]?.getValue()!!.shuttleEntered = true
    }

    private fun leavePickupLocation (id: String) {
    }


    override fun onBackPressed() {
        //do nothing
    }

}