package com.traffic.traffic_monitoring_backend.config

import com.traffic.monitoring.security.JwtAuthenticationFilter
import com.traffic.monitoring.service.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
	private val userDetailsService: CustomUserDetailsService,
	private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

	@Bean
	fun passwordEncoder(): PasswordEncoder {
		return BCryptPasswordEncoder()
	}

	@Bean
	fun authenticationProvider(): DaoAuthenticationProvider {
		return DaoAuthenticationProvider().apply {
			setUserDetailsService(userDetailsService)
			setPasswordEncoder(passwordEncoder())
		}
	}

	@Bean
	fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
		return authConfig.authenticationManager
	}

	@Bean
	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
		http
			.csrf { it.disable() }
			.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
			.authorizeHttpRequests { auth ->
				auth
					.requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
					.anyRequest().authenticated()
			}
			.headers { headers ->
				headers.frameOptions { frameOptions -> frameOptions.disable() }
			}
			.authenticationProvider(authenticationProvider())
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

		return http.build()
	}
}