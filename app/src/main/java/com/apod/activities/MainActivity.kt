package com.apod.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.apod.R
import com.apod.fragments.HelpFragment
import com.apod.fragments.HomeFragment
import com.apod.fragments.SavedFragment
import com.apod.fragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val helpFragment = HelpFragment()
    private val searchFragment = SearchFragment()
    private val homeFragment = HomeFragment()
    private val savedFragment = SavedFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setFragment(homeFragment)


        //bottom nav
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)


        //bottom navigation listener, changes fragment based on selected item.
        bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.navigation_home -> setFragment(homeFragment)
                R.id.navigation_search -> setFragment(searchFragment)
                R.id.navigation_saved -> setFragment(savedFragment)
                R.id.navigation_help -> setFragment(helpFragment)

            }

            true

        }


    }


    //setFragment method
    //takes a fragment as a parameter, starts a transaction, and sets the frame layout to the fragment.
     private fun setFragment(fragment : Fragment){
            val transactionFragment = supportFragmentManager.beginTransaction()
            transactionFragment.replace(R.id.frame_layout, fragment)
            transactionFragment.commit()
        }
     }








