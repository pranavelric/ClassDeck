package com.classroom.classdeck.di

import android.app.Application
import android.content.Context
import com.classroom.classdeck.util.NetworkConnection
import com.classroom.classdeck.util.NetworkHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class AppModules {



    @Singleton
    @Provides
    fun providesNetworkHelper(@ApplicationContext context: Context): NetworkHelper {
        return NetworkHelper(context)
    }


    @Singleton
    @Provides
    fun providesNetworkConnection(application: Application): NetworkConnection {
        return NetworkConnection(application)
    }



}