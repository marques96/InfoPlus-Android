package br.com.infoplus.infoplus.core.settings

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

enum class ThemeMode {
    AUTO, LIGHT, DARK
}

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")

    val themeModeFlow: Flow<ThemeMode> =
        context.dataStore.data.map { prefs -> 
            val modeStr = prefs[KEY_THEME_MODE] ?: ThemeMode.AUTO.name
            try {
                ThemeMode.valueOf(modeStr)
            } catch (e: Exception) {
                ThemeMode.AUTO
            }
        }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs -> prefs[KEY_THEME_MODE] = mode.name }
    }
}
