package com.briatka.pavol.littlepantry.ui.main.fragments


import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.models.NewUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_posts.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class PostsFragment : DaggerFragment() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var  fireStoreDb: FirebaseFirestore

    @Inject
    lateinit var firebaseStorage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = firebaseAuth.currentUser

        if (user != null) {
            val userDb = fireStoreDb.collection("users").document(user.uid)
            val photoReference = userDb.collection("profile_photo_reference").document(user.uid)
            userDb.get()
                .addOnSuccessListener {
                    val data = it.toObject(NewUser::class.java)
                    tv_first_name.text = data?.firstName ?: "error"
                    tv_surname.text = data?.surname ?: "error"
                    tv_nickname.text = data?.nickname ?: "error"
                }
            photoReference.get()
                .addOnSuccessListener {
                    val data = it.toObject(HashMap::class.java)
                    setProfileImage(user.uid, data?.get("photoReference") as String)
                }

        }
    }

    private fun setProfileImage(uid: String, photoRefr: String) {
        val photoRef = firebaseStorage.reference.child("$uid/profile_photo/$photoRefr")
         photoRef.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmapPhoto = BitmapFactory.decodeByteArray(it, 0, it.size)
            iv_profile_photo.setImageBitmap(bitmapPhoto)
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "${it.localizedMessage} + $photoRef", Toast.LENGTH_SHORT).show()
        }
    }
}
