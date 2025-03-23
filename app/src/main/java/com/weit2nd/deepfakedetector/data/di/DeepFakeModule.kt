package com.weit2nd.deepfakedetector.data.di

import android.content.Context
import com.weit2nd.deepfakedetector.data.repository.deepfake.DeepFakeDetectorRepository
import com.weit2nd.deepfakedetector.data.repository.deepfake.DeepFakeDetectorRepositoryImpl
import com.weit2nd.deepfakedetector.data.source.deepfake.DeepFakeDetectorDataSource
import com.weit2nd.deepfakedetector.data.source.localimage.LocalImageDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DeepFakeModule {
    @Singleton
    @Provides
    fun provideDeepFakeDetectorRepository(
        deepFakeDetectorDataSource: DeepFakeDetectorDataSource,
        localImageDataSource: LocalImageDataSource,
    ): DeepFakeDetectorRepository {
        return DeepFakeDetectorRepositoryImpl(
            deepFakeDetectorDataSource = deepFakeDetectorDataSource,
            localImageDataSource = localImageDataSource,
        )
    }

    @Singleton
    @Provides
    fun provideDeepFakeDetectorDataSource(
        @ApplicationContext context: Context,
    ): DeepFakeDetectorDataSource {
        return DeepFakeDetectorDataSource(context)
    }
}
