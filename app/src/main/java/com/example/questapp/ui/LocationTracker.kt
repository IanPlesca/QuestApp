package com.example.questapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.Flow

class LocationTracker(context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getLocationUpdates(intervalMillis: Long): Flow<Location?> = callbackFlow {
        val locationRequest = LocationRequest.create().apply {
            interval = intervalMillis
            fastestInterval = intervalMillis / 2
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { trySend(it).isSuccess }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}