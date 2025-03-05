package org.example.managers

import org.example.models.Owner

interface OwnerManager {
    fun saveOwner(owner: Owner)
    fun loadOwners(): List<Owner>
    fun findOwnerById(id: String): Owner?
}