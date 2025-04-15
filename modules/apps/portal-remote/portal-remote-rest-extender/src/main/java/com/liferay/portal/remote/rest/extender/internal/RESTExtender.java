/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.rest.extender.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.remote.rest.extender.configuration.RestExtenderConfiguration;

import jakarta.ws.rs.core.Application;

import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.ServiceDependency;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	configurationPid = "com.liferay.portal.remote.rest.extender.configuration.RestExtenderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, service = {}
)
public class RESTExtender {

	public RestExtenderConfiguration getRestExtenderConfiguration() {
		return _restExtenderConfiguration;
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_restExtenderConfiguration = ConfigurableUtil.createConfigurable(
			RestExtenderConfiguration.class, properties);

		_dependencyManager = new DependencyManager(bundleContext);

		_component = _dependencyManager.createComponent();

		CXFJaxRsServiceRegistrator cxfJaxRsServiceRegistrator =
			new CXFJaxRsServiceRegistrator(properties);

		_component.setImplementation(cxfJaxRsServiceRegistrator);

		_addBusDependencies();
		_addJaxRsApplicationDependencies();
		_addJaxRsProviderServiceDependencies();
		_addJaxRsServiceDependencies();

		_dependencyManager.add(_component);
	}

	@Deactivate
	protected void deactivate() {
		_dependencyManager.clear();
	}

	private void _addBusDependencies() {
		RestExtenderConfiguration restExtenderConfiguration =
			getRestExtenderConfiguration();

		String[] contextPaths = restExtenderConfiguration.contextPaths();

		if (contextPaths == null) {
			return;
		}

		for (String contextPath : contextPaths) {
			if (Validator.isNull(contextPath)) {
				continue;
			}

			_addTCCLServiceDependency(
				true, Bus.class,
				StringBundler.concat(
					"(", HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH,
					"=", contextPath, ")"),
				"addBus", "removeBus");
		}
	}

	private void _addJaxRsApplicationDependencies() {
		RestExtenderConfiguration restExtenderConfiguration =
			getRestExtenderConfiguration();

		String[] jaxRsApplicationFilterStrings =
			restExtenderConfiguration.jaxRsApplicationFilterStrings();

		if (jaxRsApplicationFilterStrings == null) {
			_addTCCLServiceDependency(
				false, Application.class, null, "addApplication",
				"removeApplication");

			return;
		}

		for (String jaxRsApplicationFilterString :
				jaxRsApplicationFilterStrings) {

			_addTCCLServiceDependency(
				false, Application.class, jaxRsApplicationFilterString,
				"addApplication", "removeApplication");
		}
	}

	private void _addJaxRsProviderServiceDependencies() {
		RestExtenderConfiguration restExtenderConfiguration =
			getRestExtenderConfiguration();

		String[] jaxRsProviderFilterStrings =
			restExtenderConfiguration.jaxRsProviderFilterStrings();

		if (jaxRsProviderFilterStrings == null) {
			return;
		}

		for (String jaxRsProviderFilterString : jaxRsProviderFilterStrings) {
			if (Validator.isNull(jaxRsProviderFilterString)) {
				continue;
			}

			_addTCCLServiceDependency(
				false, null, jaxRsProviderFilterString, "addProvider",
				"removeProvider");
		}
	}

	private void _addJaxRsServiceDependencies() {
		RestExtenderConfiguration restExtenderConfiguration =
			getRestExtenderConfiguration();

		String[] jaxRsServiceFilterStrings =
			restExtenderConfiguration.jaxRsServiceFilterStrings();

		if (jaxRsServiceFilterStrings == null) {
			return;
		}

		for (String jaxRsServiceFilterString : jaxRsServiceFilterStrings) {
			if (Validator.isNull(jaxRsServiceFilterString)) {
				continue;
			}

			_addTCCLServiceDependency(
				false, null, jaxRsServiceFilterString, "addService",
				"removeService");
		}
	}

	private ServiceDependency _addTCCLServiceDependency(
		boolean required, Class<?> clazz, String filter, String addName,
		String removeName) {

		ServiceDependency serviceDependency =
			_dependencyManager.createServiceDependency();

		serviceDependency.setCallbacks(addName, removeName);
		serviceDependency.setRequired(required);

		if (clazz == null) {
			serviceDependency.setService(filter);
		}
		else {
			if (filter == null) {
				serviceDependency.setService(clazz);
			}
			else {
				serviceDependency.setService(clazz, filter);
			}
		}

		_component.add(serviceDependency);

		return serviceDependency;
	}

	private org.apache.felix.dm.Component _component;
	private DependencyManager _dependencyManager;
	private RestExtenderConfiguration _restExtenderConfiguration;

}