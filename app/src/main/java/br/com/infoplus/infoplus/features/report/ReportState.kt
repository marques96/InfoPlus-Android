package br.com.infoplus.infoplus.features.report

enum class OccurrenceCategory { ASSALTO, ASSEDIO, VIOLENCIA, DESAPARECIMENTO, EMERGENCIA, OUTROS }

data class OccurrenceDraft(
    val category: OccurrenceCategory? = null,
    val title: String = "",
    val description: String = "",
    val isAnonymous: Boolean = true,
    val acceptedTerms: Boolean = false,
    val isOnline: Boolean = true

)

data class ReportUiState(
    val draft: OccurrenceDraft = OccurrenceDraft(),
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
) {
    val canSubmit: Boolean
        get() = draft.category != null
                && draft.title.trim().length >= 4
                && draft.description.trim().length >= 10
                && draft.acceptedTerms
                && !isSubmitting
}
