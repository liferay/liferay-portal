/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache.internal.expiry;

import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.time.Duration;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.impl.internal.TimeSourceConfiguration;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class EhcacheExpiryPolicyTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Before
	public void setUp() {
		CacheManagerBuilder<CacheManager> cacheManagerBuilder =
			CacheManagerBuilder.newCacheManagerBuilder();

		_cacheManager = cacheManagerBuilder.using(
			new TimeSourceConfiguration(() -> _timeMillis)
		).build(
			true
		);

		_cache = _cacheManager.createCache(
			"test",
			CacheConfigurationBuilder.newCacheConfigurationBuilder(
				String.class, EhcacheExpiryValue.class,
				ResourcePoolsBuilder.heap(10)
			).withExpiry(
				new EhcacheExpiryPolicy(
					ExpiryPolicyBuilder.timeToIdleExpiration(
						Duration.ofSeconds(600)))
			));
	}

	@After
	public void tearDown() {
		_cacheManager.close();
	}

	@Test
	public void testGetExpiryForAccess() {
		_cache.put(
			"key1", new EhcacheExpiryValue("value", Duration.ofSeconds(10)));
		_cache.put(
			"key2", new EhcacheExpiryValue("value", ExpiryPolicy.INFINITE));

		_timeMillis = 9000;

		Assert.assertNotNull(_cache.get("key1"));
		Assert.assertNotNull(_cache.get("key2"));

		_timeMillis = 11000;

		Assert.assertNull(_cache.get("key1"));
		Assert.assertNotNull(_cache.get("key2"));

		_timeMillis = 500000;

		Assert.assertNotNull(_cache.get("key2"));

		_timeMillis = 1000000;

		Assert.assertNotNull(_cache.get("key2"));

		_timeMillis = 1700000;

		Assert.assertNull(_cache.get("key2"));
	}

	@Test
	public void testGetExpiryForUpdate() {
		_cache.put(
			"key", new EhcacheExpiryValue("value", Duration.ofSeconds(10)));

		_timeMillis = 9000;

		_cache.put(
			"key", new EhcacheExpiryValue("value", Duration.ofSeconds(10)));

		_timeMillis = 15000;

		Assert.assertNotNull(_cache.get("key"));

		_timeMillis = 20000;

		Assert.assertNull(_cache.get("key"));
	}

	private Cache<String, EhcacheExpiryValue> _cache;
	private CacheManager _cacheManager;
	private volatile long _timeMillis;

}