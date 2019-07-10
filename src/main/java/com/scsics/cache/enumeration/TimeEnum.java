package com.scsics.cache.enumeration;

/**
 * Time Enum, Supplement @CacheableExpire, In seconds.
 * 
 * Enumeration Type, But this way is deprecated, Not common. 
 * replaced by com.scsics.cache.constant.ExpireTime
 * 
 * Mode of use:
 * @interface CacheableExpire 
 * TimeEnum value() default TimeEnum.MINUTES;
 * 
 * @author lichee
 */
@Deprecated
public enum TimeEnum {

	MINUTES(60), FIFTEEN_MINUTES(60 * 15), HALF_HOURS(60 * 30), 
	HOURS(3600), HALF_DAYS(3600 * 12), DAYS(3600 * 24), 
	WEEKS(3600 * 24 * 7), HALF_MONTHS(3600 * 24 * 15), MONTHS(3600 * 24 * 30), 
	QUARTER(3600 * 24 * 90), HALF_YEARS(3600 * 24 * 180), YEARS(3600 * 24 * 360);

	public final long value;

	public long getValue() {
		return value;
	}

	TimeEnum(long value) {
		this.value = value;
	}
}