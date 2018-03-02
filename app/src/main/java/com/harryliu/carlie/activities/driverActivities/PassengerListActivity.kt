package com.harryliu.carlie.activities.driverActivities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import com.harryliu.carlie.R
import com.harryliu.carlie.services.AuthenticationService
import com.harryliu.carlie.services.LocationService
import kotlinx.android.synthetic.main.activity_passenger_list.*
import com.harryliu.carlie.services.ShuttleService

/**
 * @author Haofan Zhang
 *
 * @version Feb 23, 2018
 */
class PassengerListActivity : AppCompatActivity() {

    //    private val mTripList = ArrayList<Trip>()
    private var mListView: ListView? = null
    private val mActivity = this
//    private val mAdapter: AppAdapter = AppAdapter(mTripList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passenger_list)
//        mListView!!.adapter = mAdapter

//        DatabaseService.bindTripList(::updatePassengerList)

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

//    private fun updatePassengerList(tripSnap: DataSnapshot?, mode: Int) {
//        if (tripSnap != null) {
//            val trip = tripSnap.getValue(Trip::class.java)
//            if (trip != null) {
//                when (mode) {
//                    DatabaseService.ADD -> {
//                        mTripList.add(trip)
//                        mAdapter.notifyDataSetChanged()
//                    }
//
//                    DatabaseService.REMOVE -> {
//                        for (t in mTripList) {
//                            if (t.uid == trip.uid) {
//                                mTripList.remove(t)
//                                break
//                            }
//                        }
//                        mAdapter.notifyDataSetChanged()
//                    }
//                    DatabaseService.CHANGE -> {
//                        var i = 0
//                        while (i < mTripList.size) {
//                            if (mTripList[i].uid == trip.uid) {
//                                mTripList[i] = trip
//                                break
//                            }
//                            i++
//                        }
//                    }
//                }
//            }
//        }
//    }

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