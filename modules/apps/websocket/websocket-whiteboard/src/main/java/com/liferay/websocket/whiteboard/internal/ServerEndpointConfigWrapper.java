/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.websocket.whiteboard.internal;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Decoder;
import jakarta.websocket.Encoder;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Extension;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpointConfig;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * @author Cristina González Castellano
 */
public class ServerEndpointConfigWrapper implements ServerEndpointConfig {

	public ServerEndpointConfigWrapper(
		String path, List<Class<? extends Decoder>> decoders,
		List<Class<? extends Encoder>> encoders, List<String> subprotocols,
		LogService logService) {

		_logService = logService;

		_init(path, decoders, encoders, subprotocols);
	}

	@Override
	public Configurator getConfigurator() {
		Map.Entry<ServiceReference<Endpoint>, ServiceObjectsConfigurator>
			entry = _endpoints.firstEntry();

		if (entry == null) {
			return _configurator;
		}

		return entry.getValue();
	}

	@Override
	public List<Class<? extends Decoder>> getDecoders() {
		return _serverEndpointConfig.getDecoders();
	}

	@Override
	public List<Class<? extends Encoder>> getEncoders() {
		return _serverEndpointConfig.getEncoders();
	}

	@Override
	public Class<?> getEndpointClass() {
		return _serverEndpointConfig.getEndpointClass();
	}

	@Override
	public List<Extension> getExtensions() {
		return _serverEndpointConfig.getExtensions();
	}

	@Override
	public String getPath() {
		return _serverEndpointConfig.getPath();
	}

	@Override
	public List<String> getSubprotocols() {
		return _serverEndpointConfig.getSubprotocols();
	}

	@Override
	public Map<String, Object> getUserProperties() {
		return _serverEndpointConfig.getUserProperties();
	}

	public void override(
		List<Class<? extends Decoder>> decoders,
		List<Class<? extends Encoder>> encoders, List<String> subprotocols) {

		_init(
			_serverEndpointConfig.getPath(), decoders, encoders, subprotocols);
	}

	public ServiceObjectsConfigurator removeConfigurator(
		ServiceReference<Endpoint> serviceReference) {

		return _endpoints.remove(serviceReference);
	}

	public void setConfigurator(
		ServiceReference<Endpoint> serviceReference,
		ServiceObjectsConfigurator serviceObjectsConfigurator) {

		_endpoints.put(serviceReference, serviceObjectsConfigurator);
	}

	public final class NullEndpoint extends Endpoint {

		@Override
		public void onOpen(Session session, EndpointConfig config) {
			try {
				session.close(
					new CloseReason(
						CloseReason.CloseCodes.GOING_AWAY,
						"Service is gone away"));
			}
			catch (IOException ioException) {
				_logService.log(
					LogService.LOG_ERROR, "Unable to close session",
					ioException);
			}
		}

	}

	private void _init(
		String path, List<Class<? extends Decoder>> decoders,
		List<Class<? extends Encoder>> encoders, List<String> subprotocols) {

		ServerEndpointConfig.Builder builder =
			ServerEndpointConfig.Builder.create(Endpoint.class, path);

		builder.decoders(decoders);
		builder.encoders(encoders);
		builder.subprotocols(subprotocols);

		_serverEndpointConfig = builder.build();

		_endpoints = new ConcurrentSkipListMap<>();
	}

	private final Configurator _configurator =
		new ServerEndpointConfig.Configurator() {

			@Override
			public <T> T getEndpointInstance(Class<T> endpointClass) {
				return (T)new NullEndpoint();
			}

		};

	private ConcurrentNavigableMap
		<ServiceReference<Endpoint>, ServiceObjectsConfigurator> _endpoints;
	private final LogService _logService;
	private ServerEndpointConfig _serverEndpointConfig;

}