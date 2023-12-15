package pt.isec.amovtp.touristapp.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import pt.isec.amovtp.touristapp.data.AuthUser
import pt.isec.amovtp.touristapp.data.Location
import pt.isec.amovtp.touristapp.data.User
import pt.isec.amovtp.touristapp.utils.FireAuthUtil
import pt.isec.amovtp.touristapp.utils.StorageUtil

fun FirebaseUser.toAuthUser() : AuthUser {
    val userUID = this.uid
    val userDisplayName = this.displayName ?: ""
    val userEmail = this.email ?: ""

    return AuthUser(userUID, userDisplayName, userEmail)
}

class FirebaseViewModel : ViewModel() {
    private val _error = mutableStateOf<String?>(null)
    val error : MutableState<String?>
        get() = _error

    private val _authUser = mutableStateOf(FireAuthUtil.currentUser?.toAuthUser())
    val authUser : MutableState<AuthUser?>
        get() = _authUser

    private val _user = mutableStateOf<User?>(/*getUserFromFirestore(_authUser.value!!.uid)*/null)
    val user : MutableState<User?>
        get() = _user

    fun createUserWithEmail(user: User, password: String) {
        if(user.email.isBlank() || password.isBlank())
            return
        viewModelScope.launch {
            FireAuthUtil.createUserWithEmail(user.email, password) { exception ->
                if(exception == null) {
                    _authUser.value = FireAuthUtil.currentUser?.toAuthUser()
                    StorageUtil.addUserToFirestore(_authUser.value!!.uid ,user) { e ->
                        _error.value = e?.message
                    }
                }
                _error.value = exception?.message
            }
            FireAuthUtil.signOut()
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if(email.isBlank() || password.isBlank())
            return
        //viewModelScope.launch {
            FireAuthUtil.signInWithEmail(email, password) { exception ->
                if(exception == null) {
                    _authUser.value = FireAuthUtil.currentUser?.toAuthUser()
                    StorageUtil.getUserFromFirestore(_authUser.value!!.uid) { user ->
                        _user.value = user
                    }

                }
                _error.value = exception?.message
            }
        //}
    }

    fun signOut () {
        FireAuthUtil.signOut()
        _error.value = null
        _authUser.value = null
        _user.value = null
    }

    fun getUserFromFirestore(userUID: String) : User? {
        if(userUID.isEmpty()){
            return null
        }
        lateinit var userReceived: User
        viewModelScope.launch {
            StorageUtil.getUserFromFirestore(userUID){ user ->
                userReceived = user
            }
        }
        return userReceived
    }

    fun getLocationFromFirestore(){
        viewModelScope.launch {
            StorageUtil.getLocationFromFirestore()
        }
    }
    fun addLocationsToFirestore(location:Location) {
        viewModelScope.launch {
            StorageUtil.addLocationToFirestore(location){ exception ->
                _error.value = exception?.message
            }
        }
    }

    fun uploadToStorage(imageName: String, path: String) {
        viewModelScope.launch {
            StorageUtil.getFileFromPath(path)?.let { inputStream ->
                StorageUtil.uploadFile(inputStream, imageName)
            }
        }
    }




}