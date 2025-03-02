package org.example.models

import kotlinx.serialization.Serializable

@Serializable
sealed class Vehicle {
    abstract val vin: String
    abstract val brand: String
    abstract val model: String
    abstract val year: Int
    abstract val color: String
    abstract val mileage: Int
}

@Serializable
data class Car(
    override val vin: String,
    override val brand: String,
    override val model: String,
    override val year: Int,
    override val color: String,
    override val mileage: Int,
    val bodyType: String
) : Vehicle()

@Serializable
data class Motorcycle(
    override val vin: String,
    override val brand: String,
    override val model: String,
    override val year: Int,
    override val color: String,
    override val mileage: Int,
    val motoType: String
) : Vehicle()

@Serializable
data class Commercial(
    override val vin: String,
    override val brand: String,
    override val model: String,
    override val year: Int,
    override val color: String,
    override val mileage: Int,
    val capacity: Int
) : Vehicle()
