package com.harryliu.carlie.activities.passengerActivities

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.harryliu.carlie.BuildConfig
import com.harryliu.carlie.R
import com.harryliu.carlie.firebaseModels.RealTimeValue
import com.harryliu.carlie.firebaseModels.ShuttleModel
import com.harryliu.carlie.firebaseModels.TripModel
import com.harryliu.carlie.services.AuthenticationService
import com.harryliu.carlie.services.LocationService
import com.jakewharton.rxbinding2.view.RxView
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.permissions.PermissionsListener
import com.mapbox.services.android.telemetry.permissions.PermissionsManager


/**
 * @author Harry Liu
 * @version Feb 16, 2018
 */
class RequestTripActivity : AppCompatActivity(), PermissionsListener {

    private var mMapView: MapView? = null

    private var mMap: MapboxMap? = null
    private var mPermissionsManager: PermissionsManager? = null
    private var mLocationPlugin: LocationLayerPlugin? = null
    private var mLocationEngine: LocationEngine? = null
    var initialTrip: TripModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_trip)

        val requestRideButton = findViewById<View>(R.id.request_ride_button)

        val intent = Intent(this, PlaceAutoCompleteActivity::class.java)

        RxView.clicks(requestRideButton)
                .subscribe {
                    startActivity(intent)
                }

        Mapbox.getInstance(this, BuildConfig.MAPBOX_ACCESS_TOKEN)
        mMapView = findViewById(R.id.main_map_view)
        mMapView!!.onCreate(savedInstanceState)

        mMapView!!.getMapAsync({ mapboxMap ->
            mMap = mapboxMap
            enableLocationPlugin()
        })

//        val pickupLocation = RealTimeValue(LocationModel(42.275258, -71.806504))
//        val dropOffLocation = RealTimeValue(LocationModel(42.273488, -71.810211))
//
//        initialTrip = TripModel("4R4HKefc89Rd9Zs6ccIvRnhvH6Q2", pickupLocation, dropOffLocation, "fd28ebfd-e531-467b-9a8b-fb650d26ccd4")
//
//        val tripValue = RealTimeValue(initialTrip!!)
//
//        val refs = listOf("/trips/6d7121b4-6fda-40c2-908c-4a55fdc34856/")
//        tripValue.onChange.subscribe { trip ->
//            Log.d("onChange", trip.toString())
//        }
//        tripValue.push(refs)
//                .subscribe {
//                    tripValue.startSync(refs)
//                    initialTrip?.passengerId = "10"
//                }

        val initialShuttle = ShuttleModel()

        val shuttleValue = RealTimeValue(initialShuttle)
        shuttleValue.onChange
                .subscribe { shuttle ->
                    Log.d("onChange", shuttle.toString())
                }
        shuttleValue.startSync(listOf(
                "/shuttles/shuttle1/"
        ))

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

            R.id.test_item -> {

                initialTrip!!.cancel = !initialTrip!!.cancel
//                NotificationService.showNotification(this, "Test", "Message", this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressWarnings("MissingPermission")
    private fun enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create an instance of LOST location engine
            initializeLocationEngine()

            mLocationPlugin = LocationLayerPlugin(mMapView!!, mMap!!, mLocationEngine)
            mLocationPlugin!!.setLocationLayerEnabled(LocationLayerMode.TRACKING)
        } else {
            mPermissionsManager = PermissionsManager(this)
            mPermissionsManager!!.requestLocationPermissions(this)
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun initializeLocationEngine() {
        mLocationEngine = LocationService.requestLocationUpdates(this, { location, _, _ ->
            setCameraPosition(location)
        })

    }

    private fun setCameraPosition(location: Location) {
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude), 13.0))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        mPermissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationPlugin()
        } else {
            finish()
        }
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

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
    }

    override fun onBackPressed() {
        //do nothing
    }
}