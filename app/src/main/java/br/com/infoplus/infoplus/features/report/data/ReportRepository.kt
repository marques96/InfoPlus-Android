package br.com.infoplus.infoplus.features.report.data

import br.com.infoplus.infoplus.features.report.model.OccurrenceDraft
import kotlinx.coroutines.delay

class ReportRepository {
    // MVP: simula envio. Depois você troca por API/Firebase.
    suspend fun submit(draft: OccurrenceDraft) {
        delay(900)
        // aqui você poderia lançar erro random p/ testar:
        // if (draft.title.contains("erro", true)) error("Falha simulada")
    }
}
