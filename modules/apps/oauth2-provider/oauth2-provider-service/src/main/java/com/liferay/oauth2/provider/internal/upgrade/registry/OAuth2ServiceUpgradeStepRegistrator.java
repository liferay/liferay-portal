/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.upgrade.registry;

import com.liferay.oauth2.provider.internal.upgrade.v2_0_0.OAuth2ApplicationScopeAliasesUpgradeProcess;
import com.liferay.oauth2.provider.internal.upgrade.v3_2_0.OAuth2ApplicationFeatureUpgradeProcess;
import com.liferay.oauth2.provider.internal.upgrade.v4_1_0.OAuth2ApplicationClientAuthenticationMethodUpgradeProcess;
import com.liferay.oauth2.provider.internal.upgrade.v4_2_1.OAuth2ScopeGrantRemoveCompanyIdFromObjectsRelatedUpgradeProcess;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseUuidUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 */
@Component(service = UpgradeStepRegistrator.class)
public class OAuth2ServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "1.1.0",
			UpgradeProcessFactory.alterColumnType(
				"OAuth2ScopeGrant", "scope", "VARCHAR(240) null"));

		registry.register(
			"1.1.0", "1.2.0",
			UpgradeProcessFactory.addColumns(
				"OAuth2Authorization", "remoteHostInfo VARCHAR(255) null"));

		registry.register(
			"1.2.0", "1.3.0",
			UpgradeProcessFactory.addColumns(
				"OAuth2ScopeGrant", "scopeAliases TEXT null"));

		registry.register(
			"1.3.0", "2.0.0",
			new OAuth2ApplicationScopeAliasesUpgradeProcess(
				_companyLocalService, _scopeLocator));

		registry.register(
			"2.0.0", "3.0.0",
			UpgradeProcessFactory.addColumns(
				"OAuth2Application", "clientCredentialUserId LONG"),
			UpgradeProcessFactory.addColumns(
				"OAuth2Application",
				"clientCredentialUserName VARCHAR(75) null"),
			UpgradeProcessFactory.runSQL(
				"update OAuth2Application set clientCredentialUserId = " +
					"userId, clientCredentialUserName = userName"));

		registry.register(
			"3.0.0", "3.1.0",
			UpgradeProcessFactory.addColumns(
				"OAuth2Application", "rememberDevice BOOLEAN"),
			UpgradeProcessFactory.addColumns(
				"OAuth2Application", "trustedApplication BOOLEAN"),
			UpgradeProcessFactory.addColumns(
				"OAuth2Authorization",
				"rememberDeviceContent VARCHAR(75) null"));

		registry.register(
			"3.1.0", "4.0.0", new OAuth2ApplicationFeatureUpgradeProcess());

		registry.register(
			"4.0.0", "4.0.1",
			UpgradeProcessFactory.alterColumnType(
				"OAuth2Application", "allowedGrantTypes", "VARCHAR(128) null"));

		registry.register(
			"4.0.1", "4.1.0",
			new OAuth2ApplicationClientAuthenticationMethodUpgradeProcess());

		registry.register(
			"4.1.0", "4.2.0",
			new BaseUuidUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"OAuth2Application"};
				}

			});

		registry.register(
			"4.2.0", "4.2.1",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"OAuth2Application"};
				}

			});

		registry.register(
			"4.2.1", "4.2.2",
			new OAuth2ScopeGrantRemoveCompanyIdFromObjectsRelatedUpgradeProcess());

		registry.register(
			"4.2.2", "4.2.3",
			UpgradeProcessFactory.runSQL(
				StringBundler.concat(
					"update OAuth2Application set clientAuthenticationMethod ",
					"= 'client_secret_post' where clientAuthenticationMethod ",
					"= 'client_secret_basic'")));

		registry.register(
			"4.2.3", "4.2.4",
			UpgradeProcessFactory.alterColumnType(
				"OAuth2Application", "name", "VARCHAR(255) null"));

		registry.register(
			"4.2.4", "4.2.5",
			UpgradeProcessFactory.runSQL(
				StringBundler.concat(
					"update User_ set passwordReset = [$FALSE$] where type_ = ",
					UserConstants.TYPE_DEFAULT_SERVICE_ACCOUNT, " or type_ = ",
					UserConstants.TYPE_SERVICE_ACCOUNT)));

		registry.register(
			"4.2.5", "4.2.6",
			UpgradeProcessFactory.runSQL(
				StringBundler.concat(
					"update OAuth2ScopeGrant set applicationName = ",
					"LOWER(applicationName), scopeAliases = ",
					"LOWER(scopeAliases) where bundleSymbolicName = ",
					"'com.liferay.object.rest.impl'")));

		registry.register(
			"4.2.6", "4.2.7",
			UpgradeProcessFactory.alterColumnType(
				"OAuth2Application", "externalReferenceCode",
				"VARCHAR(1000) null"));

		registry.register(
			"4.2.7", "4.2.8",
			UpgradeProcessFactory.addColumns(
				"OAuth2Authorization", "audiences TEXT null"));
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ScopeLocator _scopeLocator;

}