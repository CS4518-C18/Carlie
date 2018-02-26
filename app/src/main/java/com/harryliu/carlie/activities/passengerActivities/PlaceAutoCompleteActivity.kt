package com.harryliu.carlie.activities.passengerActivities

import android.app.Activity
import android.content.Intent
import android.location.Location.distanceBetween
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.LatLng
import com.harryliu.carlie.R
import com.harryliu.carlie.adapters.PlaceAdapter
import com.harryliu.carlie.services.PlaceService
import com.harryliu.carlie.viewModels.PlaceViewModel
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.util.concurrent.TimeUnit

        /**
         * @author Harry Liu
         *
         * @version Feb 24, 2018
         */

typealias UpdatePlaceOperation = (position: Int) -> Unit

class PlaceAutoCompleteActivity : AppCompatActivity() {
    private val RC_FINISH: Int = 123

    private var mDropOffLocationEditText: EditText? = null
    private var mPlaceService: PlaceService? = null

    private var mPlaceAutoCompleteListView: ListView? = null
    private var mPickupLocationEditText: EditText? = null

    private var mDropOffPlaces = List<AutocompletePrediction?>(0, { _ -> null })

    private var mDropOffPlacesViewModel = List<PlaceViewModel?>(0, { _ -> null })
    private var mPickupPlacesViewModel = List<PlaceViewModel?>(0, { _ -> null })
    private var mCurrentPlacesViewModel = List<PlaceViewModel?>(0, { _ -> null })

    private var mDropOffSelected: Boolean = false
    private var mCurrentPickupPlace: Place? = null
    private var mCurrentDropOffPlace: AutocompletePrediction? = null
    private var mDestinationTooFarToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_auto_complete)

        title = getString(R.string.select_places_title)

        mPlaceService = PlaceService(this)

        mDropOffLocationEditText = findViewById(R.id.drop_off_location_edit_text)
        mPickupLocationEditText = findViewById(R.id.pickup_location_edit_text)

        mPlaceAutoCompleteListView = findViewById(R.id.place_autocomplete_listView)

        mDestinationTooFarToast = Toast.makeText(this, getString(R.string.destination_too_far), Toast.LENGTH_LONG)

        val placeAdapter = PlaceAdapter(this)

        mPlaceAutoCompleteListView!!.adapter = placeAdapter

        val updatePlaceOperationObservable = Observable.create<UpdatePlaceOperation> { subscriber ->

            RxView.focusChanges(mPickupLocationEditText!!)
                    .skipInitialValue()
                    .subscribe { isFocused ->
                        if (isFocused)
                            subscriber.onNext { position ->
                                mPickupLocationEditText!!.setText(mPickupPlacesViewModel[position]!!.placeName)
                            }
                    }

            RxView.focusChanges(mDropOffLocationEditText!!)
                    .skipInitialValue()
                    .subscribe { isFocused ->
                        if (isFocused)
                            subscriber.onNext { position ->
                                mDropOffLocationEditText!!.setText(mDropOffPlacesViewModel[position]!!.placeName)

                                mCurrentDropOffPlace = mDropOffPlaces[position]
                                mDropOffSelected = true
                            }
                    }
        }


        RxAdapterView.itemClicks(mPlaceAutoCompleteListView!!)
                .withLatestFrom(
                        updatePlaceOperationObservable,
                        BiFunction<Int, UpdatePlaceOperation, Pair<Int, UpdatePlaceOperation>> { position, updatePlaceOperation ->
                            Pair(position, updatePlaceOperation)
                        })
                .subscribe { pair ->
                    val position = pair.first
                    val updatePlaceOperation = pair.second

                    updatePlaceOperation(position)

                    placeAdapter.clear()
                    placeAdapter.notifyDataSetChanged()

                    if (mCurrentPickupPlace != null && mCurrentDropOffPlace != null)
                        confirmTrip()
                }

        Observable.create<List<PlaceViewModel?>> { subscriber ->
            RxTextView.afterTextChangeEvents(mDropOffLocationEditText!!)
                    .skipInitialValue()
                    .throttleLast(1000, TimeUnit.MILLISECONDS)
                    .distinctUntilChanged()
                    .filter { _ -> !mDropOffSelected }
                    .flatMap {
                        val query = mDropOffLocationEditText!!.text.toString()
                        mPlaceService!!.predictPlaces(query)
                    }
                    .subscribe { predictions ->

                        mDropOffPlaces = predictions
                        mDropOffPlacesViewModel = predictions.map { prediction ->
                            PlaceViewModel(
                                    prediction.placeId!!,
                                    prediction.getPrimaryText(null).toString(),
                                    prediction.getSecondaryText(null).toString()
                            )
                        }
                        subscriber.onNext(mDropOffPlacesViewModel)
                    }

            mPlaceService!!.currentPlacePredictions()
                    .subscribe { places ->
                        mPickupPlacesViewModel = places.map { place ->
                            PlaceViewModel(
                                    place.id,
                                    place.name.toString(),
                                    place.address.toString())
                        }

                        mCurrentPickupPlace = places[0]
                        mPickupLocationEditText!!.setText(mPickupPlacesViewModel[0]!!.placeName)
                        subscriber.onNext(mPickupPlacesViewModel)
                    }
        }
                .subscribe { placeViewModels ->
                    placeAdapter.clear()
                    mCurrentPlacesViewModel = placeViewModels
                    placeAdapter.addAll(placeViewModels)
                    placeAdapter.notifyDataSetChanged()
                }
    }

    private fun confirmTrip() {
        Log.d("PlaceAutoComplete", "requestTrip")

        mPlaceService!!.getPlace(mCurrentDropOffPlace!!.placeId!!)
                .map { dropOffPlace -> dropOffPlace.latLng }
                .subscribe { dropOffLocation ->
                    val pickupLocation = mCurrentPickupPlace!!.latLng

                    if (!distanceWithInRange(dropOffLocation) || !distanceWithInRange(pickupLocation)) {
                        mDropOffSelected = false
                        mDestinationTooFarToast!!.show()
                    } else {
                        val intent = Intent(this, ConfirmRouteActivity::class.java)

                        intent.putExtra(ConfirmRouteActivity.ORIGIN_LAT, pickupLocation.latitude)
                        intent.putExtra(ConfirmRouteActivity.ORIGIN_LNG, pickupLocation.longitude)
                        intent.putExtra(ConfirmRouteActivity.DESTINATION_LAT, dropOffLocation.latitude)
                        intent.putExtra(ConfirmRouteActivity.DESTINATION_LNG, dropOffLocation.longitude)

                        startActivityForResult(intent, RC_FINISH)
                    }
                }
    }

    private fun distanceWithInRange(to: LatLng): Boolean {
        val campusLat = 42.274436
        val campusLng = -71.808721
        val radiusInMiles = 1.0

        val results = FloatArray(3)
        distanceBetween(campusLat, campusLng, to.latitude, to.longitude, results)

        val distance = getMiles(results[0])

        return distance <= radiusInMiles
    }

    private fun getMiles(meter: Float): Double {
        return meter * 0.000621371192
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_FINISH && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getIntExtra("quit", 0) == 1) {
                finish()
            }
        }
    }

}