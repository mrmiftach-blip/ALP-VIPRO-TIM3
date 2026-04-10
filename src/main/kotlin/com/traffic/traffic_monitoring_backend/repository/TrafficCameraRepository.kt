package com.traffic.traffic_monitoring_backend.repository

import com.traffic.traffic_monitoring_backend.model.TrafficCamera
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TrafficCameraRepository : JpaRepository<TrafficCamera, String> {
	fun findByCameraId(cameraId: String): TrafficCamera?
	fun findByCapturedAtBetween(start: LocalDateTime, end: LocalDateTime): List<TrafficCamera>
	fun findTop50ByOrderByFetchedAtDesc(): List<TrafficCamera>
}