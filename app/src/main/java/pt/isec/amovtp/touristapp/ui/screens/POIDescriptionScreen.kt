package pt.isec.amovtp.touristapp.ui.screens

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pt.isec.amovtp.touristapp.data.PointOfInterest
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel

@Composable
fun POIDescriptionScreen(modifier: Modifier = Modifier, viewModel: LocationViewModel,firebaseViewModel: FirebaseViewModel) {
    val currentPoi = viewModel.selectedPoi

    val currentGeoPoint by remember{ mutableStateOf(GeoPoint(
       currentPoi?.latitude ?: 0.0, currentPoi?.longitude ?: 0.0
    )) }
    var pois by remember { mutableStateOf<List<PointOfInterest>>(emptyList()) }
    val selectedLocation = viewModel.selectedLocation

    var loaded by remember { mutableStateOf(false)    }
    //sempre que é iniciado, carrega os POIS
    LaunchedEffect(Unit) {
        firebaseViewModel.getPoisFromFirestore(selectedLocation) { loadedPois ->
            pois = loadedPois
            loaded = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = currentPoi!!.name,
            textAlign = TextAlign.Center,
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Box (
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clipToBounds()
                .border(1.dp, color = MaterialTheme.colorScheme.tertiary)
        ){
            if(loaded)
                AndroidView(
                    factory = { context ->
                        MapView(context).apply {
                            setTileSource(TileSourceFactory.MAPNIK);//==TileSourceFactory.DEFAULT_TILE_SOURCE
                            setMultiTouchControls(true)
                            controller.setCenter(currentGeoPoint)
                            controller.setZoom(13.0)
                                for(poi in pois) {
                                    val marker = Marker(this).apply {
                                        position = GeoPoint(poi.latitude, poi.longitude)
                                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        title = poi.name
                                        subDescription = poi.description
                                        //icon = ShapeDrawable(OvalShape())
                                    }
                                    if(! poi.name.equals(currentPoi?.name))
                                        marker.icon = ShapeDrawable(OvalShape()).apply {
                                            intrinsicHeight = 40 // Altura do círculo em pixels
                                            intrinsicWidth = 40 // Largura do círculo em pixels
                                            paint.color = Color.Red.toArgb()
                                        }
                                    overlays.add(marker)
                                }
                        }
                    },
                    update = { view ->
                        view.controller.setCenter(currentGeoPoint)
                    }
                )
        }
    }
}

@Composable
fun LandscapePOIDescriptionScreen(modifier: Modifier = Modifier, viewModel: LocationViewModel, firebaseViewModel: FirebaseViewModel) {
    val currentPoi = viewModel.selectedPoi

    val currentGeoPoint by remember{ mutableStateOf(GeoPoint(
        currentPoi?.latitude ?: 0.0, currentPoi?.longitude ?: 0.0
    )) }
    var pois by remember { mutableStateOf<List<PointOfInterest>>(emptyList()) }
    val selectedLocation = viewModel.selectedLocation

    var loaded by remember { mutableStateOf(false)    }
    //sempre que é iniciado, carrega os POIS
    LaunchedEffect(Unit) {
        firebaseViewModel.getPoisFromFirestore(selectedLocation) { loadedPois ->
            pois = loadedPois
            loaded = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = currentPoi!!.name,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .fillMaxWidth()
        )

        Box (
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clipToBounds()
                .border(1.dp, color = MaterialTheme.colorScheme.tertiary)
        ){
            if(loaded)
                AndroidView(
                    factory = { context ->
                        MapView(context).apply {
                            setTileSource(TileSourceFactory.MAPNIK);//==TileSourceFactory.DEFAULT_TILE_SOURCE
                            setMultiTouchControls(true)
                            controller.setCenter(currentGeoPoint)
                            controller.setZoom(13.0)
                            for(poi in pois) {
                                val marker = Marker(this).apply {
                                    position = GeoPoint(poi.latitude, poi.longitude)
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    title = poi.name
                                    subDescription = poi.description
                                    //icon = ShapeDrawable(OvalShape())
                                }
                                if(! poi.name.equals(currentPoi?.name))
                                    marker.icon = ShapeDrawable(OvalShape()).apply {
                                        intrinsicHeight = 40 // Altura do círculo em pixels
                                        intrinsicWidth = 40 // Largura do círculo em pixels
                                        paint.color = Color.Red.toArgb()
                                    }
                                overlays.add(marker)
                            }
                        }
                    },
                    update = { view ->
                        view.controller.setCenter(currentGeoPoint)
                    }
                )
        }
    }
}