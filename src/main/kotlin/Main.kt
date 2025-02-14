package org.example

fun main() {
    while (true) {
        println("\nВыберите действие:")
        println("1. Добавить новое ТС")
        println("2. Показать в базу нового владельца")
        println("3. Добавить объявление")
        println("4. Снять объявление")
        println("5. Поиск по объявлениям")
        println("6. Выйти")

        when (readlnOrNull()?.trim()) {
            "1" -> println("Добавить новое транспортное средство (в разработке)...")
            "2" -> println("Показать в базу нового владельца (в разработке)...")
            "3" -> println("Добавить объявление (в разработке)...")
            "4" -> println("Снять объявление (в разработке)...")
            "5" -> println("Поиск по объявлениям (в разработке)...")
            "6" -> {
                println("Выход из программы...")
                return
            }
            else -> println("Некорректный ввод, попробуйте еще раз")
        }
    }
}