package com.harryliu.carlie.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.harryliu.carlie.R
import com.harryliu.carlie.activities.driverActivities.PassengerListActivity
import com.harryliu.carlie.activities.passengerActivities.RequestTripActivity
import com.harryliu.carlie.services.AuthenticationService
import com.harryliu.carlie.services.DatabaseService

/**
 * @author Harry Liu
 *
 * @version Feb 16, 2018
 */
class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 123
    private val mFirebaseAuth: FirebaseAuth = AuthenticationService.getFirebaseAuth()
    private val mAuthStateListener: FirebaseAuth.AuthStateListener =
            AuthenticationService.getAuthStateListener(
                    this,
                    RC_SIGN_IN,
                    ::onSignedInInitialize,
                    ::onSignedOutCleanup)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun onSignedInInitialize () {

    }

    private fun onSignedOutCleanup () {

    }

    override fun onResume() {
        super.onResume()
        mFirebaseAuth.addAuthStateListener(mAuthStateListener)
    }

    override fun onPause() {
        super.onPause()
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener)
    }


    private fun checkPhoneExist (phone:String?) {
        if (phone != null) {
            // go to their own views
            /*
            //if (current_user is student)
            val student_intent = Intent(this, RequestTripActivity::class.java)
            startActivity(student_intent)
            //if (current_user is driver)
            val driver_intent = Intent(this, PassengerListActivity::class.java)
            startActivity(driver_intent)
            */
        } else {
            //Toast.makeText(this, "good", Toast.LENGTH_SHORT).show()
            // let user verify phone
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // signed in successful
                val current_user: FirebaseUser? = AuthenticationService.getUser(mFirebaseAuth);

                if (current_user != null) {
                    DatabaseService.getUserPhone(current_user,
                            DatabaseService.getDatabaseReference(),
                            ::checkPhoneExist)
                }

            } else if (resultCode == RESULT_CANCELED) {
                // cancelled sign in
                finish();
            }
        }
    }
}
