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
        /*val builder = Picasso.Builder(this)
        builder.downloader(OkHttp3Downloader(this, Long.MAX_VALUE))
        val picasso = builder.build()
        picasso.setLoggingEnabled(false)

        try {
            Picasso.setSingletonInstance(picasso)
        } catch (e: IllegalStateException) {
            // for roboelectric tests
        }*/

        //Stetho.initializeWithDefaults(this);
    }
}
