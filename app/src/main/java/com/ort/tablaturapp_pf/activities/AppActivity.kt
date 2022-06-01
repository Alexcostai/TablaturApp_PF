package com.ort.tablaturapp_pf.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.fragments.app.HomeFragment
import com.ort.tablaturapp_pf.fragments.app.HomeFragmentDirections
import com.ort.tablaturapp_pf.fragments.app.LearningPathFragment
import com.ort.tablaturapp_pf.fragments.app.SearchFragment
import com.ort.tablaturapp_pf.viewmodels.LearningPathViewModel

class AppActivity : AppCompatActivity() {

    private lateinit var bottomNavigation : BottomNavigationView
    private lateinit var topAppBar : NavigationBarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemReselectedListener { item ->
            when(item.itemId) {
                R.id.home_page_menu -> setCurrentFragment(HomeFragment())
                R.id.search_page_menu -> setCurrentFragment(SearchFragment())
                R.id.learning_path_page_menu -> setCurrentFragment(LearningPathFragment())
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.navAppController,fragment)
            commit()
        }



}