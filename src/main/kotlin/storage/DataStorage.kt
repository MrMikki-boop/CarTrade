package org.example.storage

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.example.models.Ad
import org.example.models.Owner
import org.example.models.Vehicle
import java.io.File

@Serializable
data class Data(
    val owners: MutableList<Owner> = mutableListOf(),
    val vehicles: MutableList<Vehicle> = mutableListOf(),
    val ads: MutableList<Ad> = mutableListOf()
)


object DataStorage {
    private val file = File("data.json")
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
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