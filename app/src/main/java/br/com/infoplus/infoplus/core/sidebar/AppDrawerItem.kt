package br.com.infoplus.infoplus.core.sidebar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDrawerItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object PlanRoute : AppDrawerItem(
        route = "plan_route",
        title = "Planejar percurso",
        icon = Icons.Outlined.Route
    )

    data object OccurrenceHistory : AppDrawerItem(
        route = "occurrence_history",
        title = "Histórico de Ocorrências",
        icon = Icons.Outlined.History
    )

    data object SupportResources : AppDrawerItem(
        route = "support_resources",
        title = "Recursos de Apoio",
        icon = Icons.Outlined.SupportAgent
    )

    data object CommunitySupport : AppDrawerItem(
        route = "community_support",
        title = "Apoio Comunitário",
        icon = Icons.Outlined.Handshake
    )

    data object Opportunities : AppDrawerItem(
        route = "opportunities",
        title = "Editais e Vagas",
        icon = Icons.Outlined.WorkOutline
    )

    data object Inbox : AppDrawerItem(
        route = "inbox",
        title = "Caixa de entrada",
        icon = Icons.Outlined.ChatBubbleOutline
    )

    data object Settings : AppDrawerItem(
        route = "settings",
        title = "Configurações",
        icon = Icons.Outlined.Settings
    )

    data object HelpContent : AppDrawerItem(
        route = "help_content",
        title = "Ajuda e Conteúdo",
        icon = Icons.Outlined.HelpOutline
    )

    data object ExitApp : AppDrawerItem(
        route = "exit_app",
        title = "Encerrar aplicativo",
        icon = Icons.Outlined.Logout
    )

    companion object {
        val mainItems = listOf(
            PlanRoute,
            OccurrenceHistory,
            SupportResources,
            CommunitySupport,
            Opportunities,
            Inbox,
            Settings,
            HelpContent,
            ExitApp
        )
    }
}