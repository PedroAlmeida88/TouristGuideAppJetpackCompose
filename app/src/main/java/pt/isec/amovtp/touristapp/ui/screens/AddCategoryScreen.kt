package pt.isec.amovtp.touristapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(modifier: Modifier, navController: NavHostController?, firebaseViewModel: FirebaseViewModel) {
    val icons = listOf(
        Icons.Default.Call,
        Icons.Default.Face,
        Icons.Default.Favorite,
        Icons.Default.Delete,
        Icons.Default.ShoppingCart,
        Icons.Default.Home,
        Icons.Default.Build,
        Icons.Default.Place,
        Icons.Default.Warning,
        Icons.Default.LocationOn,
        Icons.Default.Create,
        Icons.Default.Email,
        Icons.Default.Star,
        Icons.Default.Lock
    )

    var expanded by remember { mutableStateOf(false) }

    var selectedIcon by remember { mutableStateOf("") }
    var categoryName by remember { mutableStateOf("") }
    var categoryDescription by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = categoryName,
            onValueChange = {
                categoryName = it
            }
        )

        /*ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { !expanded }
        ) {
            Text(text = "Lista")
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                for (icon in icons) {
                    DropdownMenuItem(
                        text = { icon.name },
                        leadingIcon = { Icon(icon, contentDescription = icon.name) },
                        onClick = {
                            selectedIcon = icon.name
                        }
                    )
                }
            }
        }*/

        OutlinedTextField(
            value = categoryDescription,
            onValueChange = {
                categoryDescription = it
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                
            }
        ) {
            Text(text = "Submit")
        }
    }
}
