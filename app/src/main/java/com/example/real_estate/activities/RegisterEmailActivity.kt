package com.example.real_estate.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import com.example.real_estate.MyUtils
import com.example.real_estate.R
import com.example.real_estate.databinding.ActivityRegisterEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterEmailActivity : AppCompatActivity() {
    //VIEW BINDING
    private lateinit var binding: ActivityRegisterEmailBinding
    //LOG TO SHOW LOGS ON LOGOUT
    private val TAG = "REGISTER_EMAIL_TAG"
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle toolbarBackBtn , go back
        binding.toolbarBackBtn.setOnClickListener {
            finish()
        }
        binding.haveAccountTv.setOnClickListener {
            finish()
        }
        binding.registerBtn.setOnClickListener {
            validateData()
            
        }
    }
    private var email = ""
    private var password = ""
    private var cpassword = ""

    //input data
    private fun validateData(){
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString()
        cpassword = binding.cPasswordEt.text.toString()

        Log.d(TAG, "validateData: Email: $email")
        Log.d(TAG, "validateData: Password: $password")
        Log.d(TAG, "validateData: Confirm Password: $cpassword")
        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            //email pattern is invalid show error
            binding.emailEt.error = "Invalid Email Pattern"
            binding.emailEt.requestFocus()
        } else if (password.isEmpty()){

            //if password is not entered show error
            binding.passwordEt.error = "Enter Password"
            binding.passwordEt.requestFocus()
        } else if (password != cpassword){

            //password and confirm password are not matching
            binding.cPasswordEt.error = "Password doesn't match"
            binding.cPasswordEt.requestFocus()
        } else {
            //all data is valid start signUp
            registerUser()

        }

    }

    private fun registerUser(){
        //show progress
        progressDialog.setMessage("Creating Account")
        progressDialog.show()

        //create account
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //user register success, we also need to save userinfo db

                Log.d(TAG, "registerUser: Register success")
                updateUserInfo()

            }
            .addOnFailureListener{e->
                //user register failed
                Log.e(TAG, "registerUser:", e)
                MyUtils.toast(this, "Failed due to ${e.message}")
                progressDialog.dismiss()

            }

    }
    private fun updateUserInfo(){
        //change progress dialog info
        progressDialog.setMessage("Saving user info...")

        //get current timestamp, e.g to show user reg date/time
        val timestamp = MyUtils.timestamp()
        val registerdUserEmail = firebaseAuth.currentUser!!.email
        val registeredUserUid = firebaseAuth.uid

        val hashMap = HashMap<String, Any>()
        hashMap["uid"] = "$registeredUserUid"
        hashMap["email"] = "$registerdUserEmail"
        hashMap["name"] = ""
        hashMap["timestamp"] = timestamp
        hashMap["phoneCode"] = ""
        hashMap["phoneNumber"] = ""
        hashMap["dob"] = ""
        hashMap["userType"] = MyUtils.USER_TYPE_EMAIL
        hashMap["token"] = ""

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("$registeredUserUid")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG,"updateUserInfo: Info saved")
                progressDialog.dismiss()

                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()

            }
            .addOnFailureListener{e->
                Log.e(TAG, "updateUserInfo:", e)
                MyUtils.toast(this, "Failed to savedue to${e.message}")
                progressDialog.dismiss()

            }


    }
}