package br.com.infoplus.infoplus.features.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.infoplus.infoplus.core.location.LocationAvailabilityChecker
import br.com.infoplus.infoplus.core.location.LocationTracker
import br.com.infoplus.infoplus.core.sensors.OrientationProvider
import br.com.infoplus.infoplus.features.map.model.CasesMapFilter
import br.com.infoplus.infoplus.features.map.model.MapMarkerUi
import br.com.infoplus.infoplus.features.map.model.MapZoneUi
import br.com.infoplus.infoplus.features.map.model.ZoneType
import br.com.infoplus.infoplus.features.report.data.ReportLocalStore
import br.com.infoplus.infoplus.features.report.model.OccurrenceCategory
import br.com.infoplus.infoplus.features.report.model.OccurrenceRecord
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class MapHomeUiState(
    val allMarkers: List<MapMarkerUi> = emptyList(),
    val filteredMarkers: List<MapMarkerUi> = emptyList(),
    val zones: List<MapZoneUi> = emptyList(),
    val selectedFilter: CasesMapFilter = CasesMapFilter.ALL,
    val nearbyOccurrencesCount: Int = 0,
    val nearbySafeZonesCount: Int = 0,
    val safetyMessage: String = "Verificando sua região...",
    val currentLocation: LatLng? = null,
    val cameraTarget: LatLng? = null,
    val hasLocationPermission: Boolean = false,
    val isGpsEnabled: Boolean = true,
    val shouldShowGpsWarning: Boolean = false,
    val followUser: Boolean = true,
    val hasCenteredInitialLocation: Boolean = false,
    val userMarkerRotation: Float = 0f,
    val userSpeedMetersPerSecond: Float = 0f,
    val userBearing: Float = 0f,
    val sensorBearing: Float = 0f,
    val effectiveBearing: Float = 0f
)

