/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.websocket.whiteboard.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.ServletContext;

import jakarta.websocket.Decoder;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Encoder;
import jakarta.websocket.Endpoint;
import jakarta.websocket.server.ServerContainer;
import jakarta.websocket.server.ServerEndpointConfig;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Cristina González
 * @author Manuel de la Peña
 */
@Component(service = {})
public class WebSocketEndpointTracker {

	@Activate
	protected void activate(final BundleContext bundleContext) {
		Object serverContainer = _servletContext.getAttribute(
			"jakarta.websocket.server.ServerContainer");

		if (serverContainer == null) {
			if (_log.isInfoEnabled()) {
				_log.info("A WebSocket server container is not registered");
			}

			return;
		}

		_serverEndpointConfigWrapperServiceTracker = new ServiceTracker<>(
			bundleContext, Endpoint.class,
			new ServiceTrackerCustomizer
				<Endpoint, ServerEndpointConfigWrapper>() {

				@Override
				public ServerEndpointConfigWrapper addingService(
					ServiceReference<Endpoint> serviceReference) {

					String path = (String)serviceReference.getProperty(
						"org.osgi.http.websocket.endpoint.path");

					if ((path == null) || path.isEmpty()) {
						return null;
					}

					List<Class<? extends Decoder>> decoders =
						(List<Class<? extends Decoder>>)
							serviceReference.getProperty(
								"org.osgi.http.websocket.endpoint.decoders");
					List<Class<? extends Encoder>> encoders =
						(List<Class<? extends Encoder>>)
							serviceReference.getProperty(
								"org.osgi.http.websocket.endpoint.encoders");
					List<String> subprotocol =
						(List<String>)serviceReference.getProperty(
							"org.osgi.http.websocket.endpoint.subprotocol");

					ServiceObjects<Endpoint> serviceObjects =
						bundleContext.getServiceObjects(serviceReference);

					ServerEndpointConfigWrapper serverEndpointConfigWrapper =
						_serverEndpointConfigWrappers.get(path);

					boolean isNew = false;

					if (serverEndpointConfigWrapper == null) {
						serverEndpointConfigWrapper =
							new ServerEndpointConfigWrapper(
								path, decoders, encoders, subprotocol,
								_logService);

						isNew = true;
					}
					else {
						Class<?> endpointClass =
							serverEndpointConfigWrapper.getEndpointClass();

						ServerEndpointConfig.Configurator configurator =
							serverEndpointConfigWrapper.getConfigurator();

						try {
							Object endpointInstance =
								configurator.getEndpointInstance(endpointClass);

							Class<?> endpointInstanceClass =
								endpointInstance.getClass();

							if (endpointInstanceClass.equals(
									ServerEndpointConfigWrapper.NullEndpoint.
										class)) {

								serverEndpointConfigWrapper.override(
									decoders, encoders, subprotocol);
							}
						}
						catch (InstantiationException instantiationException) {
							Endpoint endpoint = serviceObjects.getService();

							_logService.log(
								LogService.LOG_ERROR,
								StringBundler.concat(
									"Unable to register WebSocket endpoint ",
									endpoint.getClass(), " for path ", path),
								instantiationException);
						}
					}

					serverEndpointConfigWrapper.setConfigurator(
						serviceReference,
						new ServiceObjectsConfigurator(
							serviceObjects, _logService));

					if (isNew) {
						ServerContainer serverContainer =
							(ServerContainer)_servletContext.getAttribute(
								ServerContainer.class.getName());

						try {
							serverContainer.addEndpoint(
								serverEndpointConfigWrapper);
						}
						catch (DeploymentException deploymentException) {
							Endpoint endpoint = serviceObjects.getService();

							_logService.log(
								LogService.LOG_ERROR,
								StringBundler.concat(
									"Unable to register WebSocket endpoint ",
									endpoint.getClass(), " for path ", path),
								deploymentException);

							return null;
						}

						_serverEndpointConfigWrappers.put(
							path, serverEndpointConfigWrapper);
					}

					return serverEndpointConfigWrapper;
				}

				@Override
				public void modifiedService(
					ServiceReference<Endpoint> serviceReference,
					ServerEndpointConfigWrapper serverEndpointConfigWrapper) {

					removedService(
						serviceReference, serverEndpointConfigWrapper);

					addingService(serviceReference);
				}

				@Override
				public void removedService(
					ServiceReference<Endpoint> serviceReference,
					ServerEndpointConfigWrapper serverEndpointConfigWrapper) {

					ServiceObjectsConfigurator serviceObjectsConfigurator =
						serverEndpointConfigWrapper.removeConfigurator(
							serviceReference);

					serviceObjectsConfigurator.close();
				}

			});

		_serverEndpointConfigWrapperServiceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		if (_serverEndpointConfigWrapperServiceTracker != null) {
			_serverEndpointConfigWrapperServiceTracker.close();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WebSocketEndpointTracker.class);

	@Reference
	private LogService _logService;

	private final ConcurrentMap<String, ServerEndpointConfigWrapper>
		_serverEndpointConfigWrappers = new ConcurrentHashMap<>();
	private ServiceTracker<Endpoint, ServerEndpointConfigWrapper>
		_serverEndpointConfigWrapperServiceTracker;

	@Reference(target = "(original.bean=true)")
	private ServletContext _servletContext;

}