package com.briatka.pavol.littlepantry.ui.main

import android.os.Bundle
import android.view.WindowManager
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.briatka.pavol.littlepantry.ConnectivityLiveData
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.main.viewmodel.FirebaseAuthLiveData
import com.briatka.pavol.littlepantry.ui.main.viewmodel.MainViewModel
import com.briatka.pavol.littlepantry.utils.ui.helpers.OnTransitionChangedListener
import com.briatka.pavol.littlepantry.viewmodels.ViewModelsProviderFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var mainNavController: NavController
    private lateinit var profileNavController: NavController
    private lateinit var connectivitySnackbar: Snackbar


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

        /**The app focuses on the edit text in Profile fragment when it starts
         * and opens the keyboard. Since the fragment is neither fully visible nor in the edit mode
         * this is an undesirable behaviour that is prevented by keeping keyboard closed on start.
         * */
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.fetchUserData()

        mainNavController = this.findNavController(R.id.main_nav_host_fragment)
        profileNavController = this.findNavController(R.id.side_nav_host_fragment)
        ml_main_activity.setTransitionListener(createTransitionListener())

        connectivitySnackbar = Snackbar.make(
            ml_main_activity,
            getString(R.string.snackbar_lost_connection_message),
            Snackbar.LENGTH_INDEFINITE
        )

        subscribeToUserState()
        subscribeToConnectivityStatus()
    }

    private fun createTransitionListener(): OnTransitionChangedListener {
        return object : OnTransitionChangedListener() {
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, progress: Float) {
                viewModel.transitionProgress.onNext(progress)
                /**As the motion progresses the buttons should fade in/out accordingly.*/
                btn_inbox.alpha = 1 - progress
                btn_my_posts.alpha = 1 - progress
            }
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
}
