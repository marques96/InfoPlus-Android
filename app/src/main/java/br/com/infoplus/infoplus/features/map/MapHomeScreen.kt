package br.com.infoplus.infoplus.features.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import br.com.infoplus.infoplus.core.sidebar.AppDrawerContent
import br.com.infoplus.infoplus.core.sidebar.AppDrawerItem
import br.com.infoplus.infoplus.features.map.components.GpsStatusBanner
import br.com.infoplus.infoplus.features.map.components.MapContainer
import br.com.infoplus.infoplus.features.map.components.MapOverviewSheet
import br.com.infoplus.infoplus.features.map.components.MapTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapHomeScreen(
    onDrawerItemClick: (AppDrawerItem) -> Unit = {},
    onExitApp: () -> Unit = {},
    onQuickAlertClick: () -> Unit = {},
    viewModel: MapHomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsState()

    val scaffoldState = rememberBottomSheetScaffoldState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = viewModel::onLocationPermissionResult
    )

    LaunchedEffect(Unit) {
        val permissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            viewModel.onLocationPermissionResult(true)
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.onScreenResumed()
                Lifecycle.Event.ON_PAUSE -> viewModel.onScreenPaused()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(state.cameraTarget) {
        if (state.cameraTarget != null) {
            viewModel.onCameraTargetConsumed()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            AppDrawerContent(
                selectedRoute = null,
                onItemClick = { item ->
                    scope.launch { drawerState.close() }

                    when (item) {
                        AppDrawerItem.ExitApp -> onExitApp()
                        else -> onDrawerItemClick(item)
                    }
                }
            )
        }
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 150.dp,
            sheetDragHandle = {
                BottomSheetDefaults.DragHandle()
            },
            sheetContainerColor = MaterialTheme.colorScheme.surface,
            sheetShadowElevation = 12.dp,
            sheetContent = {
                MapOverviewSheet(
                    nearbyOccurrencesCount = state.nearbyOccurrencesCount,
                    nearbySafeZonesCount = state.nearbySafeZonesCount,
                    safetyMessage = state.safetyMessage
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                MapContainer(
                    markers = state.filteredMarkers,
                    zones = state.zones,
                    currentLocation = state.currentLocation,
                    cameraTarget = state.cameraTarget,
                    hasLocationPermission = state.hasLocationPermission,
                    followUser = state.followUser,
                    userBearing = state.userMarkerRotation,
                    onMarkerClick = viewModel::onMarkerSelected,
                    onMapTap = {
                        if (drawerState.isOpen) {
                            scope.launch { drawerState.close() }
                        }
                    },
                    onUserMoveMap = viewModel::onUserMoveMap,
                    onCameraIdle = viewModel::onCameraIdle
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp, top = 12.dp)
                ) {
                    MapTopBar(
                        onMenuClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    )
                }

                if (state.shouldShowGpsWarning) {
                    GpsStatusBanner(
                        onEnableLocationClick = {
                            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 72.dp, start = 16.dp, end = 16.dp)
                    )
                }

                FloatingActionButton(
                    onClick = {
                        val permissionGranted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED

                        if (permissionGranted) {
                            viewModel.onLocationPermissionResult(true)
                            viewModel.onMyLocationClick()
                        } else {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 170.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Minha localização"
                    )
                }

                FloatingActionButton(
                    onClick = onQuickAlertClick,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                        .size(68.dp),
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alerta rápido"
                    )
                }
            }
        }
    }
}
