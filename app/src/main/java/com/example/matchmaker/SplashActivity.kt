package com.example.matchmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.matchmaker.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity:: class.java))
        }
        else {
            startActivity(Intent(this, MainActivity :: class.java))
            //check if the user has completed the process of providing image and name.
            //to do this, get the user object with uid
            //if the user object exists, then open MainActivity
            //else if the user object doesn't exist, open SignUpActivity
        }

        finish()
    }
}