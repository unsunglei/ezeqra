package com.github.unsunglei.ezeqra.client.logreader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.unsunglei.ezeqra.client.configuration.RegexConfiguration;

@Component
public class LineMatcher {
	
	@Autowired
	private RegexConfiguration regexConfiguration;
	
	public static enum Group {
		subject, action, channel, context, item, winner
	}
	
	public boolean isChatChannelLogLine(String message) {
		String body = getMessageBody(message);
		Pattern pattern = Pattern.compile(this.regexConfiguration.getChatChannel());
		Matcher matcher = pattern.matcher(body);
		
		return matcher.find();
	}
	
	public boolean isLootCallLogLine(String context) {
		Pattern pattern = Pattern.compile(this.regexConfiguration.getLootCall());
		Matcher matcher = pattern.matcher(context);
		
		return matcher.find();
	}
	
	public LocalDateTime getLogTime(String message) {
		Pattern pattern = Pattern.compile(this.regexConfiguration.getLogLine());
		Matcher matcher = pattern.matcher(message);
		
		if(matcher.find()) {
			String month = matcher.group("month");
			String day = matcher.group("day");
			String hours = matcher.group("hours");
			String minutes = matcher.group("minutes");
			String seconds = matcher.group("seconds");
			String year = matcher.group("year");
			
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd HH:mm:ss yyyy");
			
			LocalDateTime time = LocalDateTime.parse(String.format("%s %s %s:%s:%s %s", month, day, hours, minutes, seconds, year), dtf);
			return time;
		}
		
		return null;
	}
	
	public String getMessageBody(String message) {
		Pattern pattern = Pattern.compile(this.regexConfiguration.getLogLine());
		Matcher matcher = pattern.matcher(message);
		
		if(matcher.find()) {
			return matcher.group("body");
		}
		
		return null;
	}
	
	public String getLootCallGroup(String context, Group group) {
		Pattern pattern = Pattern.compile(this.regexConfiguration.getLootCall());
		Matcher matcher = pattern.matcher(context);
		
		String match = null;
		
		if(matcher.find()) {
			match = matcher.group(group.toString());			
		}
		
		return match;
	}
	
	public String getChatChannelGroup(String messageBody, Group group) {
		Pattern pattern = Pattern.compile(this.regexConfiguration.getChatChannel());
		Matcher matcher = pattern.matcher(messageBody);
		
		String match = null;
		
		if(matcher.find()) {
			match = matcher.group(group.toString());
			// hack for shout, say.. etc because the regex does not see the channel
			if(group == Group.channel) {
				String action = matcher.group(Group.action.toString());
				if(action.startsWith("say") || action.startsWith("shout")) {
					match = action;
				} else {
					match = match.split(":")[0];
				}
			}
		}
		
		return match;
	}
}
