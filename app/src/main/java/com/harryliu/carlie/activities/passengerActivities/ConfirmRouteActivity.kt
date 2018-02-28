package com.harryliu.carlie.activities.passengerActivities

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.harryliu.carlie.BuildConfig
import com.harryliu.carlie.Passenger
import com.harryliu.carlie.R
//import com.harryliu.carlie.Trip
import com.harryliu.carlie.activities.MainActivity
import com.harryliu.carlie.services.AuthenticationService
import com.harryliu.carlie.services.DatabaseService
import com.harryliu.carlie.services.LocationService
import com.harryliu.carlie.services.NavigationService
import com.jakewharton.rxbinding2.view.RxView
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.telemetry.location.LocationEngine
import kotlinx.android.synthetic.main.activity_confirm_route.*


/**
 * @author Harry Liu
 * @version Feb 26, 2018
 */
class ConfirmRouteActivity : AppCompatActivity() {

    private var mMapView: MapView? = null

    private var mMap: MapboxMap? = null
    private var mLocationPlugin: LocationLayerPlugin? = null
    private var mLocationEngine: LocationEngine? = null
    private var mTextView: TextView? = null
    private var mConfirmRideButton: Button? = null
    private var mCancelRideButton: Button? = null

    private val mUser: Passenger = AuthenticationService.getUser()!!

    companion object {
        const val ORIGIN_LAT = "origin_lat"
        const val ORIGIN_LNG = "origin_lng"
        const val DESTINATION_LAT = "destination_lat"
        const val DESTINATION_LNG = "destination_lng"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_route)

        title = getString(R.string.confirm_ride_title)
        mConfirmRideButton = confirm_ride_button
        mCancelRideButton = cancel_ride_button

        val originLat = intent.getDoubleExtra(ORIGIN_LAT, 0.0)
        val originLng = intent.getDoubleExtra(ORIGIN_LNG, 0.0)
        val origin = Point.fromLngLat(originLng, originLat)

        val destinationLat = intent.getDoubleExtra(DESTINATION_LAT, 0.0)
        val destinationLng = intent.getDoubleExtra(DESTINATION_LNG, 0.0)
        val destination = Point.fromLngLat(destinationLng, destinationLat)

//        mConfirmRideButton!!.setOnClickListener({
//            DatabaseService.getTripFromList(mUser.uid,
//                    fun  (trip: Trip?) {
//                        if (trip == null) {
//                            val newTrip = Trip(
//                                    mUser,
//                                    origin.toString(),
//                                    destination.toString())
//                            DatabaseService.addTripToList(newTrip)
//                        }
//                    })
//        })


        mTextView = findViewById(R.id.duration_text_view)

        val cancelRideButton = findViewById<Button>(R.id.cancel_ride_button)
        val mainActivityIntent = Intent(this, MainActivity::class.java)
//
//        RxView.clicks(cancelRideButton)
//                .subscribe {
//                    DatabaseService.removeTripFromList(mUser.uid);
//                    quit()
//                }


        val confirmButton = findViewById<Button>(R.id.confirm_ride_button)
        confirmButton.isEnabled = false

        Mapbox.getInstance(this, BuildConfig.MAPBOX_ACCESS_TOKEN)
        mMapView = findViewById(R.id.route_map_view)
        mMapView!!.onCreate(savedInstanceState)

        mMapView!!.getMapAsync({ mapboxMap ->
            mMap = mapboxMap
            enableLocationPlugin()


            Log.d("origin", origin.toString())
            Log.d("destination", destination.toString())

            mMap!!.addMarker(MarkerOptions()
                    .position(LatLng(destinationLat, destinationLng))
            )

            NavigationService.getRoute(origin, destination)
                    .subscribe { currentRoute ->
                        mTextView!!.text = getString(R.string.duration, toMinutes(currentRoute.duration()!!))
                        val navigationMapRoute = NavigationMapRoute(null, mMapView!!, mMap!!, R.style.NavigationMapRoute)
                        navigationMapRoute.addRoute(currentRoute)

                        confirmButton.isEnabled = true
                    }
        })
    }


    private fun toMinutes(seconds: Double): Int {
        return (seconds / 60.0).toInt()
    }

    @SuppressWarnings("MissingPermission")
    private fun enableLocationPlugin() {
        // Create an instance of LOST location engine
        initializeLocationEngine()

        mLocationPlugin = LocationLayerPlugin(mMapView!!, mMap!!, mLocationEngine)
        mLocationPlugin!!.setLocationLayerEnabled(LocationLayerMode.TRACKING)
    }

    @SuppressWarnings("MissingPermission")
    private fun initializeLocationEngine() {
        mLocationEngine = LocationService.requestLocationUpdates(this, { location, _, _ ->
            setCameraPosition(location)
        })

    }

    private fun setCameraPosition(location: Location) {
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude - 0.008, location.longitude), 13.0))
    }

    @SuppressWarnings("MissingPermission")
    override fun onStart() {
        super.onStart()
        if (mLocationEngine != null) {
            mLocationEngine!!.requestLocationUpdates()
        }
        if (mLocationPlugin != null) {
            mLocationPlugin!!.onStart()
        }
        mMapView!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        if (mLocationEngine != null) {
            mLocationEngine!!.removeLocationUpdates()
        }
        if (mLocationPlugin != null) {
            mLocationPlugin!!.onStop()
        }
        mMapView!!.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView!!.onDestroy()
        if (mLocationEngine != null) {
            mLocationEngine!!.deactivate()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView!!.onLowMemory()
    }

    override fun onResume() {
        super.onResume()
        mMapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView!!.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMapView!!.onSaveInstanceState(outState)
    }


    override fun onBackPressed() {
        // do nothing
    }

    private fun quit () {
        val returnIntent = Intent()
        returnIntent.putExtra("quit", 1)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}