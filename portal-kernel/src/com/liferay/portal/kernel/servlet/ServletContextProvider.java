/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.portlet.GenericPortlet;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Michael Young
 */
public interface ServletContextProvider {

	public HttpServletRequest getHttpServletRequest(
		GenericPortlet portlet, PortletRequest portletRequest);

	public HttpServletResponse getHttpServletResponse(
		GenericPortlet portlet, PortletResponse portletResponse);

	public ServletContext getServletContext(GenericPortlet portlet);

	public ServletContext getServletContext(ServletContext servletContext);

}