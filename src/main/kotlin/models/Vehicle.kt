package org.example.models

sealed class Vehicle (
    open val vin: String,
    open val brand: String,
    open val model: String,
    open val year: Int,
    open val color: String,
    open val milleage: Int
)

data class Car (
    override val vin: String,
    override val brand: String,
    override val model: String,
    override val year: Int,
    override val color: String,
    override val milleage: Int,
    val type: CarType
): Vehicle(vin, brand, model, year, color, milleage)

data class Motorcycle (
    override val vin: String,
    override val brand: String,
    override val model: String,
    override val year: Int,
    override val color: String,
    override val milleage: Int,
    val type: MotoType
): Vehicle(vin, brand, model, year, color, milleage)

data class CommercialTransport (
    override val vin: String,
    override val brand: String,
    override val model: String,
    override val year: Int,
    override val color: String,
    override val milleage: Int,
    val capability: Double
): Vehicle(vin, brand, model, year, color, milleage)

enum class CarType { SEDAN, COUPE, HATCHBACK }
enum class MotoType { CROSS, SPORT, TOURING }
