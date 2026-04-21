package br.com.infoplus.infoplus.features.map.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import br.com.infoplus.infoplus.R
import br.com.infoplus.infoplus.features.map.model.MapMarkerUi
import br.com.infoplus.infoplus.features.map.model.MapZoneUi
import br.com.infoplus.infoplus.features.map.model.ZoneType
import br.com.infoplus.infoplus.features.report.model.OccurrenceCategory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

private const val DEFAULT_ZOOM_WITH_USER = 16f
private const val DEFAULT_ZOOM_FOLLOWING_USER = 17f
private const val DEFAULT_ZOOM_WITHOUT_USER = 12f
private const val USER_MARKER_Z_INDEX = 10f
private const val INCIDENT_MARKER_Z_INDEX = 2f
private const val ZONE_Z_INDEX = 1f
private const val MIN_USER_MARKER_MOVE_METERS = 1.5f
private const val MIN_CAMERA_MOVE_METERS = 5f
private const val USER_MARKER_BASE_ROTATION_OFFSET = 0f

@SuppressLint("MissingPermission")
@Composable
fun MapContainer(
    markers: List<MapMarkerUi>,
    zones: List<MapZoneUi>,
    currentLocation: LatLng?,
    cameraTarget: LatLng?,
    hasLocationPermission: Boolean,
    followUser: Boolean,
    userBearing: Float = 0f,
    onMarkerClick: (MapMarkerUi) -> Unit,
    onMapTap: () -> Unit = {},
    onUserMoveMap: () -> Unit,
    onCameraIdle: (LatLng) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val latestOnMarkerClick by rememberUpdatedState(onMarkerClick)
    val latestOnUserMoveMap by rememberUpdatedState(onUserMoveMap)
    val latestOnCameraIdle by rememberUpdatedState(onCameraIdle)
    val latestOnMapTap by rememberUpdatedState(onMapTap)

    val mapView = rememberMapViewWithLifecycle(
        context = context,
        lifecycleOwner = lifecycleOwner
    )

    val userMarkerIcon = remember(context) {
        createBitmapDescriptorFromVector(
            context = context,
            drawableRes = R.drawable.ic_user_navigation
        )
    }

    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }
    var userMarker by remember { mutableStateOf<Marker?>(null) }
    var lastCameraPosition by remember { mutableStateOf<LatLng?>(null) }
    var lastUserMarkerPosition by remember { mutableStateOf<LatLng?>(null) }
    val renderedMarkersVersion = remember { mutableIntStateOf(0) }

    val renderedIncidentMarkers = remember { mutableStateListOf<Marker>() }
    val renderedZoneCircles = remember { mutableStateListOf<Circle>() }

    MapInitializerEffect(
        mapView = mapView,
        currentLocation = currentLocation,
        onMapReady = { map -> googleMap = map },
        onMarkerClick = latestOnMarkerClick,
        onMapTap = latestOnMapTap,
        onUserMoveMap = latestOnUserMoveMap,
        onCameraIdle = latestOnCameraIdle
    )

    RenderStaticMapContentEffect(
        googleMap = googleMap,
        markers = markers,
        zones = zones,
        renderedIncidentMarkers = renderedIncidentMarkers,
        renderedZoneCircles = renderedZoneCircles,
        renderedMarkersVersion = renderedMarkersVersion
    )

    RenderUserMarkerEffect(
        googleMap = googleMap,
        currentLocation = currentLocation,
        hasLocationPermission = hasLocationPermission,
        userBearing = userBearing,
        userMarkerIcon = userMarkerIcon,
        renderedMarkersVersion = renderedMarkersVersion.intValue,
        userMarker = userMarker,
        onUserMarkerChanged = { userMarker = it },
        lastUserMarkerPosition = lastUserMarkerPosition,
        onLastUserMarkerPositionChanged = { lastUserMarkerPosition = it }
    )

    CameraFollowEffect(
        googleMap = googleMap,
        cameraTarget = cameraTarget,
        followUser = followUser,
        lastCameraPosition = lastCameraPosition,
        onLastCameraPositionChanged = { lastCameraPosition = it }
    )

    AndroidView(
        factory = { mapView }
    )
}

