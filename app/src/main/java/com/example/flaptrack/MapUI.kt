package com.example.flaptrack

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.concurrent.thread
import java.util.concurrent.CompletableFuture

class MapUI : FragmentActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST = 1
    private var searchQuery: String = ""

    var hotspotList = mutableListOf<HotspotData>()

    private lateinit var binding: ActivityMapUiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapUiBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // Calling the navigation bar function
        navigationBar()

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val setDistance = findViewById<ImageView>(R.id.setDistanceIV)
        setDistance.setOnClickListener {
            val intent = Intent(this, SetDistance::class.java)
            startActivity(intent)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {

        val maxDist = intent.getStringExtra("value_key")
        //textViewReceivedData.text = "Received Data: $receivedValue"

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
                    //val userLocation = LatLng(it.latitude, it.longitude) //actual device location uncomment this
                    val userLocation = LatLng(-33.8970590380015, 18.48906600246067) //hard coded location to finish app from
                    val markerOptions = MarkerOptions()
                    markerOptions.position(userLocation)
                    markerOptions.title("Your Locationnn")
                    mMap.addMarker(markerOptions)

                    // Move the camera to the device's current location
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))

                    // Initialize NetworkUtil
                    val networkUtil = NetworkUtil()
                    //Thread
                    thread {
                        val hotspot = try {
                            networkUtil.buildURLForEbird()?.readText()
                        } catch (e: Exception) {
                            // Handle the error, e.g., display a message to the user
                            return@thread
                        }

                        runOnUiThread {

                            // Pass the userLocation to consumeJson
                            consumeJson(hotspot,userLocation,maxDist)
                        }
                    }
                }
            }

        //calling search function
        searchFunction()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, show the device's current location on the map
            onMapReady(mMap)
        }
    }

    private fun navigationBar() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.idBirds -> {
                    val intent = Intent(this, BirdsUi::class.java)
                    startActivity(intent)
                }
                R.id.idAccount -> {
                    val intent = Intent(this, AccountSettings::class.java)
                    startActivity(intent)
                }
                else -> {
                    // Handle other cases as needed
                }
            }
            true
        }
    }



    private fun consumeJson(hotspotJSON: String?, userLocat: LatLng, maxDist: String?) {
        if (hotspotJSON != null) {
            try {
                val rootHotspotData = JSONArray(hotspotJSON)

                // Define the user's maximum allowed distance (in kilometers)
                val maxDistance = 5.0 // Change this value to your desired maximum distance



                val userLocation = userLocat

                    for (i in 0 until rootHotspotData.length()) {
                                val hotspotObject = HotspotData()
                                val hotspot = rootHotspotData.getJSONObject(i)

                                if (hotspot.has("locName")) {
                                    hotspotObject.locName = hotspot.getString("locName")
                                }

                                if (hotspot.has("lat")) {
                                    hotspotObject.lat = hotspot.getDouble("lat")
                                }

                                if (hotspot.has("lng")) {
                                    val lng = hotspot.getDouble("lng")
                                    val lat = hotspotObject.lat

                                    // Ensure lat and lng are not null before adding the marker
                                    if (lat != null && lng != null) {
                                        val hotspotLocation = LatLng(lat, lng)

                                        // Calculate the distance between the user's location and the hotspot
                                        val distance = calculateDistance(userLocation, hotspotLocation)

                                        // Check if the hotspot is within the specified distance
                                        if (distance <= maxDistance) {
                                            val markerOptions = MarkerOptions()
                                                .position(hotspotLocation)
                                                .title(hotspotObject.locName)
                                            mMap.addMarker(markerOptions)

                                            // Adding the hotspotObject to the list
                                            hotspotList.add(hotspotObject)
                                        }
                                    }

                        }
                    }
            } catch (e: JSONException)
            {
                // Handle JSON parsing errors
                e.printStackTrace()
            }
        } else
        {
            // Handle the case where hotspotJSON is null
        }
    }

    // Function to calculate the distance between two LatLng points using the Haversine formula
    private fun calculateDistance(point1: LatLng, point2: LatLng): Double {
        val radius = 6371.0 // Earth's radius in kilometers
        val lat1 = Math.toRadians(point1.latitude)
        val lat2 = Math.toRadians(point2.latitude)
        val deltaLat = Math.toRadians(point2.latitude - point1.latitude)
        val deltaLng = Math.toRadians(point2.longitude - point1.longitude)

        val a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return radius * c
    }

    //search function to search for hotspot to then give directions to location
    private fun searchFunction() {
        val searchLocation = findViewById<SearchView>(R.id.searchView1)

        searchLocation.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrBlank()) {
                    return false
                }

                // Search for the entered location in the hotspotList
                val locationToSearch = query.toLowerCase()
                val foundHotspot = hotspotList.find { hotspot ->
                    hotspot.locName?.toLowerCase() == locationToSearch
                }

                if (foundHotspot != null) {
                    // Location found, perform actions
                    showToast("Hotspot found: ${foundHotspot.locName}")
                    // You can perform other actions here
                } else {
                    // Location not found, display a message
                    showToast("Hotspot not found")
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle text changes here if needed
                return true
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}