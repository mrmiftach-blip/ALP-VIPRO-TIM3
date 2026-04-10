package com.traffic.traffic_monitoring_backend.model

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users")
data class User(
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	val id: UUID = UUID.randomUUID(),

	@Column(unique = true, nullable = false)
	val email: String,

	@Column(nullable = false)
	val password: String,

	@Column(nullable = false)
	val name: String,

	@Enumerated(EnumType.STRING)
	val role: Role = Role.USER,

	val createdAt: LocalDateTime = LocalDateTime.now(),

	var lastLoginAt: LocalDateTime? = null
) : UserDetails {

	override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
		return mutableListOf(SimpleGrantedAuthority("ROLE_${role.name}"))
	}

	override fun getUsername(): String = email
	override fun getPassword(): String = password
	override fun isAccountNonExpired(): Boolean = true
	override fun isAccountNonLocked(): Boolean = true
	override fun isCredentialsNonExpired(): Boolean = true
	override fun isEnabled(): Boolean = true
}

enum class Role {
	USER, ADMIN
}