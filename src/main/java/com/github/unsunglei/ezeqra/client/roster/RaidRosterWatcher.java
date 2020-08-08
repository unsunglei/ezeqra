package com.github.unsunglei.ezeqra.client.roster;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.unsunglei.ezeqra.client.api.ApiService;
import com.github.unsunglei.ezeqra.client.configuration.ClientConfiguration;
import com.github.unsunglei.ezeqra.client.configuration.DatabaseConfiguration;
import com.github.unsunglei.ezeqra.client.database.JsonDatabase;
import com.github.unsunglei.ezeqra.client.database.JsonDatabaseEntry;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RaidRosterWatcher {
	
	@Autowired
	private ClientConfiguration clientConfiguration;
	
	@Autowired
	private DatabaseConfiguration databaseConfiguration;
	
	@Autowired
	private ApiService apiService;
	
	private JsonDatabase database;
	
	
	@PostConstruct
	public void init() throws IOException {
		log.info("Initializing RaidRosterWatcher");
		database = new JsonDatabase(databaseConfiguration.getRoster());
	}
	
	private String raidRosterPrefix() {
		return String.format(clientConfiguration.getRaidRosterPrefixFormat(), clientConfiguration.getServer());
	}

	private List<File> getRaidRosterFiles() {
		File file = Paths.get(clientConfiguration.getEqHome()).toFile();
		
		File[] files = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return name.startsWith(raidRosterPrefix());
			}
		});
		
		return Arrays.stream(files).collect(Collectors.toList());
	}
	
	
	
	@Scheduled(cron = "${ezeqra.cron.roster}")
	public void poll() throws URISyntaxException, IOException {
		this.database.loadDatabase();
		List<File> raidRosters = getRaidRosterFiles();		
		
		for(File roster : raidRosters) {
			
			JsonDatabaseEntry entry = this.database.findByName(roster.getName());
			
			if(entry == null) {
				entry = new JsonDatabaseEntry();
				entry.setName(roster.getName());
			} else if(entry.getStatus().equals("complete")) {
				log.debug("Skipping {}", entry.getName());
				continue;
			}
			
			try {
				ResponseEntity<String> response = apiService.sendRaidRoster(roster, entry);
				if(response.getStatusCodeValue() == 200) {
					entry.setStatus("complete");
				}
			} catch (Exception e) {
				log.error("Error sending RaidRoster: {}, {}", entry.getName(), e.getMessage());
				entry.setStatus("failed");
			} finally {
				this.database.save(entry);
			}
			
		}
	}
}
