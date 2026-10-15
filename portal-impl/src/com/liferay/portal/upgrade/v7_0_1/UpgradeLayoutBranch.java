/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_0_1;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Miguel Pastor
 */
public class UpgradeLayoutBranch extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		alterColumnName("LayoutBranch", "LayoutBranchId", "layoutBranchId");
	}

}