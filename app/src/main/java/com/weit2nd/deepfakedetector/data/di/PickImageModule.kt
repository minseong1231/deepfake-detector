package com.weit2nd.deepfakedetector.data.di

import com.weit2nd.deepfakedetector.data.repository.pickimage.PickImageRepository
import com.weit2nd.deepfakedetector.data.repository.pickimage.PickImageRepositoryImpl
import com.weit2nd.deepfakedetector.data.source.pickimage.PickImageDataSource
import com.weit2nd.deepfakedetector.data.util.ActivityProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PickImageModule {
    @Singleton
    @Provides
    fun providesPickImageRepository(dataSource: PickImageDataSource): PickImageRepository {
        return PickImageRepositoryImpl(dataSource)
    }

    @Singleton
    @Provides
    fun providesPickImageDataSource(activityProvider: ActivityProvider): PickImageDataSource {
        return PickImageDataSource(activityProvider)
    }
}
