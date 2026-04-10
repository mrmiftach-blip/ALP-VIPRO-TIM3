package com.traffic.traffic_monitoring_backend.controller

import com.traffic.monitoring.dto.ApiResponse
import com.traffic.monitoring.dto.TrafficImageResponse
import com.traffic.monitoring.model.TrafficCamera
import com.traffic.monitoring.service.TrafficCameraService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/traffic")
class TrafficCameraController(
	private val trafficCameraService: TrafficCameraService
) {

	@GetMapping("/cameras")
	@PreAuthorize("isAuthenticated()")
	fun getAllCameras(): ResponseEntity<ApiResponse<List<TrafficCamera>>> {
		val cameras = trafficCameraService.getAllCameras()
		return ResponseEntity.ok(
			ApiResponse(
				success = true,
				message = "Cameras retrieved successfully",
				data = cameras
			)
		)
	}

	@GetMapping("/cameras/latest")
	@PreAuthorize("isAuthenticated()")
	fun getLatestCameras(): ResponseEntity<ApiResponse<List<TrafficCamera>>> {
		val cameras = trafficCameraService.getLatestCameras()
		return ResponseEntity.ok(
			ApiResponse(
				success = true,
				message = "Latest cameras retrieved successfully",
				data = cameras
			)
		)
	}

	@GetMapping("/cameras/{cameraId}")
	@PreAuthorize("isAuthenticated()")
	fun getCameraById(@PathVariable cameraId: String): ResponseEntity<ApiResponse<TrafficCamera>> {
		val camera = trafficCameraService.getCameraById(cameraId)
		return if (camera != null) {
			ResponseEntity.ok(
				ApiResponse(
					success = true,
					message = "Camera found",
					data = camera
				)
			)
		} else {
			ResponseEntity.status(404).body(
				ApiResponse(
					success = false,
					message = "Camera not found",
					data = null
				)
			)
		}
	}

	@GetMapping("/images")
	@PreAuthorize("isAuthenticated()")
	suspend fun getTrafficImages(
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
		dateTime: LocalDateTime?
	): ResponseEntity<ApiResponse<TrafficImageResponse>> {
		val response = trafficCameraService.getTrafficImages(dateTime)
		return ResponseEntity.ok(
			ApiResponse(
				success = true,
				message = "Traffic images retrieved successfully",
				data = response
			)
		)
	}

	@GetMapping("/cameras/nearby")
	@PreAuthorize("isAuthenticated()")
	fun getCamerasNearby(
		@RequestParam lat: Double,
		@RequestParam lon: Double,
		@RequestParam(defaultValue = "1.0") radius: Double
	): ResponseEntity<ApiResponse<List<TrafficCamera>>> {
		val cameras = trafficCameraService.getCamerasByLocation(lat, lon, radius)
		return ResponseEntity.ok(
			ApiResponse(
				success = true,
				message = "Nearby cameras retrieved successfully",
				data = cameras
			)
		)
	}
}