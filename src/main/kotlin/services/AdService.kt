package org.example.services

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.example.models.*
import java.io.File
import java.time.LocalDate

object AdService {
    private const val ADS_FILE = "ads.json"
    private val ads = mutableListOf<Ad>()

    init {
        loadAds()
    }

    private fun loadAds() {
        val file = File(ADS_FILE)
        if (file.exists()) {
            val content = file.readText()
            if (content.isNotEmpty()) {
                ads.addAll(Json.decodeFromString(content))
            }
        }
    }

    private fun saveAds() {
        File(ADS_FILE).writeText(Json.encodeToString(ads))
    }

    fun addAd() {
        println("Введите ID владельца:")
        val ownerId = readlnOrNull()?.trim().orEmpty()
        if (ownerId.isEmpty()) {
            println("Ошибка: ID владельца не может быть пустым!")
            return
        }
        println("Введите VIN ТС:")
        val vehicleId = readlnOrNull()?.trim().orEmpty()
        if (vehicleId.isEmpty()) {
            println("Ошибка: VIN ТС не может быть пустым!")
            return
        }
        println("Введите цену:")
        val price = readlnOrNull()?.toDoubleOrNull()
        if (price == null || price <= 0) {
            println("Ошибка: Цена должна быть числом!")
            return
        }

        val newAd = Ad(
            ownerId = ownerId,
            vehicleId = vehicleId,
            price = price,
            date = LocalDate.now().toString()
        )
        newAd.priceHistory.add(price)
        ads.add(newAd)
        saveAds()
        println("✅ Объявление успешно добавлено!")
    }

    // Удаление объявления
    fun removeAd() {
        if (ads.isEmpty()) {
            println("Нет активных объявлений для удаления.")
            return
        }

        println("Выберите объявление для удаления:")
        ads.forEachIndexed { index, ad ->
            println("${index + 1}. VIN: ${ad.vehicleId}, цена: ${ad.price}, дата: ${ad.date}")
        }

        val choice = readlnOrNull()?.toIntOrNull()
        if (choice == null || choice !in 1..ads.size) {
            println("Ошибка: Некорректный выбор.")
            return
        }

        println("Выберите причину снятия объявления:")
        println("1. Продано")
        println("2. Другая причина")
        val reason = when (readlnOrNull()?.toIntOrNull()) {
            1 -> "Продано"
            2 -> "Другая причина"
            else -> {
                println("Ошибка: Некорректный выбор.")
                return
            }
        }

        val removedAd = ads.removeAt(choice - 1)
        saveAds()

        println("✅ Объявление снято с продажи (Причина: $reason)")
    }

    // поиск объявлений по разным параметрам
    private fun searchByPriceAndMileage() {
        println("Введите минимальную цену (или нажмите Enter, чтобы пропустить):")
        val minPrice = readlnOrNull()?.toDoubleOrNull()

        println("Введите максимальную цену (или нажмите Enter, чтобы пропустить):")
        val maxPrice = readlnOrNull()?.toDoubleOrNull()

        println("Введите минимальный пробег (или нажмите Enter, чтобы пропустить):")
        val minMileage = readlnOrNull()?.toIntOrNull()

        println("Введите максимальный пробег (или нажмите Enter, чтобы пропустить):")
        val maxMileage = readlnOrNull()?.toIntOrNull()

        val filteredAds = ads.filter { ad ->
            val vehicle = VehicleService.getVehicleById(ad.vehicleId)
            vehicle != null &&
                    (minPrice == null || ad.price >= minPrice) &&
                    (maxPrice == null || ad.price <= maxPrice) &&
                    (minMileage == null || vehicle.mileage >= minMileage) &&
                    (maxMileage == null || vehicle.mileage <= maxMileage)
        }

        printSearchResults(filteredAds)
    }

    private fun searchByColor() {
        println("Введите цвет ТС:")
        val color = readlnOrNull()?.trim()?.lowercase() ?: return

        val filteredAds = ads.filter { ad ->
            val vehicle = VehicleService.getVehicleById(ad.vehicleId)
            vehicle != null && vehicle.color.lowercase() == color
        }

        printSearchResults(filteredAds)
    }

    private fun searchByType() {
        println("Выберите тип ТС:")
        println("1. Авто")
        println("2. Мото")
        println("3. Коммерческий")
        val typeChoice = readlnOrNull()?.toIntOrNull()

        val filteredAds = ads.filter { ad ->
            val vehicle = VehicleService.getVehicleById(ad.vehicleId)
            when (typeChoice) {
                1 -> vehicle is Car
                2 -> vehicle is Motorcycle
                3 -> vehicle is Commercial
                else -> false
            }
        }

        printSearchResults(filteredAds)
    }

    private fun showAllAds() {
        printSearchResults(ads)
    }

    private fun searchByVIN() {
        println("Введите VIN ТС:")
        val vin = readlnOrNull()?.trim()?.uppercase() ?: return

        val filteredAds = ads.filter { it.vehicleId.uppercase() == vin }
        printSearchResults(filteredAds)
    }

    private fun printSearchResults(results: List<Ad>) {
        if (results.isEmpty()) {
            println("🔍 По вашему запросу ничего не найдено.")
        } else {
            println("🔍 Найдено ${results.size} объявлений:")
            results.forEach { ad ->
                val vehicle = VehicleService.getVehicleById(ad.vehicleId)
                if (vehicle != null) {
                    println("📌 ${vehicle.brand} ${vehicle.model}, ${vehicle.year}г., Цвет: ${vehicle.color}, Пробег: ${vehicle.mileage} км, Цена: ${ad.price} руб.")
                }
            }
        }
    }

    fun showAds() {
        if (ads.isEmpty()) {
            println("🔍 Нет активных объявлений для поиска.")
            return
        }

        while (true) {
            println("\nВыберите действие:")
            println("1. Поиск по цене и пробегу")
            println("2. Поиск по цвету")
            println("3. Поиск по типу ТС")
            println("4. Общий поиск (все объявления)")
            println("5. Поиск по VIN")
            println("6. Вернуться назад")

            when (readlnOrNull()?.trim()) {
                "1" -> searchByPriceAndMileage()
                "2" -> searchByColor()
                "3" -> searchByType()
                "4" -> showAllAds()
                "5" -> searchByVIN()
                "6" -> return
                else -> println("Ошибка: Некорректный выбор!")
            }
        }
    }

}