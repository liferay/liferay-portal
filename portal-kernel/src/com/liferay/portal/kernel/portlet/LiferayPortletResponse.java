/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.servlet.URLEncoder;

import jakarta.portlet.ActionURL;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderURL;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Raymond Augé
 * @author Neil Griffin
 */
@ProviderType
public interface LiferayPortletResponse extends PortletResponse {

	public void addDateHeader(String name, long date);

	public void addHeader(String name, String value);

	public void addIntHeader(String name, int value);

	public <T extends PortletURL & ActionURL> T createActionURL();

	public ActionURL createActionURL(MimeResponse.Copy copy);

	public LiferayPortletURL createActionURL(String portletName);

	public LiferayPortletURL createActionURL(
		String portletName, MimeResponse.Copy copy);

	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle);

	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle,
		boolean includeLinkToLayoutUuid);

	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle,
		MimeResponse.Copy copy);

	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle, MimeResponse.Copy copy,
		boolean includeLinkToLayoutUuid);

	public LiferayPortletURL createLiferayPortletURL(String lifecycle);

	public LiferayPortletURL createLiferayPortletURL(
		String portletName, String lifecycle);

	public LiferayPortletURL createLiferayPortletURL(
		String portletName, String lifecycle, MimeResponse.Copy copy);

	public <T extends PortletURL & RenderURL> T createRenderURL();

	public RenderURL createRenderURL(MimeResponse.Copy copy);

	public LiferayPortletURL createRenderURL(String portletName);

	public LiferayPortletURL createRenderURL(
		String portletName, MimeResponse.Copy copy);

	public ResourceURL createResourceURL();

	public LiferayPortletURL createResourceURL(String portletName);

	public HttpServletResponse getHttpServletResponse();

	public String getLifecycle();

	public Portlet getPortlet();

	public Map<String, String[]> getProperties();

	public void setDateHeader(String name, long date);

	public void setHeader(String name, String value);

	public void setIntHeader(String name, int value);

	public void setURLEncoder(URLEncoder urlEncoder);

	public void transferHeaders(HttpServletResponse httpServletResponse);

	public void transferMarkupHeadElements();

}