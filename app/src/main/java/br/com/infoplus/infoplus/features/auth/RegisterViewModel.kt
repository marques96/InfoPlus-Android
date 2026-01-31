package br.com.infoplus.infoplus.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.infoplus.infoplus.core.user.UserProfile
import br.com.infoplus.infoplus.core.user.UserProfileStore
import br.com.infoplus.infoplus.features.report.model.Gender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val name: String = "",
    val gender: Gender = Gender.NAO_INFORMADO,
    val isSaving: Boolean = false,
    val error: String? = null
) {
    val canSave: Boolean
        get() = name.trim().length >= 3 && gender != Gender.NAO_INFORMADO && !isSaving
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val store: UserProfileStore
) : ViewModel() {

    private val _ui = MutableStateFlow(RegisterUiState())
    val ui = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            store.profileFlow().collect { p ->
                _ui.value = _ui.value.copy(name = p.name, gender = p.gender)
            }
        }
    }

    fun setName(v: String) = _ui.value.run { _ui.value = copy(name = v, error = null) }
    fun setGender(v: Gender) = _ui.value.run { _ui.value = copy(gender = v, error = null) }

    fun save(onSaved: () -> Unit) {
        val s = _ui.value
        if (!s.canSave) {
            _ui.value = s.copy(error = "Informe nome (mín. 3) e gênero.")
            return
        }

        viewModelScope.launch {
            _ui.value = _ui.value.copy(isSaving = true, error = null)
            store.save(UserProfile(name = s.name.trim(), gender = s.gender))
            _ui.value = _ui.value.copy(isSaving = false)
            onSaved()
        }
    }
}