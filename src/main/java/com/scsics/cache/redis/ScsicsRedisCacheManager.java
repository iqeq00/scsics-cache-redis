package com.scsics.cache.redis;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.scsics.cache.annotation.CacheableExpire;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * Customized RedisCacheManager
 * To solve the expire time problem in the Spring Cache.
 * 
 * The default RedisCacheManager is not good.
 * * Mode of use:
 * <bean id="cacheManager" class="org.springframework.data.redis.cache.RedisCacheManager">
 *     <constructor-arg name="redisOperations" ref="redisTemplate"/>
 *	   <property name="defaultExpiration" value="600" />
 * </bean>
 * 
 * @author lichee
 */
@SuppressWarnings({ "rawtypes" })
public class ScsicsRedisCacheManager extends RedisCacheManager implements ApplicationContextAware, InitializingBean {

	private ApplicationContext applicationContext;

	public ScsicsRedisCacheManager(RedisOperations redisOperations) {
		super(redisOperations);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() {
		parseCacheableExpire(applicationContext);
	}

	private void parseCacheableExpire(ApplicationContext applicationContext) {
		final Map<String, Long> cacheExpires = new HashMap<String, Long>();
		String[] beanNames = applicationContext.getBeanNamesForType(Object.class);
		for (String beanName : beanNames) {
			final Class clazz = applicationContext.getType(beanName);
			Service service = findAnnotation(clazz, Service.class);
			if (null == service) {
				continue;
			}
			addCacheExpires(clazz, cacheExpires);
		}
		// 设置有效期
		super.setExpires(cacheExpires);
	}

	private void addCacheExpires(final Class clazz, final Map<String, Long> cacheExpires) {
		ReflectionUtils.doWithMethods(clazz, new ReflectionUtils.MethodCallback() {
			@Override
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				ReflectionUtils.makeAccessible(method);
				CacheableExpire cacheableExpire = findCacheableExpire(clazz, method);
				Cacheable cacheable = findAnnotation(method, Cacheable.class);
				CacheConfig cacheConfig = findAnnotation(clazz, CacheConfig.class);
				Set<String> cacheNames = findCacheNames(cacheConfig, cacheable);
				cacheNames.forEach(cacheName -> cacheExpires.put(cacheName, cacheableExpire.value()));
			}
		}, new ReflectionUtils.MethodFilter() {
			@Override
			public boolean matches(Method method) {
				return null != findAnnotation(method, Cacheable.class);
			}
		});
	}

	/**
	 * CacheableExpire标注的有效期，优先使用方法上标注的有效期
	 *
	 * @param clazz
	 * @param method
	 * @return
	 */
	private CacheableExpire findCacheableExpire(Class clazz, Method method) {
		CacheableExpire methodCacheableExpire = findAnnotation(method, CacheableExpire.class);
		if (null != methodCacheableExpire) {
			return methodCacheableExpire;
		}

		CacheableExpire classCacheableExpire = findAnnotation(clazz, CacheableExpire.class);
		if (null != classCacheableExpire) {
			return classCacheableExpire;
		}

		throw new IllegalStateException("No CacheableExpire config on Class " + clazz.getName() + " and method " + method.toString());
	}

	private Set<String> findCacheNames(CacheConfig cacheConfig, Cacheable cacheable) {
		return isEmpty(cacheable.value()) ? newHashSet(cacheConfig.cacheNames()) : newHashSet(cacheable.value());
	}
}
