package br.com.infoplus.infoplus.features.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.infoplus.infoplus.core.network.NetworkMonitor
import br.com.infoplus.infoplus.features.report.data.ReportLocalStore
import br.com.infoplus.infoplus.features.report.data.ReportRepository
import br.com.infoplus.infoplus.features.report.location.LocationProvider
import br.com.infoplus.infoplus.features.report.model.Attachment
import br.com.infoplus.infoplus.features.report.model.AttachmentType
import br.com.infoplus.infoplus.features.report.model.OccurrenceCategory
import br.com.infoplus.infoplus.features.report.model.OccurrenceDraft
import br.com.infoplus.infoplus.features.report.model.ReportUiState
import br.com.infoplus.infoplus.features.report.location.ReverseGeocoder
import br.com.infoplus.infoplus.features.report.model.OccurrenceRecord
import br.com.infoplus.infoplus.features.report.model.ReportStatus
import br.com.infoplus.infoplus.features.report.model.VictimType
import br.com.infoplus.infoplus.features.report.model.Gender
import java.util.UUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val store: ReportLocalStore,
    private val repo: ReportRepository,
    private val locationProvider: LocationProvider,
    private val network: NetworkMonitor,
    private val reverseGeocoder: ReverseGeocoder
    ) : ViewModel() {

    private val _state = MutableStateFlow(ReportUiState())
    val state = _state.asStateFlow()

    private var autosaveJob: Job? = null

    init {
        viewModelScope.launch {
            network.isOnlineFlow().collect { online ->
                _state.value = _state.value.copy(isOnline = online)

                if (online) {
                    syncPendingQueue()
                }
            }
        }

        viewModelScope.launch {
            store.draftFlow().collect { draft ->
                if (draft != null) _state.value = _state.value.copy(draft = draft)
            }
        }
    }

    private fun updateDraft(newDraft: OccurrenceDraft) {
        _state.value = _state.value.copy(draft = newDraft, errorMessage = null)
        autosaveJob?.cancel()
        autosaveJob = viewModelScope.launch {
            store.saveDraft(newDraft)
        }
    }

    private suspend fun syncPendingQueue() {
        val pending = store.getPending()
        if (pending.isEmpty()) return

        for (record in pending) {
            try {
                repo.submit(record.draft)
                store.removePending(record.id)
                store.updateHistoryStatus(record.id, ReportStatus.SYNCED)
            } catch (_: Exception) {
                // deixa na fila para tentar depois
            }
        }
    }

    fun setError(msg: String) {
        _state.value = _state.value.copy(errorMessage = msg)
    }

    fun setCategory(v: OccurrenceCategory) = updateDraft(_state.value.draft.copy(category = v))
    fun setTitle(v: String) = updateDraft(_state.value.draft.copy(title = v))
    fun setDescription(v: String) = updateDraft(_state.value.draft.copy(description = v))
    fun setVictimType(v: VictimType) = updateDraft(_state.value.draft.copy(victimType = v))
    fun setVictimGender(v: Gender) = updateDraft(_state.value.draft.copy(victimGender = v))
    fun setAnonymous(v: Boolean) = updateDraft(_state.value.draft.copy(isAnonymous = v))
    fun setTerms(v: Boolean) = updateDraft(_state.value.draft.copy(acceptedTerms = v))

    fun setUseCurrentLocation(v: Boolean) = updateDraft(_state.value.draft.copy(useCurrentLocation = v))
    fun setManualLocation(v: String) = updateDraft(_state.value.draft.copy(manualLocationText = v))
    fun setDateTimeMillis(v: Long) = updateDraft(_state.value.draft.copy(dateTimeMillis = v))

    // --------------------------
    // Anexos (foto/vídeo/áudio)
    // --------------------------
    fun addAttachment(uri: String, type: AttachmentType) {
        val current = _state.value.draft.attachments
        if (current.size >= 3) return
        val next = (current + Attachment(uri = uri, type = type))
            .distinctBy { it.uri } // evita duplicar o mesmo uri
        updateDraft(_state.value.draft.copy(attachments = next))
    }

    fun removeAttachment(uri: String) {
        val next = _state.value.draft.attachments.filterNot { it.uri == uri }
        updateDraft(_state.value.draft.copy(attachments = next))
    }

    // --------------------------
    // Localização (best location)
    // --------------------------
    fun captureLocation(hasPermission: Boolean) {
        if (!hasPermission) {
            _state.value = _state.value.copy(errorMessage = "Permissão de localização necessária.")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isGettingLocation = true, errorMessage = null)

            val loc = locationProvider.getBestLocation()

            if (loc == null) {
                _state.value = _state.value.copy(
                    isGettingLocation = false,
                    errorMessage = "Não foi possível obter localização. Informe manualmente."
                )
                return@launch
            }

            val lat = loc.first
            val lon = loc.second

            // 1) salva coordenadas imediatamente
            updateDraft(_state.value.draft.copy(lat = lat, lon = lon))

            // 2) resolve endereço em background (não trava UI)
            val resolved = withContext(Dispatchers.IO) {
                reverseGeocoder.fromLatLng(lat, lon)
            }

            _state.value = _state.value.copy(isGettingLocation = false)

            // 3) se conseguiu, salva campos do endereço
            if (resolved != null) {
                updateDraft(
                    _state.value.draft.copy(
                        street = resolved.street,
                        number = resolved.number,
                        district = resolved.district,
                        city = resolved.city
                    )
                )
            } else {
                _state.value = _state.value.copy(
                    errorMessage = "Localização capturada, mas não foi possível obter o endereço."
                )
            }

        }
    }

    fun syncNow() {
        viewModelScope.launch { syncPendingQueue() }
    }

    // --------------------------
    // Envio / Offline pendente
    // --------------------------
    fun submit(onSuccess: () -> Unit) {
        val s = _state.value
        if (!s.canSubmit) {
            _state.value = s.copy(errorMessage = "Preencha os campos obrigatórios para enviar.")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isSubmitting = true,
                errorMessage = null,
                isOfflinePending = false
            )

            try {
                val draft = _state.value.draft
                val record = OccurrenceRecord(
                    id = UUID.randomUUID().toString(),
                    createdAtMillis = System.currentTimeMillis(),
                    status = if (_state.value.isOnline) ReportStatus.SYNCED else ReportStatus.QUEUED,
                    draft = draft
                )

                // Sempre entra no histórico (pra você ver no pitch)
                store.addToHistory(record)

                if (_state.value.isOnline) {
                    repo.submit(draft)
                    store.updateHistoryStatus(record.id, ReportStatus.SYNCED)
                    store.clearDraft()
                    _state.value = ReportUiState()
                    onSuccess()
                } else {
                    // Offline: entra na fila
                    store.enqueuePending(record)
                    store.clearDraft()
                    _state.value = ReportUiState(isOfflinePending = true)
                    onSuccess()
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    errorMessage = e.message ?: "Falha ao enviar."
                )
            }
        }
    }
}