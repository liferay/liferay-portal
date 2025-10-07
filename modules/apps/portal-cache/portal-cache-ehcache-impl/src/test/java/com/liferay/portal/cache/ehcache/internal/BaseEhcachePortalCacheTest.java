/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache.internal;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.cache.ehcache.internal.configuration.EhcachePortalCacheConfiguration;
import com.liferay.portal.cache.ehcache.internal.events.PortalCacheCacheEventListener;
import com.liferay.portal.cache.test.util.TestPortalCacheListener;
import com.liferay.portal.cache.test.util.TestPortalCacheReplicator;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheListener;
import com.liferay.portal.kernel.cache.PortalCacheListenerScope;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.impl.internal.executor.OnDemandExecutionService;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class BaseEhcachePortalCacheTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new CodeCoverageAssertor() {

				@Override
				public void appendAssertClasses(List<Class<?>> assertClasses) {
					assertClasses.add(BaseEhcachePortalCache.class);
				}

			},
			LiferayUnitTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() {
		_cacheConfigurationBuilder =
			CacheConfigurationBuilder.newCacheConfigurationBuilder(
				Object.class, Object.class, ResourcePoolsBuilder.heap(100));

		CacheManagerBuilder<CacheManager> cacheManagerBuilder =
			CacheManagerBuilder.newCacheManagerBuilder();

		ExecutorService executorService = ReflectionTestUtil.getFieldValue(
			BaseEhcachePortalCacheManager.class, "_executorService");

		_cacheManager = cacheManagerBuilder.using(
			new OnDemandExecutionService() {

				@Override
				public ExecutorService getOrderedExecutor(
						String poolAlias, BlockingQueue<Runnable> queue)
					throws IllegalArgumentException {

					return executorService;
				}

			}
		).build(
			true
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_cacheManager.close();
	}

	@Before
	public void setUp() {
		_cache = _cacheManager.createCache(
			_PORTAL_CACHE_NAME, _cacheConfigurationBuilder);

		BaseEhcachePortalCacheManager baseEhcachePortalCacheManager =
			new BaseEhcachePortalCacheManager() {
			};

		ReflectionTestUtil.setFieldValue(
			baseEhcachePortalCacheManager, "_cacheManager", _cacheManager);
		ReflectionTestUtil.setFieldValue(
			baseEhcachePortalCacheManager, "_defaultCacheConfiguration",
			_cacheConfigurationBuilder.build());

		_ehcachePortalCache = new EhcachePortalCache<>(
			baseEhcachePortalCacheManager,
			new EhcachePortalCacheConfiguration(
				_PORTAL_CACHE_NAME, null, Object.class, Object.class, false));

		_ehcachePortalCache.put(_KEY_1, _VALUE_1);

		_defaultPortalCacheListener = new TestPortalCacheListener<>();

		_ehcachePortalCache.registerPortalCacheListener(
			_defaultPortalCacheListener);

		_defaultPortalCacheReplicator = new TestPortalCacheReplicator<>();

		_ehcachePortalCache.registerPortalCacheListener(
			_defaultPortalCacheReplicator);
	}

	@After
	public void tearDown() {
		Configuration configuration = _cacheManager.getRuntimeConfiguration();

		Map<String, CacheConfiguration<?, ?>> cacheConfigurations =
			configuration.getCacheConfigurations();

		for (String key : cacheConfigurations.keySet()) {
			_cacheManager.removeCache(key);
		}
	}

	@Test
	public void testCacheListener() {

		// Register 1

		TestPortalCacheListener<String, String> localPortalCacheListener =
			new TestPortalCacheListener<>();

		_ehcachePortalCache.registerPortalCacheListener(
			localPortalCacheListener, PortalCacheListenerScope.LOCAL);

		_ehcachePortalCache.put(_KEY_2, _VALUE_2);

		localPortalCacheListener.assertActionsCount(1);
		localPortalCacheListener.assertPut(_KEY_2, _VALUE_2);

		localPortalCacheListener.reset();

		_defaultPortalCacheListener.assertActionsCount(1);
		_defaultPortalCacheListener.assertPut(_KEY_2, _VALUE_2);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertActionsCount(1);
		_defaultPortalCacheReplicator.assertPut(_KEY_2, _VALUE_2);

		_defaultPortalCacheReplicator.reset();

		// Register 2

		TestPortalCacheListener<String, String> remotePortalCacheListener =
			new TestPortalCacheListener<>();

		_ehcachePortalCache.registerPortalCacheListener(
			remotePortalCacheListener, PortalCacheListenerScope.REMOTE);

		_ehcachePortalCache.put(_KEY_2, _VALUE_1);

		localPortalCacheListener.assertActionsCount(1);
		localPortalCacheListener.assertUpdated(_KEY_2, _VALUE_1);

		localPortalCacheListener.reset();

		remotePortalCacheListener.assertActionsCount(0);

		_defaultPortalCacheListener.assertActionsCount(1);
		_defaultPortalCacheListener.assertUpdated(_KEY_2, _VALUE_1);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertActionsCount(1);
		_defaultPortalCacheReplicator.assertUpdated(_KEY_2, _VALUE_1);

		_defaultPortalCacheReplicator.reset();

		// Register 3

		_ehcachePortalCache.registerPortalCacheListener(
			remotePortalCacheListener, PortalCacheListenerScope.ALL);

		_ehcachePortalCache.put(_KEY_2, _VALUE_2);

		localPortalCacheListener.assertActionsCount(1);
		localPortalCacheListener.assertUpdated(_KEY_2, _VALUE_2);

		localPortalCacheListener.reset();

		remotePortalCacheListener.assertActionsCount(0);

		_defaultPortalCacheListener.assertActionsCount(1);
		_defaultPortalCacheListener.assertUpdated(_KEY_2, _VALUE_2);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertActionsCount(1);
		_defaultPortalCacheReplicator.assertUpdated(_KEY_2, _VALUE_2);

		_defaultPortalCacheReplicator.reset();

		// Unregister 1

		_ehcachePortalCache.unregisterPortalCacheListener(
			localPortalCacheListener);

		_ehcachePortalCache.put(_KEY_1, _VALUE_2);

		localPortalCacheListener.assertActionsCount(0);

		remotePortalCacheListener.assertActionsCount(0);

		_defaultPortalCacheListener.assertActionsCount(1);
		_defaultPortalCacheListener.assertUpdated(_KEY_1, _VALUE_2);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertActionsCount(1);
		_defaultPortalCacheReplicator.assertUpdated(_KEY_1, _VALUE_2);

		_defaultPortalCacheReplicator.reset();

		// Unregister 2

		_ehcachePortalCache.unregisterPortalCacheListener(
			new TestPortalCacheListener<String, String>());

		_ehcachePortalCache.put(_KEY_1, _VALUE_1);

		localPortalCacheListener.assertActionsCount(0);

		remotePortalCacheListener.assertActionsCount(0);

		_defaultPortalCacheListener.assertActionsCount(1);
		_defaultPortalCacheListener.assertUpdated(_KEY_1, _VALUE_1);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertActionsCount(1);
		_defaultPortalCacheReplicator.assertUpdated(_KEY_1, _VALUE_1);

		_defaultPortalCacheReplicator.reset();

		// Unregister 3

		_ehcachePortalCache.unregisterPortalCacheListeners();

		_ehcachePortalCache.put(_KEY_1, _VALUE_2);

		localPortalCacheListener.assertActionsCount(0);
		remotePortalCacheListener.assertActionsCount(0);
		_defaultPortalCacheListener.assertActionsCount(0);
		_defaultPortalCacheReplicator.assertActionsCount(0);
	}

	@Test
	public void testDispose() {
		Assert.assertNotNull(
			_cacheManager.getCache(
				_PORTAL_CACHE_NAME, Object.class, Object.class));

		_ehcachePortalCache.dispose();

		Assert.assertNull(
			_cacheManager.getCache(
				_PORTAL_CACHE_NAME, Object.class, Object.class));
	}

	@Test
	public void testGetEhcache() {
		Assert.assertSame(_cache, _ehcachePortalCache.getEhcache());
	}

	@Test
	public void testGetEhcacheConcurrently() throws Exception {
		Cache<?, ?> cache = _ehcachePortalCache.getEhcache();

		_ehcachePortalCache.resetEhcache();
		_cacheManager.removeCache(_PORTAL_CACHE_NAME);

		CountDownLatch countDownLatch = new CountDownLatch(1);

		FutureTask<Void> controllerFutureTask = new FutureTask<>(
			() -> {
				synchronized (_ehcachePortalCache) {
					countDownLatch.await();
				}

				return null;
			});

		Thread controllerThread = new Thread(
			controllerFutureTask,
			"Ehcache Portal Cache Test_Thread_Controller");

		controllerThread.start();

		FutureTask<Cache<?, ?>> futureTask1 = new FutureTask<>(
			_ehcachePortalCache::getEhcache);
		FutureTask<Cache<?, ?>> futureTask2 = new FutureTask<>(
			_ehcachePortalCache::getEhcache);

		Thread thread1 = new Thread(
			futureTask1, "Ehcache Portal Cache Test_Thread_1");
		Thread thread2 = new Thread(
			futureTask2, "Ehcache Portal Cache Test_Thread_2");

		thread1.start();
		thread2.start();

		countDownLatch.countDown();

		Assert.assertNotNull(futureTask1.get());
		Assert.assertNotSame(cache, futureTask2.get());

		Assert.assertSame(futureTask1.get(), futureTask2.get());
	}

	@Test
	public void testGetKeys() {
		Assert.assertEquals(_VALUE_1, _ehcachePortalCache.get(_KEY_1));
		Assert.assertNull(_ehcachePortalCache.get(_KEY_2));

		List<String> keys = _ehcachePortalCache.getKeys();

		Assert.assertEquals(keys.toString(), 1, keys.size());
		Assert.assertTrue(keys.toString(), keys.contains(_KEY_1));
	}

	@Test
	public void testGetName() {
		Assert.assertEquals(
			_PORTAL_CACHE_NAME, _ehcachePortalCache.getPortalCacheName());
	}

	@Test
	public void testGetPortalCacheListeners() {
		Map<PortalCacheListener<String, String>, PortalCacheListenerScope>
			portalCacheListeners =
				_ehcachePortalCache.getPortalCacheListeners();

		Assert.assertTrue(
			portalCacheListeners.containsKey(_defaultPortalCacheListener));
		Assert.assertTrue(
			portalCacheListeners.containsKey(_defaultPortalCacheReplicator));
	}

	@Test
	public void testPut() {
		Assert.assertEquals(_VALUE_1, _ehcachePortalCache.get(_KEY_1));
		Assert.assertNull(_ehcachePortalCache.get(_KEY_2));

		// Put 1

		_ehcachePortalCache.put(_KEY_2, _VALUE_2);

		Assert.assertEquals(_VALUE_1, _ehcachePortalCache.get(_KEY_1));
		Assert.assertEquals(_VALUE_2, _ehcachePortalCache.get(_KEY_2));

		_defaultPortalCacheListener.assertActionsCount(1);
		_defaultPortalCacheListener.assertPut(_KEY_2, _VALUE_2);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertActionsCount(1);
		_defaultPortalCacheReplicator.assertPut(_KEY_2, _VALUE_2);

		_defaultPortalCacheReplicator.reset();

		// Put 2

		_ehcachePortalCache.put(_KEY_1, _VALUE_2);

		Assert.assertEquals(_VALUE_2, _ehcachePortalCache.get(_KEY_1));
		Assert.assertEquals(_VALUE_2, _ehcachePortalCache.get(_KEY_2));

		_defaultPortalCacheListener.assertActionsCount(1);
		_defaultPortalCacheListener.assertUpdated(_KEY_1, _VALUE_2);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertActionsCount(1);
		_defaultPortalCacheReplicator.assertUpdated(_KEY_1, _VALUE_2);

		_defaultPortalCacheReplicator.reset();

		// Put 3

		PortalCacheHelperUtil.putWithoutReplicator(
			_ehcachePortalCache, _KEY_2, _VALUE_1);

		Assert.assertEquals(_VALUE_2, _ehcachePortalCache.get(_KEY_1));
		Assert.assertEquals(_VALUE_1, _ehcachePortalCache.get(_KEY_2));

		_defaultPortalCacheListener.assertActionsCount(1);
		_defaultPortalCacheListener.assertUpdated(_KEY_2, _VALUE_1);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertActionsCount(0);

		// Put 4

		Assert.assertEquals(
			_VALUE_1, _ehcachePortalCache.putIfAbsent(_KEY_2, _VALUE_2));

		Assert.assertEquals(_VALUE_2, _ehcachePortalCache.get(_KEY_1));
		Assert.assertEquals(_VALUE_1, _ehcachePortalCache.get(_KEY_2));

		_defaultPortalCacheListener.assertActionsCount(0);
		_defaultPortalCacheReplicator.assertActionsCount(0);

		// Put 5

		_ehcachePortalCache.remove(_KEY_1);

		Assert.assertNull(_ehcachePortalCache.putIfAbsent(_KEY_1, _VALUE_1));

		Assert.assertEquals(_VALUE_1, _ehcachePortalCache.get(_KEY_1));
		Assert.assertEquals(_VALUE_1, _ehcachePortalCache.get(_KEY_2));

		_defaultPortalCacheListener.assertActionsCount(2);
		_defaultPortalCacheListener.assertRemoved(_KEY_1, _VALUE_2);
		_defaultPortalCacheListener.assertPut(_KEY_1, _VALUE_1);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertActionsCount(2);
		_defaultPortalCacheReplicator.assertRemoved(_KEY_1, _VALUE_2);
		_defaultPortalCacheReplicator.assertPut(_KEY_1, _VALUE_1);

		_defaultPortalCacheReplicator.reset();
	}

	@Test
	public void testRemove() {
		Assert.assertEquals(_VALUE_1, _ehcachePortalCache.get(_KEY_1));
		Assert.assertNull(_ehcachePortalCache.get(_KEY_2));

		// Remove 1

		Assert.assertTrue(_ehcachePortalCache.remove(_KEY_1, _VALUE_1));

		Assert.assertNull(_ehcachePortalCache.get(_KEY_1));
		Assert.assertNull(_ehcachePortalCache.get(_KEY_2));

		_defaultPortalCacheListener.assertActionsCount(1);
		_defaultPortalCacheListener.assertRemoved(_KEY_1, _VALUE_1);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertActionsCount(1);
		_defaultPortalCacheReplicator.assertRemoved(_KEY_1, _VALUE_1);

		_defaultPortalCacheReplicator.reset();

		// Remove 2

		_ehcachePortalCache.put(_KEY_1, _VALUE_1);
		_ehcachePortalCache.put(_KEY_2, _VALUE_2);

		_ehcachePortalCache.remove(_KEY_2);

		Assert.assertEquals(_VALUE_1, _ehcachePortalCache.get(_KEY_1));
		Assert.assertNull(_ehcachePortalCache.get(_KEY_2));

		_defaultPortalCacheListener.assertActionsCount(3);
		_defaultPortalCacheListener.assertPut(_KEY_1, _VALUE_1);
		_defaultPortalCacheListener.assertPut(_KEY_2, _VALUE_2);
		_defaultPortalCacheListener.assertRemoved(_KEY_2, _VALUE_2);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertActionsCount(3);
		_defaultPortalCacheReplicator.assertPut(_KEY_1, _VALUE_1);
		_defaultPortalCacheReplicator.assertPut(_KEY_2, _VALUE_2);
		_defaultPortalCacheReplicator.assertRemoved(_KEY_2, _VALUE_2);

		_defaultPortalCacheReplicator.reset();

		// Remove 3

		PortalCacheHelperUtil.removeWithoutReplicator(
			_ehcachePortalCache, _KEY_1);

		Assert.assertNull(_ehcachePortalCache.get(_KEY_1));
		Assert.assertNull(_ehcachePortalCache.get(_KEY_2));

		_defaultPortalCacheListener.assertActionsCount(1);
		_defaultPortalCacheListener.assertRemoved(_KEY_1, _VALUE_1);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertActionsCount(0);

		// Remove 4

		_ehcachePortalCache.put(_KEY_1, _VALUE_1);
		_ehcachePortalCache.put(_KEY_2, _VALUE_2);

		_ehcachePortalCache.removeAll();

		Assert.assertNull(_ehcachePortalCache.get(_KEY_1));
		Assert.assertNull(_ehcachePortalCache.get(_KEY_2));

		_defaultPortalCacheListener.assertActionsCount(3);
		_defaultPortalCacheListener.assertPut(_KEY_1, _VALUE_1);
		_defaultPortalCacheListener.assertPut(_KEY_2, _VALUE_2);
		_defaultPortalCacheListener.assertRemoveAll();

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertActionsCount(3);
		_defaultPortalCacheReplicator.assertPut(_KEY_1, _VALUE_1);
		_defaultPortalCacheReplicator.assertPut(_KEY_2, _VALUE_2);
		_defaultPortalCacheReplicator.assertRemoveAll();

		_defaultPortalCacheReplicator.reset();

		// Remove 5

		_ehcachePortalCache.put(_KEY_1, _VALUE_1);
		_ehcachePortalCache.put(_KEY_2, _VALUE_2);

		PortalCacheHelperUtil.removeAllWithoutReplicator(_ehcachePortalCache);

		Assert.assertNull(_ehcachePortalCache.get(_KEY_1));
		Assert.assertNull(_ehcachePortalCache.get(_KEY_2));

		_defaultPortalCacheListener.assertActionsCount(3);
		_defaultPortalCacheListener.assertPut(_KEY_1, _VALUE_1);
		_defaultPortalCacheListener.assertPut(_KEY_2, _VALUE_2);
		_defaultPortalCacheListener.assertRemoveAll();

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertActionsCount(2);
		_defaultPortalCacheReplicator.assertPut(_KEY_1, _VALUE_1);
		_defaultPortalCacheReplicator.assertPut(_KEY_2, _VALUE_2);

		_defaultPortalCacheReplicator.reset();

		// Remove logging

		_ehcachePortalCache.put(_KEY_1, _VALUE_1);
		_ehcachePortalCache.put(_KEY_2, _VALUE_2);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				PortalCacheCacheEventListener.class.getName() +
					StringPool.PERIOD +
						_ehcachePortalCache.getPortalCacheName(),
				LoggerTestUtil.DEBUG)) {

			_ehcachePortalCache.remove(_KEY_1);
			_ehcachePortalCache.remove(_KEY_2, _VALUE_2);
			_ehcachePortalCache.removeAll();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 3, logEntries.size());

			LogEntry logEntry1 = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Removed ", _KEY_1, " from ",
					_ehcachePortalCache.getPortalCacheName()),
				logEntry1.getMessage());

			LogEntry logEntry2 = logEntries.get(1);

			Assert.assertEquals(
				StringBundler.concat(
					"Removed ", _KEY_2, " from ",
					_ehcachePortalCache.getPortalCacheName()),
				logEntry2.getMessage());

			LogEntry logEntry3 = logEntries.get(2);

			Assert.assertEquals(
				"Cleared " + _ehcachePortalCache.getPortalCacheName(),
				logEntry3.getMessage());
		}
	}

	@Test
	public void testReplace() {
		Assert.assertEquals(_VALUE_1, _ehcachePortalCache.get(_KEY_1));
		Assert.assertNull(_ehcachePortalCache.get(_KEY_2));

		// Replace 1

		Assert.assertEquals(
			_VALUE_1, _ehcachePortalCache.replace(_KEY_1, _VALUE_2));

		Assert.assertEquals(_VALUE_2, _ehcachePortalCache.get(_KEY_1));
		Assert.assertNull(_ehcachePortalCache.get(_KEY_2));

		_defaultPortalCacheListener.assertActionsCount(1);
		_defaultPortalCacheListener.assertUpdated(_KEY_1, _VALUE_2);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertActionsCount(1);
		_defaultPortalCacheReplicator.assertUpdated(_KEY_1, _VALUE_2);

		_defaultPortalCacheReplicator.reset();

		// Replace 2

		Assert.assertNull(_ehcachePortalCache.replace(_KEY_2, _VALUE_2));

		Assert.assertEquals(_VALUE_2, _ehcachePortalCache.get(_KEY_1));
		Assert.assertNull(_ehcachePortalCache.get(_KEY_2));

		_defaultPortalCacheListener.assertActionsCount(0);
		_defaultPortalCacheReplicator.assertActionsCount(0);

		// Replace 3

		Assert.assertTrue(
			_ehcachePortalCache.replace(_KEY_1, _VALUE_2, _VALUE_1));

		Assert.assertEquals(_VALUE_1, _ehcachePortalCache.get(_KEY_1));
		Assert.assertNull(_ehcachePortalCache.get(_KEY_2));

		_defaultPortalCacheListener.assertActionsCount(1);
		_defaultPortalCacheListener.assertUpdated(_KEY_1, _VALUE_1);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertActionsCount(1);
		_defaultPortalCacheReplicator.assertUpdated(_KEY_1, _VALUE_1);

		_defaultPortalCacheReplicator.reset();

		// Replace 4

		Assert.assertFalse(
			_ehcachePortalCache.replace(_KEY_1, _VALUE_2, _VALUE_1));

		Assert.assertEquals(_VALUE_1, _ehcachePortalCache.get(_KEY_1));
		Assert.assertNull(_ehcachePortalCache.get(_KEY_2));

		_defaultPortalCacheListener.assertActionsCount(0);
		_defaultPortalCacheReplicator.assertActionsCount(0);
	}

	@Test
	public void testResetEhcache() {
		DCLSingleton<Cache<Object, Object>> ehcacheDCLSingleton =
			ReflectionTestUtil.getFieldValue(
				_ehcachePortalCache, "_ehcacheDCLSingleton");

		Assert.assertNotNull(
			ReflectionTestUtil.getFieldValue(
				ehcacheDCLSingleton, "_singleton"));

		_ehcachePortalCache.resetEhcache();

		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				ehcacheDCLSingleton, "_singleton"));
	}

	@Test
	public void testSerializable() {
		BaseEhcachePortalCacheManager baseEhcachePortalCacheManager =
			new BaseEhcachePortalCacheManager() {
			};

		ReflectionTestUtil.setFieldValue(
			baseEhcachePortalCacheManager, "_cacheManager", _cacheManager);

		CacheConfigurationBuilder<Serializable, Object>
			cacheConfigurationBuilder =
				CacheConfigurationBuilder.newCacheConfigurationBuilder(
					Serializable.class, Object.class,
					ResourcePoolsBuilder.heap(100));

		ReflectionTestUtil.setFieldValue(
			baseEhcachePortalCacheManager, "_defaultCacheConfiguration",
			cacheConfigurationBuilder.build());

		EhcachePortalCache<String, Object> ehcachePortalCache =
			new EhcachePortalCache<>(
				baseEhcachePortalCacheManager,
				new EhcachePortalCacheConfiguration(
					"SerializablePortalCache", null, Serializable.class,
					Object.class, true));

		Assert.assertTrue(ehcachePortalCache.isSerializable());

		List<String> keys = ehcachePortalCache.getKeys();

		Assert.assertTrue(keys.toString(), keys.isEmpty());

		Assert.assertNull(ehcachePortalCache.get(_KEY_1));

		ehcachePortalCache.put(_KEY_1, _VALUE_1);

		Assert.assertEquals(_VALUE_1, ehcachePortalCache.get(_KEY_1));

		Object nonserializableValue = new Object();

		ehcachePortalCache.put(_KEY_2, nonserializableValue);

		Assert.assertSame(nonserializableValue, ehcachePortalCache.get(_KEY_2));

		keys = ehcachePortalCache.getKeys();

		Assert.assertEquals(keys.toString(), 2, keys.size());
		Assert.assertEquals(_KEY_1, keys.get(0));
		Assert.assertEquals(_KEY_2, keys.get(1));

		ehcachePortalCache.remove(_KEY_1);

		Assert.assertNull(ehcachePortalCache.get(_KEY_1));
	}

	@Test
	public void testTimeToLive() {
		Assert.assertEquals(_VALUE_1, _ehcachePortalCache.get(_KEY_1));
		Assert.assertNull(_ehcachePortalCache.get(_KEY_2));

		int timeToLive = 600;

		Cache<Object, Object> cache =
			(Cache<Object, Object>)_ehcachePortalCache.getEhcache();

		// Put

		_ehcachePortalCache.put(_KEY_2, _VALUE_2, timeToLive);

		Assert.assertEquals(_VALUE_2, _ehcachePortalCache.get(_KEY_2));

		_defaultPortalCacheListener.assertPut(_KEY_2, _VALUE_2, timeToLive);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertPut(_KEY_2, _VALUE_2, timeToLive);

		_defaultPortalCacheReplicator.reset();

		// Put if absent

		cache.remove(_KEY_2);

		_ehcachePortalCache.putIfAbsent(_KEY_2, _VALUE_2, timeToLive);

		Assert.assertEquals(_VALUE_2, _ehcachePortalCache.get(_KEY_2));

		_defaultPortalCacheListener.assertPut(_KEY_2, _VALUE_2, timeToLive);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertPut(_KEY_2, _VALUE_2, timeToLive);

		_defaultPortalCacheReplicator.reset();

		// Replace 1

		cache.remove(_KEY_2);

		_ehcachePortalCache.replace(_KEY_1, _VALUE_2, timeToLive);

		Assert.assertEquals(_VALUE_2, _ehcachePortalCache.get(_KEY_1));

		_defaultPortalCacheListener.assertUpdated(_KEY_1, _VALUE_2, timeToLive);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertUpdated(
			_KEY_1, _VALUE_2, timeToLive);

		_defaultPortalCacheReplicator.reset();

		// Replace 2

		cache.remove(_KEY_1);

		_ehcachePortalCache.put(_KEY_1, _VALUE_1);

		_ehcachePortalCache.replace(_KEY_1, _VALUE_1, _VALUE_2, timeToLive);

		Assert.assertEquals(_VALUE_2, _ehcachePortalCache.get(_KEY_1));

		_defaultPortalCacheListener.assertPut(_KEY_1, _VALUE_1);
		_defaultPortalCacheListener.assertUpdated(_KEY_1, _VALUE_2, timeToLive);

		_defaultPortalCacheListener.reset();

		_defaultPortalCacheReplicator.assertPut(_KEY_1, _VALUE_1);
		_defaultPortalCacheReplicator.assertUpdated(
			_KEY_1, _VALUE_2, timeToLive);

		_defaultPortalCacheReplicator.reset();
	}

	private static final String _KEY_1 = "KEY_1";

	private static final String _KEY_2 = "KEY_2";

	private static final String _PORTAL_CACHE_NAME = "PORTAL_CACHE_NAME";

	private static final String _VALUE_1 = "VALUE_1";

	private static final String _VALUE_2 = "VALUE_2";

	private static CacheConfigurationBuilder<Object, Object>
		_cacheConfigurationBuilder;
	private static CacheManager _cacheManager;

	private Cache<Object, Object> _cache;
	private TestPortalCacheListener<String, String> _defaultPortalCacheListener;
	private TestPortalCacheReplicator<String, String>
		_defaultPortalCacheReplicator;
	private EhcachePortalCache<String, String> _ehcachePortalCache;

}