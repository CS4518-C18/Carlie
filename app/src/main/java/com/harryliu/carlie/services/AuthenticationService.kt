package com.harryliu.carlie.services

import com.firebase.ui.auth.AuthUI
import java.util.Arrays.asList
import android.support.v4.app.ActivityCompat.startActivityForResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import android.support.annotation.NonNull
import android.app.Activity
import android.content.Context
import com.google.firebase.auth.PhoneAuthProvider
import com.harryliu.carlie.R
import com.harryliu.carlie.R.id.edit_phone_number
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.reflect.KFunction
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential




/**
 * @author Haofan Zhang
 * @version 2/18/18
 */
class AuthenticationService {
    companion object {
        /**
         * specifies the list of authentication methods
         */
        private val mAuthProviders:List<AuthUI.IdpConfig> = Arrays.asList(
                AuthUI.IdpConfig.EmailBuilder().build(),
                //AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.FacebookBuilder().build()
                //new AuthUI.IdpConfig.TwitterBuilder().build()
        )

        /**
         * get Firebase authentication manager
         * @return FirebaseAuth instance
         */
        fun getFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()


        /**
         * log out from current context
         * @param mContext: current context/activity
         */
        fun logOut(mContext: Context) = AuthUI.getInstance().signOut(mContext);


        /**
         * get the current user
         * @param firebaseAuth: Firebase authentication manager
         */
        fun getUser(firebaseAuth: FirebaseAuth): FirebaseUser? = firebaseAuth.currentUser


        /**
         * get authentication state listener
         * @param mActivity: current activity
         * @param RC_SIGN_IN: sign in request code
         * @param onSignedIn: function to execute after sign in
         * @param onSignedOut: function to execute after sign out
         * @return authentication state listener
         */
        fun getAuthStateListener(mActivity: Activity,
                                 RC_SIGN_IN: Int,
                                 onSignedIn: () -> Unit,
                                 onSignedOut: () -> Unit): FirebaseAuth.AuthStateListener {
            return FirebaseAuth.AuthStateListener { firebaseAuth ->

                val user: FirebaseUser? = firebaseAuth.currentUser
                if (user != null) {
                    // signed in
                    onSignedIn()
                } else {
                    // not signed in
                    onSignedOut()
                    // launch FirebaseUI auth
                    mActivity.startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(mAuthProviders)
                                    .build(),
                            RC_SIGN_IN)
                }
            }
        }



    }
}