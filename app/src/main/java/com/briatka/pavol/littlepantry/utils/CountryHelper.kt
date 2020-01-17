package com.briatka.pavol.littlepantry.utils

object CountryHelper {

    private val currencies = hashMapOf(
        "gb" to "+44",
        "sk" to "+421"
    )

    fun getCurrencySymbolFromCode(code: String): String {
        return currencies[code] ?: ""
    }
}