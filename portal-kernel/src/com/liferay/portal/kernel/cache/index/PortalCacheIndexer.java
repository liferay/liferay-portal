/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.cache.index;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheListener;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Shuyang Zhou
 */
public class PortalCacheIndexer<I, K extends Serializable, V> {

	public PortalCacheIndexer(
		IndexEncoder<I, K> indexEncoder, PortalCache<K, V> portalCache) {

		_indexEncoder = indexEncoder;

		_portalCache = portalCache;

		_portalCache.registerPortalCacheListener(
			new IndexerPortalCacheListener());

		for (K indexedCacheKey : _portalCache.getKeys()) {
			_addIndexedCacheKey(indexedCacheKey);
		}
	}

	public Set<K> getKeys(I index) {
		Set<K> keys = _indexedCacheKeys.get(index);

		if (keys == null) {
			return Collections.emptySet();
		}

		return new HashSet<>(keys);
	}

	public void removeKeys(I index) {
		Set<K> keys = _indexedCacheKeys.remove(index);

		if (keys == null) {
			return;
		}

		for (K key : keys) {
			_portalCache.remove(key);
		}
	}

	private void _addIndexedCacheKey(K key) {
		I index = _indexEncoder.encode(key);

		Set<K> keys = _indexedCacheKeys.get(index);

		if (keys == null) {
			Set<K> newKeys = Collections.newSetFromMap(
				new ConcurrentHashMap<>());

			newKeys.add(key);

			keys = _indexedCacheKeys.putIfAbsent(index, newKeys);

			if (keys == null) {
				return;
			}
		}

		keys.add(key);
	}

	private void _removeIndexedCacheKey(K key) {
		I index = _indexEncoder.encode(key);

		Set<K> keys = _indexedCacheKeys.get(index);

		if (keys == null) {
			return;
		}

		keys.remove(key);

		if (keys.isEmpty() && _indexedCacheKeys.remove(index, keys)) {
			for (K victimIndexedCacheKey : keys) {
				_addIndexedCacheKey(victimIndexedCacheKey);
			}
		}
	}

	private final IndexEncoder<I, K> _indexEncoder;
	private final ConcurrentMap<I, Set<K>> _indexedCacheKeys =
		new ConcurrentHashMap<>();
	private final PortalCache<K, V> _portalCache;

	private class IndexerPortalCacheListener
		implements PortalCacheListener<K, V> {

		@Override
		public void dispose() {
			_indexedCacheKeys.clear();
		}

		@Override
		public void notifyEntryEvicted(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive) {

			_removeIndexedCacheKey(key);
		}

		@Override
		public void notifyEntryExpired(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive) {

			_removeIndexedCacheKey(key);
		}

		@Override
		public void notifyEntryPut(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive) {

			_addIndexedCacheKey(key);
		}

		@Override
		public void notifyEntryRemoved(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive) {

			_removeIndexedCacheKey(key);
		}

		@Override
		public void notifyEntryUpdated(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive) {
		}

		@Override
		public void notifyRemoveAll(PortalCache<K, V> portalCache) {
			_indexedCacheKeys.clear();
		}

	}

}