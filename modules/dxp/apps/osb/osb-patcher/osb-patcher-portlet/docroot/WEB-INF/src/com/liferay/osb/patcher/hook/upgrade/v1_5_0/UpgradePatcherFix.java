/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.hook.upgrade.v1_5_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.util.portlet.PortletProps;

import java.io.File;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherFix extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFix();
	}

	protected void updatePatcherFix() throws Exception {
		File file = new File(_OSB_PATCHER_STATUS_PATH + "/build/patch");

		if (file.exists()) {
			file.delete();
		}
	}

	private static final String _OSB_PATCHER_STATUS_PATH = PortletProps.get(
		"osb.patcher.status.path");

}