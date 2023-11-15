package pt.isec.amovtp.touristapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@Composable
fun LoginScreen(
    navController: NavHostController?,
    menu: String
) {
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.msgLogin),
            modifier = Modifier
                .align(CenterHorizontally)
        )
        Text(
            text = stringResource(R.string.msgWB),
            modifier = Modifier
                .align(CenterHorizontally)
        )

        OutlinedTextField(
            value = name,
            onValueChange ={ name = it },
            label = { Text(text = stringResource(R.string.msgUsername),) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange ={ password = it },
            label = { Text(text = stringResource(R.string.msgPassword),) }
        )

        Button(
            onClick = {
                navController?.navigate(menu)
            },
            colors = buttonColors(MaterialTheme.colorScheme.primary),
        ) {
            Text(text = stringResource(R.string.btnSignIn))
        }
    }
}