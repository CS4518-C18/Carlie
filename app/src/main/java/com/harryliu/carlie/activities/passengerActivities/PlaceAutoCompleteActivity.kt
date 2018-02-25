package com.harryliu.carlie.activities.passengerActivities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import android.widget.ListView
import com.harryliu.carlie.R
import com.harryliu.carlie.adapters.PlaceAdaper
import com.harryliu.carlie.services.PlaceAutoCompleteService
import com.jakewharton.rxbinding2.widget.RxTextView
import java.util.concurrent.TimeUnit

/**
 * @author Harry Liu
 *
 * @version Feb 24, 2018
 */

class PlaceAutoCompleteActivity: AppCompatActivity() {

    var mDropOffLocationEditText: EditText? = null
    var mPlaceAutoCompleteService: PlaceAutoCompleteService? = null
    var mPlaceAutoCompleteListView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_auto_complete)

        mDropOffLocationEditText = findViewById(R.id.drop_off_location_edit_text)
        mPlaceAutoCompleteListView = findViewById(R.id.place_autocomplete_listView)

        mPlaceAutoCompleteService = PlaceAutoCompleteService(this)

        val placeAdapter = PlaceAdaper(this)

        mPlaceAutoCompleteListView!!.adapter = placeAdapter

        RxTextView.afterTextChangeEvents(mDropOffLocationEditText!!)
                .skipInitialValue()
                .throttleLast(1000, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .flatMap {
                    val query = mDropOffLocationEditText!!.text.toString()

                    Log.d("Query", query)
                     mPlaceAutoCompleteService!!.predictPlaces(query)
                }
                .subscribe { predictions ->
                    placeAdapter.clear()
                    placeAdapter.addAll(predictions)
                    placeAdapter.notifyDataSetChanged()
                }
    }
}