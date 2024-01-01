package pt.isec.amovtp.touristapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import pt.isec.amovtp.touristapp.R
import pt.isec.amovtp.touristapp.data.User
import pt.isec.amovtp.touristapp.ui.composables.ErrorAlertDialog
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel

@Composable
fun RegisterScreen(navController: NavHostController, firebaseViewModel: FirebaseViewModel) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var error by remember { firebaseViewModel.error }
    var isFormValid by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var isButtonClicked by remember { mutableStateOf(false) }
    var passwordVisibility by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    fun validateForm () {
        isFormValid = firstName.isNotBlank() && lastName.isNotBlank() && password.isNotBlank() && email.isNotBlank()
    }

    LaunchedEffect(key1 = isButtonClicked) {
        if(error == null && isButtonClicked){
            firebaseViewModel.signOut()
            navController.navigate(Screens.LOGIN.route)
        }
    }

    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isError || error != null) {
            ErrorAlertDialog {
                isError = false
                isButtonClicked = false
                error = null
            }
        }

        Text(
            text = stringResource(id = R.string.msgCreateAccount),
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 26.sp,
            modifier = Modifier.padding(24.dp, 0.dp, 24.dp, 64.dp)
        )

        Column (
            modifier = Modifier.fillMaxWidth()
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange ={
                        firstName = it
                        validateForm()
                    },
                    singleLine = true,
                    keyboardActions = KeyboardActions{
                        focusManager.moveFocus(FocusDirection.Next)
                    },
                    label = { Text(text = stringResource(id = R.string.msgFirstName),color = MaterialTheme.colorScheme.tertiary) },
                    modifier = Modifier.weight(1f, false)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = lastName,
                    onValueChange ={
                        lastName = it
                        validateForm()
                    },
                    singleLine = true,
                    keyboardActions = KeyboardActions{
                        focusManager.moveFocus(FocusDirection.Next)
                    },
                    label = { Text(text = stringResource(id = R.string.msgLastName),color = MaterialTheme.colorScheme.tertiary) },
                    modifier = Modifier.weight(1f, false)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = email,
            onValueChange ={
                email = it
                validateForm()
            },
            singleLine = true,
            keyboardActions = KeyboardActions{
                focusManager.moveFocus(FocusDirection.Next)
            },
            label = { Text(text = stringResource(id = R.string.msgUsername),color = MaterialTheme.colorScheme.tertiary) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange ={
                password = it
                validateForm()
            },
            singleLine = true,
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
            },
            visualTransformation = if (passwordVisibility) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation('*')
            },
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisibility = !passwordVisibility }
                ) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            label = { Text(text = stringResource(R.string.msgPassword),color = MaterialTheme.colorScheme.tertiary) }
        )

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                if (!isFormValid) {
                    isError = true
                } else {
                    val user = User(email, firstName, lastName)
                    firebaseViewModel.createUserWithEmail(user, password) {
                        isButtonClicked = true
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        ) {
            Text(text = stringResource(id = R.string.btnRegister))
        }
    }
}

@Composable
fun LandscapeRegisterScreen(navController: NavHostController, firebaseViewModel: FirebaseViewModel) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var error by remember { firebaseViewModel.error }
    var isFormValid by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var isButtonClicked by remember { mutableStateOf(false) }
    var passwordVisibility by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    fun validateForm () {
        isFormValid = firstName.isNotBlank() && lastName.isNotBlank() && password.isNotBlank() && email.isNotBlank()
    }

    LaunchedEffect(key1 = isButtonClicked) {
        if(error == null && isButtonClicked){
            firebaseViewModel.signOut()
            navController.navigate(Screens.LOGIN.route)
        }
    }

    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(64.dp, 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (isError || error != null) {
            ErrorAlertDialog {
                isError = false
                isButtonClicked = false
                error = null
            }
        }

        Text(
            text = stringResource(id = R.string.msgCreateAccount),
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(24.dp, 0.dp, 24.dp, 24.dp)
        )

        Column (
            modifier = Modifier.fillMaxWidth()
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth(1f)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange ={
                        firstName = it
                        validateForm()
                    },
                    singleLine = true,
                    keyboardActions = KeyboardActions{
                        focusManager.moveFocus(FocusDirection.Next)
                    },
                    label = { Text(text = stringResource(id = R.string.msgFirstName),color = MaterialTheme.colorScheme.tertiary) },
                    modifier = Modifier.weight(1f, false)
                )
                Spacer(modifier = Modifier.width(24.dp))
                OutlinedTextField(
                    value = lastName,
                    onValueChange ={
                        lastName = it
                        validateForm()
                    },
                    singleLine = true,
                    keyboardActions = KeyboardActions{
                        focusManager.moveFocus(FocusDirection.Next)
                    },
                    label = { Text(text = stringResource(id = R.string.msgLastName),color = MaterialTheme.colorScheme.tertiary) },
                    modifier = Modifier.weight(1f, false)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = email,
            onValueChange ={
                email = it
                validateForm()
            },
            singleLine = true,
            keyboardActions = KeyboardActions{
                focusManager.moveFocus(FocusDirection.Next)
            },
            label = { Text(text = stringResource(id = R.string.msgUsername),color = MaterialTheme.colorScheme.tertiary) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange ={
                password = it
                validateForm()
            },
            singleLine = true,
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
            },
            visualTransformation = if (passwordVisibility) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation('*')
            },
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisibility = !passwordVisibility }
                ) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            label = { Text(text = stringResource(R.string.msgPassword),color = MaterialTheme.colorScheme.tertiary) }
        )

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                if (!isFormValid) {
                    isError = true
                } else {
                    val user = User(email, firstName, lastName)
                    firebaseViewModel.createUserWithEmail(user, password) {
                        isButtonClicked = true
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        ) {
            Text(text = stringResource(id = R.string.btnRegister))
        }
    }
}