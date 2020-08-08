package com.github.unsunglei.ezeqra.client.api;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.github.unsunglei.ezeqra.client.database.JsonDatabaseEntry;
import com.github.unsunglei.ezeqra.client.loot.LootCallLogLine;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApiService {
	
	@Value("${ezeqra.api.roster.url}")
	private String rosterApiUrl;
	
	@Value("${ezeqra.api.loot-call.url}")
	private String lootCallApiUrl;
	
	@Value("${ezeqra.api.host}")
	private String apiHost;
	
	@Value("${ezeqra.api.token}")
	private UUID apiToken;

	public ResponseEntity<String> sendLootCall(LootCallLogLine line) throws URISyntaxException, HttpClientErrorException {
		log.info("Sending LootCall: {}", line);
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate = new RestTemplate();
		
		PayloadPost<LootCallLogLine> post = new PayloadPost<>();
		
		post.setPayload(line);
		post.setToken(apiToken);	
		String url = String.format("%s%s", apiHost, lootCallApiUrl);

		return restTemplate.postForEntity(url, post, String.class);
	}
	
	public ResponseEntity<String> sendRaidRoster(File roster, JsonDatabaseEntry entry) throws IOException, URISyntaxException {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("file", new FileSystemResource(roster));
		map.add("token", apiToken);

        String url = String.format("%s%s", apiHost, rosterApiUrl);		
		
		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
		return restTemplate.postForEntity(url, entity, String.class);
	}
}
