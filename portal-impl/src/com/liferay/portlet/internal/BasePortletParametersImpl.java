/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import jakarta.portlet.MutablePortletParameters;
import jakarta.portlet.PortletParameters;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Neil Griffin
 */
public abstract class BasePortletParametersImpl
	<T extends MutablePortletParameters>
		implements PortletParameters {

	public BasePortletParametersImpl(
		Map<String, String[]> parameterMap, String namespace,
		Function<Map<String, String[]>, T> mutablePortletParametersCreator) {

		_parameterMap = parameterMap;
		_namespace = namespace;
		_mutablePortletParametersCreator = mutablePortletParametersCreator;
	}

	@Override
	public T clone() {
		return _mutablePortletParametersCreator.apply(
			deepCopyMap(getParameterMap()));
	}

	@Override
	public Set<String> getNames() {
		if (_parameterMap == null) {
			return Collections.emptySet();
		}

		Set<String> keySet = _parameterMap.keySet();

		if (_namespace == null) {
			return keySet;
		}

		return new NameHashSet(keySet, _namespace);
	}

	@Override
	public String getValue(String name) {
		String[] values = getValues(name);

		if ((values == null) || (values.length < 1)) {
			return null;
		}

		return values[0];
	}

	@Override
	public String[] getValues(String name) {
		String[] values = _parameterMap.get(name);

		if (values != null) {
			return values;
		}

		if ((_namespace != null) && (name != null)) {
			values = _parameterMap.get(_namespace.concat(name));

			if ((values == null) && name.startsWith(_namespace)) {
				values = _parameterMap.get(name.substring(_namespace.length()));
			}
		}

		return values;
	}

	@Override
	public boolean isEmpty() {
		return _parameterMap.isEmpty();
	}

	@Override
	public int size() {
		return _parameterMap.size();
	}

	protected Map<String, String[]> deepCopyMap(Map<String, String[]> map) {
		Map<String, String[]> copiedMap = new HashMap<>();

		for (Map.Entry<String, String[]> entry : map.entrySet()) {
			String key = entry.getKey();
			String[] values = entry.getValue();

			if (values == null) {
				copiedMap.put(key, null);
			}
			else {
				String[] copiedParameterValues = values.clone();

				copiedMap.put(key, copiedParameterValues);
			}
		}

		return copiedMap;
	}

	protected Map<String, String[]> getParameterMap() {
		return _parameterMap;
	}

	private final Function<Map<String, String[]>, T>
		_mutablePortletParametersCreator;
	private final String _namespace;
	private final Map<String, String[]> _parameterMap;

	private static class NameHashSet extends HashSet<String> {

		@Override
		public boolean contains(Object name) {
			if (super.contains(name)) {
				return true;
			}

			if (!(name instanceof String)) {
				return false;
			}

			String nameString = (String)name;

			if (nameString.startsWith(_namespace)) {
				return super.contains(
					nameString.substring(_namespace.length()));
			}

			return false;
		}

		private NameHashSet(Set<String> names, String namespace) {
			for (String name : names) {
				if (name.startsWith(namespace)) {
					add(name.substring(namespace.length()));
				}
				else {
					add(name);
				}
			}

			_namespace = namespace;
		}

		private String _namespace;

	}

}