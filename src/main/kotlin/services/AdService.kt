package org.example.services

import org.example.managers.AdManager
import org.example.managers.JsonAdManager
import org.example.managers.JsonOwnerManager
import org.example.managers.JsonVehicleManager
import org.example.managers.OwnerManager
import org.example.managers.VehicleManager
import org.example.models.Ad
import org.example.models.Car
import org.example.models.Commercial
import org.example.models.Motorcycle
import java.time.LocalDate

object AdService {
    private val adManager: AdManager = JsonAdManager()
    private val ownerManager: OwnerManager = JsonOwnerManager()
    private val vehicleManager: VehicleManager = JsonVehicleManager()

    fun addAd() {
        val availableVehicles = vehicleManager.loadVehicles()
            .filter { vehicle -> adManager.loadAds().none { it.vehicleId == vehicle.vin } }

        if (availableVehicles.isEmpty()) {
            println("Нет доступных ТС для создания объявления.")
            return
        }

        println("Выберите ТС для объявления:")
        availableVehicles.forEachIndexed { index, vehicle ->
            println("${index + 1}. ${vehicle.brand} ${vehicle.model} (${vehicle.year}) - VIN: ${vehicle.vin}")
        }
        val vehicleChoice = readVehicleChoice(availableVehicles.size)
        val selectedVehicle = availableVehicles[vehicleChoice - 1]

        val owners = ownerManager.loadOwners()
        if (owners.isEmpty()) {
            println("Ошибка: Нет зарегистрированных владельцев. Сначала добавьте владельца.")
            return
        }

        println("Выберите владельца для объявления:")
        owners.forEachIndexed { index, owner ->
            println("${index + 1}. Имя: ${owner.name}, телефон ${owner.phone}, email ${owner.email}")
        }
        val ownerChoice = readOwnerChoice(owners.size)
        val selectedOwner = owners[ownerChoice - 1]

        val price = readPrice()

        val newAd = Ad(
            ownerId = selectedOwner.id,
            vehicleId = selectedVehicle.vin,
            price = price,
            date = LocalDate.now().toString()
        )
        newAd.priceHistory.add(price)
        adManager.saveAd(newAd)
        println("✅ Объявление успешно добавлено!")
    }

    private fun readVehicleChoice(max: Int): Int {
        while (true) {
            println("Введите номер ТС (от 1 до $max):")
            val input = readlnOrNull()?.toIntOrNull()
            if (input != null && input in 1..max) return input
            println("Ошибка: Некорректный выбор ТС!")
        }
    }

    private fun readOwnerChoice(max: Int): Int {
        while (true) {
            println("Введите номер владельца (от 1 до $max):")
            val input = readlnOrNull()?.toIntOrNull()
            if (input != null && input in 1..max) return input
            println("Ошибка: Некорректный выбор владельца!")
        }
    }

    private fun readPrice(): Double {
        while (true) {
            println("Введите цену:")
            val input = readlnOrNull()?.toDoubleOrNull()
            if (input != null && input > 0) return input
            println("Ошибка: Цена должна быть положительным числом!")
        }
    }

    // Удаление объявления
    fun removeAd() {
        val activeAds = adManager.loadAds().filter { it.status == "active" }
        if (activeAds.isEmpty()) {
            println("Нет активных объявлений для снятия.")
            return
        }

        println("Выберите объявление для удаления:")
        activeAds.forEachIndexed { index, ad ->
            val vehicle = vehicleManager.findVehicleByVin(ad.vehicleId)
            val owner = ownerManager.findOwnerById(ad.ownerId)
            println("${index + 1}. ${vehicle?.brand} ${vehicle?.model} - Владелец: ${owner?.name} (${owner?.phone}), VIN: ${ad.vehicleId}, Цена: ${ad.price}")
        }

        val choice = readAdChoice(activeAds.size)
        val selectedAd = activeAds[choice - 1]

        val reason = readRemovalReason()

        selectedAd.status = if (reason == "Продано") "sold" else "removed"
        selectedAd.removalReason = reason
        adManager.saveAd(selectedAd)

        println("✅ Объявление снято с продажи (Причина: $reason)")
    }

    private fun readAdChoice(max: Int): Int {
        while (true) {
            println("Введите номер объявления (от 1 до $max):")
            val input = readlnOrNull()?.toIntOrNull()
            if (input != null && input in 1..max) return input
            println("Ошибка: Некорректный выбор объявления!")
        }
    }

