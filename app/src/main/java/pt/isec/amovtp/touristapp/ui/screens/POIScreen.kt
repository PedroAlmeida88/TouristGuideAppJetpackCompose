package pt.isec.amovtp.touristapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import pt.isec.amovtp.touristapp.R
import pt.isec.amovtp.touristapp.data.Location
import pt.isec.amovtp.touristapp.data.PointOfInterest
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun POIScreen(modifier: Modifier = Modifier, navController: NavHostController?, viewModel : LocationViewModel,firebaseViewModel: FirebaseViewModel) {
    val selectedLocation = viewModel.selectedLocation

    var pois by remember { mutableStateOf<List<PointOfInterest>>(emptyList()) }

    //sempre que é iniciado, carrega os POIS
    LaunchedEffect(Unit) {
        firebaseViewModel.getPoisFromFirestore(selectedLocation) { loadedPois ->
            pois = loadedPois
            //Log.i("TAG", "POIScreen: " + pois)
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
        ) {
            //Fazer ciclo com butões

            Button(
                onClick = { }
            ) {
                Text(text = "Museus")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = { }
            ) {
                Text(text = "Jardins")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(onClick = {
                navController?.navigate(Screens.ADD_CATEGORY.route)
            }, ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add"
                )

            }
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(pois) { poi ->
                Card(
                    modifier = Modifier
                        //.fillMaxHeight(0,5f) // Use 50% of the screen height
                        //.height(324.dp)
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(shape = RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),

                    onClick = {
                        viewModel.selectedPoi = poi
                        navController?.navigate(Screens.POI_DESCRIPTION.route)
                    }
                ) {
                    AsyncImage(model = poi.photoUrl, contentDescription = "Point of Interest Picture")
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .wrapContentHeight(Alignment.Bottom),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Text(text = poi.name, fontSize = 20.sp)
                        Text(text = "${poi.latitude} ${poi.longitude}", fontSize = 14.sp)

                    }
                }
            }
        }
    }
}