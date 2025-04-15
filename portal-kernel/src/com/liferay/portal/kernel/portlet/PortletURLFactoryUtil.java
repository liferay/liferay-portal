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

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
public class PortletURLFactoryUtil {

	public static LiferayPortletURL create(
		HttpServletRequest httpServletRequest, Portlet portlet, Layout layout,
		String lifecycle) {

		return _portletURLFactory.create(
			httpServletRequest, portlet, layout, lifecycle);
	}

	public static LiferayPortletURL create(
		HttpServletRequest httpServletRequest, Portlet portlet, Layout layout,
		String lifecycle, MimeResponse.Copy copy) {

		return _portletURLFactory.create(
			httpServletRequest, portlet, layout, lifecycle, copy);
	}

	public static LiferayPortletURL create(
		HttpServletRequest httpServletRequest, Portlet portlet,
		String lifecycle) {

		return _portletURLFactory.create(
			httpServletRequest, portlet, lifecycle);
	}

	public static LiferayPortletURL create(
		HttpServletRequest httpServletRequest, String portletId, Layout layout,
		String lifecycle) {

		return _portletURLFactory.create(
			httpServletRequest, portletId, layout, lifecycle);
	}

	public static LiferayPortletURL create(
		HttpServletRequest httpServletRequest, String portletId, long plid,
		String lifecycle) {

		return _portletURLFactory.create(
			httpServletRequest, portletId, plid, lifecycle);
	}

	public static LiferayPortletURL create(
		HttpServletRequest httpServletRequest, String portletId,
		String lifecycle) {

		return _portletURLFactory.create(
			httpServletRequest, portletId, lifecycle);
	}

	public static LiferayPortletURL create(
		PortletRequest portletRequest, Portlet portlet, Layout layout,
		String lifecycle) {

		return _portletURLFactory.create(
			portletRequest, portlet, layout, lifecycle);
	}

	public static LiferayPortletURL create(
		PortletRequest portletRequest, Portlet portlet, long plid,
		String lifecycle) {

		return _portletURLFactory.create(
			portletRequest, portlet, plid, lifecycle);
	}

	public static LiferayPortletURL create(
		PortletRequest portletRequest, Portlet portlet, long plid,
		String lifecycle, MimeResponse.Copy copy) {

		return _portletURLFactory.create(
			portletRequest, portlet, plid, lifecycle, copy);
	}

	public static LiferayPortletURL create(
		PortletRequest portletRequest, String portletId, Layout layout,
		String lifecycle) {

		return _portletURLFactory.create(
			portletRequest, portletId, layout, lifecycle);
	}

	public static LiferayPortletURL create(
		PortletRequest portletRequest, String portletId, long plid,
		String lifecycle) {

		return _portletURLFactory.create(
			portletRequest, portletId, plid, lifecycle);
	}

	public static LiferayPortletURL create(
		PortletRequest portletRequest, String portletId, long plid,
		String lifecycle, MimeResponse.Copy copy) {

		return _portletURLFactory.create(
			portletRequest, portletId, plid, lifecycle, copy);
	}

	public static LiferayPortletURL create(
		PortletRequest portletRequest, String portletId, String lifecycle) {

		return _portletURLFactory.create(portletRequest, portletId, lifecycle);
	}

	public static PortletURLFactory getPortletURLFactory() {
		return _portletURLFactory;
	}

	public void setPortletURLFactory(PortletURLFactory portletURLFactory) {
		_portletURLFactory = portletURLFactory;
	}

	private static PortletURLFactory _portletURLFactory;

}