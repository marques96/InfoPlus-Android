package br.com.infoplus.infoplus.core.sidebar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Route
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDrawerItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object PlanRoute : AppDrawerItem(
        route = "plan_route",
        title = "Planejar percurso",
        icon = Icons.Rounded.Route
    )

    data object OccurrenceHistory : AppDrawerItem(
        route = "occurrence_history",
        title = "Histórico de Ocorrências",
        icon = Icons.Rounded.History
    )

    data object SupportResources : AppDrawerItem(
        route = "support_resources",
        title = "Recursos de Apoio",
        icon = Icons.Rounded.SupportAgent
    )

    data object CommunitySupport : AppDrawerItem(
        route = "community_support",
        title = "Apoio Comunitário",
        icon = Icons.Rounded.Handshake
    )

    data object Opportunities : AppDrawerItem(
        route = "opportunities",
        title = "Editais e Vagas",
        icon = Icons.Rounded.Work
    )

    data object Inbox : AppDrawerItem(
        route = "inbox",
        title = "Caixa de entrada",
        icon = Icons.Rounded.ChatBubble
    )

    data object Settings : AppDrawerItem(
        route = "settings",
        title = "Configurações",
        icon = Icons.Rounded.Settings
    )

    data object HelpContent : AppDrawerItem(
        route = "help_content",
        title = "Ajuda e Conteúdo",
        icon = Icons.Rounded.Help
    )

    data object ExitApp : AppDrawerItem(
        route = "exit_app",
        title = "Encerrar aplicativo",
        icon = Icons.Rounded.Logout
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