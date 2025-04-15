/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Deepak Gothe
 */
public interface ServletObjectsFactory {

	public ServletConfig getServletConfig(
		PortletConfig portletConfig, PortletRequest portletRequest);

	public HttpServletRequest getServletRequest(PortletRequest portletRequest);

	public HttpServletResponse getServletResponse(
		PortletRequest portletRequest, PortletResponse portletResponse);

}