package org.example.services

import org.example.models.Vehicle
import org.example.models.Car
import org.example.models.Commercial
import org.example.models.Motorcycle
import org.example.storage.DataStorage

object VehicleService {
    fun addVehicle() {
        println("Введите VIN (17 символов):")
        val vin = readlnOrNull()?.trim().orEmpty()
//        if (vin.length != 17) {
//            println("Ошибка: VIN должен содержать 17 символов!")
//            return
//        }

        val vehicles = DataStorage.data.vehicles
        if (vehicles.any { it.vin == vin }) {
            println("Ошибка: ТС с таким VIN уже существует!")
            return addVehicle()
        }

        println("Введите марку:")
        val brand = readlnOrNull()?.trim().orEmpty()

        println("Введите модель:")
        val model = readlnOrNull()?.trim().orEmpty()

        println("Введите год выпуска:")
        val year = readlnOrNull()?.toIntOrNull() ?: run {
            println("Ошибка: Год выпуска должен быть числом!")
            return addVehicle()
        }

        println("Введите цвет:")
        val color = readlnOrNull()?.trim().orEmpty()

        println("Введите пробег:")
        val mileage = readlnOrNull()?.toIntOrNull() ?: run {
            println("Ошибка: Пробег должен быть числом!")
            return addVehicle()
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
                    return addVehicle()
                }
                Commercial(vin, brand, model, year, color, mileage, capacity)
            }

            else -> {
                println("Ошибка: Неверный тип ТС!")
                return addVehicle()
            }
        }

        DataStorage.data.vehicles.add(vehicle)
        DataStorage.saveData()

        println("✅ Транспортное средство добавлено успешно!")
    }

    fun getVehicleById(vin: String): Vehicle? = DataStorage.data.vehicles.find { it.vin == vin }

    fun getAllVehicles(): List<Vehicle> = DataStorage.data.vehicles
}
