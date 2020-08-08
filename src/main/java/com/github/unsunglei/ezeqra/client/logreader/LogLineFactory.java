package com.github.unsunglei.ezeqra.client.logreader;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.unsunglei.ezeqra.client.logreader.LineMatcher.Group;
import com.github.unsunglei.ezeqra.client.loot.LootCallLogLine;

@Service
public class LogLineFactory {
	
	@Autowired
	private LineMatcher lineMatcher;
	
	public ChatLogLine chatLogLine(String logLine) {
		
		if(!lineMatcher.isChatChannelLogLine(logLine)) {
			return null;
		}
		
		LocalDateTime logTime = lineMatcher.getLogTime(logLine);
		String body = lineMatcher.getMessageBody(logLine);
		
		String subject = lineMatcher.getChatChannelGroup(body, Group.subject);
		String channel = lineMatcher.getChatChannelGroup(body, Group.channel);
		String context = lineMatcher.getChatChannelGroup(body, Group.context);
		String action = lineMatcher.getChatChannelGroup(body, Group.action);
		
		ChatLogLine chatLogLine = new ChatLogLine();
		chatLogLine.setAction(action);
		chatLogLine.setBody(body);
		chatLogLine.setSubject(subject);
		chatLogLine.setTime(logTime);
		chatLogLine.setChannel(channel);
		chatLogLine.setContext(context.trim());
		
		return chatLogLine;
	}
	
	public LootCallLogLine lootCallLogLine(ChatLogLine chatLogLine) {
		if(!lineMatcher.isLootCallLogLine(chatLogLine.getContext())) {
			return null;
		}
		
		String winner = lineMatcher.getLootCallGroup(chatLogLine.getContext(), Group.winner);
		String item = lineMatcher.getLootCallGroup(chatLogLine.getContext(), Group.item);
		
		LootCallLogLine lootCallLogLine = new LootCallLogLine();
		lootCallLogLine.setWinner(winner.toLowerCase());
		lootCallLogLine.setItem(item);
		lootCallLogLine.setTime(chatLogLine.getTime());
		
		return lootCallLogLine;
	}
}
