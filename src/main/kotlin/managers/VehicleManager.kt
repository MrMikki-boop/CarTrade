package org.example.managers

import org.example.models.Vehicle

interface VehicleManager {
    fun saveVehicle(vehicle: Vehicle)
    fun loadVehicles(): List<Vehicle>
    fun findVehicleByVin(vin: String): Vehicle?
}