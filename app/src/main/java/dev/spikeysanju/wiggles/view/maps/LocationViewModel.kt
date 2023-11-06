package dev.spikeysanju.wiggles.view.maps

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationViewModel : ViewModel() {
    private val _location = mutableStateOf<Location?>(null)
    val location: State<Location?> = _location

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    fun initLocationClient(context: Context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            _location.value = location
        }
    }
}
