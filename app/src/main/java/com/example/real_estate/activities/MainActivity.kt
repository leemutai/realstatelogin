package com.example.real_estate.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.real_estate.MyUtils
import com.example.real_estate.R
import com.example.real_estate.databinding.ActivityMainBinding
import com.example.real_estate.fragments.BlankHomeFragment
import com.example.real_estate.fragments.ChatsListFragment
import com.example.real_estate.fragments.FavoriteListFragment
import com.example.real_estate.fragments.ProfileFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    // View binding
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private val LOGIN_REQUEST_CODE = 101 // You can use any unique code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Activity main binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // by default (when app open) show homeFragment
        showBlankHomeFragment()

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        // Check if the user is not logged in, then start the login options activity
        if (firebaseAuth.currentUser == null) {
            startLoginOptionsActivity()
        }

        // Handle bottomNavigationView item clicks to navigate between fragments
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            // Get id of the menu item clicked
            val itemId = menuItem.itemId

            // Check which item is clicked and show fragment accordingly
            when (itemId) {
                R.id.item_home -> {
                    // Handle home fragment
                    showBlankHomeFragment()
                    return@setOnItemSelectedListener true

                }
                R.id.item_chats -> {
                    // Handle chats fragment


                    if(firebaseAuth.currentUser == null){
                        MyUtils.toast(this,"Login Required...")
                        return@setOnItemSelectedListener false
                    } else{
                        showChatsListFragment()
                        return@setOnItemSelectedListener true
                    }
                }
                R.id.item_favorite -> {
                    // Handle favorite fragment


                    if(firebaseAuth.currentUser == null){
                        MyUtils.toast(this,"Login Required...")
                        return@setOnItemSelectedListener false
                    } else{
                        showFavoriteListFragment()
                        return@setOnItemSelectedListener true
                    }
                }
                R.id.item_profile -> {
                    // Handle profile fragment


                    if(firebaseAuth.currentUser == null){
                        MyUtils.toast(this,"Login Required...")
                        return@setOnItemSelectedListener false
                    } else{
                        showProfileListFragment()
                        return@setOnItemSelectedListener true
                    }
                }
                else ->{
                    return@setOnItemSelectedListener false
                }
            }

            // Return true to indicate the item is selected
        }
    }

    private fun showBlankHomeFragment() {
        binding.toolbarTitleTv.text = "Home"

        val blankHomeFragment = BlankHomeFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, blankHomeFragment, "Home")
        fragmentTransaction.commit()
    }

    private fun showChatsListFragment() {
        binding.toolbarTitleTv.text = "Chats"

        val chatsListFragment = ChatsListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, chatsListFragment, "ChatsListFragment")
        fragmentTransaction.commit()
    }

    private fun showFavoriteListFragment() {
        binding.toolbarTitleTv.text = "Favorites"

        val favoriteListFragment = FavoriteListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, favoriteListFragment, "FavoriteListFragment")
        fragmentTransaction.commit()
    }

    private fun showProfileListFragment() {
        binding.toolbarTitleTv.text = "Profile"

        val profileFragment = ProfileFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, profileFragment, "ProfileFragment")
        fragmentTransaction.commit()
    }

    private fun startLoginOptionsActivity() {
        // Start the login options activity with startActivityForResult
        startActivityForResult(Intent(this, LoginOptionsActivity::class.java), LOGIN_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the result is from the LoginOptionsActivity
        if (requestCode == LOGIN_REQUEST_CODE) {
            // Check if the login was successful or not (you can customize this based on your logic)
            if (resultCode == RESULT_OK) {
                // Handle successful login
            } else {
                // Handle unsuccessful login or skip
                // For now, we'll just show the home fragment
                showBlankHomeFragment()
            }
        }
    }
}
