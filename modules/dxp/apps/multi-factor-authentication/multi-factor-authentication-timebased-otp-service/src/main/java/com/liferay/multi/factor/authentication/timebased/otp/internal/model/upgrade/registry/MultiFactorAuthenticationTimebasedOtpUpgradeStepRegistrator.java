/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.timebased.otp.internal.model.upgrade.registry;

import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Manuele Castro
 */
@Component(service = UpgradeStepRegistrator.class)
public class MultiFactorAuthenticationTimebasedOtpUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "1.0.1",
			UpgradeProcessFactory.addColumns(
				"MFATimeBasedOTPEntry", "lastValidTOTP VARCHAR(75) null"));

		registry.register(
			"1.0.1", "1.0.2",
			UpgradeProcessFactory.addColumns(
				"MFATimeBasedOTPEntry", "algorithm VARCHAR(75) null"));
	}

}