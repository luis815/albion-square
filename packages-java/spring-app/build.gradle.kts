plugins {
	java
	id("org.springframework.boot") version "3.1.11"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.hibernate.orm") version "6.2.24.Final"
	id("org.graalvm.buildtools.native") version "0.9.28"
	id("com.github.bjornvester.xjc") version "1.8.1"
	id("com.diffplug.spotless") version "6.25.0"
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-batch")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("io.micrometer:micrometer-registry-prometheus")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.batch:spring-batch-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springframework.boot:spring-boot-starter-json")
	implementation("com.amazonaws:aws-java-sdk-s3:1.12.756")
	implementation("io.github.mojtabaj:c-webp:1.0.2")
	implementation("org.apache.commons:commons-lang3:3.14.0")
	implementation("org.springframework.boot:spring-boot-starter-web:3.3.2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

hibernate {
	enhancement {
		enableAssociationManagement.set(true)
	}
}

tasks.bootBuildImage {
	builder.set("paketobuildpacks/builder-jammy-tiny:latest")
}

xjc {
	defaultPackage.set("com.albion_online_data.ao_bin_dumps")
}

spotless {
	java {
		palantirJavaFormat()
	}
}
