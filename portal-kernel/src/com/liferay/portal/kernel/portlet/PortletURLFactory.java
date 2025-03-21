/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
@ProviderType
public interface PortletURLFactory {

	public LiferayPortletURL create(
		HttpServletRequest httpServletRequest, Portlet portlet, Layout layout,
		String lifecycle);

	public LiferayPortletURL create(
		HttpServletRequest httpServletRequest, Portlet portlet, Layout layout,
		String lifecycle, MimeResponse.Copy copy);

	public LiferayPortletURL create(
		HttpServletRequest httpServletRequest, Portlet portlet,
		String lifecycle);

	public LiferayPortletURL create(
		HttpServletRequest httpServletRequest, String portletId, Layout layout,
		String lifecycle);

	public LiferayPortletURL create(
		HttpServletRequest httpServletRequest, String portletId, long plid,
		String lifecycle);

	public LiferayPortletURL create(
		HttpServletRequest httpServletRequest, String portletId,
		String lifecycle);

	public LiferayPortletURL create(
		PortletRequest portletRequest, Portlet portlet, Layout layout,
		String lifecycle);

	public LiferayPortletURL create(
		PortletRequest portletRequest, Portlet portlet, long plid,
		String lifecycle);

	public LiferayPortletURL create(
		PortletRequest portletRequest, Portlet portlet, long plid,
		String lifecycle, MimeResponse.Copy copy);

	public LiferayPortletURL create(
		PortletRequest portletRequest, String portletId, Layout layout,
		String lifecycle);

	public LiferayPortletURL create(
		PortletRequest portletRequest, String portletId, long plid,
		String lifecycle);

	public LiferayPortletURL create(
		PortletRequest portletRequest, String portletId, long plid,
		String lifecycle, MimeResponse.Copy copy);

	public LiferayPortletURL create(
		PortletRequest portletRequest, String portletId, String lifecycle);

}