package pt.isec.amovtp.touristapp.ui.composables

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import pt.isec.amovtp.touristapp.utils.FileUtils

import java.io.File
@Composable
fun TakePhotoOrLoadFromGallery(
    imagePath: MutableState<String?>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var tempFile by remember { mutableStateOf("") }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isOk ->
        if (!isOk) {
            imagePath.value = null
            return@rememberLauncherForActivityResult
        }
        imagePath.value = tempFile
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) {
            imagePath.value = null
            return@rememberLauncherForActivityResult
        }
        imagePath.value = FileUtils.createFileFromUri(context, uri)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                tempFile = FileUtils.getTempFilename(context)
                val fileUri = FileProvider.getUriForFile(
                    context,
                    "pt.isec.amovtp.touristapp.android.fileprovider",
                    File(tempFile)
                )
                cameraLauncher.launch(fileUri)
            }) {
                Text(text = "Take Photo")
            }

            Button(onClick = {
                galleryLauncher.launch(PickVisualMediaRequest())
            }) {
                Text(text = "Load Image")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            if (imagePath.value != null) {
                AsyncImage(
                    model = imagePath.value,
                    contentDescription = "Background image",
                    modifier = Modifier.matchParentSize()
                )
            } else {
            }
        }
    }
}