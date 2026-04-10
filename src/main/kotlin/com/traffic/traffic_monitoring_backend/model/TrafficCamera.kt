package com.traffic.traffic_monitoring_backend.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "traffic_cameras")
data class TrafficCamera(
	@Id
	val cameraId: String,

	val imageUrl: String,

	val latitude: Double,

	val longitude: Double,

	val imageHeight: Int,

	val imageWidth: Int,

	val imageMd5: String,

	val capturedAt: LocalDateTime,

	val fetchedAt: LocalDateTime = LocalDateTime.now()
)