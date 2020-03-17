package com.briatka.pavol.littlepantry.utils

class AuthConstants {

    companion object {
        const val LOGIN_FLAG = "login_flag"
        const val REGISTER_FLAG = "register_flag"
        const val LOGIN_EMAIL_ERROR = "login_email_error"
        const val LOGIN_PASSWORD_ERROR = "login_password_error"
        const val LOGIN_USER_NOT_EXIST = "login_user_not_exist"
        const val REGISTER_USER_ALREADY_EXIST = "register_user_already_exist"
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_PICK_IMAGE_FROM_GALLERY = 2
        const val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 3
        const val PERMISSION_REQUEST_CAMERA = 4
        const val REQUEST_PHONE_STATE = 5
        const val DISPATCH_CAMERA_INTENT = 6
        const val DISPATCH_GALLERY_INTENT = 7
        const val HEADER_SCROLL_UP_COEFFICIENT = 0.7f
        const val HEADER_SCROLL_DOWN_COEFFICIENT = 1.43f
        const val FILE_PROVIDER_AUTHORITY = "com.briatka.pavol.fileprovider"
        //DB collection paths
        const val USERS_PATH = "users"
        const val CONTACT_INFO_PATH = "contact_info"
        const val PROFILE_PHOTO_REFERENCE_PATH = "profile_photo_reference"
        const val PROFILE_PHOTO_REFERENCE = "profilePhotoReference"
    }
}