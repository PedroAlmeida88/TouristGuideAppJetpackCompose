package pt.isec.amovtp.touristapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import pt.isec.amovtp.touristapp.R

@Composable
fun CreditsScreen(modifier: Modifier = Modifier) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.isec_logo),
            contentDescription = "ISEC logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .size(120.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Practical Work Nº1 - Mobile Arquitecture",
            fontSize = 20.sp
        )
        Text(
            text = "Done by:",
            fontSize = 18.sp
        )
        Row (
            modifier = Modifier.fillMaxWidth()
        ) {
            Card (
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
                modifier = Modifier
                    .weight(1f, true)
                    .padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.acacio),
                    contentDescription = "Acácio Photo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(6.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary), CircleShape)
                )
                Text(text = "Acácio Agabalayeve Coutinho")
            }
            Card (
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
                modifier = Modifier
                    .weight(1f, true)
                    .padding(8.dp)
            ) {
                Image(painter = painterResource(id = R.drawable.isec_logo), contentDescription = "Acácio Photo")
                Text(text = "Acácio Agabalayeve Coutinho")
            }
        }
    }
}