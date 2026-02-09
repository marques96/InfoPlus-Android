package br.com.infoplus.infoplus.features.map

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import br.com.infoplus.infoplus.core.ui.components.ScreenContainer
import br.com.infoplus.infoplus.features.report.model.ReportStatus
import br.com.infoplus.infoplus.navigation.Routes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasesMapScreen(
    navController: NavHostController,
    vm: CasesMapViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember { MapView(context.applicationContext) }
    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }
    var clusterManager by remember { mutableStateOf<ClusterManager<CaseClusterItem>?>(null) }
    var didSetInitialCamera by remember { mutableStateOf(false) }

    val mapBundle = remember { Bundle() }

    // Lifecycle robusto do MapView
    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) = mapView.onCreate(mapBundle)
            override fun onStart(owner: LifecycleOwner) = mapView.onStart()
            override fun onResume(owner: LifecycleOwner) = mapView.onResume()
            override fun onPause(owner: LifecycleOwner) = mapView.onPause()
            override fun onStop(owner: LifecycleOwner) = mapView.onStop()
            override fun onDestroy(owner: LifecycleOwner) = mapView.onDestroy()
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Configura mapa + cluster + listeners (1x)
    LaunchedEffect(mapView) {
        mapView.getMapAsync { m ->
            googleMap = m

            m.uiSettings.isZoomControlsEnabled = true
            m.uiSettings.isMapToolbarEnabled = false

            val cm = ClusterManager<CaseClusterItem>(context, m)
            cm.renderer = CaseClusterRenderer(context, m, cm)

            // Delegar eventos do mapa para clusters
            m.setOnCameraIdleListener(cm)
            m.setOnMarkerClickListener(cm)
            m.setOnInfoWindowClickListener(cm)

            // 1️⃣ Clique no marcador (info window) → detalhe da ocorrência
            // (prevenção de erro: 1º clique abre info, 2º clique confirma abrindo detalhe)
            cm.setOnClusterItemInfoWindowClickListener { item ->
                navController.navigate("${Routes.HISTORY_DETAIL}/${item.recordId}")
            }

            // 4️⃣ Clique no cluster → zoom (pitch killer sem mudar UI)
            cm.setOnClusterClickListener { cluster ->
                m.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.position, m.cameraPosition.zoom + 2f))
                true
            }

            clusterManager = cm
        }
    }

    // Atualiza items do cluster quando markers/filtro mudarem
    LaunchedEffect(state.filteredMarkers, clusterManager, googleMap) {
        val cm = clusterManager ?: return@LaunchedEffect
        val m = googleMap ?: return@LaunchedEffect

        cm.clearItems()

        val items = state.filteredMarkers.map { marker ->
            CaseClusterItem(
                recordId = marker.id,
                lat = marker.lat,
                lon = marker.lon,
                markerTitle = marker.title,
                markerSnippet = marker.snippet,
                status = marker.status
            )
        }

        cm.addItems(items)
        cm.cluster()

        if (!didSetInitialCamera) {
            moveCameraIfNeeded(m, items)
            didSetInitialCamera = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de casos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { padding ->
        ScreenContainer(padding = padding) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                // 2️⃣ Legenda/Visibilidade de status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(onClick = {}, label = { Text("Histórico: ${state.totalInHistory}") })
                    AssistChip(onClick = {}, label = { Text("No mapa: ${state.filteredMarkers.size}") })
                    if (state.missingLocation > 0) {
                        AssistChip(onClick = {}, label = { Text("Sem localização: ${state.missingLocation}") })
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(onClick = {}, label = { Text("● Sincronizado") })
                    AssistChip(onClick = {}, label = { Text("● Salvo localmente") })
                }

                // 3️⃣ Filtro simples por categoria
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CasesMapFilter.values().forEach { f ->
                        FilterChip(
                            selected = state.selectedFilter == f,
                            onClick = { vm.setFilter(f) },
                            label = { Text(f.label) }
                        )
                    }
                }

                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { mapView }
                )
            }
        }
    }
}

private fun moveCameraIfNeeded(map: GoogleMap, items: List<CaseClusterItem>) {
    if (items.isEmpty()) {
        val br = LatLng(-14.2350, -51.9253)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(br, 3.8f))
        return
    }
    // Foco no mais recente (histórico salva mais recente primeiro)
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(items.first().position, 12f))
}

// -------- Cluster item + renderer (4️⃣ Pitch killer) --------

data class CaseClusterItem(
    val recordId: String,
    val lat: Double,
    val lon: Double,
    val markerTitle: String,
    val markerSnippet: String,
    val status: ReportStatus
) : com.google.maps.android.clustering.ClusterItem {

    override fun getPosition(): com.google.android.gms.maps.model.LatLng =
        com.google.android.gms.maps.model.LatLng(lat, lon)

    override fun getTitle(): String = markerTitle

    override fun getSnippet(): String = markerSnippet

    override fun getZIndex(): Float = 0f
}


private class CaseClusterRenderer(
    context: android.content.Context,
    map: GoogleMap,
    clusterManager: ClusterManager<CaseClusterItem>
) : DefaultClusterRenderer<CaseClusterItem>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(
        item: CaseClusterItem,
        markerOptions: com.google.android.gms.maps.model.MarkerOptions
    ) {
        super.onBeforeClusterItemRendered(item, markerOptions)

        // Marcadores “informativos” (offline-first visível)
        val hue = when (item.status) {
            ReportStatus.SYNCED ->
                com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_VIOLET
            ReportStatus.QUEUED ->
                com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ORANGE
        }

        markerOptions
            .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(hue))
            .title(item.title)
            .snippet(item.snippet)
    }
}