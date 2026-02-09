package br.com.infoplus.infoplus.features.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.infoplus.infoplus.features.report.data.ReportLocalStore
import br.com.infoplus.infoplus.features.report.model.OccurrenceCategory
import br.com.infoplus.infoplus.features.report.model.OccurrenceRecord
import br.com.infoplus.infoplus.features.report.model.ReportStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class CasesMapFilter(val label: String) {
    ALL("Todos"),
    VIOLENCE("Violência"),
    DISCRIMINATION("Discriminação"),
    OTHER("Outros")
}

data class MapMarkerUi(
    val id: String,
    val lat: Double,
    val lon: Double,
    val title: String,
    val snippet: String,
    val status: ReportStatus,
    val category: OccurrenceCategory?
)

data class CasesMapUiState(
    val allMarkers: List<MapMarkerUi> = emptyList(),
    val filteredMarkers: List<MapMarkerUi> = emptyList(),
    val selectedFilter: CasesMapFilter = CasesMapFilter.ALL,
    val totalInHistory: Int = 0,
    val missingLocation: Int = 0
)

@HiltViewModel
class CasesMapViewModel @Inject constructor(
    private val store: ReportLocalStore
) : ViewModel() {

    private val _state = MutableStateFlow(CasesMapUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            store.historyFlow().collectLatest { history ->
                val (markers, missing) = buildMarkers(history)
                val selected = _state.value.selectedFilter

                _state.value = _state.value.copy(
                    allMarkers = markers,
                    filteredMarkers = applyFilter(markers, selected),
                    totalInHistory = history.size,
                    missingLocation = missing
                )
            }
        }
    }

    fun setFilter(filter: CasesMapFilter) {
        val markers = _state.value.allMarkers
        _state.value = _state.value.copy(
            selectedFilter = filter,
            filteredMarkers = applyFilter(markers, filter)
        )
    }

    private fun applyFilter(markers: List<MapMarkerUi>, filter: CasesMapFilter): List<MapMarkerUi> {
        if (filter == CasesMapFilter.ALL) return markers

        return markers.filter { m ->
            val cat = m.category
            when (filter) {
                CasesMapFilter.VIOLENCE -> cat in setOf(
                    OccurrenceCategory.VIOLENCIA,
                    OccurrenceCategory.ASSALTO,
                    OccurrenceCategory.EMERGENCIA_MEDICA,
                    OccurrenceCategory.DESAPARECIMENTO
                )
                CasesMapFilter.DISCRIMINATION -> cat in setOf(OccurrenceCategory.ASSEDIO)
                CasesMapFilter.OTHER -> cat == OccurrenceCategory.OUTROS || cat == null
                else -> true
            }
        }
    }

    private fun buildMarkers(history: List<OccurrenceRecord>): Pair<List<MapMarkerUi>, Int> {
        var missing = 0

        val markers = history.mapNotNull { r ->
            val lat = r.draft.lat
            val lon = r.draft.lon
            if (lat == null || lon == null) {
                missing++
                return@mapNotNull null
            }

            val catLabel = r.draft.category?.name
                ?.replace("_", " ")
                ?.lowercase()
                ?.replaceFirstChar { it.titlecase() }
                ?: "Ocorrência"

            val statusLabel = when (r.status) {
                ReportStatus.SYNCED -> "Sincronizado"
                ReportStatus.QUEUED -> "Salvo localmente"
            }

            val place = listOfNotNull(
                r.draft.district.takeIf { it.isNotBlank() },
                r.draft.city.takeIf { it.isNotBlank() }
            ).joinToString(" • ").ifBlank { "Local não detalhado" }

            MapMarkerUi(
                id = r.id,
                lat = lat,
                lon = lon,
                title = catLabel,
                snippet = "$place • $statusLabel",
                status = r.status,
                category = r.draft.category
            )
        }

        return markers to missing
    }
}