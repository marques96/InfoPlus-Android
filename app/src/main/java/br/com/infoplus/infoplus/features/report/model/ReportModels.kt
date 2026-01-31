package br.com.infoplus.infoplus.features.report.model

enum class OccurrenceCategory {
    ASSALTO, ASSEDIO, VIOLENCIA, DESAPARECIMENTO, EMERGENCIA_MEDICA, OUTROS
}

enum class AttachmentType { IMAGE, VIDEO, AUDIO }

enum class VictimType { SELF, OTHER }

enum class Gender {
    FEMININO,
    MASCULINO,
    NAO_BINARIO,
    TRAVESTI,
    HOMEM_TRANS,
    MULHER_TRANS,
    NAO_INFORMADO
}

data class Attachment(
    val uri: String,
    val type: AttachmentType
)

data class OccurrenceDraft(
    val category: OccurrenceCategory? = null,
    val title: String = "",
    val description: String = "",
    val dateTimeMillis: Long = System.currentTimeMillis(),

    val useCurrentLocation: Boolean = true,
    val manualLocationText: String = "",
    val lat: Double? = null,
    val lon: Double? = null,

    // ✅ Endereço resolvido (reverse geocoding)
    val street: String = "",
    val number: String = "",
    val district: String = "",
    val city: String = "",

    val victimType: VictimType? = null,
    val victimGender: Gender = Gender.NAO_INFORMADO,

    val isAnonymous: Boolean = true,
    val attachments: List<Attachment> = emptyList(),
    val acceptedTerms: Boolean = false
)


data class ReportUiState(
    val draft: OccurrenceDraft = OccurrenceDraft(),
    val isGettingLocation: Boolean = false,
    val isSubmitting: Boolean = false,
    val isOfflinePending: Boolean = false,

    val isOnline: Boolean = true,

    val errorMessage: String? = null
) {
    val hasValidLocation: Boolean
        get() = (draft.useCurrentLocation && draft.lat != null && draft.lon != null) ||
                (!draft.useCurrentLocation && draft.manualLocationText.trim().isNotEmpty())

    val canSubmit: Boolean
        get() = draft.category != null &&
                draft.title.trim().length >= 4 &&
                draft.description.trim().length >= 10 &&
                draft.victimType != null &&
                (draft.victimType != VictimType.OTHER || draft.victimGender != Gender.NAO_INFORMADO) &&
                draft.acceptedTerms &&
                hasValidLocation &&
                !isSubmitting
}
