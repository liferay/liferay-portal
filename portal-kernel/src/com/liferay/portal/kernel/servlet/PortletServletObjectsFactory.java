/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Deepak Gothe
 */
public class PortletServletObjectsFactory implements ServletObjectsFactory {

	@Override
	public ServletConfig getServletConfig(
		PortletConfig portletConfig, PortletRequest portletRequest) {

		PortletContext portletContext = portletConfig.getPortletContext();

		Object servletConfig = portletContext.getAttribute(
			_PORTLET_CONTAINER_SERVLET_CONFIG);

		if (servletConfig == null) {
			servletConfig = portletRequest.getAttribute(
				PortletServlet.PORTLET_SERVLET_CONFIG);
		}

		return (ServletConfig)servletConfig;
	}

	@Override
	public HttpServletRequest getServletRequest(PortletRequest portletRequest) {
		Object request = portletRequest.getAttribute(
			_PORTLET_CONTAINER_SERVLET_REQUEST);

		if (request == null) {
			request = portletRequest.getAttribute(
				PortletServlet.PORTLET_SERVLET_REQUEST);
		}

		return (HttpServletRequest)request;
	}

	@Override
	public HttpServletResponse getServletResponse(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		Object response = portletRequest.getAttribute(
			_PORTLET_CONTAINER_SERVLET_RESPONSE);

		if (response == null) {
			response = portletRequest.getAttribute(
				PortletServlet.PORTLET_SERVLET_RESPONSE);
		}

		return (HttpServletResponse)response;
	}

	private static final String _PORTLET_CONTAINER_SERVLET_CONFIG =
		"jakarta.portlet.portletc.servletConfig";

	private static final String _PORTLET_CONTAINER_SERVLET_REQUEST =
		"jakarta.portlet.portletc.httpServletRequest";

	private static final String _PORTLET_CONTAINER_SERVLET_RESPONSE =
		"jakarta.portlet.portletc.httpServletResponse";

}