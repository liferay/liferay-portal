/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.util.MappingEnumeration;
import com.liferay.portal.kernel.util.SetUtil;

import jakarta.servlet.http.HttpSession;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Minhchau Dang
 * @author Shuyang Zhou
 */
public class PortletSessionAttributeMap extends AbstractMap<String, Object> {

	public PortletSessionAttributeMap(HttpSession httpSession) {
		this(httpSession, null);
	}

	public PortletSessionAttributeMap(
		HttpSession httpSession, String scopePrefix) {

		this.httpSession = httpSession;
		this.scopePrefix = scopePrefix;
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		if (key == null) {
			return false;
		}

		key = encodeKey(String.valueOf(key));

		Enumeration<String> enumeration = getAttributeNames(false);

		while (enumeration.hasMoreElements()) {
			String attributeName = enumeration.nextElement();

			if (attributeName.equals(key)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		Enumeration<String> enumeration = getAttributeNames(false);

		while (enumeration.hasMoreElements()) {
			Object attributeValue = httpSession.getAttribute(
				enumeration.nextElement());

			if (attributeValue.equals(value)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		Map<String, Object> map = new HashMap<>();

		Enumeration<String> enumeration = getAttributeNames(true);

		while (enumeration.hasMoreElements()) {
			String attributeName = enumeration.nextElement();

			map.put(attributeName, get(attributeName));
		}

		return Collections.unmodifiableSet(map.entrySet());
	}

	@Override
	public Object get(Object key) {
		if (key == null) {
			return null;
		}

		return httpSession.getAttribute(encodeKey(String.valueOf(key)));
	}

	@Override
	public boolean isEmpty() {
		Enumeration<String> enumeration = getAttributeNames(false);

		return !enumeration.hasMoreElements();
	}

	@Override
	public Set<String> keySet() {
		return Collections.unmodifiableSet(
			SetUtil.fromEnumeration(getAttributeNames(true)));
	}

	@Override
	public Object put(String key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ?> map) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		int size = 0;

		Enumeration<String> enumeration = getAttributeNames(false);

		while (enumeration.hasMoreElements()) {
			enumeration.nextElement();

			size++;
		}

		return size;
	}

	@Override
	public Collection<Object> values() {
		List<Object> attributeValues = new ArrayList<>();

		Enumeration<String> enumeration = getAttributeNames(false);

		while (enumeration.hasMoreElements()) {
			attributeValues.add(
				httpSession.getAttribute(enumeration.nextElement()));
		}

		return attributeValues;
	}

	protected String encodeKey(String key) {
		if (scopePrefix == null) {
			return key;
		}

		return scopePrefix.concat(key);
	}

	protected Enumeration<String> getAttributeNames(boolean removePrefix) {
		Enumeration<String> enumeration = httpSession.getAttributeNames();

		if (scopePrefix == null) {
			return enumeration;
		}

		return new MappingEnumeration<>(
			enumeration, new AttributeNameMapper(scopePrefix, removePrefix));
	}

	protected final HttpSession httpSession;
	protected final String scopePrefix;

	protected static class AttributeNameMapper
		implements MappingEnumeration.Mapper<String, String> {

		@Override
		public String map(String attributeName) {
			if (attributeName.startsWith(_attributeNamespace)) {
				if (_removePrefix) {
					return attributeName.substring(
						_attributeNamespace.length());
				}

				return attributeName;
			}

			return null;
		}

		protected AttributeNameMapper(
			String attributeNamespace, boolean removePrefix) {

			_attributeNamespace = attributeNamespace;
			_removePrefix = removePrefix;
		}

		private final String _attributeNamespace;
		private final boolean _removePrefix;

	}

}