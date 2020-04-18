package com.briatka.pavol.littlepantry.models

import android.graphics.Bitmap

data class DisplayableUser(
     var uFirstName: String = "",
     var uSurname: String = "",
     var uNickname: String = "",
     var uProfilePhoto: Bitmap? = null,
     var uPhoneNumber: String = "",
     var uAddressLine: String = "",
     var uCity: String = "",
     var uZipCode: String = "",
     var uCountry: String = "",
     var uEmail: String = "",
     var uPassword: String =""
 )