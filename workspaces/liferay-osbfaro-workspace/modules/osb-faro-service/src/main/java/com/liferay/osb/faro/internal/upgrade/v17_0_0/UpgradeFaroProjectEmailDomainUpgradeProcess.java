/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.internal.upgrade.v17_0_0;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

/**
 * @author Leilany Ulisses
 */
public class UpgradeFaroProjectEmailDomainUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		alterTableName(
			"OSBFaro_FaroProjectEmailAddressDomain",
			"OSBFaro_FaroProjectEmailDomain");
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.alterColumnName(
				"OSBFaro_FaroProjectEmailAddressDomain",
				"faroProjectEmailAddressDomainId", "faroProjectEmailDomainId"),
			UpgradeProcessFactory.alterColumnName(
				"OSBFaro_FaroProjectEmailAddressDomain", "emailAddressDomain",
				"emailDomain")
		};
	}

}