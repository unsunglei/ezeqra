package com.github.unsunglei.ezeqra.client.api;

import java.util.UUID;

import lombok.Data;

@Data
public class PayloadPost<T> {
	T payload;
	UUID token;
}
