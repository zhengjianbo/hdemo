package com.ram.kettle.util;

import java.util.UUID;

public class UUIDUtil {
	public static String getUUIDAsString() {
		return UUID.randomUUID().toString();
	}
}
