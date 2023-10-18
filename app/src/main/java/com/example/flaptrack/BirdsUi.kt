package com.example.flaptrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flaptrack.databinding.ActivityBirdsUiBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BirdsUi : AppCompatActivity() {

    private lateinit var binding: ActivityBirdsUiBinding


    private lateinit var recyclerView: RecyclerView
    private val userID = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var birdArrayList: ArrayList<BirdieInfo>
    private lateinit var databaseReference: DatabaseReference

    var firebaseDatabase: FirebaseDatabase? = null
    private val theDatabase = Firebase.database


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBirdsUiBinding.inflate(layoutInflater)
        setContentView(binding.root)



        recyclerView = findViewById(R.id.rvBirdListView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        birdArrayList = arrayListOf()

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userID!!).child("Birdie Information")
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(birdSnapshot in snapshot.children){
                        val birdie = birdSnapshot.getValue(BirdieInfo::class.java)
                        if(!birdArrayList.contains(birdie)){
                            birdArrayList.add(birdie!!)
                        }
                    }
                    recyclerView.adapter = BirdAdapter(birdArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BirdsUi, error.toString(), Toast.LENGTH_SHORT).show()
            }
        })



        binding.floatButton.setOnClickListener {
            val addBirdIntent = Intent(this, AddNewBird::class.java)
            startActivity(addBirdIntent)
        }




        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference =
            theDatabase.getReference("users").child(userID!!).child("Bird Information")


        MaterialAlertDialogBuilder(this)
            .setTitle("Welcome")
            .setMessage("The bottom navigation menu allows you navigate through the app")
            .setNeutralButton("Dismiss") { dialog, which ->
                dialog.dismiss()
            }

            .setPositiveButton("More") { dialog, which ->
                //calling the display metric function
                displayMetricSelection()
            }
            .show()


        //calling the navigation bar function
        navigationBar()
    }




    private fun displayMetricSelection() {
        val singleItems = arrayOf("Kilometres", "Miles")
        val checkedItem = 1

        MaterialAlertDialogBuilder(this)
            .setTitle("Select Metric")
            .setNeutralButton("Ignore for now") { dialog, which ->
                // Respond to neutral button press
                MaterialAlertDialogBuilder(this)
                    .setTitle("Got You!")
                    .setMessage(
                        "You can change the metric in the account section at the bottom navigation." +
                                "\nFor now, default settings are set to km!"
                    )
                    .setNeutralButton("Ok") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
                //End of dialog
            }
            .setPositiveButton("Save") { dialog, which ->
                // Respond to positive button press which will change the metric
            }
            // Single-choice items (initialized with checked item)
            .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                // Respond to item chosen
            }
            .show()
    }


    fun navigationBar()
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