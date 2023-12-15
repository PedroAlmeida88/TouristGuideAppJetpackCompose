package pt.isec.amovtp.touristapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.osmdroid.util.GeoPoint
import pt.isec.amovtp.touristapp.R
import pt.isec.amovtp.touristapp.data.Location
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(modifier: Modifier = Modifier, navController: NavHostController?,viewModel : LocationViewModel,firebaseViewModel: FirebaseViewModel) {
    var autoEnabled by remember{ mutableStateOf(false) }
    val location = viewModel.currentLocation.observeAsState()
    var locations by remember { mutableStateOf<List<Location>>(emptyList())}
    var geoPoint by remember{ mutableStateOf(GeoPoint(
        30.00, 10.00
    )) }



    //sempre que é iniciado, carrega as localizações
    LaunchedEffect(Unit) {
        firebaseViewModel.getLocationFromFirestore { loadedLocations ->
            locations = loadedLocations
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
                    firebaseViewModel.getLocationFromFirestore(){location->

                    }
                }
            ) {
                Text(text = "A...Z")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = {  }
            ) {
                Text(text = "Perto de mim")
            }
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(locations) { location ->
                Card(
                    modifier = Modifier
                        //.fillMaxHeight(0,5f) // Use 50% of the screen height
                        //.height(324.dp)
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(shape = RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),

                    onClick = {
                        //geoPoint = GeoPoint(poi.latitude, poi.longitude)
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
                        Text(text = location.name, fontSize = 20.sp)
                        Text(text = "${location.latitude} ${location.longitude}", fontSize = 14.sp)
                        Button(
                            onClick = { navController?.navigate(Screens.SHOW_MAP.route) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillParentMaxHeight(0.05f),
                            shape = CutCornerShape(percent = 0),

                            ) {
                            Text(text = "Ver no mapa")
                        }
                    }
                    
                }
            }
        }

    }

}