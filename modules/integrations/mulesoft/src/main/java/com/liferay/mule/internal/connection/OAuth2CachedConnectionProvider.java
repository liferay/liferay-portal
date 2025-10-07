/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.connection;

import com.liferay.mule.internal.connection.config.OAuth2AuthenticationConfig;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Matija Petanjek
 */
@Alias("oauth2-connection")
@DisplayName("2 - OAuth 2.0")
public class OAuth2CachedConnectionProvider
	extends BaseCachedConnectionProvider {

	@Override
	public LiferayConnection connect() throws ConnectionException {
		logger.debug(
			"Initializing connection to Liferay Portal instance using OAuth " +
				"2.0 authorization");

		return LiferayConnection.withOAuth2Authentication(
			httpService, oAuth2AuthenticationConfig.getOpenApiSpecPath(),
			oAuth2AuthenticationConfig.getConsumerKey(),
			oAuth2AuthenticationConfig.getConsumerSecret(),
			liferayProxyConfig.getProxyConfig());
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	private static final Logger logger = LoggerFactory.getLogger(
		OAuth2CachedConnectionProvider.class);

	@ParameterGroup(name = ParameterGroup.CONNECTION)
	private OAuth2AuthenticationConfig oAuth2AuthenticationConfig;

}