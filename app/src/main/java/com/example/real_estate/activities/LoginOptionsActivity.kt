package com.example.real_estate.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.real_estate.LoginPhoneActivity
import com.example.real_estate.MyUtils
import com.example.real_estate.R
import com.example.real_estate.databinding.ActivityLoginOptionsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginOptionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginOptionsBinding
    private val TAG = "LOGIN_OPTIONS_TAG"
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private val LOGIN_GOOGLE_REQUEST_CODE = 102 // You can use any unique code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        // Configure Google SignIn
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions)

        binding.skipBtn.setOnClickListener {
            // Go back to the dashboard
            onBackPressed()
        }

        binding.loginGoogleBtn.setOnClickListener {
            beginLoginBtn()
        }

        binding.loginEmailBtn.setOnClickListener {
            startActivity(Intent(this, LoginEmailActivity::class.java))
        }

        //handle loginEmail click,start login phonePhone activity
        binding.loginPhoneBtn.setOnClickListener {
            startActivity(Intent(this,LoginPhoneActivity::class.java))
        }
    }


    private fun beginLoginBtn() {
        Log.d(TAG, "beginLoginBtn:")

        val googleSignInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(googleSignInIntent, LOGIN_GOOGLE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOGIN_GOOGLE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    Log.d(TAG, "googleSignInARL: AccountID: ${account.id}")

                    firebaseAuthWithGoogleAccount(account.idToken)
                } catch (e: ApiException) {
                    Log.e(TAG, "googleSignInARL: ApiException", e)
                    MyUtils.toast(this, "Failed signin due to ${e.message}")
                } catch (e: Exception) {
                    Log.e(TAG, "googleSignInARL: Exception", e)
                    MyUtils.toast(this, "Failed signin due to ${e.message}")
                }
            } else {
                // Cancelled from Google SignIn options/confirmation dialog
                Log.d(TAG, "googleSignInARL: Cancelled...!")
                MyUtils.toast(this, "Cancelled...!")
            }
        }
    }

    private fun firebaseAuthWithGoogleAccount(idToken: String?) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: $idToken")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                if (authResult.additionalUserInfo?.isNewUser == true) {
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Account Created...!")
                    updateUserInfoDb()
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener { e ->
                // Sign-in failed, show exception
                Log.e(TAG, "firebaseAuthWithGoogleAccount:", e)
                MyUtils.toast(this, "Failed signin due to ${e.message}")
            }
    }

    private fun updateUserInfoDb() {
        Log.d(TAG, "updateUserInfoDb: ")

        // Set message and show progress dialog
        progressDialog.setMessage("Saving user info...")
        progressDialog.show()

        val timestamp = MyUtils.timestamp()
        val registeredUserUid = firebaseAuth.uid.orEmpty()
        val registeredUserEmail = firebaseAuth.currentUser?.email.orEmpty()
        val name = firebaseAuth.currentUser?.displayName.orEmpty()

        val hashMap = HashMap<String, Any>().apply {
            put("uid", registeredUserUid)
            put("email", registeredUserEmail)
            put("name", name)
            put("timestamp", timestamp)
            put("phoneCode", "")
            put("phoneNumber", "")
            put("profileImageUrl", "")
            put("dob", "")
            put("userType", MyUtils.USER_TYPE_GOOGLE)
            put("token", "")
        }

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(registeredUserUid)
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "updateUserInfoDb: User Info saved...!")
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "updateUserInfoDb: ", e)
                progressDialog.dismiss()
                MyUtils.toast(this, "Failed to save due to ${e.message}")
            }
    }
}
