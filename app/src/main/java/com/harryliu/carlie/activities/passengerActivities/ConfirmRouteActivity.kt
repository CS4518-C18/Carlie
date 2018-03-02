package com.harryliu.carlie.activities.passengerActivities

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.harryliu.carlie.BuildConfig
import com.harryliu.carlie.R
import com.harryliu.carlie.firebaseModels.PassengerModel
import com.harryliu.carlie.firebaseModels.RealTimeValue
import com.harryliu.carlie.firebaseModels.TripModel
import com.harryliu.carlie.services.AuthenticationService
import com.harryliu.carlie.services.LocationService
import com.harryliu.carlie.services.NavigationService
import com.harryliu.carlie.services.dataServices.TripService
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
import org.json.JSONObject


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
    private val mUser: PassengerModel = AuthenticationService.getUser()!!


    companion object {
        const val ORIGIN_LAT = "origin_lat"
        const val ORIGIN_LNG = "origin_lng"
        const val DESTINATION_LAT = "destination_lat"
        const val DESTINATION_LNG = "destination_lng"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_route)

        val originLat = intent.getDoubleExtra(ORIGIN_LAT, 0.0)
        val originLng = intent.getDoubleExtra(ORIGIN_LNG, 0.0)
        val origin = Point.fromLngLat(originLng, originLat)

        val destinationLat = intent.getDoubleExtra(DESTINATION_LAT, 0.0)
        val destinationLng = intent.getDoubleExtra(DESTINATION_LNG, 0.0)
        val destination = Point.fromLngLat(destinationLng, destinationLat)

        title = getString(R.string.confirm_ride_title)
        val mConfirmRideButton = confirm_ride_button
        val mCancelRideButton = cancel_ride_button
        mTextView = duration_text_view
        mMapView = route_map_view

        mConfirmRideButton.isEnabled = false

        val initialTrip = TripModel()
        initialTrip.passengerId = mUser.uid!!

        initialTrip.pickupLocation?.latitude = originLat
        initialTrip.pickupLocation?.longitude = originLng

        initialTrip.dropOffLocation?.latitude = destinationLat
        initialTrip.dropOffLocation?.longitude = destinationLng

        mConfirmRideButton.setOnClickListener {
            requestTrip(initialTrip)
            mCancelRideButton.isEnabled = false
        }

        mCancelRideButton.setOnClickListener {
            quit()
        }


        Mapbox.getInstance(this, BuildConfig.MAPBOX_ACCESS_TOKEN)
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

                        mConfirmRideButton.isEnabled = true
                    }
        })
    }

    private fun requestTrip(initialTrip: TripModel) {
        sendHTTPRequest(initialTrip, ::setupTrip)
    }

    private fun setupTrip(
            response: String?,
            initialTrip: TripModel) {

        val tripStat = JSONObject(response)
        val shuttleId: String = tripStat.getString("shuttleId")
        val status: String = tripStat.getString("status")
        Log.d("response", shuttleId + status)

        initialTrip.shuttleId = shuttleId
        val tripValue = RealTimeValue(initialTrip)

        val refs = listOf("/shuttles/$shuttleId/trips/${initialTrip.passengerId}")

        if (status == "exist") {
            tripValue.startSync(refs)
        } else if (status == "new") {
            tripValue.push(refs)
                    .subscribe {
                        tripValue.startSync(refs)
                    }
        }

        TripService.mCurrentTrip = initialTrip
        val intent = Intent(this, CurrentTripActivity::class.java)
        startActivity(intent)
    }

    private fun sendHTTPRequest(
            initialTrip: TripModel,
            callback: (String?, TripModel) -> Unit) {
        val queue: RequestQueue = Volley.newRequestQueue(this)
        val url = "https://carlie-server.herokuapp.com/passengers/${initialTrip.passengerId}/trips/new"

        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    callback(response, initialTrip)
                }, Response.ErrorListener { _ ->
        })
        queue.add(stringRequest)
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
        mLocationEngine = LocationService.requestLocationUpdates(this, 200, { location, _, _ ->
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
        quit()
    }

    private fun quit() {
        val returnIntent = Intent()
        returnIntent.putExtra("quit", 1)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}