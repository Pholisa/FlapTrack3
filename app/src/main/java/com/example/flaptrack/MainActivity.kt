package com.example.flaptrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSignUp = findViewById<Button>(R.id.btnRegister)
        val btnSignIn = findViewById<Button>(R.id.btnLogin)


        btnSignUp.setOnClickListener {
            // Code to execute when the button is clicked
            // Start the new activity here
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        btnSignIn.setOnClickListener {
            // Code to execute when the button is clicked
            // Start the new activity here
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}

//-------------------------------------ooo000EndOfFile000ooo----------------------------------------