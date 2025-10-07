/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.internal.upgrade.registry;

import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.release.ReleaseRenamingUpgradeStep;
import com.liferay.saml.persistence.internal.upgrade.v2_4_0.util.SamlPeerBindingTable;
import com.liferay.saml.persistence.internal.upgrade.v3_0_1.SamlSpIdpConnectionDataUpgradeProcess;
import com.liferay.saml.persistence.internal.upgrade.v3_0_2.SamlPeerBindingUpgradeProcess;
import com.liferay.saml.persistence.internal.upgrade.v3_2_0.util.SamlIbSloMessageTable;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 */
@Component(service = UpgradeStepRegistrator.class)
public class SamlServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerReleaseCreationUpgradeSteps(
			new ReleaseRenamingUpgradeStep(
				"com.liferay.saml.persistence.service", "saml-portlet",
				_releaseLocalService));

		registry.register("0.0.1", "1.0.0", new DummyUpgradeStep());

		registry.register(
			"1.0.0", "1.1.0",
			UpgradeProcessFactory.alterColumnType(
				"SamlIdpSpSession", "nameIdFormat", "VARCHAR(1024) null"),
			UpgradeProcessFactory.alterColumnType(
				"SamlIdpSpSession", "nameIdValue", "VARCHAR(1024) null"),
			UpgradeProcessFactory.alterColumnType(
				"SamlIdpSpSession", "samlSpEntityId", "VARCHAR(1024) null"),
			UpgradeProcessFactory.alterColumnType(
				"SamlSpAuthRequest", "samlIdpEntityId", "VARCHAR(1024) null"),
			UpgradeProcessFactory.alterColumnType(
				"SamlSpMessage", "samlIdpEntityId", "VARCHAR(1024) null"),
			UpgradeProcessFactory.alterColumnType(
				"SamlSpSession", "nameIdFormat", "VARCHAR(1024) null"),
			UpgradeProcessFactory.alterColumnType(
				"SamlSpSession", "nameIdValue", "VARCHAR(1024) null"));

		registry.register(
			"1.1.0", "1.1.1",
			UpgradeProcessFactory.alterColumnType(
				"SamlSpSession", "assertionXml", "TEXT null"),
			UpgradeProcessFactory.alterColumnType(
				"SamlSpSession", "samlSpSessionKey", "VARCHAR(75) null"),
			UpgradeProcessFactory.alterColumnType(
				"SamlSpSession", "sessionIndex", "VARCHAR(75) null"));

		registry.register(
			"1.1.1", "1.1.2",
			UpgradeProcessFactory.alterColumnType(
				"SamlSpSession", "jSessionId", "VARCHAR(200) null"));

		registry.register(
			"1.1.2", "1.1.3",
			UpgradeProcessFactory.alterColumnType(
				"SamlSpIdpConnection", "forceAuthn", "BOOLEAN"));

		registry.register(
			"1.1.3", "1.1.4",
			new com.liferay.saml.persistence.internal.upgrade.v1_1_4.
				UpgradeClassNames());

		registry.register(
			"1.1.4", "2.0.0",
			new com.liferay.saml.persistence.internal.upgrade.v2_0_0.
				SamlSpSessionDataUpgradeProcess(_configurationAdmin));

		registry.register(
			"2.0.0", "2.1.0",
			UpgradeProcessFactory.addColumns(
				"SamlIdpSpConnection", "encryptionForced BOOLEAN"));

		registry.register(
			"2.1.0", "2.2.0",
			UpgradeProcessFactory.addColumns(
				"SamlSpIdpConnection", "unknownUsersAreStrangers BOOLEAN"));

		registry.register(
			"2.2.0", "2.3.0",
			UpgradeProcessFactory.addColumns(
				"SamlSpIdpConnection",
				"userIdentifierExpression VARCHAR(200) null"));

		registry.register("2.3.0", "2.4.0", SamlPeerBindingTable.create());

		registry.register(
			"2.4.0", "2.4.1",
			UpgradeProcessFactory.alterColumnType(
				"SamlPeerBinding", "samlNameIdNameQualifier",
				"VARCHAR(1024) null"));

		registry.register(
			"2.4.1", "2.4.2",
			new com.liferay.saml.persistence.internal.upgrade.v3_0_0.
				SamlIdpSpSessionUpgradeProcess());

		registry.register(
			"2.4.2", "2.5.0",
			new com.liferay.saml.persistence.internal.upgrade.v3_0_0.
				SamlSpSessionUpgradeProcess());

		registry.register(
			"2.5.0", "3.0.0",
			UpgradeProcessFactory.dropColumns(
				"SamlIdpSpSession", "nameIdFormat"),
			UpgradeProcessFactory.dropColumns(
				"SamlIdpSpSession", "nameIdValue"),
			UpgradeProcessFactory.dropColumns(
				"SamlIdpSpSession", "samlSpEntityId"),
			UpgradeProcessFactory.dropColumns("SamlSpSession", "nameIdFormat"),
			UpgradeProcessFactory.dropColumns(
				"SamlSpSession", "nameIdNameQualifier"),
			UpgradeProcessFactory.dropColumns(
				"SamlSpSession", "nameIdSPNameQualifier"),
			UpgradeProcessFactory.dropColumns("SamlSpSession", "nameIdValue"),
			UpgradeProcessFactory.dropColumns(
				"SamlSpSession", "samlIdpEntityId"));

		registry.register(
			"3.0.0", "3.0.1", new SamlSpIdpConnectionDataUpgradeProcess());

		registry.register(
			"3.0.1", "3.0.2", new SamlPeerBindingUpgradeProcess(),
			UpgradeProcessFactory.alterColumnType(
				"SamlPeerBinding", "samlPeerEntityId", "VARCHAR(1024) null"),
			UpgradeProcessFactory.alterColumnType(
				"SamlPeerBinding", "samlNameIdFormat", "VARCHAR(128) null"),
			UpgradeProcessFactory.alterColumnType(
				"SamlPeerBinding", "samlNameIdValue", "VARCHAR(1024) null"));

		registry.register(
			"3.0.2", "3.0.3",
			UpgradeProcessFactory.alterColumnType(
				"SamlSpSession", "sessionIndex", "VARCHAR(200) null"));

		registry.register(
			"3.0.3", "3.0.4",
			UpgradeProcessFactory.alterColumnType(
				"SamlPeerBinding", "samlNameIdNameQualifier",
				"VARCHAR(1024) null"));

		registry.register(
			"3.0.4", "3.1.0",
			UpgradeProcessFactory.addColumns(
				"SamlSpAuthRequest", "samlRelayState VARCHAR(2048) null"));

		registry.register("3.1.0", "3.2.0", SamlIbSloMessageTable.create());
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ReleaseLocalService _releaseLocalService;

}