/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.portlet.InvokerFilterContainer;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.InvokerPortletFactory;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;

/**
 * @author Shuyang Zhou
 * @author Neil Griffin
 */
@OSGiBeanProperties(property = "service.ranking:Integer=1")
public class InvokerPortletFactoryImpl implements InvokerPortletFactory {

	@Override
	public InvokerPortlet create(
			com.liferay.portal.kernel.model.Portlet portletModel,
			Portlet portlet, PortletConfig portletConfig,
			PortletContext portletContext,
			InvokerFilterContainer invokerFilterContainer,
			boolean checkAuthToken, boolean facesPortlet, boolean headerPortlet)
		throws PortletException {

		try {
			return new InvokerPortletImpl(
				portletModel, portlet, portletConfig, portletContext,
				invokerFilterContainer, checkAuthToken, facesPortlet,
				headerPortlet);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	@Override
	public InvokerPortlet create(
			com.liferay.portal.kernel.model.Portlet portletModel,
			Portlet portlet, PortletContext portletContext,
			InvokerFilterContainer invokerFilterContainer)
		throws PortletException {

		try {
			return new InvokerPortletImpl(
				portletModel, portlet, portletContext, invokerFilterContainer);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

}