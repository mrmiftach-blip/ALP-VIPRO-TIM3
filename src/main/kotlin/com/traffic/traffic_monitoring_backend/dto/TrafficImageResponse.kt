package com.traffic.traffic_monitoring_backend.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class LtaTrafficImageResponse(
	@JsonProperty("api_info")
	val apiInfo: ApiInfo,
	val items: List<TrafficItem>
)

data class ApiInfo(
	val status: String
)

data class TrafficItem(
	val timestamp: String,
	val cameras: List<CameraData>
)

data class CameraData(
	@JsonProperty("camera_id")
	val cameraId: String,

	val timestamp: String,

	val image: String,

	@JsonProperty("image_metadata")
	val imageMetadata: ImageMetadata,

	val location: Location
)

data class ImageMetadata(
	val height: Int,
	val width: Int,
	val md5: String
)

data class Location(
	val latitude: Double,
	val longitude: Double
)

data class TrafficImageResponse(
	val timestamp: LocalDateTime,
	val cameras: List<CameraInfo>,
	val totalCameras: Int
)

data class CameraInfo(
	val cameraId: String,
	val imageUrl: String,
	val latitude: Double,
	val longitude: Double,
	val imageSize: ImageSize,
	val capturedAt: LocalDateTime
)

data class ImageSize(
	val width: Int,
	val height: Int
)