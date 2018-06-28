package com.droidteahouse.cooperhewitt

import com.droidteahouse.cooperhewitt.di.DaggerAppComponent

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

public class ArtApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }


    override fun onCreate() {
        super.onCreate()
        //insert into db in background with hash

    }
}
