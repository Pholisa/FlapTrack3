package com.example.flaptrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.flaptrack.databinding.ActivitySetDistanceBinding
import com.example.flaptrack.databinding.ActivityViewBadgeBinding

class ViewBadge : AppCompatActivity() {

    private lateinit var binding: ActivityViewBadgeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBadgeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //calling the navigation function
        navigationBar()
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
}