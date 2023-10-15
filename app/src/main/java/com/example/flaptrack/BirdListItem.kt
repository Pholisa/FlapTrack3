package com.example.flaptrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.flaptrack.databinding.ActivityBirdListItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BirdListItem : AppCompatActivity() {

    private lateinit var binding: ActivityBirdListItemBinding

    private val theDatabase = Firebase.database
    private val userID = FirebaseAuth.getInstance().currentUser?.uid
    private val myReference =
        theDatabase.getReference("users").child(userID!!).child("Bird Preview")
    private lateinit var firebaseAuthentication: FirebaseAuth

    private lateinit var database: DatabaseReference
    private lateinit var birdName: TextView
    private lateinit var birdSpecies: TextView
    private lateinit var dataImage: ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBirdListItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        birdName = findViewById(R.id.tvBirdName)
        birdSpecies = findViewById(R.id.tvBirdSpecies)
        dataImage = findViewById(R.id.ivBirdPicture)


        database = Firebase.database.reference
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        database.child("users").child(userID!!).get().addOnSuccessListener {
            val name = it.child("Bird Information").child("birdName").value.toString()
            val species =
                it.child("Bird Information").child("birdSpecies").child("").value.toString()
            val photo =
                it.child("Bird Information").child("dataImage").child("").value.toString()


            birdName.text = name
            birdSpecies.text = species
            //dataImage.setImageURI() = emailAd



        }.addOnFailureListener {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()

        }
    }

}




