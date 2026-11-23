/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache.internal.events;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.cache.AggregatedPortalCacheListener;
import com.liferay.portal.cache.ehcache.internal.BaseEhcachePortalCache;
import com.liferay.portal.cache.ehcache.internal.expiry.EhcacheExpiryValue;
import com.liferay.portal.cache.ehcache.internal.io.SerializableObjectWrapper;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.Serializable;

import java.time.Duration;

import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.impl.events.CacheEventAdapter;

/**
 * @author Edward C. Han
 * @author Shuyang Zhou
 */
public class PortalCacheCacheEventListener<K extends Serializable, V>
	extends CacheEventAdapter<Object, Object> {

	public PortalCacheCacheEventListener(
		AggregatedPortalCacheListener<K, V> aggregatedPortalCacheListener,
		PortalCache<K, V> portalCache) {

		_aggregatedPortalCacheListener = aggregatedPortalCacheListener;
		_portalCache = portalCache;

		boolean requireSerialization = false;

		if (_portalCache instanceof BaseEhcachePortalCache) {
			BaseEhcachePortalCache<?, ?> baseEhcachePortalCache =
				(BaseEhcachePortalCache<?, ?>)_portalCache;

			requireSerialization = baseEhcachePortalCache.isSerializable();
		}

		_requireSerialization = requireSerialization;

		_log = LogFactoryUtil.getLog(
			PortalCacheCacheEventListener.class.getName() + StringPool.PERIOD +
				portalCache.getPortalCacheName());
	}

	@Override
	public Object clone() {
		return new PortalCacheCacheEventListener<>(
			_aggregatedPortalCacheListener, _portalCache);
	}

	@Override
	public void onCreation(Object key, Object value) {
		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Put ", _getKey(key), " into ",
					_portalCache.getPortalCacheName()));
		}

		if (_aggregatedPortalCacheListener.isEmpty()) {
			return;
		}

		_aggregatedPortalCacheListener.notifyEntryPut(
			_portalCache, _getKey(key), _getValue(value),
			_getTimeToLive(value));
	}

	@Override
	public void onEviction(Object key, Object value) {
		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Evicted ", _getKey(key), " from ",
					_portalCache.getPortalCacheName()));
		}

		if (_aggregatedPortalCacheListener.isEmpty()) {
			return;
		}

		_aggregatedPortalCacheListener.notifyEntryEvicted(
			_portalCache, _getKey(key), _getValue(value),
			_getTimeToLive(value));
	}

	@Override
	public void onExpiry(Object key, Object value) {
		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Expired ", _getKey(key), " from ",
					_portalCache.getPortalCacheName()));
		}

		if (_aggregatedPortalCacheListener.isEmpty()) {
			return;
		}

		_aggregatedPortalCacheListener.notifyEntryExpired(
			_portalCache, _getKey(key), _getValue(value),
			_getTimeToLive(value));
	}

	@Override
	public void onUpdate(Object key, Object oldValue, Object newValue) {
		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Updated ", _getKey(key), " in ",
					_portalCache.getPortalCacheName()));
		}

		if (_aggregatedPortalCacheListener.isEmpty()) {
			return;
		}

		_aggregatedPortalCacheListener.notifyEntryUpdated(
			_portalCache, _getKey(key), _getValue(newValue),
			_getTimeToLive(newValue));
	}

	private K _getKey(Object key) {
		if (_requireSerialization) {
			return SerializableObjectWrapper.unwrap(key);
		}

		return (K)key;
	}

	private int _getTimeToLive(Object value) {
		EhcacheExpiryValue ehcacheExpiryValue = (EhcacheExpiryValue)value;

		Duration duration = ehcacheExpiryValue.getTimeToLive();

		if (duration.equals(ExpiryPolicy.INFINITE)) {
			return 0;
		}

		return (int)duration.getSeconds();
	}

	private V _getValue(Object value) {
		EhcacheExpiryValue ehcacheExpiryValue = (EhcacheExpiryValue)value;

		value = ehcacheExpiryValue.getValue();

		if (_requireSerialization) {
			return SerializableObjectWrapper.unwrap(value);
		}

		return (V)value;
	}

	private final AggregatedPortalCacheListener<K, V>
		_aggregatedPortalCacheListener;
	private final Log _log;
	private final PortalCache<K, V> _portalCache;
	private final boolean _requireSerialization;

}