/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.field.render;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Carolina Barbosa
 */
public class ObjectFieldRenderingContext {

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	public long getGroupId() {
		return _groupId;
	}

	public HttpServletRequest getHttpServletRequest() {
		return _httpServletRequest;
	}

	public HttpServletResponse getHttpServletResponse() {
		return _httpServletResponse;
	}

	public Locale getLocale() {
		return _locale;
	}

	public String getPortletId() {
		return _portletId;
	}

	public Map<String, Object> getProperties() {
		return _properties;
	}

	public Object getProperty(String name) {
		return _properties.get(name);
	}

	public long getUserId() {
		return _userId;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		_externalReferenceCode = externalReferenceCode;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public void setHttpServletResponse(
		HttpServletResponse httpServletResponse) {

		_httpServletResponse = httpServletResponse;
	}

	public void setLocale(Locale locale) {
		_locale = locale;
	}

	public void setPortletId(String portletId) {
		_portletId = portletId;
	}

	public void setProperties(Map<String, Object> properties) {
		_properties.putAll(properties);
	}

	public void setProperty(String name, Object value) {
		_properties.put(name, value);
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	private String _externalReferenceCode;
	private long _groupId;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private Locale _locale;
	private String _portletId;
	private final Map<String, Object> _properties = new HashMap<>();
	private long _userId;

}