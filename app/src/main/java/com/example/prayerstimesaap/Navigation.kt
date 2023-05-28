package com.example.prayerstimesaap

import android.os.Bundle
import androidx.navigation.NavController

class Navigation {

    companion object {
        fun navigate(navController: NavController, id: Int, bundle: Bundle? = null) {
            navController.navigate(id, bundle)

        }

        fun navToPrayersFragment(navController: NavController,bundle: Bundle?=null) {
            navController.popBackStack(navController.graph.startDestinationId, false)
            navController.popBackStack(R.id.prayersFragment,false)
        }

        fun navToQiblaFragment(navController: NavController,bundle: Bundle?=null){
            navController.popBackStack(R.id.prayersFragment,false)
            navigate(navController,R.id.qiblaFragmnet)
        }

    }
}