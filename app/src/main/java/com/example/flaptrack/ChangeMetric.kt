package com.example.flaptrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.example.flaptrack.databinding.ActivityAboutBinding
import com.example.flaptrack.databinding.ActivityChangeMetricBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChangeMetric : AppCompatActivity() {
    private lateinit var binding: ActivityChangeMetricBinding
    private var selectedMetric: String = ""
    // Initialize Firebase
    private val userID = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance()
    private val myReference = database.getReference("Metric").child(userID!!)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeMetricBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //Calling navigation bar metric
        navigationBar()

        //metric vars
        var switchMetric = findViewById<SwitchCompat>(R.id.switch1)
        var metric = findViewById<TextView>(R.id.tvMetric)

        //Calling function that handles the switching of the tog
        settingTogSwitch(switchMetric,metric)



        // Retrieving data from Firebase
        myReference.child("SelectedMetric").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists()) {
                    // User has a saved metric
                    val retrievedData = snapshot.value.toString()
                    selectedMetric = retrievedData

                    if(selectedMetric == "miles")
                    {
                            // To switch on the SwitchCompat
                            binding.switch1.isChecked = true
                            metric.text = "miles"

                    }
                    else if(selectedMetric =="kilometres")
                    {
                            //set switch off
                            binding.switch1.isChecked = false
                            metric.text = "kilometres"
                    }
                }
                else //if there is nothing in databse
                {
                    //Calling function that handles the switching of the tog
                    settingTogSwitch(switchMetric,metric)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseData", "Data retrieval failed: $error")
            }
        })

        //saving to firebase
        var saveToDatabase = findViewById<Button>(R.id.btnSetMetric)
        saveToDatabase.setOnClickListener {

            if (userID != null)
            {
                myReference.child("SelectedMetric").setValue(selectedMetric)
            }
        }
    }


    private fun settingTogSwitch(switchMetric:SwitchCompat,metric:TextView)
    {
        switchMetric.setOnCheckedChangeListener { compoundButton, onSwitch ->

            if(onSwitch)
            {
                metric.text = "miles"
                selectedMetric = metric.text.toString()
            }
            else
            {
                metric.text = "kilometres"
                selectedMetric = metric.text.toString()
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

//-------------------------------------ooo000EndOfFile000ooo----------------------------------------