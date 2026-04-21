package br.com.infoplus.infoplus.core.sensors

import kotlinx.coroutines.flow.Flow

interface OrientationProvider {
    fun orientationUpdates(): Flow<Float>
}