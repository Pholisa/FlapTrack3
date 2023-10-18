package com.example.flaptrack

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import coil.load
import coil.transform.CircleCropTransformation
import com.example.flaptrack.databinding.ActivityAddNewBirdBinding
import com.google.android.gms.location.FusedLocationProviderClient
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
import com.google.android.gms.location.LocationServices
import android.location.Location



class AddNewBird : AppCompatActivity() {

    //Declaring

    private lateinit var textDate: TextView
    private lateinit var buttonDate: Button

    private lateinit var binding: ActivityAddNewBirdBinding

    private val LOCATION_PERMISSION_REQUEST_CODE = 1007

    private val database = Firebase.database
    private val userID = FirebaseAuth.getInstance().currentUser?.uid
    private val myReference = database.getReference("users").child(userID!!).child("Bird Information")
    private val myReferenceTwo = database.getReference("users").child(userID!!).child("Birdie Information")


    private val cameraRequestCode = 1
    private val galleryRequestCode = 2

    private lateinit var locationClient : FusedLocationProviderClient

    private var imageURL: String? = null
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewBirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigationBar()

        textDate = findViewById(R.id.tvDate)
        buttonDate = findViewById(R.id.btnDate)
        locationClient = LocationServices.getFusedLocationProviderClient(this)


        binding.btnPickIamge.setOnClickListener {
            galleryCheckPermission()

        }
        binding.btnSave.setOnClickListener {

            savingData()
        }


        //----------Calendar functionality-----------------------------------------------------//

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



    @SuppressLint("SuspiciousIndentation")
    private fun savingData() {

       val storageReference = FirebaseStorage.getInstance().reference.child("Bird Image")
          .child(imageUri!!.lastPathSegment!!)

     val builder = AlertDialog.Builder(this@AddNewBird)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        storageReference.putFile(imageUri!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isComplete);
            val urlImage = uriTask.result
            imageURL = urlImage.toString()
            UploadingData()
            dialog.dismiss()
        }.addOnFailureListener {
            dialog.dismiss()
        }
        }


    private fun UploadingData() {
        val name = binding.edBirdName.text.toString()
        val species = binding.edBirdSpecies.text.toString()
        val date = binding.tvDate.text.toString()
        var theLocation = ""  // Declare theLocation as a var

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Get current location
            locationClient.lastLocation.addOnSuccessListener { location: Location ->
                location?.let {
                    val theData = "${it.longitude}, ${it.latitude}"

                    theLocation = theData.toString()

                    // Continue with saving the data here
                    saveData(name, species, date, theLocation)
                }
            }
        } else {
            locationRequesting()
        }

    }


    //----------Saving all the data in the database-----------------------------------------------------//

    private fun saveData(name: String, species: String, date: String, theLocation: String) {
        if (name.isEmpty() || species.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
        } else {
            val saveClass = BirdInfo(name, species, date, imageURL, theLocation)
            val birdSave = BirdieInfo(name, species, date, theLocation)

            myReferenceTwo.push().setValue( birdSave)

            myReference.push().setValue(saveClass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@AddNewBird, "Saved", Toast.LENGTH_SHORT).show()
                    }
                    val intent = Intent(this, BirdsUi::class.java)
                    startActivity(intent)
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        this@AddNewBird, e.message.toString(), Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }


    private fun locationRequesting() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
            )

    }

    //----------Permission to location---------------------------------------------------------------///

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with getting location
                UploadingData()
            } else {
                // Permission denied, show a dialog to the user
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder(this)
                        .setMessage("This app requires location permission to work properly.")
                        .setPositiveButton("Ask again") { _, _ ->
                           locationRequesting()
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    //----------Permission to access external storage-----------------------------------------------------//

    private fun galleryCheckPermission() {

        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    gallery()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(
                        this@AddNewBird,
                        "You have denied the storage permission to select image",
                        Toast.LENGTH_SHORT
                    ).show()
                    showRorationalDialogForPermission()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    showRorationalDialogForPermission()
                }

            }).onSameThread().check()

    }

    private fun updateText(calendar: Calendar) {
        val dateFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.UK)
        textDate.setText(sdf.format(calendar.time))
    }

    //----------Saving Image-------------------------------------
    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, galleryRequestCode)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && resultCode == RESULT_OK ) {

            when (requestCode) {
                cameraRequestCode -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.ivImage.load(bitmap)
                    {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }
                    this.imageUri = data.data!!

                }

                galleryRequestCode -> {

                    binding.ivImage.load(data?.data)
                    {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())

                    }
                    this.imageUri = data?.data
                }
            }
        }
    }


    private fun showRorationalDialogForPermission() {
        AlertDialog.Builder(this).setMessage(
            "It looks like you have turned off permissions" +
                    "required for this feature. It can be enabled under App settings!!!"
        )
            .setPositiveButton("Go To SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }.show()


    }

    fun navigationBar() {
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
//-------------------------------------ooo000EndOfFile000ooo----------------------------------------