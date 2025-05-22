package com.example.questapp.viewmodel


import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.questapp.ui.LocationTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class LocationInfo(
    val currentLocation: Location? = null,
    val distanceToTarget: Float? = null,
    val isInRange: Boolean = false,
    val errorMessage: String? = null
)

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val locationTracker = LocationTracker(application)
    private val _locationInfo = MutableStateFlow(LocationInfo())
    val locationInfo: StateFlow<LocationInfo> = _locationInfo

    fun startLocationUpdates(targetLat: Double, targetLon: Double, radius: Float) {
        viewModelScope.launch {
            locationTracker.getLocationUpdates(10000L).collectLatest { location ->
                if (location != null) {
                    val distance = FloatArray(1)
                    Location.distanceBetween(
                        location.latitude, location.longitude,
                        targetLat, targetLon,
                        distance
                    )
                    val isInRange = distance[0] <= radius
                    _locationInfo.value = LocationInfo(
                        currentLocation = location,
                        distanceToTarget = distance[0],
                        isInRange = isInRange,
                        errorMessage = null
                    )
                } else {
                    _locationInfo.value = LocationInfo(
                        errorMessage = "Nu am putut obține locația ta!"
                    )
                }
            }
        }
    }
}