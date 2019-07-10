package com.scsics.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.scsics.cache.constant.ExpireTime;

/**
 * Cacheable Expire, Supplement @Cacheable, In seconds.
 * 
 * @author lichee
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface CacheableExpire {

	/**
	 * Sets the expire time (in seconds). default MINUTES.
	 */
	long value() default ExpireTime.HOURS;
}