package com.razard.search.app.api.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan("com.razard.search.domain")
public class ApplicationConfig {
}
