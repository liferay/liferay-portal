/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.beans;

import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletSession;
import jakarta.portlet.RenderParameters;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.Cookie;

import java.security.Principal;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * @author Neil Griffin
 */
public class DummyPortletRequest implements PortletRequest {

	public static final PortletRequest INSTANCE = new DummyPortletRequest();

	@Override
	public Object getAttribute(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAuthType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getContextPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Cookie[] getCookies() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<Locale> getLocales() {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("deprecation")
	public String getParameter(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("deprecation")
	public Map<String, String[]> getParameterMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("deprecation")
	public Enumeration<String> getParameterNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("deprecation")
	public String[] getParameterValues(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PortalContext getPortalContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public PortletContext getPortletContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public PortletMode getPortletMode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public PortletSession getPortletSession() {
		throw new UnsupportedOperationException();
	}

	@Override
	public PortletSession getPortletSession(boolean create) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PortletPreferences getPreferences() {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("deprecation")
	public Map<String, String[]> getPrivateParameterMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<String> getProperties(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProperty(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("deprecation")
	public Map<String, String[]> getPublicParameterMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRemoteUser() {
		throw new UnsupportedOperationException();
	}

	@Override
	public RenderParameters getRenderParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRequestedSessionId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getResponseContentType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<String> getResponseContentTypes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getScheme() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getServerName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getServerPort() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getUserAgent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Principal getUserPrincipal() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getWindowID() {
		throw new UnsupportedOperationException();
	}

	@Override
	public WindowState getWindowState() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isPortletModeAllowed(PortletMode portletMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSecure() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isUserInRole(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWindowStateAllowed(WindowState windowState) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAttribute(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAttribute(String name, Object value) {
		throw new UnsupportedOperationException();
	}

}