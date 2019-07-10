# Scsics Framework Cache Redis

### Customized RedisCacheManager

To solve the expire time problem in the Spring Cache.

The default RedisCacheManager is not good.

Mode of use:

```xml
<bean id="cacheManager" class="org.springframework.data.redis.cache.RedisCacheManager">
	<constructor-arg name="redisOperations" ref="redisTemplate"/>
	<property name="defaultExpiration" value="600" />
</bean>
```

### Customized RedisCacheManager

To solve the expire time problem in the Spring Cache.

Use the separators, For example: @Cacheable(value = "task#60")

But this way is deprecated, Writing method and Redis storage is not beautiful.

replaced by com.scsics.cache.redis.ScsicsRedisCacheManager

Mode of use:

```xml
<bean id="cacheManager" class="com.scsics.cache.redis.SeparatorRedisCacheManager">
	<constructor-arg name="redisOperations" ref="redisTemplate"/>
	<property name="defaultExpiration" value="600" />
	<property name="loadRemoteCachesOnStartup" value="true"/>
	<property name="usePrefix" value="true"/>
	<property name="cachePrefix">
		<bean class="com.scsics.cache.redis.ScsicsRedisCachePrefix">
			<constructor-arg name="delimiter" value="lichee:"/>
		</bean>
	</property> 
</bean>
```

