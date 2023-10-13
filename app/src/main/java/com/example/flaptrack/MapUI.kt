package com.example.flaptrack

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
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
import kotlin.math.*

class MapUI : FragmentActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST = 1

    var hotspotList = mutableListOf<HotspotData>()

    private lateinit var binding: ActivityMapUiBinding
    private var userLocation: LatLng = LatLng(0.0, 0.0) // Initialize with a default value
    private val maxDistanceKm = 10.0 // Set the maximum distance in kilometers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapUiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize NetworkUtil
        val networkUtil = NetworkUtil()

        // Calling the navigation bar function
        navigationBar()

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        thread {
            val hotspot = try {
                networkUtil.buildURLForEbird()?.readText()
            } catch (e: Exception) {
                // Handle the error, e.g., display a message to the user
                return@thread
            }

            runOnUiThread {
                consumeJson(hotspot)
            }
        }
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
                    // Update user's location
                    userLocation = LatLng(it.latitude, it.longitude)

                    // Add a marker at the device's current location
                    val markerOptions = MarkerOptions()
                    markerOptions.position(userLocation)
                    mMap.addMarker(markerOptions)

                    // Move the camera to the device's current location
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))

                    // Filter and display hotspots
                    filterAndDisplayHotspots()
                }
            }
    }

    private fun filterAndDisplayHotspots() {
        val filteredHotspots = hotspotList.filter { hotspot ->
            val distance = calculateDistance(userLocation, LatLng(hotspot.lat ?: 0.0, hotspot.lng ?: 0.0))
            distance <= maxDistanceKm
        }


        filteredHotspots.forEach { hotspot ->
            val lat = hotspot.lat ?: 0.0
            val lng = hotspot.lng ?: 0.0
            val hotspotLocation = LatLng(lat, lng)
            val markerOptions = MarkerOptions()
                .position(hotspotLocation)
                .title(hotspot.locName)
            mMap.addMarker(markerOptions)
        }

    }

    // Haversine formula to calculate distance between two points
    private fun calculateDistance(p1: LatLng, p2: LatLng): Double {
        val lat1 = p1.latitude
        val lon1 = p1.longitude
        val lat2 = p2.latitude
        val lon2 = p2.longitude
        val r = 6371.0 // Earth's radius in kilometers
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(lonDistance / 2) * sin(lonDistance / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
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

    private fun consumeJson(hotspotJSON: String?) {
        if (hotspotJSON != null) {
            try {
                val rootHotspotData = JSONArray(hotspotJSON)

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
                        hotspotObject.lng = hotspot.getDouble("lng")
                    }

                    // Add the hotspotObject to the list
                    hotspotList.add(hotspotObject)
                }

            } catch (e: JSONException)
            {
                // Handle JSON parsing errors
                e.printStackTrace()
            }
        }
        else
        {
            // Handle the case where hotspotJSON is null
        }
    }
}
