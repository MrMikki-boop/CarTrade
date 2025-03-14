package org.example.services

import org.example.managers.JsonOwnerManager
import org.example.managers.OwnerManager
import org.example.models.Owner
import java.util.UUID

object OwnerService {
    private val ownerManager: OwnerManager = JsonOwnerManager()

    fun addOwner() {
        val name = readName()
        val phone = readPhone()
        val email = readEmail()

        val owner = Owner(UUID.randomUUID().toString(), name, phone, email)
        ownerManager.saveOwner(owner)
        println("✅ Владелец успешно добавлен!")
    }

    private fun readName(): String {
        while (true) {
            println("Введите имя владельца:")
            val input = readlnOrNull()?.trim().orEmpty()
            if (input.isNotEmpty()) return input
            println("Ошибка: Имя не может быть пустым!")
        }
    }

    private fun readPhone(): String {
        while (true) {
            println("Введите телефон владельца:")
            val input = readlnOrNull()?.trim().orEmpty()
            if (input.matches(Regex("^\\+7\\d{10}\$"))) return input
            println("Ошибка: Некорректный номер телефона!")
        }
    }

    private fun readEmail(): String {
        while (true) {
            println("Введите email владельца:")
            val input = readlnOrNull()?.trim().orEmpty()
            if (input.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"))) return input
            println("Ошибка: Некорректный email!")
        }
    }
}
