package pt.isec.amovtp.touristapp.ui.screens

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.isec.amovtp.touristapp.data.Location
import pt.isec.amovtp.touristapp.ui.composables.SelectGalleryImage
import pt.isec.amovtp.touristapp.ui.composables.TakePhoto
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel


@Composable
fun AddLocationScreen(modifier: Modifier.Companion, locationViewModel: LocationViewModel, firebaseViewModel: FirebaseViewModel) {

    var locationName by remember { mutableStateOf("") }
    var locationDescription by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var isFormValid by remember { mutableStateOf(false) }
    var isInputEnabled by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf("gallery") }

    val location = locationViewModel.currentLocation.observeAsState()


    fun validateForm() {
        val longitudeDouble: Double? = longitude.toDoubleOrNull()
        val latitudeDouble: Double? = latitude.toDoubleOrNull()

        isFormValid = locationName.isNotBlank() &&
                locationDescription.isNotBlank() &&
                longitudeDouble != null &&
                latitudeDouble != null &&
                locationViewModel.imagePath.value != null
        /*
        Log.i("FormValidation", "Location Name: $locationName")
        Log.i("FormValidation", "Location Description: $locationDescription")
        Log.i("FormValidation", "Longitude: $longitude")
        Log.i("FormValidation", "Longitude Double: $longitudeDouble")
        Log.i("FormValidation", "Longitude: $latitude")
        Log.i("FormValidation", "Longitude Double: $latitudeDouble")

         */
    }

    Column (
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {

        if(isError)
            Text(
                text = "Preencher todos os campos (corretamente)",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                color = Color.Red
            )
        Text(
            text = "Location Name",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        OutlinedTextField(
            value = locationName,
            onValueChange ={
                locationName = it
                validateForm()
            },
            label = { Text(text = "Location Name") },

        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Location Description",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        OutlinedTextField(
            value = locationDescription,
            onValueChange ={
                locationDescription = it
                validateForm()
            },
            label = { Text(text = "Location Description") },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    isInputEnabled = false;
                    latitude = (location.value?.latitude ?: 0.0).toString()
                    longitude = (location.value?.longitude ?: 0.0).toString()
                    validateForm()

                },
                modifier = Modifier
                    .weight(1f)
                    .height(96.dp)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                shape = CutCornerShape(percent = 0)
                ) {
                Text(text = "Get coordinates from current location")
            }

            Button(
                onClick = {
                    isInputEnabled = true
                    validateForm()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(96.dp)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                shape = CutCornerShape(percent = 0)
                ) {
                Text(text = "Write coordinates")
            }
        }

        OutlinedTextField(
            value = longitude,
            onValueChange ={
                longitude = it
                validateForm()
            },
            label = { Text(text = "Longitude") },
            enabled = isInputEnabled
        )
        OutlinedTextField(
            value = latitude,
            onValueChange ={
                latitude = it
                validateForm()
            },
            label = { Text(text = "Latitude") },
            enabled = isInputEnabled
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(16.dp)
        ) {
            Button(
                onClick = {type = "picture" },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            ) {
                Text(text = "Take Photo")
            }
            Button(
                onClick = {
                          type = "galerry"
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            ) {
                Text(text = "Load Photo from Gallery")
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .padding(8.dp)
                .weight(1f)
        ) {
            if (type.equals("picture")) {
                TakePhoto(locationViewModel.imagePath, Modifier.fillMaxSize())
                validateForm()
            } else {
                SelectGalleryImage(locationViewModel.imagePath, Modifier.fillMaxSize())
                validateForm()
            }
        }
        Button(
            onClick = {
                if (!isFormValid) {
                    isError = true
                } else {
                    isError = false
                    var location = Location(
                        name = locationName,
                        description = locationDescription,
                        latitude = latitude.toDouble(),
                        longitude = longitude.toDouble(),
                        photoUrl = ""
                    )
                    firebaseViewModel.addLocationsToFirestore(location)
                    firebaseViewModel.uploadToStorage( imageName = locationName, path = locationViewModel.imagePath.value ?: "")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        ) {
            Text(text = "Submit")
        }


    }


}

