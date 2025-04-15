/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;

import jakarta.portlet.Event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Shuyang Zhou
 * @author Neil Griffin
 */
@ProviderType
public interface PortletContainer {

	public void preparePortlet(
			HttpServletRequest httpServletRequest, Portlet portlet)
		throws PortletContainerException;

	public ActionResult processAction(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException;

	public List<Event> processEvent(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet,
			Layout layout, Event event)
		throws PortletContainerException;

	public void processPublicRenderParameters(
		HttpServletRequest httpServletRequest, Layout layout);

	public void processPublicRenderParameters(
		HttpServletRequest httpServletRequest, Layout layout, Portlet portlet);

	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException;

	public void renderHeaders(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException;

	public void serveResource(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException;

}