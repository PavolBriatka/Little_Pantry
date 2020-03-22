package com.briatka.pavol.littlepantry.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ListView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.briatka.pavol.littlepantry.ConnectivityLiveData
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.main.adapters.DrawerMenuAdapter
import com.briatka.pavol.littlepantry.ui.main.viewmodel.FirebaseAuthLiveData
import com.briatka.pavol.littlepantry.ui.main.viewmodel.MainViewModel
import com.briatka.pavol.littlepantry.utils.ui.helpers.OnTransitionChangedListener
import com.briatka.pavol.littlepantry.viewmodels.ViewModelsProviderFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_header.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var mainNavController: NavController
    private lateinit var profileNavController: NavController
    private lateinit var connectivitySnackbar: Snackbar
    private val disposables = CompositeDisposable()


    @Inject
    lateinit var viewModelFactory: ViewModelsProviderFactory
    @Inject
    lateinit var firebaseAuthStatus: FirebaseAuthLiveData
    @Inject
    lateinit var connectivityStatus: ConnectivityLiveData
    @Inject
    lateinit var drawerMenuItems: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.fetchUserData()

        mainNavController = this.findNavController(R.id.main_nav_host_fragment)
        profileNavController = this.findNavController(R.id.side_nav_host_fragment)
        ml_main_activity.setTransitionListener(createTransitionListener())

        connectivitySnackbar = Snackbar.make(
            main_drawer_layout,
            getString(R.string.snackbar_lost_connection_message),
            Snackbar.LENGTH_INDEFINITE
        )

        initDrawer()
        subscribeToUserState()
        subscribeToConnectivityStatus()
    }

    private fun createTransitionListener(): OnTransitionChangedListener {
        return object : OnTransitionChangedListener() {
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, progress: Float) {
                viewModel.transitionProgress.onNext(progress)
                btn_inbox.alpha = 1 - progress
                btn_my_posts.alpha = 1 - progress
            }
        }
    }

    override fun onStart() {
        super.onStart()
        disposables.addAll(
            subscribeToUserData()
        )
    }

    override fun onStop() {
        disposables.clear()
        super.onStop()
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeToUserData(): Disposable {
        return viewModel.userData.hide()
            .observeOn(AndroidSchedulers.mainThread())
            .firstElement()
            .subscribe {
                tv_header_nickname.text = "(${it.nickname})"
                tv_header_user_name.text = "${it.firstName} ${it.surname}"
            }
    }

    private fun subscribeToConnectivityStatus() {
        connectivityStatus.observe(this, Observer<Boolean> { isConnected ->
            if (isConnected) {
                connectivitySnackbar.dismiss()
            } else {
                connectivitySnackbar.show()
            }
        })
    }

    private fun subscribeToUserState() {
        firebaseAuthStatus.observe(this, Observer<FirebaseUser> { user ->
            if (user == null)
                mainNavController.navigate(R.id.action_open_auth_activity)

        })
    }

    private fun initDrawer() {
        NavigationUI.setupActionBarWithNavController(this, mainNavController, main_drawer_layout)
        NavigationUI.setupWithNavController(main_navigation_view, mainNavController)

        val listView = findViewById<ListView>(R.id.rl_drawer_menu_items)
        val adapter = DrawerMenuAdapter().also {
            it.setMenuItems(drawerMenuItems)
        }

        listView.adapter = adapter
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, _, id ->
                when (id) {
                    0L -> {
                        mainNavController.navigate(R.id.action_open_profile)
                        main_drawer_layout.closeDrawers()
                    }
                    1L -> {/*nav to inbox*/
                    }
                    2L -> {
                        viewModel.logout()
                        finish()
                    }
                }
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            mainNavController,
            main_drawer_layout
        )
    }
}
