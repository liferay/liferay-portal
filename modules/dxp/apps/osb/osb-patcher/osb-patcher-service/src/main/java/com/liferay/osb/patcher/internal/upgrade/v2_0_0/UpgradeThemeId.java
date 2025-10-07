/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.internal.upgrade.v2_0_0;

import com.liferay.portal.kernel.upgrade.BaseThemeIdUpgradeProcess;

/**
 * @author Eudaldo Alonso
 */
public class UpgradeThemeId extends BaseThemeIdUpgradeProcess {

	@Override
	public String[][] getThemeIds() {
		return new String[][] {
			{"osbpatcher_WAR_osbpatchertheme", "classic_WAR_classictheme"}
		};
	}

}