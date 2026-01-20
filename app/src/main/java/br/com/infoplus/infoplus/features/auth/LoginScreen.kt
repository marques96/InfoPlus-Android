@file:OptIn(ExperimentalMaterial3Api::class)

package br.com.infoplus.infoplus.features.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.text.KeyboardOptions
import androidx.navigation.NavController
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import br.com.infoplus.infoplus.navigation.Routes
import br.com.infoplus.infoplus.R

@Composable
fun LoginScreen(navController: NavController) {
    // Cores aproximadas do print
    val topYellow = Color(0xFFF6E45A)
    val purple = Color(0xFF5B5A95)
    val titleBlue = Color(0xFF3A4A8B)
    val linkPurple = Color(0xFF6A5ACD)
    val bg = Color(0xFFF2F2F2)

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.info_wbg),
                        contentDescription = "Info+",
                        modifier = Modifier.height(28.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = topYellow
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(purple),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "© 2024 Info+.com.br",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        },
        containerColor = bg
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Faça seu login",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = titleBlue
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // E-mail
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "E-mail",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("seuemail@dominio.com", color = Color(0xFF9E9E9E)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = purple,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent
                            )
                        )
                        HorizontalDivider(color = Color(0xFFB9B2B2), thickness = 1.dp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Senha
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Senha",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        OutlinedTextField(
                            value = senha,
                            onValueChange = { senha = it },
                            placeholder = { Text("Senha", color = Color(0xFF9E9E9E)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = purple,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent
                            )
                        )
                        HorizontalDivider(color = Color(0xFFB9B2B2), thickness = 1.dp)
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Botão Login
                    Button(
                        onClick = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .width(220.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = purple)
                    ) {
                        Text("Login", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Esqueceu a senha?
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Esqueceu a senha? ",
                            fontSize = 13.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "clique aqui!",
                            fontSize = 13.sp,
                            color = linkPurple,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                // TODO: navController.navigate(Routes.FORGOT_PASSWORD)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    SocialButton(
                        text = "Entrar com Facebook",
                        iconRes = R.drawable.facebook,
                        borderColor = Color(0xFFB9B2B2)
                    ) { }

                    Spacer(modifier = Modifier.height(10.dp))

                    SocialButton(
                        text = "Entrar com Google",
                        iconRes = R.drawable.google,
                        borderColor = Color(0xFFB9B2B2)
                    ) { }

                    Spacer(modifier = Modifier.height(10.dp))

                    SocialButton(
                        text = "Entrar com Apple",
                        iconRes = R.drawable.apple,
                        borderColor = Color(0xFFB9B2B2)
                    ) { }
                }
            }
        }
    }
}

@Composable
private fun SocialButton(
    text: String,
    iconRes: Int,
    borderColor: Color,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
        border = BorderStroke(1.dp, borderColor),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, color = Color.Black, fontSize = 13.sp)
    }
}
