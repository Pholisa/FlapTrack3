package com.example.flaptrack

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.flaptrack.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuthentication: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuthentication = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val username = binding.edEmailAddress.text.toString()
            val password = binding.edPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {

                firebaseAuthentication.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, BirdsUi::class.java)
                            startActivity(intent)
                        }
                        else
                        {
                            Toast.makeText(this,"Incorrect username or password", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else
            {

                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }

        }

        //Create new password when password is forgotten
        binding.tvForgotRedirectText.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.activity_forgot_password, null)
            val userEmailAddress = view.findViewById<EditText>(R.id.editBox)

            builder.setView(view)
            val dialog = builder.create()

            view.findViewById<Button>(R.id.btnReset).setOnClickListener {
                compareEmail(userEmailAddress)
                dialog.dismiss()
            }
            view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()

            }
            if(dialog.window != null)
            {
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))

            }
            dialog.show()
        }

        //Register if not registered already
        var register = findViewById<TextView>(R.id.tvRegisterRedirectText2)
        register.setOnClickListener {
            val signupIntent = Intent(this, Register::class.java)
            startActivity(signupIntent)
        }

    }

    private fun compareEmail(email: EditText)
    {
        if(email.text.toString().isEmpty())
        {
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches())
        {
            return
        }
        firebaseAuthentication.sendPasswordResetEmail(email.text.toString()).addOnCompleteListener {
                task ->
            if(task.isSuccessful)
            {
                Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

//-------------------------------------ooo000EndOfFile000ooo----------------------------------------