@HiltViewModel
class MapHomeViewModel @Inject constructor(
    private val store: ReportLocalStore,
    private val locationTracker: LocationTracker,
    private val locationAvailabilityChecker: LocationAvailabilityChecker,
    private val orientationProvider: OrientationProvider
) : ViewModel() {

    private val _state = MutableStateFlow(MapHomeUiState())
    val state = _state.asStateFlow()

    private var locationUpdatesJob: Job? = null
    private var orientationUpdatesJob: Job? = null

    init {
        observeHistory()
    }

    fun onScreenResumed() {
        refreshLocationAvailability()
        startOrientationTrackingIfPossible()
    }

    fun onScreenPaused() {
        stopLocationTracking()
        stopOrientationTracking()
    }

    fun onLocationPermissionResult(granted: Boolean) {
        _state.value = _state.value.copy(
            hasLocationPermission = granted,
            shouldShowGpsWarning = granted && !locationAvailabilityChecker.isLocationEnabled()
        )

        if (granted) {
            startLocationTrackingIfPossible()
            startOrientationTrackingIfPossible()
        } else {
            stopLocationTracking()
            stopOrientationTracking()
        }
    }

    fun refreshLocationAvailability() {
        val enabled = locationAvailabilityChecker.isLocationEnabled()

        _state.value = _state.value.copy(
            isGpsEnabled = enabled,
            shouldShowGpsWarning = _state.value.hasLocationPermission && !enabled
        )

        if (enabled && _state.value.hasLocationPermission) {
            startLocationTrackingIfPossible()
            startOrientationTrackingIfPossible()
        } else {
            stopLocationTracking()
            stopOrientationTracking()
        }
    }

    fun onMyLocationClick() {
        _state.value = _state.value.copy(followUser = true)
        refreshLocationAvailability()

        if (!_state.value.hasLocationPermission || !_state.value.isGpsEnabled) {
            return
        }

        viewModelScope.launch {
            val freshLocation = locationTracker.getCurrentLocation()

            if (freshLocation != null) {
                _state.value = _state.value.copy(
                    followUser = true,
                    cameraTarget = freshLocation
                )

                applyRealtimeLocation(
                    location = freshLocation,
                    bearing = _state.value.userBearing,
                    speedMetersPerSecond = _state.value.userSpeedMetersPerSecond
                )
            } else {
                _state.value.currentLocation?.let { lastKnownLocation ->
                    _state.value = _state.value.copy(
                        followUser = true,
                        cameraTarget = lastKnownLocation
                    )

                    updateUserLocation(
                        lastKnownLocation.latitude,
                        lastKnownLocation.longitude
                    )
                }
            }
        }
    }

    fun onUserMoveMap() {
        _state.value = _state.value.copy(followUser = false)
    }

    fun onMarkerSelected(marker: MapMarkerUi) {
        _state.value = _state.value.copy(
            cameraTarget = LatLng(marker.lat, marker.lon),
            followUser = false
        )
    }

    fun onCameraIdle(target: LatLng) {
        updateUserLocation(
            lat = target.latitude,
            lon = target.longitude
        )
    }

    fun onCameraTargetConsumed() {
        _state.value = _state.value.copy(cameraTarget = null)
    }

    fun setFilter(filter: CasesMapFilter) {
        val markers = _state.value.allMarkers
        val filteredMarkers = applyFilter(markers, filter)
        val zones = buildZones(filteredMarkers)

        _state.value = _state.value.copy(
            selectedFilter = filter,
            filteredMarkers = filteredMarkers,
            zones = zones
        )

        _state.value.currentLocation?.let { location ->
            updateUserLocation(location.latitude, location.longitude)
        }
    }

    private fun startLocationTrackingIfPossible() {
        if (locationUpdatesJob != null) return
        if (!_state.value.hasLocationPermission) return

        if (!locationAvailabilityChecker.isLocationEnabled()) {
            _state.value = _state.value.copy(
                isGpsEnabled = false,
                shouldShowGpsWarning = true
            )
            return
        }

        _state.value = _state.value.copy(
            isGpsEnabled = true,
            shouldShowGpsWarning = false
        )

        locationUpdatesJob = viewModelScope.launch {
            locationTracker.locationStateUpdates().collectLatest { update ->
                applyRealtimeLocation(
                    location = update.latLng,
                    bearing = update.bearing,
                    speedMetersPerSecond = update.speedMetersPerSecond
                )
            }
        }

        viewModelScope.launch {
            locationTracker.getCurrentLocation()?.let { location ->
                applyRealtimeLocation(
                    location = location,
                    bearing = _state.value.userBearing,
                    speedMetersPerSecond = _state.value.userSpeedMetersPerSecond
                )
            }
        }
    }

    private fun stopLocationTracking() {
        locationUpdatesJob?.cancel()
        locationUpdatesJob = null
    }

    private fun startOrientationTrackingIfPossible() {
        if (orientationUpdatesJob != null) return

        orientationUpdatesJob = viewModelScope.launch {
            orientationProvider.orientationUpdates().collectLatest { sensorBearing ->
                val effective = resolveEffectiveBearing(
                    gpsBearing = _state.value.userBearing,
                    sensorBearing = sensorBearing,
                    speedMetersPerSecond = _state.value.userSpeedMetersPerSecond
                )

                _state.value = _state.value.copy(
                    sensorBearing = sensorBearing,
                    effectiveBearing = effective,
                    userMarkerRotation = effective
                )
            }
        }
    }

    private fun stopOrientationTracking() {
        orientationUpdatesJob?.cancel()
        orientationUpdatesJob = null
    }

    private fun resolveEffectiveBearing(
        gpsBearing: Float,
        sensorBearing: Float,
        speedMetersPerSecond: Float
    ): Float {
        return if (speedMetersPerSecond > 1.5f) {
            gpsBearing
        } else {
            sensorBearing
        }
    }

    private fun applyRealtimeLocation(
        location: LatLng,
        bearing: Float,
        speedMetersPerSecond: Float
    ) {
        val isFirstLocation = !_state.value.hasCenteredInitialLocation

        val gpsBearing = if (speedMetersPerSecond > 0.8f) {
            bearing
        } else {
            _state.value.userBearing
        }

        val effective = resolveEffectiveBearing(
            gpsBearing = gpsBearing,
            sensorBearing = _state.value.sensorBearing,
            speedMetersPerSecond = speedMetersPerSecond
        )

        _state.value = _state.value.copy(
            currentLocation = location,
            cameraTarget = if (_state.value.followUser || isFirstLocation) {
                location
            } else {
                _state.value.cameraTarget
            },
            hasCenteredInitialLocation = true,
            userBearing = gpsBearing,
            userSpeedMetersPerSecond = speedMetersPerSecond,
            effectiveBearing = effective,
            userMarkerRotation = effective
        )

        updateUserLocation(
            lat = location.latitude,
            lon = location.longitude
        )
    }

    fun updateUserLocation(lat: Double, lon: Double) {
        val markers = _state.value.filteredMarkers
        val zones = _state.value.zones

        val nearbyOccurrences = markers.count { marker ->
            distanceInMeters(
                lat1 = lat,
                lon1 = lon,
                lat2 = marker.lat,
                lon2 = marker.lon
            ) <= 1000.0
        }

        val nearbySafeZones = zones.count { zone ->
            zone.type == ZoneType.FRIENDLY &&
                    distanceInMeters(
                        lat1 = lat,
                        lon1 = lon,
                        lat2 = zone.centerLat,
                        lon2 = zone.centerLon
                    ) <= 1000.0
        }

        val message = if (nearbyOccurrences == 0) {
            "Você está seguro. Não há incidentes ocorrendo em um raio de 1 km."
        } else {
            "Há $nearbyOccurrences incidente(s) registrado(s) em um raio de 1 km da sua localização."
        }

        _state.value = _state.value.copy(
            nearbyOccurrencesCount = nearbyOccurrences,
            nearbySafeZonesCount = nearbySafeZones,
            safetyMessage = message
        )
    }

    private fun observeHistory() {
        viewModelScope.launch {
            store.historyFlow().collectLatest { history ->
                val markers = buildMarkers(history)
                val selectedFilter = _state.value.selectedFilter
                val filteredMarkers = applyFilter(markers, selectedFilter)
                val zones = buildZones(filteredMarkers)

                _state.value = _state.value.copy(
                    allMarkers = markers,
                    filteredMarkers = filteredMarkers,
                    zones = zones
                )

                _state.value.currentLocation?.let { location ->
                    updateUserLocation(location.latitude, location.longitude)
                }
            }
        }
    }

    private fun applyFilter(
        markers: List<MapMarkerUi>,
        filter: CasesMapFilter
    ): List<MapMarkerUi> {
        return markers.filter { marker ->
            when (filter) {
                CasesMapFilter.ALL -> true

                CasesMapFilter.VIOLENCE -> marker.category in setOf(
                    OccurrenceCategory.VIOLENCIA,
                    OccurrenceCategory.ASSALTO,
                    OccurrenceCategory.EMERGENCIA_MEDICA,
                    OccurrenceCategory.DESAPARECIMENTO
                )

                CasesMapFilter.DISCRIMINATION ->
                    marker.category == OccurrenceCategory.ASSEDIO

                CasesMapFilter.OTHER ->
                    marker.category == OccurrenceCategory.OUTROS || marker.category == null
            }
        }
    }

    private fun buildMarkers(history: List<OccurrenceRecord>): List<MapMarkerUi> {
        return history.mapNotNull { record ->
            val lat = record.draft.lat ?: return@mapNotNull null
            val lon = record.draft.lon ?: return@mapNotNull null

            MapMarkerUi(
                id = record.id,
                lat = lat,
                lon = lon,
                title = record.draft.category?.name ?: "Ocorrência",
                snippet = record.draft.city ?: "",
                status = record.status,
                category = record.draft.category
            )
        }
    }

    private fun getSeverityWeight(category: OccurrenceCategory?): Int {
        return when (category) {
            OccurrenceCategory.ASSALTO -> 4
            OccurrenceCategory.VIOLENCIA -> 4
            OccurrenceCategory.DESAPARECIMENTO -> 4
            OccurrenceCategory.EMERGENCIA_MEDICA -> 3
            OccurrenceCategory.ASSEDIO -> 2
            OccurrenceCategory.OUTROS, null -> 1
        }
    }

    private fun buildZones(markers: List<MapMarkerUi>): List<MapZoneUi> {
        if (markers.isEmpty()) return emptyList()

        val grouped = mutableListOf<MutableList<MapMarkerUi>>()
        val threshold = 0.0035

        markers.forEach { marker ->
            val cluster = grouped.firstOrNull { group ->
                group.any { existing ->
                    abs(existing.lat - marker.lat) < threshold &&
                            abs(existing.lon - marker.lon) < threshold
                }
            }

            if (cluster != null) {
                cluster.add(marker)
            } else {
                grouped.add(mutableListOf(marker))
            }
        }

        return grouped.mapIndexedNotNull { index, group ->
            if (group.size < 2) return@mapIndexedNotNull null

            val centerLat = group.map { it.lat }.average()
            val centerLon = group.map { it.lon }.average()
            val severityScore = group.sumOf { getSeverityWeight(it.category) }
            val zoneType = if (severityScore >= 7) ZoneType.RISK else ZoneType.FRIENDLY
            val radius = 120.0 + (group.size * 28.0)

            MapZoneUi(
                id = "zone_$index",
                centerLat = centerLat,
                centerLon = centerLon,
                radiusMeters = radius,
                type = zoneType,
                title = if (zoneType == ZoneType.RISK) {
                    "Zona de risco"
                } else {
                    "Zona recomendada"
                },
                score = severityScore,
                supportCount = group.size
            )
        }
    }

    private fun distanceInMeters(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadius = 6371000.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2) *
                sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    override fun onCleared() {
        stopLocationTracking()
        stopOrientationTracking()
        super.onCleared()
    }
}