package org.example.managers

import org.example.models.Ad

interface AdManager {
    fun saveAd(ad: Ad)
    fun loadAds(): List<Ad>
    fun findAdById(id: String): Ad?
}