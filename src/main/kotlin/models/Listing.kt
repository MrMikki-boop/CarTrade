package org.example.models

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Listing(
    val id: Int,
    val ownerId: Int,
    val vin: String,
    val price: Double,
    val date: String,
    val history: List<Double> = listOf()
)

data class PriceChange( // Возможно реализовать в следующих версиях
    val date: LocalDate,
    val oldPrice: Double,
    val newPrice: Double
)
