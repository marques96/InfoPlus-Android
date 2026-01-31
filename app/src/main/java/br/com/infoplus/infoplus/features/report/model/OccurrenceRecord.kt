package br.com.infoplus.infoplus.features.report.model

enum class ReportStatus { QUEUED, SYNCED }

data class OccurrenceRecord(
    val id: String,
    val createdAtMillis: Long,
    val status: ReportStatus,
    val draft: OccurrenceDraft
)
