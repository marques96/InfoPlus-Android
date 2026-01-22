package br.com.infoplus.infoplus.features.report.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.com.infoplus.infoplus.features.report.model.OccurrenceCategory
import br.com.infoplus.infoplus.features.report.model.OccurrenceDraft
import br.com.infoplus.infoplus.features.report.model.Attachment
import br.com.infoplus.infoplus.features.report.model.AttachmentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(name = "report_store")

class ReportLocalStore(private val context: Context) {

    private val DRAFT_KEY = stringPreferencesKey("draft_json")
    private val PENDING_KEY = stringPreferencesKey("pending_json")

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

    fun pendingFlow(): Flow<List<OccurrenceDraft>> =
        context.dataStore.data.map { prefs ->
            val raw = prefs[PENDING_KEY] ?: "[]"
            val arr = JSONArray(raw)

            buildList<OccurrenceDraft> {
                for (i in 0 until arr.length()) {
                    add(jsonToDraft(arr.getJSONObject(i).toString()))
                }
            }
        }

    suspend fun addPending(draft: OccurrenceDraft) {
        context.dataStore.edit { prefs ->
            val raw = prefs[PENDING_KEY] ?: "[]"
            val arr = JSONArray(raw)
            arr.put(draftToJson(draft))
            prefs[PENDING_KEY] = arr.toString()
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
            isAnonymous = o.optBoolean("isAnonymous", true),
            acceptedTerms = o.optBoolean("acceptedTerms", false),
            attachments = attachments
        )
    }
}
