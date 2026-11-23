/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache.internal;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.cache.BasePortalCache;
import com.liferay.portal.cache.ehcache.internal.configuration.EhcachePortalCacheConfiguration;
import com.liferay.portal.cache.ehcache.internal.events.PortalCacheCacheEventListener;
import com.liferay.portal.cache.ehcache.internal.expiry.EhcacheExpiryValue;
import com.liferay.portal.cache.ehcache.internal.io.SerializableObjectWrapper;
import com.liferay.portal.kernel.cache.PortalCacheListener;
import com.liferay.portal.kernel.cache.PortalCacheListenerScope;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.Serializable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ehcache.Cache;
import org.ehcache.expiry.ExpiryPolicy;

/**
 * @author Tina Tian
 */
public abstract class BaseEhcachePortalCache<K extends Serializable, V>
	extends BasePortalCache<K, V> implements EhcacheWrapper {

	public BaseEhcachePortalCache(
		BaseEhcachePortalCacheManager<K, V> baseEhcachePortalCacheManager,
		EhcachePortalCacheConfiguration ehcachePortalCacheConfiguration) {

		super(baseEhcachePortalCacheManager);

		keyType = ehcachePortalCacheConfiguration.getKeyType();
		valueType = ehcachePortalCacheConfiguration.getValueType();

		_portalCacheName = ehcachePortalCacheConfiguration.getPortalCacheName();
		_serializable =
			ehcachePortalCacheConfiguration.isRequireSerialization();

		_log = LogFactoryUtil.getLog(
			PortalCacheCacheEventListener.class.getName() + StringPool.PERIOD +
				_portalCacheName);
	}

	@Override
	public List<K> getKeys() {
		Cache<?, ?> cache = getEhcache();

		List<K> rawKeys = new ArrayList<>();

		cache.forEach(entry -> rawKeys.add((K)entry.getKey()));

		if (!_serializable) {
			return rawKeys;
		}

		return TransformUtil.transform(
			rawKeys, object -> SerializableObjectWrapper.unwrap(object));
	}

	@Override
	public String getPortalCacheName() {
		return _portalCacheName;
	}

	public boolean isSerializable() {
		return _serializable;
	}

	@Override
	public void removeAll() {
		Cache<?, ?> cache = getEhcache();

		cache.clear();

		if (_log.isDebugEnabled()) {
			_log.debug("Cleared " + getPortalCacheName());
		}

		aggregatedPortalCacheListener.notifyRemoveAll(this);
	}

	protected abstract void dispose();

	@Override
	protected V doGet(K key) {
		Cache<Object, Object> cache = (Cache<Object, Object>)getEhcache();

		return _getValue(cache.get(_wrapKey(key)));
	}

	@Override
	protected void doPut(K key, V value, int timeToLive) {
		Cache<Object, Object> cache = (Cache<Object, Object>)getEhcache();

		cache.put(_wrapKey(key), _wrapValue(value, timeToLive));
	}

	@Override
	protected V doPutIfAbsent(K key, V value, int timeToLive) {
		Cache<Object, Object> cache = (Cache<Object, Object>)getEhcache();

		return _getValue(
			cache.putIfAbsent(_wrapKey(key), _wrapValue(value, timeToLive)));
	}

	@Override
	protected void doRemove(K key) {
		Cache<Object, Object> cache = (Cache<Object, Object>)getEhcache();

		Object wrappedKey = _wrapKey(key);

		V value = _getValue(cache.get(wrappedKey));

		cache.remove(wrappedKey);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Removed ", key, " from ", getPortalCacheName()));
		}

		aggregatedPortalCacheListener.notifyEntryRemoved(
			this, key, value, DEFAULT_TIME_TO_LIVE);
	}

	@Override
	protected boolean doRemove(K key, V value) {
		Cache<Object, Object> cache = (Cache<Object, Object>)getEhcache();

		boolean removed = cache.remove(
			_wrapKey(key), _wrapValue(value, DEFAULT_TIME_TO_LIVE));

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Removed ", key, " from ", getPortalCacheName()));
		}

		aggregatedPortalCacheListener.notifyEntryRemoved(
			this, key, value, DEFAULT_TIME_TO_LIVE);

		return removed;
	}

	@Override
	protected V doReplace(K key, V value, int timeToLive) {
		Cache<Object, Object> cache = (Cache<Object, Object>)getEhcache();

		return _getValue(
			cache.replace(_wrapKey(key), _wrapValue(value, timeToLive)));
	}

	@Override
	protected boolean doReplace(K key, V oldValue, V newValue, int timeToLive) {
		Cache<Object, Object> cache = (Cache<Object, Object>)getEhcache();

		return cache.replace(
			_wrapKey(key), _wrapValue(oldValue, DEFAULT_TIME_TO_LIVE),
			_wrapValue(newValue, timeToLive));
	}

	protected Map<PortalCacheListener<K, V>, PortalCacheListenerScope>
		getPortalCacheListeners() {

		return aggregatedPortalCacheListener.getPortalCacheListeners();
	}

	protected abstract void resetEhcache();

	protected final Class<?> keyType;
	protected final Class<?> valueType;

	private V _getValue(Object value) {
		if (value == null) {
			return null;
		}

		EhcacheExpiryValue ehcacheExpiryValue = (EhcacheExpiryValue)value;

		value = ehcacheExpiryValue.getValue();

		if (_serializable) {
			return SerializableObjectWrapper.unwrap(value);
		}

		return (V)value;
	}

	private Object _wrapKey(K key) {
		if (!_serializable) {
			return key;
		}

		return new SerializableObjectWrapper(key);
	}

	private Object _wrapValue(V value, int timeToLive) {
		Duration duration = ExpiryPolicy.INFINITE;

		if (timeToLive > 0) {
			duration = Duration.of(timeToLive, ChronoUnit.SECONDS);
		}

		if (_serializable && (value instanceof Serializable)) {
			return new EhcacheExpiryValue(
				new SerializableObjectWrapper((Serializable)value), duration);
		}

		return new EhcacheExpiryValue(value, duration);
	}

	private final Log _log;
	private final String _portalCacheName;
	private final boolean _serializable;

}