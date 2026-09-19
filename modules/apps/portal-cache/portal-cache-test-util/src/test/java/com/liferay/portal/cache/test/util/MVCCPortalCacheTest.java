/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.test.util;

import com.liferay.portal.cache.MVCCPortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.util.List;
import java.util.concurrent.Semaphore;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class MVCCPortalCacheTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new CodeCoverageAssertor() {

				@Override
				public void appendAssertClasses(List<Class<?>> assertClasses) {
					assertClasses.add(MVCCPortalCache.class);
				}

			},
			LiferayUnitTestRule.INSTANCE);

	@Before
	public void setUp() {
		_mvccTestPortalCache = new MVCCTestPortalCache<>(_PORTAL_CACHE_NAME);

		_mvccPortalCache = new MVCCPortalCache<>(_mvccTestPortalCache);

		_testPortalCacheListener = new TestPortalCacheListener<>();

		_mvccTestPortalCache.registerPortalCacheListener(
			_testPortalCacheListener);

		_testPortalCacheReplicator = new TestPortalCacheReplicator<>();

		_mvccTestPortalCache.registerPortalCacheListener(
			_testPortalCacheReplicator);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testForHiddenBridge() {
		@SuppressWarnings("rawtypes")
		MVCCPortalCache mvccPortalCache = new MVCCPortalCache(
			new TestPortalCache(_PORTAL_CACHE_NAME));

		Serializable key = _KEY_1;
		MockMVCCModel value = new MockMVCCModel(_VERSION_1);

		mvccPortalCache.put(key, value);
		mvccPortalCache.put(key, value, 10);
	}

	@Test
	public void testMVCCCacheWithConcurrent() throws Exception {
		Assert.assertNull(_mvccPortalCache.get(_KEY_1));
		Assert.assertNull(_mvccPortalCache.get(_KEY_2));

		// Concurrent put 1

		_mvccTestPortalCache.block();

		Thread thread1 = new Thread() {

			@Override
			public void run() {
				_mvccPortalCache.put(_KEY_1, new MockMVCCModel(_VERSION_1));
			}

		};

		thread1.start();

		_mvccTestPortalCache.waitUntilBlock(1);

		Thread thread2 = new Thread() {

			@Override
			public void run() {
				_mvccPortalCache.put(_KEY_1, new MockMVCCModel(_VERSION_1));
			}

		};

		thread2.start();

		_mvccTestPortalCache.waitUntilBlock(2);

		_mvccTestPortalCache.unblock(2);

		thread1.join();
		thread2.join();

		_assertVersion(_VERSION_1, _mvccPortalCache.get(_KEY_1));
		Assert.assertNull(_mvccPortalCache.get(_KEY_2));

		_testPortalCacheListener.assertActionsCount(2);
		_testPortalCacheListener.assertPut(
			_KEY_1, new MockMVCCModel(_VERSION_1));

		_testPortalCacheListener.reset();

		_testPortalCacheReplicator.assertActionsCount(2);
		_testPortalCacheReplicator.assertPut(
			_KEY_1, new MockMVCCModel(_VERSION_1));

		_testPortalCacheReplicator.reset();

		// Concurrent put 2

		_mvccTestPortalCache.block();

		thread1 = new Thread() {

			@Override
			public void run() {
				PortalCacheHelperUtil.putWithoutReplicator(
					_mvccPortalCache, _KEY_1, new MockMVCCModel(_VERSION_2));
			}

		};

		thread1.start();

		_mvccTestPortalCache.waitUntilBlock(1);

		thread2 = new Thread() {

			@Override
			public void run() {
				PortalCacheHelperUtil.putWithoutReplicator(
					_mvccPortalCache, _KEY_1, new MockMVCCModel(_VERSION_2));
			}

		};

		thread2.start();

		_mvccTestPortalCache.waitUntilBlock(2);

		_mvccTestPortalCache.unblock(2);

		thread1.join();
		thread2.join();

		_assertVersion(_VERSION_2, _mvccPortalCache.get(_KEY_1));
		Assert.assertNull(_mvccPortalCache.get(_KEY_2));

		_testPortalCacheListener.assertActionsCount(2);
		_testPortalCacheListener.assertUpdated(
			_KEY_1, new MockMVCCModel(_VERSION_2));

		_testPortalCacheListener.reset();

		_testPortalCacheReplicator.assertActionsCount(0);
	}

	@Test
	public void testMVCCCacheWithTTL() {
		doTestMVCCCache(true);
	}

	@Test
	public void testMVCCCacheWithoutTTL() {
		doTestMVCCCache(false);
	}

	@Test
	public void testPutWithSameVersion() {
		MVCCPortalCache<Serializable, MockMVCCModel> mvccPortalCache =
			new MVCCPortalCache<>(new TestPortalCache<>(_PORTAL_CACHE_NAME));

		Serializable key = _KEY_1;
		MockMVCCModel mockMVCCModel1 = new MockMVCCModel(_VERSION_0);

		mvccPortalCache.put(key, mockMVCCModel1);

		Assert.assertSame(mockMVCCModel1, mvccPortalCache.get(key));

		MockMVCCModel mockMVCCModel2 = new MockMVCCModel(_VERSION_0);

		mvccPortalCache.put(key, mockMVCCModel2);

		Assert.assertSame(mockMVCCModel2, mvccPortalCache.get(key));
	}

	protected void doTestMVCCCache(boolean timeToLive) {
		Assert.assertNull(_mvccPortalCache.get(_KEY_1));
		Assert.assertNull(_mvccPortalCache.get(_KEY_2));
		Assert.assertTrue(_mvccPortalCache.isMVCC());

		// Put 1

		if (timeToLive) {
			_mvccPortalCache.put(_KEY_1, new MockMVCCModel(_VERSION_1), 10);
		}
		else {
			_mvccPortalCache.put(_KEY_1, new MockMVCCModel(_VERSION_1));
		}

		_assertVersion(_VERSION_1, _mvccPortalCache.get(_KEY_1));
		Assert.assertNull(_mvccPortalCache.get(_KEY_2));

		_testPortalCacheListener.assertActionsCount(1);

		if (timeToLive) {
			_testPortalCacheListener.assertPut(
				_KEY_1, new MockMVCCModel(_VERSION_1), 10);
		}
		else {
			_testPortalCacheListener.assertPut(
				_KEY_1, new MockMVCCModel(_VERSION_1));
		}

		_testPortalCacheListener.reset();

		_testPortalCacheReplicator.assertActionsCount(1);

		if (timeToLive) {
			_testPortalCacheReplicator.assertPut(
				_KEY_1, new MockMVCCModel(_VERSION_1), 10);
		}
		else {
			_testPortalCacheReplicator.assertPut(
				_KEY_1, new MockMVCCModel(_VERSION_1));
		}

		_testPortalCacheReplicator.reset();

		// Put 2

		if (timeToLive) {
			_mvccPortalCache.put(_KEY_1, new MockMVCCModel(_VERSION_0), 10);
		}
		else {
			_mvccPortalCache.put(_KEY_1, new MockMVCCModel(_VERSION_0));
		}

		_assertVersion(_VERSION_1, _mvccPortalCache.get(_KEY_1));
		Assert.assertNull(_mvccPortalCache.get(_KEY_2));

		_testPortalCacheListener.assertActionsCount(0);
		_testPortalCacheReplicator.assertActionsCount(0);

		// Put 3

		if (timeToLive) {
			_mvccPortalCache.put(_KEY_1, new MockMVCCModel(_VERSION_2), 10);
		}
		else {
			_mvccPortalCache.put(_KEY_1, new MockMVCCModel(_VERSION_2));
		}

		_assertVersion(_VERSION_2, _mvccPortalCache.get(_KEY_1));
		Assert.assertNull(_mvccPortalCache.get(_KEY_2));

		_testPortalCacheListener.assertActionsCount(1);

		if (timeToLive) {
			_testPortalCacheListener.assertUpdated(
				_KEY_1, new MockMVCCModel(_VERSION_2), 10);
		}
		else {
			_testPortalCacheListener.assertUpdated(
				_KEY_1, new MockMVCCModel(_VERSION_2));
		}

		_testPortalCacheListener.reset();

		_testPortalCacheReplicator.assertActionsCount(1);

		if (timeToLive) {
			_testPortalCacheReplicator.assertUpdated(
				_KEY_1, new MockMVCCModel(_VERSION_2), 10);
		}
		else {
			_testPortalCacheReplicator.assertUpdated(
				_KEY_1, new MockMVCCModel(_VERSION_2));
		}

		_testPortalCacheReplicator.reset();
	}

	private void _assertVersion(long version, MockMVCCModel mockMVCCModel) {
		Assert.assertEquals(version, mockMVCCModel.getMvccVersion());
	}

	private static final String _KEY_1 = "KEY_1";

	private static final String _KEY_2 = "KEY_2";

	private static final String _PORTAL_CACHE_NAME = "PORTAL_CACHE_NAME";

	private static final long _VERSION_0 = 0;

	private static final long _VERSION_1 = 1;

	private static final long _VERSION_2 = 2;

	private MVCCPortalCache<String, MockMVCCModel> _mvccPortalCache;
	private MVCCTestPortalCache<String, MockMVCCModel> _mvccTestPortalCache;
	private TestPortalCacheListener<String, MockMVCCModel>
		_testPortalCacheListener;
	private TestPortalCacheReplicator<String, MockMVCCModel>
		_testPortalCacheReplicator;

	private class MVCCTestPortalCache<K extends Serializable, V>
		extends TestPortalCache<K, V> {

		public void block() {
			_semaphore = new Semaphore(0);
		}

		public void unblock(int permits) {
			Semaphore semaphore = _semaphore;

			_semaphore = null;

			semaphore.release(permits);
		}

		public void waitUntilBlock(int threadCount) {
			Semaphore semaphore = _semaphore;

			if (semaphore != null) {
				while (semaphore.getQueueLength() < threadCount);
			}
		}

		@Override
		protected V doPutIfAbsent(K key, V value, int timeToLive) {
			Semaphore semaphore = _semaphore;

			if (semaphore != null) {
				try {
					semaphore.acquire();
				}
				catch (Exception exception) {
					throw new IllegalStateException(exception);
				}
			}

			return super.doPutIfAbsent(key, value, timeToLive);
		}

		@Override
		protected boolean doReplace(
			K key, V oldValue, V newValue, int timeToLive) {

			Semaphore semaphore = _semaphore;

			if (semaphore != null) {
				try {
					semaphore.acquire();
				}
				catch (Exception exception) {
					throw new IllegalStateException(exception);
				}
			}

			return super.doReplace(key, oldValue, newValue, timeToLive);
		}

		private MVCCTestPortalCache(String portalCacheName) {
			super(portalCacheName);
		}

		private volatile Semaphore _semaphore;

	}

}