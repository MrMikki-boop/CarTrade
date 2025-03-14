package org.example.services

import org.example.managers.JsonVehicleManager
import org.example.managers.VehicleManager
import org.example.models.Vehicle
import org.example.models.Motorcycle
import org.example.models.CarBodyType
import org.example.models.Car
import org.example.models.MotoType
import org.example.models.Commercial
import java.time.LocalDate

object VehicleService {
    private val vehicleManager: VehicleManager = JsonVehicleManager()

    fun addVehicle() {
        println("Введите VIN (17 символов):")
        val vin = readlnOrNull()?.trim().orEmpty()
//        val vin = readVin()

        if (vehicleManager.findVehicleByVin(vin) != null) {
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
        vehicleManager.saveVehicle(vehicle)

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
                    println("Выберите тип кузова:")
                    println("1. Седан")
                    println("2. Хэтчбек")
                    println("3. Универсал")
                    val bodyTypeChoice = readlnOrNull()?.toIntOrNull()
                    val bodyType = when (bodyTypeChoice) {
                        1 -> CarBodyType.SEDAN
                        2 -> CarBodyType.HATCHBACK
                        3 -> CarBodyType.UNIVERSAL
                        else -> {
                            println("Ошибка: Неверный выбор. Используется седан по умолчанию.")
                            CarBodyType.SEDAN
                        }
                    }
                    return Car(vin, brand, model, year, color, mileage, bodyType)
                }

                2 -> {
                    println("Выберите тип кузова:")
                    println("1. Кроссовый")
                    println("2. Спортивный")
                    println("3. Грантуризмо")
                    val motoTypeChoice = readlnOrNull()?.toIntOrNull()
                    val motoType = when (motoTypeChoice) {
                        1 -> MotoType.CROSS
                        2 -> MotoType.SPORT
                        3 -> MotoType.GRAN_TURISMO
                        else -> {
                            println("Ошибка: Неверный выбор. Используется кроссовый по умолчанию.")
                            MotoType.CROSS
                        }
                    }
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
}
