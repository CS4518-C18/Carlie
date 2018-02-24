package com.harryliu.carlie.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.harryliu.carlie.R
import com.harryliu.carlie.activities.driverActivities.PassengerListActivity
import com.harryliu.carlie.activities.passengerActivities.RequestTripActivity
import com.harryliu.carlie.services.AuthenticationService
import com.harryliu.carlie.services.DatabaseService
import kotlinx.android.synthetic.main.activity_main.*


/**
 * @author Harry Liu
 *
 * @version Feb 16, 2018
 */
class MainActivity : AppCompatActivity() {

    // authentication
    private val RC_SIGN_IN: Int = 123
    private val mFirebaseAuth: FirebaseAuth = AuthenticationService.getFirebaseAuth()
    private val mAuthStateListener: FirebaseAuth.AuthStateListener =
            AuthenticationService.getAuthStateListener(
                    this,
                    RC_SIGN_IN,
                    ::onSignedInInitialize,
                    ::onSignedOutCleanup)

    // database
    private val mDatabaseReference: DatabaseReference =
            DatabaseService.getDatabaseReference()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun onSignedInInitialize () {
        val currentUser: FirebaseUser? = AuthenticationService.getUser(mFirebaseAuth)
        // check if phone exist
        if (currentUser != null) {
            DatabaseService.getUserPhone(currentUser,
                    mDatabaseReference,
                    ::checkPhoneExist)
        }

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
        // if exist
        if (phone != null) {
            // go to their own views
            val currentUser:FirebaseUser? = AuthenticationService.getUser(mFirebaseAuth);
            if (currentUser != null) {
                DatabaseService.getUserType(currentUser, mDatabaseReference, ::startUserActivity)
            }
        } else {
            // go to add phone activity
            val intent = Intent(this, AddPhoneActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startUserActivity (type:String?) {
        if (type == "student") {
            val intent = Intent(this, RequestTripActivity::class.java)
            startActivity(intent)
            finish()
        } else if (type == "driver") {
            val intent = Intent(this, PassengerListActivity::class.java)
            startActivity(intent)
            finish()
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
