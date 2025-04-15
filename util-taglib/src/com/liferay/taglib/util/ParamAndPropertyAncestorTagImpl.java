/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.BaseBodyTagSupport;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class ParamAndPropertyAncestorTagImpl
	extends BaseBodyTagSupport
	implements ParamAncestorTag, PropertyAncestorTag {

	@Override
	public void addParam(String name, String value) {
		if (Validator.isNull(name)) {
			throw new IllegalArgumentException();
		}

		if (_dynamicServletRequest == null) {
			_dynamicServletRequest = new DynamicServletRequest(getRequest());

			_httpServletRequest = _dynamicServletRequest;
		}

		Map<String, String[]> params =
			_dynamicServletRequest.getDynamicParameterMap();

		// PLT.26.6

		if (!_allowEmptyParam && ((value == null) || (value.length() == 0))) {
			params.remove(name);

			if (_removedParameterNames == null) {
				_removedParameterNames = new HashSet<>();
			}

			_removedParameterNames.add(name);

			return;
		}

		String[] values = params.get(name);

		if (!_copyCurrentRenderParameters || (values == null)) {
			values = new String[] {value};
		}
		else {
			String[] newValues = new String[values.length + 1];

			System.arraycopy(values, 0, newValues, 0, values.length);

			newValues[newValues.length - 1] = value;

			values = newValues;
		}

		params.put(name, values);
	}

	@Override
	public void addProperty(String name, String value) {
		if (_properties == null) {
			_properties = new LinkedHashMap<>();
		}

		String[] values = _properties.get(name);

		if (!_copyCurrentRenderParameters || (values == null)) {
			values = new String[] {value};
		}
		else {
			String[] newValues = new String[values.length + 1];

			System.arraycopy(values, 0, newValues, 0, values.length);

			newValues[newValues.length - 1] = value;

			values = newValues;
		}

		_properties.put(name, values);
	}

	public void clearParams() {
		if (_dynamicServletRequest != null) {
			Map<String, String[]> params =
				_dynamicServletRequest.getDynamicParameterMap();

			params.clear();

			_httpServletRequest =
				(HttpServletRequest)_dynamicServletRequest.getRequest();

			_dynamicServletRequest = null;
		}

		if (_removedParameterNames != null) {
			_removedParameterNames.clear();
		}
	}

	public void clearProperties() {
		if (_properties != null) {
			_properties.clear();
		}
	}

	public Map<String, String[]> getParams() {
		if (_dynamicServletRequest != null) {
			return _dynamicServletRequest.getDynamicParameterMap();
		}

		return null;
	}

	public Map<String, String[]> getProperties() {
		return _properties;
	}

	public Set<String> getRemovedParameterNames() {
		return _removedParameterNames;
	}

	@Override
	public HttpServletRequest getRequest() {
		if (_dynamicServletRequest != null) {
			return _dynamicServletRequest;
		}

		if (_httpServletRequest != null) {
			return _httpServletRequest;
		}

		return super.getRequest();
	}

	public boolean isAllowEmptyParam() {
		return _allowEmptyParam;
	}

	@Override
	public void release() {
		super.release();

		_httpServletRequest = null;
		_servletContext = null;

		_allowEmptyParam = false;
		_copyCurrentRenderParameters = true;
		_properties = null;
		_removedParameterNames = null;
	}

	public void setAllowEmptyParam(boolean allowEmptyParam) {
		_allowEmptyParam = allowEmptyParam;
	}

	public void setCopyCurrentRenderParameters(
		boolean copyCurrentRenderParameters) {

		_copyCurrentRenderParameters = copyCurrentRenderParameters;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		_httpServletRequest = (HttpServletRequest)pageContext.getRequest();

		_servletContext = (ServletContext)_httpServletRequest.getAttribute(
			WebKeys.CTX);

		if (_servletContext == null) {
			_servletContext = pageContext.getServletContext();
		}
	}

	public void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	protected ServletContext getServletContext() {
		return _servletContext;
	}

	private boolean _allowEmptyParam;
	private boolean _copyCurrentRenderParameters = true;
	private DynamicServletRequest _dynamicServletRequest;
	private HttpServletRequest _httpServletRequest;
	private Map<String, String[]> _properties;
	private Set<String> _removedParameterNames;
	private ServletContext _servletContext;

}