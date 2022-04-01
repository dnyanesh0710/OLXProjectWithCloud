package com.zensar.olx.users.db;

import java.util.HashMap;
import java.util.Map;

public class TokenStorage {
	private static Map<String, String> tokenCache;
	
	static {
		tokenCache=new HashMap<>();
	}
	
	public static String getToken(String key) {
		return tokenCache.get(key);
	}
	
	public static void storeToken(String key,String token) {
		tokenCache.put(key, token);
	}
	public static String removeToken(String key) {
		return tokenCache.remove(key);
	}
}
