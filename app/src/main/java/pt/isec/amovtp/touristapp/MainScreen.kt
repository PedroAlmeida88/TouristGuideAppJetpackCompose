package pt.isec.amovtp.touristapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHost

enum class Screens(val display: String, val showAppBar: Boolean) {
    MENU("Menu", false),
    LOGIN("Login", true),

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold (
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(

        )
    }
}