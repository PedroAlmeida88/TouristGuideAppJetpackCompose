package pt.isec.amovtp.touristapp.utils

import android.content.ContentValues.TAG
import android.content.res.AssetManager
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import pt.isec.amovtp.touristapp.data.Category
import pt.isec.amovtp.touristapp.data.Comment
import pt.isec.amovtp.touristapp.data.Location
import pt.isec.amovtp.touristapp.data.PointOfInterest
import pt.isec.amovtp.touristapp.data.User
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream


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
        fun addPOIToFirestore(locationName: String, poi: PointOfInterest,onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore

            val poisData = hashMapOf(
                "Description" to poi.description,
                "Category" to poi.category,
                "Latitude" to poi.latitude,
                "Longitude" to poi.longitude,
                "PhotoUrl" to poi.photoUrl,
            )

            db.collection(Collections.Locations.route)
                .document(locationName)
                .collection(Collections.POIs.route)
                .document(poi.name)
                .set(poisData)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
        }
        fun addLocationPhotoUrlToFirestore(name: String ,photoUrl: Uri) {
            val db = Firebase.firestore
            val newPhotoUrl = photoUrl.toString()
            val updateData = hashMapOf(
                "PhotoUrl" to newPhotoUrl as Any
            )
            db.collection(Collections.Locations.route).document(name)
                .update(updateData)
        }

        fun addPOIPhotoUrlToFirestore(name: String ,photoUrl: Uri,locationName: String) {
            val db = Firebase.firestore
            val newPhotoUrl = photoUrl.toString()
            val updateData = hashMapOf(
                "PhotoUrl" to newPhotoUrl as Any
            )
            db.collection(Collections.Locations.route)
                .document(locationName)
                .collection(Collections.POIs.route)
                .document(name)
                .update(updateData)
        }

        fun addCategoryToFirestore (category: Category, onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore

            val categoryData = hashMapOf(
                "Description" to category.description,
                "Icon" to category.icon
            )

            db.collection(Collections.Category.route).document(category.name).set(categoryData)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
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
                        val imageUrl = document.getString("PhotoUrl") ?: ""
                        val location = Location(name, description, latitude, longitude, imageUrl)
                        locations.add(location)
                    }
                    callback(locations)
                }
                .addOnFailureListener { e ->
                    Log.i(TAG, "ERROR: ${e.toString()}")
                    callback(emptyList())
                }
        }
        fun getPoisFromFirestore(location: Location?, callback: (List<PointOfInterest>) -> Unit) {
            val db = Firebase.firestore
            val idDocument = location?.name ?: ""

            db.collection(Collections.Locations.route)
                .document(idDocument)
                .collection(Collections.POIs.route)
                .get()
                .addOnSuccessListener { result ->
                    val pois = mutableListOf<PointOfInterest>()

                    for (document in result) {
                        try {
                            val name = document.id
                            val description = document.getString("Description") ?: ""
                            val latitude = document.getDouble("Latitude") ?: 0.0
                            val longitude = document.getDouble("Longitude") ?: 0.0
                            val imageUrl = document.getString("PhotoUrl") ?: ""

                            val category = pt.isec.amovtp.touristapp.data.Category(
                                "Categoria Teste",
                                "Alterar no StorageUtil",
                                ""
                            )

                            val pointOfInterest = PointOfInterest(name, description, latitude, longitude, imageUrl,category)
                            pois.add(pointOfInterest)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing POI document: ${e.message}")
                        }
                    }

                    callback(pois)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error fetching POIs: ${e.message}")
                    callback(emptyList())
                }
        }
        fun getCategoryToFirestore(callback: (List<Category>) -> Unit) {
            val db = Firebase.firestore

            db.collection(Collections.Category.route)
                .get()
                .addOnSuccessListener { result ->
                    val categories = mutableListOf<Category>()

                    for (document in result) {
                        val name = document.id
                        val description = document.getString("Description") ?: ""
                        val icon = document.getString("Icon") ?: ""
                        val category = Category(name, description,icon)
                        categories.add(category)
                    }
                    callback(categories)
                }
                .addOnFailureListener { e ->
                    Log.i(TAG, "ERROR: ${e.toString()}")
                    callback(emptyList())
                }
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
        fun uploadLocationFile(directory: String,inputStream: InputStream, imgFile: String) {
            val storage = Firebase.storage
            val ref1 = storage.reference
            val ref2 = ref1.child(directory)
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
                    addLocationPhotoUrlToFirestore(imgFile,downloadUri)


                    //   https://firebasestorage.googleapis.com/v0/b/p0405ansamov.appspot.com/o/images%2Fimage.png?alt=media&token=302c7119-c3a9-426d-b7b4-6ab5ac25fed9
                } else {
                    // Handle failures
                    // ...
                }
            }


        }

        fun uploadPOIFile(directory: String,inputStream: InputStream, imgFile: String,locationName: String) {
            val storage = Firebase.storage
            val ref1 = storage.reference
            val ref2 = ref1.child(directory)
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
                    addPOIPhotoUrlToFirestore(imgFile,downloadUri,locationName)


                    //   https://firebasestorage.googleapis.com/v0/b/p0405ansamov.appspot.com/o/images%2Fimage.png?alt=media&token=302c7119-c3a9-426d-b7b4-6ab5ac25fed9
                } else {
                    // Handle failures
                    // ...
                }
            }


        }




    }
}