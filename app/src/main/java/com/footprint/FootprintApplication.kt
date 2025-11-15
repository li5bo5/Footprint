package com.footprint

import android.app.Application
import com.footprint.data.local.FootprintDatabase
import com.footprint.data.repository.FootprintRepository

class FootprintApplication : Application() {
    lateinit var repository: FootprintRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = FootprintDatabase.getInstance(this)
        repository = FootprintRepository(database.footprintDao(), database.travelGoalDao())
    }
}
