package org.example.managers

import org.example.models.Owner
import org.example.storage.DataStorage

class JsonOwnerManager : OwnerManager {
    override fun saveOwner(owner: Owner) {
        DataStorage.data.owners.add(owner)
        DataStorage.saveData()
    }

    override fun loadOwners(): List<Owner> {
        return DataStorage.data.owners
    }

    override fun findOwnerById(id: String): Owner? {
        return DataStorage.data.owners.find { it.id == id }
    }
}