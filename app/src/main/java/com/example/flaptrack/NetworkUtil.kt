package com.example.flaptrack

import android.net.Uri
import android.util.Log
import java.net.MalformedURLException
import java.net.URL

private val EBIRD_BASEURL = "https://api.ebird.org/v2/ref/hotspot/ZA?fmt=json"
private val PARAM_METRIC = "metric"
private val METRIC_VALUE = "true"
private val PARAM_API_KEY = "key"
private val LOGGING_TAG = "URLICREATED"

class NetworkUtil
{
    fun buildURLForEbird(): URL? {
        val buildUri: Uri = Uri.parse(EBIRD_BASEURL).buildUpon()
            .appendQueryParameter(
                PARAM_API_KEY,
                BuildConfig.EBIRD_API_KEY
            ) // passing in api key
            .appendQueryParameter(
                PARAM_METRIC,
                METRIC_VALUE
            ) // passing in metric as measurement unit
            .build()
        var url: URL? = null
        try {
            url = URL(buildUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        Log.i(LOGGING_TAG, "buildURLForEbird: $url")
        return url
    }
}

//-------------------------------------ooo000EndOfFile000ooo----------------------------------------