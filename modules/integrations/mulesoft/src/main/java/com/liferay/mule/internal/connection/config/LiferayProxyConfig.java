/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.connection.config;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.http.api.client.proxy.ProxyConfig;
import org.mule.runtime.http.api.client.proxy.ProxyConfigBuilder;

/**
 * @author Matija Petanjek
 */
public class LiferayProxyConfig {

	public ProxyConfig getProxyConfig() {
		if ((proxyHost != null) && (proxyPort != null)) {
			ProxyConfigBuilder proxyConfigBuilder = ProxyConfig.builder();

			proxyConfigBuilder.host(proxyHost);
			proxyConfigBuilder.password(proxyPassword);
			proxyConfigBuilder.port(proxyPort);
			proxyConfigBuilder.username(proxyUsername);

			return proxyConfigBuilder.build();
		}

		return null;
	}

	private static final String PROXY_CONFIG = "Proxy Config";

	@DisplayName("Host")
	@Optional
	@Parameter
	@Placement(order = 1, tab = PROXY_CONFIG)
	private String proxyHost;

	@DisplayName("Password")
	@Optional
	@Parameter
	@Password
	@Placement(order = 4, tab = PROXY_CONFIG)
	private String proxyPassword;

	@DisplayName("Port")
	@Optional
	@Parameter
	@Placement(order = 2, tab = PROXY_CONFIG)
	private Integer proxyPort;

	@DisplayName("Username")
	@Optional
	@Parameter
	@Placement(order = 3, tab = PROXY_CONFIG)
	private String proxyUsername;

}