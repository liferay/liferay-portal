/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.LiferayRenderRequest;
import com.liferay.portlet.internal.RenderRequestImpl;

import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
public class RenderRequestFactory {

	public static LiferayRenderRequest create(
		HttpServletRequest httpServletRequest, Portlet portlet,
		InvokerPortlet invokerPortlet, PortletContext portletContext,
		WindowState windowState, PortletMode portletMode,
		PortletPreferences portletPreferences) {

		return create(
			httpServletRequest, portlet, invokerPortlet, portletContext,
			windowState, portletMode, portletPreferences, 0);
	}

	public static LiferayRenderRequest create(
		HttpServletRequest httpServletRequest, Portlet portlet,
		InvokerPortlet invokerPortlet, PortletContext portletContext,
		WindowState windowState, PortletMode portletMode,
		PortletPreferences portletPreferences, long plid) {

		RenderRequestImpl renderRequestImpl = new RenderRequestImpl();

		renderRequestImpl.init(
			httpServletRequest, portlet, invokerPortlet, portletContext,
			windowState, portletMode, portletPreferences, plid);

		return renderRequestImpl;
	}

}