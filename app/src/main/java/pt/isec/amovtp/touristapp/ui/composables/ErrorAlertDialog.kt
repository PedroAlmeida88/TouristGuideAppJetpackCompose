package pt.isec.amovtp.touristapp.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import pt.isec.amovtp.touristapp.R

@Composable
fun ErrorAlertDialog(onAction: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
            onAction()
        },
        confirmButton = {
            Button(onClick = {
                onAction()
            }) {
                Text(text = "Ok")
            }
        },
        text = { Text(text = stringResource(id = R.string.msgError)) },
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color.Red
            )
        }
    )
}