package com.formations.spring_products_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(
			new Info()
				.title("Products API")
				.version("1.0")
				.description(
					"REST API for product management with Nico (Happy Birthday) "
				)
		);
	}
}
