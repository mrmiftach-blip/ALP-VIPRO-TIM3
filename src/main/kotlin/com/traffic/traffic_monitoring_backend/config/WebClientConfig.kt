package com.traffic.traffic_monitoring_backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

	@Bean
	fun webClient(): WebClient {
		return WebClient.builder()
			.baseUrl("https://api.data.gov.sg/v1/transport")
			.codecs { configurer ->
				configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)
			}
			.build()
	}
}