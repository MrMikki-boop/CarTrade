package org.example.storage

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.example.models.*
import java.io.File

@Serializable
data class Data(
    val owners: MutableList<Owner> = mutableListOf(),
    val vehicles: MutableList<Vehicle> = mutableListOf(),
    val listings: MutableList<Listing> = mutableListOf()
)


object DataStorage {
    private val file = File("data.json")
    private val json = Json {
        prettyPrint = true
    }

    var data: Data = loadData()

    private fun loadData(): Data {
        return if (file.exists()) {
            json.decodeFromString(Data.serializer(), file.readText())
        } else {
            Data()
        }
    }

    fun saveData() {
        file.writeText(json.encodeToString(data))
    }
}