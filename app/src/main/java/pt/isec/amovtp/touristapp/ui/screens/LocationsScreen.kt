package pt.isec.amovtp.touristapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import pt.isec.amovtp.touristapp.R
import pt.isec.amovtp.touristapp.data.Location
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel
import pt.isec.amovtp.touristapp.utils.location.LocationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(navController: NavHostController?,viewModel : LocationViewModel,firebaseViewModel: FirebaseViewModel) {
    //geopoin
    val myLocation = viewModel.currentLocation.observeAsState()

    val context = LocalContext.current
    var locations by remember { mutableStateOf<List<Location>>(emptyList())}
    val userUID = firebaseViewModel.authUser.value!!.uid


    //sempre que é iniciado, carrega as localizações
    LaunchedEffect(Unit) {
        firebaseViewModel.getLocationFromFirestore { loadedLocations ->
            locations = loadedLocations
            for (l in locations) {
                //caso já tenha votado ou tenha sido criado por ele
                if (userUID in l.userUIDsApprovals || userUID == l.userUID) {
                    l.enableBtn = false
                }
            }

        }
    }
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()

            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ){

            Button(
                onClick = {
                    locations = locations.sortedBy { it.name }
                }
            ) {
                Text(text = stringResource(id = R.string.msgAlphabetic))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = {
                    locations = locations.sortedBy { location ->
                        LocationUtils().haversine(
                            myLocation.value?.latitude ?: 0.0,
                            myLocation.value?.longitude ?: 0.0,
                            location.latitude,
                            location.longitude
                        )
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.msgCloseToMe))
            }
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(locations) { location ->
                val borderColor = when (location.approvals) {
                    0 -> Color.Red
                    1 -> Color.Yellow
                    else -> MaterialTheme.colorScheme.tertiary
                }
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(2.dp, borderColor, shape = RoundedCornerShape(16.dp))
                        .clip(shape = RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),

                    onClick = {
                        viewModel.selectedLocation = location
                        navController?.navigate(Screens.POI.route)
                    }
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            //.padding(8.dp)
                            .wrapContentHeight(Alignment.Bottom),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        //Image(painter = painterResource(u = ), contentDescription = "city picture")
                        AsyncImage(model = location.photoUrl, contentDescription = "City Picture")
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .wrapContentHeight(Alignment.Bottom),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if(location.approvals < 2) {
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {
                                                firebaseViewModel.updateAprovalLocationInFirestore(
                                                    location,
                                                    userUID
                                                )
                                                firebaseViewModel.getLocationFromFirestore { loadedLocations ->
                                                    locations = loadedLocations
                                                    for (l in locations) {
                                                        if (userUID in l.userUIDsApprovals || userUID == l.userUID) {
                                                            l.enableBtn = false
                                                        }
                                                    }
                                                }
                                            },
                                            modifier = Modifier.padding(8.dp),
                                            enabled = location.enableBtn
                                        ) {
                                            Icon(
                                                imageVector = Default.CheckCircle,
                                                contentDescription = null
                                            )
                                        }
                                        Text(text = "${location.approvals}/2")
                                    }

                                }

                                if(location.userUID == userUID) {
                                    IconButton(
                                        onClick = {
                                            viewModel.selectedLocation = location
                                            navController?.navigate(Screens.EDIT_LOCATION.route)
                                        },
                                        modifier = Modifier.padding(8.dp),
                                    ) {
                                        Icon(
                                            imageVector = Default.Edit,
                                            contentDescription = null
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            if(location.totalPois == 0) {
                                                firebaseViewModel.deleteLocationsFromFirestore(
                                                    location
                                                )
                                                firebaseViewModel.getLocationFromFirestore { loadedLocations ->
                                                    locations = loadedLocations
                                                }
                                                Toast.makeText(context, "Localização eliminada com sucesso!", Toast.LENGTH_LONG).show()
                                            }else
                                                Toast.makeText(context, "Localização não eliminada.Já existem POIS associados", Toast.LENGTH_LONG).show()

                                        },
                                        modifier = Modifier.padding(8.dp),
                                    ) {
                                        Icon(
                                            imageVector = Default.Delete,
                                            contentDescription = null
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = location.name, fontSize = 20.sp)
                                    Text(text = location.description, fontSize = 14.sp)
                                    Text(
                                        text = "${location.latitude} ${location.longitude}",
                                        fontSize = 8.sp
                                    )
                                    if (location.writenCoords)
                                        Text(text = stringResource(id = R.string.msgCoordsWritten), fontSize = 8.sp)
                                    else
                                        Text(
                                            text = stringResource(id = R.string.msgCoordsLocation),
                                            fontSize = 8.sp
                                        )
                                }
                            }
                        }
                    }

                }
            }
        }

    }

}

