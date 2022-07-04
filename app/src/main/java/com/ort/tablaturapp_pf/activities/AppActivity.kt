package com.ort.tablaturapp_pf.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.fragments.app.HomeFragment
import com.ort.tablaturapp_pf.fragments.app.LearningPathFragment
import com.ort.tablaturapp_pf.fragments.app.SearchFragment

class AppActivity : AppCompatActivity() {

    private lateinit var bottomNavigation : BottomNavigationView
    private lateinit var appNav: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        bottomNavigation = findViewById(R.id.bottom_navigation)
        appNav = supportFragmentManager.findFragmentById(R.id.navAppController) as NavHostFragment

        NavigationUI.setupWithNavController(bottomNavigation, appNav.navController)

    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.navAppController,fragment)
            commit()
        }

}