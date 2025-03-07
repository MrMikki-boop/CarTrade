package org.example.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Ad(
    val id: String = UUID.randomUUID().toString(),
    val ownerId: String,
    val vehicleId: String,
    var price: Double,
    val date: String,
    var status: ListingStatus = ListingStatus.ACTIVE, // active, sold, removed
    val priceHistory: MutableList<Double> = mutableListOf(),
    var removalReason: String? = null
)

enum class ListingStatus { ACTIVE, SOLD, REMOVED }