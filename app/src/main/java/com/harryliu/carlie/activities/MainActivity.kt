package com.harryliu.carlie.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.harryliu.carlie.firebaseModels.PassengerModel
import com.harryliu.carlie.R
import com.harryliu.carlie.activities.driverActivities.PassengerListActivity
import com.harryliu.carlie.activities.passengerActivities.RequestTripActivity
import com.harryliu.carlie.services.AuthenticationService
import com.harryliu.carlie.services.DatabaseService


/**
 * @author Haofan Zhang
 *
 * @version Feb 18 2018
 */
class MainActivity : AppCompatActivity() {

    // authentication
    private val RC_SIGN_IN: Int = 123
    private val RC_FINISH: Int = 124

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

    override fun onResume() {
        super.onResume()
        AuthenticationService.addAuthStateListener(mAuthStateListener)
    }

    override fun onPause() {
        super.onPause()
        AuthenticationService.removeAuthStateListener(mAuthStateListener)
    }

    private fun onSignedInInitialize() {
        val currentUser: FirebaseUser? = AuthenticationService.getFirebaseUser()
        // check if phone exist
        if (currentUser != null) {
            DatabaseService.getUser(currentUser.uid, ::checkPhoneExist)
        }
    }

    private fun onSignedOutCleanup() {

    }

    private fun checkPhoneExist(user: PassengerModel?) {
        // if exist
        if ((user != null) && (user.phone != null)) {
            // go to their own views
            AuthenticationService.setUser(user)
            startUserActivity(user)
        } else {
            // go to add phone activity
            val intent = Intent(this, AddPhoneActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startUserActivity(user: PassengerModel) {
        val type = user.type
        if (type == "student") {
            val intent = Intent(this, RequestTripActivity::class.java)
            startActivity(intent)
        } else if (type == "driver") {
            val intent = Intent(this, PassengerListActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // signed in successful

            } else if (resultCode == RESULT_CANCELED) {
                // cancelled sign in
                finish()
            }
        }
    }
}
