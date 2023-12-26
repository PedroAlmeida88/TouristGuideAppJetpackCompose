package pt.isec.amovtp.touristapp.ui.composables

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        AsyncImage(
            model = imagePath.value,
            contentDescription = "Background Image",
            modifier = Modifier
                .weight(1f)
                .size(150.dp)
                .padding(4.dp)
        )

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
        ) {
            Button(
                onClick = {
                    tempFile = FileUtils.getTempFilename(context)
                    val fileUri = FileProvider.getUriForFile(
                        context,
                        "pt.isec.amovtp.touristapp.android.fileprovider",
                        File(tempFile)
                    )
                    cameraLauncher.launch(fileUri)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Take Photo")
            }

            Button(
                onClick = {
                    galleryLauncher.launch(PickVisualMediaRequest())
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Load Image")
            }
        }
    }
}

@Composable
fun LandscapeTakePhotoOrLoadFromGallery(
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

    Column (
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        AsyncImage(
            model = imagePath.value,
            contentDescription = "Background Image",
            modifier = Modifier
                .weight(1f)
                .size(150.dp)
                .padding(4.dp)
        )

        Row {
            Button(
                onClick = {
                    tempFile = FileUtils.getTempFilename(context)
                    val fileUri = FileProvider.getUriForFile(
                        context,
                        "pt.isec.amovtp.touristapp.android.fileprovider",
                        File(tempFile)
                    )
                    cameraLauncher.launch(fileUri)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Take Photo")
            }

            Button(
                onClick = {
                    galleryLauncher.launch(PickVisualMediaRequest())
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Load Image")
            }
        }
    }
}