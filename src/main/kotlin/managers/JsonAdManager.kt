package org.example.managers

import org.example.models.Ad
import org.example.storage.DataStorage

class JsonAdManager : AdManager {
    override fun saveAd(ad: Ad) {
        DataStorage.data.ads.add(ad)
        DataStorage.saveData()
    }

    override fun loadAds(): List<Ad> {
        return DataStorage.data.ads
    }

    override fun findAdById(id: String): Ad? {
        return DataStorage.data.ads.find { it.id == id }
    }
}
