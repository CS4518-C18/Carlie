package com.harryliu.carlie.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.harryliu.carlie.R
import android.content.Intent
import com.harryliu.carlie.services.NotificationService
import android.widget.EditText
import android.widget.Toast
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.View


/**
 * @author Yuan Wang
 *
 * @version Feb 18 2018
 */
class MainActivity : AppCompatActivity() {

    //private var mServiceIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // mServiceIntent = Intent(applicationContext, NotificationService::class.java)
    }


}
