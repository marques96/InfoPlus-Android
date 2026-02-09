package br.com.infoplus.infoplus.core.user

import br.com.infoplus.infoplus.features.report.model.Gender

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val cpf: String = "",
    val gender: Gender = Gender.NAO_INFORMADO,
    val createdAt: Long = System.currentTimeMillis()
)