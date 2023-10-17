package com.example.flaptrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.flaptrack.databinding.ActivityBirdsUiBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class BirdsUi : AppCompatActivity() {

    private lateinit var binding: ActivityBirdsUiBinding

    private lateinit var theArrayList: ArrayList<BirdInfo>
    private lateinit var adapter: MyAdapter
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


