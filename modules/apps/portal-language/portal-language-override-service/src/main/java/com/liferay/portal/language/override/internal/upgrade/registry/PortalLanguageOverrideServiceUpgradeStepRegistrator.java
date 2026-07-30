/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.internal.upgrade.registry;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.language.override.internal.upgrade.v1_1_0.PLOEntryExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Thiago Buarque
 */
@Component(service = UpgradeStepRegistrator.class)
public class PortalLanguageOverrideServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "1.1.0",
			(UpgradeProcess)new PLOEntryExternalReferenceCodeUpgradeProcess());
	}

}