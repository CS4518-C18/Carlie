package com.harryliu.carlie.activities.passengerActivities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.ContactsContract
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.harryliu.carlie.BuildConfig
import com.harryliu.carlie.R
import com.harryliu.carlie.R.layout.activity_current_trip
import com.harryliu.carlie.firebaseModels.LocationModel
import com.harryliu.carlie.firebaseModels.RealTimeValue
import com.harryliu.carlie.services.dataServices.TripService
import com.mapbox.mapboxsdk.Mapbox
import com.harryliu.carlie.firebaseModels.TripModel
import com.harryliu.carlie.services.*
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.annotations.Polygon
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.permissions.PermissionsListener
import com.mapbox.services.android.telemetry.permissions.PermissionsManager
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_current_trip.*

/**
 * @author Haofan Zhang
 */
class CurrentTripActivity : AppCompatActivity(), PermissionsListener {

    private var mMapView: MapView? = null

    private var mMap: MapboxMap? = null
    private var mPermissionsManager: PermissionsManager? = null
    private var mLocationPlugin: LocationLayerPlugin? = null
    private var mLocationEngine: LocationEngine? = null


    private val currentTrip: TripModel = TripService.mCurrentTrip!!
    private val currentTripValue = RealTimeValue(currentTrip)
    private val currentTripRefs = listOf("/shuttles/${currentTrip.shuttleId}/trips/${currentTrip.passengerId}/")

    private var geofenceManager: GeofenceManager? = null
    private var mPolygon: Polygon? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_current_trip)
        val cancel_ride_button: Button = cancel_ride_button

        geofenceManager = GeofenceManager(this)

        val pickupLocation = currentTrip.pickupLocation!!
        val dropOffLocation = currentTrip.dropOffLocation!!

        showGeofenceArea(pickupLocation)

        val shuttleLocationValue = RealTimeValue(LocationModel())

        val currentLocRefs = listOf("/shuttles/${currentTrip.shuttleId}/location/")

        currentTripValue.onChange.subscribe { newTrip ->
            Log.d("onChange", newTrip.toString())
            if (newTrip.shuttleEntered) {
                NotificationService.showNotification(this, "enter", "good", this)
            }
        }

        shuttleLocationValue.startSync(currentLocRefs)
        currentTripValue.startSync(currentTripRefs)

        geofenceManager!!.addGeofence(
                currentTrip.passengerId!!,
                pickupLocation.latitude,
                pickupLocation.longitude,
                ::enterPickupLocation,
                ::leavePickupLocation)

        Mapbox.getInstance(this, BuildConfig.MAPBOX_ACCESS_TOKEN)
        mMapView = map_view
        mMapView!!.onCreate(savedInstanceState)

        mMapView!!.getMapAsync({ mapboxMap ->
            mMap = mapboxMap
            enableLocationPlugin()

            mMap!!.addMarker(MarkerOptions()
                    .position(LatLng(dropOffLocation.latitude, dropOffLocation.longitude))
            )
        })



        val distanceToShuttleObservable = Observable.create<Float> { subscriber ->
            shuttleLocationValue.onChange.subscribe { newLocation ->
                subscriber.onNext(getDistance(newLocation, pickupLocation))
            }
        }

        distanceToShuttleObservable.subscribe { distance ->
            message_text_view.text = getString(R.string.shuttle_arrive_time, estimateArriveTime(distance))
        }

        cancel_ride_button.setOnClickListener { _ ->
            currentTripValue.remove(currentTripRefs)
            finish()
        }

    }

    private fun showGeofenceArea(location: LocationModel) {
        if(mPolygon != null)
            mMap?.removePolygon(mPolygon!!)
        val polygonOptions = MapUIService.newSquarePolygon(location.longitude, location.latitude, 0.005)
        polygonOptions.fillColor(Color.parseColor("#3bb2d0"))
        mPolygon = mMap?.addPolygon(polygonOptions)
    }

    private fun enterPickupLocation (id: String) {
        currentTrip.passengerLeft = false
    }

    private fun leavePickupLocation (id: String) {
        currentTrip.passengerLeft = true
        NotificationService.showNotification(this, "title", "msg", this)
    }

    private fun estimateArriveTime(distance: Float): Int {
            val drivingSpeedInMetersPerSecond = 13.4112
            return (distance / drivingSpeedInMetersPerSecond / 60).toInt()
    }

    private fun getDistance (shuttleLocation: LocationModel, pickupLocation: LocationModel): Float {
        val newLat = shuttleLocation.latitude
        val newLng = shuttleLocation.longitude
            val sl = Location("shuttle")
            sl.latitude = newLat
            sl.longitude = newLng
            val pl = Location("pickup")
            pl.latitude = pickupLocation.latitude
            pl.longitude = pickupLocation.longitude
            return sl.distanceTo(pl)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_common, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out_item -> {
                AuthenticationService.logOut(this)
                quit()
                return true
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
        mLocationEngine = LocationService.requestLocationUpdates(this, 200, { location, _, _ ->
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
            quit()
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
        geofenceManager!!.removeGeofence(currentTrip.passengerId!!)
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
        TripService.mCurrentTrip = currentTrip
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

    private fun quit() {
        val returnIntent = Intent()
        returnIntent.putExtra("quit", 1)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}