package org.example.services

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.models.*
import java.io.File

object VehicleService {
    private const val VEHICLES_FILE = "vehicles.json"

    fun addVehicle() {
        println("Введите VIN (17 символов):")
        val vin = readlnOrNull()?.trim().orEmpty()

//        if (vin.length != 17) {
//            println("Ошибка: VIN должен содержать 17 символов!")
//            return
//        }

        val vehicles = loadVehicles()
        if (vehicles.any { it.vin == vin }) {
            println("Ошибка: ТС с таким VIN уже существует!")
            return
        }

        println("Введите марку:")
        val brand = readlnOrNull()?.trim().orEmpty()

        println("Введите модель:")
        val model = readlnOrNull()?.trim().orEmpty()

        println("Введите год выпуска:")
        val year = readlnOrNull()?.toIntOrNull() ?: run {
            println("Ошибка: Год выпуска должен быть числом!")
            return
        }

        println("Введите цвет:")
        val color = readlnOrNull()?.trim().orEmpty()

        println("Введите пробег:")
        val mileage = readlnOrNull()?.toIntOrNull() ?: run {
            println("Ошибка: Пробег должен быть числом!")
            return
        }

        println("Выберите тип ТС:\n1. Авто\n2. Мото\n3. Коммерческий транспорт")
        val type = readlnOrNull()?.toIntOrNull()

        val vehicle: Vehicle = when (type) {
            1 -> {
                println("Введите тип кузова (седан, хэтчбэк, универсал):")
                val bodyType = readlnOrNull()?.trim().orEmpty()
                Car(vin, brand, model, year, color, mileage, bodyType)
            }

            2 -> {
                println("Введите тип мотоцикла (кроссовый, спортивный, грантуризмо):")
                val motoType = readlnOrNull()?.trim().orEmpty()
                Motorcycle(vin, brand, model, year, color, mileage, motoType)
            }

            3 -> {
                println("Введите грузоподъемность (в кг):")
                val capacity = readlnOrNull()?.toIntOrNull() ?: run {
                    println("Ошибка: Грузоподъемность должна быть числом!")
                    return
                }
                Commercial(vin, brand, model, year, color, mileage, capacity)
            }

            else -> {
                println("Ошибка: Неверный тип ТС!")
                return
            }
        }

        val updatedVehicles = vehicles.toMutableList().apply { add(vehicle) }
        saveVehicles(updatedVehicles)

        println("✅ Транспортное средство добавлено успешно!")
    }

    private fun loadVehicles(): List<Vehicle> {
        val file = File(VEHICLES_FILE)
        if (!file.exists()) return emptyList()
        return Json.decodeFromString(file.readText())
    }

    fun getVehicleById(vin: String): Vehicle? {
        return loadVehicles().find { it.vin == vin }
    }

    private fun saveVehicles(vehicles: List<Vehicle>) {
        File(VEHICLES_FILE).writeText(Json.encodeToString(vehicles))
    }
}
