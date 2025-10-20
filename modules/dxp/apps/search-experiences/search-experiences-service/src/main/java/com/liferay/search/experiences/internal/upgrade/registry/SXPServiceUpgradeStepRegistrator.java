/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.upgrade.registry;

import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.search.experiences.internal.upgrade.v3_1_4.SXPBlueprintAndSXPElementUpgradeProcess;
import com.liferay.search.experiences.internal.upgrade.v3_2_0.SXPBlueprintCollectionProviderUpgradeProcess;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(enabled = true, service = UpgradeStepRegistrator.class)
public class SXPServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "1.1.0",
			UpgradeProcessFactory.addColumns(
				"SXPElement", "key_ VARCHAR(75) null",
				"version VARCHAR(75) null"),
			UpgradeProcessFactory.addColumns(
				"SXPBlueprint", "key_ VARCHAR(75) null",
				"version VARCHAR(75) null"));

		registry.register(
			"1.1.0", "1.2.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"SXPBlueprint", "SXPElement"};
				}

			});

		registry.register(
			"1.2.0", "1.3.0",
			new com.liferay.search.experiences.internal.upgrade.v1_3_0.
				SXPBlueprintAndSXPElementUpgradeProcess());

		registry.register(
			"1.3.0", "1.3.1",
			new com.liferay.search.experiences.internal.upgrade.v1_3_1.
				DummyUpgradeProcess());

		registry.register(
			"1.3.1", "1.3.2",
			new com.liferay.search.experiences.internal.upgrade.v1_3_2.
				DummyUpgradeProcess());

		registry.register(
			"1.3.2", "1.3.3",
			new com.liferay.search.experiences.internal.upgrade.v1_3_3.
				DummyUpgradeProcess());

		registry.register(
			"1.3.3", "2.0.0",
			new com.liferay.search.experiences.internal.upgrade.v2_0_0.
				DummyUpgradeProcess());

		registry.register(
			"2.0.0", "2.0.1",
			new com.liferay.search.experiences.internal.upgrade.v2_0_1.
				SXPBlueprintUpgradeProcess());

		registry.register(
			"2.0.1", "2.0.2",
			new com.liferay.search.experiences.internal.upgrade.v2_0_2.
				SXPBlueprintAndSXPElementUpgradeProcess());

		registry.register(
			"2.0.2", "2.0.3",
			new com.liferay.search.experiences.internal.upgrade.v2_0_3.
				SXPElementUpgradeProcess());

		registry.register(
			"2.0.3", "3.0.0",
			new com.liferay.search.experiences.internal.upgrade.v3_0_0.
				SXPBlueprintUpgradeProcess());

		registry.register(
			"3.0.0", "3.1.0",
			new com.liferay.search.experiences.internal.upgrade.v3_1_0.
				SXPBlueprintAndSXPElementUpgradeProcess());

		registry.register(
			"3.1.0", "3.1.1",
			new com.liferay.search.experiences.internal.upgrade.v3_1_1.
				SXPBlueprintAndSXPElementUpgradeProcess());

		registry.register(
			"3.1.1", "3.1.2",
			new com.liferay.search.experiences.internal.upgrade.v3_1_2.
				SXPBlueprintUpgradeProcess(_jsonFactory));

		registry.register(
			"3.1.2", "3.1.3",
			new com.liferay.search.experiences.internal.upgrade.v3_1_3.
				SXPBlueprintAndSXPElementUpgradeProcess(
					_groupLocalService, _jsonFactory));

		registry.register(
			"3.1.3", "3.1.4",
			new SXPBlueprintAndSXPElementUpgradeProcess(
				_assetCategoryLocalService, _groupLocalService, _jsonFactory));

		registry.register(
			"3.1.4", "3.2.0",
			new SXPBlueprintCollectionProviderUpgradeProcess());
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

}