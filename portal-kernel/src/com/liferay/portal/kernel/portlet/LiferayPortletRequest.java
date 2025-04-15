/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.model.Portlet;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface LiferayPortletRequest extends PortletRequest {

	public void cleanUp();

	public Map<String, String[]> clearRenderParameters();

	public void defineObjects(
		PortletConfig portletConfig, PortletResponse portletResponse);

	public HttpServletRequest getHttpServletRequest();

	public String getLifecycle();

	public HttpServletRequest getOriginalHttpServletRequest();

	public long getPlid();

	public Portlet getPortlet();

	public String getPortletName();

	public HttpServletRequest getPortletRequestDispatcherRequest();

	public void invalidateSession();

	public void setPortletRequestDispatcherRequest(
		HttpServletRequest httpServletRequest);

}