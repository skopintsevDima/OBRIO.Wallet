package ua.obrio.common.presentation.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.obrio.common.presentation.ui.resources.provider.DefaultResourceProvider
import ua.obrio.common.presentation.ui.resources.provider.ResourceProvider

@Module
@InstallIn(SingletonComponent::class)
class ResourcesModule {
    @Provides
    fun provideResourceProvider(@ApplicationContext context: Context): ResourceProvider =
        DefaultResourceProvider(context)
}