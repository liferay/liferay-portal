/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.changeset.internal.upgrade.registry;

import com.liferay.changeset.internal.upgrade.v2_0_0.util.ChangesetCollectionTable;
import com.liferay.changeset.internal.upgrade.v2_0_0.util.ChangesetEntryTable;
import com.liferay.portal.kernel.upgrade.BaseSQLServerDatetimeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author José Ángel Jiménez
 */
@Component(service = UpgradeStepRegistrator.class)
public class ChangesetServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "2.0.0",
			new BaseSQLServerDatetimeUpgradeProcess(
				new Class<?>[] {
					ChangesetCollectionTable.class, ChangesetEntryTable.class
				}));

		registry.register(
			"2.0.0", "2.1.0",
			UpgradeProcessFactory.addColumns(
				"ChangesetEntry",
				"classExternalReferenceCode VARCHAR(1000) null"));

		registry.register(
			"2.1.0", "3.0.0",
			new com.liferay.changeset.internal.upgrade.v3_0_0.
				ChangesetEntryIndexedColumnSizeUpgradeProcess());
	}

}