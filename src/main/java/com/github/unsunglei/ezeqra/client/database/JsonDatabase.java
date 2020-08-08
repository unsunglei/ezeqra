package com.github.unsunglei.ezeqra.client.database;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
public class JsonDatabase {
	
	@JsonIgnore
	private String filePath;
	
	private List<JsonDatabaseEntry> entries = Collections.synchronizedList(new ArrayList<>());
	
	public JsonDatabase(String filePath) throws JsonParseException, JsonMappingException, IOException {
		this.filePath = filePath;
		this.loadDatabase();
	}
	
	public void loadDatabase() throws JsonParseException, JsonMappingException, IOException {
		log.debug("Loading database '{}'", this.filePath);
		File file = new File(this.filePath);
		
		if(file.exists()) {
			ObjectMapper mapper = createObjectMapper();
			JsonDatabase database = mapper.readValue(file, JsonDatabase.class);
			this.entries = database.getEntries();
		} else {
			this.entries = Collections.synchronizedList(new ArrayList<>());
		}
		
		log.debug("Loaded database '{}', entries: {}", this.filePath, this.entries.size());
	}
	
	private void saveDatabase() throws IOException {
		log.debug("Saving database '{}', entries: {}", this.filePath, this.entries.size());
		File file = new File(this.filePath);
		ObjectMapper mapper = createObjectMapper();
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
		
		try(FileWriter writer = new FileWriter(file)) {
			writer.write(json);
			log.debug("Saved database '{}', entries: {}", this.filePath, this.entries.size());
		}
	}
	
	public JsonDatabaseEntry findByName(String name) {
		return this.entries.stream().filter(
				e -> e.getName().equals(name)
		).findFirst().orElse(null);
	}
	
	public synchronized JsonDatabaseEntry save(JsonDatabaseEntry entry) throws IOException {
		log.debug("Saving entry to '{}', entry: {}", this.filePath, entry);
		// remove the old entry if it exists by filtering where it is not equal
		this.entries = this.entries.stream().filter(
				e-> !e.getName().equals(entry.getName())
		).collect(Collectors.toList());
		
		// update the time
		entry.setTime(LocalDateTime.now());
		
		this.entries.add(entry);
		
		// save DB
		this.saveDatabase();
		log.debug("Saved entry to '{}', entry: {}", this.filePath, entry);
		return entry;
	}
	
	private ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		return mapper;
	}
}
