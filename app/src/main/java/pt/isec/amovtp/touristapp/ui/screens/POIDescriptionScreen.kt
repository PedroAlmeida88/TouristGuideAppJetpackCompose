package pt.isec.amovtp.touristapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel

@Composable
fun POIDescriptionScreen(modifier: Modifier = Modifier, viewModel: LocationViewModel) {
    val location = viewModel.currentLocation.observeAsState()

    val geoPoint by remember{ mutableStateOf(GeoPoint(
        location.value?.latitude ?: 0.0, location.value?.longitude ?: 0.0
    )) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(16.dp))
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds()
                //.background(Color(255, 240, 128)),
        ){
            AndroidView(
                factory = { context ->
                    MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK);//==TileSourceFactory.DEFAULT_TILE_SOURCE
                        setMultiTouchControls(true)
                        controller.setCenter(geoPoint)
                        controller.setZoom(8.0)

                        overlays.add(
                            Marker(this).apply {
                                position = GeoPoint(
                                    location.value?.latitude ?: 0.0,
                                    location.value?.longitude ?: 0.0
                                )
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = "Estou aqui!"
                                isFlat = true
                            }
                        )
                    }
                },
                update = { view ->
                    view.controller.setCenter(geoPoint)
                }
            )
        }
        Text(text = "lkjjadnwkldnalkw")
    }
}