    private fun readRemovalReason(): String {
        while (true) {
            println("Выберите причину снятия объявления:")
            println("1. Продано")
            println("2. Другая причина")
            when (readlnOrNull()?.toIntOrNull()) {
                1 -> return "Продано"
                2 -> return "Другая причина"
                else -> println("Ошибка: Некорректный выбор!")
            }
        }
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

        val filteredAds = adManager.loadAds().filter { ad ->
            ad.status == "active" &&
                    vehicleManager.findVehicleByVin(ad.vehicleId)?.let { vehicle ->
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

        val filteredAds = adManager.loadAds().filter { ad ->
            ad.status == "active" &&
                    vehicleManager.findVehicleByVin(ad.vehicleId)?.color?.lowercase() == color
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
                val filteredAds = adManager.loadAds().filter { ad ->
                    ad.status == "active" &&
                            vehicleManager.findVehicleByVin(ad.vehicleId)?.let { vehicle ->
                                vehicle is Car && (bodyType.isNullOrEmpty() || vehicle.bodyType.lowercase() == bodyType)
                            } ?: false
                }
                printSearchResults(filteredAds)
            }

            2 -> {
                println("Введите тип мотоцикла (кроссовый, спортивный, грантуризмо, или нажмите Enter для всех):")
                val motoType = readlnOrNull()?.trim()?.lowercase()
                val filteredAds = adManager.loadAds().filter { ad ->
                    ad.status == "active" &&
                            vehicleManager.findVehicleByVin(ad.vehicleId)?.let { vehicle ->
                                vehicle is Motorcycle && (motoType.isNullOrEmpty() || vehicle.motoType.lowercase() == motoType)
                            } ?: false
                }
                printSearchResults(filteredAds)
            }

            3 -> {
                println("Введите минимальную грузоподъёмность (в кг, или нажмите Enter для всех):")
                val minCapacity = readlnOrNull()?.toIntOrNull()
                val filteredAds = adManager.loadAds().filter { ad ->
                    ad.status == "active" &&
                            vehicleManager.findVehicleByVin(ad.vehicleId)?.let { vehicle ->
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
        printSearchResults(adManager.loadAds())
    }

    private fun searchAll() {
        val filteredAds = adManager.loadAds().filter { it.status == "active" }
        printSearchResults(filteredAds)
    }

    private fun searchByVIN() {
        println("Введите VIN ТС:")
        val vin = readlnOrNull()?.trim()?.uppercase() ?: return

        val filteredAds = adManager.loadAds().filter { ad ->
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
                val vehicle = vehicleManager.findVehicleByVin(ad.vehicleId)
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
        val activeAds = adManager.loadAds().filter { it.status == "active" }
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
        val activeAds = adManager.loadAds().filter { it.status == "active" }
        if (activeAds.isEmpty()) {
            println("Нет активных объявлений.")
            return
        }

        println("Выберите объявление для изменения цены:")
        activeAds.forEachIndexed { index, ad ->
            val vehicle = vehicleManager.findVehicleByVin(ad.vehicleId)
            if (vehicle == null) {
                println("Ошибка: ТС с VIN ${ad.vehicleId} не найдено!")
                return
            }
            println("${index + 1}. ${vehicle.brand} ${vehicle.model} - Текущая цена: ${ad.price} - VIN: ${ad.vehicleId}")
        }

        val choice = readAdChoice(activeAds.size)
        val ad = activeAds[choice - 1]

        val newPrice = readPrice()

        println("Текущая цена: ${ad.price}. Вы уверены, что хотите изменить на $newPrice? (да/нет)")
        val confirm = readConfirmation()
        if (confirm != "да") {
            println("Изменение отменено.")
            return
        }

        ad.priceHistory.add(ad.price)
        ad.price = newPrice
        adManager.saveAd(ad)
        println("✅ Цена объявления обновлена!")
    }

    // История цены
    fun showPriceHistory() {
        val activeAds = adManager.loadAds().filter { it.status == "active" }
        if (activeAds.isEmpty()) {
            println("Нет активных объявлений для просмотра истории цен.")
            return
        }

        activeAds.forEachIndexed { index, ad ->
            val vehicle = vehicleManager.findVehicleByVin(ad.vehicleId)
            if (vehicle != null) {
                println("${index + 1}. ${vehicle.brand} ${vehicle.model} - Текущая цена: ${ad.price} - VIN: ${ad.vehicleId}")
            } else {
                println("Ошибка: ТС с VIN ${ad.vehicleId} не найдено!")
            }
        }

        val choice = readAdChoice(activeAds.size)
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

    private fun readConfirmation(): String {
        while (true) {
            println("Введите 'да' или 'нет':")
            val input = readlnOrNull()?.trim()?.lowercase()
            if (input == "да" || input == "нет") return input
            println("Ошибка: Введите только 'да' или 'нет'!")
        }
    }
}
