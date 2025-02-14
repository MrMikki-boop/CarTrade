package org.example.models

import kotlinx.serialization.Serializable


@Serializable
sealed class Vehicle (
    open val vin: String,
    open val brand: String,
    open val model: String,
    open val year: Int,
    open val color: String,
    open val mileage: Int
)

data class Car (
    override val vin: String,
    override val brand: String,
    override val model: String,
    override val year: Int,
    override val color: String,
    override val mileage: Int,
    val type: CarType
) : Vehicle(vin, brand, model, year, color, mileage)

data class Motorcycle (
    override val vin: String,
    override val brand: String,
    override val model: String,
    override val year: Int,
    override val color: String,
    override val mileage: Int,
    val motoType: MotoType
) : Vehicle(vin, brand, model, year, color, mileage)

data class CommercialTransport (
    override val vin: String,
    override val brand: String,
    override val model: String,
    override val year: Int,
    override val color: String,
    override val mileage: Int,
    val loadCapacity: Double
) : Vehicle(vin, brand, model, year, color, mileage)

enum class CarType { SEDAN, COUPE, HATCHBACK }
enum class MotoType { CROSS, SPORT, TOURING }
