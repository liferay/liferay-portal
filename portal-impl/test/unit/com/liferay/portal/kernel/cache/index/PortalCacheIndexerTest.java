/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.cache.index;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheListener;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.tools.ToolDependencies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Preston Crary
 */
public class PortalCacheIndexerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		ToolDependencies.wireCaches();

		_portalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.MULTI_VM, RandomTestUtil.randomString());

		_portalCache = ReflectionTestUtil.getFieldValue(
			_portalCache, "_portalCache");

		_portalCacheIndexer = new PortalCacheIndexer<>(
			_indexEncoder, _portalCache);

		List<PortalCacheListener<?, ?>> portalCacheListeners =
			ReflectionTestUtil.getFieldValue(
				_portalCache, "_portalCacheListeners");

		_cacheListener =
			(PortalCacheListener<TestKey, String>)portalCacheListeners.get(0);

		_mappedMethodCallableInvocationHandler =
			new MappedMethodCallableInvocationHandler(
				ReflectionTestUtil.getFieldValue(
					_portalCacheIndexer, "_indexedCacheKeys"),
				true);

		ReflectionTestUtil.setFieldValue(
			_portalCacheIndexer, "_indexedCacheKeys",
			ProxyUtil.newProxyInstance(
				ClassLoader.getSystemClassLoader(),
				new Class<?>[] {ConcurrentMap.class},
				_mappedMethodCallableInvocationHandler));
	}

	@Test
	public void testAddIndexedCacheKeyConcurrentPutDifferentKeys()
		throws ReflectiveOperationException {

		_mappedMethodCallableInvocationHandler.putBeforeCallable(
			ConcurrentMap.class.getMethod(
				"putIfAbsent", Object.class, Object.class),
			new Callable<Void>() {

				@Override
				public Void call() {
					_portalCache.put(_INDEX_1_KEY_1, _VALUE);

					return null;
				}

			});

		_portalCache.put(_INDEX_1_KEY_2, _VALUE);

		assertIndexCacheSynchronization();
	}

	@Test
	public void testAddIndexedCacheKeyPutSameKey() {
		_portalCache.put(_INDEX_1_KEY_1, _VALUE);
		_portalCache.put(_INDEX_1_KEY_1, _VALUE);

		assertIndexCacheSynchronization();
	}

	@Test
	public void testAddIndexedCacheKeyWithDifferentIndex() {
		_portalCache.put(_INDEX_1_KEY_1, _VALUE);
		_portalCache.put(_INDEX_2_KEY_3, _VALUE);

		assertIndexCacheSynchronization();
	}

	@Test
	public void testAddIndexedCacheKeyWithSameIndex() {
		_portalCache.put(_INDEX_1_KEY_1, _VALUE);
		_portalCache.put(_INDEX_1_KEY_2, _VALUE);

		assertIndexCacheSynchronization();
	}

	@Test
	public void testConstructor() {
		_portalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.MULTI_VM, RandomTestUtil.randomString());

		_portalCache.put(_INDEX_1_KEY_1, _VALUE);

		_portalCacheIndexer = new PortalCacheIndexer<>(
			_indexEncoder, _portalCache);

		assertIndexCacheSynchronization();
	}

	@Test
	public void testDispose() {
		_portalCache.put(_INDEX_1_KEY_1, _VALUE);

		_portalCache.unregisterPortalCacheListeners();

		Set<TestKey> testKeys = _portalCacheIndexer.getKeys(
			_indexEncoder.encode(_INDEX_1_KEY_1));

		Assert.assertTrue(testKeys.toString(), testKeys.isEmpty());
	}

	@Test
	public void testGetIndexedCacheKeysWithIndexKey() {
		_portalCache.put(_INDEX_1_KEY_1, _VALUE);

		Set<TestKey> testKeys = _portalCacheIndexer.getKeys(
			_indexEncoder.encode(_INDEX_1_KEY_1));

		testKeys.clear();

		assertIndexCacheSynchronization();
	}

	@Test
	public void testGetIndexedCacheKeysWithoutIndexKey() {
		_portalCacheIndexer.getKeys(_indexEncoder.encode(_INDEX_1_KEY_1));

		assertIndexCacheSynchronization();
	}

	@Test
	public void testNotifyEntryEvicted() {
		_portalCache.put(_INDEX_1_KEY_1, _VALUE);

		_portalCache.remove(_INDEX_1_KEY_1);

		_cacheListener.notifyEntryEvicted(
			_portalCache, _INDEX_1_KEY_1, _VALUE, 0);

		assertIndexCacheSynchronization();
	}

	@Test
	public void testNotifyEntryExpired() {
		_portalCache.put(_INDEX_1_KEY_1, _VALUE);

		_portalCache.remove(_INDEX_1_KEY_1);

		_cacheListener.notifyEntryExpired(
			_portalCache, _INDEX_1_KEY_1, _VALUE, 0);

		assertIndexCacheSynchronization();
	}

	@Test
	public void testNotifyEntryRemoved() {
		_portalCache.put(_INDEX_1_KEY_1, _VALUE);

		_portalCache.remove(_INDEX_1_KEY_1);

		assertIndexCacheSynchronization();
	}

	@Test
	public void testNotifyEntryUpdated() {
		_portalCache.put(_INDEX_1_KEY_1, _VALUE);

		_portalCache.put(_INDEX_1_KEY_1, _VALUE);

		assertIndexCacheSynchronization();
	}

	@Test
	public void testNotifyRemoveAll() {
		_portalCache.put(_INDEX_1_KEY_1, _VALUE);
		_portalCache.put(_INDEX_1_KEY_2, _VALUE);
		_portalCache.put(_INDEX_2_KEY_3, _VALUE);

		_portalCache.removeAll();

		assertIndexCacheSynchronization();
	}

	@Test
	public void testRemoveIndexedCacheKeyConcurrentPut()
		throws ReflectiveOperationException {

		_portalCache.put(_INDEX_1_KEY_1, _VALUE);

		_mappedMethodCallableInvocationHandler.putBeforeCallable(
			ConcurrentMap.class.getMethod("remove", Object.class, Object.class),
			new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					_portalCache.put(_INDEX_1_KEY_2, _VALUE);

					return null;
				}

			});

		_portalCache.remove(_INDEX_1_KEY_1);

		assertIndexCacheSynchronization();
	}

	@Test
	public void testRemoveIndexedCacheKeyConcurrentRemove()
		throws ReflectiveOperationException {

		_portalCache.put(_INDEX_1_KEY_1, _VALUE);

		_mappedMethodCallableInvocationHandler.putBeforeCallable(
			ConcurrentMap.class.getMethod("remove", Object.class, Object.class),
			new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					_portalCacheIndexer.removeKeys(1L);

					return null;
				}

			});

		_portalCache.remove(_INDEX_1_KEY_1);

		assertIndexCacheSynchronization();
	}

	@Test
	public void testRemoveIndexedCacheKeyWithKey() {
		_portalCache.put(_INDEX_1_KEY_1, _VALUE);
		_portalCache.put(_INDEX_1_KEY_2, _VALUE);

		_portalCache.remove(_INDEX_1_KEY_1);

		assertIndexCacheSynchronization();
	}

	@Test
	public void testRemoveIndexedCacheKeyWithoutKey() {
		_portalCache.put(_INDEX_1_KEY_1, _VALUE);

		_portalCache.remove(_INDEX_1_KEY_2);

		assertIndexCacheSynchronization();
	}

	@Test
	public void testRemoveIndexedCacheKeysWithIndex() {
		_portalCache.put(_INDEX_1_KEY_1, _VALUE);

		_portalCacheIndexer.removeKeys(_indexEncoder.encode(_INDEX_1_KEY_1));

		assertIndexCacheSynchronization();
	}

	@Test
	public void testRemoveIndexedCacheKeysWithoutIndex() {
		_portalCache.put(_INDEX_1_KEY_1, _VALUE);

		_portalCacheIndexer.removeKeys(_indexEncoder.encode(_INDEX_2_KEY_3));

		assertIndexCacheSynchronization();
	}

	protected void assertIndexCacheSynchronization() {
		Set<TestKey> expectedTestKeys = new HashSet<>(_portalCache.getKeys());

		Set<Long> indexes = new HashSet<>();

		for (TestKey testKey : expectedTestKeys) {
			indexes.add(_indexEncoder.encode(testKey));
		}

		Set<TestKey> actualTestKeys = new HashSet<>();

		for (Long index : indexes) {
			actualTestKeys.addAll(_portalCacheIndexer.getKeys(index));
		}

		Assert.assertEquals(expectedTestKeys, actualTestKeys);
	}

	private static final TestKey _INDEX_1_KEY_1 = new TestKey(1L, 1L);

	private static final TestKey _INDEX_1_KEY_2 = new TestKey(1L, 2L);

	private static final TestKey _INDEX_2_KEY_3 = new TestKey(2L, 3L);

	private static final String _VALUE = "VALUE";

	private static final IndexEncoder<Long, TestKey> _indexEncoder =
		new TestKeyIndexEncoder();

	private PortalCacheListener<TestKey, String> _cacheListener;
	private MappedMethodCallableInvocationHandler
		_mappedMethodCallableInvocationHandler;
	private PortalCache<TestKey, String> _portalCache;
	private PortalCacheIndexer<Long, TestKey, String> _portalCacheIndexer;

	private class MappedMethodCallableInvocationHandler
		implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			Callable<?> beforeCallable = null;

			if (_removeOnCall) {
				beforeCallable = _beforeCallables.remove(method);
			}
			else {
				beforeCallable = _beforeCallables.get(method);
			}

			if (beforeCallable != null) {
				beforeCallable.call();
			}

			try {
				return method.invoke(_instance, args);
			}
			catch (InvocationTargetException invocationTargetException) {
				throw invocationTargetException.getTargetException();
			}
			finally {
				Callable<?> afterCallable = null;

				if (_removeOnCall) {
					afterCallable = _afterCallables.remove(method);
				}
				else {
					afterCallable = _afterCallables.get(method);
				}

				if (afterCallable != null) {
					afterCallable.call();
				}
			}
		}

		public void putAfterCallable(Method method, Callable<?> callable) {
			_afterCallables.put(method, callable);
		}

		public void putBeforeCallable(Method method, Callable<?> callable) {
			_beforeCallables.put(method, callable);
		}

		private MappedMethodCallableInvocationHandler(
			Object instance, boolean removeOnCall) {

			_instance = instance;
			_removeOnCall = removeOnCall;
		}

		private final Map<Method, Callable<?>> _afterCallables =
			new HashMap<>();
		private final Map<Method, Callable<?>> _beforeCallables =
			new HashMap<>();
		private final Object _instance;
		private final boolean _removeOnCall;

	}

}