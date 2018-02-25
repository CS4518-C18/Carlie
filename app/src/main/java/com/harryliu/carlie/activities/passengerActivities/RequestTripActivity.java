package com.harryliu.carlie.activities.passengerActivities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.harryliu.carlie.Passenger;
import com.harryliu.carlie.R;
import com.harryliu.carlie.Trip;
import com.harryliu.carlie.services.AuthenticationService;
import com.harryliu.carlie.services.DatabaseService;
import com.harryliu.carlie.services.TripService;

/**
 * @author Harry Liu
 *
 * @version Feb 16, 2018
 */

public class RequestTripActivity extends AppCompatActivity{
    private Button mButtonSend;
    private Button mButtonCancel;
    private Button mButtonLogout;
    private EditText mEditStart;
    private EditText mEditDestination;
    private Activity mActivity = this;
    private FirebaseUser mUser = AuthenticationService.Companion.getFirebaseUser();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_trip);

        mButtonSend = findViewById(R.id.request_trip_send);
        mButtonCancel = findViewById(R.id.request_trip_cancel);
        mButtonLogout = findViewById(R.id.request_trip_logout);
        mEditStart = findViewById(R.id.request_trip_start);
        mEditDestination = findViewById(R.id.request_trip_destination);

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TripService.passengerCurrentTrip == null) {
                    Trip newTrip = new Trip(
                            AuthenticationService.Companion.getUser(),
                            mEditStart.getText().toString(),
                            mEditDestination.getText().toString());
                    TripService.passengerCurrentTrip = newTrip;
                    DatabaseService.Companion.addTripToList(newTrip);
                }
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Trip currentTrip = TripService.passengerCurrentTrip;
                if (currentTrip != null) {
                    DatabaseService.Companion.removeTripFromList(currentTrip);
                    TripService.passengerCurrentTrip = null;
                }
            }
        });

        mButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationService.Companion.logOut(mActivity);
                mActivity.finish();
            }
        });

    }

}
