/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.live;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;

/**
 * @author Kevin Lee
 */
public class LiveUpgradeProcessFactory {

	public static LiveUpgradeProcess addColumns(String... columnDefinitions) {
		return (tableName, liveUpgradeSchemaDiff) -> {
			UpgradeProcess upgradeProcess = UpgradeProcessFactory.addColumns(
				tableName, columnDefinitions);

			upgradeProcess.upgrade();

			liveUpgradeSchemaDiff.recordAddColumns(columnDefinitions);
		};
	}

	public static LiveUpgradeProcess alterColumnName(
		String oldColumnName, String newColumnName) {

		return (tableName, liveUpgradeSchemaDiff) -> {
			UpgradeProcess upgradeProcess =
				UpgradeProcessFactory.alterColumnName(
					tableName, oldColumnName, newColumnName);

			upgradeProcess.upgrade();

			liveUpgradeSchemaDiff.recordAlterColumnName(
				oldColumnName, newColumnName);
		};
	}

	public static LiveUpgradeProcess alterColumnType(
		String columnName, String newColumnType) {

		return (tableName, liveUpgradeSchemaDiff) -> {
			UpgradeProcess upgradeProcess =
				UpgradeProcessFactory.alterColumnType(
					tableName, columnName, newColumnType);

			upgradeProcess.upgrade();

			liveUpgradeSchemaDiff.recordAlterColumnType(
				columnName, newColumnType);
		};
	}

	public static LiveUpgradeProcess dropColumns(String... columnNames) {
		return (tableName, liveUpgradeSchemaDiff) -> {
			UpgradeProcess upgradeProcess = UpgradeProcessFactory.dropColumns(
				tableName, columnNames);

			upgradeProcess.upgrade();

			liveUpgradeSchemaDiff.recordDropColumns(columnNames);
		};
	}

}