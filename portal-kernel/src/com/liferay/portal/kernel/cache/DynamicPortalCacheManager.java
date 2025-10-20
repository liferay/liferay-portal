/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.cache;

import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ProxyFactory;

import java.io.Serializable;

import java.net.URL;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Tina Tian
 */
public class DynamicPortalCacheManager<K extends Serializable, V>
	implements PortalCacheManager<K, V> {

	public DynamicPortalCacheManager(String portalCacheManagerName) {
		_portalCacheManagerName = portalCacheManagerName;

		_portalCacheManager =
			(PortalCacheManager<K, V>)_DUMMY_PORTAL_CACHE_MANAGER;
	}

	@Override
	public void clearAll() throws PortalCacheException {
		_portalCacheManager.clearAll();
	}

	@Override
	public void destroy() {
		_dynamicPortalCaches.clear();
	}

	@Override
	public PortalCache<K, V> fetchPortalCache(String portalCacheName) {
		return _dynamicPortalCaches.computeIfAbsent(
			portalCacheName,
			key -> {
				PortalCache<K, V> portalCache =
					_portalCacheManager.fetchPortalCache(portalCacheName);

				if (portalCache == null) {
					return null;
				}

				return new DynamicPortalCache<>(
					this, portalCache, key, portalCache.isMVCC(),
					portalCache.isSharded());
			});
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
			portalCacheName, mvcc, PropsValues.DATABASE_PARTITION_ENABLED);
	}

	@Override
	public PortalCache<K, V> getPortalCache(
			String portalCacheName, boolean mvcc, boolean sharded)
		throws PortalCacheException {

		return _dynamicPortalCaches.computeIfAbsent(
			portalCacheName,
			key -> new DynamicPortalCache<>(
				this, _portalCacheManager.getPortalCache(key, mvcc, sharded),
				key, mvcc, sharded));
	}

	@Override
	public Set<PortalCacheManagerListener> getPortalCacheManagerListeners() {
		PortalCacheManager<K, V> portalCacheManager = _portalCacheManager;

		if (portalCacheManager == _DUMMY_PORTAL_CACHE_MANAGER) {
			return Collections.unmodifiableSet(_portalCacheManagerListeners);
		}

		return portalCacheManager.getPortalCacheManagerListeners();
	}

	@Override
	public String getPortalCacheManagerName() {
		return _portalCacheManagerName;
	}

	@Override
	public void reconfigurePortalCaches(
		URL configurationURL, ClassLoader classLoader) {

		PortalCacheManager<K, V> portalCacheManager = _portalCacheManager;

		if (portalCacheManager == _DUMMY_PORTAL_CACHE_MANAGER) {
			throw new UnsupportedOperationException(
				"This method is not supported because real portal cache " +
					"manager is missing now, please retry later");
		}

		portalCacheManager.reconfigurePortalCaches(
			configurationURL, classLoader);
	}

	@Override
	public boolean registerPortalCacheManagerListener(
		PortalCacheManagerListener portalCacheManagerListener) {

		PortalCacheManager<K, V> portalCacheManager = _portalCacheManager;

		if ((portalCacheManager == _DUMMY_PORTAL_CACHE_MANAGER) ||
			portalCacheManager.registerPortalCacheManagerListener(
				portalCacheManagerListener)) {

			_portalCacheManagerListeners.add(portalCacheManagerListener);

			return true;
		}

		return false;
	}

	@Override
	public void removePortalCache(String portalCacheName) {
		_dynamicPortalCaches.remove(portalCacheName);

		_portalCacheManager.removePortalCache(portalCacheName);
	}

	@Override
	public void removePortalCaches(long companyId) {
		_portalCacheManager.removePortalCaches(companyId);
	}

	@Override
	public boolean unregisterPortalCacheManagerListener(
		PortalCacheManagerListener portalCacheManagerListener) {

		PortalCacheManager<K, V> portalCacheManager = _portalCacheManager;

		if ((portalCacheManager == _DUMMY_PORTAL_CACHE_MANAGER) ||
			portalCacheManager.unregisterPortalCacheManagerListener(
				portalCacheManagerListener)) {

			_portalCacheManagerListeners.remove(portalCacheManagerListener);

			return true;
		}

		return false;
	}

	@Override
	public void unregisterPortalCacheManagerListeners() {
		_portalCacheManager.unregisterPortalCacheManagerListeners();

		_portalCacheManagerListeners.clear();
	}

	protected PortalCacheManager<K, V> getPortalCacheManager() {
		return _portalCacheManager;
	}

	protected void setPortalCacheManager(
		PortalCacheManager<? extends Serializable, ?> portalCacheManager) {

		if (_portalCacheManager == portalCacheManager) {
			return;
		}

		if (portalCacheManager == null) {
			portalCacheManager = _DUMMY_PORTAL_CACHE_MANAGER;
		}

		_portalCacheManager = (PortalCacheManager<K, V>)portalCacheManager;

		for (PortalCacheManagerListener portalCacheManagerListener :
				_portalCacheManagerListeners) {

			_portalCacheManager.registerPortalCacheManagerListener(
				portalCacheManagerListener);
		}

		for (DynamicPortalCache<K, V> dynamicPortalCache :
				_dynamicPortalCaches.values()) {

			dynamicPortalCache.setPortalCache(
				_portalCacheManager.getPortalCache(
					dynamicPortalCache.getPortalCacheName(),
					dynamicPortalCache.isMVCC(),
					dynamicPortalCache.isSharded()));
		}
	}

	private static final PortalCacheManager<? extends Serializable, ?>
		_DUMMY_PORTAL_CACHE_MANAGER = ProxyFactory.newDummyInstance(
			PortalCacheManager.class);

	private final Map<String, DynamicPortalCache<K, V>> _dynamicPortalCaches =
		new ConcurrentHashMap<>();
	private volatile PortalCacheManager<K, V> _portalCacheManager;
	private final Set<PortalCacheManagerListener> _portalCacheManagerListeners =
		new CopyOnWriteArraySet<>();
	private final String _portalCacheManagerName;

}