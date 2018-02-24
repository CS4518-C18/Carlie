package com.harryliu.carlie.activities.driverActivities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.harryliu.carlie.DragDelItem
import com.harryliu.carlie.Passenger
import com.harryliu.carlie.R
import com.harryliu.carlie.Trip
import com.harryliu.carlie.services.DatabaseService
import java.util.ArrayList

/**
 * @author Haofan Zhang
 * @version 2/23/18.
 */
class PassengerListActivity : AppCompatActivity() {

    private val mTripList = ArrayList<Trip>()
    private var mListView: ListView? = null
    private val mContext = this
    private val mDatabaseReference = DatabaseService.getDatabaseReference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passenger_list)
        //supportActionBar!!.hide()

        DatabaseService.getTripList(mDatabaseReference, ::initializePassengerList)
        DatabaseService.bindTripList(mDatabaseReference, ::updatePassengerList)
        mListView = findViewById(R.id.passenger_list)
    }

    private fun updatePassengerList(tripSnap: DataSnapshot?) {
        val trip = tripSnap!!.getValue(Trip::class.java)
        if (trip != null) {
            System.out.println(trip.passenger.name)
        }
    }

    private fun initializePassengerList(list: Iterable<DataSnapshot>) {
       list.forEach { tripSnap ->
           val trip: Trip? = tripSnap.getValue(Trip::class.java)
           if (trip != null) {
               mTripList.add(trip)
           }
       }
        mListView!!.adapter = AppAdapter(mTripList)
    }

        internal inner class AppAdapter constructor(val mAppList: List<Trip>) : BaseAdapter() {
            override fun getCount(): Int {
                return mAppList.size
            }

            override fun getItem(position: Int): Trip {
                return mAppList[position]
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getView(position: Int, cv: View?, parent: ViewGroup): View {
                var convertView = cv

                val holder: ViewHolder
                val menuView: View
                if (convertView == null) {
                    convertView = View.inflate(mContext,
                            R.layout.swipe_content, null)
                    menuView = View.inflate(mContext,
                            R.layout.swipe_menu, null)
                    convertView = DragDelItem(convertView, menuView);
                    holder = ViewHolder(convertView)
                } else {
                    holder = convertView.tag as ViewHolder
                }
                val item = getItem(position)

                // change based on item
                holder.name.text = item.passenger.name
                return convertView
            }

            internal inner class ViewHolder(view: View) {
                var name: TextView
                var tv_open: TextView
                var tv_del: TextView

                init {
                    // set up reference
                    name = view.findViewById(R.id.passenger_name)
                    tv_open = view.findViewById(R.id.tv_open)
                    tv_del = view.findViewById(R.id.tv_del)

                    //DisplayMetrics displayMetrics = new DisplayMetrics();
                    //getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    //int width = displayMetrics.widthPixels;
                    //cLayout.setMinimumWidth(width-60);
                    view.tag = this
                }
            }
        }


}