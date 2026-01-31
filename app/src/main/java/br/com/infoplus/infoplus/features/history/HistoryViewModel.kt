package br.com.infoplus.infoplus.features.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.infoplus.infoplus.features.report.data.ReportLocalStore
import br.com.infoplus.infoplus.features.report.model.OccurrenceRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    store: ReportLocalStore
) : ViewModel() {

    val history = store.historyFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList<OccurrenceRecord>()
    )
}
