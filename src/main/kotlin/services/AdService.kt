package org.example.services

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.example.models.Ad
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
        println("✅ Объявление успешно снято с продажи (Прчина: $reason)!")
    }
}