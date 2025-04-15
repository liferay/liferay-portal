/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.model;

import com.liferay.portal.kernel.exception.PortalException;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Julio Camarero
 */
public interface Renderer {

	public String getClassName();

	public long getClassPK();

	public String getIconCssClass() throws PortalException;

	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse);

	public String getTitle(Locale locale);

	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception;

}