package com.harryliu.carlie.services

import com.mapbox.mapboxsdk.annotations.PolygonOptions
import com.mapbox.mapboxsdk.geometry.LatLng

/**
 * @author Harry Liu
 * @version Mar 2, 2018
 */

object MapUIService {
//    fun newCirclePolygon(centerX: Double, centerY: Double, radius: Double): PolygonOptions {
//        val circle = mutableListOf<LatLng>()
//        for (degrees in 0..359) {
//            val degreesDouble = degrees.toDouble()
//            val y = centerY + radius * Math.sin(degreesDouble)
//            val x = centerX + radius * Math.cos(degreesDouble)
//            circle.add(LatLng(y, x))
//        }
//        return PolygonOptions().addAll(circle)
//    }

    fun newSquarePolygon(centerX: Double, centerY: Double, halfWidth: Double): PolygonOptions {
        val square = mutableListOf<LatLng>()
        square.add(LatLng(centerY + halfWidth, centerX - halfWidth))
        square.add(LatLng(centerY + halfWidth, centerX + halfWidth))
        square.add(LatLng(centerY - halfWidth, centerX + halfWidth))
        square.add(LatLng(centerY - halfWidth, centerX - halfWidth))
        return PolygonOptions().addAll(square)
    }
}
