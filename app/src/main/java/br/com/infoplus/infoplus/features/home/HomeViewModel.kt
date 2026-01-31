package br.com.infoplus.infoplus.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.infoplus.infoplus.core.network.NetworkMonitor
import br.com.infoplus.infoplus.features.report.data.ReportLocalStore
import br.com.infoplus.infoplus.features.report.data.ReportRepository
import br.com.infoplus.infoplus.features.report.model.ReportStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.first


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val network: NetworkMonitor,
    private val store: ReportLocalStore,
    private val repo: ReportRepository
) : ViewModel() {

    init {
        observeNetworkAndSync()
    }

    private fun observeNetworkAndSync() {
        viewModelScope.launch {
            network.isOnlineFlow().collect { online ->
                if (online) {
                    syncPendingQueue()
                }
            }
        }
    }

    private suspend fun syncPendingQueue() {
        val online = network.isOnlineFlow().first()
        if (!online) return

        val pending = store.getPending()
        if (pending.isEmpty()) return

        for (record in pending) {
            try {
                repo.submit(record.draft)
                store.removePending(record.id)
                store.updateHistoryStatus(record.id, ReportStatus.SYNCED)
            } catch (_: Exception) {
                // mantém na fila
            }
        }
    }


    // botão manual (vamos usar depois nas ações rápidas)
    fun syncNow() {
        viewModelScope.launch {
            syncPendingQueue()
        }
    }
}
