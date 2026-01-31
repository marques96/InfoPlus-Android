package br.com.infoplus.infoplus.features.report.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.com.infoplus.infoplus.features.report.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(name = "report_store")

class ReportLocalStore(private val context: Context) {

    private val DRAFT_KEY = stringPreferencesKey("draft_json")
    private val PENDING_KEY = stringPreferencesKey("pending_json")
    private val HISTORY_KEY = stringPreferencesKey("history_json")

    // -----------------------------
    // Draft (form em andamento)
    // -----------------------------
    fun draftFlow(): Flow<OccurrenceDraft?> =
        context.dataStore.data.map { prefs ->
            prefs[DRAFT_KEY]?.let { jsonToDraft(it) }
        }

    suspend fun saveDraft(draft: OccurrenceDraft) {
        context.dataStore.edit { prefs ->
            prefs[DRAFT_KEY] = draftToJson(draft).toString()
        }
    }

    suspend fun clearDraft() {
        context.dataStore.edit { prefs -> prefs.remove(DRAFT_KEY) }
    }

    // -----------------------------
    // Pending queue (offline)
    // -----------------------------
    fun pendingFlow(): Flow<List<OccurrenceRecord>> =
        context.dataStore.data.map { prefs ->
            val raw = prefs[PENDING_KEY] ?: "[]"
            jsonToRecordsList(raw)
        }

    suspend fun getPending(): List<OccurrenceRecord> = pendingFlow().first()

    suspend fun savePending(records: List<OccurrenceRecord>) {
        context.dataStore.edit { prefs ->
            val arr = JSONArray().apply {
                records.forEach { put(recordToJson(it)) }
            }
            prefs[PENDING_KEY] = arr.toString()
        }
    }

    suspend fun enqueuePending(record: OccurrenceRecord) {
        context.dataStore.edit { prefs ->
            val raw = prefs[PENDING_KEY] ?: "[]"
            val arr = JSONArray(raw)
            arr.put(recordToJson(record))
            prefs[PENDING_KEY] = arr.toString()
        }
    }

    suspend fun removePending(id: String) {
        val current = getPending().filterNot { it.id == id }
        savePending(current)
    }

    // -----------------------------
    // History (sempre visível)
    // -----------------------------
    fun historyFlow(): Flow<List<OccurrenceRecord>> =
        context.dataStore.data.map { prefs ->
            val raw = prefs[HISTORY_KEY] ?: "[]"
            jsonToRecordsList(raw)
        }

    suspend fun getHistory(): List<OccurrenceRecord> = historyFlow().first()

    suspend fun saveHistory(records: List<OccurrenceRecord>) {
        context.dataStore.edit { prefs ->
            val arr = JSONArray().apply {
                records.forEach { put(recordToJson(it)) }
            }
            prefs[HISTORY_KEY] = arr.toString()
        }
    }

    suspend fun addToHistory(record: OccurrenceRecord) {
        val history = getHistory()
        saveHistory(listOf(record) + history) // mais recente primeiro
    }

    suspend fun updateHistoryStatus(id: String, newStatus: ReportStatus) {
        val history = getHistory().map { r ->
            if (r.id == id) r.copy(status = newStatus) else r
        }
        saveHistory(history)
    }

    // -----------------------------
    // JSON helpers
    // -----------------------------
    private fun recordToJson(r: OccurrenceRecord): JSONObject = JSONObject().apply {
        put("id", r.id)
        put("createdAtMillis", r.createdAtMillis)
        put("status", r.status.name)
        put("draft", draftToJson(r.draft))
    }

    private fun jsonToRecord(o: JSONObject): OccurrenceRecord {
        val id = o.optString("id", "")
        val createdAt = o.optLong("createdAtMillis", System.currentTimeMillis())
        val statusName = o.optString("status", ReportStatus.QUEUED.name)
        val status = runCatching { ReportStatus.valueOf(statusName) }.getOrElse { ReportStatus.QUEUED }

        val draftObj = o.optJSONObject("draft")
        val draft = if (draftObj != null) jsonToDraft(draftObj.toString()) else jsonToDraft(o.toString())

        return OccurrenceRecord(
            id = if (id.isBlank()) java.util.UUID.randomUUID().toString() else id,
            createdAtMillis = createdAt,
            status = status,
            draft = draft
        )
    }

