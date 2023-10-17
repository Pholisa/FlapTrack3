package com.example.flaptrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flaptrack.databinding.ActivityBirdListViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BirdListView : AppCompatActivity() {

    private lateinit var binding : ActivityBirdListViewBinding


    //private lateinit var theArrayList: ArrayList<BirdInfo>
    private lateinit var adapter: MyAdapter
    var firebaseDatabase : FirebaseDatabase ?=null
    var databaseReference : DatabaseReference?=null
    var eventListener : ValueEventListener?=null

    private var theArrayList = mutableListOf<BirdInfo>()


    private val theDatabase = Firebase.database
    private val userID = FirebaseAuth.getInstance().currentUser?.uid

    private lateinit var database: DatabaseReference


   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       binding = ActivityBirdListViewBinding.inflate(layoutInflater)
       setContentView(binding.root)



       firebaseDatabase = FirebaseDatabase.getInstance()
       databaseReference = theDatabase.getReference("users").child(userID!!).child("Bird Information")

       initialiseRecycleView()
       //getData()

       //val userId = intent.getStringExtra("useremail") //You need this in order to access data for a specif userID



   }

    private fun initialiseRecycleView(){
        adapter = MyAdapter()
        binding.apply {
            rvBirdListView.layoutManager = LinearLayoutManager(this@BirdListView)
            rvBirdListView.adapter = adapter
        }
    }
    //******************************************************************************************
    //Get Values in Database
    private fun getData(){
        databaseReference?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // Log.e("00000", "onDataChange: $snapshot")
                theArrayList.clear()
                for (it in snapshot.children){

                    val theBirdName =
                        it.child("Bird Information").child("birdName").child("").value.toString()
                    val theBirdSpecies =
                        it.child("Bird Information").child("birdSpecies").child("").value.toString()
                    val theDate =
                        it.child("Bird Information").child("date").child("").value.toString()
                    val theImage =
                        it.child("Bird Information").child("image").child("").value.toString()

                    val theLoc =
                        it.child("Bird Information").child("image").child("").value.toString()



                    val bird = BirdInfo(birdName = theBirdName, birdSpecies =theBirdSpecies, date = theDate, image = theImage, location = theLoc )
                    theArrayList.add(bird)
                }
                Log.e("0000", "size: ${theArrayList.size}")
                adapter?.setItem(theArrayList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("0000", "onCancelled: ${error.toException()}")
            }
        })
    }

//       val gridLayoutManager = GridLayoutManager(this@BirdListView, 1)
//       binding.rvBirdListView.layoutManager = gridLayoutManager
//
//       val builder = AlertDialog.Builder(this@BirdListView)
//       builder.setCancelable(false)
//       builder.setView(R.layout.progress_layout)
//       val dialog = builder.create()
//       dialog.show()
//
//       theArrayList = ArrayList()
//       adapter = MyAdapter(this@BirdListView, theArrayList)
//       binding.rvBirdListView.adapter = adapter
//       databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userID!!).child("Business Information")
//       dialog.show()
//
//       eventListener = databaseReference!!.addValueEventListener(object: ValueEventListener{
//           override fun onDataChange(snapshot: DataSnapshot) {
//
//               theArrayList.clear()
//               for (itemSnapshot in snapshot.children){
//                   val birdInfo = itemSnapshot.getValue(BirdInfo::class.java)
//                   if(birdInfo != null){
//                       theArrayList.add(birdInfo)
//                   }
//               }
//               adapter.notifyDataSetChanged()
//               dialog.dismiss()
//           }
//
//           override fun onCancelled(error: DatabaseError) {
//               dialog.dismiss()
//           }
//
//       })



  // }

//    private fun getBirdData() {
//
//
//        database = FirebaseDatabase.getInstance().getReference("users")
//        database.addValueEventListener(object : ValueEventListener
//        {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.exists())
//                {
//                    for(userSnapshot in snapshot.children){
//
//                        var bird = userSnapshot.getValue(BirdInfo::class.java)
//                        theArrayList.add(bird!!)
//                    }
//                    theRecyclerView.adapter = MyAdapter(theArrayList)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })
//
//
//    }
    fun navigationBar() {
        //This will account for event clicking of the navigation bar (similar to if statement format)
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
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