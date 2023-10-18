package com.example.flaptrack

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.flaptrack.databinding.ActivityMapUiBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MapUI : FragmentActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST = 1
    var hotspotList = mutableListOf<HotspotData>()
    private var userLocation: LatLng = LatLng(0.0, 0.0) // Initialize user location
    private var maxDist: Double = 0.0
    private lateinit var binding: ActivityMapUiBinding
    private val database = FirebaseDatabase.getInstance()
    private val userID = FirebaseAuth.getInstance().currentUser?.uid
    private val myReference2 = database.getReference("Metric").child(userID!!)
    private var selectedMetric: String = ""
    private var finalMaxDist: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMapUiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Calling the navigation bar function
        navigationBar()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val setDistance = findViewById<ImageView>(R.id.setDistanceIV)
        setDistance.setOnClickListener {
            val intent = Intent(this, SetDistance::class.java)
            startActivity(intent)
        }


        // Retrieving metric data from Firebase
        myReference2.child("SelectedMetric").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists()) {
                    // User has a saved metric
                    val retrievedData = snapshot.value.toString()
                    var retrievedMetric = retrievedData

                    if(retrievedMetric == "miles")
                    {
                        selectedMetric = "miles"
                        finalMaxDist = convertDistance(maxDist)
                    }
                    else if(retrievedMetric =="kilometres")
                    {
                        selectedMetric = "km"
                    }
                }
                else //if there is nothing in databse
                {
                    selectedMetric ="km"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseData", "Data retrieval failed: $error")
            }
        })





    }

    //----------------------------------------------------------------------------------------------
    override fun onMapReady(googleMap: GoogleMap)
    {
        //setting max distance
        // Initialize Firebase
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val myReference = database.getReference("Hotspot Maximum Distance").child(userID!!)

        // Retrieving max distance data from Firebase
        myReference.child("Distance").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists()) {
                    // User has a saved distance, set it to the SeekBar
                    val retrievedData = snapshot.value.toString()
                    maxDist = retrievedData.toDouble()

                }
                else
                {
                    showToast("Cannot find max distance")
                }
            }


            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseData", "Data retrieval failed: $error")
            }
        })

        //val maxDist = intent.getStringExtra("value_key")
        //textViewReceivedData.text = "Received Data: $receivedValue"

        mMap = googleMap

        //Zoom in Controls
        mMap?.uiSettings?.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isRotateGesturesEnabled = true
            isScrollGesturesEnabled = true
            isTiltGesturesEnabled = true
            isZoomGesturesEnabled = true
        }

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
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
                    // userLocation = LatLng(it.latitude, it.longitude) //actual device location uncomment this
                     userLocation = LatLng(-33.8970590380015, 18.48906600246067) //hard coded location to finish app from
                    val markerOptions = MarkerOptions()
                    markerOptions.position(userLocation)
                    markerOptions.title("Your Locationnn")
                    mMap.addMarker(markerOptions)

                    // How zoomed in the map will be.
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))

                    // Initialize NetworkUtil
                    val networkUtil = NetworkUtil()
                    // Thread
                    thread {
                        val hotspot = try {
                            networkUtil.buildURLForEbird()?.readText()
                        }
                        catch (e: Exception)
                        {
                            // Handle the error, e.g., display a message to the user
                            runOnUiThread {
                                showToast("Error: Failed to fetch hotspot data.")
                            }
                            return@thread
                        }

                        runOnUiThread {
                            // Pass the userLocation to consumeJson
                            consumeJson(hotspot, userLocation, maxDist)
                            searchFunction(hotspot)
                        }
                    }
                }
            }
    }
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    //Markers listeners when a user clicks on a hotspot
    override fun onMarkerClick(p0: Marker): Boolean
    {
       // showToast("Hotspot name is: " + p0.title)

        // Bottom sheet
        val sheet1 = findViewById<FrameLayout>(R.id.sheet)
        val locatName = findViewById<TextView>(R.id.tvLocatNme)
        val locatDistance= findViewById<TextView>(R.id.tvLocatDist)
        val direction = findViewById<Button>(R.id.btnDirec)

        val location = searchArea(userLocation,p0.position)

        BottomSheetBehavior.from(sheet1).apply {
            peekHeight = 100
            state = BottomSheetBehavior.STATE_EXPANDED

            locatName.text = p0.title
            locatDistance.text = "${location} $selectedMetric"

            direction.setOnClickListener {
                val url: String? = getDirectionsUrl(userLocation!!,p0.position!!)
                val downloadTask: DownloadTask = DownloadTask()

                //downloading json data from google directions
                downloadTask.execute(url)
            }

        }

        return false
    }
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    private fun getDirectionsUrl(startLocat: LatLng, endLocat: LatLng): String?
    {
        // Origin of route
        val str_origin = "origin=" + startLocat.latitude + "," + startLocat.longitude

        // Destination of route
        val str_dest = "destination="+ endLocat.latitude + "," + endLocat.longitude

        //setting transportation mode
        val mode = "mode=driving"

        // Building the parameters to the web service
         val parameters = "$str_origin&$str_dest&$mode"

        // Output format
        val output = "json"

        var s = "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=AIzaSyB3EPkYb9njIsQ1oX9w511QuDb3gC6xDKY"
        //. Building the url to the web service
        return s
    }
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    inner class DownloadTask:
    AsyncTask<String?, Void?, String>()
    {
        override fun onPostExecute(result: String)
        {
            super.onPostExecute(result)
            val parserTask = ParserTask()
            parserTask.execute(result)
        }
        override fun doInBackground(vararg url: String?): String {
            var data = ""

            try
            {
                data = downloadUrl(url[0].toString()).toString()
            } catch(e: java.lang.Exception){
                Log.d("Background Task", e.toString())
            }
            return data
        }
    }
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    //Method to download json data from url
    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String?
    {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try{
            val url = URL(strUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()
            iStream = urlConnection!!.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while(br.readLine().also {line = it} != null)
            {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
        }
        catch(e:java.lang.Exception)
        {
            Log.d("Exception", e.toString())
        }
        finally
        {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data
    }
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    //Class to pass JSON format
    inner class ParserTask:
    AsyncTask<String?, Int?, List<List<HashMap<String, String>>>?>()
    {
        //passing data in a no ui thread
        override fun doInBackground(vararg jsonData: String?): List<List<HashMap<String, String>>>? {
            val jObject : JSONObject
            var routes: List<List<HashMap<String, String>>>? = null
            try{
                jObject = JSONObject(jsonData[0])
                val parser = DataParser()
                routes = parser.parse(jObject)
            }catch(e: java.lang.Exception){
                e.printStackTrace()
            }
            return routes
        }
        //----------------------------------------------------------------------------------------------

        //----------------------------------------------------------------------------------------------
        override fun onPostExecute(result: List<List<HashMap<String, String>>>?)
        {
            val points = ArrayList<LatLng?>()
            val lineOptions = PolylineOptions()
            for(i in result!!.indices)
            {
                val path = result[i]
                for(j in path.indices)
                {
                    val point = path[j]
                    val lat = point["lat"]!!.toDouble()
                    val lng = point["lng"]!!.toDouble()
                    val position = LatLng(lat,lng)
                    points.add(position)
                }
                lineOptions.addAll(points)
                lineOptions.width(10f)
                lineOptions.color(Color.RED)
                lineOptions.geodesic(true)
            }
            //Draw lines for the i-th route
            if(points.size != 0) mMap!!.addPolyline(lineOptions)
        }
    }
    //----------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, show the device's current location on the map
            onMapReady(mMap)
        }
    }
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    private fun searchArea(myLocation: LatLng, endLocation: LatLng) :String
    {
        val results = FloatArray(1) // Change the array size to 1

        val myLatitude = myLocation.latitude
        val myLongitude = myLocation.longitude
        val endLatitude = endLocation.latitude
        val endLongitude = endLocation.longitude

        Location.distanceBetween(myLatitude, myLongitude, endLatitude, endLongitude, results)

        val s = String.format("%.1f",results[0]/1000)

        return s
    }
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    //navigation menu function
    private fun navigationBar()
    {
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
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    private fun consumeJson(hotspotJSON: String?, userLocat: LatLng, maxDist: Double)
    {
        if (hotspotJSON != null)
        {
            try {
                val rootHotspotData = JSONArray(hotspotJSON)

                // Define the user's maximum allowed distance (in kilometers)
               // val maxDistance = 5.0 // Change this value to your desired maximum distance


                val userLocation = userLocat

                    for (i in 0 until rootHotspotData.length())
                    {
                                val hotspotObject = HotspotData()
                                val hotspot = rootHotspotData.getJSONObject(i)

                                if (hotspot.has("locName"))
                                {
                                    hotspotObject.locName = hotspot.getString("locName")
                                }

                                if (hotspot.has("lat"))
                                {
                                    hotspotObject.lat = hotspot.getDouble("lat")
                                }

                                if (hotspot.has("lng"))
                                {
                                    val lng = hotspot.getDouble("lng")
                                    val lat = hotspotObject.lat

                                    // Ensure lat and lng are not null before adding the marker
                                    if (lat != null && lng != null)
                                    {
                                        val hotspotLocation = LatLng(lat, lng)

                                        // Calculate the distance between the user's location and the hotspot
                                        val distance = calculateDistance(userLocation, hotspotLocation)

                                        // Check if the hotspot is within the specified distance
                                        if (distance <= maxDist)
                                        {
                                            val markerOptions = MarkerOptions()
                                                .position(hotspotLocation)
                                                .title(hotspotObject.locName)
                                            mMap.addMarker(markerOptions)
                                            mMap.setOnMarkerClickListener(this)

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
        }
        else
        {
            // Handle the case where hotspotJSON is null
        }
    }
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    /* Function to calculate the distance between two LatLng from the earth's
     points using the Haversine formula. This is to use for filtering of hotspots not directions
     */
    private fun calculateDistance(point1: LatLng, point2: LatLng): Double
    {
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
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    //search function to search for hotspot to then give directions to location
    private fun searchFunction(hotspotJSON: String?)
    {
        val searchLocation = findViewById<SearchView>(R.id.searchView1)

        searchLocation.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrBlank()) {
                    return false
                }

                val searchQuery = searchLocation.query.toString().toLowerCase()

                var matchingLat: Double? = null
                var matchingLng: Double? = null

                if (hotspotJSON != null)
                {
                    try
                    {
                        val rootHotspotData = JSONArray(hotspotJSON)

                        //Going through alll objects to search for matching location
                        for (i in 0 until rootHotspotData.length())
                        {
                            val hotspot = rootHotspotData.getJSONObject(i)

                            // Check if the hotspot JSON object contains the desired fields
                            if (hotspot.has("locName") && hotspot.has("lat") && hotspot.has("lng"))
                            {
                                val locName = hotspot.getString("locName").toLowerCase()
                                val lat = hotspot.getDouble("lat")
                                val lng = hotspot.getDouble("lng")

                                //Check if the current hotspot matches the search query
                                if (locName == searchQuery)
                                {
                                    matchingLat = lat
                                    matchingLng = lng
                                    break // Exit the loop if a match is found
                                }
                            }
                        }

                        if(matchingLat != null && matchingLng != null)
                        {
                            showToast("$searchQuery - lat=$matchingLat, lng=$matchingLng of $searchQuery")

                            // You can perform further actions here, such as moving the camera
                            val hotspotLatLng = LatLng(matchingLat, matchingLng)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hotspotLatLng, 18f))
                        }
                        else
                        {
                            showToast("No matching hotspot found")
                        }

                    } catch (e: JSONException)
                    {
                        e.printStackTrace()
                    }
                }
                else
                {
                    // Handle the case where hotspotJSON is null
                    showToast("Unable to display hotspots at the moment")
                }

                return true
            }

            //if text message changes
            override fun onQueryTextChange(newText: String?): Boolean
            {
                // Handle text changes here if needed
                return true
            }
        })
    }
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    //Toast to method to make writing toast messages easier
    private fun showToast(message: String)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun convertDistance(enterKm:Double) :Double
    {
        var convertedVal = enterKm/1.609

        return convertedVal
    }

}
//-------------------------------------ooo000EndOfFile000ooo----------------------------------------