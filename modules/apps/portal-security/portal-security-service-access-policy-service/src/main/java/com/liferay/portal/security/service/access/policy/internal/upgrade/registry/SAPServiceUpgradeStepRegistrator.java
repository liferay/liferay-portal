/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.service.access.policy.internal.upgrade.registry;

import com.liferay.portal.kernel.upgrade.BaseSQLServerDatetimeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.security.service.access.policy.internal.upgrade.v3_0_0.util.SAPEntryTable;
import com.liferay.portal.security.service.access.policy.internal.upgrade.v3_0_1.SAPEntryUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marcellus Tavares
 */
@Component(service = UpgradeStepRegistrator.class)
public class SAPServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register("2.0.0", "2.0.13", new DummyUpgradeStep());

		registry.register("2.0.1", "2.0.13", new DummyUpgradeStep());

		registry.register("2.0.2", "2.0.13", new DummyUpgradeStep());

		registry.register("2.0.3", "2.0.13", new DummyUpgradeStep());

		registry.register("2.0.4", "2.0.13", new DummyUpgradeStep());

		registry.register("2.0.5", "2.0.13", new DummyUpgradeStep());

		registry.register("2.0.6", "2.0.13", new DummyUpgradeStep());

		registry.register("2.0.7", "2.0.13", new DummyUpgradeStep());

		registry.register("2.0.8", "2.0.13", new DummyUpgradeStep());

		registry.register("2.0.9", "2.0.13", new DummyUpgradeStep());

		registry.register("2.0.10", "2.0.13", new DummyUpgradeStep());

		registry.register("2.0.11", "2.0.13", new DummyUpgradeStep());

		registry.register("2.0.12", "2.0.13", new DummyUpgradeStep());

		registry.register(
			"2.0.13", "3.0.0",
			new BaseSQLServerDatetimeUpgradeProcess(
				new Class<?>[] {SAPEntryTable.class}));

		registry.register("3.0.0", "3.0.1", new SAPEntryUpgradeProcess());

		registry.register(
			"3.0.1", "3.0.2",
			UpgradeProcessFactory.alterColumnType(
				"SAPEntry", "title", "TEXT null"));
	}

}