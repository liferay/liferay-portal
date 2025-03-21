/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.websocket.whiteboard.internal;

import jakarta.websocket.Endpoint;
import jakarta.websocket.server.ServerEndpointConfig;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.osgi.framework.ServiceObjects;
import org.osgi.service.log.LogService;

/**
 * @author Cristina González
 */
public class ServiceObjectsConfigurator
	extends ServerEndpointConfig.Configurator {

	public ServiceObjectsConfigurator(
		ServiceObjects<Endpoint> serviceObjects, LogService logService) {

		_serviceObjects = serviceObjects;
		_logService = logService;
	}

	public void close() {
		Iterator<EndpointWrapper> iterator = _endpointWrappers.iterator();

		while (iterator.hasNext()) {
			EndpointWrapper endpointWrapper = iterator.next();

			iterator.remove();

			endpointWrapper.close();
		}
	}

	@Override
	public <T> T getEndpointInstance(Class<T> endpointClass) {
		return (T)_wrapped();
	}

	private EndpointWrapper _wrapped() {
		EndpointWrapper endpointWrapper = new EndpointWrapper(
			_serviceObjects, _logService);

		_endpointWrappers.add(endpointWrapper);

		return endpointWrapper;
	}

	private final Set<EndpointWrapper> _endpointWrappers = new HashSet<>();
	private final LogService _logService;
	private final ServiceObjects<Endpoint> _serviceObjects;

}