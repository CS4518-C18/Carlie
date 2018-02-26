package com.harryliu.carlie.services

import android.content.Context
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.harryliu.carlie.BuildConfig
import io.reactivex.Observable

/**
 * @author Yuan Wang
 *
 * @version Feb 16, 2018
 */

class PlaceAutoCompleteService(context: Context) {
    private val mGeoDataClient = Places.getGeoDataClient(context, null)

    fun predictPlaces(query: String): Observable<List<AutocompletePrediction>> {
        return Observable.create<List<AutocompletePrediction>> { subscriber ->
            val filter = AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                    .build()
            getLatLngBounds()
            mGeoDataClient.getAutocompletePredictions(query, getLatLngBounds(), filter)
                    .addOnFailureListener { exception ->
                        exception.printStackTrace()
                    }
                    .addOnSuccessListener { response ->
                        val predictions = response.toList()
                        subscriber.onNext(predictions)
                    }
        }
    }

    private fun getLatLngBounds(): LatLngBounds {

        val deltaDegrees = 0.5

        val southWestCorner = LatLng(BuildConfig.CAMPUS_CENTER_LAT - deltaDegrees, BuildConfig.CAMPUS_CENTER_LNG - deltaDegrees)
        val northEastCorner = LatLng(BuildConfig.CAMPUS_CENTER_LAT + deltaDegrees, BuildConfig.CAMPUS_CENTER_LNG + deltaDegrees)

        return LatLngBounds(southWestCorner, northEastCorner)
    }
}
