/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools;

import com.liferay.portal.cache.key.SimpleCacheKeyGenerator;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheException;
import com.liferay.portal.kernel.cache.PortalCacheListener;
import com.liferay.portal.kernel.cache.PortalCacheListenerScope;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerListener;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.PortalCacheManagerProvider;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.cache.key.CacheKeyGeneratorUtil;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.auth.DefaultFullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.model.DefaultModelHintsImpl;
import com.liferay.portal.security.permission.ResourceActionsImpl;
import com.liferay.portal.security.xml.SecureXMLFactoryProviderImpl;
import com.liferay.portal.util.FastDateFormatFactoryImpl;
import com.liferay.portal.util.FileImpl;
import com.liferay.portal.util.InitUtil;
import com.liferay.portal.util.PortalImpl;
import com.liferay.portal.xml.SAXReaderImpl;

import java.io.Serializable;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.BundleContext;

/**
 * @author Raymond Augé
 */
public class ToolDependencies {

	public static void wireBasic() {
		InitUtil.init();

		wireCaches();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		bundleContext.registerService(
			FriendlyURLNormalizer.class,
			(FriendlyURLNormalizer)ProxyUtil.newProxyInstance(
				ToolDependencies.class.getClassLoader(),
				new Class<?>[] {FriendlyURLNormalizer.class},
				(proxy, method, args) -> null),
			null);
		bundleContext.registerService(
			FullNameGenerator.class, DefaultFullNameGenerator.INSTANCE, null);

		CacheKeyGeneratorUtil cacheKeyGeneratorUtil =
			new CacheKeyGeneratorUtil();

		cacheKeyGeneratorUtil.setDefaultCacheKeyGenerator(
			new SimpleCacheKeyGenerator());

		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());

		FileUtil fileUtil = new FileUtil();

		fileUtil.setFile(new FileImpl());

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		SAXReaderUtil saxReaderUtil = new SAXReaderUtil();

		SAXReaderImpl secureSAXReaderImpl = new SAXReaderImpl();

		secureSAXReaderImpl.setSecure(true);

		saxReaderUtil.setSAXReader(secureSAXReaderImpl);

		SecureXMLFactoryProviderUtil secureXMLFactoryProviderUtil =
			new SecureXMLFactoryProviderUtil();

		secureXMLFactoryProviderUtil.setSecureXMLFactoryProvider(
			new SecureXMLFactoryProviderImpl());

		UnsecureSAXReaderUtil unsecureSAXReaderUtil =
			new UnsecureSAXReaderUtil();

		unsecureSAXReaderUtil.setSAXReader(new SAXReaderImpl());

		// DefaultModelHintsImpl requires SecureXMLFactoryProviderUtil

		ModelHintsUtil modelHintsUtil = new ModelHintsUtil();

		DefaultModelHintsImpl defaultModelHintsImpl =
			new DefaultModelHintsImpl();

		defaultModelHintsImpl.afterPropertiesSet();

