package com.github.unsunglei.ezeqra.client.logreader;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.unsunglei.ezeqra.client.configuration.ClientConfiguration;
import com.github.unsunglei.ezeqra.client.loot.LootCallLogLine;
import com.github.unsunglei.ezeqra.client.loot.LootCallService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LogReader {

	@Autowired ClientConfiguration clientConfig;	
	@Autowired LogLineFactory logLineFactory;
	@Autowired LootCallService lootCallService;
	
	
	
	private RandomAccessFile file;
	
	private List<LootCallLogLine> lootCallLogLines = new ArrayList<>();
	
	@PostConstruct
	public void init() throws IOException {
		String logFileName = String.format(clientConfig.getLogFileFormat(), clientConfig.getPlayer(), clientConfig.getServer());
		String logFilePath = Paths.get(clientConfig.getEqHome(), "Logs", logFileName).toString();
		file = new RandomAccessFile(logFilePath, "r");
		file.seek(0);
		
		if(clientConfig.isLogStartAtEnd()) {
			file.seek(file.length());
		}
		
		log.info("ClientConfiguration: \n{}", clientConfig);
		
	}
	
	@Scheduled(cron = "*/10 * * * * *")
	public void readNextLines() throws IOException {
		
		while(file.getFilePointer() < file.length()) {
			
			String message = getNextLine(file);
			
			ChatLogLine chatLogLine = logLineFactory.chatLogLine(message);
			if(chatLogLine != null) {
				LootCallLogLine lootCall = logLineFactory.lootCallLogLine(chatLogLine);
				if(lootCall != null) {
					log.info("{}", chatLogLine);
					log.info("{}", lootCall);
					
					lootCallService.addLootCall(lootCall);
				}
			}
		}
	}	
	
	private String getNextLine(RandomAccessFile file) throws IOException {
		
		StringBuilder message = new StringBuilder();
	
		while(true) {
			char ch = (char) file.readUnsignedByte();
			
			if(ch == '\n') {
				break;
			}
			
			message.append(ch);
		}
		
		return message.toString();
	}
}
