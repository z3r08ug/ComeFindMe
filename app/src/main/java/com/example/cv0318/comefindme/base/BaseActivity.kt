package com.example.cv0318.comefindme.base

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

abstract class BaseActivity: AppCompatActivity() {
    lateinit var mToolbar: Toolbar
    lateinit var mLoadingBar: ProgressDialog
    private lateinit var mAuth: FirebaseAuth
    lateinit var mUsersRef: DatabaseReference
    lateinit var mUserProfilePicRef: StorageReference
    lateinit var mUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        mAuth = FirebaseAuth.getInstance()
        mUserId = mAuth.currentUser!!.uid
        mUsersRef = FirebaseDatabase.getInstance().reference.child("Users").child(mUserId)
        mUserProfilePicRef = FirebaseStorage.getInstance().reference.child("profile_pic")
        mLoadingBar = ProgressDialog(this)
    }
}