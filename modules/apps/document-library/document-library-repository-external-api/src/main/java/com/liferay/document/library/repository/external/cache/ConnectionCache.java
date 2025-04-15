/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.repository.external.cache;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.repository.RepositoryException;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.util.TransientValue;

import jakarta.servlet.http.HttpSession;

/**
 * @author Iván Zaera
 */
public class ConnectionCache<T> {

	public ConnectionCache(
		Class<T> connectionClass, long repositoryId,
		ConnectionBuilder<T> connectionBuilder) {

		_connectionBuilder = connectionBuilder;

		_sessionKey =
			ConnectionCache.class.getName() + StringPool.POUND + repositoryId;

		_connectionThreadLocal = new CentralizedThreadLocal<>(
			connectionClass.getName());
	}

	public T getConnection() throws RepositoryException {
		T connection = null;

		HttpSession httpSession = PortalSessionThreadLocal.getHttpSession();

		if (httpSession != null) {
			TransientValue<T> transientValue =
				(TransientValue<T>)httpSession.getAttribute(_sessionKey);

			if (transientValue != null) {
				connection = transientValue.getValue();
			}
		}
		else {
			connection = _connectionThreadLocal.get();
		}

		if (connection != null) {
			return connection;
		}

		connection = _connectionBuilder.buildConnection();

		if (httpSession != null) {
			TransientValue<T> transientValue = new TransientValue<>(connection);

			httpSession.setAttribute(_sessionKey, transientValue);
		}

		_connectionThreadLocal.set(connection);

		return connection;
	}

	private final ConnectionBuilder<T> _connectionBuilder;
	private final ThreadLocal<T> _connectionThreadLocal;
	private final String _sessionKey;

}