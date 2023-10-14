package com.example.flaptrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.example.flaptrack.databinding.ActivityAccountSettingsBinding
import com.example.flaptrack.databinding.ActivitySetDistanceBinding

class SetDistance : AppCompatActivity() {

    private lateinit var binding: ActivitySetDistanceBinding
    private var startPoint = 0
    private var endPoint = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetDistanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var seekBarDistance = findViewById<SeekBar>(R.id.seekBarDistance)
        var distance = findViewById<TextView>(R.id.tvMaxDistance)
        var saveDistance = findViewById<Button>(R.id.btnSetDistance)

        var selectedDistance = 0 // Initialize a variable to store the selected distance

        seekBarDistance.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                distance.text = p1.toString()
                selectedDistance = p1 // Update the selected distance when the seekbar changes
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                // No need to track start and stop for this case
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                // No need to track start and stop for this case
            }
        })

        saveDistance.setOnClickListener {
            // Display the selected distance in a Toast
            Toast.makeText(applicationContext, "Distance of $selectedDistance km set", Toast.LENGTH_SHORT).show()
        }

        // val intent = Intent(this, MapUI::class.java)
        // intent.putExtra("value_key", selectedDistance.toString())

        // calling the navigation function
        navigationBar()
    }


    private fun navigationBar()
    {
        //This will account for event clicking of the navigation bar (similar to if statement format)
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

                else -> {}
            }
            true
        }
    }
}