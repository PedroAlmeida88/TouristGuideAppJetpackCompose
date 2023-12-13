package pt.isec.amovtp.touristapp.utils

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import pt.isec.amovtp.touristapp.data.Comment
import pt.isec.amovtp.touristapp.data.Location
import pt.isec.amovtp.touristapp.data.PointOfInterest
import pt.isec.amovtp.touristapp.data.User
import java.util.Locale.Category

enum class Collections(val collectionName: String){
    Locations("Locations"),
    Users("Users"),
    Comments("Comments"),
    Category("Category"),
    POIs("POIs");

    val route : String
        get() = this.collectionName

}
class StorageUtil {
    companion object {
        /*
         *  Adicionar Novos dados na Firebase
         */

        fun addLocationToFirestore(location: Location,onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore

            val locationData = hashMapOf(
                "Description" to location.description,
                "Latitude" to location.latitude,
                "Longitude" to location.longitude,
                "PhotoUrl" to location.photoUrl,
            )
            db.collection(Collections.Locations.route).document(location.name).set(locationData)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
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

        fun getLocationFromFirestore () {
            val db = Firebase.firestore

            db.collection(Collections.Locations.route)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        //println("${document.id} => ${document.data}")
                        Log.i(TAG, "getLocationFromFirestore: " + document.data.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Log.i(TAG, "ERROR"+ e.toString())
                }
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