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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatListNumberedRtl
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import pt.isec.amovtp.touristapp.data.Category
import pt.isec.amovtp.touristapp.data.PointOfInterest
import pt.isec.amovtp.touristapp.ui.composables.DropDownComposable
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun POIScreen(modifier: Modifier = Modifier, navController: NavHostController?, viewModel : LocationViewModel,firebaseViewModel: FirebaseViewModel) {
    val selectedLocation = viewModel.selectedLocation
    val selectedCategory = viewModel.selectedCategory
    val userUID = firebaseViewModel.authUser.value!!.uid
    val context = LocalContext.current

    var categories by remember {
        mutableStateOf<List<Category>>(emptyList())
    }


    var pois by remember { mutableStateOf<List<PointOfInterest>>(emptyList()) }

    //sempre que é iniciado, carrega os POIS
    LaunchedEffect(Unit) {
        firebaseViewModel.getPoisFromFirestore(selectedLocation) { loadedPois ->
            pois = loadedPois
            for (p in pois) {
                //caso já tenha votado ou tenha sido criado por ele
                if (userUID in p.userUIDsApprovals || userUID == p.userUID) {
                    p.enableBtn = false
                }
            }
        }

        firebaseViewModel.getCategoriesFromFirestore(){ loadedCategories ->
            categories = loadedCategories
        }
    }
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            //.background(MaterialTheme.colorScheme.onBackground)
            .padding(8.dp),
        ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    viewModel.selectedCategory = Category("","","",0, 0,emptyList(),"",false)
                },
                modifier = Modifier.padding(4.dp, 0.dp)
            ) {
                Icon(imageVector = Default.RestartAlt, contentDescription = "")
            }
            DropDownComposable(navController = navController, viewModel = viewModel, firebaseViewModel = firebaseViewModel)
            IconButton(
                onClick = {
                    navController?.navigate(Screens.LIST_CATEGORY.route)
                },
                modifier = Modifier.padding(4.dp, 0.dp)
            ) {
                Icon(imageVector = Default.FormatListNumberedRtl, contentDescription = "")
            }
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(pois.filter { it.category.name == selectedCategory?.name || selectedCategory?.name == ""}) { poi ->
                val warningColor = when (poi.approvals) {
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
                    )
                ) {
                    if(poi.approvals < 2){
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
                                text = "Atenção: este POI ainda não foi aprovado (${poi.approvals}/2)",
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
                            AsyncImage(model = poi.photoUrl, contentDescription = "Point of Interest Picture")
                        }
                        //Coluna do texto
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxHeight()
                                .padding(8.dp),

                        ){
                            Text(text = poi.name, fontSize = 20.sp,color = MaterialTheme.colorScheme.tertiary)
                            Text(text = poi.description, fontSize = 14.sp,color = MaterialTheme.colorScheme.tertiary)
                            Row (
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){
                                Text(text = "${poi.latitude} ${poi.longitude}", fontSize = 8.sp,color = MaterialTheme.colorScheme.tertiary)
                                if (poi.writenCoords) {
                                    Text(
                                        text = stringResource(id = pt.isec.amovtp.touristapp.R.string.msgCoordsWritten),
                                        fontSize = 6.sp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }else{
                                    Text(
                                        text = stringResource(id = pt.isec.amovtp.touristapp.R.string.msgCoordsLocation),
                                        fontSize = 6.sp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.selectedPoi = poi
                                navController?.navigate(Screens.ADD_COMMENTS.route)
                            },
                            modifier = Modifier.padding(4.dp, 0.dp),
                        ) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.sym_action_chat),
                                contentDescription = null
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.selectedPoi = poi
                                navController?.navigate(Screens.ADD_POI_PICTURES.route)
                            },
                            modifier = Modifier.padding(4.dp, 0.dp),
                        ) {
                            Icon(
                                imageVector = Default.PhotoLibrary,
                                contentDescription = null
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.selectedPoi = poi
                                navController?.navigate(Screens.POI_DESCRIPTION.route)
                            },
                            modifier = Modifier.padding(4.dp, 0.dp),
                        ) {
                            Icon(
                                imageVector = Default.Map,
                                contentDescription = null
                            )
                        }
                        if (poi.approvals < 2) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(4.dp, 0.dp)
                            ){
                                IconButton(
                                    onClick = {
                                        firebaseViewModel.updateAprovalPOIsInFirestore(
                                            selectedLocation,
                                            poi,
                                            userUID
                                        )
                                        firebaseViewModel.getPoisFromFirestore(selectedLocation) { loadedPois ->
                                            pois = loadedPois
                                            for (p in pois)
                                                if (userUID in p.userUIDsApprovals || userUID == p.userUID)
                                                    p.enableBtn = false
                                        }

                                    },
                                    enabled = poi.enableBtn
                                ) {
                                    Icon(
                                        imageVector = Default.CheckCircle,
                                        contentDescription = null
                                    )
                                }
                                Text(
                                    text = "${poi.approvals}/2",
                                )
                            }
                        }
                        if(poi.userUID == userUID) {
                            IconButton(
                                onClick = {
                                    viewModel.selectedPoi = poi
                                    navController?.navigate(Screens.EDIT_POI.route)
                                },
                                modifier = Modifier.padding(4.dp, 0.dp),
                            ) {
                                Icon(
                                    imageVector = Default.Edit,
                                    contentDescription = null
                                )
                            }

                            IconButton(
                                onClick = {
                                    if(poi.approvals == 0) {
                                        firebaseViewModel.deletePOIFromFirestore(selectedLocation?.name ?: "", poi)
                                        firebaseViewModel.getPoisFromFirestore(selectedLocation) { loadedPois ->
                                            pois = loadedPois
                                        }
                                        Toast.makeText(context, "Point Of Interest deleted successfully!", Toast.LENGTH_LONG).show()
                                    }else{
                                        Toast.makeText(context, "Points of Interest already have votes!", Toast.LENGTH_LONG).show()

                                    }
                                },
                                modifier = Modifier.padding(4.dp, 0.dp),
                            ) {
                                Icon(
                                    imageVector = Default.DeleteForever,
                                    contentDescription = null
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun LandscapePOIScreen(navController: NavHostController?, viewModel: LocationViewModel, firebaseViewModel: FirebaseViewModel) {
    val selectedLocation = viewModel.selectedLocation
    val selectedCategory = viewModel.selectedCategory
    val userUID = firebaseViewModel.authUser.value!!.uid
    val context = LocalContext.current

    var categories by remember {
        mutableStateOf<List<Category>>(emptyList())
    }


    var pois by remember { mutableStateOf<List<PointOfInterest>>(emptyList()) }

    //sempre que é iniciado, carrega os POIS
    LaunchedEffect(Unit) {
        firebaseViewModel.getPoisFromFirestore(selectedLocation) { loadedPois ->
            pois = loadedPois
            for (p in pois) {
                //caso já tenha votado ou tenha sido criado por ele
                if (userUID in p.userUIDsApprovals || userUID == p.userUID) {
                    p.enableBtn = false
                }
            }
        }

        firebaseViewModel.getCategoriesFromFirestore(){ loadedCategories ->
            categories = loadedCategories
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
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    viewModel.selectedCategory = Category("","","",0, 0,emptyList(),"",false)
                },
                modifier = Modifier.padding(4.dp, 0.dp)
            ) {
                Icon(imageVector = Default.RestartAlt, contentDescription = "")
            }
            DropDownComposable(navController = navController, viewModel = viewModel, firebaseViewModel = firebaseViewModel)
            IconButton(
                onClick = {
                    navController?.navigate(Screens.LIST_CATEGORY.route)
                },
                modifier = Modifier.padding(4.dp, 0.dp)
            ) {
                Icon(imageVector = Default.FormatListNumberedRtl, contentDescription = "")
            }
        }
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(count = 2)
        ){
            items(pois.filter { it.category.name == selectedCategory?.name || selectedCategory?.name == ""}) { poi ->
                val warningColor = when (poi.approvals) {
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
                        viewModel.selectedPoi = poi
                        navController?.navigate(Screens.POI_DESCRIPTION.route)
                    }
                ) {
                    if(poi.approvals < 2){
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
                                text = "Atenção: este POI ainda não foi aprovado (${poi.approvals}/2)",
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
                            AsyncImage(model = poi.photoUrl, contentDescription = "Point of Interest Picture")
                        }
                        //Coluna do texto
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxHeight()
                                .padding(8.dp),

                            ){
                            Text(text = poi.name, fontSize = 20.sp,color = MaterialTheme.colorScheme.tertiary)
                            Text(text = poi.description, fontSize = 14.sp,color = MaterialTheme.colorScheme.tertiary)
                            Row (
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){
                                Text(text = "${poi.latitude} ${poi.longitude}", fontSize = 8.sp,color = MaterialTheme.colorScheme.tertiary)
                                if (poi.writenCoords) {
                                    Text(
                                        text = stringResource(id = pt.isec.amovtp.touristapp.R.string.msgCoordsWritten),
                                        fontSize = 6.sp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }else{
                                    Text(
                                        text = stringResource(id = pt.isec.amovtp.touristapp.R.string.msgCoordsLocation),
                                        fontSize = 6.sp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
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
                    //icons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.selectedPoi = poi
                                navController?.navigate(Screens.ADD_COMMENTS.route)
                            },
                            modifier = Modifier.padding(4.dp, 0.dp),
                        ) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.sym_action_chat),
                                contentDescription = null
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.selectedPoi = poi
                                navController?.navigate(Screens.ADD_POI_PICTURES.route)
                            },
                            modifier = Modifier.padding(4.dp, 0.dp),
                        ) {
                            Icon(
                                imageVector = Default.PhotoLibrary,
                                contentDescription = null
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.selectedPoi = poi
                                navController?.navigate(Screens.POI_DESCRIPTION.route)
                            },
                            modifier = Modifier.padding(4.dp, 0.dp),
                        ) {
                            Icon(
                                imageVector = Default.Map,
                                contentDescription = null
                            )
                        }

                        if (poi.approvals < 2) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(4.dp, 0.dp)
                            ){
                                IconButton(
                                    onClick = {
                                        firebaseViewModel.updateAprovalPOIsInFirestore(
                                            selectedLocation,
                                            poi,
                                            userUID
                                        )
                                        firebaseViewModel.getPoisFromFirestore(selectedLocation) { loadedPois ->
                                            pois = loadedPois
                                            for (p in pois)
                                                if (userUID in p.userUIDsApprovals || userUID == p.userUID)
                                                    p.enableBtn = false
                                        }

                                    },
                                    enabled = poi.enableBtn
                                ) {
                                    Icon(
                                        imageVector = Default.CheckCircle,
                                        contentDescription = null
                                    )
                                }
                                Text(
                                    text = "${poi.approvals}/2",
                                )
                            }
                        }
                        if(poi.userUID == userUID) {
                            IconButton(
                                onClick = {
                                    viewModel.selectedPoi = poi
                                    navController?.navigate(Screens.EDIT_POI.route)
                                },
                                modifier = Modifier.padding(4.dp, 0.dp),
                            ) {
                                Icon(
                                    imageVector = Default.Edit,
                                    contentDescription = null
                                )
                            }

                            IconButton(
                                onClick = {
                                    if(poi.approvals == 0) {
                                        firebaseViewModel.deletePOIFromFirestore(selectedLocation?.name ?: "", poi)
                                        firebaseViewModel.getPoisFromFirestore(selectedLocation) { loadedPois ->
                                            pois = loadedPois
                                        }
                                        Toast.makeText(context, "Point Of Interest deleted successfully!", Toast.LENGTH_LONG).show()
                                    }else{
                                        Toast.makeText(context, "Points of Interest already have votes!", Toast.LENGTH_LONG).show()

                                    }
                                },
                                modifier = Modifier.padding(4.dp, 0.dp),
                            ) {
                                Icon(
                                    imageVector = Default.DeleteForever,
                                    contentDescription = null
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}