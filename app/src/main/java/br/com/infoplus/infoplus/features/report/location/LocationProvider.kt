package br.com.infoplus.infoplus.features.report.location

import br.com.infoplus.infoplus.core.location.LocationTracker
import javax.inject.Inject

class LocationProvider @Inject constructor(
    private val tracker: LocationTracker
) {
    suspend fun getBestLocation(): Pair<Double, Double>? {
        val location = tracker.getCurrentLocation() ?: return null
        return location.latitude to location.longitude
    }
}
