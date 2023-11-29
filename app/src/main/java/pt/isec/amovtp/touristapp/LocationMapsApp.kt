package pt.isec.amovtp.touristapp

import android.app.Application
import com.google.android.gms.location.LocationServices
import pt.isec.amovtp.touristapp.utils.location.FusedLocationHandler

class LocationMapsApp : Application() {


    val locationHandler by lazy {
        val locationProvider = LocationServices.getFusedLocationProviderClient(this)
        FusedLocationHandler(locationProvider)
    }

    override fun onCreate() {
        super.onCreate()
    }
}