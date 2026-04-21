package br.com.infoplus.infoplus.features.map.model

import br.com.infoplus.infoplus.features.report.model.OccurrenceCategory

sealed interface MapFeedItem {
    data class ZoneCard(
        val id: String,
        val title: String,
        val subtitle: String,
        val type: ZoneType,
        val score: Int,
        val supportCount: Int,
        val centerLat: Double,
        val centerLon: Double
    ) : MapFeedItem

    data class OccurrenceCard(
        val id: String,
        val title: String,
        val subtitle: String,
        val category: OccurrenceCategory?,
        val lat: Double,
        val lon: Double
    ) : MapFeedItem
}