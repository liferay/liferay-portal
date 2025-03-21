/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.soap.extender.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.remote.soap.extender.SoapDescriptorBuilder;
import com.liferay.portal.remote.soap.extender.internal.configuration.SoapExtenderConfiguration;

import jakarta.xml.ws.handler.Handler;

import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.ServiceDependency;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	configurationPid = "com.liferay.portal.remote.soap.extender.internal.configuration.SoapExtenderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, service = {}
)
public class SoapExtender {

	public SoapExtenderConfiguration getSoapExtenderConfiguration() {
		return _soapExtenderConfiguration;
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_soapExtenderConfiguration = ConfigurableUtil.createConfigurable(
			SoapExtenderConfiguration.class, properties);

		_dependencyManager = new DependencyManager(bundleContext);

		_enableComponent();
	}

	@Deactivate
	protected void deactivate() {
		_dependencyManager.clear();
	}

	private void _addBusDependencies(org.apache.felix.dm.Component component) {
		SoapExtenderConfiguration soapExtenderConfiguration =
			getSoapExtenderConfiguration();

		String[] contextPaths = soapExtenderConfiguration.contextPaths();

		if (contextPaths == null) {
			return;
		}

		for (String contextPath : contextPaths) {
			_addTCCLServiceDependency(
				component, true, Bus.class,
				StringBundler.concat(
					"(", HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH,
					"=", contextPath, ")"),
				"addBus", "removeBus");
		}
	}

	private void _addJaxWsHandlerServiceDependencies(
		org.apache.felix.dm.Component component) {

		SoapExtenderConfiguration soapExtenderConfiguration =
			getSoapExtenderConfiguration();

		String[] jaxWsHandlerFilterStrings =
			soapExtenderConfiguration.jaxWsHandlerFilterStrings();

		if (jaxWsHandlerFilterStrings == null) {
			return;
		}

		for (String jaxWsHandlerFilterString : jaxWsHandlerFilterStrings) {
			_addTCCLServiceDependency(
				component, false, Handler.class, jaxWsHandlerFilterString,
				"addHandler", "removeHandler");
		}
	}

	private void _addJaxWsServiceDependencies(
		org.apache.felix.dm.Component component) {

		SoapExtenderConfiguration soapExtenderConfiguration =
			getSoapExtenderConfiguration();

		String[] jaxWsServiceFilterStrings =
			soapExtenderConfiguration.jaxWsServiceFilterStrings();

		if (jaxWsServiceFilterStrings == null) {
			return;
		}

		for (String jaxWsServiceFilterString : jaxWsServiceFilterStrings) {
			_addTCCLServiceDependency(
				component, false, null, jaxWsServiceFilterString, "addService",
				"removeService");
		}
	}

	private void _addSoapDescriptorBuilderServiceDependency(
		org.apache.felix.dm.Component component) {

		ServiceDependency serviceDependency =
			_dependencyManager.createServiceDependency();

		serviceDependency.setCallbacks("setSoapDescriptorBuilder", null);
		serviceDependency.setRequired(false);
		serviceDependency.setService(
			SoapDescriptorBuilder.class,
			_soapExtenderConfiguration.soapDescriptorBuilderFilter());

		component.add(serviceDependency);
	}

	private ServiceDependency _addTCCLServiceDependency(
		org.apache.felix.dm.Component component, boolean required,
		Class<?> clazz, String filterString, String addName,
		String removeName) {

		ServiceDependency serviceDependency =
			_dependencyManager.createServiceDependency();

		serviceDependency.setCallbacks(addName, removeName);
		serviceDependency.setRequired(required);

		if (clazz == null) {
			serviceDependency.setService(filterString);
		}
		else {
			serviceDependency.setService(clazz, filterString);
		}

		component.add(serviceDependency);

		return serviceDependency;
	}

	private void _enableComponent() {
		org.apache.felix.dm.Component component =
			_dependencyManager.createComponent();

		CXFJaxWsServiceRegistrator cxfJaxWsServiceRegistrator =
			new CXFJaxWsServiceRegistrator();

		cxfJaxWsServiceRegistrator.setSoapDescriptorBuilder(
			_soapDescriptorBuilder);

		component.setImplementation(cxfJaxWsServiceRegistrator);

		_addBusDependencies(component);
		_addJaxWsHandlerServiceDependencies(component);
		_addJaxWsServiceDependencies(component);
		_addSoapDescriptorBuilderServiceDependency(component);

		_dependencyManager.add(component);
	}

	private DependencyManager _dependencyManager;

	@Reference
	private SoapDescriptorBuilder _soapDescriptorBuilder;

	private volatile SoapExtenderConfiguration _soapExtenderConfiguration;

}