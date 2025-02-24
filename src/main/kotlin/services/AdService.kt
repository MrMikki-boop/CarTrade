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
}