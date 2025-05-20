/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.hook.upgrade;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.osb.patcher.hook.upgrade.v7_3_0.UpgradePatcherFix;

/**
 * @author Kiana Suetani
 */
public class UpgradeProcess_7_3_0 extends UpgradeProcess {

	@Override
	public int getThreshold() {
		return 730;
	}

	@Override
	protected void doUpgrade() throws Exception {
		upgrade(UpgradePatcherFix.class);
	}

}