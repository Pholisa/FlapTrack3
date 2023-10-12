package com.example.flaptrack

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.flaptrack.databinding.ActivityAboutBinding
import com.example.flaptrack.databinding.ActivityMapUiBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapUI : FragmentActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST = 1

    private lateinit var binding: ActivityMapUiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_map_ui)
        binding = ActivityMapUiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //calling the navigation bar function
        navigationBar()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
            return
        }

        // Enable My Location button and show the user's location on the map
        mMap.isMyLocationEnabled = true

        // Get the device's current location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    // Add a marker at the device's current location
                    val userLocation = LatLng(it.latitude, it.longitude)
                    val markerOptions = MarkerOptions()
                    markerOptions.position(userLocation)
                    mMap.addMarker(markerOptions)

                    // Move the camera to the device's current location
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                }
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, show the device's current location on the map
            onMapReady(mMap)
        }
    }

    private fun navigationBar()
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
