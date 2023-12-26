package pt.isec.amovtp.touristapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
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

val icons = listOf(
    Icons.Default.Call,
    Icons.Default.Face,
    Icons.Default.Favorite,
    Icons.Default.Delete,
    Icons.Default.ShoppingCart,
    Icons.Default.Home,
    Icons.Default.Build,
    Icons.Default.Warning,
    Icons.Default.LocationOn,
    Icons.Default.Create,
    Icons.Default.Email,
    Icons.Default.Star,
    Icons.Default.Lock
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(modifier: Modifier, navController: NavHostController?, firebaseViewModel: FirebaseViewModel) {
    val focusManager = LocalFocusManager.current
    val userUID = firebaseViewModel.authUser.value!!.uid

    var expanded by remember { mutableStateOf(false) }

    var selectedIcon by remember { mutableStateOf<ImageVector?>(null) }
    var categoryName by remember { mutableStateOf("") }
    var categoryDescription by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    var isError by remember { mutableStateOf(false) }
    var isValidForm by remember { mutableStateOf(false) }

    fun validateForm() {
        isValidForm = selectedIcon != null &&
                categoryDescription.isNotBlank() &&
                categoryDescription.isNotBlank()
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(12.dp)
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.msgAddCategory),
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 24.dp)
        )

        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if(isError)
                ErrorAlertDialog {
                    isError = false
                }

            OutlinedTextField(
                label = { Text(text = stringResource(id = R.string.msgName)) },
                value = categoryName,
                singleLine = true,
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                onValueChange = {
                    categoryName = it
                    validateForm()
                }
            )

            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(align = Alignment.Center),
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

        Spacer(modifier = Modifier.height(8.dp))
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
                        categoryName, categoryDescription, selectedIcon!!.name, 0, 0,
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
fun LandscapeAddCategoryScreen(modifier: Modifier = Modifier, navController: NavHostController?, firebaseViewModel: FirebaseViewModel) {
    val focusManager = LocalFocusManager.current
    val userUID = firebaseViewModel.authUser.value!!.uid

    var expanded by remember { mutableStateOf(false) }

    var selectedIcon by remember { mutableStateOf<ImageVector?>(null) }
    var categoryName by remember { mutableStateOf("") }
    var categoryDescription by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    var isError by remember { mutableStateOf(false) }
    var isValidForm by remember { mutableStateOf(false) }

    fun validateForm() {
        isValidForm = selectedIcon != null &&
                categoryDescription.isNotBlank() &&
                categoryDescription.isNotBlank()
    }

    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(64.dp, 12.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(id = R.string.msgAddCategory),
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 24.dp)
        )

        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if(isError)
                ErrorAlertDialog {
                    isError = false
                }

            OutlinedTextField(
                label = { Text(text = stringResource(id = R.string.msgName)) },
                value = categoryName,
                singleLine = true,
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                onValueChange = {
                    categoryName = it
                    validateForm()
                },
                modifier = Modifier.weight(4f)
            )

            Box (
                modifier = Modifier
                    .weight(1f)
                    .wrapContentSize(align = Alignment.Center),
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

        Spacer(modifier = Modifier.height(8.dp))
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
                        categoryName, categoryDescription, selectedIcon!!.name, 0, 0,
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