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
    var firstTime = true

//    private val mAdapter: AppAdapter = AppAdapter(mTripList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passenger_list)

        val shuttleModel = ShuttleModel()
        val shuttleModelValue = RealTimeValue(shuttleModel)

        shuttleModel.onTripsMapChange.subscribe {childMapChanges ->
            Log.d("onTripsMapChange", childMapChanges.toString())
            if (firstTime) {
                val plAdapter = PassengerListAdapter(this, shuttleModel)
                plAdapter.mode = Attributes.Mode.Single
                tripList.adapter = plAdapter
                plAdapter.notifyDataSetChanged()
                firstTime = false
            }
        }

        shuttleModelValue.startSync(listOf("/shuttles/shuttle1/"))
//        val

//        DatabaseService.bindTripList(::updatePassengerList)
        ShuttleService.startLocationUpdates(this, shuttleModel.mCurrentLocationValue!!)

        geofenceManager = GeofenceManager(this)
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

    private fun enterPickupLocation(id: String) {
        mTripList[id]?.getValue()!!.shuttleEntered = true
    }

    private fun leavePickupLocation(id: String) {
    }


    override fun onBackPressed() {
        //do nothing
    }

}