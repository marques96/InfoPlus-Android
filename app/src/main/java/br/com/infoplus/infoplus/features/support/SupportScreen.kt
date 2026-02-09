package br.com.infoplus.infoplus.features.support

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
fun SupportScreen(navController: NavController) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf(SupportCategory.WELCOME) }

    fun dial(number: String) {
        context.startActivity(Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
        })
    }

    fun openUrl(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        })
    }

    fun openSearch(query: String) {
        val url = "https://www.google.com/search?q=" + Uri.encode(query)
        openUrl(url)
    }

    val resources = remember(selected) { SupportData.resources(selected) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recursos de apoio") },
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
                                "Escolha o tipo de apoio",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "O INFO+ prioriza acolhimento e orientação. Para serviços locais (estado/cidade), usamos “Buscar unidade” para evitar dados incorretos.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(12.dp))

                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                SegmentedButton(
                                    selected = selected == SupportCategory.LEGAL,
                                    onClick = { selected = SupportCategory.LEGAL },
                                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                                ) { Text("Jurídica") }

                                SegmentedButton(
                                    selected = selected == SupportCategory.PSYCHO,
                                    onClick = { selected = SupportCategory.PSYCHO },
                                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                                ) { Text("Psicológica") }

                                SegmentedButton(
                                    selected = selected == SupportCategory.WELCOME,
                                    onClick = { selected = SupportCategory.WELCOME },
                                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                                ) { Text("Acolhimento") }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = selected.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = selected.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(resources) { item ->
                    SupportResourceCard(
                        item = item,
                        onDial = { dial(it) },
                        onOpenUrl = { openUrl(it) },
                        onSearch = { openSearch(it) }
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
private fun SupportResourceCard(
    item: SupportResource,
    onDial: (String) -> Unit,
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
            Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
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

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                if (item.phone != null) {
                    FilledTonalButton(
                        onClick = { onDial(item.phone) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null)
                        Spacer(Modifier.height(0.dp))
                        Text("Ligar")
                    }
                }

                if (item.searchQuery != null || item.url != null) {
                    OutlinedButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null)
                        Spacer(Modifier.height(0.dp))
                        Text(if (expanded) "Fechar" else "Abrir")
                    }
                }
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
                            Spacer(Modifier.height(0.dp))
                            Text("Abrir site")
                        }
                    }
                    if (item.searchQuery != null) {
                        OutlinedButton(
                            onClick = { onSearch(item.searchQuery) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(Modifier.height(0.dp))
                            Text("Buscar unidade / serviços próximos")
                        }
                    }
                }
            }
        }
    }
}
