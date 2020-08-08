package com.github.unsunglei.ezeqra.client.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties("ezeqra.client")
@Data
public class ClientConfiguration {
	private String eqHome;
	private String server;
	private String player;
	private String logFileFormat;
	private boolean logStartAtEnd;
	private String raidRosterPrefixFormat;
}
