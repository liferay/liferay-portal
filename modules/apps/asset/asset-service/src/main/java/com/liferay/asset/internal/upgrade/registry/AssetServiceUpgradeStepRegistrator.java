/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.internal.upgrade.registry;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.ViewCountUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(service = UpgradeStepRegistrator.class)
public class AssetServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register("0.0.1", "1.0.0", new DummyUpgradeStep());

		registry.register("1.0.0", "1.1.0", new DummyUpgradeStep());

		registry.register(
			"1.1.0", "2.0.0", new DummyUpgradeStep(),
			new ViewCountUpgradeProcess(
				"AssetEntry", AssetEntry.class, "entryId", "viewCount"));

		registry.register("2.0.0", "2.0.1", new DummyUpgradeStep());

		registry.register("2.0.1", "2.1.0", new DummyUpgradeStep());
	}

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.view.count.service)(release.schema.version>=1.0.0))"
	)
	private Release _release;

}