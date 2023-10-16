package com.example.flaptrack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.flaptrack.databinding.ActivityBirdListViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BirdListView : AppCompatActivity() {

    private lateinit var binding : ActivityBirdListViewBinding

    private val theDatabase = Firebase.database
    private val userID = FirebaseAuth.getInstance().currentUser?.uid
    private val myReference = theDatabase.getReference("users").child(userID!!).child("Bird Preview")

    private lateinit var database: DatabaseReference


   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       binding = ActivityBirdListViewBinding.inflate(layoutInflater)
       setContentView(binding.root)

       database = Firebase.database.reference
       val userID = FirebaseAuth.getInstance().currentUser?.uid
       database.child("users").child(userID!!).get().addOnSuccessListener {
           val name = it.child("Bird Information").child("birdName").value.toString()
           val species =
               it.child("Bird Information").child("birdSpecie").value.toString()
           val date =
               it.child("Bird Information").child("date").value.toString()

           val arrayName = ArrayList<String>()
           arrayName.add(name)

           val arraySpecie = ArrayList<String>()
           arraySpecie.add(species)

           val arrayDate = ArrayList<String>()
           arrayDate.add(date)



       }
   }
}