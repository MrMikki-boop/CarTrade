package org.example.services

import kotlinx.serialization.json.Json
import org.example.models.Owner
import java.io.File
import java.util.UUID

object OwnerService {
    private const val OWNERS_FILE = "owners.json"

    fun addOwner() {
        println("Введите имя владельца:")
        val name = readlnOrNull()?.trim().orEmpty()
        if (name.isEmpty()) {
            println("Ошибка: Имя владельца не может быть пустым!")
            return
        }

        println("Введите телефон владельца:")
        val phone = readlnOrNull()?.trim().orEmpty()
        if (!phone.matches(Regex("^\\+7\\d{10}\$"))) {
            println("Ошибка: Некорректный номер телефона!")
            return
        }


        println("Введите email владельца:")
        val email = readlnOrNull()?.trim().orEmpty()
        if (!email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"))) {
            println("Ошибка: Некорректный email!")
            return
        }

        val owner = Owner(UUID.randomUUID().toString(), name, phone, email)
        val owners = loadOwners().toMutableList()
        owners.add(owner)

        saveOwners(owners)

        println("✅ Владелец успешно добавлен!")
    }

    private fun loadOwners(): List<Owner> {
        val file = File(OWNERS_FILE)
        if (!file.exists()) return emptyList()
        return Json.decodeFromString(file.readText())
    }

    private fun saveOwners(owners: List<Owner>) {
        File(OWNERS_FILE).writeText(Json.encodeToString(owners))
    }
}