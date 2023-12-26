package pt.isec.amovtp.touristapp.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.isec.amovtp.touristapp.R
import pt.isec.amovtp.touristapp.data.Comment
import pt.isec.amovtp.touristapp.ui.composables.ErrorAlertDialog
import pt.isec.amovtp.touristapp.ui.composables.RatingBar
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AddCommentsScreen(modfier: Modifier.Companion, locationViewModel: LocationViewModel,firebaseViewModel: FirebaseViewModel) {
    val selectedLocation = locationViewModel.selectedLocation
    val selectedPoi = locationViewModel.selectedPoi
    var commentToSubmit by remember { mutableStateOf("") }
    var isInputEnabled by remember { mutableStateOf(true) }
    var charLimitExceeded by remember { mutableStateOf(false) }
    var alreadyComment by remember { mutableStateOf(false) }
    var alreadyRated by remember { mutableStateOf(false) }

    var myRating by remember { mutableIntStateOf(0) }
    var rating by remember { mutableIntStateOf(0) }
    var isRatingEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val userName = firebaseViewModel.user.value?.firstName + " " + firebaseViewModel.user.value?.lastName
    val userUID = firebaseViewModel.authUser.value!!.uid
    val date = getDate()

    var comments by remember { mutableStateOf<List<Comment?>>(emptyList()) }

    LaunchedEffect(Unit) {
        firebaseViewModel.getCommentsFromFirestore(selectedLocation,selectedPoi) { loadedComments ->
            comments = loadedComments
            for (c in comments) {
                if (c?.userUID == firebaseViewModel.authUser.value?.uid && c?.description != "") {
                    alreadyComment = true
                }
                if(c?.userUID == firebaseViewModel.authUser.value?.uid && c?.rating != 0){
                    alreadyRated = true
                }
            }
        }
    }

    if(charLimitExceeded)
        ErrorAlertDialog {
            charLimitExceeded = false
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(alreadyRated)
            Text(text = "You already rated", color = Color.Green, fontSize = 16.sp)
        if(alreadyComment)
            Text(text = "You already commented", color = Color.Green, fontSize = 16.sp)

        Text(
            text = "Comentários" + selectedPoi?.name,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 24.dp)
        )

        if(!alreadyComment)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 12.dp)
            ) {
                OutlinedTextField(
                    value = commentToSubmit,
                    onValueChange = {
                        commentToSubmit = it
                    },
                    singleLine = false,
                    maxLines = 5,
                    label = { Text(text = stringResource(id = R.string.msgComment)) },
                    enabled = isInputEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Button(
                    onClick = {
                        if(commentToSubmit.length >= 200 || commentToSubmit.isBlank())
                            charLimitExceeded = true
                        else {
                            charLimitExceeded = false
                            isInputEnabled = false
                            for (c in comments) {
                                if (c?.userUID == firebaseViewModel.authUser.value?.uid) {
                                    rating = c!!.rating
                                }
                            }
                            firebaseViewModel.addCommentToFirestore(Comment(commentToSubmit,userName, userUID,date,rating),selectedLocation,selectedPoi)
                            //atualizar a lista
                            firebaseViewModel.getCommentsFromFirestore(selectedLocation, selectedPoi) { loadedComments ->
                                comments = loadedComments
                            }
                        }

                    },
                    modifier = Modifier
                        .padding(top = 8.dp),

                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                    shape = CutCornerShape(percent = 0)
                ) {
                    Text(text = stringResource(id = R.string.btnSubmit))
                }
            }

        if(!alreadyRated)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 12.dp)
            ) {
                RatingBar(
                    currentRating = myRating,
                    onRatingChanged = {
                        if (isRatingEnabled) {
                            myRating = it
                        }
                    },
                    starsColor = Color.Yellow,
                    size = 48.dp
                )

                IconButton(
                    onClick = {
                        isRatingEnabled = false
                        rating = myRating
                        for (c in comments) {
                            if (c?.userUID == firebaseViewModel.authUser.value?.uid) {
                                commentToSubmit = c!!.description
                            }
                        }
                        firebaseViewModel.addCommentToFirestore(Comment(commentToSubmit,userName, userUID,date,rating),selectedLocation,selectedPoi)
                        //atualizar a lista
                        firebaseViewModel.getCommentsFromFirestore(selectedLocation, selectedPoi) { loadedComments ->
                            comments = loadedComments
                        }
                        Toast.makeText(context, "Classificação submetida com sucesso!", Toast.LENGTH_LONG).show()
                    },
                    enabled = isRatingEnabled
                ) {
                    Icon(Icons.Default.ThumbUp, contentDescription = stringResource(id = R.string.btnSubmit))
                }

            }

        if(comments.size == 0){
            Text(text = "Ainda não há comentários...", fontSize = 24.sp)
        }


        for (comment in comments) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = comment!!.userName,
                            fontSize = 14.sp,
                        )
                        if (comment.rating != 0)
                            Text(
                                text = comment.rating.toString() + "*", //+ Icons.Filled.Star,
                                fontSize = 14.sp,
                            )
                        Text(
                            text = comment.date,
                            fontSize = 14.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    if (comment?.description != "")
                        Text(
                            text = comment!!.description,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(4.dp)
                        )
                }
            }
        }
    }
}

