package com.github.unsunglei.ezeqra.client.loot;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class LootCallLogLine {
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime time;
	private String winner;
	private String item;
}
