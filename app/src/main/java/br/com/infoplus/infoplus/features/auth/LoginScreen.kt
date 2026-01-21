@file:OptIn(ExperimentalMaterial3Api::class)

package br.com.infoplus.infoplus.features.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.infoplus.infoplus.R
import br.com.infoplus.infoplus.navigation.Routes

@Composable
fun LoginScreen(navController: NavController) {
    // Identidade (ajuste fino depois se quiser)
    val primary = Color(0xFF5B5A95)        // roxo principal
    val link = Color(0xFF6A5ACD)           // roxo do link
    val title = primary          // título mais sóbrio (menos "azulão")
    val text = Color(0xFF111111)           // texto preto
    val line = Color(0xFFB9B2B2)           // divisor
    val socialBorder = Color(0xFFCDC7C7)

    // Gradiente sutil (diferença pequena = moderno)
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFF6F5FF), // leve puxado pro roxo (quase imperceptível)
            Color(0xFFFFFBF0)  // leve puxado pro quente (bem sutil)
        )
    )

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.info_wbg),
                        contentDescription = "Info+",
                        modifier = Modifier.height(28.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            // Rodapé discreto (não compete com o CTA)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "© 2024 Info+.com.br",
                    color = Color(0xFF666666),
                    fontSize = 12.sp
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Faça seu login",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = title
            )

            Spacer(modifier = Modifier.weight(1f))

            // ===== E-mail =====
            FieldLabel("E-mail", text)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("E-mail", color = Color(0xFF8A8A8A)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = text,
                    unfocusedTextColor = text,
                    cursorColor = primary,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            HorizontalDivider(color = line, thickness = 1.dp)

            Spacer(modifier = Modifier.height(14.dp))

            // ===== Senha =====
            FieldLabel("Senha", text)
            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                placeholder = { Text("Senha", color = Color(0xFF8A8A8A)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                        Icon(
                            imageVector = if (senhaVisivel) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (senhaVisivel) "Ocultar senha" else "Mostrar senha",
                            tint = Color(0xFF666666)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = text,
                    unfocusedTextColor = text,
                    cursorColor = primary,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            HorizontalDivider(color = line, thickness = 1.dp)

            Spacer(modifier = Modifier.height(26.dp))

            // ===== Botão Login =====
            Button(
                onClick = {
                    // autenticação real depois
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primary)
            ) {
                Text("Login", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ===== Esqueceu a senha =====
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Esqueceu a senha? ",
                    fontSize = 13.sp,
                    color = Color(0xFF333333)
                )
                Text(
                    text = "clique aqui!",
                    fontSize = 13.sp,
                    color = link,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        // TODO: navController.navigate(Routes.FORGOT_PASSWORD)
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ===== Botões sociais =====
            SocialButton(
                text = "Entrar com Facebook",
                iconRes = R.drawable.facebook,
                borderColor = socialBorder
            ) { }

            Spacer(modifier = Modifier.height(10.dp))

            SocialButton(
                text = "Entrar com Google",
                iconRes = R.drawable.google,
                borderColor = socialBorder
            ) { }

            Spacer(modifier = Modifier.height(10.dp))

            SocialButton(
                text = "Entrar com Apple",
                iconRes = R.drawable.apple,
                borderColor = socialBorder
            ) { }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun FieldLabel(label: String, textColor: Color) {
    Text(
        text = label,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = textColor,
        modifier = Modifier.fillMaxWidth()
    )
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
            .height(44.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White.copy(alpha = 0.65f)),
        border = BorderStroke(1.dp, borderColor),
        contentPadding = PaddingValues(horizontal = 14.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, color = Color(0xFF1F1F1F), fontSize = 13.sp)
    }
}
