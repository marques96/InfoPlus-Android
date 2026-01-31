package br.com.infoplus.infoplus.features.report.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationProvider(context: Context) {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getBestLocation(): Pair<Double, Double>? =
        suspendCancellableCoroutine { cont ->
            client.lastLocation
                .addOnSuccessListener { loc ->
                    if (loc != null) {
                        cont.resume(loc.latitude to loc.longitude)
                        return@addOnSuccessListener
                    }

                    // Fallback: pega localização atual
                    client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { cur ->
                            cont.resume(cur?.let { it.latitude to it.longitude })
                        }
                        .addOnFailureListener { cont.resume(null) }
                }
                .addOnFailureListener {
                    // tenta currentLocation mesmo assim
                    client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { cur ->
                            cont.resume(cur?.let { it.latitude to it.longitude })
                        }
                        .addOnFailureListener { cont.resume(null) }
                }
        }
}
