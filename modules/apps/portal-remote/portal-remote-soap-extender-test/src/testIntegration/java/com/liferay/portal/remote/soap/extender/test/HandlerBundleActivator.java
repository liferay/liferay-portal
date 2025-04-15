/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.soap.extender.test;

import jakarta.xml.ws.handler.Handler;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Carlos Sierra Andrés
 */
public class HandlerBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("soap.address", "/greeter");

		_serviceRegistration = bundleContext.registerService(
			Handler.class, new SampleHandler(), properties);

		_greeterBundleActivator = new GreeterBundleActivator();

		try {
			_greeterBundleActivator.start(bundleContext);
		}
		catch (Exception exception) {
			cleanUp(bundleContext);

			throw exception;
		}
	}

	@Override
	public void stop(BundleContext bundleContext) {
		cleanUp(bundleContext);
	}

	protected void cleanUp(BundleContext bundleContext) {
		try {
			_greeterBundleActivator.stop(bundleContext);
		}
		catch (Exception exception) {
		}

		_serviceRegistration.unregister();
	}

	private GreeterBundleActivator _greeterBundleActivator;

	@SuppressWarnings("rawtypes")
	private ServiceRegistration<Handler> _serviceRegistration;

}