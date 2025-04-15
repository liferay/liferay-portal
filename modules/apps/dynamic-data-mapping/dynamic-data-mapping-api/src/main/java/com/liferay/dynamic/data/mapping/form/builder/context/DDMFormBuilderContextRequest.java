/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.builder.context;

import com.liferay.dynamic.data.mapping.model.DDMStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Rafael Praxedes
 */
public class DDMFormBuilderContextRequest {

	public static DDMFormBuilderContextRequest with(
		DDMStructure ddmStructure, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Locale locale,
		boolean readOnly) {

		DDMFormBuilderContextRequest ddmFormBuilderContextRequest =
			new DDMFormBuilderContextRequest();

		if (ddmStructure != null) {
			ddmFormBuilderContextRequest.addProperty(
				"ddmStructure", ddmStructure);
		}

		ddmFormBuilderContextRequest.setHttpServletRequest(httpServletRequest);
		ddmFormBuilderContextRequest.setHttpServletResponse(
			httpServletResponse);
		ddmFormBuilderContextRequest.setLocale(locale);
		ddmFormBuilderContextRequest.setReadOnly(readOnly);

		return ddmFormBuilderContextRequest;
	}

	public void addProperty(String key, Object value) {
		_properties.put(key, value);
	}

	public HttpServletRequest getHttpServletRequest() {
		return getProperty("request");
	}

	public HttpServletResponse getHttpServletResponse() {
		return getProperty("response");
	}

	public Locale getLocale() {
		return getProperty("locale");
	}

	public <T> T getProperty(String name) {
		return (T)_properties.get(name);
	}

	public boolean getReadOnly() {
		return getProperty("readOnly");
	}

	public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		addProperty("request", httpServletRequest);
	}

	public void setHttpServletResponse(
		HttpServletResponse httpServletResponse) {

		addProperty("response", httpServletResponse);
	}

	public void setLocale(Locale locale) {
		addProperty("locale", locale);
	}

	public void setReadOnly(boolean readOnly) {
		addProperty("readOnly", readOnly);
	}

	private DDMFormBuilderContextRequest() {
	}

	private final Map<String, Object> _properties = new HashMap<>();

}