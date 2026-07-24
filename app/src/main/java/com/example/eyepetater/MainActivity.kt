package com.example.eyepetater

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.ept.community.CommunityFragment
import com.example.ept.daily.DailyFragment
import com.example.ept.dicover.discovery.DiscoveryFragment
import com.example.ept.home.fragment.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.view_pager)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        setupViewPager()
        setupBottomNavigation()

        // Apply window insets to the root layout
        val root = findViewById<android.view.View>(R.id.fragment_container)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        // Disable user scrolling (optional, remove if you want swipe between tabs)
        viewPager.isUserInputEnabled = false

        // Set initial page to DiscoveryFragment (index 2)
        viewPager.setCurrentItem(2, false)
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    viewPager.setCurrentItem(0, false)
                    true
                }
                R.id.nav_daily -> {
                    viewPager.setCurrentItem(1, false)
                    true
                }
                R.id.nav_discovery -> {
                    viewPager.setCurrentItem(2, false)
                    true
                }
                R.id.nav_community -> {
                    viewPager.setCurrentItem(3, false)
                    true
                }
                else -> false
            }
        }

        // Set initial selected item
        bottomNavigation.selectedItemId = R.id.nav_discovery
    }

    private inner class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment()
                1 -> DailyFragment()
                2 -> DiscoveryFragment()
                3 -> CommunityFragment()
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}
