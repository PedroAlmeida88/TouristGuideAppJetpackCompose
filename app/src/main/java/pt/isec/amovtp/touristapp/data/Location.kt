package pt.isec.amovtp.touristapp.data

data class Location (
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val photoUrl: String
){
    val userId: String = ""
    val countToAccept: Int = -1
    val avaliation: Int = -1
}
