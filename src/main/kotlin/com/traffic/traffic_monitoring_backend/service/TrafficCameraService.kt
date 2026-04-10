package com.traffic.traffic_monitoring_backend.service

import com.traffic.monitoring.dto.*
import com.traffic.monitoring.model.TrafficCamera
import com.traffic.monitoring.repository.TrafficCameraRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class TrafficCameraService(
	private val trafficCameraRepository: TrafficCameraRepository,
	private val webClient: WebClient
) {

	@Value("\${lta.api.key}")
	private lateinit var apiKey: String

	private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

	suspend fun getTrafficImages(dateTime: LocalDateTime? = null): TrafficImageResponse {
		val response = fetchFromLTA(dateTime)

		val cameras = response.items.flatMap { item ->
			item.cameras.map { camera ->
				CameraInfo(
					cameraId = camera.cameraId,
					imageUrl = camera.image,
					latitude = camera.location.latitude,
					longitude = camera.location.longitude,
					imageSize = ImageSize(
						width = camera.imageMetadata.width,
						height = camera.imageMetadata.height
					),
					capturedAt = LocalDateTime.parse(camera.timestamp, formatter)
				)
			}
		}

		// Save to database for history
		saveCameraData(cameras)

		return TrafficImageResponse(
			timestamp = LocalDateTime.now(),
			cameras = cameras,
			totalCameras = cameras.size
		)
	}

	private suspend fun fetchFromLTA(dateTime: LocalDateTime?): LtaTrafficImageResponse {
		val uriSpec = webClient.get()
			.uri { uriBuilder ->
				var builder = uriBuilder.path("/traffic-images")
				if (dateTime != null) {
					val formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
					builder = builder.queryParam("date_time", formattedDateTime)
				}
				builder.build()
			}
			.header("X-API-Key", apiKey)

		return uriSpec.retrieve().awaitBody()
	}

	private fun saveCameraData(cameras: List<CameraInfo>) {
		cameras.forEach { camera ->
			val entity = TrafficCamera(
				cameraId = camera.cameraId,
				imageUrl = camera.imageUrl,
				latitude = camera.latitude,
				longitude = camera.longitude,
				imageHeight = camera.imageSize.height,
				imageWidth = camera.imageSize.width,
				imageMd5 = "",
				capturedAt = camera.capturedAt
			)
			trafficCameraRepository.save(entity)
		}
	}

	fun getCamerasByLocation(lat: Double, lon: Double, radiusKm: Double = 1.0): List<TrafficCamera> {
		return trafficCameraRepository.findAll().filter { camera ->
			val distance = calculateDistance(lat, lon, camera.latitude, camera.longitude)
			distance <= radiusKm
		}
	}

	private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
		val R = 6371.0
		val dLat = Math.toRadians(lat2 - lat1)
		val dLon = Math.toRadians(lon2 - lon1)
		val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				Math.sin(dLon / 2) * Math.sin(dLon / 2)
		val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
		return R * c
	}

	fun getAllCameras(): List<TrafficCamera> {
		return trafficCameraRepository.findAll()
	}

	fun getCameraById(cameraId: String): TrafficCamera? {
		return trafficCameraRepository.findById(cameraId).orElse(null)
	}

	fun getLatestCameras(): List<TrafficCamera> {
		return trafficCameraRepository.findTop50ByOrderByFetchedAtDesc()
	}
}