		modelHintsUtil.setModelHints(defaultModelHintsImpl);
	}

	public static void wireCaches() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		bundleContext.registerService(
			PortalCacheManager.class,
			new TestPortalCacheManager<>(PortalCacheManagerNames.MULTI_VM),
			null);
		bundleContext.registerService(
			PortalCacheManager.class,
			new TestPortalCacheManager<>(PortalCacheManagerNames.SINGLE_VM),
			null);

		bundleContext.registerService(
			MultiVMPool.class, new TestMultiVMPool(), null);
		bundleContext.registerService(
			SingleVMPool.class, new TestSingleVMPool(), null);
	}

	public static void wireDeployers() {
		wireBasic();

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());
	}

	public static void wireServiceBuilder() {
		wireDeployers();

		ResourceActionsUtil resourceActionsUtil = new ResourceActionsUtil();

		resourceActionsUtil.setResourceActions(new ResourceActionsImpl());
	}

	private static class TestMultiVMPool implements MultiVMPool {

		@Override
		public void clear() {
			_portalCacheManager.clearAll();
		}

		@Override
		public PortalCache<? extends Serializable, ? extends Serializable>
			getPortalCache(String portalCacheName) {

			return _portalCacheManager.getPortalCache(portalCacheName);
		}

		@Override
		public PortalCache<? extends Serializable, ? extends Serializable>
			getPortalCache(String portalCacheName, boolean mvcc) {

			return getPortalCache(portalCacheName);
		}

		@Override
		public PortalCache<? extends Serializable, ? extends Serializable>
			getPortalCache(
				String portalCacheName, boolean mvcc, boolean sharded) {

			return getPortalCache(portalCacheName);
		}

		@Override
		public PortalCacheManager
			<? extends Serializable, ? extends Serializable>
				getPortalCacheManager() {

			return _portalCacheManager;
		}

		@Override
		public void removePortalCache(String portalCacheName) {
			_portalCacheManager.removePortalCache(portalCacheName);
		}

		private final PortalCacheManager
			<? extends Serializable, ? extends Serializable>
				_portalCacheManager =
					(PortalCacheManager
						<? extends Serializable, ? extends Serializable>)
							PortalCacheManagerProvider.getPortalCacheManager(
								PortalCacheManagerNames.MULTI_VM);

	}

	private static class TestPortalCache<K extends Serializable, V>
		implements PortalCache<K, V> {

		public TestPortalCache(String portalCacheName) {
			_portalCacheName = portalCacheName;
		}

		@Override
		public V get(K key) {
			return _map.get(key);
		}

		@Override
		public List<K> getKeys() {
			return new ArrayList<>(_map.keySet());
		}

		@Override
		public PortalCacheManager<K, V> getPortalCacheManager() {
			return null;
		}

		@Override
		public String getPortalCacheName() {
			return _portalCacheName;
		}

		public boolean isMVCC() {
			return false;
		}

		@Override
		public boolean isSharded() {
			return false;
		}

		@Override
		public void put(K key, V value) {
			put(key, value, DEFAULT_TIME_TO_LIVE);
		}

		@Override
		public void put(K key, V value, int timeToLive) {
			V oldValue = _map.put(key, value);

			for (PortalCacheListener<K, V> portalCacheListener :
					_portalCacheListeners) {

				if (oldValue != null) {
					portalCacheListener.notifyEntryUpdated(
						this, key, value, timeToLive);
				}
				else {
					portalCacheListener.notifyEntryPut(
						this, key, value, timeToLive);
				}
			}
		}

		@Override
		public void registerPortalCacheListener(
			PortalCacheListener<K, V> portalCacheListener) {

			_portalCacheListeners.add(portalCacheListener);
		}

		@Override
		public void registerPortalCacheListener(
			PortalCacheListener<K, V> portalCacheListener,
			PortalCacheListenerScope portalCacheListenerScope) {

			_portalCacheListeners.add(portalCacheListener);
		}

		@Override
		public void remove(K key) {
			_map.remove(key);

			for (PortalCacheListener<K, V> portalCacheListener :
					_portalCacheListeners) {

				portalCacheListener.notifyEntryRemoved(
					this, key, null, DEFAULT_TIME_TO_LIVE);
			}
		}

		@Override
		public void removeAll() {
			_map.clear();

			for (PortalCacheListener<K, V> portalCacheListener :
					_portalCacheListeners) {

				portalCacheListener.notifyRemoveAll(this);
			}
		}

		@Override
		public void unregisterPortalCacheListener(
			PortalCacheListener<K, V> portalCacheListener) {

			portalCacheListener.dispose();

			_portalCacheListeners.remove(portalCacheListener);
		}

		@Override
		public void unregisterPortalCacheListeners() {
			for (PortalCacheListener<K, V> portalCacheListener :
					_portalCacheListeners) {

				portalCacheListener.dispose();
			}

			_portalCacheListeners.clear();
		}

		private final Map<K, V> _map = new ConcurrentHashMap<>();
		private final List<PortalCacheListener<K, V>> _portalCacheListeners =
			new ArrayList<>();
		private final String _portalCacheName;

	}

	private static class TestPortalCacheManager<K extends Serializable, V>
		implements PortalCacheManager<K, V> {

		@Override
		public void clearAll() throws PortalCacheException {
			for (PortalCache<K, V> portalCache : _portalCaches.values()) {
				portalCache.removeAll();
			}
		}

		@Override
		public void destroy() {
			_portalCaches.clear();
		}

		public PortalCache<K, V> fetchPortalCache(String portalCacheName) {
			return _portalCaches.get(portalCacheName);
		}

		@Override
		public PortalCache<K, V> getPortalCache(String portalCacheName)
			throws PortalCacheException {

			return getPortalCache(portalCacheName, false);
		}

		@Override
		public PortalCache<K, V> getPortalCache(
				String portalCacheName, boolean mvcc)
			throws PortalCacheException {

			return getPortalCache(
				portalCacheName, mvcc, DBPartition.isPartitionEnabled());
		}

		@Override
		public PortalCache<K, V> getPortalCache(
				String portalCacheName, boolean mvcc, boolean sharded)
			throws PortalCacheException {

			PortalCache<K, V> portalCache = _portalCaches.get(portalCacheName);

			if (portalCache != null) {
				return portalCache;
			}

			portalCache = new TestPortalCache<>(portalCacheName);

			PortalCache<K, V> previousPortalCache = _portalCaches.putIfAbsent(
				portalCacheName, portalCache);

			if (previousPortalCache != null) {
				return previousPortalCache;
			}

			return portalCache;
		}

		@Override
		public Set<PortalCacheManagerListener>
			getPortalCacheManagerListeners() {

			return Collections.emptySet();
		}

		@Override
		public String getPortalCacheManagerName() {
			return _portalCacheManagerName;
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
			_portalCaches.remove(portalCacheName);
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

		private TestPortalCacheManager(String portalCacheManagerName) {
			_portalCacheManagerName = portalCacheManagerName;
		}

		private final String _portalCacheManagerName;
		private final ConcurrentMap<String, PortalCache<K, V>> _portalCaches =
			new ConcurrentHashMap<>();

	}

	private static class TestSingleVMPool implements SingleVMPool {

		@Override
		public void clear() {
			_portalCacheManager.clearAll();
		}

		@Override
		public PortalCache<? extends Serializable, ?> getPortalCache(
			String portalCacheName) {

			return _portalCacheManager.getPortalCache(portalCacheName);
		}

		@Override
		public PortalCacheManager<? extends Serializable, ?>
			getPortalCacheManager() {

			return _portalCacheManager;
		}

		@Override
		public void removePortalCache(String portalCacheName) {
			_portalCacheManager.removePortalCache(portalCacheName);
		}

		private final PortalCacheManager<? extends Serializable, ?>
			_portalCacheManager =
				PortalCacheManagerProvider.getPortalCacheManager(
					PortalCacheManagerNames.SINGLE_VM);

	}

}