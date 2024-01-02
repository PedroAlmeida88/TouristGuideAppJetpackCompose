package pt.isec.amovtp.touristapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import pt.isec.amovtp.touristapp.R
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel

@Composable
fun MenuScreen(
    navController: NavHostController?,
    firebaseViewModel: FirebaseViewModel,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground)
    ) {
        Box (modifier = Modifier.align(Alignment.TopCenter)) {
            Column (modifier = Modifier.align(Alignment.TopCenter)) {
                if(firebaseViewModel.user.value != null)
                    Text(
                        text= stringResource(id = R.string.msgHello) + firebaseViewModel.user.value?.firstName + " " + firebaseViewModel.user.value?.lastName,
                        fontSize = 26.sp,
                        modifier = Modifier
                            .padding(24.dp, 64.dp, 24.dp, 24.dp),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                else
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(64.dp)
                            .padding(24.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )

                Image(
                    painter = painterResource(id = R.drawable.landscape),
                    contentDescription = "Landscape",
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter,
                    modifier = Modifier
                        .padding(6.dp)
                        .border(
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        )
                        .fillMaxWidth()
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp, 250.dp, 24.dp, 24.dp)
                .align(Alignment.Center)
        ) {
            Button(
                onClick = { navController?.navigate(Screens.LOCATIONS.route) },
                shape = CutCornerShape(percent = 0),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 0.dp, 24.dp, 0.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.btnLocations),
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController?.navigate(Screens.CREDITS.route) },
                shape = CutCornerShape(percent = 0),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 0.dp, 24.dp, 0.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.btnCredits),
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun LandscapeMenuScreen(navController: NavHostController?, firebaseViewModel: FirebaseViewModel, vararg options: String) {
    Column (
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(64.dp, 8.dp)
    ) {
        if(firebaseViewModel.user.value != null)
            Text(
                text= stringResource(id = R.string.msgHello) + firebaseViewModel.user.value?.firstName + " " + firebaseViewModel.user.value?.lastName,
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(24.dp, 48.dp)
            )
        else
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .padding(24.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

        Button(
            onClick = { navController?.navigate(Screens.LOCATIONS.route) },
            shape = CutCornerShape(percent = 0),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 0.dp, 24.dp, 0.dp)
        ) {
            Text(
                text = stringResource(id = R.string.btnLocations),
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController?.navigate(Screens.CREDITS.route) },
            shape = CutCornerShape(percent = 0),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 0.dp, 24.dp, 0.dp)
        ) {
            Text(
                text = stringResource(id = R.string.btnCredits),
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}