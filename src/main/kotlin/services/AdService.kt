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
        val availableVehicles = VehicleService.getAllVehicles()
            .filter { vehicle -> ads.none { it.vehicleId == vehicle.vin } }

        if (availableVehicles.isEmpty()) {
            println("Нет доступных ТС для создания объявления.")
            return
        }

        println("Выберите ТС для объявления:")
        availableVehicles.forEachIndexed { index, vehicle ->
            println("${index + 1}. ${vehicle.brand} ${vehicle.model} (${vehicle.year}) - VIN: ${vehicle.vin}")
        }

        val choice = readlnOrNull()?.toIntOrNull()
        if (choice == null || choice !in 1..availableVehicles.size) {
            println("Ошибка: Некорректный выбор.")
            return
        }

        val selectedVehicle = availableVehicles[choice - 1]

        println("Введите ID владельца:")
        val ownerId = readlnOrNull()?.trim().orEmpty()
        val owners = OwnerService.loadOwners()
        if (owners.none { it.id == ownerId }) {
            println("Ошибка: Владелец с ID $ownerId не найден!")
            return addAd()
        }
        if (ownerId.isEmpty()) {
            println("Ошибка: ID владельца не может быть пустым!")
            return addAd()
        }

        println("Введите цену:")
        val price = readlnOrNull()?.toDoubleOrNull()
        if (price == null || price <= 0) {
            println("Ошибка: Цена должна быть числом!")
            return addAd()
        }

        val newAd = Ad(
            ownerId = ownerId,
            vehicleId = selectedVehicle.vin,
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
        val activeAds = ads.filter { it.status == "active" }
        if (activeAds.isEmpty()) {
            println("Нет активных объявлений для снятия.")
            return
        }

        println("Выберите объявление для удаления:")
        activeAds.forEachIndexed { index, ad ->
            val vehicle = VehicleService.getVehicleById(ad.vehicleId)
            println("${index + 1}. ${vehicle?.brand} ${vehicle?.model} - VIN: ${ad.vehicleId}, Цена: ${ad.price}")
        }

        val choice = readlnOrNull()?.toIntOrNull()
        if (choice == null || choice !in 1..activeAds.size) {
            println("Ошибка: Некорректный выбор.")
            return
        }

        val selectedAd = activeAds[choice - 1]

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

        selectedAd.status = if (reason == "Продано") "sold" else "removed"
        selectedAd.removalReason = reason
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
        println("4. Общий список")
        val typeChoice = readlnOrNull()?.toIntOrNull()

        val filteredAds = ads.filter { ad ->
            val vehicle = VehicleService.getVehicleById(ad.vehicleId)
            when (typeChoice) {
                1 -> vehicle is Car
                2 -> vehicle is Motorcycle
                3 -> vehicle is Commercial
                4 -> true
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
        val activeAds = ads.filter { it.status == "active" }
        if (activeAds.isEmpty()) {
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

    // Изменение цены
    fun changeAdPrice() {
        println("Выберите объявление для изменения цены:")

        val activeAds = ads.filter { it.status == "active" }
        if (activeAds.isEmpty()) {
            println("Нет активных объявлений.")
            return
        }

        activeAds.forEachIndexed { index, ad ->
            val vehicle = VehicleService.getVehicleById(ad.vehicleId)
            if (vehicle == null) {
                println("Ошибка: ТС с VIN ${ad.vehicleId} не найдено!")
                return
            }
            println("${index + 1}. ${vehicle.brand} ${vehicle.model} - Текущая цена: ${ad.price} - VIN: ${ad.vehicleId}")
        }

        val choice = readlnOrNull()?.toIntOrNull()
        if (choice == null || choice !in 1..activeAds.size) {
            println("Ошибка: Некорректный выбор.")
            return changeAdPrice()
        }

        val ad = activeAds[choice - 1]

        println("Введите новую цену:")
        val newPrice = readlnOrNull()?.toDoubleOrNull()
        if (newPrice == null || newPrice <= 0) {
            println("Ошибка: Цена должна быть положительным числом!")
            return changeAdPrice()
        }

        println("Текущая цена: ${ad.price}. Вы уверены, что хотите изменить на $newPrice? (да/нет)")
        val confirm = readlnOrNull()?.trim()?.lowercase()
        if (confirm != "да") {
            println("Изменение отменено.")
            return
        }

        ad.priceHistory.add(newPrice)
        ad.price = newPrice
        saveAds()
        println("✅ Цена объявления обновлена!")
    }

    // История цены
    fun showPriceHistory() {
        val activeAds = ads.filter { it.status == "active" }
        if (activeAds.isEmpty()) {
            println("Нет активных объявлений для просмотра истории цен.")
            return
        }

        activeAds.forEachIndexed { index, ad ->
            val vehicle = VehicleService.getVehicleById(ad.vehicleId)
            if (vehicle != null) {
                println("${index + 1}. ${vehicle.brand} ${vehicle.model} - Текущая цена: ${ad.price} - VIN: ${ad.vehicleId}")
            } else {
                println("Ошибка: ТС с VIN ${ad.vehicleId} не найдено!")
            }
        }

        val choice = readlnOrNull()?.toIntOrNull()
        if (choice == null || choice !in 1..activeAds.size) {
            println("Ошибка: Некорректный выбор. Попробуйте ещё раз.")
            return showPriceHistory()
        }

        val ad = activeAds[choice - 1]

        println("📊 История изменения цен для VIN: ${ad.vehicleId}")
        if (ad.priceHistory.isEmpty()) {
            println("⏳ Изменений цены не было.")
        } else {
            ad.priceHistory.forEachIndexed { index, price ->
                println("${index + 1}. $price руб.")
            }
        }
    }
}