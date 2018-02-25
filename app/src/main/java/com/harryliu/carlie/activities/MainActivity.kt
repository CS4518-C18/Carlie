package com.harryliu.carlie.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.harryliu.carlie.R
import com.harryliu.carlie.services.GeofenceManager.libraryGeofence
import com.harryliu.carlie.services.GeofenceManager.fullerGeofence
import android.widget.Toast
import android.hardware.SensorEvent



/**
 * @author Harry Liu
 *
 * @version Feb 16, 2018
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun createGeofence(){
        return CustomGeofence;
    }

    private fun enterGeofence(cGeofence: CustomGeofence, event: SensorEvent) {
        if (!cGeofence.finishedSixSteps) {
            if (cGeofence.inForTheFirstTime) {
                val enterGeofence = "entering " + cGeofence.name + " geofence"
                Toast.makeText(this,
                        enterGeofence,
                        Toast.LENGTH_SHORT).show()
                cGeofence.initialStepInGeofence = event.values[0]
                cGeofence.inForTheFirstTime = false
                cGeofence.outForTheFirstTime = true
            } else {
                if (event.values[0] - cGeofence.initialStepInGeofence >= 6) {
                    cGeofence.numEnteredGeofence++
                    val sixStepGeofence = "6 steps in " + cGeofence.name + " geofence"
                    Toast.makeText(this,
                            sixStepGeofence,
                            Toast.LENGTH_SHORT).show()
                    println(sixStepGeofence)
                    cGeofence.finishedSixSteps = true
//                    setNumEnteredGeofenceText()
                }
            }
        }
    }

//    private fun setNumEnteredGeofenceText() {
//        val fullerText = ("You visited Fuller lab for "
//                + (fullerGeofence.numEnteredGeofence).toString
//                + " times")
//        val libraryText = ("You visited Library for "
//                + (libraryGeofence.numEnteredGeofence).toString
//                + " times")
//        mTextViewFullerLab.setText(fullerText)
//        mTextViewLibrary.setText(libraryText)
//    }
}
