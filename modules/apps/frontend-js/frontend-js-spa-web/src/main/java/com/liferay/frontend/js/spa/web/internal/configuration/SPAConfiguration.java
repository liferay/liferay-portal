/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.spa.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Bruno Basto
 */
@ExtendedObjectClassDefinition(
	category = "infrastructure",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	description = "spa-configuration-description",
	id = "com.liferay.frontend.js.spa.web.internal.configuration.SPAConfiguration",
	localization = "content/Language", name = "spa-configuration-name"
)
public interface SPAConfiguration {

	@Meta.AD(
		deflt = "-1", description = "cache-expiration-time-description",
		name = "cache-expiration-time-name", required = false
	)
	public long cacheExpirationTime();

	@Meta.AD(
		description = "custom-excluded-paths-description",
		name = "custom-excluded-paths-name", required = false
	)
	public String[] customExcludedPaths();

	@Meta.AD(
		deflt = "true", description = "enable-spa-description",
		name = "enable-spa-name", required = false
	)
	public boolean enabled();

	@Meta.AD(
		deflt = ":not([target=\"_blank\"])|:not([data-senna-off])|:not([data-resource-href])",
		description = "navigation-exception-selectors-description",
		name = "navigation-exception-selectors-name", required = false
	)
	public String[] navigationExceptionSelectors();

	@Meta.AD(
		deflt = "false", description = "preload-css-description",
		name = "preload-css-name", required = false
	)
	public boolean preloadCSS();

	@Meta.AD(
		deflt = "0", description = "request-timeout-description",
		name = "request-timeout-name", required = false
	)
	public int requestTimeout();

	@Meta.AD(
		deflt = "30000", description = "user-notification-timeout-description",
		name = "user-notification-timeout-name", required = false
	)
	public int userNotificationTimeout();

}