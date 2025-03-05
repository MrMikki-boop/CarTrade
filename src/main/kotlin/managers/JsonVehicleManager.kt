package org.example.managers

import org.example.models.Vehicle
import org.example.storage.DataStorage

class JsonVehicleManager : VehicleManager {
    override fun saveVehicle(vehicle: Vehicle) {
        DataStorage.data.vehicles.add(vehicle)
        DataStorage.saveData()
    }

    override fun loadVehicles(): List<Vehicle> {
        return DataStorage.data.vehicles
    }

    override fun findVehicleByVin(vin: String): Vehicle? {
        return DataStorage.data.vehicles.find { it.vin == vin }
    }
}