/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import java.util.Map;

/**
 * @author Hugo Huijser
 */
public class UnicodePropertiesBuilder extends BaseMapBuilder {

	public static UnicodePropertiesWrapper create(boolean safe) {
		return new UnicodePropertiesWrapper(safe);
	}

	public static UnicodePropertiesWrapper create(
		Map<String, String> map, boolean safe) {

		return new UnicodePropertiesWrapper(map, safe);
	}

	public static UnicodePropertiesWrapper fastLoad(String props) {
		UnicodePropertiesWrapper unicodePropertiesWrapper =
			new UnicodePropertiesWrapper();

		unicodePropertiesWrapper.fastLoad(props);

		return unicodePropertiesWrapper;
	}

	public static UnicodePropertiesWrapper load(String props) {
		UnicodePropertiesWrapper unicodePropertiesWrapper =
			new UnicodePropertiesWrapper();

		unicodePropertiesWrapper.load(props);

		return unicodePropertiesWrapper;
	}

	public static UnicodePropertiesWrapper put(String line) {
		UnicodePropertiesWrapper unicodePropertiesWrapper =
			new UnicodePropertiesWrapper();

		unicodePropertiesWrapper.put(line);

		return unicodePropertiesWrapper;
	}

	public static UnicodePropertiesWrapper put(String key, boolean value) {
		return put(key, String.valueOf(value));
	}

	public static UnicodePropertiesWrapper put(String key, double value) {
		return put(key, String.valueOf(value));
	}

	public static UnicodePropertiesWrapper put(String key, int value) {
		return put(key, String.valueOf(value));
	}

	public static UnicodePropertiesWrapper put(String key, long value) {
		return put(key, String.valueOf(value));
	}

	public static UnicodePropertiesWrapper put(String key, short value) {
		return put(key, String.valueOf(value));
	}

	public static UnicodePropertiesWrapper put(String key, String value) {
		UnicodePropertiesWrapper unicodePropertiesWrapper =
			new UnicodePropertiesWrapper();

		unicodePropertiesWrapper.put(key, value);

		return unicodePropertiesWrapper;
	}

	public static UnicodePropertiesWrapper put(
		String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		UnicodePropertiesWrapper unicodePropertiesWrapper =
			new UnicodePropertiesWrapper();

		unicodePropertiesWrapper.put(key, valueUnsafeSupplier);

		return unicodePropertiesWrapper;
	}

	public static UnicodePropertiesWrapper putAll(
		Map<? extends String, ? extends String> map) {

		UnicodePropertiesWrapper unicodePropertiesWrapper =
			new UnicodePropertiesWrapper();

		unicodePropertiesWrapper.putAll(map);

		return unicodePropertiesWrapper;
	}

	public static UnicodePropertiesWrapper setProperty(
		String key, String value) {

		UnicodePropertiesWrapper unicodePropertiesWrapper =
			new UnicodePropertiesWrapper();

		unicodePropertiesWrapper.setProperty(key, value);

		return unicodePropertiesWrapper;
	}

	public static UnicodePropertiesWrapper setProperty(
		String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		UnicodePropertiesWrapper unicodePropertiesWrapper =
			new UnicodePropertiesWrapper();

		unicodePropertiesWrapper.setProperty(key, valueUnsafeSupplier);

		return unicodePropertiesWrapper;
	}

	public static final class UnicodePropertiesWrapper {

		public UnicodePropertiesWrapper() {
			_unicodeProperties = new UnicodeProperties();
		}

		public UnicodePropertiesWrapper(boolean safe) {
			_unicodeProperties = new UnicodeProperties(safe);
		}

		public UnicodePropertiesWrapper(Map<String, String> map, boolean safe) {
			_unicodeProperties = new UnicodeProperties(map, safe);
		}

		public UnicodeProperties build() {
			return _unicodeProperties;
		}

		public String buildString() {
			return _unicodeProperties.toString();
		}

		public UnicodePropertiesWrapper fastLoad(String props) {
			_unicodeProperties.fastLoad(props);

			return this;
		}

		public UnicodePropertiesWrapper load(String props) {
			_unicodeProperties.load(props);

			return this;
		}

		public UnicodePropertiesWrapper put(String line) {
			_unicodeProperties.put(line);

			return this;
		}

		public UnicodePropertiesWrapper put(String key, boolean value) {
			return put(key, String.valueOf(value));
		}

		public UnicodePropertiesWrapper put(String key, double value) {
			return put(key, String.valueOf(value));
		}

		public UnicodePropertiesWrapper put(String key, int value) {
			return put(key, String.valueOf(value));
		}

		public UnicodePropertiesWrapper put(String key, long value) {
			return put(key, String.valueOf(value));
		}

		public UnicodePropertiesWrapper put(String key, short value) {
			return put(key, String.valueOf(value));
		}

		public UnicodePropertiesWrapper put(String key, String value) {
			_unicodeProperties.put(key, value);

			return this;
		}

		public UnicodePropertiesWrapper put(
			String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

			try {
				String value = valueUnsafeSupplier.get();

				if (value != null) {
					_unicodeProperties.put(key, value);
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}

			return this;
		}

		public UnicodePropertiesWrapper putAll(
			Map<? extends String, ? extends String> map) {

			_unicodeProperties.putAll(map);

			return this;
		}

		public UnicodePropertiesWrapper setProperty(String key, String value) {
			_unicodeProperties.setProperty(key, value);

			return this;
		}

		public UnicodePropertiesWrapper setProperty(
			String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

			try {
				String value = valueUnsafeSupplier.get();

				if (value != null) {
					_unicodeProperties.setProperty(key, value);
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}

			return this;
		}

		private final UnicodeProperties _unicodeProperties;

	}

}