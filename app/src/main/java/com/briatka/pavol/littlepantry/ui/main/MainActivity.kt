package com.briatka.pavol.littlepantry.ui.main

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.main.viewmodel.MainViewModel
import com.briatka.pavol.littlepantry.viewmodels.ViewModelsProviderFactory
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseUser
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), OnNavigationItemSelectedListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var navController: NavController


    @Inject
    lateinit var viewModelFactory: ViewModelsProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        navController = this.findNavController(R.id.main_nav_host_fragment)

        init()
        subscribeToUserState()
    }

    private fun subscribeToUserState() {
        viewModel.firebaseAuthLiveData.observe(this, Observer<FirebaseUser> { user ->
            if (user == null) {
                navController.navigate(R.id.action_open_auth_activity)
            } else {
                Toast.makeText(this, "Logged in", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun init() {
        NavigationUI.setupActionBarWithNavController(this, navController, main_drawer_layout)
        NavigationUI.setupWithNavController(main_navigation_view, navController)
        main_navigation_view.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_profile -> {
            }
            R.id.action_logout -> {
                viewModel.logout()
            }
        }

        menuItem.isChecked = true
        main_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            navController,
            main_drawer_layout
        )
    }
}
