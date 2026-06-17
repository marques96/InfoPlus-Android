package br.com.infoplus.infoplus.core.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val prefs: ThemePreferences
) : ViewModel() {

    val themeMode = prefs.themeModeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ThemeMode.AUTO
    )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            prefs.setThemeMode(mode)
        }
    }

    @Composable
    fun isDarkTheme(): Boolean {
        val mode = themeMode.collectAsState().value
        return when (mode) {
            ThemeMode.AUTO -> {
                val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                // Dia: 06:00 às 17:59 | Noite: 18:00 às 05:59
                hour >= 18 || hour < 6
            }
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        }
    }
}
