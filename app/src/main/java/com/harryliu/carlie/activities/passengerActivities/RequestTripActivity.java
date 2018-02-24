package com.harryliu.carlie.activities.passengerActivities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.harryliu.carlie.Passenger;
import com.harryliu.carlie.R;
import com.harryliu.carlie.Trip;
import com.harryliu.carlie.services.AuthenticationService;
import com.harryliu.carlie.services.DatabaseService;

/**
 * @author Harry Liu
 *
 * @version Feb 16, 2018
 */

public class RequestTripActivity extends AppCompatActivity{
    DatabaseReference dr = DatabaseService.Companion.getDatabaseReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_trip);

        Button b = findViewById(R.id.request_trip_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseService.Companion.addTripList(dr,
                        new Trip(
                                new Passenger(AuthenticationService.Companion.getUser(
                                        AuthenticationService.Companion.getFirebaseAuth()).getUid(),
                                        "30",
                                        "john"), "", ""));
            }
        });
    }

}
