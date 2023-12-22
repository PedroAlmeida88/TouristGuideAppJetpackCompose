package pt.isec.amovtp.touristapp.data

data class Location (
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val photoUrl: String,
    val writenCoords: Boolean,
    val approvals: Int,
    val userUIDsApprovals:List<String>,
    var enableBtn: Boolean = true

)