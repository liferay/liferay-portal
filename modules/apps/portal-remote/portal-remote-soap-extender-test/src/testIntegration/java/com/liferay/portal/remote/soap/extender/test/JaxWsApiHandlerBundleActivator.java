/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.soap.extender.test;

import com.liferay.petra.string.StringBundler;

import jakarta.xml.ws.Binding;
import jakarta.xml.ws.Endpoint;
import jakarta.xml.ws.handler.Handler;
import jakarta.xml.ws.spi.Provider;

import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Carlos Sierra Andrés
 */
public class JaxWsApiHandlerBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		_configurationAdminBundleActivator =
			new ConfigurationAdminBundleActivator();

		_configurationAdminBundleActivator.start(bundleContext);

		String filterString = StringBundler.concat(
			"(&(objectClass=", Provider.class.getName(), ")(",
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH,
			"=/soap-test))");

		WaiterUtil.waitForFilter(bundleContext, filterString, 10_000);

		try {
			_endpoint = Endpoint.publish("/greeterApi", new GreeterImpl());

			Binding binding = _endpoint.getBinding();

			@SuppressWarnings("rawtypes")
			List<Handler> handlers = binding.getHandlerChain();

			Handler<?> handler = new SampleHandler();

			handlers.add(handler);

			binding.setHandlerChain(handlers);
		}
		catch (Exception exception) {
			cleanUp(bundleContext);
		}
	}

	@Override
	public void stop(BundleContext bundleContext) {
		cleanUp(bundleContext);
	}

	protected void cleanUp(BundleContext bundleContext) {
		try {
			_configurationAdminBundleActivator.stop(bundleContext);
		}
		catch (Exception exception) {
		}

		if (_endpoint != null) {
			_endpoint.stop();
		}
	}

	private ConfigurationAdminBundleActivator
		_configurationAdminBundleActivator;
	private Endpoint _endpoint;

}