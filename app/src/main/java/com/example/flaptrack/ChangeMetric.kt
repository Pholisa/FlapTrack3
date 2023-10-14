package com.example.flaptrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.example.flaptrack.databinding.ActivityAboutBinding
import com.example.flaptrack.databinding.ActivityChangeMetricBinding

class ChangeMetric : AppCompatActivity() {
    private lateinit var binding: ActivityChangeMetricBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeMetricBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Calling navigation bar metric
        navigationBar()

        var switchMetric = findViewById<SwitchCompat>(R.id.switch1)
        var metric = findViewById<TextView>(R.id.tvMetric)

        switchMetric.setOnCheckedChangeListener { compoundButton, onSwitch ->

            if(onSwitch)
            {
                metric.text = "miles"
            }
            else
            {
                metric.text = "kilometres"
            }

        }
    }

    private fun navigationBar()
    {
        //This will account for event clicking of the navigation bar (similar to if statement format)
        binding.bottomNavigationView.setOnItemSelectedListener {item ->
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