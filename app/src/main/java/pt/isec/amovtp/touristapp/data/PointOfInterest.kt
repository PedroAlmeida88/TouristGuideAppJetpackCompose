package pt.isec.amovtp.touristapp.data

data class PointOfInterest (
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val photoUrl: String,
    val category: Category,
    val writenCoords: Boolean,
    val approvals: Int,
    val userUIDsApprovals:List<String>, //lista de userUIDs
    var userUID: String,                 //userUID do criador da localização
    var enableBtn: Boolean = true
    )


