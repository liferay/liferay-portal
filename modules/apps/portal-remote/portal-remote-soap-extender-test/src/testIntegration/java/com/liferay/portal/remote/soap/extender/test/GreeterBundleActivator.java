/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.soap.extender.test;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Carlos Sierra Andrés
 */
public class GreeterBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("jaxws", true);
		properties.put("soap.address", "/greeter");

		_serviceRegistration = bundleContext.registerService(
			Greeter.class, new GreeterImpl(), properties);

		_configAdminBundleActivator = new ConfigurationAdminBundleActivator();

		_configAdminBundleActivator.start(bundleContext);

		ServiceTracker<Bus, Bus> serviceTracker = ServiceTrackerFactory.open(
			bundleContext,
			StringBundler.concat(
				"(&(", HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH,
				"=/soap-test)(objectClass=", Bus.class.getName(), "))"));

		Bus bus = serviceTracker.waitForService(10_000);

		if (bus == null) {
			throw new IllegalStateException(
				"Bus was not registered within 10 seconds");
		}

		ServerRegistry serverRegistry = bus.getExtension(ServerRegistry.class);

		List<Server> servers = null;

		for (int i = 0; i <= 100; i++) {
			servers = serverRegistry.getServers();

			if (!servers.isEmpty()) {
				break;
			}

			Thread.sleep(100);
		}

		if (servers.isEmpty()) {
			cleanUp(bundleContext);

			throw new IllegalStateException(
				"Endpoint was not registered within 10 seconds");
		}
	}

	@Override
	public void stop(BundleContext bundleContext) {
		cleanUp(bundleContext);
	}

	protected void cleanUp(BundleContext bundleContext) {
		try {
			_configAdminBundleActivator.stop(bundleContext);
		}
		catch (Exception exception) {
		}

		_serviceRegistration.unregister();
	}

	private ConfigurationAdminBundleActivator _configAdminBundleActivator;
	private ServiceRegistration<Greeter> _serviceRegistration;

}