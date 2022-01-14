package com.example.lesson09

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.example.lesson09.databinding.ActivityMainBinding

private const val GPS_PERMISSION_REQUEST_CODE = 1234
private const val CONTACTS_PERMISSION_REQUEST_CODE = 12345
private const val TAG = "@@@ MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.errorTextView.isVisible = false

        binding.gpsButton.setOnClickListener {
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showCoordinates()
            } else {
                showRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    GPS_PERMISSION_REQUEST_CODE
                )
                binding.errorTextView.isVisible = true
            }
        }

        binding.contactsButton.setOnClickListener {
            if (checkPermission(Manifest.permission.READ_CONTACTS)) {
                showContacts()
            } else {
                showRequestPermissionRationale(
                    Manifest.permission.READ_CONTACTS,
                    CONTACTS_PERMISSION_REQUEST_CODE
                )
                binding.contactsTextView.text = "Нет доступа к контактам"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun showRequestPermissionRationale(permission: String, requestCode: Int) {
        var title = ""
        var message = ""

        when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> {
                title = "Доступ к GPS"
                message =
                    "Пожалуйста, предоставте доступ к GPS. Он нам нужен для отображения ваших координат"
            }
            Manifest.permission.READ_CONTACTS -> {
                title = "Доступ к Контактам"
                message =
                    "Пожалуйста, предоставте доступ к Контактам. Он нам нужен для отображения ваших контаков"
            }
        }

        shouldShowRequestPermissionRationale(permission)
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Предоставить доступ") { _, _ ->
                requestPermission(permission, requestCode)
            }
            .setNegativeButton("Не надо") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermission(permission: String): Boolean {
        when (checkSelfPermission(permission)) {
            PackageManager.PERMISSION_GRANTED -> return true
            PackageManager.PERMISSION_DENIED -> return false
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermission(permission: String, requestCode: Int) {
        requestPermissions(
            arrayOf(permission), requestCode
        )
    }

    @SuppressLint("MissingPermission")
    fun showCoordinates() {
        Log.d(TAG, "showCoordinates() called")
        binding.errorTextView.isVisible = false
        var lat = 0.0
        var lon = 0.0
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f) {
            if (lat != it.latitude || lon != it.longitude) {
                lat = it.latitude
                lon = it.longitude
                with(binding) {
                    latTextView.text = it.latitude.toString()
                    lonTextView.text = it.longitude.toString()
                }
                Log.d(TAG, "showCoordinates() called in request requestLocationUpdates")
                if (Geocoder.isPresent()) {
                    Thread {
                        Log.d(TAG, "showCoordinates() called" + Thread.currentThread().id)
                        val geocoder = Geocoder(this)
                        val addrList = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        addrList.firstOrNull()?.let {
                            runOnUiThread {
                                binding.addrTextView.text = it.getAddressLine(0)
                                MapsActivity.start(this, it.latitude, it.longitude)
                            }
                        }
                    }.start()
                }
            }


        }
    }

    @SuppressLint("Range")
    fun showContacts() {
        this?.let {
            val contentResolver: ContentResolver = it.contentResolver
            // Отправляем запрос на получение контактов и получаем ответ в виде Cursor
            val cursorWithContacts: Cursor? = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null,
                ContactsContract.Contacts.DISPLAY_NAME
            )
            cursorWithContacts?.let { cursor ->
                for (i in 0..cursor.count) {
                    // Переходим на позицию в Cursor
                    if (cursor.moveToPosition(i)) {
                        // Берём из Cursor столбец с именем
                        val name =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        addView(name)
                    }

                }
            }
            cursorWithContacts?.close()
        }
    }

    fun addView(textToShow: String) {
        binding.mainLinearLayout.addView(AppCompatTextView(this).apply {
            text = textToShow
        })
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
        }

        if (requestCode == CONTACTS_PERMISSION_REQUEST_CODE) {
            val pos = permissions.indexOf(Manifest.permission.READ_CONTACTS)
            if (grantResults[pos] == PackageManager.PERMISSION_GRANTED) {
                showContacts()
            } else {
                binding.contactsTextView.text = "Нет доступа к контактам"
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}