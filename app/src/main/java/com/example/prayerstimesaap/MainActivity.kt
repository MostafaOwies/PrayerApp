package com.example.prayerstimesaap

import android.os.Bundle
import androidx.activity.viewModels
import com.example.prayerstimesaap.databinding.ActivityMainBinding
import com.example.prayerstimesaap.ui.base.BaseActivity
import com.example.prayerstimesaap.ui.fragments.PrayersFragment
import com.example.prayerstimesaap.ui.fragments.PrayersViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private var binding: ActivityMainBinding? = null
    val viewModel:PrayersViewModel by viewModels()
    private val prayersFragment=PrayersFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.navHostFragment,prayersFragment)
            commit()
        }


    }
}