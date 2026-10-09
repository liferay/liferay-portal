/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;

/**
 * @author Hugo Huijser
 */
public class HashMapDictionaryBuilder<K, V> extends BaseMapBuilder {

	public static <K, V> HashMapDictionaryWrapper<K, V> create(Map<K, V> map) {
		return new HashMapDictionaryWrapper<>(map);
	}

	public static <K, V> HashMapDictionaryWrapper<K, V> put(
		Collection<? extends K> inputCollection,
		UnsafeFunction<K, V, Exception> unsafeFunction) {

		HashMapDictionaryWrapper<K, V> hashMapDictionaryWrapper =
			new HashMapDictionaryWrapper<>();

		return hashMapDictionaryWrapper.put(inputCollection, unsafeFunction);
	}

	public static <K, V> HashMapDictionaryWrapper<K, V> put(
		K key, UnsafeSupplier<V, Exception> valueUnsafeSupplier) {

		HashMapDictionaryWrapper<K, V> hashMapDictionaryWrapper =
			new HashMapDictionaryWrapper<>();

		return hashMapDictionaryWrapper.put(key, valueUnsafeSupplier);
	}

	public static <K, V> HashMapDictionaryWrapper<K, V> put(K key, V value) {
		HashMapDictionaryWrapper<K, V> hashMapDictionaryWrapper =
			new HashMapDictionaryWrapper<>();

		return hashMapDictionaryWrapper.put(key, value);
	}

	public static <K, V> HashMapDictionaryWrapper<K, V> put(
		UnsafeSupplier<K, Exception> keyUnsafeSupplier,
		UnsafeSupplier<V, Exception> valueUnsafeSupplier) {

		HashMapDictionaryWrapper<K, V> hashMapDictionaryWrapper =
			new HashMapDictionaryWrapper<>();

		return hashMapDictionaryWrapper.put(
			keyUnsafeSupplier, valueUnsafeSupplier);
	}

	public static <K, V> HashMapDictionaryWrapper<K, V> put(
		UnsafeSupplier<K, Exception> keyUnsafeSupplier, V value) {

		HashMapDictionaryWrapper<K, V> hashMapDictionaryWrapper =
			new HashMapDictionaryWrapper<>();

		return hashMapDictionaryWrapper.put(keyUnsafeSupplier, value);
	}

	public static <K, V> HashMapDictionaryWrapper<K, V> putAll(
		Dictionary<? extends K, ? extends V> dictionary) {

		HashMapDictionaryWrapper<K, V> hashMapDictionaryWrapper =
			new HashMapDictionaryWrapper<>();

		return hashMapDictionaryWrapper.putAll(dictionary);
	}

	public static <K, V> HashMapDictionaryWrapper<K, V> putAll(
		Map<? extends K, ? extends V> inputMap) {

		HashMapDictionaryWrapper<K, V> hashMapDictionaryWrapper =
			new HashMapDictionaryWrapper<>();

		return hashMapDictionaryWrapper.putAll(inputMap);
	}

	public static final class HashMapDictionaryWrapper<K, V> {

		public HashMapDictionaryWrapper() {
			_hashMapDictionary = new HashMapDictionary<>();
		}

		public HashMapDictionaryWrapper(Map<K, V> map) {
			_hashMapDictionary = new HashMapDictionary<>(map);
		}

		public HashMapDictionary<K, V> build() {
			return _hashMapDictionary;
		}

		public HashMapDictionaryWrapper<K, V> put(
			Collection<? extends K> keyCollection,
			UnsafeFunction<K, V, Exception> unsafeFunction) {

			try {
				for (K key : keyCollection) {
					V value = unsafeFunction.apply(key);

					if (value == null) {
						continue;
					}

					_hashMapDictionary.put(key, value);
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}

			return this;
		}

		public HashMapDictionaryWrapper<K, V> put(
			K key, UnsafeSupplier<V, Exception> valueUnsafeSupplier) {

			if (valueUnsafeSupplier == null) {
				_hashMapDictionary.put(key, null);

				return this;
			}

			try {
				V value = valueUnsafeSupplier.get();

				if (value != null) {
					_hashMapDictionary.put(key, value);
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}

			return this;
		}

		public HashMapDictionaryWrapper<K, V> put(K key, V value) {
			_hashMapDictionary.put(key, value);

			return this;
		}

		public HashMapDictionaryWrapper<K, V> put(
			UnsafeSupplier<K, Exception> keyUnsafeSupplier,
			UnsafeSupplier<V, Exception> valueUnsafeSupplier) {

			try {
				K key = keyUnsafeSupplier.get();

				if (key == null) {
					return this;
				}

				if (valueUnsafeSupplier == null) {
					_hashMapDictionary.put(key, null);

					return this;
				}

				V value = valueUnsafeSupplier.get();

				if (value != null) {
					_hashMapDictionary.put(key, value);
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}

			return this;
		}

		public HashMapDictionaryWrapper<K, V> put(
			UnsafeSupplier<K, Exception> keyUnsafeSupplier, V value) {

			try {
				K key = keyUnsafeSupplier.get();

				if (key != null) {
					_hashMapDictionary.put(key, value);
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}

			return this;
		}

		public HashMapDictionaryWrapper<K, V> putAll(
			Dictionary<? extends K, ? extends V> dictionary) {

			if (dictionary != null) {
				_hashMapDictionary.putAll(dictionary);
			}

			return this;
		}

		public HashMapDictionaryWrapper<K, V> putAll(
			Map<? extends K, ? extends V> inputMap) {

			if (inputMap != null) {
				_hashMapDictionary.putAll(inputMap);
			}

			return this;
		}

		public HashMapDictionaryWrapper<K, V> remove(K key) {
			_hashMapDictionary.remove(key);

			return this;
		}

		protected HashMapDictionary<K, V> getDictionary() {
			return _hashMapDictionary;
		}

		private final HashMapDictionary<K, V> _hashMapDictionary;

	}

}