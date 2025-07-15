package com.example.playlistmaker.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)



        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.searchFragment -> navigateWithAnimation(R.id.searchFragment)
                R.id.mediaFragment -> navigateWithAnimation(R.id.mediaFragment)
                R.id.settingFragment -> navigateWithAnimation(R.id.settingFragment)
                else -> false
            }
            true
        }
        setupBottomNavigation()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun navigateWithAnimation(destinationId: Int): Boolean {
        if (navController.currentDestination?.id == destinationId) return false

        val navOrder = listOf(R.id.searchFragment, R.id.mediaFragment, R.id.settingFragment)
        val currentPos = navOrder.indexOf(navController.currentDestination?.id)
        val destPos = navOrder.indexOf(destinationId)
        val isForward = destPos > currentPos

        val options = NavOptions.Builder().apply {
            setLaunchSingleTop(true)
            if (isForward) {
                setEnterAnim(R.anim.slide_in_right)
                setExitAnim(R.anim.slide_out_left)
            } else {
                setEnterAnim(R.anim.slide_in_left)
                setExitAnim(R.anim.slide_out_right)
            }
        }.build()

        navController.navigate(destinationId, null, options)
        return true
    }

    private fun setupBottomNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment

        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.infoPlaylist -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
                R.id.createPlaylistFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
    }

}