@Composable
fun LandscapeLocationsScreen(navController: NavHostController?, viewModel: LocationViewModel, firebaseViewModel: FirebaseViewModel) {
    val myLocation = viewModel.currentLocation.observeAsState()

    val context = LocalContext.current
    var locations by remember { mutableStateOf<List<Location>>(emptyList())}
    val userUID = firebaseViewModel.authUser.value!!.uid


    //sempre que é iniciado, carrega as localizações
    LaunchedEffect(Unit) {
        firebaseViewModel.getLocationFromFirestore { loadedLocations ->
            locations = loadedLocations
            for (l in locations) {
                //caso já tenha votado ou tenha sido criado por ele
                if (userUID in l.userUIDsApprovals || userUID == l.userUID) {
                    l.enableBtn = false
                }
            }

        }
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {

            Button(
                onClick = {
                    locations = locations.sortedBy { it.name }
                }
            ) {
                Text(text = stringResource(id = R.string.msgAlphabetic))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = {
                    locations = locations.sortedBy { location ->
                        LocationUtils().haversine(
                            myLocation.value?.latitude ?: 0.0,
                            myLocation.value?.longitude ?: 0.0,
                            location.latitude,
                            location.longitude
                        )
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.msgCloseToMe))
            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(locations) { location ->
                val borderColor = when (location.approvals) {
                    0 -> Color.Red
                    1 -> Color.Yellow
                    else -> MaterialTheme.colorScheme.tertiary
                }
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(2.dp, borderColor, shape = RoundedCornerShape(16.dp))
                        .clip(shape = RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),

                    onClick = {
                        viewModel.selectedLocation = location
                        navController?.navigate(Screens.POI.route)
                    }
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentHeight(Alignment.Bottom),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        //Image(painter = painterResource(u = ), contentDescription = "city picture")
                        AsyncImage(model = location.photoUrl, contentDescription = "City Picture")
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .wrapContentHeight(Alignment.Bottom),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if(location.approvals < 2) {
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {
                                                firebaseViewModel.updateAprovalLocationInFirestore(
                                                    location,
                                                    userUID
                                                )
                                                firebaseViewModel.getLocationFromFirestore { loadedLocations ->
                                                    locations = loadedLocations
                                                    for (l in locations) {
                                                        if (userUID in l.userUIDsApprovals || userUID == l.userUID) {
                                                            l.enableBtn = false
                                                        }
                                                    }
                                                }
                                            },
                                            modifier = Modifier.padding(8.dp),
                                            enabled = location.enableBtn
                                        ) {
                                            Icon(
                                                imageVector = Default.CheckCircle,
                                                contentDescription = null
                                            )
                                        }
                                        Text(text = "${location.approvals}/2")
                                    }

                                }

                                if(location.userUID == userUID) {
                                    IconButton(
                                        onClick = {
                                            viewModel.selectedLocation = location
                                            navController?.navigate(Screens.EDIT_LOCATION.route)
                                        },
                                        modifier = Modifier.padding(8.dp),
                                    ) {
                                        Icon(
                                            imageVector = Default.Edit,
                                            contentDescription = null
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            if(location.totalPois == 0) {
                                                firebaseViewModel.deleteLocationsFromFirestore(
                                                    location
                                                )
                                                firebaseViewModel.getLocationFromFirestore { loadedLocations ->
                                                    locations = loadedLocations
                                                }
                                                Toast.makeText(context, "Localização eliminada com sucesso!", Toast.LENGTH_LONG).show()
                                            }else
                                                Toast.makeText(context, "Localização não eliminada.Já existem POIS associados", Toast.LENGTH_LONG).show()

                                        },
                                        modifier = Modifier.padding(8.dp),
                                    ) {
                                        Icon(
                                            imageVector = Default.Delete,
                                            contentDescription = null
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = location.name, fontSize = 20.sp)
                                    Text(text = location.description, fontSize = 14.sp)
                                    Text(
                                        text = "${location.latitude} ${location.longitude}",
                                        fontSize = 8.sp
                                    )
                                    if (location.writenCoords)
                                        Text(text = stringResource(id = R.string.msgCoordsWritten), fontSize = 8.sp)
                                    else
                                        Text(
                                            text = stringResource(id = R.string.msgCoordsLocation),
                                            fontSize = 8.sp
                                        )
                                }
                            }
                        }
                    }

                }
            }
        }

    }
}