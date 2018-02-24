package com.harryliu.carlie.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.harryliu.carlie.Passenger
import com.harryliu.carlie.R
import com.harryliu.carlie.activities.driverActivities.PassengerListActivity
import com.harryliu.carlie.activities.passengerActivities.RequestTripActivity
import com.harryliu.carlie.services.AuthenticationService
import com.harryliu.carlie.services.DatabaseService
import kotlinx.android.synthetic.main.activity_add_phone.*
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author Haofan Zhang
 * @version 2/19/18
 */
class AddPhoneActivity: AppCompatActivity()  {
    private val RC_FINISH: Int = 124

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_phone)

        // enter phone number
        button_confirm_phone.setOnClickListener { view ->
            val firebaseUser: FirebaseUser? = AuthenticationService.getFirebaseUser()
            if (firebaseUser != null) {
                val phoneNumber = edit_phone_number.text.toString()
                val userName = edit_name.text.toString()
                if (phoneNumber.length == 10) {
                    // store user's phone
                    val currentUser = Passenger(
                            firebaseUser.uid,
                            phoneNumber,
                            userName,
                            "student")
                    DatabaseService.storeUser(currentUser)
                    AuthenticationService.setUser(currentUser)
                    startUserActivity(currentUser)
                } else {
                    Toast.makeText(this, "invalid phone number", Toast.LENGTH_SHORT).show()
                }
                //AuthenticationService.verifyPhone("1" + phoneNumber, this, ::storeId)
            }
        }
    }

    private fun storeId (id: String?, passed: Boolean) {

    }

    private fun startUserActivity (user:Passenger) {
        val type = user.type
        if (type == "student") {
            val intent = Intent(this, RequestTripActivity::class.java)
            startActivityForResult(intent, RC_FINISH)
        } else if (type == "driver") {
            val intent = Intent(this, PassengerListActivity::class.java)
            startActivityForResult(intent, RC_FINISH)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_FINISH) {
            finish()
        }
    }
}