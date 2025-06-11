/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.internal.upgrade.v2_0_0;

import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.portal.kernel.upgrade.BasePortletIdUpgradeProcess;

/**
 * @author Eudaldo Alonso
 */
public class UpgradePortletId extends BasePortletIdUpgradeProcess {

	@Override
	protected String[][] getRenamePortletIdsArray() {
		return new String[][] {
			{"1_WAR_osbpatcherportlet", PatcherPortletKeys.PATCHER}
		};
	}

}