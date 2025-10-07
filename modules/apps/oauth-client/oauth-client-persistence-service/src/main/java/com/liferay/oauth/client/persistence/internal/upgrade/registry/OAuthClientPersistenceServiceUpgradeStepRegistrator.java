/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.internal.upgrade.registry;

import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

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
	}

}