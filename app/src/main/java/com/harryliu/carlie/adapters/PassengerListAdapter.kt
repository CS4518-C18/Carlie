package com.harryliu.carlie.adapters
import android.content.Context
import com.daimajia.swipe.adapters.BaseSwipeAdapter
import android.widget.TextView
import android.widget.Toast
import java.nio.file.Files.delete
import com.daimajia.swipe.SwipeLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daimajia.swipe.SimpleSwipeListener
import com.harryliu.carlie.R
import com.harryliu.carlie.firebaseModels.ShuttleModel
import kotlinx.android.synthetic.main.list_item_trip.view.*
import java.util.stream.Collectors.toList


/**
 * @author Haofan Zhang
 */
class PassengerListAdapter(context: Context, shuttleModel: ShuttleModel): BaseSwipeAdapter () {
    val mContext = context
    val mTripList = shuttleModel.trips
    val mKeyList = mTripList.keys.toList()

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe_item
    }

    override fun generateView(position: Int, parent: ViewGroup): View {
        val v = LayoutInflater.from(mContext).inflate(R.layout.list_item_trip, null)
        val swipeLayout = v.findViewById(getSwipeLayoutResourceId(position)) as SwipeLayout
        swipeLayout.addSwipeListener(object : SimpleSwipeListener() {
            override fun onOpen(layout: SwipeLayout) {

            }
        })
        return v
    }

    override fun fillValues(position: Int, convertView: View) {
        val name = convertView.swipe_name
        val destination = convertView.swipe_destination
        name.text = mTripList[mKeyList[position]]!!.passengerId
        destination.text = mTripList[mKeyList[position]]!!.dropOffLocation!!.latitude.toString() + ", " +
                mTripList[mKeyList[position]]!!.dropOffLocation!!.longitude.toString()
    }

    override fun getCount(): Int {
        return mTripList.size
    }

    override fun getItem(position: Int): Any? {
        return mTripList[mKeyList[position]]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}