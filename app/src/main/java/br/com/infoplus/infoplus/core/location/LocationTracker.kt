package br.com.infoplus.infoplus.core.location

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface LocationTracker {
    suspend fun getCurrentLocation(): LatLng?

    fun locationUpdates(intervalMillis: Long = 2_000L): Flow<LatLng>

    fun locationStateUpdates(intervalMillis: Long = 2_000L): Flow<UserLocationUpdate>
}