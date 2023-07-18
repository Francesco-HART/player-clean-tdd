package com.example.musicapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.musicapp.login.SharedPref
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {





        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        setupWithNavController(bottomNavigationView, navController)

        val isLightMode = SharedPref().getValue(SharedPref.Keys.APP_THEME.name, true.toString()) == true.toString()
        AppCompatDelegate.setDefaultNightMode(
            if(isLightMode) AppCompatDelegate.MODE_NIGHT_NO
            else AppCompatDelegate.MODE_NIGHT_YES
        )



        // Disable navbar on login fragment
        navController.addOnDestinationChangedListener{_, destination,_ ->
            if(destination.id == R.id.loginFragment){
                bottomNavigationView.visibility = View.GONE
            }else{
                bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }
}
