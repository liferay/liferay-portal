/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.config;

import com.liferay.mule.internal.connection.BasicCachedConnectionProvider;
import com.liferay.mule.internal.connection.OAuth2CachedConnectionProvider;
import com.liferay.mule.internal.operation.LiferayBatchOperations;
import com.liferay.mule.internal.operation.LiferayCRUDOperations;

import java.util.concurrent.TimeUnit;

import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

/**
 * @author Matija Petanjek
 */
@Configuration
@ConnectionProviders(
	{BasicCachedConnectionProvider.class, OAuth2CachedConnectionProvider.class}
)
@Operations({LiferayBatchOperations.class, LiferayCRUDOperations.class})
public class LiferayConfig {

	@DisplayName("Connection Timeout")
	@Optional(defaultValue = "5")
	@Parameter
	@Placement(order = 1, tab = Placement.ADVANCED_TAB)
	@Summary("Socket connection timeout value")
	private int connectionTimeout;

	@DisplayName("Connection Timeout Unit")
	@Optional(defaultValue = "SECONDS")
	@Parameter
	@Placement(order = 2, tab = Placement.ADVANCED_TAB)
	@Summary("Time unit to be used in the Timeout configurations")
	private TimeUnit connectionTimeoutTimeUnit;

}