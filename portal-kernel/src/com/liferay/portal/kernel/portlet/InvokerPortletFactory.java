/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Shuyang Zhou
 * @author Neil Griffin
 */
@ProviderType
public interface InvokerPortletFactory {

	public InvokerPortlet create(
			com.liferay.portal.kernel.model.Portlet portletModel,
			Portlet portlet, PortletConfig portletConfig,
			PortletContext portletContext,
			InvokerFilterContainer invokerFilterContainer,
			boolean checkAuthToken, boolean facesPortlet, boolean headerPortlet)
		throws PortletException;

	public InvokerPortlet create(
			com.liferay.portal.kernel.model.Portlet portletModel,
			Portlet portlet, PortletContext portletContext,
			InvokerFilterContainer invokerFilterContainer)
		throws PortletException;

}