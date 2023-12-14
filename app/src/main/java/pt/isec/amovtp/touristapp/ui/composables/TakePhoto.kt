package pt.isec.amovtp.touristapp.ui.composables


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import pt.isec.amovtp.touristapp.R
import pt.isec.amovtp.touristapp.utils.FileUtils

import java.io.File

@Composable
fun TakePhoto(
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
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
            Text(text ="Take Photo")//pt.isec.amovtp.touristapp.ui.composables
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
                /*
                Image(
                    painter = painterResource(id = R.drawable.cn),
                    contentDescription = "Default Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.matchParentSize()
                )
                               */
            }
        }
    }
}