package com.example.flaptrack

import android.Manifest
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import coil.load
import coil.transform.CircleCropTransformation
import com.example.flaptrack.databinding.ActivityAddNewBirdBinding
import com.google.firebase.auth.FirebaseAuth
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

    //val storage = Firebase.storage
    private lateinit var storageRef : StorageReference
    private var imageUri: Uri?=null


    private val cameraRequestCode = 1
    private val galleryRequestCode = 2

    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewBirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageRef = FirebaseStorage.getInstance().getReference("Images")

        textDate = findViewById(R.id.tvDate)
        buttonDate = findViewById(R.id.btnDate)

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
        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){
            binding.ivImage.setImageURI(it)
            if(it != null)
            {
                imageUri = it
            }
        }


        binding.btnCamera.setOnClickListener {
            cameraCheckPermission()
        }

        binding.btnGallery.setOnClickListener {
            pickImage.launch("image/*")

            //galleryCheckPermission()
        }
        binding.btnSave.setOnClickListener {
            count++
            val intent = Intent(this@AddNewBird, ViewBadge :: class.java)
            intent.putExtra("COUNT", count)
            UploadData()
        }

        //when you click on the image
        binding.ivImage.setOnClickListener{
            //selectImage.launch("image/*")
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItem = arrayOf("Select photo from Gallery",
                "Capture photo from Camera")
            pictureDialog.setItems(pictureDialogItem){dialog, which ->
                when(which){
                    0 -> gallery()
                    1 -> camera()


                }

            }
            pictureDialog.show()
        }

    }


    private fun UploadData() {

        val theBirdName = binding.edBirdName.text.toString()
        val theBirdSpecies = binding.edBirdSpecies.text.toString()
        val date = binding.tvDate.text.toString()


        if (binding.edBirdName.text.toString().isEmpty() ||
            binding.edBirdSpecies.text.toString().isEmpty()
        )
        {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
        }
        else
        {

            val personId = myReference.push().key!!
            imageUri?.let {
                storageRef.child(personId).putFile(it)
                    .addOnSuccessListener { task ->
                        task.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { url ->
                                Toast.makeText(
                                    this,
                                    "Image stored successfully",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                                val imageURL = url.toString()


                                val dataClass = Saving(theBirdName, theBirdSpecies, date, imageURL)
                                myReference.push().setValue(dataClass).addOnSuccessListener {
                                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()


                                }

                            }
                    }
            }
        }
    }

    private fun galleryCheckPermission(){

        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener (object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    gallery()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(this@AddNewBird,
                        "You have denied the storage permission to select image",
                        Toast.LENGTH_SHORT).show()
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

    private fun gallery()
    {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, galleryRequestCode)
    }
    private fun cameraCheckPermission()
    {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA).withListener(
            object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let{
                        if(report.areAllPermissionsGranted())
                        {
                            camera()
                        }
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?) {
                    showRorationalDialogForPermission()
                }
            }
        ).onSameThread().check()
    }


    private fun camera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, cameraRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK)
        {
            when(requestCode)
            {
                cameraRequestCode->{
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.ivImage.load(bitmap)
                    {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }

                }
                galleryRequestCode->{
                    binding.ivImage.load(data?.data)
                    {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())

                    }
                }
            }
        }
    }



    private fun showRorationalDialogForPermission(){
        AlertDialog.Builder(this).setMessage("It looks like you have turned off permissions" +
                "required for this feature. It can be enabled under App settings!!!")
            .setPositiveButton("Go To SETTINGS"){_,_->
                try{
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
                catch(e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }
            .setNegativeButton("CANCEL"){dialog,_->
                dialog.dismiss()
            }.show()


    }
}