package pt.isec.amovtp.touristapp.data

data class PointOfInterest (
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val photoUrl: String,

    ){
    val userId: Int = -1
    val countToAccept: Int = 0
}
