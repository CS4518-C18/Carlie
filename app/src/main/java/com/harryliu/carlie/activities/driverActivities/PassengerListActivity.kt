package com.harryliu.carlie.activities.driverActivities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import com.google.firebase.database.DataSnapshot
import com.harryliu.carlie.R
import com.harryliu.carlie.firebaseModels.RealTimeValue
import com.harryliu.carlie.firebaseModels.TripModel
import com.harryliu.carlie.services.*
import kotlinx.android.synthetic.main.activity_passenger_list.*

/**
 * @author Haofan Zhang
 *
 * @version Feb 23, 2018
 */
class PassengerListActivity : AppCompatActivity() {

    private val mTripList = HashMap<String, RealTimeValue<TripModel>>()
    private val geofenceManager = GeofenceManager(this)
    private var mListView: ListView? = null
    private val mActivity = this

//    private val mAdapter: AppAdapter = AppAdapter(mTripList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passenger_list)
//        mListView!!.adapter = mAdapter

        DatabaseService.bindTripList(::updatePassengerList)
        ShuttleService.startLocationUpdates(this, "shuttle1")
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

    private fun updatePassengerList(tripSnap: DataSnapshot?, mode: Int) {
        if (tripSnap != null) {
            val trip = tripSnap.getValue(TripModel::class.java)
            if (trip != null) {
                when (mode) {
                    DatabaseService.ADD -> {
                        val tripValue = RealTimeValue(trip)
                        val tripRefs = listOf("/shuttles/${trip.shuttleId}/trips/${trip.passengerId}/")
                        tripValue.startSync(tripRefs)
                        mTripList[trip.passengerId!!] = tripValue

                        geofenceManager.addGeofence(
                                trip.passengerId!!,
                                trip.pickupLocation!!.latitude,
                                trip.pickupLocation!!.longitude,
                                ::enterPickupLocation,
                                ::leavePickupLocation)
                        //mAdapter.notifyDataSetChanged()
                    }

                    DatabaseService.REMOVE -> {
                        mTripList.remove(trip.passengerId)
                        geofenceManager.removeGeofence(trip.passengerId!!)
                        //mAdapter.notifyDataSetChanged()
                    }
                    DatabaseService.CHANGE -> {
                        if (trip.passengerLeft) {
                            NotificationService.showNotification(this, "title", "msg", this)
                        }
                    }
                }
            }
        }
    }

    private fun enterPickupLocation (id: String) {
        mTripList[id]?.getValue()!!.shuttleEntered = true
    }

    private fun leavePickupLocation (id: String) {
    }

//    internal inner class AppAdapter constructor(val mAppList: List<Trip>) : BaseAdapter() {
//        override fun getCount(): Int {
//            return mAppList.size
//        }
//
//        override fun getItem(position: Int): Trip {
//            return mAppList[position]
//        }
//
//        override fun getItemId(position: Int): Long {
//            return position.toLong()
//        }
//
//        override fun getView(position: Int, cv: View?, parent: ViewGroup): View {
//
//            val holder: ViewHolder
//            val menuView: View
//            val contentView: View
//            val convertView: View
//            if (cv == null) {
//                contentView = View.inflate(mActivity, R.layout.swipe_content, null)
//                menuView = View.inflate(mActivity, R.layout.swipe_menu, null)
//                convertView = DragDelItem(contentView, menuView)
//                holder = ViewHolder(convertView)
//            } else {
//                convertView = cv
//                holder = convertView.tag as ViewHolder
//            }
//            val item = getItem(position)
//
//            // change based on item
//            holder.name.text = item.passenger.name
//            return convertView
//        }
//
//        internal inner class ViewHolder(view: View) {
//            var name: TextView
//            var tv_open: TextView
//            var tv_del: TextView
//
//            init {
//                // set up reference
//                name = view.findViewById(R.id.passenger_list_name)
//                tv_open = view.findViewById(R.id.tv_open)
//                tv_del = view.findViewById(R.id.tv_del)
//                view.tag = this
//            }
//        }
//    }

    override fun onBackPressed() {
        //do nothing
    }

}