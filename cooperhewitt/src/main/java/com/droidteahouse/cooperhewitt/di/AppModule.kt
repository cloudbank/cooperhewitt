/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.droidteahouse.cooperhewitt.di

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import com.droidteahouse.cooperhewitt.api.ArtAPI
import com.droidteahouse.cooperhewitt.db.ArtDao
import com.droidteahouse.cooperhewitt.db.ArtDb
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module(includes = arrayOf(ViewModelModule::class))
class AppModule {
    @Provides
    @Singleton
    internal fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    internal fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("ArtPrefs", Context.MODE_PRIVATE)
    }


    @Reusable
    @Provides
    internal fun provideArtService(): ArtAPI {
        val okHttpClient = OkHttpClient.Builder()
                // .addInterceptor(logger)
                // .addNetworkInterceptor(HeaderInterceptor())
                //  .authenticator(TokenAuthenticator())
                .build()
        return Retrofit.Builder()
                .baseUrl("https://api.collection.cooperhewitt.org/rest/")
                .client(okHttpClient)
                //@todo add back
                // .addConverterFactory(GsonConverterFactory.create(GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ArtAPI::class.java)
    }

    //this is an expensive operation so, we would want a singleton object.
    @Singleton
    @Provides
    internal fun provideDb(application: Application): ArtDb {
        return Room.databaseBuilder(application, ArtDb::class.java, "artsy.db").build()
    }


    @Provides
    @Reusable
    internal fun provideIOExecutor(): Executor {
        return Executors.newSingleThreadExecutor()
    }


    @Provides
    @Singleton
    internal fun provideDao(db: ArtDb): ArtDao {
        return db.artDao()
    }


}
