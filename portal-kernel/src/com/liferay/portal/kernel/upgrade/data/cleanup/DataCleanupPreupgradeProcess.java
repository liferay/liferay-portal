/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.data.cleanup;

import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Luis Ortiz
 */
public class DataCleanupPreupgradeProcess extends UpgradeProcess {

	public static List<DataCleanupPreupgradeProcess> dependsOn(
		DataCleanupPreupgradeProcess... dataCleanupPreupgradeProcesses) {

		return ListUtil.fromArray(dataCleanupPreupgradeProcesses);
	}

	public static List<DataCleanupPreupgradeProcess>
		getSortedDataCleanupPreupgradeProcesses(
			Map
				<DataCleanupPreupgradeProcess,
				 List<DataCleanupPreupgradeProcess>>
					dataCleanupPreupgradeProcessesMap) {

		List<DataCleanupPreupgradeProcess>
			sortedDataCleanupPreupgradeProcesses = new ArrayList<>();

		while (sortedDataCleanupPreupgradeProcesses.size() !=
					dataCleanupPreupgradeProcessesMap.size()) {

			int size = sortedDataCleanupPreupgradeProcesses.size();

			for (Map.Entry
					<DataCleanupPreupgradeProcess,
					 List<DataCleanupPreupgradeProcess>> entry :
						dataCleanupPreupgradeProcessesMap.entrySet()) {

				DataCleanupPreupgradeProcess dataCleanupPreupgradeProcess =
					entry.getKey();

				if (sortedDataCleanupPreupgradeProcesses.contains(
						dataCleanupPreupgradeProcess) ||
					!sortedDataCleanupPreupgradeProcesses.containsAll(
						entry.getValue())) {

					continue;
				}

				sortedDataCleanupPreupgradeProcesses.add(
					dataCleanupPreupgradeProcess);
			}

			if (size == sortedDataCleanupPreupgradeProcesses.size()) {
				throw new RuntimeException("Circular dependency");
			}
		}

		return sortedDataCleanupPreupgradeProcesses;
	}

	public DataCleanupPreupgradeProcess() {
		_dataCleanupPreupgradeProcesses = Collections.emptyList();
	}

	public DataCleanupPreupgradeProcess(
		DataCleanupPreupgradeProcess... dataCleanupPreupgradeProcesses) {

		_dataCleanupPreupgradeProcesses = ListUtil.fromArray(
			dataCleanupPreupgradeProcesses);
	}

	@Override
	public void upgrade() throws DataCleanupPreupgradeException {
		try {
			super.upgrade();
		}
		catch (UpgradeException upgradeException) {
			throw new DataCleanupPreupgradeException(upgradeException);
		}
	}

	@Override
	public void upgrade(UpgradeProcess upgradeProcess)
		throws DataCleanupPreupgradeException {

		try {
			upgradeProcess.upgrade();
		}
		catch (UpgradeException upgradeException) {
			throw new DataCleanupPreupgradeException(upgradeException);
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		for (DataCleanupPreupgradeProcess dataCleanupPreupgradeProcess :
				_dataCleanupPreupgradeProcesses) {

			upgrade(dataCleanupPreupgradeProcess);
		}
	}

	private final List<DataCleanupPreupgradeProcess>
		_dataCleanupPreupgradeProcesses;

}