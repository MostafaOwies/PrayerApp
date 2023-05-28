package com.example.prayerstimesaap

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.prayerstimesaap.databinding.ActivityMainBinding
import com.example.prayerstimesaap.ui.base.BaseActivity
import com.example.prayerstimesaap.ui.fragments.prayersfrag.PrayersFragment
import com.example.prayerstimesaap.ui.fragments.prayersfrag.PrayersViewModel
import com.example.prayerstimesaap.ui.fragments.qiblafrag.QiblaViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : BaseActivity() {

    private var binding: ActivityMainBinding? = null
    val prayersViewModel: PrayersViewModel by viewModels()
    val qiblaViewModel: QiblaViewModel by viewModels()
    private val prayersFragment= PrayersFragment()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupNavigation()

    }

    private fun setupNavigation() {

        navController = findNavController(R.id.navHostFragment)
        binding?.bottomNavigation?.setupWithNavController(navController)
        binding?.bottomNavigation?.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.prayersFragment -> {
                    Navigation.navToPrayersFragment(navController)
                }

                R.id.qiblaFragmnet -> {
                    Navigation.navToQiblaFragment(navController)
                }
            }
            false
        }
    }
}