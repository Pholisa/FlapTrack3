package com.example.flaptrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.example.flaptrack.databinding.ActivitySetDistanceBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SetDistance : AppCompatActivity() {

    private lateinit var binding: ActivitySetDistanceBinding
    private var selectedDistance: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetDistanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var seekBarDistance = findViewById<SeekBar>(R.id.seekBarDistance)
        var distance = findViewById<TextView>(R.id.tvMaxDistance)
        var saveDistance = findViewById<Button>(R.id.btnSetDistance)

        // Initialize Firebase
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val myReference = database.getReference("Hotspot Maximum Distance").child(userID!!)

        // Retrieving data from Firebase
        myReference.child("Distance").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists()) {
                    // User has a saved distance, set it to the SeekBar
                    val retrievedData = snapshot.value.toString()
                    selectedDistance = retrievedData.toDouble()
                    distance.text = selectedDistance.toString()
                    seekBarDistance.progress = selectedDistance.toInt()
                }
                else
                {
                    // Data doesn't exist at this location
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseData", "Data retrieval failed: $error")
            }
        })

        seekBarDistance.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                distance.text = progress.toString()
                selectedDistance = progress.toDouble()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        saveDistance.setOnClickListener {
            // Display the selected distance in a Toast
            Toast.makeText(applicationContext, "Distance of $selectedDistance km set", Toast.LENGTH_SHORT).show()

            // Store the selected distance in Firebase
            saveDistanceToFirebase(selectedDistance)
        }

        // Set up the bottom navigation bar
        navigationBar()
    }

    private fun saveDistanceToFirebase(selectedDistance: Double) {
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        if (userID != null) {
            val database = FirebaseDatabase.getInstance()
            val myReference = database.getReference("Hotspot Maximum Distance").child(userID)

            // Store the selected distance in the "Distance" node
            myReference.child("Distance").setValue(selectedDistance)
        }
    }

    private fun navigationBar() {
        // This will account for event clicking of the navigation bar (similar to if statement format)
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.idBirds -> {
                    val intent = Intent(this, BirdsUi::class.java)
                    startActivity(intent)
                }
                R.id.idHome -> {
                    val intent = Intent(this, MapUI::class.java)
                    startActivity(intent)
                }
                R.id.idAccount -> {
                    val intent = Intent(this, AccountSettings::class.java)
                    startActivity(intent)
                }
                else -> {
                }
            }
            true
        }
    }
}
