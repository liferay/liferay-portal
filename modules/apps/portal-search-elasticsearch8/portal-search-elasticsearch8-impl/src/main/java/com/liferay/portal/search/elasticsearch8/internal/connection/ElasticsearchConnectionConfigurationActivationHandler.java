/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.connection;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.search.elasticsearch8.configuration.ElasticsearchConnectionConfiguration;
import com.liferay.portal.security.key.secret.SecretResolver;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(
	configurationPid = "com.liferay.portal.search.elasticsearch8.configuration.ElasticsearchConnectionConfiguration",
	service = {}
)
public class ElasticsearchConnectionConfigurationActivationHandler {

	@Activate
	protected void activate(Map<String, Object> properties) {
		ElasticsearchConnectionConfiguration
			elasticsearchConnectionConfiguration =
				ConfigurableUtil.createConfigurable(
					ElasticsearchConnectionConfiguration.class, properties);

		ElasticsearchConnection.Builder builder =
			new ElasticsearchConnection.Builder(
				elasticsearchConnectionConfiguration::networkHostAddresses);

		builder.active(
			elasticsearchConnectionConfiguration.active()
		).authenticationEnabled(
			elasticsearchConnectionConfiguration.authenticationEnabled()
		).compressionEnabled(
			elasticsearchConnectionConfiguration.compressionEnabled()
		).connectionId(
			elasticsearchConnectionConfiguration.connectionId()
		).httpSSLEnabled(
			elasticsearchConnectionConfiguration.httpSSLEnabled()
		).maxConnections(
			elasticsearchConnectionConfiguration.maxConnections()
		).maxConnectionsPerRoute(
			elasticsearchConnectionConfiguration.maxConnectionsPerRoute()
		).password(
			_secretResolver.resolve(
				CompanyConstants.SYSTEM,
				elasticsearchConnectionConfiguration.password())
		).proxyConfig(
			createProxyConfig(elasticsearchConnectionConfiguration)
		).truststorePassword(
			_secretResolver.resolve(
				CompanyConstants.SYSTEM,
				elasticsearchConnectionConfiguration.truststorePassword())
		).truststorePath(
			elasticsearchConnectionConfiguration.truststorePath()
		).truststoreType(
			elasticsearchConnectionConfiguration.truststoreType()
		).userName(
			elasticsearchConnectionConfiguration.username()
		);

		elasticsearchConnectionManager.addElasticsearchConnection(
			builder.build());
	}

	protected ProxyConfig createProxyConfig(
		ElasticsearchConnectionConfiguration
			elasticsearchConnectionConfiguration) {

		ProxyConfig.Builder proxyConfigBuilder = ProxyConfig.builder(http);

		return proxyConfigBuilder.networkAddresses(
			elasticsearchConnectionConfiguration.networkHostAddresses()
		).host(
			elasticsearchConnectionConfiguration.proxyHost()
		).password(
			_secretResolver.resolve(
				CompanyConstants.SYSTEM,
				elasticsearchConnectionConfiguration.proxyPassword())
		).port(
			elasticsearchConnectionConfiguration.proxyPort()
		).userName(
			elasticsearchConnectionConfiguration.proxyUserName()
		).build();
	}

	@Reference
	protected ElasticsearchConnectionManager elasticsearchConnectionManager;

	@Reference
	protected Http http;

	@Reference
	private SecretResolver _secretResolver;

}