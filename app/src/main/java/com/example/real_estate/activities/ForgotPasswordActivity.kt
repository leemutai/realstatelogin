package com.example.real_estate.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import com.example.real_estate.MyUtils
import com.example.real_estate.R
import com.example.real_estate.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    //view binding
    private lateinit var binding: ActivityForgotPasswordBinding
    //TAG for logs in logcat
    private val TAG = "FORGOT_PASSWORD_TAG"

    //progressdialog to show while sending password recovery instructions
    private lateinit var progressDialog: ProgressDialog

    //firebase auth for auth related tasks
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init view binding..activity_forgot_password.xml = ActivityForgotPasswordBinding
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init /setup progressdialog to show sending password recovery instructions
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //firebase auth for auth related tasks
        firebaseAuth = FirebaseAuth.getInstance()
        //handle toolbarbackbtn click, go back
        binding.toolbarBackBtn.setOnClickListener {
            finish()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }
    private var email = ""
    private fun validateData(){
        email = binding.emailEt.text.toString().trim()
        Log.d(TAG,"validateData: Email: $email")

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.error = "Invalid Email Pattern!"
            binding.emailEt.requestFocus()
        } else {
            sendPasswordRecoveryInstructions()
        }
    }
    private fun sendPasswordRecoveryInstructions(){
        Log.d(TAG,"sendPasswordRecoveryInstructions")
        //SHOW PROGRESS
        progressDialog.setMessage("Sending password recovery instructions to $email")
        progressDialog.show()

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Log.d(TAG,"sendPasswordRecoveryInstructions: Instructions sent to $email")
                progressDialog.dismiss()
                MyUtils.toast(this,"Instructions to reset password sent to $email")
            }
            .addOnFailureListener {e->

                Log.e(TAG,"sendPasswordRecoveryInstructions: ", e)
                progressDialog.dismiss()
                MyUtils.toast(this,"Failed to send due to ${e.message}")
            }
    }
}