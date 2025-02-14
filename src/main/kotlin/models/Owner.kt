package org.example.models

import kotlinx.serialization.Serializable


@Serializable
data class Owner(
    val id: Int,
    val name: String,
    val phone: String,
    val email: String
)
