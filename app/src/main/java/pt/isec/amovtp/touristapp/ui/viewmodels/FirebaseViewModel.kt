package pt.isec.amovtp.touristapp.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import pt.isec.amovtp.touristapp.utils.FireAuthUtil

data class User(val username: String, val email: String, val picture: String?)

fun FirebaseUser.toUser() : User {
    val username = this.displayName ?: ""
    val strEmail = this.email ?: ""
    val photoUrl = this.photoUrl?.toString()

    return User(username, strEmail, photoUrl)
}

class FirebaseViewModel : ViewModel() {
    private val _error = mutableStateOf<String?>(null)
    val error : MutableState<String?>
        get() = _error

    private val _user = mutableStateOf(FireAuthUtil.currentUser?.toUser())
    val user : MutableState<User?>
        get() = _user

    fun createUserWithEmail(email: String, password: String) {
        if(email.isBlank() || password.isBlank())
            return
        viewModelScope.launch {
            FireAuthUtil.createUserWithEmail(email, password) { exception ->
                if(exception == null)
                    _user.value = FireAuthUtil.currentUser?.toUser()
                _error.value = exception?.message
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if(email.isBlank() || password.isBlank())
            return
        FireAuthUtil.signInWithEmail(email, password) { exception ->
            if(exception == null)
                _user.value = FireAuthUtil.currentUser?.toUser()
            _error.value = exception?.message
        }
    }

    fun signOut () {
        FireAuthUtil.signOut()
        _error.value = null
        _user.value = null
    }

    fun addDataToFirestore () {
        viewModelScope.launch {

        }
    }
}