package pt.isec.amovtp.touristapp.ui.screens

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pt.isec.amovtp.touristapp.data.Comment
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel
import kotlin.math.log

@Composable
fun AddCommentsScreen(modfier: Modifier.Companion, locationViewModel: LocationViewModel,firebaseViewModel: FirebaseViewModel) {
    val selectedLocation = locationViewModel.selectedLocation
    val selectedPoi = locationViewModel.selectedPoi

    var comments by remember {
        mutableStateOf<List<Comment?>>(emptyList())
    }

    LaunchedEffect(Unit) {

        firebaseViewModel.getCommentsFromFirestore(selectedLocation,selectedPoi) { loadedComments ->
            comments = loadedComments
        }

    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Comentários [${selectedPoi?.name}]", fontSize = 24.sp)

        Spacer(Modifier.height(16.dp))


        if(comments.size == 0){
            Text(text = "Ainda não há comentários...", fontSize = 24.sp)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(comments) { comment->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(128, 192, 255)
                    ),
                    onClick = {}
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Nome do usuário
                        Text(text = "User: " + comment!!.userName, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp)) // Espaçamento entre o nome do usuário e o conteúdo do comentário
                        // Conteúdo do comentário
                        Text(text = comment.description, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}