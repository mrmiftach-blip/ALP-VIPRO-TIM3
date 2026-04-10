package com.traffic.traffic_monitoring_backend.service

import com.traffic.monitoring.dto.*
import com.traffic.monitoring.model.Role
import com.traffic.monitoring.model.User
import com.traffic.monitoring.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthService(
	private val userRepository: UserRepository,
	private val passwordEncoder: PasswordEncoder,
	private val jwtService: JwtService,
	private val authenticationManager: AuthenticationManager,
	private val userDetailsService: CustomUserDetailsService
) {

	@Transactional
	fun register(request: RegisterRequest): ApiResponse<LoginResponse> {
		if (userRepository.existsByEmail(request.email)) {
			return ApiResponse(
				success = false,
				message = "Email already registered",
				data = null
			)
		}

		val user = User(
			email = request.email,
			password = passwordEncoder.encode(request.password),
			name = request.name,
			role = Role.USER
		)

		userRepository.save(user)

		val userDetails = userDetailsService.loadUserByUsername(user.email)
		val accessToken = jwtService.generateAccessToken(userDetails)
		val refreshToken = jwtService.generateRefreshToken(userDetails)

		return ApiResponse(
			success = true,
			message = "Registration successful",
			data = LoginResponse(
				accessToken = accessToken,
				refreshToken = refreshToken,
				email = user.email,
				name = user.name
			)
		)
	}

	fun login(request: LoginRequest): ApiResponse<LoginResponse> {
		return try {
			val authentication = authenticationManager.authenticate(
				UsernamePasswordAuthenticationToken(request.email, request.password)
			)

			SecurityContextHolder.getContext().authentication = authentication

			val userDetails = userDetailsService.loadUserByUsername(request.email)
			val accessToken = jwtService.generateAccessToken(userDetails)
			val refreshToken = jwtService.generateRefreshToken(userDetails)

			// Update last login
			val user = userRepository.findByEmail(request.email).orElse(null)
			user?.let {
				it.lastLoginAt = LocalDateTime.now()
				userRepository.save(it)
			}

			ApiResponse(
				success = true,
				message = "Login successful",
				data = LoginResponse(
					accessToken = accessToken,
					refreshToken = refreshToken,
					email = userDetails.username,
					name = (userDetails as User).name
				)
			)
		} catch (e: Exception) {
			ApiResponse(
				success = false,
				message = "Invalid email or password",
				data = null
			)
		}
	}

	fun refreshToken(request: RefreshTokenRequest): ApiResponse<LoginResponse> {
		return try {
			val username = jwtService.extractUsername(request.refreshToken)
			val userDetails = userDetailsService.loadUserByUsername(username)

			if (jwtService.isTokenValid(request.refreshToken, userDetails)) {
				val newAccessToken = jwtService.generateAccessToken(userDetails)
				val newRefreshToken = jwtService.generateRefreshToken(userDetails)

				ApiResponse(
					success = true,
					message = "Token refreshed successfully",
					data = LoginResponse(
						accessToken = newAccessToken,
						refreshToken = newRefreshToken,
						email = userDetails.username,
						name = (userDetails as User).name
					)
				)
			} else {
				ApiResponse(
					success = false,
					message = "Invalid refresh token",
					data = null
				)
			}
		} catch (e: Exception) {
			ApiResponse(
				success = false,
				message = "Invalid refresh token: ${e.message}",
				data = null
			)
		}
	}
}