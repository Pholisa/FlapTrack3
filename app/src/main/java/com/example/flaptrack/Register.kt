package com.example.flaptrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.flaptrack.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuthentication: FirebaseAuth
    private val theDatabase = Firebase.database
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuthentication = FirebaseAuth.getInstance()
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val myReference = userID?.let {
            theDatabase.getReference("users").child(it).child("Personal Details")
        }

        //Logging in text view set on click listener
        var login = findViewById<TextView>(R.id.tvLoginRedirectText2)
        login.setOnClickListener {
            val loginIntent = Intent(this, Login::class.java)
            startActivity(loginIntent)
        }

        //Signing up
        var signUp = findViewById<Button>(R.id.btnSignUp)
        signUp.setOnClickListener {
            val username = binding.edEmail.text.toString()
            val surname = binding.edlastName.text.toString()
            val name = binding.edFirstname.text.toString()
            val age = binding.edAge.text.toString()
            val password = binding.edPassword.text.toString()
            val confirmPassword = binding.edPasswordConfirm.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty())
            {
                if (password == confirmPassword)
                {
                    firebaseAuthentication.createUserWithEmailAndPassword(username, password).addOnCompleteListener {
                        if (it.isSuccessful)
                        {
                            val user = StoringPersonalInfo(name, surname, age)
                            myReference?.setValue(user)?.addOnSuccessListener {
                                Toast.makeText(this, "Information Saved", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, Login::class.java)
                                startActivity(intent)
                            }
                        }
                        else
                        {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else
                {
                    Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }


    }
}

