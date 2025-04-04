package com.weit2nd.deepfakedetector.data.di

import com.weit2nd.deepfakedetector.data.repository.emotion.EmotionRepository
import com.weit2nd.deepfakedetector.data.repository.emotion.EmotionRepositoryImpl
import com.weit2nd.deepfakedetector.data.source.model.OnnxDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EmotionModule {
    @Singleton
    @Provides
    fun provideEmotionRepository(
        onnxDataSource: OnnxDataSource,
    ): EmotionRepository {
        return EmotionRepositoryImpl(
            onnxDataSource = onnxDataSource,
        )
    }
}
