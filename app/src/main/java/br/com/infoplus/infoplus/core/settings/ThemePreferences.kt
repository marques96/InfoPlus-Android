package br.com.infoplus.infoplus.core.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val KEY_DARK = booleanPreferencesKey("dark_theme")

    val darkThemeFlow: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[KEY_DARK] ?: false }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_DARK] = enabled }
    }
}
