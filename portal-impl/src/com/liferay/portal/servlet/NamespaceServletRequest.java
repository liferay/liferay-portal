/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.portal.kernel.portlet.RestrictPortletServletRequest;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.util.JavaConstants;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * This class ensures that portlet attributes and parameters are private to the
 * portlet.
 * </p>
 *
 * @author Brian Myunghun Kim
 */
public class NamespaceServletRequest extends DynamicServletRequest {

	public static Set<String> reservedAttrs = new HashSet<String>() {
		{
			add(JavaConstants.JAVAX_PORTLET_CONFIG);
			add(JavaConstants.JAVAX_PORTLET_PORTLET);
			add(JavaConstants.JAVAX_PORTLET_REQUEST);
			add(JavaConstants.JAVAX_PORTLET_RESPONSE);
			add(JavaConstants.JAVAX_SERVLET_FORWARD_CONTEXT_PATH);
			add(JavaConstants.JAVAX_SERVLET_FORWARD_PATH_INFO);
			add(JavaConstants.JAVAX_SERVLET_FORWARD_QUERY_STRING);
			add(JavaConstants.JAVAX_SERVLET_FORWARD_REQUEST_URI);
			add(JavaConstants.JAVAX_SERVLET_FORWARD_SERVLET_PATH);
			add(JavaConstants.JAVAX_SERVLET_INCLUDE_CONTEXT_PATH);
			add(JavaConstants.JAVAX_SERVLET_INCLUDE_PATH_INFO);
			add(JavaConstants.JAVAX_SERVLET_INCLUDE_QUERY_STRING);
			add(JavaConstants.JAVAX_SERVLET_INCLUDE_REQUEST_URI);
			add(JavaConstants.JAVAX_SERVLET_INCLUDE_SERVLET_PATH);
			add(MimeResponse.MARKUP_HEAD_ELEMENT);
			add(PortletRequest.LIFECYCLE_PHASE);
		}
	};

	public NamespaceServletRequest(
		HttpServletRequest httpServletRequest, String attrNamespace,
		String paramNamespace) {

		this(httpServletRequest, attrNamespace, paramNamespace, true);
	}

	public NamespaceServletRequest(
		HttpServletRequest httpServletRequest, String attrNamespace,
		String paramNamespace, boolean inherit) {

		super(httpServletRequest, inherit);

		_attrNamespace = attrNamespace;
		_paramNamespace = paramNamespace;
	}

	@Override
	public Object getAttribute(String name) {
		Object value = super.getAttribute(_attrNamespace + name);

		if (value == null) {
			value = super.getAttribute(name);
		}

		return value;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		List<String> names = new ArrayList<>();

		Enumeration<String> enumeration = super.getAttributeNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			if (name.startsWith(_attrNamespace)) {
				names.add(name.substring(_attrNamespace.length()));
			}
			else if (_isReservedParam(name)) {
				names.add(name);
			}
		}

		return Collections.enumeration(names);
	}

	@Override
	public String getParameter(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		String value = super.getParameter(name);

		if (value == null) {
			value = super.getParameter(_paramNamespace + name);
		}

		return value;
	}

	@Override
	public void removeAttribute(String name) {
		if (_isReservedParam(name)) {
			super.removeAttribute(name);
		}
		else {
			super.removeAttribute(_attrNamespace + name);
		}
	}

	@Override
	public void setAttribute(String name, Object value) {
		if (_isReservedParam(name)) {
			super.setAttribute(name, value);
		}
		else {
			super.setAttribute(_attrNamespace + name, value);
		}
	}

	public void setAttribute(
		String name, Object value, boolean privateRequestAttribute) {

		if (!privateRequestAttribute) {
			super.setAttribute(name, value);
		}
		else {
			setAttribute(name, value);
		}
	}

	@Override
	protected void injectInto(DynamicServletRequest dynamicServletRequest) {
		dynamicServletRequest.setRequest(
			new NamespaceServletRequest(
				(HttpServletRequest)getRequest(), _attrNamespace,
				_paramNamespace));
	}

	private boolean _isReservedParam(String name) {
		if (reservedAttrs.contains(name) ||
			RestrictPortletServletRequest.isSharedRequestAttribute(name)) {

			return true;
		}

		return false;
	}

	private final String _attrNamespace;
	private final String _paramNamespace;

}