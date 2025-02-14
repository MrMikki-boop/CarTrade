package org.example

import org.example.models.Owner
import org.example.storage.DataStorage

fun main() {
    println("Запуск программы...")

    DataStorage.data.owners.add(Owner(1, "Иван", "8-800-355-35-35", "ivan@gmail.com"))
    DataStorage.saveData()
    println("Тестовые данные сохранены")

    while (true) {
        println("\nВыберите действие:")
        println("1. Добавить новое ТС")
        println("2. Добавить в базу нового владельца")
        println("3. Добавить объявление")
        println("4. Снять объявление")
        println("5. Поиск по объявлениям")
        println("6. Выйти")

        when (readlnOrNull()?.trim()) {
            "1" -> println("Добавить новое транспортное средство (в разработке)...")
            "2" -> println("Добавить в базу нового владельца (в разработке)...")
            "3" -> println("Добавить объявление (в разработке)...")
            "4" -> println("Снять объявление (в разработке)...")
            "5" -> println("Поиск по объявлениям (в разработке)...")
            "6" -> {
                println("Выход из программы...")
                return
            }
            else -> println("Некорректный ввод, попробуйте еще раз")
        }
    }
}