package com.example.lesson09

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.example.lesson09.databinding.ActivityMainBinding

private const val GPS_PERMISSION_REQUEST_CODE = 1234

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.errorTextView.isVisible = false

        binding.goButton.setOnClickListener {
            if (checkGpsPermission()) {
                showCoordinates()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    GPS_PERMISSION_REQUEST_CODE
                )
                binding.errorTextView.isVisible = true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkGpsPermission(): Boolean {
        when (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> return true
            PackageManager.PERMISSION_DENIED -> return false
        }
        return false
    }

    @SuppressLint("MissingPermission")
    fun showCoordinates() {
        binding.errorTextView.isVisible = false
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f) {
            with(binding) {
                latTextView.text = it.latitude.toString()
                lonTextView.text = it.longitude.toString()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == GPS_PERMISSION_REQUEST_CODE) {
            val pos = permissions.indexOf(Manifest.permission.ACCESS_FINE_LOCATION)
            if (grantResults[pos] == PackageManager.PERMISSION_GRANTED) {
                showCoordinates()
            } else {
                binding.errorTextView.isVisible = true
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}