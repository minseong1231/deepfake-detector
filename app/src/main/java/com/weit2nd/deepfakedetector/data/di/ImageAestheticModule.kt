package com.weit2nd.deepfakedetector.data.di

import android.content.Context
import com.weit2nd.deepfakedetector.data.repository.deepfake.DeepFakeDetectorRepository
import com.weit2nd.deepfakedetector.data.repository.deepfake.DeepFakeDetectorRepositoryImpl
import com.weit2nd.deepfakedetector.data.repository.imageaesthetic.ImageAestheticRepository
import com.weit2nd.deepfakedetector.data.repository.imageaesthetic.ImageAestheticRepositoryImpl
import com.weit2nd.deepfakedetector.data.source.model.OnnxDataSource
import com.weit2nd.deepfakedetector.data.source.localimage.LocalImageDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageAestheticModule {
    @Singleton
    @Provides
    fun provideImageAestheticRepository(
        onnxDataSource: OnnxDataSource,
        localImageDataSource: LocalImageDataSource,
    ): ImageAestheticRepository {
        return ImageAestheticRepositoryImpl(
            onnxDataSource = onnxDataSource,
            localImageDataSource = localImageDataSource,
        )
    }
}
