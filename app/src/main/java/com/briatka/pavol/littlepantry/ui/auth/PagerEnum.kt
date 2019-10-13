package com.briatka.pavol.littlepantry.ui.auth

import com.briatka.pavol.littlepantry.R

enum class PagerEnum(val layoutId: Int, val title: Int) {

    LOGIN(R.layout.pager_login_layout, R.string.login_pager_log_in_tab),
    REGISTER(R.layout.pager_register_layout, R.string.login_pager_register_tab)
}