package org.example.models

import kotlinx.serialization.Serializable


@Serializable
data class Owner(
    val id: String,
    val name: String,
    val phone: String,
    val email: String
)
