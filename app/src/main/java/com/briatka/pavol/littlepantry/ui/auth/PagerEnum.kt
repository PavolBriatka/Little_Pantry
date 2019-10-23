package com.briatka.pavol.littlepantry.ui.auth

import com.briatka.pavol.littlepantry.R

enum class PagerEnum(val layoutId: Int, val title: Int) {

    LOGIN(R.layout.pager_login_layout, R.string.log_in_label),
    REGISTER(R.layout.pager_register_layout, R.string.register_label)
}