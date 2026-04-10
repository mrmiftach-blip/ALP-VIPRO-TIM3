package com.traffic.traffic_monitoring_backend.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
	@Value("\${jwt.secret}") private val secret: String,
	@Value("\${jwt.access-token-expiration}") private val accessTokenExpiration: Long,
	@Value("\${jwt.refresh-token-expiration}") private val refreshTokenExpiration: Long
) {

	private fun getSigningKey(): SecretKey {
		val keyBytes = Base64.getDecoder().decode(secret)
		return Keys.hmacShaKeyFor(keyBytes)
	}

	fun generateAccessToken(userDetails: UserDetails): String {
		return generateToken(userDetails, accessTokenExpiration)
	}

	fun generateRefreshToken(userDetails: UserDetails): String {
		return generateToken(userDetails, refreshTokenExpiration)
	}

	private fun generateToken(userDetails: UserDetails, expiration: Long): String {
		val claims: MutableMap<String, Any> = HashMap()
		claims["roles"] = userDetails.authorities.joinToString { it.authority }

		return Jwts.builder()
			.setClaims(claims)
			.setSubject(userDetails.username)
			.setIssuedAt(Date(System.currentTimeMillis()))
			.setExpiration(Date(System.currentTimeMillis() + expiration))
			.signWith(getSigningKey())
			.compact()
	}

	fun extractUsername(token: String): String {
		return extractAllClaims(token).subject
	}

	fun extractExpiration(token: String): Date {
		return extractAllClaims(token).expiration
	}

	private fun extractAllClaims(token: String): Claims {
		return Jwts.parserBuilder()
			.setSigningKey(getSigningKey())
			.build()
			.parseClaimsJws(token)
			.body
	}

	fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
		val username = extractUsername(token)
		return (username == userDetails.username) && !isTokenExpired(token)
	}

	private fun isTokenExpired(token: String): Boolean {
		return extractExpiration(token).before(Date())
	}
}