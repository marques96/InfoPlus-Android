package br.com.infoplus.infoplus.features.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.infoplus.infoplus.navigation.Routes

data class MenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val primary = Color(0xFF5B5A95) // roxo do projeto
    val bgBrush = Brush.verticalGradient(
        listOf(Color.White, Color(0xFFF6F5FF), Color(0xFFFFFBF0))
    )

    val items = listOf(
        MenuItem(
            title = "Registrar ocorrência",
            subtitle = "Relate violência/assédio",
            icon = Icons.Filled.Campaign,
            route = Routes.REPORT // você vai criar essa rota
        ),
        MenuItem(
            title = "Mapa de casos",
            subtitle = "Zonas de risco",
            icon = Icons.Filled.Map,
            route = Routes.MAP
        ),
        MenuItem(
            title = "Memorial digital",
            subtitle = "Nomes e histórias",
            icon = Icons.Filled.CollectionsBookmark,
            route = Routes.MEMORIAL
        ),
        MenuItem(
            title = "Recursos de apoio",
            subtitle = "Ajuda e contatos",
            icon = Icons.Filled.VolunteerActivism,
            route = Routes.SUPPORT
        ),
        MenuItem(
            title = "Editais e vagas",
            subtitle = "Oportunidades",
            icon = Icons.Filled.Work,
            route = Routes.OPPORTUNITIES
        ),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Menu",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primary,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->

        val state = rememberLazyGridState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgBrush)
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(14.dp))

            Text(
                text = "Categorias",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F)
            )

            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = state,
                contentPadding = PaddingValues(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items) { item ->
                    MenuCard(
                        item = item,
                        primary = primary,
                        onClick = { navController.navigate(item.route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuCard(
    item: MenuItem,
    primary: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)

    // Fundo “moderno”: um degradê bem sutil dentro do card
    val cardBrush = Brush.linearGradient(
        listOf(Color.White, Color(0xFFF2F1FF))
    )

    Box(
        modifier = Modifier
            .height(132.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(cardBrush)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        // Ícone em “bolha” roxa
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = primary
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
        ) {
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF1F1F1F)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.subtitle,
                fontSize = 12.sp,
                color = Color(0xFF5A5A5A),
                modifier = Modifier.alpha(0.95f)
            )
        }
    }
}
