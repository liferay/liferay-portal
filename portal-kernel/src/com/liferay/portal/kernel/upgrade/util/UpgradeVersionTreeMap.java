/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.util;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.version.Version;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * @author Luis Ortiz
 */
public class UpgradeVersionTreeMap
	extends TreeMap<Version, List<UpgradeProcess>> {

	@Override
	public List<UpgradeProcess> put(
		Version key, List<UpgradeProcess> upgradeProcesses) {

		_put(key, new ArrayList<>(upgradeProcesses));

		return upgradeProcesses;
	}

	public void put(Version key, UpgradeProcess... upgradeProcesses) {
		List<UpgradeStep> upgradeStepList = new ArrayList<>();

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			Collections.addAll(
				upgradeStepList, upgradeProcess.getUpgradeSteps());
		}

		_put(key, upgradeStepList);
	}

	private void _put(Version key, List<UpgradeStep> upgradeSteps) {
		Version version = new Version(
			key.getMajor(), key.getMinor(), key.getMicro(), key.getQualifier());

		List<UpgradeProcess> upgradeProcesses = new ArrayList<>();

		for (UpgradeStep upgradeStep : upgradeSteps) {
			if (!(upgradeStep instanceof UpgradeProcess)) {
				continue;
			}

			upgradeProcesses.add((UpgradeProcess)upgradeStep);
		}

		super.put(version, upgradeProcesses);
	}

}