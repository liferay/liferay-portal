/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.upgrade.registry;

import com.liferay.asset.categories.internal.upgrade.v1_0_0.AssetCategoryFriendlyURLUpgradeProcess;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(service = UpgradeStepRegistrator.class)
public class AssetCategoriesServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"0.0.1", "1.0.0",
			new AssetCategoryFriendlyURLUpgradeProcess(
				_assetCategoryLocalService, _classNameLocalService,
				_friendlyURLEntryLocalService));
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

}