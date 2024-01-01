package pt.isec.amovtp.touristapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import pt.isec.amovtp.touristapp.R
import pt.isec.amovtp.touristapp.data.Category
import pt.isec.amovtp.touristapp.ui.composables.ErrorAlertDialog
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel

fun getImageVectorFromName (iconName: String) : ImageVector? {
    return when (iconName) {
        "Filled.Call" -> Icons.Default.Call
        "Filled.Face" -> Icons.Default.Face
        "Filled.Favorite" -> Icons.Default.Favorite
        "Filled.Delete" -> Icons.Default.Delete
        "Filled.ShoppingCart" -> Icons.Default.ShoppingCart
        "Filled.Home" -> Icons.Default.Home
        "Filled.Build" -> Icons.Default.Build
        "Filled.Warning" -> Icons.Default.Warning
        "Filled.LocationOn" -> Icons.Default.LocationOn
        "Filled.Create" -> Icons.Default.Create
        "Filled.Email" -> Icons.Default.Email
        "Filled.Star" -> Icons.Default.Star
        "Filled.Lock" -> Icons.Default.Lock
        else -> null
    }
}

@Composable
fun EditCategoryScreen(modifier: Modifier, navController: NavHostController?, locationViewModel: LocationViewModel,firebaseViewModel: FirebaseViewModel) {
    val selectedCategory = locationViewModel.selectedCategory
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val userUID = firebaseViewModel.authUser.value!!.uid

    var expanded by remember { mutableStateOf(false) }

    var selectedIcon by remember { mutableStateOf(getImageVectorFromName(selectedCategory!!.icon)) }
    val categoryName by remember { mutableStateOf(selectedCategory!!.name) }
    var categoryDescription by remember { mutableStateOf(selectedCategory!!.description) }

    var isValidForm by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    fun validateForm () {
        isValidForm = selectedIcon != null &&
                categoryName.isNotBlank() &&
                categoryDescription.isNotBlank()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(12.dp)
            .fillMaxSize()
    ) {
        if(isError)
            ErrorAlertDialog {
                isError = false
            }

        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 100.dp)
        ) {
            Text(
                text = categoryName,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(8.dp, 0.dp)
            )

            Box (
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .wrapContentSize(align = Alignment.Center)
                    .padding(0.dp, 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(
                    onClick = {
                        expanded = true
                    }
                ) {
                    if(selectedIcon == null)
                        Icon(Icons.Default.List, contentDescription = "List")
                    else
                        Icon(selectedIcon!!, contentDescription = selectedIcon!!.name)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    scrollState = scrollState
                ) {
                    for (icon in icons) {
                        DropdownMenuItem(
                            text = {
                                Text(icon.name)
                            },
                            leadingIcon = { Icon(imageVector = icon, contentDescription = icon.name) },
                            onClick = {
                                expanded = false
                                selectedIcon = icon
                                validateForm()
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            label = { Text(text = stringResource(id = R.string.msgDescription)) },
            value = categoryDescription,
            singleLine = true,
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
            },
            onValueChange = {
                categoryDescription = it
                validateForm()
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (!isValidForm)
                    isError = true
                else {
                    val category = Category(
                        categoryName,
                        categoryDescription,
                        selectedIcon!!.name,
                        selectedCategory!!.totalPois,
                        0,
                        emptyList(), userUID
                    )
                    firebaseViewModel.addCategoryToFirestore(category) {
                        navController?.popBackStack()
                    }
                }
            }
        ) {
            Text(text = stringResource(id = R.string.btnSubmit))
        }
    }
}

@Composable
fun LandscapeEditCategoryScreen(navController: NavHostController, modifier: Modifier, locationViewModel: LocationViewModel, firebaseViewModel: FirebaseViewModel) {
    val selectedCategory = locationViewModel.selectedCategory
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val userUID = firebaseViewModel.authUser.value!!.uid

    var expanded by remember { mutableStateOf(false) }

    var selectedIcon by remember { mutableStateOf(getImageVectorFromName(selectedCategory!!.icon)) }
    val categoryName by remember { mutableStateOf(selectedCategory!!.name) }
    var categoryDescription by remember { mutableStateOf(selectedCategory!!.description) }

    var isValidForm by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    fun validateForm () {
        isValidForm = selectedIcon != null &&
                categoryName.isNotBlank() &&
                categoryDescription.isNotBlank()
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(64.dp, 12.dp)
            .fillMaxSize()
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 15.dp)
        ) {
            Text(
                text = categoryName,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(8.dp, 0.dp)
            )

            Box (
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .wrapContentSize(align = Alignment.Center)
                    .padding(0.dp, 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(
                    onClick = {
                        expanded = true
                    }
                ) {
                    if(selectedIcon == null)
                        Icon(Icons.Default.List, contentDescription = "List")
                    else
                        Icon(selectedIcon!!, contentDescription = selectedIcon!!.name)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    scrollState = scrollState
                ) {
                    for (icon in icons) {
                        DropdownMenuItem(
                            text = {
                                Text(icon.name)
                            },
                            leadingIcon = { Icon(imageVector = icon, contentDescription = icon.name) },
                            onClick = {
                                expanded = false
                                selectedIcon = icon
                                validateForm()
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            label = { Text(text = stringResource(id = R.string.msgDescription)) },
            value = categoryDescription,
            singleLine = true,
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
            },
            onValueChange = {
                categoryDescription = it
                validateForm()
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (!isValidForm)
                    isError = true
                else {
                    val category = Category(
                        categoryName,
                        categoryDescription,
                        selectedIcon!!.name,
                        selectedCategory!!.totalPois,
                        0,
                        emptyList(), userUID
                    )
                    firebaseViewModel.addCategoryToFirestore(category) {
                        navController.popBackStack()
                    }
                }
            }
        ) {
            Text(text = stringResource(id = R.string.btnSubmit))
        }
    }
}