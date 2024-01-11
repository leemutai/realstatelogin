package com.example.real_estate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private  lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser == null){
            startLoginOptionsActivity()
        }
    }
    private fun startLoginOptionsActivity(){
        startActivity(Intent(this, LoginOptionsActivity::class.java))
    }
}