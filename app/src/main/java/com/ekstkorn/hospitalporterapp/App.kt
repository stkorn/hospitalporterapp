package com.ekstkorn.hospitalporterapp

import android.app.Application
import com.ekstkorn.hospitalporterapp.module.appModules
import org.koin.android.ext.android.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(androidContext = this, modules = appModules)
    }

}