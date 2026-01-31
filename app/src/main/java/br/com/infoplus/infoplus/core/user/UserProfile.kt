package br.com.infoplus.infoplus.core.user

import br.com.infoplus.infoplus.features.report.model.Gender

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val cpf: String = "",
    val gender: Gender = Gender.UNSPECIFIED,
    val createdAt: Long = System.currentTimeMillis()
)

enum class Gender {
    MALE,
    FEMALE,
    NON_BINARY,
    TRANS,
    OTHER,
    UNSPECIFIED
}
