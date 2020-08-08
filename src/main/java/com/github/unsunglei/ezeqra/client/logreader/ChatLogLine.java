package com.github.unsunglei.ezeqra.client.logreader;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatLogLine {
	private LocalDateTime time;
	private String subject;
	private String action;
	private String channel;
	private String body;
	private String context;
}
