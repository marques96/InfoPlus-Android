package br.com.infoplus.infoplus.features.report.data

import br.com.infoplus.infoplus.features.report.model.OccurrenceDraft
import kotlinx.coroutines.delay
import javax.inject.Inject

class ReportRepository @Inject constructor() {
    suspend fun submit(draft: OccurrenceDraft) {
        delay(900)
    }
}