@Composable
private fun rememberMapViewWithLifecycle(
    context: Context,
    lifecycleOwner: LifecycleOwner
): MapView {
    val mapView = remember {
        MapView(context).apply {
            layoutParams = android.view.ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) = mapView.onCreate(null)
            override fun onStart(owner: LifecycleOwner) = mapView.onStart()
            override fun onResume(owner: LifecycleOwner) = mapView.onResume()
            override fun onPause(owner: LifecycleOwner) = mapView.onPause()
            override fun onStop(owner: LifecycleOwner) = mapView.onStop()
            override fun onDestroy(owner: LifecycleOwner) = mapView.onDestroy()
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    return mapView
}

@Composable
private fun MapInitializerEffect(
    mapView: MapView,
    currentLocation: LatLng?,
    onMapReady: (GoogleMap) -> Unit,
    onMarkerClick: (MapMarkerUi) -> Unit,
    onMapTap: () -> Unit,
    onUserMoveMap: () -> Unit,
    onCameraIdle: (LatLng) -> Unit
) {
    LaunchedEffect(mapView) {
        mapView.getMapAsync { map ->
            configureGoogleMapUi(map)

            val initialLatLng = currentLocation ?: LatLng(-3.1190, -60.0217)
            val initialZoom = if (currentLocation != null) {
                DEFAULT_ZOOM_WITH_USER
            } else {
                DEFAULT_ZOOM_WITHOUT_USER
            }

            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(initialLatLng, initialZoom)
            )

            map.setOnMarkerClickListener { clicked ->
                val marker = clicked.tag as? MapMarkerUi
                marker?.let(onMarkerClick)
                marker != null
            }

            map.setOnMapClickListener {
                onMapTap()
            }

            map.setOnCameraMoveStartedListener { reason ->
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    onUserMoveMap()
                }
            }

            map.setOnCameraIdleListener {
                onCameraIdle(map.cameraPosition.target)
            }

            onCameraIdle(initialLatLng)
            onMapReady(map)
        }
    }
}

private fun configureGoogleMapUi(map: GoogleMap) {
    map.uiSettings.isZoomControlsEnabled = false
    map.uiSettings.isCompassEnabled = true
    map.uiSettings.isMyLocationButtonEnabled = false
    map.isBuildingsEnabled = true
    map.isMyLocationEnabled = false
}

@Composable
private fun RenderStaticMapContentEffect(
    googleMap: GoogleMap?,
    markers: List<MapMarkerUi>,
    zones: List<MapZoneUi>,
    renderedIncidentMarkers: MutableList<Marker>,
    renderedZoneCircles: MutableList<Circle>,
    renderedMarkersVersion: MutableIntState
) {
    LaunchedEffect(googleMap, markers, zones) {
        val map = googleMap ?: return@LaunchedEffect

        clearIncidentMarkers(renderedIncidentMarkers)
        clearZoneCircles(renderedZoneCircles)

        renderZones(
            map = map,
            zones = zones,
            renderedZoneCircles = renderedZoneCircles
        )

        renderIncidentMarkers(
            map = map,
            markers = markers,
            renderedIncidentMarkers = renderedIncidentMarkers
        )

        renderedMarkersVersion.intValue++
    }
}

private fun clearIncidentMarkers(renderedIncidentMarkers: MutableList<Marker>) {
    renderedIncidentMarkers.forEach { it.remove() }
    renderedIncidentMarkers.clear()
}

private fun clearZoneCircles(renderedZoneCircles: MutableList<Circle>) {
    renderedZoneCircles.forEach { it.remove() }
    renderedZoneCircles.clear()
}

private fun renderZones(
    map: GoogleMap,
    zones: List<MapZoneUi>,
    renderedZoneCircles: MutableList<Circle>
) {
    zones.forEach { zone ->
        val colors = zoneColors(zone.type)

        val circle = map.addCircle(
            CircleOptions()
                .center(LatLng(zone.centerLat, zone.centerLon))
                .radius(zone.radiusMeters)
                .fillColor(colors.fillColor)
                .strokeColor(colors.strokeColor)
                .strokeWidth(3f)
                .zIndex(ZONE_Z_INDEX)
        )

        renderedZoneCircles += circle
    }
}

private fun renderIncidentMarkers(
    map: GoogleMap,
    markers: List<MapMarkerUi>,
    renderedIncidentMarkers: MutableList<Marker>
) {
    markers.forEach { marker ->
        val googleMarker = map.addMarker(
            MarkerOptions()
                .position(LatLng(marker.lat, marker.lon))
                .title(marker.title)
                .snippet(marker.snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(markerHue(marker.category)))
                .zIndex(INCIDENT_MARKER_Z_INDEX)
        )

        googleMarker?.tag = marker
        googleMarker?.let { renderedIncidentMarkers += it }
    }
}

