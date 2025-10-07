/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.connection;

import com.liferay.mule.internal.connection.config.BasicAuthenticationConfig;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Matija Petanjek
 */
@Alias("basic-connection")
@DisplayName("1 - Basic")
public class BasicCachedConnectionProvider
	extends BaseCachedConnectionProvider {

	@Override
	public LiferayConnection connect() throws ConnectionException {
		logger.debug(
			"Initializing connection to Liferay Portal instance using basic " +
				"authentication");

		return LiferayConnection.withBasicAuthentication(
			httpService, basicAuthenticationConfig.getOpenApiSpecPath(),
			basicAuthenticationConfig.getUsername(),
			basicAuthenticationConfig.getPassword(),
			liferayProxyConfig.getProxyConfig());
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	private static final Logger logger = LoggerFactory.getLogger(
		BasicCachedConnectionProvider.class);

	@ParameterGroup(name = ParameterGroup.CONNECTION)
	private BasicAuthenticationConfig basicAuthenticationConfig;

}