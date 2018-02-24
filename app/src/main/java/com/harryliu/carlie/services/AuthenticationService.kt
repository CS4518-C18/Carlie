package com.harryliu.carlie.services

import com.firebase.ui.auth.AuthUI
import java.util.Arrays.asList
import android.support.v4.app.ActivityCompat.startActivityForResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import android.support.annotation.NonNull
import android.app.Activity
import android.content.Context
import android.widget.Toast
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
        private val mAuth: FirebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance()
        private val mUser: FirebaseUser? = mAuth.currentUser

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

        fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
            mAuth.addAuthStateListener(listener)
        }


        fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
            mAuth.removeAuthStateListener(listener)
        }
        /**
         * get Firebase authentication manager
         * @return FirebaseAuth instance
         */
        fun getFirebaseAuth(): FirebaseAuth = mAuth


        /**
         * log out from current context
         * @param mContext: current context/activity
         */
        fun logOut(mContext: Context) = AuthUI.getInstance().signOut(mContext);


        /**
         * get the current user
         * @param firebaseAuth: Firebase authentication manager
         */
        fun getUser(): FirebaseUser? = mUser


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
                if (mUser != null) {
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

        fun addAuthStateListener () {

        }

        fun verifyPhone (phoneNumber: String,
                                 activity: Activity,
                                 callback: (String?, Boolean) -> Unit) {

            val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    if (mUser != null) {
                        Toast.makeText(activity, "ohhhhh", Toast.LENGTH_SHORT).show()
                        mUser.updatePhoneNumber(credential)
                        callback(null, true)
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.

                    if (e is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(activity, "invalid number", Toast.LENGTH_SHORT).show()
                        callback(null, false)
                    } else if (e is FirebaseTooManyRequestsException) {
                        Toast.makeText(activity, "too many requests", Toast.LENGTH_SHORT).show()
                        callback(null, false)
                    }
                }

                override fun onCodeSent(verificationId: String?,
                                        token: PhoneAuthProvider.ForceResendingToken?) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    Toast.makeText(activity, "good number", Toast.LENGTH_SHORT).show()
                    callback(verificationId, false)
                }
            }

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    activity,
                    mCallbacks)
        }

        fun updatePhone (verificationId: String?, code: String) {
            if (mUser != null) {
                val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
                mUser.updatePhoneNumber(credential)
            }
        }


    }
}