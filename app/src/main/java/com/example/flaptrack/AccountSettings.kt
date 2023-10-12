package com.example.flaptrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.flaptrack.databinding.ActivityAccountSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AccountSettings : AppCompatActivity() {

    private lateinit var binding: ActivityAccountSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var setDistance = findViewById<TextView>(R.id.tvSetMaxDistance)
        var changeMetric = findViewById<TextView>(R.id.tvChangeMetric)
        var badges = findViewById<TextView>(R.id.tvBadges)
        var about = findViewById<TextView>(R.id.tvAbout)
        var deleteAccount = findViewById<TextView>(R.id.tvDeleteProfile)
        var logout = findViewById<TextView>(R.id.tvLogout)

        //calling the navigation function
        navigationBar()


        //Calling the SetTheDistance method that takes user to the set distance screen
        setTheDistanceUI(setDistance)
        //Calling change metric method
        changeTheMetricUI(changeMetric)
        //calling badges method
        badgesUI(badges)
        //calling about function
        aboutUI(about)
        //calling the delete account function
        deleteProfileUI(deleteAccount)
        //calling the logout function
        logoutUI(logout)
    }

    private fun navigationBar()
    {
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

    private fun setTheDistanceUI(setDistance : TextView)
    {
        setDistance.setOnClickListener{
            val intent = Intent(this, SetDistance::class.java)
            startActivity(intent)
        }
    }

    private fun changeTheMetricUI(changeMetric : TextView)
    {
        changeMetric.setOnClickListener{
            val intent = Intent(this, ChangeMetric::class.java)
            startActivity(intent)
        }
    }

    private fun badgesUI(badges : TextView)
    {
        badges.setOnClickListener{
            val intent = Intent(this, ViewBadge::class.java)
            startActivity(intent)
        }
    }

    private fun aboutUI(about : TextView)
    {
        about.setOnClickListener{
            val intent = Intent(this, About::class.java)
            startActivity(intent)
        }
    }

    private fun deleteProfileUI(deleteAccount : TextView)
    {
        deleteAccount.setOnClickListener{
            MaterialAlertDialogBuilder(this)
                .setTitle("Delete Profile")
                .setMessage("Do you wish to proceed with the deletion of your profile? Please note that this action is irreversible.")
                .setNeutralButton("Delete") { dialog, which ->
                    //displayMetricSelection()
                }

                .setPositiveButton("Dismiss") { dialog, which ->

                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun logoutUI(logout : TextView)
    {
        logout.setOnClickListener{
            MaterialAlertDialogBuilder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to sign-out?")
                .setNeutralButton("Dismiss") { dialog, which ->
                    dialog.dismiss()
                }

                .setPositiveButton("Sign out") { dialog, which ->
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                }
                .show()
        }
    }
}