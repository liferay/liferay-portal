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
public interface PortletInstanceFactory {

	public void clear(Portlet portlet);

	public void clear(Portlet portlet, boolean resetRemotePortletBag);

	public InvokerPortlet create(Portlet portlet, ServletContext servletContext)
		throws PortletException;

	public InvokerPortlet create(
			Portlet portlet, ServletContext servletContext,
			boolean destroyPrevious)
		throws PortletException;

	public void delete(Portlet portlet);

	public void destroy(Portlet portlet);

}