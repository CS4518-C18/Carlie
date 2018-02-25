package com.harryliu.carlie.activities

import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.harryliu.carlie.BuildConfig
import com.harryliu.carlie.R
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.location.LocationEngineListener
import com.mapbox.services.android.telemetry.location.LocationEnginePriority
import com.mapbox.services.android.telemetry.location.LostLocationEngine
import com.mapbox.services.android.telemetry.permissions.PermissionsListener
import com.mapbox.services.android.telemetry.permissions.PermissionsManager


/**
 * @author Harry Liu
 *
 * @version Feb 16, 2018
 */
class MainActivity : AppCompatActivity(), PermissionsListener, LocationEngineListener {

    private var mMapView: MapView? = null

    private var mMap: MapboxMap? = null
    private var mPermissionsManager: PermissionsManager? = null
    private var mLocationPlugin: LocationLayerPlugin? = null
    private var mLocationEngine: LocationEngine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Mapbox.getInstance(this, BuildConfig.MAPBOX_ACCESS_TOKEN)
        mMapView = findViewById(R.id.main_map_view)
        mMapView!!.onCreate(savedInstanceState)

        mMapView!!.getMapAsync({ mapboxMap ->
            mMap = mapboxMap
            enableLocationPlugin()
        })
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
        mLocationEngine = LostLocationEngine(this)
        mLocationEngine!!.priority = LocationEnginePriority.HIGH_ACCURACY
        mLocationEngine!!.interval = 200

        mLocationEngine!!.addLocationEngineListener(this)
        mLocationEngine!!.activate()

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
    override fun onConnected() {
        Log.d("onConnected", "Connected")
        mLocationEngine!!.requestLocationUpdates()
    }

    override fun onLocationChanged(location: Location) {
        setCameraPosition(location)
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
}
