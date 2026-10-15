/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.fido2.credential.internal.upgrade.registry;

import com.liferay.multi.factor.authentication.fido2.credential.internal.upgrade.v2_0_0.MFAFIDO2CredentialUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Arthur Chan
 */
@Component(service = UpgradeStepRegistrator.class)
public class MFAFIDO2CredentialServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "2.0.0",
			UpgradeProcessFactory.alterColumnName(
				"MFAFIDO2CredentialEntry", "publicKeyCode", "publicKeyCOSE"));

		registry.register(
			"2.0.0", "2.1.0", new MFAFIDO2CredentialUpgradeProcess());
	}

}