package br.com.infoplus.infoplus.features.opening

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.infoplus.infoplus.R
import br.com.infoplus.infoplus.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun OpeningScreen(navController: NavController) {

    LaunchedEffect(Unit) {
        delay(1200)
        navController.navigate(Routes.LOGIN) {
            popUpTo(Routes.OPENING) { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.info), // ajuste se o nome for outro
            contentDescription = "Info+ logo",
            modifier = Modifier.width(260.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Tornar visível o que é\nsilenciado",
            color = Color(0xFF4B49C7),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
