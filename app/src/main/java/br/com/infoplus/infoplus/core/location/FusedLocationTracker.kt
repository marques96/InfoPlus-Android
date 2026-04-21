package br.com.infoplus.infoplus.core.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine

@Singleton
class FusedLocationTracker @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationTracker {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LatLng? =
        suspendCancellableCoroutine { continuation ->
            client.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(LatLng(location.latitude, location.longitude))
                        return@addOnSuccessListener
                    }

                    client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { current ->
                            continuation.resume(
                                current?.let { LatLng(it.latitude, it.longitude) }
                            )
                        }
                        .addOnFailureListener {
                            continuation.resume(null)
                        }
                }
                .addOnFailureListener {
                    client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { current ->
                            continuation.resume(
                                current?.let { LatLng(it.latitude, it.longitude) }
                            )
                        }
                        .addOnFailureListener {
                            continuation.resume(null)
                        }
                }
        }

    @SuppressLint("MissingPermission")
    override fun locationUpdates(intervalMillis: Long): Flow<LatLng> =
        locationStateUpdates(intervalMillis = intervalMillis).callbackMapToLatLng()

    @SuppressLint("MissingPermission")
    override fun locationStateUpdates(intervalMillis: Long): Flow<UserLocationUpdate> = callbackFlow {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            intervalMillis
        )
            .setMinUpdateIntervalMillis(1_000L)
            .setWaitForAccurateLocation(true)
            .build()

        var lastBearing = 0f
        var lastLatLng: LatLng? = null

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                val latLng = LatLng(location.latitude, location.longitude)

                val computedBearing = lastLatLng?.let { previous ->
                    val distance = distanceBetween(previous, latLng)
                    if (distance >= 3f) calculateBearing(previous, latLng) else null
                }

                val resolvedBearing = when {
                    location.hasBearing() && location.speed > 2f -> {
                        location.bearing.normalizeBearing()
                    }
                    computedBearing != null -> {
                        computedBearing.normalizeBearing()
                    }
                    else -> {
                        lastBearing
                    }
                }

                lastBearing = resolvedBearing
                lastLatLng = latLng

                trySend(
                    UserLocationUpdate(
                        latLng = latLng,
                        bearing = resolvedBearing,
                        speedMetersPerSecond = location.speed,
                        accuracyMeters = if (location.hasAccuracy()) location.accuracy else null
                    )
                )
            }
        }

        client.requestLocationUpdates(request, callback, context.mainLooper)

        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }
}

private fun Float.normalizeBearing(): Float {
    var value = this % 360f
    if (value < 0f) value += 360f
    return value
}

private fun Flow<UserLocationUpdate>.callbackMapToLatLng(): Flow<LatLng> =
    map { update -> update.latLng }

private fun distanceBetween(a: LatLng, b: LatLng): Float {
    val result = FloatArray(1)
    android.location.Location.distanceBetween(
        a.latitude, a.longitude,
        b.latitude, b.longitude,
        result
    )
    return result[0]
}

private fun calculateBearing(from: LatLng, to: LatLng): Float {
    val startLat = Math.toRadians(from.latitude)
    val startLng = Math.toRadians(from.longitude)
    val endLat = Math.toRadians(to.latitude)
    val endLng = Math.toRadians(to.longitude)

    val dLng = endLng - startLng

    val y = kotlin.math.sin(dLng) * kotlin.math.cos(endLat)
    val x = kotlin.math.cos(startLat) * kotlin.math.sin(endLat) -
            kotlin.math.sin(startLat) * kotlin.math.cos(endLat) * kotlin.math.cos(dLng)

    val bearing = Math.toDegrees(kotlin.math.atan2(y, x)).toFloat()
    return (bearing + 360f) % 360f
}