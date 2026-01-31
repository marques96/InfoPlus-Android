package br.com.infoplus.infoplus.features.report.location

import android.content.Context
import android.location.Geocoder
import java.util.Locale

data class ResolvedAddress(
    val full: String = "",
    val street: String = "",
    val number: String = "",
    val district: String = "", // bairro
    val city: String = "",
    val state: String = ""
)

class ReverseGeocoder(private val context: Context) {

    private val geocoder = Geocoder(context, Locale("pt", "BR"))

    fun fromLatLng(lat: Double, lon: Double): ResolvedAddress? {
        return try {
            @Suppress("DEPRECATION")
            val a = geocoder.getFromLocation(lat, lon, 1)?.firstOrNull() ?: return null

            val street = a.thoroughfare.orEmpty()
            val number = a.subThoroughfare.orEmpty()

            var district = a.subLocality.orEmpty()

            val city = a.locality?.takeIf { it.isNotBlank() }
                ?: a.subAdminArea?.takeIf { it.isNotBlank() }
                ?: ""

            val state = a.adminArea.orEmpty()

            val line0 = runCatching { a.getAddressLine(0) }.getOrNull().orEmpty()

            if (district.isBlank() && line0.contains(" - ")) {
                val afterDash = line0.substringAfter(" - ").trim()
                district = afterDash.substringBefore(",").trim()
            }

            val full = buildString {
                if (street.isNotBlank()) append(street)
                if (number.isNotBlank()) {
                    if (isNotEmpty()) append(", ")
                    append(number)
                }
                if (district.isNotBlank()) {
                    if (isNotEmpty()) append(" - ")
                    append(district)
                }
                if (city.isNotBlank()) {
                    if (isNotEmpty()) append(" - ")
                    append(city)
                }
                if (state.isNotBlank()) {
                    if (city.isNotBlank()) append("/").append(state)
                    else {
                        if (isNotEmpty()) append(" - ")
                        append(state)
                    }
                }
            }.ifBlank { line0 }

            ResolvedAddress(
                full = full,
                street = street,
                number = number,
                district = district,
                city = city,
                state = state
            )
        } catch (_: Exception) {
            null
        }
    }
}
