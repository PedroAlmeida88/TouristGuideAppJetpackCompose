package pt.isec.amovtp.touristapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun MenuScreen(title: String, logIn : String,navController: NavHostController?, vararg options: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text= title,
            fontSize = 36.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(24.dp, 64.dp, 24.dp, 64.dp)
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            for (btnName in options) {
                //val buttonText = stringResource(id = R.string.btn$btnName)
                //stringResource(id = R.string.msgHomeMenu)
                Button(
                    onClick = { navController?.navigate(btnName) },
                    shape = CutCornerShape(percent = 0),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp, 0.dp, 24.dp, 0.dp)
                ) {
                    Text(
                        /*text = buttonText,*/
                        text = btnName,
                        color = Color.LightGray,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }


        }
        Button  (
            onClick = {navController?.navigate(logIn)},
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ){
            Icon(Icons.Default.ExitToApp, contentDescription = stringResource(R.string.msgLogout))
        }
    }
}