@Composable
private fun RenderUserMarkerEffect(
    googleMap: GoogleMap?,
    currentLocation: LatLng?,
    hasLocationPermission: Boolean,
    userBearing: Float,
    userMarkerIcon: BitmapDescriptor?,
    renderedMarkersVersion: Int,
    userMarker: Marker?,
    onUserMarkerChanged: (Marker?) -> Unit,
    lastUserMarkerPosition: LatLng?,
    onLastUserMarkerPositionChanged: (LatLng?) -> Unit
) {
    LaunchedEffect(
        googleMap,
        currentLocation,
        hasLocationPermission,
        userBearing,
        userMarkerIcon,
        renderedMarkersVersion
    ) {
        val map = googleMap ?: return@LaunchedEffect

        if (!hasLocationPermission || currentLocation == null) {
            userMarker?.remove()
            onUserMarkerChanged(null)
            onLastUserMarkerPositionChanged(null)
            return@LaunchedEffect
        }

        val markerRotation = normalizedUserMarkerRotation(userBearing)
        val existingMarker = userMarker

        if (existingMarker == null) {
            val markerOptions = MarkerOptions()
                .position(currentLocation)
                .anchor(0.5f, 0.5f)
                .flat(true)
                .rotation(markerRotation)
                .zIndex(USER_MARKER_Z_INDEX)

            userMarkerIcon?.let(markerOptions::icon)

            val newMarker = map.addMarker(markerOptions)
            onUserMarkerChanged(newMarker)
            onLastUserMarkerPositionChanged(currentLocation)
            return@LaunchedEffect
        }

        val shouldMoveMarker = lastUserMarkerPosition == null ||
                distanceBetween(lastUserMarkerPosition, currentLocation) > MIN_USER_MARKER_MOVE_METERS

        if (shouldMoveMarker) {
            existingMarker.position = currentLocation
            onLastUserMarkerPositionChanged(currentLocation)
        }

        existingMarker.rotation = markerRotation
        existingMarker.isFlat = true
        existingMarker.zIndex = USER_MARKER_Z_INDEX
    }
}

@Composable
private fun CameraFollowEffect(
    googleMap: GoogleMap?,
    cameraTarget: LatLng?,
    followUser: Boolean,
    lastCameraPosition: LatLng?,
    onLastCameraPositionChanged: (LatLng?) -> Unit
) {
    LaunchedEffect(googleMap, cameraTarget, followUser) {
        val map = googleMap ?: return@LaunchedEffect
        val target = cameraTarget ?: return@LaunchedEffect

        val shouldForceMove = followUser
        val shouldMove = shouldForceMove ||
                lastCameraPosition == null ||
                distanceBetween(lastCameraPosition, target) > MIN_CAMERA_MOVE_METERS

        if (!shouldMove) return@LaunchedEffect

        onLastCameraPositionChanged(target)

        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                target,
                if (followUser) DEFAULT_ZOOM_FOLLOWING_USER else DEFAULT_ZOOM_WITH_USER
            )
        )
    }
}

private fun normalizedUserMarkerRotation(userBearing: Float): Float {
    return (userBearing.normalizeBearing() + USER_MARKER_BASE_ROTATION_OFFSET).normalizeBearing()
}

private fun markerHue(category: OccurrenceCategory?): Float {
    return when (category) {
        OccurrenceCategory.ASSALTO,
        OccurrenceCategory.VIOLENCIA,
        OccurrenceCategory.DESAPARECIMENTO -> BitmapDescriptorFactory.HUE_RED

        OccurrenceCategory.ASSEDIO -> BitmapDescriptorFactory.HUE_VIOLET
        OccurrenceCategory.EMERGENCIA_MEDICA -> BitmapDescriptorFactory.HUE_ORANGE
        OccurrenceCategory.OUTROS,
        null -> BitmapDescriptorFactory.HUE_AZURE
    }
}

private data class ZoneColors(
    val fillColor: Int,
    val strokeColor: Int
)

private fun zoneColors(type: ZoneType): ZoneColors {
    return when (type) {
        ZoneType.RISK -> ZoneColors(
            fillColor = Color.argb(70, 239, 83, 80),
            strokeColor = Color.argb(180, 198, 40, 40)
        )

        ZoneType.FRIENDLY -> ZoneColors(
            fillColor = Color.argb(55, 76, 175, 80),
            strokeColor = Color.argb(170, 46, 125, 50)
        )
    }
}

private fun distanceBetween(a: LatLng, b: LatLng): Float {
    val result = FloatArray(1)
    android.location.Location.distanceBetween(
        a.latitude,
        a.longitude,
        b.latitude,
        b.longitude,
        result
    )
    return result[0]
}

private fun Float.normalizeBearing(): Float {
    var value = this % 360f
    if (value < 0f) value += 360f
    return value
}

private fun createBitmapDescriptorFromVector(
    context: Context,
    @DrawableRes drawableRes: Int
): BitmapDescriptor? {
    return try {
        val drawable = ContextCompat.getDrawable(context, drawableRes) ?: return null
        drawable.toBitmapDescriptorOrNull()
    } catch (_: Exception) {
        null
    }
}

private fun Drawable.toBitmapDescriptorOrNull(): BitmapDescriptor? {
    return try {
        val width = intrinsicWidth.takeIf { it > 0 } ?: 96
        val height = intrinsicHeight.takeIf { it > 0 } ?: 96

        val bitmap: Bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)

        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)

        BitmapDescriptorFactory.fromBitmap(bitmap)
    } catch (_: Exception) {
        null
    }
}