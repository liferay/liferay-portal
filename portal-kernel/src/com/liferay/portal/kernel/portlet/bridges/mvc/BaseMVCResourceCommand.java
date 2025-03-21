/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.bridges.mvc;

import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequestDispatcher;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.IOException;

/**
 * @author Brian Wing Shun Chan
 */
public abstract class BaseMVCResourceCommand implements MVCResourceCommand {

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		try {
			doServeResource(resourceRequest, resourceResponse);

			return !SessionErrors.isEmpty(resourceRequest);
		}
		catch (PortletException portletException) {
			throw portletException;
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	protected abstract void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception;

	protected PortletConfig getPortletConfig(ResourceRequest resourceRequest) {
		return PortletConfigFactoryUtil.get(
			PortalUtil.getPortletId(resourceRequest));
	}

	protected PortletRequestDispatcher getPortletRequestDispatcher(
		ResourceRequest resourceRequest, String path) {

		PortletConfig portletConfig = getPortletConfig(resourceRequest);

		PortletContext portletContext = portletConfig.getPortletContext();

		return portletContext.getRequestDispatcher(path);
	}

	protected void include(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse,
			String jspPath)
		throws IOException, PortletException {

		PortletConfig portletConfig = getPortletConfig(resourceRequest);

		PortletContext portletContext = portletConfig.getPortletContext();

		PortletRequestDispatcher portletRequestDispatcher =
			portletContext.getRequestDispatcher(jspPath);

		portletRequestDispatcher.include(resourceRequest, resourceResponse);
	}

}