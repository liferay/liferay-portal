/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.connection;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.search.opensearch2.configuration.OpenSearchConnectionConfiguration;
import com.liferay.portal.security.key.secret.SecretResolver;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "com.liferay.portal.search.opensearch2.configuration.OpenSearchConnectionConfiguration",
	service = OpenSearchConnectionsHolder.class
)
public class OpenSearchConnectionsHolderImpl
	implements OpenSearchConnectionsHolder {

	@Override
	public void addOpenSearchConnection(
		OpenSearchConnection openSearchConnection) {

		String connectionId = openSearchConnection.getConnectionId();

		if (connectionId == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Skipping connection because connection ID is null");
			}

			return;
		}

		if (openSearchConnection.isActive()) {
			try {
				openSearchConnection.connect();
			}
			catch (RuntimeException runtimeException) {
				_log.error(
					"OpenSearch connection could not be established. Search " +
						"will be unavailable.",
					runtimeException);

				throw runtimeException;
			}
		}

		_openSearchConnections.put(connectionId, openSearchConnection);
	}

	@Override
	public OpenSearchConnection getOpenSearchConnection(String connectionId) {
		return _openSearchConnections.get(connectionId);
	}

	@Override
	public Collection<OpenSearchConnection> getOpenSearchConnections() {
		return _openSearchConnections.values();
	}

	@Override
	public void removeOpenSearchConnection(String connectionId) {
		if (connectionId == null) {
			return;
		}

		OpenSearchConnection openSearchConnection = _openSearchConnections.get(
			connectionId);

		if (openSearchConnection == null) {
			return;
		}

		openSearchConnection.close();

		_openSearchConnections.remove(connectionId);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		OpenSearchConnectionConfiguration openSearchConnectionConfiguration =
			ConfigurableUtil.createConfigurable(
				OpenSearchConnectionConfiguration.class, properties);

		OpenSearchConnection.Builder openSearchConnectionBuilder =
			new OpenSearchConnection.Builder();

		addOpenSearchConnection(
			openSearchConnectionBuilder.active(
				openSearchConnectionConfiguration.active()
			).authenticationEnabled(
				openSearchConnectionConfiguration.authenticationEnabled()
			).compressionEnabled(
				openSearchConnectionConfiguration.compressionEnabled()
			).connectionId(
				openSearchConnectionConfiguration.connectionId()
			).httpSSLEnabled(
				openSearchConnectionConfiguration.httpSSLEnabled()
			).maxConnections(
				openSearchConnectionConfiguration.maxConnections()
			).maxConnectionsPerRoute(
				openSearchConnectionConfiguration.maxConnectionsPerRoute()
			).networkHostAddresses(
				openSearchConnectionConfiguration.networkHostAddresses()
			).password(
				_secretResolver.resolve(
					CompanyConstants.SYSTEM,
					openSearchConnectionConfiguration.password())
			).proxyConfig(
				createProxyConfig(openSearchConnectionConfiguration)
			).truststorePassword(
				_secretResolver.resolve(
					CompanyConstants.SYSTEM,
					openSearchConnectionConfiguration.truststorePassword())
			).truststorePath(
				openSearchConnectionConfiguration.truststorePath()
			).truststoreType(
				openSearchConnectionConfiguration.truststoreType()
			).userName(
				openSearchConnectionConfiguration.username()
			).build());
	}

	protected ProxyConfig createProxyConfig(
		OpenSearchConnectionConfiguration openSearchConnectionConfiguration) {

		ProxyConfig.Builder proxyConfigBuilder = ProxyConfig.builder(http);

		return proxyConfigBuilder.networkAddresses(
			openSearchConnectionConfiguration.networkHostAddresses()
		).host(
			openSearchConnectionConfiguration.proxyHost()
		).password(
			_secretResolver.resolve(
				CompanyConstants.SYSTEM,
				openSearchConnectionConfiguration.proxyPassword())
		).port(
			openSearchConnectionConfiguration.proxyPort()
		).userName(
			openSearchConnectionConfiguration.proxyHost()
		).build();
	}

	@Deactivate
	protected void deactivate() {
		Collection<OpenSearchConnection> openSearchConnections =
			_openSearchConnections.values();

		for (OpenSearchConnection openSearchConnection :
				openSearchConnections) {

			openSearchConnection.close();
		}
	}

	@Reference
	protected Http http;

	private static final Log _log = LogFactoryUtil.getLog(
		OpenSearchConnectionsHolderImpl.class);

	private final Map<String, OpenSearchConnection> _openSearchConnections =
		new ConcurrentHashMap<>();

	@Reference
	private SecretResolver _secretResolver;

}