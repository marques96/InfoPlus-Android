package br.com.infoplus.infoplus.core.di

import br.com.infoplus.infoplus.core.location.FusedLocationTracker
import br.com.infoplus.infoplus.core.location.LocationTracker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationTracker(
        tracker: FusedLocationTracker
    ): LocationTracker
}
