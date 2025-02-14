package org.example.models

import java.time.LocalDate

data class Listing(
    val id: String,
    val ownerId: String,
    val vehicleVin: String,
    var price: Double,
    val history: MutableList<PriceChange> = mutableListOf(),
    var status: ListingStatus = ListingStatus.ACTIVE,
    val createdAt: LocalDate = LocalDate.now()
)

data class PriceChange(
    val date: LocalDate,
    val oldPrice: Double,
    val newPrice: Double
)

enum class ListingStatus {
    ACTIVE, SOLD, REMOVED
}
