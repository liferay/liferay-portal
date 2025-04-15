/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.Cookie;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Dante Wang
 */
public class MockPortletResponse implements PortletResponse {

	public MockPortletResponse() {
		this(null);
	}

	public MockPortletResponse(PortalContext portalContext) {
		_portalContext = Objects.requireNonNullElse(
			portalContext, new MockPortalContext());
	}

	@Override
	public void addProperty(Cookie cookie) {
		_cookies.add(cookie);
	}

	@Override
	public void addProperty(String key, Element value) {
		Element[] oldElements = _xmlProperties.get(key);

		if (oldElements != null) {
			_xmlProperties.put(key, ArrayUtil.append(oldElements, value));
		}
		else {
			_xmlProperties.put(key, new Element[] {value});
		}
	}

	@Override
	public void addProperty(String key, String value) {
		String[] values = _properties.get(key);

		if (values != null) {
			_properties.put(key, ArrayUtil.append(values, value));
		}
		else {
			_properties.put(key, new String[] {value});
		}
	}

	@Override
	public Element createElement(String tagName) throws DOMException {
		if (_xmlDocument == null) {
			try {
				DocumentBuilderFactory documentBuilderFactory =
					DocumentBuilderFactory.newInstance();

				DocumentBuilder documentBuilder =
					documentBuilderFactory.newDocumentBuilder();

				_xmlDocument = documentBuilder.newDocument();
			}
			catch (ParserConfigurationException parserConfigurationException) {
				throw new DOMException(
					DOMException.INVALID_STATE_ERR,
					parserConfigurationException.toString());
			}
		}

		return _xmlDocument.createElement(tagName);
	}

	@Override
	public String encodeURL(String path) {
		return path;
	}

	public Cookie getCookie(String name) {
		for (Cookie cookie : _cookies) {
			if (name.equals(cookie.getName())) {
				return cookie;
			}
		}

		return null;
	}

	public Cookie[] getCookies() {
		return _cookies.toArray(new Cookie[0]);
	}

	@Override
	public String getNamespace() {
		return _namespace;
	}

	public PortalContext getPortalContext() {
		return _portalContext;
	}

	public String[] getProperties(String key) {
		return _properties.get(key);
	}

	public String getProperty(String key) {
		String[] values = _properties.get(key);

		if (ArrayUtil.isEmpty(values)) {
			return null;
		}

		return values[0];
	}

	public Set<String> getPropertyNames() {
		return Collections.unmodifiableSet(_properties.keySet());
	}

	@Override
	public Collection<String> getPropertyValues(String key) {
		return Arrays.asList(_properties.get(key));
	}

	public Element[] getXmlProperties(String key) {
		return _xmlProperties.get(key);
	}

	public Element getXmlProperty(String key) {
		Element[] elements = _xmlProperties.get(key);

		if (ArrayUtil.isEmpty(elements)) {
			return null;
		}

		return elements[0];
	}

	public Set<String> getXmlPropertyNames() {
		return Collections.unmodifiableSet(_xmlProperties.keySet());
	}

	public void setNamespace(String namespace) {
		_namespace = namespace;
	}

	@Override
	public void setProperty(String key, String value) {
		_properties.put(key, new String[] {value});
	}

	private final Set<Cookie> _cookies = new LinkedHashSet<>();
	private String _namespace = StringPool.BLANK;
	private final PortalContext _portalContext;
	private final Map<String, String[]> _properties = new LinkedHashMap<>();
	private Document _xmlDocument;
	private final Map<String, Element[]> _xmlProperties = new LinkedHashMap<>();

}