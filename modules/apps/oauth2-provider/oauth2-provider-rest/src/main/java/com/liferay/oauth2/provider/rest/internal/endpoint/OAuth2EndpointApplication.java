/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint;

import jakarta.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	property = {
		"liferay.auth.verifier=false", "liferay.cors.annotation=true",
		"liferay.jackson=false", "liferay.oauth2=false",
		"oauth2.scope.checker.type=none", "osgi.jaxrs.application.base=/oauth2",
		"osgi.jaxrs.name=Liferay.OAuth2.Application"
	},
	service = Application.class
)
public class OAuth2EndpointApplication extends Application {
}