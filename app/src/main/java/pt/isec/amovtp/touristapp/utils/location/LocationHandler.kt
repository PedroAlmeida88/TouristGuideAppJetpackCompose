package pt.isec.amovtp.touristapp.utils.location

import android.location.Location

interface LocationHandler {
    var locationEnabled : Boolean
    var onLocation : ((Location) -> Unit)?
    fun startLocationUpdates()
    fun stopLocationUpdates()
}