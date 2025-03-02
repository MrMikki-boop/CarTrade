package org.example.services

import org.example.models.Owner
import org.example.storage.DataStorage
import java.util.UUID

object OwnerService {
    fun addOwner() {
        println("Введите имя владельца:")
        val name = readlnOrNull()?.trim().orEmpty()
        if (name.isEmpty()) {
            println("Ошибка: Имя владельца не может быть пустым!")
            return addOwner()
        }

        println("Введите телефон владельца:")
        val phone = readlnOrNull()?.trim().orEmpty()
        if (!phone.matches(Regex("^\\+7\\d{10}\$"))) {
            println("Ошибка: Некорректный номер телефона!")
            return addOwner()
        }

        println("Введите email владельца:")
        val email = readlnOrNull()?.trim().orEmpty()
        if (!email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"))) {
            println("Ошибка: Некорректный email!")
            return addOwner()
        }

        val owner = Owner(UUID.randomUUID().toString(), name, phone, email)
        DataStorage.data.owners.add(owner)
        DataStorage.saveData()

        println("✅ Владелец успешно добавлен!")
    }

    fun loadOwners(): List<Owner> = DataStorage.data.owners
}
