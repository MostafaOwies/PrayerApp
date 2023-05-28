package com.example.prayerstimesaap.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import javax.inject.Inject

class CompassManager @Inject constructor(private val context: Context) :
    SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private var azimuth: Float = 0f
    private var lastAccelerometerData = FloatArray(3)
    private var lastMagnetometerData = FloatArray(3)
    var onAzimuthChangedListener: ((Float) -> Unit)? = null


    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    fun start() {
        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager?.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, lastAccelerometerData, 0, event.values.size)
        } else if (event.sensor == magnetometer) {
            System.arraycopy(event.values, 0, lastMagnetometerData, 0, event.values.size)
        }

        val rotationMatrix = FloatArray(9)
        val inclinationMatrix = FloatArray(9)
        val success = SensorManager.getRotationMatrix(
            rotationMatrix,
            inclinationMatrix,
            lastAccelerometerData,
            lastMagnetometerData
        )

        if (success) {
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)
            azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
            onAzimuthChangedListener?.invoke(azimuth)
            // Adjust the azimuth value based on your requirements
            // For example, you can add a correction factor or convert it to a compass bearing (0-360 degrees)
            // ...
            // Update your UI with the new azimuth value
            // ...
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Handle accuracy changes if needed
    }
}