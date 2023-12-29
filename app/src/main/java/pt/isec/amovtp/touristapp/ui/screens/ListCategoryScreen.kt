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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
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
    val context = LocalContext.current
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
                val warningColor = when (category.approvals) {
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
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if(category.approvals < 2){
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .height(IntrinsicSize.Min),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = warningColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp)) // Adiciona espaço entre o ícone e o texto
                                Text(
                                    text = "Atenção: esta categoria ainda não foi aprovada (${category.approvals}/2)",
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
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Column {
                                Row{

                                    Text(
                                        text = category.name,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Icon(
                                        imageVector = getImageVectorFromName(category.icon)!!,
                                        contentDescription = "",
                                        Modifier.size(20.dp)
                                    )

                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = category.description,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
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
                                                Toast.makeText(context, "Category deleted successfully!", Toast.LENGTH_LONG).show()
                                            }else
                                                Toast.makeText(context, "Category not deleted. There are already associated Points of Interest", Toast.LENGTH_LONG).show()

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
    val context = LocalContext.current
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
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(count = 2)
        ){
            items(categories) { category ->
                val warningColor = when (category.approvals) {
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
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if(category.approvals < 2){
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .height(IntrinsicSize.Min),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = warningColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp)) // Adiciona espaço entre o ícone e o texto
                                Text(
                                    text = "Atenção: esta categoria ainda não foi aprovada (${category.approvals}/2)",
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
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Column {
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
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
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
                                                Toast.makeText(context, "Category deleted successfully!", Toast.LENGTH_LONG).show()
                                            }else
                                                Toast.makeText(context, "Category not deleted. There are already associated Points of Interest", Toast.LENGTH_LONG).show()

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