package com.akash.trackrealtimelocation.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.akash.trackrealtimelocation.R
import com.akash.trackrealtimelocation.livedata.LocationModel
import com.akash.trackrealtimelocation.utils.GpsUtils
import com.akash.trackrealtimelocation.viewmodel.LocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_tracking.*

/**
 * @author Akash Garg
 * */

class LocationActivity : FragmentActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var lat = 0.0
    private var lng = 0.0
    private lateinit var locationViewModel: LocationViewModel
    private var isGPSEnabled = false
    var mPositionMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)

        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
        val mapFragment = supportFragmentManager.findFragmentById(com.akash.trackrealtimelocation.R.id.gmap) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        GpsUtils(this).turnGPSOn(object : GpsUtils.OnGpsListener {

            override fun gpsStatus(isGPSEnable: Boolean) {
                this@LocationActivity.isGPSEnabled = isGPSEnable
            }
        })
    }


    override fun onStart() {
        super.onStart()
        invokeLocationAction()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPSEnabled = true
                invokeLocationAction()
            }
        }
    }

    private fun invokeLocationAction() {
        when {
            !isGPSEnabled -> latLong.text = getString(com.akash.trackrealtimelocation.R.string.enable_gps)
            isPermissionsGranted() -> startLocationUpdate()
            shouldShowRequestPermissionRationale() -> latLong.text = getString(com.akash.trackrealtimelocation.R.string.permission_request)

            else -> ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    LOCATION_REQUEST
            )
        }
    }


    @SuppressLint("SetTextI18n")
    private fun startLocationUpdate() {
        locationViewModel.getLocationData().observe(this, Observer {
            latLong.text = " Latitude -: ${it.latitude}\nLongitude-: ${it.longitude}"
            lat = it.latitude
            lng = it.longitude
            Log.e("****", "----" + it.latitude + "," + it.longitude)
            Toast.makeText(this@LocationActivity, it.latitude.toString(), Toast.LENGTH_SHORT).show()

            val myLatLng = LatLng(it.latitude, it.longitude)
            val myPosition = CameraPosition.Builder()
                    .target(myLatLng).zoom(18f).bearing(90f).tilt(26f).build()

            if (mPositionMarker == null) {
                mPositionMarker = mMap!!.addMarker(MarkerOptions()
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_track_new))
                        .position(LatLng(it.latitude, it.longitude)))
            }

            animateMarker(mPositionMarker!!, it)
            mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition))
        })
    }


    private fun animateMarker(marker: Marker, location: LocationModel) {
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val startLatLng = marker.position
        val startRotation = marker.rotation.toDouble()
        val duration: Long = 250
        val interpolator = LinearInterpolator()

        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val t = interpolator.getInterpolation(elapsed.toFloat() / duration)

                val lng = t * location.longitude + (1 - t) * startLatLng.longitude
                val lat = t * location.latitude + (1 - t) * startLatLng.latitude

                val rotation = (t * 90 + (1 - t) * startRotation)
//                Log.e("LocationActivity##", "..@akash..bearing-->" + location.bearing)
                marker.position = LatLng(lat, lng)
                marker.rotation = rotation.toFloat()

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 14)
                }
            }
        })
    }

    private fun isPermissionsGranted() = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

    private fun shouldShowRequestPermissionRationale() =
            ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            )

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST -> {
                invokeLocationAction()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        // initial map marker point 0.0 ..

        mMap = googleMap
        val sydney = LatLng(lat, lng)
        mMap!!.addMarker(MarkerOptions().position(sydney).title("Current location is here"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap!!.uiSettings.isZoomControlsEnabled = true
        mMap!!.uiSettings.setAllGesturesEnabled(true)
        mMap!!.uiSettings.isZoomGesturesEnabled = true
        mMap!!.isMyLocationEnabled = true
    }

}

const val LOCATION_REQUEST = 100
const val GPS_REQUEST = 101


