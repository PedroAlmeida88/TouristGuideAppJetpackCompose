package pt.isec.amovtp.touristapp.utils

import android.content.ContentValues.TAG
import android.content.res.AssetManager
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import pt.isec.amovtp.touristapp.data.Comment
import pt.isec.amovtp.touristapp.data.Location
import pt.isec.amovtp.touristapp.data.PointOfInterest
import pt.isec.amovtp.touristapp.data.User
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
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
        //private val db = Firebase.firestore
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
        fun addPhotoUrlToFirestore(name: String ,photoUrl: Uri) {
            val db = Firebase.firestore
            val newPhotoUrl = photoUrl.toString()
            val updateData = hashMapOf(
                "PhotoUrl" to newPhotoUrl as Any
            )
            db.collection(Collections.Locations.route).document(name)
                .update(updateData)
        }

        fun addPOIToFirestore (onResult: (Throwable?) -> Unit) {

        }

        fun addCategoryToFirestore (onResult: (Throwable?) -> Unit) {

        }

        fun addCommentToFirestore (onResult: (Throwable?) -> Unit) {

        }

        fun addUserToFirestore (uid: String, user: User, onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore
            val userData = hashMapOf(
                "Email" to user.email,
                "FirstName" to user.firstName,
                "LastName" to user.lastName,
            )

            db.collection(Collections.Users.route).document(uid).set(userData)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
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
        fun getLocationFromFirestore(callback: (List<Location>) -> Unit) {
            val db = Firebase.firestore

            db.collection(Collections.Locations.route)
                .get()
                .addOnSuccessListener { result ->
                    val locations = mutableListOf<Location>()

                    for (document in result) {
                        Log.i(TAG, "getLocationFromFirestore: " +document.data.toString())
                        val name = document.id
                        val description = document.getString("Description") ?: ""
                        val latitude = document.getDouble("Latitude") ?: 0.0
                        val longitude = document.getDouble("Longitude") ?: 0.0
                        val imageResource = document.getString("PhotoUrl") ?: ""
                        val location = Location(name, description, latitude, longitude, imageResource)
                        locations.add(location)
                    }
                    callback(locations)
                }
                .addOnFailureListener { e ->
                    Log.i(TAG, "ERROR: ${e.toString()}")
                    callback(emptyList())
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

        fun getUserFromFirestore (userUID: String, userData: (User) -> Unit){
            val db = Firebase.firestore

            db.collection(Collections.Users.route).document(userUID).get()
                .addOnSuccessListener { doc ->
                    if(doc.exists()) {
                        val user = User(doc.get("Email").toString(), doc.get("FirstName").toString(), doc.get("LastName").toString()) //doc.toObject<User>()!!
                        userData(user)
                    }
                }
        }

        //Storage

        fun getFileFromAsset(assetManager: AssetManager, strName: String): InputStream? {
            var istr: InputStream? = null
            try {
                istr = assetManager.open(strName)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return istr
        }

        fun getFileFromPath(filePath: String): InputStream? {
            return try {
                FileInputStream(filePath)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
        fun uploadFile(inputStream: InputStream, imgFile: String) {
            val storage = Firebase.storage
            val ref1 = storage.reference
            val ref2 = ref1.child("images")
            val ref3 = ref2.child(imgFile)

            val uploadTask = ref3.putStream(inputStream)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref3.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    println(downloadUri.toString())
                    //add Uti to database
                    addPhotoUrlToFirestore(imgFile,downloadUri)


                    //   https://firebasestorage.googleapis.com/v0/b/p0405ansamov.appspot.com/o/images%2Fimage.png?alt=media&token=302c7119-c3a9-426d-b7b4-6ab5ac25fed9
                } else {
                    // Handle failures
                    // ...
                }
            }


        }

    }
}