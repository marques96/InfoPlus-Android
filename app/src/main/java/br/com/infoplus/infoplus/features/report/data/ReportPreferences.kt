package br.com.infoplus.infoplus.features.report.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.reportPreferencesDataStore by preferencesDataStore(
    name = "report_preferences"
)

@Singleton
class ReportPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val SKIP_REPORT_INTRO = booleanPreferencesKey("skip_report_intro")
    }

    val skipReportIntroFlow: Flow<Boolean> =
        context.reportPreferencesDataStore.data.map { preferences ->
            preferences[Keys.SKIP_REPORT_INTRO] ?: false
        }

    suspend fun setSkipReportIntro(skip: Boolean) {
        context.reportPreferencesDataStore.edit { preferences ->
            preferences[Keys.SKIP_REPORT_INTRO] = skip
        }
    }
}