    /**
     * Compatível com o antigo formato:
     * - Novo: array de {id, createdAtMillis, status, draft:{...}}
     * - Antigo: array de draft puro (sem id/status)
     */
    private fun jsonToRecordsList(raw: String): List<OccurrenceRecord> {
        val arr = JSONArray(raw)
        return buildList {
            for (i in 0 until arr.length()) {
                val item = arr.get(i)
                when (item) {
                    is JSONObject -> {
                        if (item.has("draft") || item.has("id")) add(jsonToRecord(item))
                        else {
                            // antigo: draft puro
                            val draft = jsonToDraft(item.toString())
                            add(
                                OccurrenceRecord(
                                    id = java.util.UUID.randomUUID().toString(),
                                    createdAtMillis = System.currentTimeMillis(),
                                    status = ReportStatus.QUEUED,
                                    draft = draft
                                )
                            )
                        }
                    }
                    else -> {
                        // fallback: ignora
                    }
                }
            }
        }
    }

    private fun draftToJson(d: OccurrenceDraft): JSONObject = JSONObject().apply {
        put("category", d.category?.name)
        put("title", d.title)
        put("description", d.description)
        put("dateTimeMillis", d.dateTimeMillis)
        put("useCurrentLocation", d.useCurrentLocation)
        put("manualLocationText", d.manualLocationText)
        put("lat", d.lat)
        put("lon", d.lon)
        put("street", d.street)
        put("number", d.number)
        put("district", d.district)
        put("city", d.city)
        put("victimType", d.victimType?.name)
        put("victimGender", d.victimGender.name)
        put("isAnonymous", d.isAnonymous)
        put("acceptedTerms", d.acceptedTerms)

        val attachmentsArr = JSONArray().apply {
            d.attachments.forEach { a ->
                put(JSONObject().apply {
                    put("uri", a.uri)
                    put("type", a.type.name)
                })
            }
        }
        put("attachments", attachmentsArr)
    }

    private fun jsonToDraft(raw: String): OccurrenceDraft {
        val o = JSONObject(raw)

        val attArr = o.optJSONArray("attachments") ?: JSONArray()
        val attachments: List<Attachment> = buildList {
            for (i in 0 until attArr.length()) {
                val item = attArr.getJSONObject(i)
                val uri = item.optString("uri", "")
                val typeName = item.optString("type", "")
                val type = runCatching { AttachmentType.valueOf(typeName) }.getOrNull()
                if (uri.isNotBlank() && type != null) add(Attachment(uri, type))
            }
        }

        val catName = o.optString("category", "")
        val category: OccurrenceCategory? =
            if (catName.isBlank()) null else runCatching { OccurrenceCategory.valueOf(catName) }.getOrNull()
        val victimTypeName = o.optString("victimType", "")
        val victimType: VictimType? =
            if (victimTypeName.isBlank()) null
            else runCatching { VictimType.valueOf(victimTypeName) }.getOrNull()

        val genderName = o.optString("victimGender", Gender.NAO_INFORMADO.name)
        val victimGender: Gender =
            runCatching { Gender.valueOf(genderName) }.getOrElse { Gender.NAO_INFORMADO }


        return OccurrenceDraft(
            category = category,
            title = o.optString("title", ""),
            description = o.optString("description", ""),
            dateTimeMillis = o.optLong("dateTimeMillis", System.currentTimeMillis()),
            useCurrentLocation = o.optBoolean("useCurrentLocation", true),
            manualLocationText = o.optString("manualLocationText", ""),
            lat = if (o.isNull("lat")) null else o.optDouble("lat"),
            lon = if (o.isNull("lon")) null else o.optDouble("lon"),
            street = o.optString("street", ""),
            number = o.optString("number", ""),
            district = o.optString("district", ""),
            city = o.optString("city", ""),
            victimType = victimType,
            victimGender = victimGender,
            isAnonymous = o.optBoolean("isAnonymous", true),
            acceptedTerms = o.optBoolean("acceptedTerms", false),
            attachments = attachments
        )
    }
}
