plugins {
//	alias(libs.plugins.kotlin.jvm)
//	alias(libs.plugins.kotlin.spring)
//	alias(libs.plugins.kotlin.jpa)
//	alias(libs.plugins.spring.boot)
//	alias(libs.plugins.spring.dependency.management)

	val kotlinVersion = "2.1.10" // Use a stable Kotlin version
	kotlin("jvm") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
	kotlin("plugin.jpa") version kotlinVersion
	id("org.springframework.boot") version "3.4.3" // Use a stable Spring Boot version
	id("io.spring.dependency-management") version "1.1.7"

//	val kotlinVersion = "2.3.20"
//	kotlin("jvm") version kotlinVersion
//	kotlin("plugin.spring") version kotlinVersion
//	kotlin("plugin.jpa") version kotlinVersion
//	id("org.springframework.boot") version "4.0.5"
//	id("io.spring.dependency-management") version "1.1.7"
//
//
//	kotlin("jvm") version "2.2.21"
//	kotlin("plugin.spring") version "2.2.21"
//	id("org.springframework.boot") version "4.0.5"
//	id("io.spring.dependency-management") version "1.1.7"
//	kotlin("plugin.jpa") version "2.2.21"
}

group = "com.traffic"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenLocal()
	maven {
		url = uri("https://maven.aliyun.com/repository/public/")
	}
	maven {
		url = uri("https://maven.aliyun.com/repository/spring/")
	}

	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-h2console")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("tools.jackson.module:jackson-module-kotlin")
	runtimeOnly("com.h2database:h2")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
