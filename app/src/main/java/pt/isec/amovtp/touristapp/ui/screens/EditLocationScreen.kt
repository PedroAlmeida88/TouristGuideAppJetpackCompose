package pt.isec.amovtp.touristapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import pt.isec.amovtp.touristapp.R
import pt.isec.amovtp.touristapp.data.Location
import pt.isec.amovtp.touristapp.ui.composables.ErrorAlertDialog
import pt.isec.amovtp.touristapp.ui.composables.TakePhotoOrLoadFromGallery
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel

@Composable
fun EditLocationScreen( navController: NavHostController?, locationViewModel: LocationViewModel, firebaseViewModel: FirebaseViewModel) {
    //location atual
    val selectedLocation = locationViewModel.selectedLocation
    val userUID = firebaseViewModel.authUser.value!!.uid
    val focusManager = LocalFocusManager.current
    val location = locationViewModel.currentLocation.observeAsState()

    val context = LocalContext.current
    var locationName by remember { mutableStateOf(selectedLocation!!.name) }
    var locationDescription by remember { mutableStateOf(selectedLocation!!.description) }
    var longitude by remember { mutableStateOf(selectedLocation!!.longitude.toString()) }
    var latitude by remember { mutableStateOf(selectedLocation!!.latitude.toString()) }

    var isFormValid by remember { mutableStateOf(false) }
    var isInputEnabled by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var writenCoords by remember { mutableStateOf(selectedLocation!!.writenCoords) }


    fun validateForm() {
        val longitudeDouble: Double? = longitude.toDoubleOrNull()
        val latitudeDouble: Double? = latitude.toDoubleOrNull()

        isFormValid = locationName.isNotBlank() &&
                locationDescription.isNotBlank() &&
                longitudeDouble != null &&
                latitudeDouble != null &&
                locationViewModel.imagePath.value != null

    }

    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if(isError)
            ErrorAlertDialog {
                isError = false
            }

        Text(
            text = locationName,
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = locationDescription,
            onValueChange ={
                locationDescription = it
                validateForm()
            },
            singleLine = true,
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
            },
            label = { Text(text = stringResource(id = R.string.msgDescription), color = MaterialTheme.colorScheme.tertiary) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.msgGetCoordinates),
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(6.dp, 0.dp)
            )
            Switch(
                checked = isInputEnabled,
                onCheckedChange = {
                    isInputEnabled = it
                    writenCoords = it
                },
                modifier = Modifier.padding(4.dp, 0.dp)
            )
            Text(
                text = stringResource(id = R.string.msgWriteCoordinates),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(6.dp, 0.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 12.dp)
        ) {
            OutlinedTextField(
                value = longitude,
                onValueChange ={
                    longitude = it
                    validateForm()
                },
                singleLine = true,
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                label = { Text(text = stringResource(id = R.string.msgLongitude),color = MaterialTheme.colorScheme.tertiary) },
                enabled = isInputEnabled,
                modifier = Modifier
                    .weight(1f, false)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = latitude,
                onValueChange ={
                    latitude = it
                    validateForm()
                },
                singleLine = true,
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                },
                label = { Text(text = stringResource(id = R.string.msgLatitude),color = MaterialTheme.colorScheme.tertiary) },
                enabled = isInputEnabled,
                modifier = Modifier
                    .weight(1f, false)
            )
        }

        Box {
            locationViewModel.imagePath.value = selectedLocation?.photoUrl
            TakePhotoOrLoadFromGallery(locationViewModel.imagePath, Modifier.fillMaxWidth())
            validateForm()
        }
        Button(
            onClick = {
                if (!isFormValid) {
                    isError = true
                } else {
                    isError = false
                    val location = Location(
                        name = locationName,
                        description = locationDescription,
                        latitude = latitude.toDouble(),
                        longitude = longitude.toDouble(),
                        photoUrl = selectedLocation!!.photoUrl,
                        writenCoords = writenCoords,
                        approvals = 0,
                        userUIDsApprovals = emptyList(),
                        userUID = userUID,
                        totalPois = selectedLocation.totalPois
                    )
                    firebaseViewModel.addLocationsToFirestore(location)
                    firebaseViewModel.uploadLocationToStorage(directory = "images/"+locationName ,imageName = locationName, path = locationViewModel.imagePath.value ?: "")
                    locationViewModel.imagePath.value = null
                    navController?.popBackStack()
                    Toast.makeText(context,"Success", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        ) {
            Text(text = stringResource(id = R.string.btnSubmit))
        }
    }
}

@Composable
fun LandscapeEditLocationScreen(navController: NavHostController?, locationViewModel: LocationViewModel, firebaseViewModel: FirebaseViewModel) {
    val selectedLocation = locationViewModel.selectedLocation
    val userUID = firebaseViewModel.authUser.value!!.uid
    val focusManager = LocalFocusManager.current
    val location = locationViewModel.currentLocation.observeAsState()

    val context = LocalContext.current
    var locationName by remember { mutableStateOf(selectedLocation!!.name) }
    var locationDescription by remember { mutableStateOf(selectedLocation!!.description) }
    var longitude by remember { mutableStateOf(selectedLocation!!.longitude.toString()) }
    var latitude by remember { mutableStateOf(selectedLocation!!.latitude.toString()) }

    var isFormValid by remember { mutableStateOf(false) }
    var isInputEnabled by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var writenCoords by remember { mutableStateOf(selectedLocation!!.writenCoords) }


    fun validateForm() {
        val longitudeDouble: Double? = longitude.toDoubleOrNull()
        val latitudeDouble: Double? = latitude.toDoubleOrNull()

        isFormValid = locationName.isNotBlank() &&
                locationDescription.isNotBlank() &&
                longitudeDouble != null &&
                latitudeDouble != null &&
                locationViewModel.imagePath.value != null

    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if(isError)
            ErrorAlertDialog {
                isError = false
            }

        Text(
            text = locationName,
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        Row {
            Column (
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(8.dp, 0.dp)
            ) {
                OutlinedTextField(
                    value = locationDescription,
                    onValueChange ={
                        locationDescription = it
                        validateForm()
                    },
                    singleLine = true,
                    keyboardActions = KeyboardActions {
                        focusManager.clearFocus()
                    },
                    label = { Text(text = stringResource(id = R.string.msgDescription)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.msgGetCoordinates),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(6.dp, 0.dp)
                    )
                    Switch(
                        checked = isInputEnabled,
                        onCheckedChange = {
                            isInputEnabled = it
                            writenCoords = it
                        },
                        modifier = Modifier.padding(4.dp, 0.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.msgWriteCoordinates),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(6.dp, 0.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 12.dp)
                ) {
                    OutlinedTextField(
                        value = longitude,
                        onValueChange ={
                            longitude = it
                            validateForm()
                        },
                        singleLine = true,
                        keyboardActions = KeyboardActions {
                            focusManager.moveFocus(FocusDirection.Next)
                        },
                        label = { Text(text = stringResource(id = R.string.msgLongitude)) },
                        enabled = isInputEnabled,
                        modifier = Modifier
                            .weight(1f, false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = latitude,
                        onValueChange ={
                            latitude = it
                            validateForm()
                        },
                        singleLine = true,
                        keyboardActions = KeyboardActions {
                            focusManager.clearFocus()
                        },
                        label = { Text(text = stringResource(id = R.string.msgLatitude)) },
                        enabled = isInputEnabled,
                        modifier = Modifier
                            .weight(1f, false)
                    )
                }
            }

            Box (
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp, 0.dp)
                    .align(Alignment.CenterVertically)
            ) {
                locationViewModel.imagePath.value = selectedLocation?.photoUrl
                TakePhotoOrLoadFromGallery(locationViewModel.imagePath, Modifier.fillMaxWidth())
                validateForm()
            }
        }

        Button(
            onClick = {
                if (!isFormValid) {
                    isError = true
                } else {
                    isError = false
                    val location = Location(
                        name = locationName,
                        description = locationDescription,
                        latitude = latitude.toDouble(),
                        longitude = longitude.toDouble(),
                        photoUrl = selectedLocation!!.photoUrl,
                        writenCoords = writenCoords,
                        approvals = 0,
                        userUIDsApprovals = emptyList(),
                        userUID = userUID,
                        totalPois = selectedLocation.totalPois
                    )
                    firebaseViewModel.addLocationsToFirestore(location)
                    firebaseViewModel.uploadLocationToStorage(directory = "images/"+locationName ,imageName = locationName, path = locationViewModel.imagePath.value ?: "")
                    locationViewModel.imagePath.value = null
                    navController?.popBackStack()
                    Toast.makeText(context,"Success", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        ) {
            Text(text = stringResource(id = R.string.btnSubmit))
        }
    }
}