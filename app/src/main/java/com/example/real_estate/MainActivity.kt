package com.example.real_estate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Activity main binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //by default (when app open ) show homeFragment
        showBlankHomeFragment()

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        // Check if the user is not logged in, then start the login options activity
        if (firebaseAuth.currentUser == null) {
            startLoginOptionsActivity()
        }

        // Handle bottomNavigationView item clicks to navigate between fragments
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            // Get id of the menu item clicked
            val itemId = menuItem.itemId

            // Check which item is clicked and show fragment accordingly
            when (itemId) {
                R.id.item_home -> {
                    // Handle home fragment
                    showBlankHomeFragment()
                }
                R.id.item_chats -> {
                    // Handle chats fragment
                    showChatsListFragment()
                }
                R.id.item_favorite -> {
                    // Handle favorite fragment
                    showFavoriteListFragment()
                }
                R.id.item_profile -> {
                    // Handle profile fragment
                    showProfileListFragment()
                }
            }

            // Return true to indicate the item is selected
            true
        }
    }

    private fun showBlankHomeFragment(){
        binding.toolbarTitleTv.text = "Home"

        val blankHomeFragment = BlankHomeFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, blankHomeFragment, "Home")
        fragmentTransaction.commit()
    }

    private fun showChatsListFragment(){
        binding.toolbarTitleTv.text = "Chats"

        val chatsListFragment = ChatsListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, chatsListFragment, "ChatsListFragment")
        fragmentTransaction.commit()
    }

    private fun showFavoriteListFragment(){
        binding.toolbarTitleTv.text = "Favorites"

        val favoriteListFragment = FavoriteListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, favoriteListFragment, "FavoriteListFragment")
        fragmentTransaction.commit()
    }

    private fun showProfileListFragment(){
        binding.toolbarTitleTv.text = "Profile"

        val profileFragment = ProfileFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, profileFragment, "ProfileFragment")
        fragmentTransaction.commit()
    }






    private fun startLoginOptionsActivity() {
        // Start the login options activity
        startActivity(Intent(this, LoginOptionsActivity::class.java))
        // Finish the current activity
        finish()
    }
}
