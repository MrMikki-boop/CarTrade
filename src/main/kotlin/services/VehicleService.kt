package org.example.services

import org.example.models.Vehicle
import org.example.models.Car
import org.example.models.Commercial
import org.example.models.Motorcycle
import org.example.storage.DataStorage
import java.time.LocalDate

object VehicleService {
    fun addVehicle() {
        println("Введите VIN (17 символов):")
        val vin = readlnOrNull()?.trim().orEmpty()
//        val vin = readVin()

        val vehicles = DataStorage.data.vehicles
        if (vehicles.any { it.vin == vin }) {
            println("Ошибка: ТС с таким VIN уже существует!")
            return addVehicle()
        }

        println("Введите марку:")
        val brand = readlnOrNull()?.trim().orEmpty()

        println("Введите модель:")
        val model = readlnOrNull()?.trim().orEmpty()

        val year = readYear()

        println("Введите цвет:")
        val color = readlnOrNull()?.trim().orEmpty()

        val mileage = readMileage()

        val vehicle = createVehicle(vin, brand, model, year, color, mileage)

        DataStorage.data.vehicles.add(vehicle)
        DataStorage.saveData()

        println("✅ Транспортное средство добавлено успешно!")
    }

    private fun readVin(): String {
        while (true) {
            println("Введите VIN (17 символов):")
            val input = readlnOrNull()?.trim().orEmpty()
            if (input.length == 17) return input
            println("Ошибка: VIN должен содержать 17 символов!")
        }
    }

    private fun readMileage(): Int {
        while (true) {
            println("Введите пробег:")
            val input = readlnOrNull()?.toIntOrNull()
            if (input != null && input >= 0) return input
            println("Ошибка: Пробег должен быть неотрицательным числом!")
        }
    }

    private fun readYear(): Int {
        while (true) {
            println("Введите год выпуска:")
            val input = readlnOrNull()?.toIntOrNull()
            if (input != null && input in 1900..LocalDate.now().year) return input
            println("Ошибка: Год должен быть числом от 1900 до ${LocalDate.now().year}!")
        }
    }

    private fun readCapacity(): Int {
        while (true) {
            println("Введите грузоподъемность (в кг):")
            val input = readlnOrNull()?.toIntOrNull()
            if (input != null && input > 0) return input
            println("Ошибка: Грузоподъёмность должна быть положительным числом!")
        }
    }

    private fun createVehicle(
        vin: String,
        brand: String,
        model: String,
        year: Int,
        color: String,
        mileage: Int
    ): Vehicle {
        while (true) {
            println("Выберите тип ТС:\n1. Авто\n2. Мото\n3. Коммерческий транспорт")
            val type = readlnOrNull()?.toIntOrNull()
            when (type) {
                1 -> {
                    println("Введите тип кузова (седан, хэтчбэк, универсал):")
                    val bodyType = readlnOrNull()?.trim().orEmpty()
                    return Car(vin, brand, model, year, color, mileage, bodyType)
                }

                2 -> {
                    println("Введите тип мотоцикла (кроссовый, спортивный, грантуризмо):")
                    val motoType = readlnOrNull()?.trim().orEmpty()
                    return Motorcycle(vin, brand, model, year, color, mileage, motoType)
                }

                3 -> {
                    val capacity = readCapacity()
                    return Commercial(vin, brand, model, year, color, mileage, capacity)
                }

                else -> println("Ошибка: Неверный тип ТС!")
            }
        }
    }

    fun getVehicleById(vin: String): Vehicle? = DataStorage.data.vehicles.find { it.vin == vin }
    fun getAllVehicles(): List<Vehicle> = DataStorage.data.vehicles
}
