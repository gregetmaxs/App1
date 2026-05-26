package com.kasirku.core.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    // Database, Firebase, and Repository providers will be added in Langkah 2
}
