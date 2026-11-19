/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.multiple.internal.cluster.link.messaging;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.cache.TransactionalPortalCache;
import com.liferay.portal.cache.multiple.internal.PortalCacheClusterEvent;
import com.liferay.portal.cache.multiple.internal.PortalCacheClusterEventType;
import com.liferay.portal.cache.multiple.internal.cluster.link.ClusterLinkMessageUtil;
import com.liferay.portal.cache.test.util.TestPortalCache;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerListener;
import com.liferay.portal.kernel.cache.transactional.TransactionalPortalCacheUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class ClusterLinkPortalCacheClusterListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_clusterLinkPortalCacheClusterListener, "_serviceTrackerMap",
			new ServiceTrackerMap
				<String, PortalCacheManager<? extends Serializable, ?>>() {

				@Override
				public void close() {
				}

				@Override
				public boolean containsKey(String key) {
					return _PORTAL_CACHE_MANAGER_NAME.equals(key);
				}

				@Override
				public PortalCacheManager<? extends Serializable, ?> getService(
					String key) {

					if (_PORTAL_CACHE_MANAGER_NAME.equals(key)) {
						return _portalCacheManager;
					}

					return null;
				}

				@Override
				public Set<String> keySet() {
					return Collections.singleton(_PORTAL_CACHE_MANAGER_NAME);
				}

				@Override
				public Collection<PortalCacheManager<? extends Serializable, ?>>
					values() {

					return Collections.singletonList(_portalCacheManager);
				}

			});
	}

	@Test
	public void testReceivePutWithNullValue() throws Exception {
		TestPortalCache<String, String> testPortalCache = new TestPortalCache<>(
			_PORTAL_CACHE_NAME);

		_portalCache = new TransactionalPortalCache<>(testPortalCache, false);

		testPortalCache.put(_KEY, _VALUE_1);

		TransactionalPortalCacheUtil.preparePut(testPortalCache, _KEY);

		_receive(PortalCacheClusterEventType.PUT, _KEY, null);

		Assert.assertFalse(
			TransactionalPortalCacheUtil.completePut(
				testPortalCache, _KEY, _VALUE_1));
		Assert.assertNull(testPortalCache.get(_KEY));
	}

	@Test
	public void testReceivePutWithValue() throws Exception {
		TestPortalCache<String, String> testPortalCache = new TestPortalCache<>(
			_PORTAL_CACHE_NAME);

		_portalCache = new TransactionalPortalCache<>(testPortalCache, false);

		TransactionalPortalCacheUtil.preparePut(testPortalCache, _KEY);

		_receive(PortalCacheClusterEventType.PUT, _KEY, _VALUE_2);

		Assert.assertFalse(
			TransactionalPortalCacheUtil.completePut(
				testPortalCache, _KEY, _VALUE_1));
		Assert.assertEquals(_VALUE_2, testPortalCache.get(_KEY));
	}

	@Test
	public void testReceiveRemove() throws Exception {
		List<Boolean> completePutResults = new ArrayList<>();

		TestPortalCache<String, String> testPortalCache = new TestPortalCache<>(
			_PORTAL_CACHE_NAME) {

			@Override
			protected void doRemove(String key) {
				completePutResults.add(
					TransactionalPortalCacheUtil.completePut(
						this, key, _VALUE_1));

				super.doRemove(key);
			}

		};

		_portalCache = new TransactionalPortalCache<>(testPortalCache, false);

		TransactionalPortalCacheUtil.preparePut(testPortalCache, _KEY);

		_receive(PortalCacheClusterEventType.REMOVE, _KEY, null);

		Assert.assertEquals(
			Collections.singletonList(Boolean.FALSE), completePutResults);
		Assert.assertNull(testPortalCache.get(_KEY));
	}

	@Test
	public void testReceiveRemoveAll() throws Exception {
		TestPortalCache<String, String> testPortalCache = new TestPortalCache<>(
			_PORTAL_CACHE_NAME);

		_portalCache = new TransactionalPortalCache<>(testPortalCache, false);

		testPortalCache.put(_KEY, _VALUE_1);

		TransactionalPortalCacheUtil.preparePut(testPortalCache, _KEY);

		_receive(PortalCacheClusterEventType.REMOVE_ALL, null, null);

		Assert.assertFalse(
			TransactionalPortalCacheUtil.completePut(
				testPortalCache, _KEY, _VALUE_1));
		Assert.assertNull(testPortalCache.get(_KEY));
	}

	@Test
	public void testReceiveRemoveWithMVCCPortalCache() throws Exception {
		TestPortalCache<String, String> testPortalCache = new TestPortalCache<>(
			_PORTAL_CACHE_NAME) {

			@Override
			public boolean isMVCC() {
				return true;
			}

		};

		_portalCache = new TransactionalPortalCache<>(testPortalCache, true);

		TransactionalPortalCacheUtil.preparePut(testPortalCache, _KEY);

		_receive(PortalCacheClusterEventType.REMOVE, _KEY, null);

		Assert.assertTrue(
			TransactionalPortalCacheUtil.completePut(
				testPortalCache, _KEY, _VALUE_1));
		Assert.assertEquals(_VALUE_1, testPortalCache.get(_KEY));
	}

	private void _receive(
			PortalCacheClusterEventType portalCacheClusterEventType, String key,
			String value)
		throws Exception {

		PortalCacheClusterEvent portalCacheClusterEvent =
			new PortalCacheClusterEvent(
				_PORTAL_CACHE_MANAGER_NAME, _PORTAL_CACHE_NAME, key,
				portalCacheClusterEventType);

		portalCacheClusterEvent.setElementValue(value);

		Message message = new Message();

		ClusterLinkMessageUtil.populateMessageFromPortalCacheClusterEvent(
			message, portalCacheClusterEvent);

		_clusterLinkPortalCacheClusterListener.receive(message);
	}

	private static final String _KEY = RandomTestUtil.randomString();

	private static final String _PORTAL_CACHE_MANAGER_NAME =
		RandomTestUtil.randomString();

	private static final String _PORTAL_CACHE_NAME =
		RandomTestUtil.randomString();

	private static final String _VALUE_1 = RandomTestUtil.randomString();

	private static final String _VALUE_2 = RandomTestUtil.randomString();

	private final ClusterLinkPortalCacheClusterListener
		_clusterLinkPortalCacheClusterListener =
			new ClusterLinkPortalCacheClusterListener();
	private PortalCache<String, String> _portalCache;

	private final PortalCacheManager<String, String> _portalCacheManager =
		new PortalCacheManager<String, String>() {

			@Override
			public void clearAll() {
			}

			@Override
			public void destroy() {
			}

			@Override
			public PortalCache<String, String> fetchPortalCache(
				String portalCacheName) {

				if (_PORTAL_CACHE_NAME.equals(portalCacheName)) {
					return _portalCache;
				}

				return null;
			}

			@Override
			public PortalCache<String, String> getPortalCache(
				String portalCacheName) {

				return fetchPortalCache(portalCacheName);
			}

			@Override
			public PortalCache<String, String> getPortalCache(
				String portalCacheName, boolean mvcc) {

				return fetchPortalCache(portalCacheName);
			}

			@Override
			public PortalCache<String, String> getPortalCache(
				String portalCacheName, boolean mvcc, boolean sharded) {

				return fetchPortalCache(portalCacheName);
			}

			@Override
			public Set<PortalCacheManagerListener>
				getPortalCacheManagerListeners() {

				return Collections.emptySet();
			}

			@Override
			public String getPortalCacheManagerName() {
				return _PORTAL_CACHE_MANAGER_NAME;
			}

			@Override
			public void reconfigurePortalCaches(
				URL configurationURL, ClassLoader classLoader) {
			}

			@Override
			public boolean registerPortalCacheManagerListener(
				PortalCacheManagerListener portalCacheManagerListener) {

				return false;
			}

			@Override
			public void removePortalCache(String portalCacheName) {
			}

			@Override
			public void removePortalCaches(long companyId) {
			}

			@Override
			public boolean unregisterPortalCacheManagerListener(
				PortalCacheManagerListener portalCacheManagerListener) {

				return false;
			}

			@Override
			public void unregisterPortalCacheManagerListeners() {
			}

		};

}