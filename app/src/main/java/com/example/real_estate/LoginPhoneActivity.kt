package com.example.real_estate

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.real_estate.activities.MainActivity
import com.example.real_estate.databinding.ActivityLoginPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit

class LoginPhoneActivity : AppCompatActivity() {
    //VIEW binding
    private lateinit var binding:ActivityLoginPhoneBinding
    //tag to show logs in logcat
    private val TAG = "LOGIN_PHONE_TAG"
    //progressdialog to show while phonelogin, savng user info
    private lateinit var progessDialog: ProgressDialog
    //firebase auth for auth related tasks
    private lateinit var firebaseAuth: FirebaseAuth

    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    private lateinit var mCallBacks: OnVerificationStateChangedCallbacks

    private var mVerificationId:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //activity phone loging.xml = ActivityLoginPhoneBinding
        binding = ActivityLoginPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //for the start show phone input ui and hide otp ui
        binding.phoneInputRl.visibility = View.VISIBLE
        binding.otpInputRl.visibility = View.GONE

        //init/setup progressDialog to show while login
        progessDialog = ProgressDialog(this)
        progessDialog.setTitle("please wait")
        progessDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        phoneLoginCallBack()

        //HABDLE TOOOLBARBACK clicks, go back
        binding.toolbarBackBtn.setOnClickListener {
            finish()
        }
        //handle toolbarBackBtn click , go back
        binding.sendOtpBtn.setOnClickListener {
            validateData()
        }
        //handle resendOtpTv click , resend OTP
        binding.resendOtpTv.setOnClickListener {
            //first check if force resending token is null or not
            if (forceResendingToken != null){
                //force resending token is not null, we can resend OTP
                resendVerificationCode()
            } else {
                //Force resending token is null , may be due to internet connection issue
                MyUtils.toast(this,"Can't resend OTP! Try again...!")
            }
        }
        //handle veriftyOtBtn click, verify OTP received
        binding.verifyOtpBtn.setOnClickListener {
            //input OTP
            val otp = binding.otpEt.text.toString().trim()
            //VALIDATE if otp is entered and length is 6 characters

            if (otp.isEmpty()){
                //otp is empty show error
                binding.otpEt.error = "Enter OTP"
                binding.otpEt.requestFocus()
            } else if (otp.length < 6){
                //otp length is less than 6 characters, show error
                binding.otpEt.error = "OTP length must be 6 characters"
                binding.otpEt.requestFocus()
            } else {
                //data is valid startVerification
                verifyPhoneNumberWithCode(otp)
            }
        }
    }
    private var phoneCode = ""
    private var phoneNumber = ""
    private var phoneNumberWithCode = ""
    private fun validateData(){
        phoneCode = binding.phoneCodeEt.selectedCountryCodeWithPlus
        phoneNumber = binding.phoneNumberEt.text.toString().trim()
        phoneNumberWithCode = phoneCode + phoneNumber

        Log.d(TAG,"validateData:Phone Code $phoneCode")
        Log.d(TAG,"validateData:Phone Number $phoneNumber")
        Log.d(TAG,"validateData:Phone Number With Code $phoneNumberWithCode")

        if (phoneNumber.isEmpty()){
            binding.phoneNumberEt.error = "Enter Phone Number"
            binding.phoneNumberEt.requestFocus()
        }else {

        }
    }
    private fun startPhoneNumberVerification(){
        //show progress
        progessDialog.setMessage("Sending OTP to $phoneNumberWithCode")
        progessDialog.show()
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumberWithCode)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBacks)
            .build()
        //start phone verification with reauthorizations
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendVerificationCode(){
        progessDialog.setMessage("Sending OTP to $phoneNumberWithCode")
        progessDialog.show()
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumberWithCode)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBacks)
            .setForceResendingToken(forceResendingToken!!)
            .build()
        //start phone verification with reauthorizations
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(otp:String){
        Log.d(TAG,"verifyPhoneNumberWithCode: OTP: $otp")
        progessDialog.setMessage("verifying OTP...")
        progessDialog.show()

        val credential = PhoneAuthProvider.getCredential(mVerificationId!!, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun phoneLoginCallBack(){
        mCallBacks = object :OnVerificationStateChangedCallbacks(){

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG,"onCodeSent:")
                //the sms code verification has been sent to the provided phone number
                //now need to ask the user to enter the code and then construct a credential
                // by combining the code with verification ID.

                //Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                forceResendingToken = token

                progessDialog.dismiss()

                binding.phoneInputRl.visibility = View.GONE
                binding.otpInputRl.visibility = View.VISIBLE
                //show toast for success sending OTP
                MyUtils.toast(this@LoginPhoneActivity,"OTP sent $phoneNumberWithCode")
                //show a message that please type the verification code sent to the phone number user has input
                binding.loginPhoneLabelTv.text = "please type verification sent to $phoneNumberWithCode"
            }
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                //this callback will be invoked in two situations:
                //1 - Instant verifications . In some cases the phone number can be instantly
                //  verified without needing to send or enter a verification code
                //2 - Auto-retrieval . On some devices Google play services can automatically
                //  detect the incoming verification SMS and perform verification without user action
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }
            override fun onVerificationFailed(e: FirebaseException) {
                Log.e(TAG,"onVerificationFailed",e)
                //this callback is invoked if an invalid request for verification is made
                //for instance if the phone numbet format is not valid
                progessDialog.dismiss()
                MyUtils.toast(this@LoginPhoneActivity,"Failed to verify due to ${e.message}")

            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential){
        Log.d(TAG,"signInWithPhoneAuthCredential: ")

        //shw progress
        progessDialog.setMessage("Logging In...")
        progessDialog.show()
        //sign in to firebase autj using phone credentials
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {authResult ->
                //signin success, lets check if the user is new(New Acc Register) or existing (existing login)
                Log.d(TAG,"signInWithPhoneAuthCredential: ")

                if (authResult.additionalUserInfo!!.isNewUser){
                    Log.d(TAG,"signInWithPhoneAuthCredential: Account created...")
                    //New user, Account created No need to save user info to firebase realtime database , start Main Activity

                    updateUserInfo()
                } else {
                    Log.d(TAG,"signInWithPhoneAuthCredential: Logged In")
                    startActivity(Intent(this,MainActivity::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener {e->
                Log.e(TAG,"signInWithPhoneAuthCredential: ",e)
                progessDialog.dismiss()
                MyUtils.toast(this,"Login Failed due to ${e.message}")
            }
    }

    private fun updateUserInfo(){
        Log.d(TAG,"updateUserInfo: ")

        //show progress
        progessDialog.setMessage("Saving User Info...")
        progessDialog.show()

        val timestamp = MyUtils.timestamp()
        val registeredUserUid = firebaseAuth.uid

    }
}








