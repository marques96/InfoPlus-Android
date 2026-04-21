package br.com.infoplus.infoplus.core.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Singleton
class SensorOrientationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : OrientationProvider {

    override fun orientationUpdates(): Flow<Float> = callbackFlow {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val sensor =
            sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
                ?: sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        if (sensor == null) {
            close()
            return@callbackFlow
        }

        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)

        var lastAzimuth = 0f

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getOrientation(rotationMatrix, orientationAngles)

                val azimuthRadians = orientationAngles[0]
                val azimuthDegrees = Math.toDegrees(azimuthRadians.toDouble()).toFloat()
                val normalized = normalizeBearing(azimuthDegrees)

                val smoothed = smoothBearing(lastAzimuth, normalized, factor = 0.18f)
                lastAzimuth = smoothed

                trySend(smoothed)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        sensorManager.registerListener(
            listener,
            sensor,
            SensorManager.SENSOR_DELAY_GAME
        )

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}

private fun normalizeBearing(value: Float): Float {
    var result = value % 360f
    if (result < 0f) result += 360f
    return result
}

private fun smoothBearing(current: Float, target: Float, factor: Float): Float {
    val delta = shortestAngleDelta(current, target)
    return normalizeBearing(current + delta * factor)
}

private fun shortestAngleDelta(from: Float, to: Float): Float {
    var delta = (to - from + 540f) % 360f - 180f
    if (abs(delta) < 0.1f) delta = 0f
    return delta
}