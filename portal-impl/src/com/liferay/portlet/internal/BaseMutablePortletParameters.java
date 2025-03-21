/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.MutablePortletParameters;
import jakarta.portlet.PortletParameters;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Neil Griffin
 */
public abstract class BaseMutablePortletParameters
	<T extends MutablePortletParameters>
		extends BasePortletParametersImpl<T>
		implements LiferayMutablePortletParameters {

	public BaseMutablePortletParameters(
		Map<String, String[]> parameterMap,
		Function<Map<String, String[]>, T> mutablePortletParametersCreator) {

		super(parameterMap, null, mutablePortletParametersCreator);
	}

	@Override
	public MutablePortletParameters add(PortletParameters portletParameters) {
		MutablePortletParameters oldMutablePortletParameters = clone();

		Map<String, String[]> parameterMap = getParameterMap();

		if (portletParameters instanceof BasePortletParametersImpl) {
			BasePortletParametersImpl<?> basePortletParametersImpl =
				(BasePortletParametersImpl<?>)portletParameters;

			Map<String, String[]> liferayPortletParameterMap =
				basePortletParametersImpl.getParameterMap();

			for (Map.Entry<String, String[]> entry :
					liferayPortletParameterMap.entrySet()) {

				String[] values = entry.getValue();

				String[] copiedValues = values.clone();

				parameterMap.put(entry.getKey(), copiedValues);
			}
		}
		else {
			for (String newParameterName : portletParameters.getNames()) {
				String[] values = portletParameters.getValues(newParameterName);

				String[] copiedValues = values.clone();

				parameterMap.put(newParameterName, copiedValues);
			}
		}

		_mutated = true;

		return oldMutablePortletParameters;
	}

	@Override
	public void clear() {
		Map<String, String[]> parameterMap = getParameterMap();

		parameterMap.clear();

		_mutated = true;
	}

	@Override
	public boolean isMutated() {
		return _mutated;
	}

	@Override
	public boolean removeParameter(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		Map<String, String[]> parameterMap = getParameterMap();

		if (parameterMap.containsKey(name)) {
			parameterMap.remove(name);

			_mutated = true;

			return true;
		}

		return false;
	}

	@Override
	public MutablePortletParameters set(PortletParameters portletParameters) {
		MutablePortletParameters oldMutablePortletParameters = clone();

		Set<String> oldParameterNames = oldMutablePortletParameters.getNames();

		if (portletParameters instanceof BasePortletParametersImpl) {
			BasePortletParametersImpl<?> basePortletParametersImpl =
				(BasePortletParametersImpl<?>)portletParameters;

			Map<String, String[]> liferayPortletParameterMap =
				basePortletParametersImpl.getParameterMap();

			if (oldParameterNames.isEmpty() &&
				liferayPortletParameterMap.isEmpty()) {

				return oldMutablePortletParameters;
			}

			Map<String, String[]> parameterMap = getParameterMap();

			parameterMap.clear();

			for (Map.Entry<String, String[]> entry :
					liferayPortletParameterMap.entrySet()) {

				String[] values = entry.getValue();

				String[] copiedValues = values.clone();

				parameterMap.put(entry.getKey(), copiedValues);
			}
		}
		else {
			Set<String> newParameterNames = portletParameters.getNames();

			if (oldParameterNames.isEmpty() && newParameterNames.isEmpty()) {
				return oldMutablePortletParameters;
			}

			Map<String, String[]> parameterMap = getParameterMap();

			parameterMap.clear();

			for (String newParameterName : newParameterNames) {
				String[] values = portletParameters.getValues(newParameterName);

				String[] copiedValues = values.clone();

				parameterMap.put(newParameterName, copiedValues);
			}
		}

		_mutated = true;

		return oldMutablePortletParameters;
	}

	@Override
	public String setValue(String name, String value) {
		String[] oldValues = setValues(name, new String[] {value});

		_mutated = true;

		if ((oldValues != null) && (oldValues.length > 0)) {
			return oldValues[0];
		}

		return null;
	}

	@Override
	public String setValue(String name, String value, boolean append) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		String[] oldValues = getValues(name);

		if (value == null) {
			removeParameter(name);
		}
		else {
			Map<String, String[]> parameterMap = getParameterMap();

			if (append && (oldValues != null)) {
				String[] newValues = ArrayUtil.append(oldValues, value);

				parameterMap.put(name, newValues);
			}
			else {
				parameterMap.put(name, new String[] {value});
			}
		}

		_mutated = true;

		if ((oldValues != null) && (oldValues.length > 0)) {
			return oldValues[0];
		}

		return null;
	}

	@Override
	public String[] setValues(String name, String... values) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		_mutated = true;

		Map<String, String[]> parameterMap = getParameterMap();

		return parameterMap.put(name, values);
	}

	@Override
	public String[] setValues(String name, String[] values, boolean append) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		String[] oldValues = getValues(name);

		if (values == null) {
			removeParameter(name);
		}
		else {
			Map<String, String[]> parameterMap = getParameterMap();

			if (append && (oldValues != null)) {
				String[] newValues = ArrayUtil.append(oldValues, values);

				parameterMap.put(name, newValues);
			}
			else {
				parameterMap.put(name, values);
			}
		}

		_mutated = true;

		return oldValues;
	}

	private boolean _mutated;

}