fun getDate(): String {

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yy")
        LocalDateTime.now().format(formatter)
    } else {
        ""
    }
}

@Composable
fun LandscapeAddCommentsScreen(modfier: Modifier.Companion, locationViewModel: LocationViewModel, firebaseViewModel: FirebaseViewModel) {
    val selectedLocation = locationViewModel.selectedLocation
    val selectedPoi = locationViewModel.selectedPoi
    var commentToSubmit by remember { mutableStateOf("") }
    var isInputEnabled by remember { mutableStateOf(true) }
    var charLimitExceeded by remember { mutableStateOf(false) }
    var alreadyComment by remember { mutableStateOf(false) }
    var alreadyRated by remember { mutableStateOf(false) }

    var myRating by remember { mutableIntStateOf(0) }
    var rating by remember { mutableIntStateOf(0) }
    var isRatingEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val userName = firebaseViewModel.user.value?.firstName + " " + firebaseViewModel.user.value?.lastName
    val userUID = firebaseViewModel.authUser.value!!.uid
    val date = getDate()

    var comments by remember { mutableStateOf<List<Comment?>>(emptyList()) }

    LaunchedEffect(Unit) {
        firebaseViewModel.getCommentsFromFirestore(selectedLocation,selectedPoi) { loadedComments ->
            comments = loadedComments
            for (c in comments) {
                if (c?.userUID == firebaseViewModel.authUser.value?.uid && c?.description != "") {
                    alreadyComment = true
                }
                if(c?.userUID == firebaseViewModel.authUser.value?.uid && c?.rating != 0){
                    alreadyRated = true
                }
            }
        }
    }

    if(charLimitExceeded)
        ErrorAlertDialog {
            charLimitExceeded = false
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(64.dp, 8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(alreadyRated)
            Text(text = "You already rated", color = Color.Green, fontSize = 16.sp)
        if(alreadyComment)
            Text(text = "You already commented", color = Color.Green, fontSize = 16.sp)

        Text(
            text = "Comentários" + selectedPoi?.name,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 24.dp)
        )

        if(!alreadyComment)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 12.dp)
            ) {
                OutlinedTextField(
                    value = commentToSubmit,
                    onValueChange = {
                        commentToSubmit = it
                    },
                    singleLine = false,
                    maxLines = 5,
                    label = { Text(text = stringResource(id = R.string.msgComment)) },
                    enabled = isInputEnabled,
                    modifier = Modifier
                        .weight(6f)
                )

                IconButton(
                    onClick = {
                        if(commentToSubmit.length >= 200 || commentToSubmit.isBlank())
                            charLimitExceeded = true
                        else {
                            charLimitExceeded = false
                            isInputEnabled = false
                            for (c in comments) {
                                if (c?.userUID == firebaseViewModel.authUser.value?.uid) {
                                    rating = c!!.rating
                                }
                            }
                            firebaseViewModel.addCommentToFirestore(Comment(commentToSubmit,userName, userUID,date,rating),selectedLocation,selectedPoi)
                            //atualizar a lista
                            firebaseViewModel.getCommentsFromFirestore(selectedLocation, selectedPoi) { loadedComments ->
                                comments = loadedComments
                            }
                        }

                    },
                    modifier = Modifier
                        .weight(1f),
                ) {
                    Icon(Icons.Default.AddComment, contentDescription = "Add")
                }
            }

        if(!alreadyRated)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 12.dp)
            ) {
                RatingBar(
                    currentRating = myRating,
                    onRatingChanged = {
                        if (isRatingEnabled) {
                            myRating = it
                        }
                    },
                    starsColor = Color.Yellow,
                    size = 48.dp
                )

                IconButton(
                    onClick = {
                        isRatingEnabled = false
                        rating = myRating
                        for (c in comments) {
                            if (c?.userUID == firebaseViewModel.authUser.value?.uid) {
                                commentToSubmit = c!!.description
                            }
                        }
                        firebaseViewModel.addCommentToFirestore(Comment(commentToSubmit,userName, userUID,date,rating),selectedLocation,selectedPoi)
                        //atualizar a lista
                        firebaseViewModel.getCommentsFromFirestore(selectedLocation, selectedPoi) { loadedComments ->
                            comments = loadedComments
                        }
                        Toast.makeText(context, "Classificação submetida com sucesso!", Toast.LENGTH_LONG).show()
                    },
                    enabled = isRatingEnabled
                ) {
                    Icon(Icons.Default.ThumbUp, contentDescription = stringResource(id = R.string.btnSubmit))
                }

            }

        if(comments.size == 0){
            Text(text = "Ainda não há comentários...", fontSize = 24.sp)
        }


        for (comment in comments) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = comment!!.userName,
                            fontSize = 14.sp,
                        )
                        if (comment.rating != 0)
                            Text(
                                text = comment.rating.toString() + "*", //+ Icons.Filled.Star,
                                fontSize = 14.sp,
                            )
                        Text(
                            text = comment.date,
                            fontSize = 14.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    if (comment?.description != "")
                        Text(
                            text = comment!!.description,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(4.dp)
                        )
                }
            }
        }
    }
}