package com.traffic.traffic_monitoring_backend.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
	@field:NotBlank(message = "Email is required")
	@field:Email(message = "Invalid email format")
	val email: String,

	@field:NotBlank(message = "Password is required")
	val password: String
)

data class RegisterRequest(
	@field:NotBlank(message = "Email is required")
	@field:Email(message = "Invalid email format")
	val email: String,

	@field:NotBlank(message = "Name is required")
	@field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
	val name: String,

	@field:NotBlank(message = "Password is required")
	@field:Size(min = 6, message = "Password must be at least 6 characters")
	val password: String
)

data class LoginResponse(
	val accessToken: String,
	val refreshToken: String,
	val tokenType: String = "Bearer",
	val email: String,
	val name: String
)

data class RefreshTokenRequest(
	val refreshToken: String
)

data class ApiResponse<T>(
	val success: Boolean,
	val message: String,
	val data: T? = null,
	val timestamp: Long = System.currentTimeMillis()
)