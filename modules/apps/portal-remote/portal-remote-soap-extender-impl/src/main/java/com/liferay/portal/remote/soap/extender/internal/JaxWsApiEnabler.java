/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.soap.extender.internal;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.remote.soap.extender.internal.configuration.JaxWsApiConfiguration;

import jakarta.xml.ws.spi.Provider;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxws22.spi.ProviderImpl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	configurationPid = "com.liferay.portal.remote.soap.extender.internal.configuration.JaxWsApiConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, service = {}
)
public class JaxWsApiEnabler {

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		JaxWsApiConfiguration jaxWsApiConfiguration =
			ConfigurableUtil.createConfigurable(
				JaxWsApiConfiguration.class, properties);

		_contextPath = jaxWsApiConfiguration.contextPath();

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext,
			StringBundler.concat(
				"(&(objectClass=org.apache.cxf.Bus)(",
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "=",
				_contextPath, "))"),
			new CXFBusServiceTrackerCustomizer());
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private BundleContext _bundleContext;
	private Bus _bus;
	private String _contextPath;
	private ServiceRegistration<Provider> _serviceRegistration;
	private ServiceTracker<Bus, Bus> _serviceTracker;

	private class CXFBusServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<Bus, Bus> {

		@Override
		public Bus addingService(ServiceReference<Bus> serviceReference) {
			if (_bus != null) {
				return null;
			}

			_bus = _bundleContext.getService(serviceReference);

			BusFactory.setDefaultBus(_bus);

			ProviderImpl providerImpl = new ProviderImpl();

			Dictionary<String, Object> providerProperties = new Hashtable<>();

			providerProperties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH,
				_contextPath);

			_serviceRegistration = _bundleContext.registerService(
				Provider.class, providerImpl, providerProperties);

			return _bus;
		}

		@Override
		public void modifiedService(
			ServiceReference<Bus> serviceReference, Bus bus) {
		}

		@Override
		public void removedService(
			ServiceReference<Bus> serviceReference, Bus bus) {

			_serviceRegistration.unregister();

			BusFactory.setDefaultBus(null);

			BusFactory.clearDefaultBusForAnyThread(_bus);

			_bus = null;
		}

	}

}