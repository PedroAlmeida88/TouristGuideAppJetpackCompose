package pt.isec.amovtp.touristapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun POIScreen(modifier: Modifier = Modifier, navController: NavHostController?, viewModel : LocationViewModel) {

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()

            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ) {
            //Fazer ciclo com butões
            Button(
                onClick = { }
            ) {
                Text(text = "Museus")
            }
            Button(
                onClick = { }
            ) {
                Text(text = "Jardins")
            }
            IconButton(onClick = {
                navController?.navigate(Screens.ADD_CATEGORY.route)
            }) {
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
            items(viewModel.POIsBarcelona) { poi ->
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
                        //geoPoint = GeoPoint(poi.latitude, poi.longitude)
                        navController?.navigate(Screens.POI.route)
                    }
                ) {
                    Image(
                        painter = painterResource(id = poi.imagesRes),
                        contentDescription = "city picture",
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .wrapContentHeight(Alignment.Bottom),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Text(text = poi.team, fontSize = 20.sp)
                        Text(text = "${poi.latitude} ${poi.longitude}", fontSize = 14.sp)

                    }
                }
            }
        }

    }
}