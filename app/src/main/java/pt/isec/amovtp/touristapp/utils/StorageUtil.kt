package pt.isec.amovtp.touristapp.utils

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.isec.amovtp.touristapp.data.Comment
import pt.isec.amovtp.touristapp.data.Location
import pt.isec.amovtp.touristapp.data.PointOfInterest
import pt.isec.amovtp.touristapp.data.User
import java.util.Locale.Category

class StorageUtil {
    companion object {
        private val database by lazy { Firebase.firestore }

        /*
         *  Adicionar Novos dados na Firebase
         */

        fun addLocationToFirestore (onResult: (Throwable?) -> Unit) {

        }

        fun addPOIToFirestore (onResult: (Throwable?) -> Unit) {

        }

        fun addCategoryToFirestore (onResult: (Throwable?) -> Unit) {

        }

        fun addCommentToFirestore (onResult: (Throwable?) -> Unit) {

        }

        fun addUserToFirestore (onResult: (Throwable?) -> Unit) {

        }

        /*
         *  Ler dados da Firestore
         */

        fun getLocationFromFirestore () : List<Location> {
            return emptyList()
        }

        fun getPOIFromFirestore () : List<PointOfInterest> {
            return emptyList()
        }

        fun getCategoryFromFirestore () : List<Category> {
            return emptyList()
        }

        fun getCommentsFromFirestore () : List<Comment> {
            return emptyList()
        }

        fun getUserFromFirestore () : User? {
            return null
        }
    }
}