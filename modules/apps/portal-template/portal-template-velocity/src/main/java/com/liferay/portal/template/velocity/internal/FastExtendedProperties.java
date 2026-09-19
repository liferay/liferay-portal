/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.velocity.internal;

import java.io.IOException;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.ExtendedProperties;

/**
 * @author Shuyang Zhou
 */
public class FastExtendedProperties extends ExtendedProperties {

	public FastExtendedProperties() {
	}

	public FastExtendedProperties(ExtendedProperties extendedProperties)
		throws IOException {

		// Do not call putAll. See LPS-61927.

		//putAll(extendedProperties);

		Enumeration<String> enumeration = extendedProperties.keys();

		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();

			Object value = extendedProperties.get(key);

			addProperty(key, value);
		}
	}

	@Override
	public void clear() {
		_map.clear();
	}

	@Override
	public Object clone() {
		FastExtendedProperties fastExtendedProperties =
			(FastExtendedProperties)super.clone();

		fastExtendedProperties._map = new ConcurrentHashMap<>(_map);

		return fastExtendedProperties;
	}

	@Override
	public boolean contains(Object value) {
		return _map.containsKey(value);
	}

	@Override
	public boolean containsKey(Object key) {
		return _map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return _map.containsValue(value);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Enumeration elements() {
		return Collections.enumeration(_map.values());
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Set entrySet() {
		return _map.entrySet();
	}

	@Override
	public boolean equals(Object object) {
		return _map.equals(object);
	}

	@Override
	public Object get(Object key) {
		return _map.get(key);
	}

	@Override
	public int hashCode() {
		return _map.hashCode();
	}

	@Override
	public boolean isEmpty() {
		return _map.isEmpty();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Set keySet() {
		return _map.keySet();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Enumeration keys() {
		return Collections.enumeration(_map.keySet());
	}

	@Override
	public Object put(Object key, Object value) {
		return _map.put(key, value);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void putAll(Map t) {
		_map.putAll(t);
	}

	@Override
	public Object remove(Object key) {
		return _map.remove(key);
	}

	@Override
	public int size() {
		return _map.size();
	}

	@Override
	public String toString() {
		return _map.toString();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Collection values() {
		return _map.values();
	}

	private Map<Object, Object> _map = new ConcurrentHashMap<>();

}