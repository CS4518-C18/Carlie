package com.harryliu.carlie.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_phone)

        // enter phone number
        button_confirm_phone.setOnClickListener { view ->
            val currentUser = AuthenticationService.getUser()
            if (currentUser != null) {
                val phoneNumber = edit_phone_number.text.toString()

                if (phoneNumber.length == 10) {
                    // store user's phone
                    DatabaseService.storeUserPhone(
                            currentUser,
                            phoneNumber)
                    DatabaseService.getUserType(
                            currentUser,
                            ::startUserActivity)

                } else {
                    Toast.makeText(this, "invalid phone number", Toast.LENGTH_SHORT).show()
                }
                //AuthenticationService.verifyPhone(phoneNumber, this, ::storeId)
            }
        }
    }

    private fun storeId (id: String?, passed: Boolean) {

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
}