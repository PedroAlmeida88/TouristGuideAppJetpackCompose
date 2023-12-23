package pt.isec.amovtp.touristapp.data

data class Category (
    var name: String,
    val description:String,
    val icon: String,

    val totalPois: Int,                 //num de pois associados
    val approvals: Int,
    val userUIDsApprovals:List<String>, //lista de userUIDs
    var userUID: String,                //userUID do criador da categoria
    var enableBtn: Boolean = true
)
