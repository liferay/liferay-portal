/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.connection;

import com.liferay.mule.internal.connection.config.LiferayProxyConfig;

import java.io.IOException;

import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.http.api.HttpService;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import org.slf4j.Logger;

/**
 * @author Matija Petanjek
 */
public abstract class BaseCachedConnectionProvider
	implements CachedConnectionProvider<LiferayConnection> {

	@Override
	public void disconnect(LiferayConnection liferayConnection) {
		getLogger().debug("Closing connection to Liferay Portal instance");

		liferayConnection.invalidate();
	}

	@Override
	public ConnectionValidationResult validate(
		LiferayConnection liferayConnection) {

		try {
			HttpResponse httpResponse =
				liferayConnection.getOpenAPISpecHttpResponse();

			int statusCode = httpResponse.getStatusCode();

			if ((statusCode >= 200) && (statusCode < 300)) {
				return ConnectionValidationResult.success();
			}

			return ConnectionValidationResult.failure(
				String.format(
					"%s (%d)", httpResponse.getReasonPhrase(),
					httpResponse.getStatusCode()),
				new ConnectionException(
					"Unable to connect to Liferay instance"));
		}
		catch (IOException | TimeoutException exception) {
			return ConnectionValidationResult.failure(
				exception.getMessage(), exception);
		}
	}

	protected abstract Logger getLogger();

	@Inject
	protected HttpService httpService;

	@ParameterGroup(name = "Proxy config")
	protected LiferayProxyConfig liferayProxyConfig;

}