package br.com.infoplus.infoplus.core.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val prefs: ThemePreferences
) : ViewModel() {

    val isDark = prefs.darkThemeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    fun toggle() {
        viewModelScope.launch {
            prefs.setDarkTheme(!isDark.value)
        }
    }

    fun setDark(enabled: Boolean) {
        viewModelScope.launch { prefs.setDarkTheme(enabled) }
    }
}
