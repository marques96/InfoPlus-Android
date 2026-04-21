package br.com.infoplus.infoplus.features.map.model

import androidx.compose.ui.graphics.Color
import br.com.infoplus.infoplus.features.report.model.OccurrenceCategory
import br.com.infoplus.infoplus.features.report.model.ReportStatus

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

enum class ZoneType {
    RISK,
    FRIENDLY
}

data class MapZoneUi(
    val id: String,
    val centerLat: Double,
    val centerLon: Double,
    val radiusMeters: Double,
    val type: ZoneType,
    val title: String,
    val score: Int,
    val supportCount: Int
)