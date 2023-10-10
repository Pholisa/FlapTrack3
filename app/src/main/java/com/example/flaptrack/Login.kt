package com.example.flaptrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val  btnSignIn = findViewById<Button>(R.id.btnLogin)

        btnSignIn.setOnClickListener {
            // Code to execute when the button is clicked
            // Start the new activity here
            val intent = Intent(this, BirdsUi::class.java)
            startActivity(intent)
        }
    }
}