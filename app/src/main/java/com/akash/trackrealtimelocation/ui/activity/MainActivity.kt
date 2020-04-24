package com.akash.trackrealtimelocation.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.akash.trackrealtimelocation.R
import kotlinx.android.synthetic.main.activity_main.*


/**
 * @author Akash Garg
 * */

class MainActivity : BaseActivity() {

    private val TAG = "MainActivity"
    private val LOCATION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()
    }

    override fun onResume() {
        super.onResume()
        btnStartTracking.setOnClickListener {
            startActivity(Intent(this@MainActivity, LocationActivity::class.java))
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission to Location denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "@akash-----Permission has been denied by user")
                } else {
                    Log.e(TAG, "-----Permission has been granted by user")
                }
            }
        }
    }
}
