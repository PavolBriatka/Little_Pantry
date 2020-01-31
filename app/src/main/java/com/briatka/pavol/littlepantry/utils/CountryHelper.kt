package com.briatka.pavol.littlepantry.utils

import android.content.Context
import com.briatka.pavol.littlepantry.R

class CountryHelper(context: Context) {

    private val phoneCodes = hashMapOf(
        "gb" to "+44",
        "sk" to "+421"
    )

    private val countries = hashMapOf(
        "gb" to context.getString(R.string.country_gb),
        "sk" to context.getString(R.string.country_svk)
    )

    fun getPhoneCodeFromCountryCode(code: String): String {
        return phoneCodes[code] ?: ""
    }

    fun getCountryFromCountryCode(code: String): String {
        return countries[code] ?: ""
    }
}