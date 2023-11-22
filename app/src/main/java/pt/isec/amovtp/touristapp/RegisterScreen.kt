package pt.isec.amovtp.touristapp

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import pt.isec.amovtp.touristapp.utils.Utils
import java.io.File

@Composable
fun RegisterScreen(navController: NavHostController) {
    val imagePath: MutableState<String?> = remember {
        mutableStateOf(null)
    }

    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult( contract = ActivityResultContracts.PickVisualMedia() ) { uri ->
        if (uri == null) {
            imagePath.value = null
            return@rememberLauncherForActivityResult
        }
        imagePath.value = Utils.createFileFromUri(context, uri)
    }

    var tempFile by remember { mutableStateOf("") }
    val cameraLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()){ isOk ->
        if (!isOk) {
            imagePath.value = null
            return@rememberLauncherForActivityResult
        }
        imagePath.value = tempFile
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box (
            modifier = Modifier.matchParentSize()
        ) {
            if (imagePath.value == null) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = "Default Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.matchParentSize()
                )
            } else {
                AsyncImage(
                    model = imagePath.value,
                    contentDescription = "Background Image",
                    modifier = Modifier.matchParentSize()
                )
            }
        }

        Row (
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ){
            Button(onClick = {
                galleryLauncher.launch(PickVisualMediaRequest())
            }) {
                Text(text = "Gallery")
            }

            Button(onClick = {
                tempFile = Utils.getTempFilename(context)
                val fileUri = FileProvider.getUriForFile(
                    context,
                    "pt.isec.amovtp.touristapp.android.fileprovider",
                    File(tempFile)
                )
                cameraLauncher.launch(fileUri)
            }) {
                Text(text = "Camera")
            }
        }
    }
}