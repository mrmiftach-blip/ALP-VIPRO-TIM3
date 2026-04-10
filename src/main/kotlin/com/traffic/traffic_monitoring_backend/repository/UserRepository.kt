package com.traffic.traffic_monitoring_backend.repository

import com.traffic.traffic_monitoring_backend.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
	fun findByEmail(email: String): Optional<User>
	fun existsByEmail(email: String): Boolean
}