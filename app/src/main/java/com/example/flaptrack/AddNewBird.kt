package com.example.flaptrack

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import coil.load
import coil.transform.CircleCropTransformation
import com.example.flaptrack.databinding.ActivityAddNewBirdBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



class AddNewBird : AppCompatActivity() {

    private lateinit var textDate: TextView
    private lateinit var buttonDate: Button

    private lateinit var binding :ActivityAddNewBirdBinding

    private val database = Firebase.database
    private val userID = FirebaseAuth.getInstance().currentUser?.uid
    private val myReference = database.getReference("users").child(userID!!).child("Bird Information")


    private var imageURL : String? = null
    private var imageUri: Uri?=null


    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewBirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigationBar()


        textDate = findViewById(R.id.tvDate)
        buttonDate = findViewById(R.id.btnDate)

        val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ){result->
            if(result.resultCode == RESULT_OK){
                val data = result.data
                imageUri = data!!.data
                binding.ivImage.setImageURI(imageUri)
            }
            else{
                Toast.makeText(this@AddNewBird, "No Image Selected", Toast.LENGTH_SHORT).show()
            }


        }

        binding.btnPickIamge.setOnClickListener{
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        binding.btnSave.setOnClickListener{

            SavingData()
        }


        val calendarBox = Calendar.getInstance()
        val dateBox = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            calendarBox.set(Calendar.YEAR, year)
            calendarBox.set(Calendar.MONTH, month)
            calendarBox.set(Calendar.DAY_OF_MONTH, day)
            updateText(calendarBox)
        }
        buttonDate.setOnClickListener {
            DatePickerDialog(
                this, dateBox, calendarBox.get(Calendar.YEAR), calendarBox.get(Calendar.MONTH),
                calendarBox.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

    }


    private fun SavingData() {

        val storageReference = FirebaseStorage.getInstance().reference.child("Task Image").child(imageUri!!.lastPathSegment!!)

        val builder = AlertDialog.Builder(this@AddNewBird)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        storageReference.putFile(imageUri!!).addOnSuccessListener {
            taskSnapshot->
            val uriTask = taskSnapshot.storage.downloadUrl
            while(!uriTask.isComplete);
            val urlImage = uriTask.result
            imageURL = urlImage.toString()
            UploadingData()

            dialog.dismiss()
        }.addOnFailureListener{
            dialog.dismiss()
        }
    }

    private fun UploadingData() {

        val name = binding.edBirdName.text.toString()
        val species = binding.edBirdSpecies.text.toString()
        val date = binding.tvDate.text.toString()


        if (binding.edBirdName.text.toString().isEmpty() ||
            binding.edBirdSpecies.text.toString().isEmpty() ||
            binding.tvDate.text.toString().isEmpty()
        ) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
        }
        else {

            val saveClass = BirdInfo(name, species, date, imageURL)
            FirebaseDatabase.getInstance().getReference("Business Information").setValue(saveClass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@AddNewBird, "Saved", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        this@AddNewBird, e.message.toString(), Toast.LENGTH_SHORT
                    ).show()
                }
        }

    }

    private fun updateText(calendar: Calendar) {
        val dateFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.UK)
        textDate.setText(sdf.format(calendar.time))
    }



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