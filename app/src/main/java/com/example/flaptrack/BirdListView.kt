package com.example.flaptrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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


    private lateinit var theArrayList: ArrayList<BirdInfo>
    private lateinit var adapter: MyAdapter
    var databaseReference : DatabaseReference?=null
    var eventListener : ValueEventListener?=null

  /*  private val theDatabase = Firebase.database
    private val userID = FirebaseAuth.getInstance().currentUser?.uid
    private val myReference = theDatabase.getReference("users").child(userID!!).child("Bird Preview")

    private lateinit var database: DatabaseReference
*/

   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       binding = ActivityBirdListViewBinding.inflate(layoutInflater)
       setContentView(binding.root)

       val gridLayoutManager = GridLayoutManager(this@BirdListView, 1)
       binding.rvBirdListView.layoutManager = gridLayoutManager

       val builder = AlertDialog.Builder(this@BirdListView)
       builder.setCancelable(false)
       builder.setView(R.layout.progress_layout)
       val dialog = builder.create()
       dialog.show()

       theArrayList = ArrayList()
       adapter = MyAdapter(this@BirdListView, theArrayList)
       binding.rvBirdListView.adapter = adapter
       databaseReference = FirebaseDatabase.getInstance().getReference("Bird Preview")
       dialog.show()

       eventListener = databaseReference!!.addValueEventListener(object: ValueEventListener{
           override fun onDataChange(snapshot: DataSnapshot) {

               theArrayList.clear()
               for (itemSnapshot in snapshot.children){
                   val birdInfo = itemSnapshot.getValue(BirdInfo::class.java)
                   if(birdInfo != null){
                       theArrayList.add(birdInfo)
                   }
               }
               adapter.notifyDataSetChanged()
               dialog.dismiss()
           }

           override fun onCancelled(error: DatabaseError) {
               dialog.dismiss()
           }

       })



   }

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
//                TODO("Not yet implemented")
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