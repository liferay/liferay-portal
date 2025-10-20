/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.internal.upgrade.registry;

import com.liferay.client.extension.internal.upgrade.v3_0_0.ClassNamesUpgradeProcess;
import com.liferay.client.extension.internal.upgrade.v3_1_0.util.ClientExtensionEntryRelTable;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.release.ReleaseRenamingUpgradeStep;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera
 */
@Component(service = UpgradeStepRegistrator.class)
public class ClientExtensionUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerReleaseCreationUpgradeSteps(
			new ReleaseRenamingUpgradeStep(
				"com.liferay.client.extension.service",
				"com.liferay.remote.app.service", _releaseLocalService));

		registry.register(
			"1.0.0", "1.0.1",
			UpgradeProcessFactory.alterColumnType(
				"RemoteAppEntry", "url", "VARCHAR(1024) null"));

		registry.register(
			"1.0.1", "2.0.0",
			new com.liferay.client.extension.internal.upgrade.v2_0_0.
				RemoteAppEntryUpgradeProcess());

		registry.register(
			"2.0.0", "2.1.0",
			new com.liferay.client.extension.internal.upgrade.v2_1_0.
				ResourcePermissionsUpgradeProcess());

		registry.register(
			"2.1.0", "2.2.0",
			UpgradeProcessFactory.addColumns(
				"RemoteAppEntry", "friendlyURLMapping VARCHAR(75)"));

		registry.register(
			"2.2.0", "2.3.0",
			new com.liferay.client.extension.internal.upgrade.v2_3_0.
				RemoteAppEntryUpgradeProcess());

		registry.register(
			"2.3.0", "2.4.0",
			new com.liferay.client.extension.internal.upgrade.v2_4_0.
				RemoteAppEntryUpgradeProcess());

		registry.register(
			"2.4.0", "2.5.0",
			new com.liferay.client.extension.internal.upgrade.v2_5_0.
				RemoteAppEntryUpgradeProcess());

		registry.register("2.5.0", "2.5.1", new ClassNamesUpgradeProcess());

		registry.register(
			"2.5.1", "3.0.0",
			new com.liferay.client.extension.internal.upgrade.v3_0_0.
				ClientExtensionEntryUpgradeProcess());

		registry.register(
			"3.0.0", "3.0.1", ClientExtensionEntryRelTable.create());

		registry.register(
			"3.0.1", "3.1.0",
			new com.liferay.client.extension.internal.upgrade.v3_1_0.
				ClientExtensionEntryUpgradeProcess());

		registry.register(
			"3.1.0", "3.2.0",
			new CTModelUpgradeProcess(
				"ClientExtensionEntry", "ClientExtensionEntryRel"));

		registry.register(
			"3.2.0", "3.3.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"ClientExtensionEntry"};
				}

			});

		registry.register(
			"3.3.0", "3.4.0",
			UpgradeProcessFactory.addColumns(
				"ClientExtensionEntryRel", "typeSettings TEXT null"));

		registry.register(
			"3.4.0", "3.5.0",
			UpgradeProcessFactory.addColumns(
				"ClientExtensionEntryRel", "groupId LONG",
				"lastPublishDate DATE null"));

		registry.register("3.5.0", "3.5.1", new DummyUpgradeProcess());
	}

	@Reference
	private ReleaseLocalService _releaseLocalService;

}