package org.example.services

import org.example.models.Ad
import org.example.models.Car
import org.example.models.Commercial
import org.example.models.Motorcycle
import org.example.storage.DataStorage
import java.time.LocalDate

object AdService {
    private val ads: MutableList<Ad> get() = DataStorage.data.ads

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

        val vehicleChoice = readlnOrNull()?.toIntOrNull()
        if (vehicleChoice == null || vehicleChoice !in 1..availableVehicles.size) {
            println("Ошибка: Некорректный выбор ТС.")
            return addAd()
        }

        val selectedVehicle = availableVehicles[vehicleChoice - 1]

        val owners = OwnerService.loadOwners()
        if (owners.isEmpty()) {
            println("Ошибка: Нет зарегистрированных владельцев. Сначала добавьте владельца.")
            return
        }

        println("Выберите владельца для объявления:")
        owners.forEachIndexed { index, owner ->
            println("${index + 1}. Имя: ${owner.name}, телефон ${owner.phone}, email ${owner.email}")
        }

        val ownerChoice = readlnOrNull()?.toIntOrNull()
        if (ownerChoice == null || ownerChoice !in 1..owners.size) {
            println("Ошибка: Некорректный выбор владельца.")
            return addAd()
        }

        val selectedOwner = owners[ownerChoice - 1]

        println("Введите цену:")
        val price = readlnOrNull()?.toDoubleOrNull()
        if (price == null || price <= 0) {
            println("Ошибка: Цена должна быть положительным числом!")
            return addAd()
        }

        val newAd = Ad(
            ownerId = selectedOwner.id,
            vehicleId = selectedVehicle.vin,
            price = price,
            date = LocalDate.now().toString()
        )
        newAd.priceHistory.add(price)
        ads.add(newAd)
        DataStorage.saveData()
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
        DataStorage.saveData()
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
            ad.status == "active" &&
                    VehicleService.getVehicleById(ad.vehicleId)?.let { vehicle ->
                        (minPrice == null || ad.price >= minPrice) &&
                                (maxPrice == null || ad.price <= maxPrice) &&
                                (minMileage == null || vehicle.mileage >= minMileage) &&
                                (maxMileage == null || vehicle.mileage <= maxMileage)
                    } ?: false
        }
        printSearchResults(filteredAds)
    }

    private fun searchByColor() {
        println("Введите цвет ТС:")
        val color = readlnOrNull()?.trim()?.lowercase() ?: return

        val filteredAds = ads.filter { ad ->
            ad.status == "active" &&
                    VehicleService.getVehicleById(ad.vehicleId)?.color?.lowercase() == color
        }
        printSearchResults(filteredAds)
    }

    private fun searchByType() {
        println("Выберите тип ТС:")
        println("1. Авто")
        println("2. Мото")
        println("3. Коммерческий")
        val typeChoice = readlnOrNull()?.toIntOrNull()

        when (typeChoice) {
            1 -> {
                println("Введите тип кузова (седан, хэтчбэк, универсал, или нажмите Enter для всех):")
                val bodyType = readlnOrNull()?.trim()?.lowercase()
                val filteredAds = ads.filter { ad ->
                    ad.status == "active" &&
                            VehicleService.getVehicleById(ad.vehicleId)?.let { vehicle ->
                                vehicle is Car && (bodyType.isNullOrEmpty() || vehicle.bodyType.lowercase() == bodyType)
                            } ?: false
                }
                printSearchResults(filteredAds)
            }

            2 -> {
                println("Введите тип мотоцикла (кроссовый, спортивный, грантуризмо, или нажмите Enter для всех):")
                val motoType = readlnOrNull()?.trim()?.lowercase()
                val filteredAds = ads.filter { ad ->
                    ad.status == "active" &&
                            VehicleService.getVehicleById(ad.vehicleId)?.let { vehicle ->
                                vehicle is Motorcycle && (motoType.isNullOrEmpty() || vehicle.motoType.lowercase() == motoType)
                            } ?: false
                }
                printSearchResults(filteredAds)
            }

            3 -> {
                println("Введите минимальную грузоподъёмность (в кг, или нажмите Enter для всех):")
                val minCapacity = readlnOrNull()?.toIntOrNull()
                val filteredAds = ads.filter { ad ->
                    ad.status == "active" &&
                            VehicleService.getVehicleById(ad.vehicleId)?.let { vehicle ->
                                vehicle is Commercial && (minCapacity == null || vehicle.capacity >= minCapacity)
                            } ?: false
                }
                printSearchResults(filteredAds)
            }

            else -> {
                println("Ошибка: Некорректный выбор.")
            }
        }
    }

    private fun showAllAds() {
        printSearchResults(ads)
    }

    private fun searchAll() {
        val filteredAds = ads.filter { it.status == "active" }
        printSearchResults(filteredAds)
    }

    private fun searchByVIN() {
        println("Введите VIN ТС:")
        val vin = readlnOrNull()?.trim()?.uppercase() ?: return

        val filteredAds = ads.filter { ad ->
            ad.status == "active" && ad.vehicleId.uppercase() == vin
        }
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
                    val extraInfo = when (vehicle) {
                        is Car -> "Тип кузова: ${vehicle.bodyType}"
                        is Motorcycle -> "Тип мотоцикла: ${vehicle.motoType}"
                        is Commercial -> "Грузоподъёмность: ${vehicle.capacity} кг"
                    }
                    println("📌 ${vehicle.brand} ${vehicle.model}, ${vehicle.year}г., Цвет: ${vehicle.color}, Пробег: ${vehicle.mileage} км, Цена: ${ad.price} руб., $extraInfo")
                }
            }
        }
    }

    // Основной метод поиска
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
            println("5. Общий поиск (все объявления за всё время)")
            println("6. Поиск по VIN")
            println("7. Вернуться назад")

            when (readlnOrNull()?.trim()) {
                "1" -> searchByPriceAndMileage()
                "2" -> searchByColor()
                "3" -> searchByType()
                "4" -> searchAll()
                "5" -> showAllAds()
                "6" -> searchByVIN()
                "7" -> return
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
            println("Ошибка: Некорректный выбор. Попробуйте ещё раз.")
            return changeAdPrice()
        }

        val ad = activeAds[choice - 1]

        println("Введите новую цену:")
        val newPrice = readlnOrNull()?.toDoubleOrNull()
        if (newPrice == null || newPrice <= 0) {
            println("Ошибка: Цена должна быть положительным числом! Попробуйте ещё раз.")
            return changeAdPrice()
        }

        println("Текущая цена: ${ad.price}. Вы уверены, что хотите изменить на $newPrice? (да/нет)")
        val confirm = readlnOrNull()?.trim()?.lowercase()
        if (confirm != "да") {
            println("Изменение отменено.")
            return
        }

        ad.priceHistory.add(ad.price)
        ad.price = newPrice
        DataStorage.saveData()
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
            println("⏳ Цена не менялась с момента создания: ${ad.price} руб.")
        } else {
            ad.priceHistory.forEachIndexed { index, price ->
                println("${index + 1}. $price руб.")
            }
        }
    }
}