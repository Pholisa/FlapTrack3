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
    // Initialize Firebase
    private val userID = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance()
    private val myReference = database.getReference("Hotspot Maximum Distance").child(userID!!)
    private val myReference2 = database.getReference("Metric").child(userID!!)
    private var finalDistance: Double = 0.0
    private var selectedMetric: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetDistanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var seekBarDistance = findViewById<SeekBar>(R.id.seekBarDistance)
        var distance = findViewById<TextView>(R.id.tvMaxDistance)
        var saveDistance = findViewById<Button>(R.id.btnSetMetric)

        // Retrieving metric data from Firebase
        myReference2.child("SelectedMetric").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists()) {
                    // User has a saved metric
                    val retrievedData = snapshot.value.toString()
                    var retrievedMetric = retrievedData

                    if(retrievedMetric == "miles")
                    {
                        selectedMetric = "miles"
                        finalDistance = convertDistance(seekBarDistance.progress.toDouble())
                    }
                    else if(retrievedMetric =="kilometres")
                    {
                        selectedMetric = "km"
                    }
                }
                else //if there is nothing in databse
                {
                    selectedMetric ="km"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseData", "Data retrieval failed: $error")
            }
        })


        // Retrieving distance data from Firebase
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
            Toast.makeText(applicationContext, "Distance of $selectedDistance$selectedMetric set", Toast.LENGTH_SHORT).show()

            // Store the selected distance in Firebase
            saveDistanceToFirebase(selectedDistance)
        }

        // Set up the bottom navigation bar
        navigationBar()
    }

    private fun saveDistanceToFirebase(selectedDistance: Double) {
        if (userID != null) {
            val database = FirebaseDatabase.getInstance()
            val myReference = database.getReference("Hotspot Maximum Distance").child(userID)

            // Store the selected distance in the "Distance" node
            myReference.child("Distance").setValue(selectedDistance)
        }
    }

    private fun convertDistance(enterKm:Double) :Double
    {
        var convertedVal = enterKm/1.609

        return convertedVal
    }

    private fun navigationBar()
    {
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
