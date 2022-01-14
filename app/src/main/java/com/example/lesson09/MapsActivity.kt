package com.example.lesson09

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.lesson09.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val LAT_EXTRAS_KEY = "lat"
        private const val LON_EXTRAS_KEY = "lon"

        @JvmStatic
        fun start(context: Context, lat: Double, lon: Double) {
            val starter = Intent(context, MapsActivity::class.java)
                .putExtra(LAT_EXTRAS_KEY, lat)
                .putExtra(LON_EXTRAS_KEY, lon)
            context.startActivity(starter)
        }
    }

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun extractLocation(): Pair<Double, Double>? {
        val lat = if (intent.hasExtra(LAT_EXTRAS_KEY)) {
            intent.getDoubleExtra(LAT_EXTRAS_KEY, -1.0)
        } else null
        val lon = if (intent.hasExtra(LON_EXTRAS_KEY)) {
            intent.getDoubleExtra(LON_EXTRAS_KEY, -1.0)
        } else null
        return if (lat != null && lon != null) {
            Pair(lat, lon)
        } else null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        extractLocation()?.let {
            val selectPoint = LatLng(it.first, it.second)
            map.addMarker(MarkerOptions().position(selectPoint).title("Marker in selectPoint"))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectPoint,19f))
            map.uiSettings.isZoomControlsEnabled = true
            map.mapType = GoogleMap.MAP_TYPE_HYBRID

        } ?: run {
            val sydney = LatLng(-34.0, 151.0)
            map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }


    }
}