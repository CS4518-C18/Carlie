package com.harryliu.carlie.services

import android.content.Context
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.Places
import io.reactivex.Observable


/**
 * @author Harry Liu
 *
 * @version Feb 25, 2018
 */

class PlaceService(context: Context) {

    private val mGeoDataClient = Places.getGeoDataClient(context, null)
    private val mPlaceDetectionClient = Places.getPlaceDetectionClient(context, null)

    fun getPlace(placeId: String): Observable<Place> {
        return Observable.create { observer ->
            mGeoDataClient.getPlaceById(placeId)
                    .addOnSuccessListener { placeBufferResponse ->
                        val place = placeBufferResponse.get(0).freeze()
                        observer.onNext(place)
                        placeBufferResponse.release()
                    }
        }

    }

    @SuppressWarnings("MissingPermission")
    fun currentPlacePredictions(): Observable<List<Place>> {
        return Observable.create<List<Place>> { subscriber ->
            val placeResult = mPlaceDetectionClient.getCurrentPlace(null)
            placeResult.addOnCompleteListener({ task ->
                val likelyPlaces = task.result
                val places = task.result.map { placeLikelihood ->
                    placeLikelihood.place.freeze()
                }

                subscriber.onNext(places)
                likelyPlaces.release()
            })
        }
    }

    fun predictPlaces(query: String): Observable<List<AutocompletePrediction>> {
        return Observable.create<List<AutocompletePrediction>> { subscriber ->
            val filter = AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                    .build()
            mGeoDataClient.getAutocompletePredictions(query, null, filter)
                    .addOnFailureListener { exception ->
                        exception.printStackTrace()
                    }
                    .addOnSuccessListener { response ->
                        val predictions = response.map { prediction -> prediction.freeze() }
                        subscriber.onNext(predictions)
                        response.release()
                    }
        }
    }
}
