package com.example.flaptrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
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
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class BirdsUi : AppCompatActivity() {

    private lateinit var binding: ActivityBirdsUiBinding

    private lateinit var theArrayList: ArrayList<BirdInfo>
    private lateinit var theAdapter: MyAdapter
    var firebaseDatabase : FirebaseDatabase ?=null
    private val theDatabase = Firebase.database
    var databaseReference: DatabaseReference? = null
    var eventListener: ValueEventListener? = null

    private val userID = FirebaseAuth.getInstance().currentUser?.uid




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBirdsUiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gridLayoutManager = GridLayoutManager(this@BirdsUi, 1)
        binding.rvBirdListView.layoutManager = gridLayoutManager

//        val builder = AlertDialog.Builder(this@BirdsUi)
//        builder.setCancelable(false)
//        builder.setView(R.layout.progress_layout)
//        val dialog = builder.create()
//        dialog.show()



        binding.floatButton.setOnClickListener {
            val addBirdIntent = Intent(this, AddNewBird::class.java)
            startActivity(addBirdIntent)
        }
//
//       binding.svSearchBirds.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//          override fun onQueryTextSubmit(query: String?): Boolean {
//             return false
//           }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                searchList(newText)
//               return true
//           }
//        })
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = theDatabase.getReference("users").child(userID!!).child("Bird Information")

        initialiseRecycleView()
        getData()


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

    private fun initialiseRecycleView(){
        theAdapter = MyAdapter()
        binding.apply {
            rvBirdListView.layoutManager = LinearLayoutManager(this@BirdsUi)
            rvBirdListView.adapter = theAdapter
        }
    }
    //******************************************************************************************
    //Get Values in Database
    private fun getData() {
        val databaseReference = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userID!!)  // Assuming userID is a variable holding the user's ID
            .child("Bird Information")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val birdList = ArrayList<BirdInfo>()

                for (dataSnapshot in snapshot.children) {
                    val birdData = dataSnapshot.getValue(BirdInfo::class.java)
                    birdData?.let {
                        birdList.add(it)
                    }
                }

                // Create and set the adapter
                val adapter = MyAdapter()
                adapter.setItem(birdList)
                binding.rvBirdListView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("0000", "onCancelled: ${error.toException()}")
            }
        })
    }







//    private fun getData(){
//        databaseReference?.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                // Log.e("00000", "onDataChange: $snapshot")
//                theArrayList.clear()
//                for (it in snapshot.children){
//
//                    val birdData: BirdInfo? = it.getValue(BirdInfo::class.java)
//                    birdData?.let {
//                        val theData = arrayOf(
//                            it.birdSpecies.toString(),
//                            it.birdName.toString(),
//                            it.date.toString(),
//                            it.image.toString(),
//                            it.location.toString()
//                        )
//
//                        val view = RecyclerView(this@BirdsUi)
//
//                        for(item in theData.indices){
//                            val
//                        }
//                    }
//                    //       val theBirdName = it.child("birdName").toString()
//                    val theBirdName = it.key
//                    val theBirdSpecies =
//                        it.child("birdSpecies").value.toString()
//                    val theDate =
//                        it.child("date").getValue().toString()
//
//                    val theImage =
//                        it.child("image").value.toString()
//
//                    val theLoc =
//                        it.child("location").getValue().toString()
//
//
//
//                    val bird = BirdInfo(birdName = theBirdName, birdSpecies =theBirdSpecies, date = theDate, image = theImage, location = theLoc )
//                    theArrayList.add(bird)
//                }
//                Log.e("0000", "size: ${theArrayList.size}")
//                theAdapter?.setItem(theArrayList)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("0000", "onCancelled: ${error.toException()}")
//            }
//        })
//    }



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
                /*
                R.id.idBirds -> {
                    val intent = Intent(this, BirdsUi::class.java)
                    startActivity(intent)
                }
                 */
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


