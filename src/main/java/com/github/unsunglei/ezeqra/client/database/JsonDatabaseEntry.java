package com.github.unsunglei.ezeqra.client.database;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class JsonDatabaseEntry {
	private String name;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime time;
	private String status;
	
	private Map<String, Object> context = new LinkedHashMap<>();
}
