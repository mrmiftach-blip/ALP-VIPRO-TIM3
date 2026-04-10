package com.traffic.traffic_monitoring_backend.controller

import com.traffic.monitoring.dto.*
import com.traffic.monitoring.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
	private val authService: AuthService
) {

	@PostMapping("/register")
	fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<ApiResponse<LoginResponse>> {
		val response = authService.register(request)
		return if (response.success) {
			ResponseEntity.ok(response)
		} else {
			ResponseEntity.badRequest().body(response)
		}
	}

	@PostMapping("/login")
	fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<LoginResponse>> {
		val response = authService.login(request)
		return if (response.success) {
			ResponseEntity.ok(response)
		} else {
			ResponseEntity.status(401).body(response)
		}
	}

	@PostMapping("/refresh")
	fun refreshToken(@RequestBody request: RefreshTokenRequest): ResponseEntity<ApiResponse<LoginResponse>> {
		val response = authService.refreshToken(request)
		return if (response.success) {
			ResponseEntity.ok(response)
		} else {
			ResponseEntity.status(401).body(response)
		}
	}
}