package com.voidstorm.hivemqtls

import com.google.gson.annotations.SerializedName

/**
 * Data class representing the crash alert payload received via MQTT
 * Based on the format in Payload.txt
 */
data class CrashAlert(
    @SerializedName("msgType") val msgType: String = "",
    @SerializedName("crashID") val crashId: Int = 0,
    @SerializedName("acceleration") val acceleration: Float = 0f,
    @SerializedName("tiltAngle") val tiltAngle: Float = 0f,
    @SerializedName("timestamp") val timestamp: String = "",
    @SerializedName("crashType") val crashType: String = "",
    @SerializedName("crashStatus") val crashStatus: String = "",
    @SerializedName("location") val location: Location? = null
)

/**
 * Data class representing a location with latitude and longitude
 */
data class Location(
    @SerializedName("lat") val lat: Double = 0.0,
    @SerializedName("lon") val lon: Double = 0.0
) {
    override fun toString(): String {
        return "Lat: $lat, Lon: $lon"
    }
}