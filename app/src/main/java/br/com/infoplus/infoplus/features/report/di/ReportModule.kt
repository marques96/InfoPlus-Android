package br.com.infoplus.infoplus.features.report.di

import android.content.Context
import br.com.infoplus.infoplus.features.report.data.ReportLocalStore
import br.com.infoplus.infoplus.features.report.data.ReportRepository
import br.com.infoplus.infoplus.features.report.location.LocationProvider
import br.com.infoplus.infoplus.features.report.location.ReverseGeocoder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReportModule {

    @Provides
    @Singleton
    fun provideReportLocalStore(@ApplicationContext context: Context): ReportLocalStore =
        ReportLocalStore(context)

    @Provides
    fun provideReportRepository(): ReportRepository =
        ReportRepository()

    @Provides
    @Singleton
    fun provideLocationProvider(@ApplicationContext context: Context): LocationProvider =
        LocationProvider(context)

    @Provides
    @Singleton
    fun provideReverseGeocoder(@ApplicationContext context: Context): ReverseGeocoder =
        ReverseGeocoder(context)

}
