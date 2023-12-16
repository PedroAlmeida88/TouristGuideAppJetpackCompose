package pt.isec.amovtp.touristapp.ui.viewmodels

import android.location.Location
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.isec.amovtp.touristapp.R
import pt.isec.amovtp.touristapp.utils.location.LocationHandler
import pt.isec.amovtp.touristapp.data.Location as LocationData

class LocationViewModelFactory (
    private val locationHandler: LocationHandler
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return LocationViewModel(locationHandler) as T
    }
}

data class Coordinates(val team: String,val latitude : Double, val longitude: Double,val imagesRes: Int)

class LocationViewModel(private val locationHandler: LocationHandler) : ViewModel() {
    val imagePath : MutableState<String?> = mutableStateOf(null)//todo teste

    val Locations = listOf(
        LocationData("Liverpool", "Liverpool description", 53.430819, -2.960828, ""),
        LocationData("Manchester", "Manchester description", 53.482989, -2.200292, ""),
        LocationData("Munich", "Munich description", 48.218775, 11.624753, ""),
        LocationData("Barcelona", "Barcelona description", 41.38087, 2.122802, "")
    )
    val POIsBarcelona = listOf(
        Coordinates("La Sagrada Familia", 41.4036, 2.1744, R.drawable.acacio),
        Coordinates("Camp Nou", 41.38087, 2.122802, R.drawable.acacio),
    )
    // Permissions
    var coarseLocationPermission = false
    var fineLocationPermission = false
    var backgroundLocationPermission = false

    private val _currentLocation = MutableLiveData(Location(null))
    val currentLocation : LiveData<Location>
        get() = _currentLocation


    private val locationEnabled : Boolean
        get() = locationHandler.locationEnabled

    init {
        locationHandler.onLocation = {
            _currentLocation.value = it
        }
    }

    fun startLocationUpdates() {
        if (fineLocationPermission && coarseLocationPermission) {
            locationHandler.startLocationUpdates()
        }

    }

    fun stopLocationUpdates() {
        locationHandler.stopLocationUpdates()
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
    }
}
