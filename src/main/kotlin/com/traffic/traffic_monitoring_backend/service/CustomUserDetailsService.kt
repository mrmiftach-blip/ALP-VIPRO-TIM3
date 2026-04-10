package com.traffic.traffic_monitoring_backend.service

import com.traffic.traffic_monitoring_backend.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
	private val userRepository: UserRepository
) : UserDetailsService {

	override fun loadUserByUsername(username: String): UserDetails {
		return userRepository.findByEmail(username)
			.orElseThrow { UsernameNotFoundException("User not found with email: $username") }
	}
}