package br.com.infoplus.infoplus.features.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onAudioAlertClick: (String) -> Unit = {}, 
    viewModel: MapHomeViewModel = hiltViewModel(),
    themeViewModel: br.com.infoplus.infoplus.core.settings.ThemeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsState()
    val isDarkMode = themeViewModel.isDarkTheme()

    val scaffoldState = rememberBottomSheetScaffoldState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = viewModel::onLocationPermissionResult
    )

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) viewModel.startRecording()
        }
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

    if (state.showAudioRegisterPrompt) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissAudioPrompt() },
            title = { Text("Áudio gravado com sucesso") },
            text = { Text("Deseja registrar uma ocorrência agora com este áudio anexado?") },
            confirmButton = {
                Button(onClick = {
                    val uri = state.lastRecordedAudioUri ?: ""
                    viewModel.dismissAudioPrompt()
                    onAudioAlertClick(uri)
                }) {
                    Text("Sim, registrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissAudioPrompt() }) {
                    Text("Não agora")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
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
                    isDarkMode = isDarkMode,
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

                AnimatedVisibility(
                    visible = state.isRecordingAudio,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 80.dp)
                ) {
                    RecordingIndicator(
                        durationSeconds = state.recordingDurationSeconds,
                        onStopClick = { viewModel.stopRecording() }
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
                        .padding(end = 16.dp, bottom = 260.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Minha localização"
                    )
                }

                FloatingActionButton(
                    onClick = {
                        val permission = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.RECORD_AUDIO
                        )
                        if (permission == PackageManager.PERMISSION_GRANTED) {
                            viewModel.startRecording()
                        } else {
                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 100.dp)
                        .size(56.dp),
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Gravar Áudio",
                        modifier = Modifier.size(28.dp)
                    )
                }

                FloatingActionButton(
                    onClick = onQuickAlertClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 180.dp)
                        .size(64.dp),
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Relatar",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecordingIndicator(
    durationSeconds: Int,
    onStopClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Surface(
        color = Color.Black.copy(alpha = 0.8f),
        contentColor = Color.White,
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color.Red.copy(alpha = alpha))
            )
            
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            HorizontalDivider(modifier = Modifier.height(24.dp).width(1.dp), color = Color.White.copy(alpha = 0.3f))

            TextButton(
                onClick = onStopClick,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("PARAR DE GRAVAR", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}
