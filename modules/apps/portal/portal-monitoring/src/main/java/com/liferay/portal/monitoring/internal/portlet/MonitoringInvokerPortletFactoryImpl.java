/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.portlet;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.monitoring.DataSampleFactory;
import com.liferay.portal.kernel.portlet.InvokerFilterContainer;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.InvokerPortletFactory;
import com.liferay.portal.monitoring.internal.configuration.MonitoringConfiguration;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;

import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 * @author Philip Jones
 * @author Neil Griffin
 */
@Component(
	configurationPid = "com.liferay.portal.monitoring.internal.configuration.MonitoringConfiguration",
	enabled = false, property = Constants.SERVICE_RANKING + ":Integer=100",
	service = InvokerPortletFactory.class
)
public class MonitoringInvokerPortletFactoryImpl
	implements InvokerPortletFactory {

	@Override
	public InvokerPortlet create(
			com.liferay.portal.kernel.model.Portlet portletModel,
			Portlet portlet, PortletConfig portletConfig,
			PortletContext portletContext,
			InvokerFilterContainer invokerFilterContainer,
			boolean checkAuthToken, boolean facesPortlet, boolean headerPortlet)
		throws PortletException {

		InvokerPortlet invokerPortlet = _invokerPortletFactory.create(
			portletModel, portlet, portletConfig, portletContext,
			invokerFilterContainer, checkAuthToken, facesPortlet,
			headerPortlet);

		return new MonitoringInvokerPortlet(
			_dataSampleFactory, invokerPortlet, _monitoringConfiguration);
	}

	@Override
	public InvokerPortlet create(
			com.liferay.portal.kernel.model.Portlet portletModel,
			Portlet portlet, PortletContext portletContext,
			InvokerFilterContainer invokerFilterContainer)
		throws PortletException {

		InvokerPortlet invokerPortlet = _invokerPortletFactory.create(
			portletModel, portlet, portletContext, invokerFilterContainer);

		return new MonitoringInvokerPortlet(
			_dataSampleFactory, invokerPortlet, _monitoringConfiguration);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_monitoringConfiguration = ConfigurableUtil.createConfigurable(
			MonitoringConfiguration.class, properties);
	}

	@Reference
	private DataSampleFactory _dataSampleFactory;

	@Reference(target = "(" + Constants.SERVICE_RANKING + "=1)")
	private InvokerPortletFactory _invokerPortletFactory;

	private volatile MonitoringConfiguration _monitoringConfiguration;

}