/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.connection.config;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

/**
 * @author Matija Petanjek
 */
public class OAuth2AuthenticationConfig {

	public String getConsumerKey() {
		return consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public String getOpenApiSpecPath() {
		return openApiSpecPath;
	}

	@DisplayName("Consumer Key")
	@Expression(ExpressionSupport.NOT_SUPPORTED)
	@Parameter
	@Placement(order = 2)
	private String consumerKey;

	@DisplayName("Consumer Secret")
	@Expression(ExpressionSupport.NOT_SUPPORTED)
	@Parameter
	@Placement(order = 3)
	private String consumerSecret;

	@DisplayName("OpenAPI Spec URL")
	@Parameter
	@Placement(order = 1)
	private String openApiSpecPath;

}