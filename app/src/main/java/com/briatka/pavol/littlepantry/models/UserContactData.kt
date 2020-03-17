package com.briatka.pavol.littlepantry.models

data class UserContactData(
    val phoneNumber: String = "",
    val addressLine: String = "",
    val city: String = "",
    val zipCode: String = "",
    val country: String = "")