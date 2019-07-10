package com.scsics.cache.redis;

import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Customized RedisCachePrefix To solve the prefix problem 
 * in the Spring Data Redis.
 * 
 * @author lichee
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ScsicsRedisCachePrefix implements RedisCachePrefix {

	private final RedisSerializer serializer = new StringRedisSerializer();
	private final String delimiter;

	public ScsicsRedisCachePrefix() {
		this(":");
	}

	public ScsicsRedisCachePrefix(String delimiter) {
		this.delimiter = delimiter;
	}

	public byte[] prefix(String cacheName) {
		return serializer.serialize((delimiter != null ? delimiter.concat(cacheName) : ":".concat(cacheName)));
	}
}
