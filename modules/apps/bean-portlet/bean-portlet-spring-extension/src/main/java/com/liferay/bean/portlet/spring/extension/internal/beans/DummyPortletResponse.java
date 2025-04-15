/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.beans;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.Cookie;

import java.util.Collection;

import org.w3c.dom.Element;

/**
 * @author Neil Griffin
 */
public class DummyPortletResponse implements PortletResponse {

	public static final PortletResponse INSTANCE = new DummyPortletResponse();

	@Override
	public void addProperty(Cookie cookie) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addProperty(String key, Element element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addProperty(String key, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Element createElement(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeURL(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNamespace() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProperty(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> getPropertyNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> getPropertyValues(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setProperty(String key, String value) {
		throw new UnsupportedOperationException();
	}

}