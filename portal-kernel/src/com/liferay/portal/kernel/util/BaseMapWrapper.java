/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import java.util.Collection;
import java.util.Map;

/**
 * @author Hugo Huijser
 */
public abstract class BaseMapWrapper<K, V> {

	protected void doPut(
		BaseMapBuilder.UnsafeSupplier<K, Exception> keyUnsafeSupplier,
		BaseMapBuilder.UnsafeSupplier<V, Exception> valueUnsafeSupplier) {

		try {
			K key = keyUnsafeSupplier.get();

			if (key == null) {
				return;
			}

			if (valueUnsafeSupplier == null) {
				_put(key, null);

				return;
			}

			V value = valueUnsafeSupplier.get();

			if (value != null) {
				_put(key, value);
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	protected void doPut(
		BaseMapBuilder.UnsafeSupplier<K, Exception> keyUnsafeSupplier,
		V value) {

		try {
			K key = keyUnsafeSupplier.get();

			if (key != null) {
				_put(key, value);
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	protected void doPut(
		Collection<? extends K> keyCollection,
		BaseMapBuilder.UnsafeFunction<K, V, Exception> unsafeFunction) {

		try {
			for (K key : keyCollection) {
				V value = unsafeFunction.apply(key);

				if (value == null) {
					continue;
				}

				_put(key, value);
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	protected void doPut(
		K key,
		BaseMapBuilder.UnsafeSupplier<V, Exception> valueUnsafeSupplier) {

		if (valueUnsafeSupplier == null) {
			_put(key, null);

			return;
		}

		try {
			V value = valueUnsafeSupplier.get();

			if (value != null) {
				_put(key, value);
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	protected void doPutAll(Map<? extends K, ? extends V> inputMap) {
		if (inputMap != null) {
			Map<K, V> map = getMap();

			map.putAll(inputMap);
		}
	}

	protected abstract Map<K, V> getMap();

	private void _put(K key, V value) {
		Map<K, V> map = getMap();

		map.put(key, value);
	}

}