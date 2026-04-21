package br.com.infoplus.infoplus.core.location

import com.google.android.gms.maps.model.LatLng

data class UserLocationUpdate(
    val latLng: LatLng,
    val bearing: Float = 0f,
    val speedMetersPerSecond: Float = 0f,
    val accuracyMeters: Float? = null
)