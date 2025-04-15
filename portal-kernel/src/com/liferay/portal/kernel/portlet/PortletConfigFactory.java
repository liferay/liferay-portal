/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.model.Portlet;

import jakarta.portlet.PortletConfig;

import jakarta.servlet.ServletContext;

/**
 * @author Brian Wing Shun Chan
 */
public interface PortletConfigFactory {

	public PortletConfig create(Portlet portlet, ServletContext servletContext);

	public void destroy(Portlet portlet);

	public PortletConfig get(Portlet portlet);

	public PortletConfig get(String portletId);

	public PortletConfig update(Portlet portlet);

}