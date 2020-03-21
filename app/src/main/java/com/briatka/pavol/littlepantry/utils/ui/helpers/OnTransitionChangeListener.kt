package com.briatka.pavol.littlepantry.utils.ui.helpers

import androidx.constraintlayout.motion.widget.MotionLayout

/**
 * A helper class to remove the boilerplate code from activity/ fragment where
 *  only onTransitionChange function is needed.
 *  */
abstract class OnTransitionChangedListener: MotionLayout.TransitionListener {

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
        //do nothing
    }

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
        //do nothing
    }

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
        //do nothing
    }
}