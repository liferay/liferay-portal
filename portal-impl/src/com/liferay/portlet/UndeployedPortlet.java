/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.language.LanguageUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.GenericPortlet;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequestDispatcher;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Tomas Polesovsky
 */
public class UndeployedPortlet extends GenericPortlet {

	public static UndeployedPortlet getInstance() {
		return _undeployedPortlet;
	}

	@Override
	public void processAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletContext portletContext = getPortletContext();

		PortletRequestDispatcher portletRequestDispatcher =
			portletContext.getRequestDispatcher(
				"/html/portal/undeployed_portlet.jsp");

		portletRequestDispatcher.include(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(
			ResourceRequest renderRequest, ResourceResponse renderResponse)
		throws IOException {

		PrintWriter printWriter = renderResponse.getWriter();

		printWriter.write(
			LanguageUtil.get(renderRequest.getLocale(), "undeployed"));
	}

	private static final UndeployedPortlet _undeployedPortlet =
		new UndeployedPortlet();

}