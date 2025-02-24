package org.example

import org.example.services.AdService
import org.example.services.OwnerService
import org.example.services.VehicleService

fun main() {
    while (true) {
        println("\nВыберите действие:")
        println("1. Добавить новое ТС")
        println("2. Добавить в базу нового владельца")
        println("3. Добавить объявление")
        println("4. Снять объявление")
        println("5. Поиск по объявлениям")
        println("6. Выйти")

        when (readlnOrNull()?.trim()) {
            "1" -> VehicleService.addVehicle()
            "2" -> OwnerService.addOwner()
            "3" -> AdService.addAd()
            "4" -> AdService.removeAd()
            "5" -> println("Поиск по объявлениям (в разработке)...")
            "6" -> {
                println("Выход из программы...")
                return
            }
            else -> println("Некорректный ввод, попробуйте еще раз")
        }
    }
}
