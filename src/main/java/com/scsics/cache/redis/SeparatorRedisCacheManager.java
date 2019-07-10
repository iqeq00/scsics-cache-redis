package com.scsics.cache.redis;

import java.util.Objects;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisOperations;

/**
 * Customized RedisCacheManager
 * To solve the expire time problem in the Spring Cache.
 * 
 * Use the separators, For example: @Cacheable(value = "task#60")
 * But this way is deprecated, Writing method and Redis storage is not beautiful.
 * replaced by com.scsics.cache.redis.ScsicsRedisCacheManager
 * 
 * Mode of use:
 * <bean id="cacheManager" class="com.scsics.cache.redis.SeparatorRedisCacheManager">
 *     <constructor-arg name="redisOperations" ref="redisTemplate"/>
 *     <property name="defaultExpiration" value="600" />
 *     <property name="loadRemoteCachesOnStartup" value="true"/>
 *     <property name="usePrefix" value="true"/>
 *     <property name="cachePrefix">
 *         <bean class="com.scsics.cache.redis.ScsicsRedisCachePrefix">
 *             <constructor-arg name="delimiter" value="lichee:"/>
 *         </bean>
 *     </property> 
 * </bean>
 * 
 * @author lichee
 */
@Deprecated
public class SeparatorRedisCacheManager extends RedisCacheManager {

	private static Logger logger = LoggerFactory.getLogger(SeparatorRedisCacheManager.class);
	private static final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
    private static final Pattern pattern = Pattern.compile("[+\\-*/%]");
    /** 分隔符 */
    private char separator = '#';

    public SeparatorRedisCacheManager(@SuppressWarnings("rawtypes") RedisOperations redisOperations) {
        super(redisOperations);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected RedisCache createCache(String cacheName) {
        // 获取默认时间
        long expiration = computeExpiration(cacheName);     
        int index = cacheName.indexOf(this.getSeparator());
        if (index > 0) {
            expiration = getExpiration(cacheName, index, expiration);
        }
        return new RedisCache(cacheName, (isUsePrefix() ? getCachePrefix().prefix(cacheName) : null),
                getRedisOperations(), expiration);
    }

    /**
     * 计算缓存时间
     * @param name 缓存名字 cache#60*60
     * @param separatorIndex 分隔符位置
     * @param defalutExp 默认缓存时间
     * @return
     */
    protected long getExpiration(final String name, final int separatorIndex, final long defalutExp) {
        Long expiration = null;
        String expirationAsString = name.substring(separatorIndex + 1);
        try {
            if (pattern.matcher(expirationAsString).find()) {
                expiration = NumberUtils.toLong(scriptEngine.eval(expirationAsString).toString(), defalutExp);
            } else {
                expiration = NumberUtils.toLong(expirationAsString, defalutExp);
            }
        } catch (ScriptException e) {
        	logger.error("缓存时间转换错误:{},异常:{}", name, e.getMessage());
        }
        return Objects.nonNull(expiration) ? expiration.longValue() : defalutExp;
    }

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

}