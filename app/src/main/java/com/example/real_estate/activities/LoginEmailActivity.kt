package com.example.real_estate.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import com.example.real_estate.MyUtils
import com.example.real_estate.R
import com.example.real_estate.databinding.ActivityLoginEmailBinding
import com.google.firebase.auth.FirebaseAuth

class LoginEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginEmailBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private var TAG = "LOGIN_EMAIL_TAG"
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.toolbarBackBtn.setOnClickListener {
            finish()
        }

        binding.loginBtn.setOnClickListener {
            validateData()
        }
        binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this, RegisterEmailActivity::class.java))
        }

        //handle forgot passwordTv click, open ForgotPasswordActivity to send password recovery intructions to registered email
        binding.forgotPasswordTv.setOnClickListener {
            startActivity(Intent(this,ForgotPasswordActivity::class.java))

        }
    }

    private fun validateData() {
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString()

        Log.d(TAG, "validateData: Email: $email")
        Log.d(TAG, "validateData: Password: $password")

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEt.error = "Invalid Email"
            binding.emailEt.requestFocus()
        } else if (password.isEmpty()) {
            binding.passwordEt.error = "Enter Password"
            binding.passwordEt.requestFocus()
        } else {
            loginUser()
        }
    }

    private fun loginUser() {
        if (email.isEmpty() || password.isEmpty()) {
            MyUtils.toast(this, "Email or password cannot be empty")
            return
        }

        progressDialog.setMessage("Logging In...")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d(TAG, "loginUser: Logged In...")
                progressDialog.dismiss()

                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "loginUser :", e)
                progressDialog.dismiss()
                MyUtils.toast(this, "Failed due to ${e.message}")
            }
    }
}
