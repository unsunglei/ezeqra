package com.github.unsunglei.ezeqra.client.loot;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.github.unsunglei.ezeqra.client.api.ApiService;
import com.github.unsunglei.ezeqra.client.configuration.DatabaseConfiguration;
import com.github.unsunglei.ezeqra.client.database.JsonDatabase;
import com.github.unsunglei.ezeqra.client.database.JsonDatabaseEntry;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LootCallService {
	
	private static final String TIME = "time";
	private static final String ITEM = "item";
	private static final String WINNER = "winner";

	@Autowired 
	private DatabaseConfiguration databaseConfiguration;
	
	@Autowired
	private ApiService apiService;
	
	private JsonDatabase database;
	
	@PostConstruct
	public void init() throws IOException {
		log.info("Initializing LootCallService");
		database = new JsonDatabase(databaseConfiguration.getLootCall());
	}
	
	private String createUniqueKey(LootCallLogLine line) {
		return String.format("%s__%s__%s", line.getTime(), line.getItem(), line.getWinner());
	}
	
	public void addLootCall(LootCallLogLine line) throws IOException {
		String key = this.createUniqueKey(line);
		
		JsonDatabaseEntry entry = database.findByName(key);
		if(entry == null) {
			entry = new JsonDatabaseEntry();
			entry.setName(createUniqueKey(line));
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
			entry.getContext().put(TIME, line.getTime().format(dtf));
			entry.getContext().put(ITEM, line.getItem());
			entry.getContext().put(WINNER, line.getWinner());
			entry.setStatus("new");
			
			this.database.save(entry);
		} else {
			log.warn("LootCall exists, not adding. {}", line);
		}
	}
	
	@Scheduled(cron = "${ezeqra.cron.loot-call}")
	private void sendLootCalls() throws IOException {
		this.database.loadDatabase();
		
		List<JsonDatabaseEntry> entries = this.database.getEntries();
		entries = entries.stream().filter(e -> !e.getStatus().equals("completed")).collect(Collectors.toList());
		
		for(JsonDatabaseEntry entry : entries) {
			LootCallLogLine line = new LootCallLogLine();
			try {
				line.setItem((String) entry.getContext().get(ITEM));
				line.setWinner((String) entry.getContext().get(WINNER));
				line.setTime(LocalDateTime.parse((String) entry.getContext().get(TIME)));
				apiService.sendLootCall(line);
				entry.setStatus("completed");
			} catch (Exception e) {
				log.error("Error sending LootCall: {}, entry: {}, {}", line, entry, e.getMessage());
				entry.setStatus("failed");
			} finally {
				this.database.save(entry);
			}
		}
	}
}
