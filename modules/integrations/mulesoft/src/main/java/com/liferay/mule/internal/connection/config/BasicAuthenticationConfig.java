/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.connection.config;

import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

/**
 * @author Matija Petanjek
 */
public class BasicAuthenticationConfig {

	public String getOpenApiSpecPath() {
		return openApiSpecPath;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	@DisplayName("OpenAPI Spec URL")
	@Parameter
	@Placement(order = 1)
	private String openApiSpecPath;

	@DisplayName("Password")
	@Parameter
	@Password
	@Placement(order = 3)
	private String password;

	@DisplayName("Username")
	@Parameter
	@Placement(order = 2)
	private String username;

}