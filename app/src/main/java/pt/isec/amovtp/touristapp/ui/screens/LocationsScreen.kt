package pt.isec.amovtp.touristapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
                Text(
                    text = stringResource(id = R.string.msgAlphabetic),
                    color = MaterialTheme.colorScheme.secondary
                )
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
                Text(
                    text = stringResource(id = R.string.msgCloseToMe),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(locations) { location ->
                val warningColor = when (location.approvals) {
                    0 -> Color.Red
                    1 -> Color.Yellow
                    else -> MaterialTheme.colorScheme.primary
                }
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(shape = RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),

                    onClick = {
                        viewModel.selectedLocation = location
                        navController?.navigate(Screens.POI.route)
                    }
                ) {
                    //Coluna com to_do o conteudo da card
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            //.padding(8.dp)
                            .wrapContentHeight(Alignment.Bottom),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        //Linha do aviso
                        if(location.approvals < 2){
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .height(IntrinsicSize.Min),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Default.Warning,
                                    contentDescription = null,
                                    tint = warningColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp)) // Adiciona espaço entre o ícone e o texto
                                Text(
                                    text = "Atenção: esta localização ainda não foi aprovada (${location.approvals}/2)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.tertiary,
                                )
                            }
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(MaterialTheme.colorScheme.onTertiary)
                                //.padding(vertical = 16.dp)
                            )
                        }
                        //linha com a coluna de imagem e coluna de texto
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ){
                            //  Coluna da imagem
                            Column (
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(8.dp),
                            ){
                                AsyncImage(
                                    model = location.photoUrl,
                                    contentDescription = "City Picture"
                                )
                            }
                            //Coluna do texto
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier
                                    .weight(1.5f)
                                    .fillMaxHeight()
                                    .padding(8.dp),

                            ) {
                                Text(text = location.name, fontSize = 20.sp,color = MaterialTheme.colorScheme.tertiary)
                                Text(text = location.description, fontSize = 12.sp,color = MaterialTheme.colorScheme.tertiary)

                                Row (
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ){
                                    Text(
                                        text = "${location.latitude} ${location.longitude}",
                                        fontSize = 8.sp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    if (location.writenCoords) {
                                        Text(
                                            text = stringResource(id = R.string.msgCoordsWritten),
                                            fontSize = 6.sp,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    } else {
                                        Text(
                                            text = stringResource(id = R.string.msgCoordsLocation),
                                            fontSize = 6.sp,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }
                            }
                        }
                        if(location.approvals < 2 || location.userUID == userUID)
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(MaterialTheme.colorScheme.onTertiary)
                            )
                        //Linha do icons
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
                                            Toast.makeText(context, "Location deleted successfully!", Toast.LENGTH_LONG).show()
                                        }else
                                            Toast.makeText(context, "Location not deleted. There are already associated Points of Interest", Toast.LENGTH_LONG).show()

                                    },
                                    modifier = Modifier.padding(8.dp),
                                ) {
                                    Icon(
                                        imageVector = Default.Delete,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.onTertiary)
                    //.padding(vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
fun LandscapeLocationsScreen(navController: NavHostController?, viewModel: LocationViewModel, firebaseViewModel: FirebaseViewModel) {
    //geopoint
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
                Text(
                    text = stringResource(id = R.string.msgAlphabetic),
                    color = MaterialTheme.colorScheme.secondary
                )
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
                Text(
                    text = stringResource(id = R.string.msgCloseToMe),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(count = 2)
        ){
            items(locations) { location ->
                val warningColor = when (location.approvals) {
                    0 -> Color.Red
                    1 -> Color.Yellow
                    else -> MaterialTheme.colorScheme.primary
                }
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(shape = RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),

                    onClick = {
                        viewModel.selectedLocation = location
                        navController?.navigate(Screens.POI.route)
                    }
                ) {
                    //Coluna com to_do o conteudo da card
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            //.padding(8.dp)
                            .wrapContentHeight(Alignment.Bottom),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        //Linha do aviso
                        if(location.approvals < 2){
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .height(IntrinsicSize.Min),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Default.Warning,
                                    contentDescription = null,
                                    tint = warningColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp)) // Adiciona espaço entre o ícone e o texto
                                Text(
                                    text = "Atenção: esta localização ainda não foi aprovada (${location.approvals}/2)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.tertiary,
                                )
                            }
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(MaterialTheme.colorScheme.onTertiary)
                                //.padding(vertical = 16.dp)
                            )
                        }
                        //linha com a coluna de imagem e coluna de texto
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ){
                            //  Coluna da imagem
                            Column (
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(8.dp),
                            ){
                                AsyncImage(
                                    model = location.photoUrl,
                                    contentDescription = "City Picture"
                                )
                            }
                            //Coluna do texto
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier
                                    .weight(1.5f)
                                    .fillMaxHeight()
                                    .padding(8.dp),

                                ) {
                                Text(text = location.name, fontSize = 20.sp)
                                Text(text = location.description, fontSize = 14.sp)

                                Text(
                                    text = "${location.latitude} ${location.longitude}",
                                    fontSize = 8.sp,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                if (location.writenCoords)
                                    Text(
                                        text = stringResource(id = R.string.msgCoordsWritten),
                                        fontSize = 6.sp,
                                        color = MaterialTheme.colorScheme.tertiary

                                    )
                                else
                                    Text(
                                        text = stringResource(id = R.string.msgCoordsLocation),
                                        fontSize = 6.sp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(MaterialTheme.colorScheme.onTertiary)
                            //.padding(vertical = 16.dp)
                        )
                        //Linha do icons
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
                                            Toast.makeText(context, "Location deleted successfully!", Toast.LENGTH_LONG).show()
                                        }else
                                            Toast.makeText(context, "Location not deleted. There are already associated Points of Interest", Toast.LENGTH_LONG).show()

                                    },
                                    modifier = Modifier.padding(8.dp),
                                ) {
                                    Icon(
                                        imageVector = Default.Delete,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.onTertiary)
                    //.padding(vertical = 16.dp)
                )
            }
        }


    }
}