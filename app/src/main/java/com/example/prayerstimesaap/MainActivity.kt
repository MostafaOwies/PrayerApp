package com.example.prayerstimesaap

import android.os.Bundle
import androidx.activity.viewModels
import com.example.prayerstimesaap.ui.base.BaseActivity
import com.example.prayerstimesaap.ui.fragments.PrayersFragment
import com.example.prayerstimesaap.ui.fragments.PrayersViewModel


class MainActivity : BaseActivity() {
    val viewModel:PrayersViewModel by viewModels()
    private val prayersFragment=PrayersFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }
}