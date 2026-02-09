package br.com.infoplus.infoplus.features.opportunities

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.infoplus.infoplus.core.ui.components.ScreenContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpportunitiesScreen(navController: NavController) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf(OpportunityCategory.JOBS) }

    fun openUrl(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        })
    }

    fun openSearch(query: String) {
        val url = "https://www.google.com/search?q=" + Uri.encode(query)
        openUrl(url)
    }

    val items = remember(selected) { OpportunitiesData.items(selected) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editais e vagas inclusivas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { padding ->
        ScreenContainer(padding = padding) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {

                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Curadoria de oportunidades",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "O INFO+ organiza caminhos para oportunidades e serviços. Para evitar informações incorretas por região, usamos links oficiais quando estáveis e busca guiada quando necessário.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(12.dp))

                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                SegmentedButton(
                                    selected = selected == OpportunityCategory.JOBS,
                                    onClick = { selected = OpportunityCategory.JOBS },
                                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 4)
                                ) { Text("Vagas") }

                                SegmentedButton(
                                    selected = selected == OpportunityCategory.GRANTS,
                                    onClick = { selected = OpportunityCategory.GRANTS },
                                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 4)
                                ) { Text("Editais") }

                                SegmentedButton(
                                    selected = selected == OpportunityCategory.TRAINING,
                                    onClick = { selected = OpportunityCategory.TRAINING },
                                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 4)
                                ) { Text("Cursos") }

                                SegmentedButton(
                                    selected = selected == OpportunityCategory.PUBLIC,
                                    onClick = { selected = OpportunityCategory.PUBLIC },
                                    shape = SegmentedButtonDefaults.itemShape(index = 3, count = 4)
                                ) { Text("Público") }
                            }

                            Spacer(Modifier.height(10.dp))
                            Text(
                                selected.subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                items(items) { item ->
                    OpportunityCard(
                        item = item,
                        onOpenUrl = ::openUrl,
                        onSearch = ::openSearch
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Voltar") }
                }
            }
        }
    }
}

@Composable
private fun OpportunityCard(
    item: OpportunityItem,
    onOpenUrl: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text(
                item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (item.tags.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.tags.take(3).forEach { tag ->
                        AssistChip(onClick = {}, label = { Text(tag) })
                    }
                }
            }

            val hasActions = item.url != null || item.searchQuery != null
            AnimatedVisibility(visible = hasActions) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null)
                        Spacer(Modifier.height(0.dp))
                        Text(if (expanded) "Fechar ações" else "Ações")
                    }

                    AnimatedVisibility(visible = expanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (item.url != null) {
                                OutlinedButton(
                                    onClick = { onOpenUrl(item.url) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = null)
                                    Text("Abrir link")
                                }
                            }
                            if (item.searchQuery != null) {
                                OutlinedButton(
                                    onClick = { onSearch(item.searchQuery) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                    Text("Buscar oportunidades")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}