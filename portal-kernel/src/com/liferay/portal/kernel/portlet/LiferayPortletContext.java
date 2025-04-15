/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.model.Portlet;

import jakarta.portlet.PortletContext;

import jakarta.servlet.ServletContext;

/**
 * @author Raymond Augé
 */
public interface LiferayPortletContext extends PortletContext {

	public Portlet getPortlet();

	public ServletContext getServletContext();

}