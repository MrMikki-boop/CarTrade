package org.example

import org.example.services.AdService
import org.example.services.OwnerService
import org.example.services.VehicleService

fun main() {
    while (true) {
        println("\nВыберите действие:")
        println("1. Добавить новое ТС")
        println("2. Добавить данные владельца")
        println("3. Разместить новое объявление / снять объявление с публикации")
        println("4. Посмотреть историю цены на ТС / изменить цену ТС")
        println("5. Поиск по объявлениям")
        println("6. Выйти")

        when (readlnOrNull()?.trim()) {
            "1" -> VehicleService.addVehicle()
            "2" -> OwnerService.addOwner()
            "3" -> manageAd()
            "4" -> changePrice()
            "5" -> AdService.showAds()
            "6" -> {
                println("Выход из программы...")
                return
            }
            else -> println("Некорректный ввод, попробуйте еще раз")
        }
    }
}

fun manageAd() {
    println("\nВыберите действие с объявлениями:")
    println("1. Разместить новое объявление")
    println("2. Снять объявление с публикации")
    println("3. Вернуться назад")

    when (readlnOrNull()?.trim()) {
        "1" -> AdService.addAd()
        "2" -> AdService.removeAd()
        "3" -> return
        else -> {
            println("Некорректный ввод, попробуйте еще раз")
            manageAd()
        }
    }
}

fun changePrice() {
    println("\nВыберите действие с объявлениями:")
    println("1. Изменить цену")
    println("2. Показать историю цен")
    println("3. Вернуться назад")

    when (readlnOrNull()?.trim()) {
        "1" -> AdService.changeAdPrice()
        "2" -> AdService.showPriceHistory()
        "3" -> return
        else -> {
            println("Некорректный ввод, попробуйте еще раз")
            changePrice()
        }
    }
}
