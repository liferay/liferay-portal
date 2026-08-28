/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.internal.upgrade.registry;

import com.liferay.oauth.client.persistence.internal.upgrade.v1_4_0.OAuthClientASLocalMetadataUpgradeProcess;
import com.liferay.oauth.client.persistence.internal.upgrade.v1_4_1.OAuthClientEntryUpgradeProcess;
import com.liferay.oauth.client.persistence.internal.upgrade.v1_5_2.OAuthClientASLocalMetadataIssuerUpgradeProcess;
import com.liferay.oauth.client.persistence.internal.upgrade.v1_6_0.util.OAuthClientPRLocalMetadataTable;
import com.liferay.oauth.client.persistence.internal.upgrade.v1_6_1.OAuthClientEntryTokenConnectionTimeoutUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseUuidUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(service = UpgradeStepRegistrator.class)
public class OAuthClientPersistenceServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "1.1.0",
			new com.liferay.oauth.client.persistence.internal.upgrade.v1_1_0.
				OAuthClientEntryOIDCUserInfoMapperJSONUpgradeProcess());

		registry.register(
			"1.1.0", "1.2.0",
			UpgradeProcessFactory.addColumns(
				"OAuthClientEntry", "metadataCacheTime LONG"),
			UpgradeProcessFactory.runSQL(
				"update OAuthClientEntry set metadataCacheTime = 360000"));

		registry.register(
			"1.2.0", "1.3.0",
			UpgradeProcessFactory.addColumns(
				"OAuthClientEntry", "customClaimsJSON TEXT null"));

		registry.register(
			"1.3.0", "1.4.0", new OAuthClientASLocalMetadataUpgradeProcess());

		registry.register(
			"1.4.0", "1.4.1",
			new OAuthClientEntryUpgradeProcess(_configurationAdmin));

		registry.register(
			"1.4.1", "1.5.0",
			new BaseUuidUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"OAuthClientEntry", "OAuthClientASLocalMetadata"
					};
				}

			});

		registry.register(
			"1.5.0", "1.5.1",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"OAuthClientEntry", "OAuthClientASLocalMetadata"
					};
				}

			});

		registry.register(
			"1.5.1", "1.5.2",
			new OAuthClientASLocalMetadataIssuerUpgradeProcess());

		registry.register(
			"1.5.2", "1.5.3",
			new com.liferay.oauth.client.persistence.internal.upgrade.v1_5_3.
				OAuthClientASLocalMetadataUpgradeProcess());

		registry.register(
			"1.5.3", "1.6.0", OAuthClientPRLocalMetadataTable.create());

		registry.register(
			"1.6.0", "1.6.1",
			new OAuthClientEntryTokenConnectionTimeoutUpgradeProcess(
				_configurationAdmin));

		registry.register(
			"1.6.1", "1.6.2",
			new com.liferay.oauth.client.persistence.internal.upgrade.v1_6_2.
				OAuthClientASLocalMetadataUpgradeProcess());
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

}