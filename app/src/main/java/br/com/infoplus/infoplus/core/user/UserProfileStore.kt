package br.com.infoplus.infoplus.core.user

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.com.infoplus.infoplus.features.report.model.Gender
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.profileDataStore by preferencesDataStore(name = "user_profile")

@Singleton
class UserProfileStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val NAME_KEY = stringPreferencesKey("profile_name")
    private val GENDER_KEY = stringPreferencesKey("profile_gender")

    fun profileFlow(): Flow<UserProfile> =
        context.profileDataStore.data.map { prefs ->
            val name = prefs[NAME_KEY] ?: ""
            val genderName = prefs[GENDER_KEY] ?: Gender.NAO_INFORMADO.name
            val gender = runCatching { Gender.valueOf(genderName) }.getOrElse { Gender.NAO_INFORMADO }
            UserProfile(name = name, gender = gender)
        }

    suspend fun save(profile: UserProfile) {
        context.profileDataStore.edit { prefs ->
            prefs[NAME_KEY] = profile.name
            prefs[GENDER_KEY] = profile.gender.name
        }
    }

    suspend fun clear() {
        context.profileDataStore.edit { prefs ->
            prefs.remove(NAME_KEY)
            prefs.remove(GENDER_KEY)
        }
    }
}