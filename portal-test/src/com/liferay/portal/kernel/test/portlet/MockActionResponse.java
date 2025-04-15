/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import jakarta.portlet.ActionResponse;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletModeException;
import jakarta.portlet.RenderURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;

import java.io.IOException;

import java.util.Map;

/**
 * @author Dante Wang
 */
public class MockActionResponse
	extends MockStateAwareResponse implements ActionResponse {

	@Override
	public RenderURL createRedirectURL(MimeResponse.Copy copy)
		throws IllegalStateException {

		return new MockRenderURL(getPortalContext(), copy);
	}

	public String getRedirectedUrl() {
		return _redirectedUrl;
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		if (!_redirectAllowed) {
			throw new IllegalStateException(
				"Cannot call sendRedirect after windowState, portletMode, or " +
					"renderParameters have been set");
		}

		_redirectedUrl = location;
	}

	@Override
	public void sendRedirect(String location, String renderUrlParamName)
		throws IOException {

		sendRedirect(location);

		if (renderUrlParamName != null) {
			setRenderParameter(renderUrlParamName, location);
		}
	}

	@Override
	public void setPortletMode(PortletMode portletMode)
		throws PortletModeException {

		if (_redirectedUrl != null) {
			throw new IllegalStateException(
				"Cannot set portlet mode after sendRedirect has been called");
		}

		super.setPortletMode(portletMode);

		_redirectAllowed = false;
	}

	@Override
	public void setRenderParameter(String key, String value) {
		if (_redirectedUrl != null) {
			throw new IllegalStateException(
				"Cannot set render parameter after sendRedirect has been " +
					"called");
		}

		super.setRenderParameter(key, value);

		_redirectAllowed = false;
	}

	@Override
	public void setRenderParameter(String key, String[] values) {
		if (_redirectedUrl != null) {
			throw new IllegalStateException(
				"Cannot set render parameter after sendRedirect has been " +
					"called");
		}

		super.setRenderParameter(key, values);

		_redirectAllowed = false;
	}

	@Override
	public void setRenderParameters(Map<String, String[]> parameters) {
		if (_redirectedUrl != null) {
			throw new IllegalStateException(
				"Cannot set render parameters after sendRedirect has been " +
					"called");
		}

		super.setRenderParameters(parameters);

		_redirectAllowed = false;
	}

	@Override
	public void setWindowState(WindowState windowState)
		throws WindowStateException {

		if (_redirectedUrl != null) {
			throw new IllegalStateException(
				"Cannot set window state after sendRedirect has been called");
		}

		super.setWindowState(windowState);

		_redirectAllowed = false;
	}

	private boolean _redirectAllowed = true;
	private String _redirectedUrl;

}