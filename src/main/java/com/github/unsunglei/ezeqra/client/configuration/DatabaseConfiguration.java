package com.github.unsunglei.ezeqra.client.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties("ezeqra.database")
@Data
public class DatabaseConfiguration {
	private String roster;
	private String lootCall;
}
