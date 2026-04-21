package br.com.infoplus.infoplus.core.di

import br.com.infoplus.infoplus.core.sensors.OrientationProvider
import br.com.infoplus.infoplus.core.sensors.SensorOrientationProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SensorModule {

    @Binds
    @Singleton
    abstract fun bindOrientationProvider(
        impl: SensorOrientationProvider
    ): OrientationProvider
}