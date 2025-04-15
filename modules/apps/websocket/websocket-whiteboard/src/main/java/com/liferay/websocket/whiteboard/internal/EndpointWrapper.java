/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.websocket.whiteboard.internal;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;

import java.io.IOException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.osgi.framework.ServiceObjects;
import org.osgi.service.log.LogService;

/**
 * @author Cristina González Castellano
 */
public class EndpointWrapper extends Endpoint {

	public EndpointWrapper(
		ServiceObjects<Endpoint> serviceObjects, LogService logService) {

		_serviceObjects = serviceObjects;
		_logService = logService;

		_endpoint = serviceObjects.getService();
	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {
		if (_closed) {
			return;
		}

		_endpoint.onClose(session, closeReason);

		_sessions.remove(session);

		_serviceObjects.ungetService(_endpoint);
	}

	@Override
	public void onError(Session session, Throwable throwable) {
		if (_closed) {
			return;
		}

		_endpoint.onError(session, throwable);
	}

	@Override
	public void onOpen(Session session, EndpointConfig endpointConfig) {
		if (_closed) {
			return;
		}

		_endpoint.onOpen(session, endpointConfig);

		_sessions.add(session);
	}

	protected void close() {
		_closed = true;

		Iterator<Session> iterator = _sessions.iterator();

		while (iterator.hasNext()) {
			Session session = iterator.next();

			iterator.remove();

			try {
				CloseReason closeReason = new CloseReason(
					CloseReason.CloseCodes.GOING_AWAY, "Service is going away");

				session.close(closeReason);

				_endpoint.onClose(session, closeReason);

				_serviceObjects.ungetService(_endpoint);
			}
			catch (IOException ioException) {
				_logService.log(
					LogService.LOG_ERROR, "Unable to close session",
					ioException);
			}
		}
	}

	private volatile boolean _closed;
	private final Endpoint _endpoint;
	private final LogService _logService;
	private final ServiceObjects<Endpoint> _serviceObjects;
	private final Set<Session> _sessions = new HashSet<>();

}