package com.github.unsunglei.ezeqra.client.api;

import java.util.UUID;

import com.github.unsunglei.ezeqra.client.loot.LootCallLogLine;

import lombok.Data;

@Data
public class LootCallPost {
	private LootCallLogLine lootCall;
	private UUID token;
}
