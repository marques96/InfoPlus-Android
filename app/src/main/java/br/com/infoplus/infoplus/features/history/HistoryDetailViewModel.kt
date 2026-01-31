package br.com.infoplus.infoplus.features.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.infoplus.infoplus.features.report.data.ReportLocalStore
import br.com.infoplus.infoplus.features.report.data.ReportRepository
import br.com.infoplus.infoplus.features.report.model.OccurrenceRecord
import br.com.infoplus.infoplus.features.report.model.ReportStatus
import br.com.infoplus.infoplus.core.network.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.first

data class HistoryDetailUiState(
    val isLoading: Boolean = true,
    val record: OccurrenceRecord? = null,
    val status: ReportStatus? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class HistoryDetailViewModel @Inject constructor(
    private val store: ReportLocalStore,
    private val repo: ReportRepository,
    private val network: NetworkMonitor
) : ViewModel() {

    private val _ui = MutableStateFlow(HistoryDetailUiState())
    val ui = _ui.asStateFlow()

    private var lastId: String? = null

    fun load(id: String) {
        if (lastId == id && _ui.value.record != null) return // evita recarregar a cada recomposição
        lastId = id

        viewModelScope.launch {
            _ui.value = HistoryDetailUiState(isLoading = true)

            val record = store.getHistory().firstOrNull { it.id == id }
            if (record == null) {
                _ui.value = HistoryDetailUiState(
                    isLoading = false,
                    errorMessage = "Registro não encontrado."
                )
                return@launch
            }

            _ui.value = HistoryDetailUiState(
                isLoading = false,
                record = record,
                status = record.status
            )
        }
    }

    fun trySyncOne(id: String) {
        viewModelScope.launch {
            val online = network.isOnlineFlow().first()
            if (!online) {
                _ui.value = _ui.value.copy(
                    errorMessage = "Sem internet para enviar."
                )
                return@launch
            }

            val record = store.getHistory().firstOrNull { it.id == id } ?: return@launch
            if (record.status == ReportStatus.SYNCED) return@launch

            try {
                repo.submit(record.draft)
                store.removePending(id)
                store.updateHistoryStatus(id, ReportStatus.SYNCED)
                load(id)
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(
                    errorMessage = e.message ?: "Falha ao enviar."
                )
            }
        }
    }
}
