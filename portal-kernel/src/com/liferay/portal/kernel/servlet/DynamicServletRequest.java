/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Sampsa Sohlman
 */
public class DynamicServletRequest extends HttpServletRequestWrapper {

	public static final String DYNAMIC_QUERY_STRING = "DYNAMIC_QUERY_STRING";

	public static HttpServletRequest addQueryString(
		HttpServletRequest httpServletRequest,
		Map<String, String[]> parameterMap, String queryString) {

		return addQueryString(
			httpServletRequest, parameterMap, queryString, true);
	}

	public static HttpServletRequest addQueryString(
		HttpServletRequest httpServletRequest,
		Map<String, String[]> parameterMap, String queryString,
		boolean inherit) {

		String[] parameters = StringUtil.split(queryString, CharPool.AMPERSAND);

		if (parameters.length == 0) {
			return httpServletRequest;
		}

		parameterMap = new HashMap<>(parameterMap);

		for (String parameter : parameters) {
			String[] parameterParts = StringUtil.split(
				parameter, CharPool.EQUAL);

			String name = parameterParts[0];

			String value = StringPool.BLANK;

			if (parameterParts.length == 2) {
				value = parameterParts[1];
			}

			String[] values = parameterMap.get(name);

			if (values == null) {
				parameterMap.put(name, new String[] {value});
			}
			else {
				String[] newValues = new String[values.length + 1];

				System.arraycopy(values, 0, newValues, 0, values.length);

				newValues[newValues.length - 1] = value;

				parameterMap.put(name, newValues);
			}
		}

		httpServletRequest = new DynamicServletRequest(
			httpServletRequest, parameterMap, inherit);

		httpServletRequest.setAttribute(DYNAMIC_QUERY_STRING, queryString);

		return httpServletRequest;
	}

	public static HttpServletRequest addQueryString(
		HttpServletRequest httpServletRequest, String queryString) {

		return addQueryString(
			httpServletRequest, new HashMap<String, String[]>(), queryString,
			true);
	}

	public static HttpServletRequest addQueryString(
		HttpServletRequest httpServletRequest, String queryString,
		boolean inherit) {

		return addQueryString(
			httpServletRequest, new HashMap<String, String[]>(), queryString,
			inherit);
	}

	public DynamicServletRequest(HttpServletRequest httpServletRequest) {
		this(httpServletRequest, null, true);
	}

	public DynamicServletRequest(
		HttpServletRequest httpServletRequest, boolean inherit) {

		this(httpServletRequest, null, inherit);
	}

	public DynamicServletRequest(
		HttpServletRequest httpServletRequest, Map<String, String[]> params) {

		this(httpServletRequest, params, true);
	}

	public DynamicServletRequest(
		HttpServletRequest httpServletRequest, Map<String, String[]> params,
		boolean inherit) {

		super(httpServletRequest);

		_inherit = inherit;

		_params = new HashMap<>();

		if (params != null) {
			_params.putAll(params);
		}

		if (_inherit && (httpServletRequest instanceof DynamicServletRequest)) {
			DynamicServletRequest dynamicServletRequest =
				(DynamicServletRequest)httpServletRequest;

			dynamicServletRequest.injectInto(this);

			params = dynamicServletRequest.getDynamicParameterMap();

			for (Map.Entry<String, String[]> entry : params.entrySet()) {
				String name = entry.getKey();
				String[] oldValues = entry.getValue();

				String[] curValues = _params.get(name);

				if (curValues == null) {
					_params.put(name, oldValues);
				}
				else {
					String[] newValues = ArrayUtil.append(oldValues, curValues);

					_params.put(name, newValues);
				}
			}
		}
	}

	public void appendParameter(String name, String value) {
		String[] values = _params.get(name);

		if (values == null) {
			values = new String[] {value};
		}
		else {
			String[] newValues = new String[values.length + 1];

			System.arraycopy(values, 0, newValues, 0, values.length);

			newValues[newValues.length - 1] = value;

			values = newValues;
		}

		_params.put(name, values);
	}

	public Map<String, String[]> getDynamicParameterMap() {
		return _params;
	}

	@Override
	public String getParameter(String name) {
		String[] values = _params.get(name);

		if (_inherit && (values == null)) {
			return super.getParameter(name);
		}

		if (ArrayUtil.isNotEmpty(values)) {
			return values[0];
		}

		return null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> map = new HashMap<>();

		if (_inherit) {
			map.putAll(super.getParameterMap());
		}

		map.putAll(_params);

		return map;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		if (_params.isEmpty()) {
			if (_inherit) {
				return super.getParameterNames();
			}

			return Collections.emptyEnumeration();
		}

		Set<String> names = null;

		if (_inherit) {
			Enumeration<String> enumeration = super.getParameterNames();

			while (enumeration.hasMoreElements()) {
				if (names == null) {
					names = new LinkedHashSet<>();
				}

				names.add(enumeration.nextElement());
			}
		}

		if (names == null) {
			names = _params.keySet();
		}
		else {
			names.addAll(_params.keySet());
		}

		return Collections.enumeration(names);
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] values = _params.get(name);

		if (_inherit && (values == null)) {
			return super.getParameterValues(name);
		}

		return values;
	}

	public void setParameter(String name, String value) {
		_params.put(name, new String[] {value});
	}

	public void setParameterValues(String name, String[] values) {
		_params.put(name, values);
	}

	protected void injectInto(DynamicServletRequest dynamicServletRequest) {
		dynamicServletRequest.setRequest(getRequest());
	}

	private final boolean _inherit;
	private final Map<String, String[]> _params;

}