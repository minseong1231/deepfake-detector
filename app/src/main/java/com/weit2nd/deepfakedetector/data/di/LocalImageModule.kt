package com.weit2nd.deepfakedetector.data.di

import android.content.Context
import com.weit2nd.deepfakedetector.data.source.localimage.LocalImageDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalImageModule {
    @Singleton
    @Provides
    fun providesLocalImageDataSource(
        @ApplicationContext context: Context,
    ): LocalImageDataSource {
        return LocalImageDataSource(context)
    }
}
