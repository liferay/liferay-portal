/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.internal.upgrade.registry;

import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.security.sso.openid.connect.persistence.internal.upgrade.v2_2_0.OpenIdConnectProviderConfigurationUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(service = UpgradeStepRegistrator.class)
public class OpenIdConnectServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "1.1.0",
			UpgradeProcessFactory.addColumns(
				"OpenIdConnectSession", "userId LONG",
				"configurationPid VARCHAR(256) null"));

		registry.register(
			"1.1.0", "2.0.0",
			new com.liferay.portal.security.sso.openid.connect.persistence.
				internal.upgrade.v2_0_0.OpenIdConnectSessionUpgradeProcess(
					_configurationAdmin));

		registry.register(
			"2.0.0", "2.1.0",
			new com.liferay.portal.security.sso.openid.connect.persistence.
				internal.upgrade.v2_1_0.OpenIdConnectSessionUpgradeProcess());

		registry.register(
			"2.1.0", "2.1.1",
			UpgradeProcessFactory.alterColumnType(
				"OpenIdConnectSession", "accessToken", "TEXT null"),
			UpgradeProcessFactory.alterColumnType(
				"OpenIdConnectSession", "idToken", "TEXT null"));

		registry.register(
			"2.1.1", "2.2.0",
			new OpenIdConnectProviderConfigurationUpgradeProcess(
				_oAuthClientEntryLocalService));
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

}