/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.model.Portlet;

import jakarta.portlet.PortletException;

import jakarta.servlet.ServletContext;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletInstanceFactoryUtil {

	public static void clear(Portlet portlet) {
		_portletInstanceFactory.clear(portlet);
	}

	public static void clear(Portlet portlet, boolean resetRemotePortletBag) {
		_portletInstanceFactory.clear(portlet, resetRemotePortletBag);
	}

	public static InvokerPortlet create(
			Portlet portlet, ServletContext servletContext)
		throws PortletException {

		return _portletInstanceFactory.create(portlet, servletContext);
	}

	public static InvokerPortlet create(
			Portlet portlet, ServletContext servletContext,
			boolean destroyPrevious)
		throws PortletException {

		return _portletInstanceFactory.create(
			portlet, servletContext, destroyPrevious);
	}

	public static void delete(Portlet portlet) {
		_portletInstanceFactory.delete(portlet);
	}

	public static void destroy(Portlet portlet) {
		_portletInstanceFactory.destroy(portlet);
	}

	public static PortletInstanceFactory getPortletInstanceFactory() {
		return _portletInstanceFactory;
	}

	public void destroy() {

		// LPS-10473

	}

	public void setPortletInstanceFactory(
		PortletInstanceFactory portletInstanceFactory) {

		_portletInstanceFactory = portletInstanceFactory;
	}

	private static PortletInstanceFactory _portletInstanceFactory;

}