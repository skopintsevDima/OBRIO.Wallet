package ua.obrio.common.data.di.source

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.obrio.common.data.memory.MemoryAccountSource
import ua.obrio.common.data.source.AccountSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
    @Provides
    @Singleton
    fun provideUserAccountSource(): AccountSource = MemoryAccountSource()
}