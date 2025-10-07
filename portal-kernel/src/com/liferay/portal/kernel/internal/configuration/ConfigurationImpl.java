/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.configuration;

import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class ConfigurationImpl implements Configuration {

	public ConfigurationImpl(ClassLoader classLoader, String name) {
		_classLoaderAggregateProperties =
			ClassLoaderAggregatePropertiesUtil.create(classLoader, name);

		printSources();
	}

	@Override
	public void clearCache() {
		_configurationArrayCache.clear();
		_configurationCache.clear();
		_configurationFilterArrayCache.clear();
		_configurationFilterCache.clear();

		_properties = null;
	}

	@Override
	public boolean contains(String key) {
		Object value = _configurationCache.get(key);

		if (value == null) {
			value = _classLoaderAggregateProperties.getProperty(key);

			if (value == null) {
				value = _nullValue;
			}

			_configurationCache.put(key, value);
		}

		if (value == _nullValue) {
			return false;
		}

		return true;
	}

	@Override
	public String get(String key) {
		Object value = _configurationCache.get(key);

		if (value == null) {
			value = _classLoaderAggregateProperties.getString(key);

			if (value == null) {
				value = _nullValue;
			}

			_configurationCache.put(key, value);
		}
		else if (_PRINT_DUPLICATE_CALLS_TO_GET) {
			System.out.println("Duplicate call to get " + key);
		}

		if (value instanceof String) {
			return (String)value;
		}

		return null;
	}

	@Override
	public String get(String key, Filter filter) {
		FilterCacheKey filterCacheKey = new FilterCacheKey(key, filter);

		Object value = null;

		if (filterCacheKey != null) {
			value = _configurationFilterCache.get(filterCacheKey);
		}

		if (value == null) {
			value = _classLoaderAggregateProperties.getString(key, filter);

			if (filterCacheKey != null) {
				if (value == null) {
					value = _nullValue;
				}

				_configurationFilterCache.put(filterCacheKey, value);
			}
		}

		if (value instanceof String) {
			return (String)value;
		}

		return null;
	}

	@Override
	public String[] getArray(String key) {
		Object value = _configurationArrayCache.get(key);

		if (value == null) {
			String[] array = _classLoaderAggregateProperties.getStringArray(
				key);

			value = _fixArrayValue(array);

			_configurationArrayCache.put(key, value);
		}

		if (value instanceof String[]) {
			return (String[])value;
		}

		return _EMPTY_ARRAY;
	}

	@Override
	public String[] getArray(String key, Filter filter) {
		FilterCacheKey filterCacheKey = new FilterCacheKey(key, filter);

		Object value = null;

		if (filterCacheKey != null) {
			value = _configurationFilterArrayCache.get(filterCacheKey);
		}

		if (value == null) {
			String[] array = _classLoaderAggregateProperties.getStringArray(
				key, filter);

			value = _fixArrayValue(array);

			if (filterCacheKey != null) {
				_configurationFilterArrayCache.put(filterCacheKey, value);
			}
		}

		if (value instanceof String[]) {
			return (String[])value;
		}

		return _EMPTY_ARRAY;
	}

	@Override
	public List<String> getLoadedSources() {
		return _classLoaderAggregateProperties.loadedSources();
	}

	@Override
	public Properties getProperties() {
		if (_properties != null) {
			return _properties;
		}

		// For some strange reason, componentProperties.getProperties() returns
		// values with spaces after commas. So a property setting of "xyz=1,2,3"
		// actually returns "xyz=1, 2, 3". This can break applications that
		// don't expect that extra space. However, getting the property value
		// directly through componentProperties returns the correct value. This
		// method fixes the weird behavior by returning properties with the
		// correct values.

		Properties properties = new Properties();

		Properties componentPropertiesProperties =
			_classLoaderAggregateProperties.getProperties();

		for (String key : componentPropertiesProperties.stringPropertyNames()) {
			properties.setProperty(
				key, _classLoaderAggregateProperties.getString(key));
		}

		_properties = properties;

		return properties;
	}

	@Override
	public Properties getProperties(String prefix, boolean removePrefix) {
		return PropertiesUtil.getProperties(
			getProperties(), prefix, removePrefix);
	}

	@Override
	public void set(String key, String value) {
		_classLoaderAggregateProperties.setProperty(key, value);

		clearCache();
	}

	protected void printSources() {
		if (GetterUtil.getBoolean(
				System.getProperty("configuration.impl.quiet"))) {

			return;
		}

		List<String> sources = _classLoaderAggregateProperties.loadedSources();

		for (int i = sources.size() - 1; i >= 0; i--) {
			String source = sources.get(i);

			if (_printedSources.contains(source)) {
				continue;
			}

			_printedSources.add(source);

			if (source.startsWith("bundleresource://")) {
				continue;
			}

			String info = "Loading " + source;

			System.out.println(info);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> _castPropertiesToMap(Properties properties) {
		return (Map)properties;
	}

	private Object _fixArrayValue(String[] array) {
		Object value = _nullValue;

		if (ArrayUtil.isNotEmpty(array)) {

			// Commons Configuration parses an empty property into a String
			// array with one String containing one space. It also leaves a
			// trailing array member if you set a property in more than one
			// line.

			if (Validator.isNull(array[array.length - 1])) {
				String[] subarray = new String[array.length - 1];

				System.arraycopy(array, 0, subarray, 0, subarray.length);

				array = subarray;
			}

			if (array.length > 0) {
				value = array;
			}
		}

		return value;
	}

	private static final String[] _EMPTY_ARRAY = new String[0];

	private static final boolean _PRINT_DUPLICATE_CALLS_TO_GET = false;

	private static final Object _nullValue = new Object();

	private final ClassLoaderAggregateProperties
		_classLoaderAggregateProperties;
	private final Map<String, Object> _configurationArrayCache =
		new ConcurrentHashMap<>();
	private final Map<String, Object> _configurationCache =
		new ConcurrentHashMap<>();
	private final Map<FilterCacheKey, Object> _configurationFilterArrayCache =
		new ConcurrentHashMap<>();
	private final Map<FilterCacheKey, Object> _configurationFilterCache =
		new ConcurrentHashMap<>();
	private final Set<String> _printedSources = new HashSet<>();
	private Properties _properties;

	private static class FilterCacheKey {

		@Override
		public boolean equals(Object object) {
			FilterCacheKey filterCacheKey = (FilterCacheKey)object;

			if (Objects.equals(_key, filterCacheKey._key) &&
				Arrays.equals(_selectors, filterCacheKey._selectors)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hashCode = HashUtil.hash(0, _key);

			for (String selector : _selectors) {
				hashCode = HashUtil.hash(hashCode, selector);
			}

			return hashCode;
		}

		private FilterCacheKey(String key, Filter filter) {
			_key = key;

			_selectors = filter.getSelectors();
		}

		private final String _key;
		private final String[] _selectors;

	}

}