package com.github.unsunglei.ezeqra.client.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties("ezeqra.regex")
@Data
public class RegexConfiguration {
	private String lootCall;
	private String chatChannel;
	private String logLine;
}
