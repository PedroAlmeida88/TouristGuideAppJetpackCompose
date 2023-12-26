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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import pt.isec.amovtp.touristapp.data.Category
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel

@Composable
fun ListCategoryScreen(navController: NavHostController?,modifier: Modifier = Modifier, viewModel: LocationViewModel, firebaseViewModel: FirebaseViewModel) {
    var myRating by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    var isRatingEnabled by remember { mutableStateOf(true) }
    val currentPoi = viewModel.selectedPoi
    val userUID = firebaseViewModel.authUser.value!!.uid


    var categories by remember {
        mutableStateOf<List<Category>>(emptyList())
    }

    //sempre que é iniciado, carrega as categorias
    LaunchedEffect(Unit) {
        firebaseViewModel.getCategoriesFromFirestore(){ loadedCategories ->
            categories = loadedCategories
            for (c in categories) {
                if (userUID in c.userUIDsApprovals || userUID == c.userUID) {
                    c.enableBtn = false
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            items(categories) { category ->
                val borderColor = when (category.approvals) {
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
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = category.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = category.description,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        //if(location.approvals < 2) {
                        //if(category.userUID == userUID) {
                        //}


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (category.approvals < 2){
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            firebaseViewModel.updateAprovalCategoryInFirestore(
                                                category,
                                                userUID
                                            )
                                            firebaseViewModel.getCategoriesFromFirestore() { loadedCategories ->
                                                categories = loadedCategories
                                                for (c in categories) {
                                                    if (userUID in c.userUIDsApprovals || userUID == c.userUID) {
                                                        c.enableBtn = false
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier.padding(8.dp),
                                        enabled = category.enableBtn
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null
                                        )
                                    }
                                    Text("${category.approvals}/2") // Uncomment this line if needed
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                if(category.userUID == userUID) {

                                    IconButton(
                                        onClick = {
                                            viewModel.selectedCategory = category
                                            navController?.navigate(Screens.EDIT_CATEGORY.route)
                                        },
                                        modifier = Modifier.padding(8.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            if(category.totalPois == 0) {
                                                firebaseViewModel.deleteCategoryFromFirestore(category)
                                                firebaseViewModel.getCategoriesFromFirestore() { loadedCategories ->
                                                    categories = loadedCategories
                                                }
                                                Toast.makeText(context, "Categoria eliminada com sucesso!", Toast.LENGTH_LONG).show()
                                            }else
                                                Toast.makeText(context, "Categoria não eliminada.Já existem POIS associados", Toast.LENGTH_LONG).show()

                                        },
                                        modifier = Modifier.padding(8.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
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

    }
}

@Composable
fun LandscapeListCategoryScreen(navController: NavHostController?, modifier: Modifier = Modifier, viewModel: LocationViewModel, firebaseViewModel: FirebaseViewModel) {
    var myRating by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    var isRatingEnabled by remember { mutableStateOf(true) }
    val currentPoi = viewModel.selectedPoi
    val userUID = firebaseViewModel.authUser.value!!.uid

    var categories by remember {
        mutableStateOf<List<Category>>(emptyList())
    }

    //sempre que é iniciado, carrega as categorias
    LaunchedEffect(Unit) {
        firebaseViewModel.getCategoriesFromFirestore(){ loadedCategories ->
            categories = loadedCategories
            for (c in categories) {
                if (userUID in c.userUIDsApprovals || userUID == c.userUID) {
                    c.enableBtn = false
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier
        .fillMaxSize()
        .padding(64.dp, 8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(categories) { category ->
            val borderColor = when (category.approvals) {
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
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = category.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = category.description,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (category.approvals < 2){
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        firebaseViewModel.updateAprovalCategoryInFirestore(
                                            category,
                                            userUID
                                        )
                                        firebaseViewModel.getCategoriesFromFirestore() { loadedCategories ->
                                            categories = loadedCategories
                                            for (c in categories) {
                                                if (userUID in c.userUIDsApprovals || userUID == c.userUID) {
                                                    c.enableBtn = false
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.padding(8.dp),
                                    enabled = category.enableBtn
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null
                                    )
                                }
                                Text("${category.approvals}/2") // Uncomment this line if needed
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            if(category.userUID == userUID) {

                                IconButton(
                                    onClick = {
                                        viewModel.selectedCategory = category
                                        navController?.navigate(Screens.EDIT_CATEGORY.route)
                                    },
                                    modifier = Modifier.padding(8.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        if(category.totalPois == 0) {
                                            firebaseViewModel.deleteCategoryFromFirestore(category)
                                            firebaseViewModel.getCategoriesFromFirestore() { loadedCategories ->
                                                categories = loadedCategories
                                            }
                                            Toast.makeText(context, "Categoria eliminada com sucesso!", Toast.LENGTH_LONG).show()
                                        }else
                                            Toast.makeText(context, "Categoria não eliminada.Já existem POIS associados", Toast.LENGTH_LONG).show()

                                    },
                                    modifier = Modifier.padding(8.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
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

}