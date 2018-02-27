package com.harryliu.carlie.services

import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * @author Harry Liu
 * @version Feb 16, 2018
 */

object NavigationService {
    fun getRoute(origin: Point, destination: Point): Observable<DirectionsRoute> {
        return Observable.create<DirectionsRoute> { subscriber ->
            NavigationRoute.builder()
                    .accessToken(com.harryliu.carlie.BuildConfig.MAPBOX_ACCESS_TOKEN)
                    .origin(origin)
                    .destination(destination)
                    .build()
                    .getRoute(object : Callback<DirectionsResponse> {
                        override fun onFailure(call: Call<DirectionsResponse>?, t: Throwable?) {
                            t?.printStackTrace()
                        }

                        override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                            if (response.body() == null)
                                return
                            else if (response.body()!!.routes().size < 1) {
                                return
                            }

                            val currentRoute = response.body()!!.routes()[0]
                            subscriber.onNext(currentRoute)
                        }
                    })
        }
    }
}
