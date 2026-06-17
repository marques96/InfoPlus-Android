package br.com.infoplus.infoplus.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.infoplus.infoplus.core.settings.ThemeViewModel
import br.com.infoplus.infoplus.core.settings.ThemeMode
import androidx.navigation.NavController
import br.com.infoplus.infoplus.navigation.Routes
import br.com.infoplus.infoplus.features.home.HomeViewModel

data class MenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val homeVm: HomeViewModel = hiltViewModel()
    val themeVm: ThemeViewModel = hiltViewModel()
    val isDark = themeVm.isDarkTheme()


    val items = listOf(
        MenuItem(
            title = "Relatar",
            subtitle = "Sinalizar ocorrência",
            icon = Icons.Filled.Warning,
            route = Routes.REPORT
        ),
        MenuItem(
            title = "Histórico",
            subtitle = "Seus registros",
            icon = Icons.Filled.History,
            route = Routes.HISTORY
        ),
        MenuItem(
            title = "Mapa",
            subtitle = "Zonas de risco",
            icon = Icons.Filled.Map,
            route = Routes.MAP
        ),
        MenuItem(
            title = "Memorial",
            subtitle = "Homenagens",
            icon = Icons.Filled.CollectionsBookmark,
            route = Routes.MEMORIAL
        ),
        MenuItem(
            title = "Apoio",
            subtitle = "Canais de ajuda",
            icon = Icons.Filled.VolunteerActivism,
            route = Routes.SUPPORT
        ),
        MenuItem(
            title = "Vagas",
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
                        "Info+",
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = { 
                        val nextMode = if (isDark) ThemeMode.LIGHT else ThemeMode.DARK
                        themeVm.setThemeMode(nextMode)
                    }) {
                        Icon(
                            imageVector = if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Alternar tema"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        val state = rememberLazyGridState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Como podemos ajudar?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = state,
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items) { item ->
                    MenuCard(
                        item = item,
                        primary = MaterialTheme.colorScheme.primary,
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
    val shape = RoundedCornerShape(24.dp)
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        onClick = onClick,
        shape = shape,
        color = colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()
            .border(1.dp, colorScheme.outlineVariant.copy(alpha = 0.5f), shape)
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Ícone estilo Waze: amigável e colorido
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (item.title == "Relatar") colorScheme.errorContainer 
                        else primary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = if (item.title == "Relatar") colorScheme.error else primary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colorScheme.onSurface,
                )
                Text(
                    text = item.subtitle,
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
