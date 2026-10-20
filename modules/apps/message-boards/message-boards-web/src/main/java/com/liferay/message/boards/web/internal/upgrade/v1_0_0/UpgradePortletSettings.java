/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.upgrade.v1_0_0;

import com.liferay.message.boards.constants.MBConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.settings.MBGroupServiceSettings;
import com.liferay.portal.kernel.util.PortletKeys;

/**
 * @author Sergio González
 */
public class UpgradePortletSettings
	extends com.liferay.portal.upgrade.v7_0_0.UpgradePortletSettings {

	@Override
	protected void doUpgrade() throws Exception {
		upgradeMainPortlet(
			MBPortletKeys.MESSAGE_BOARDS, MBConstants.SERVICE_NAME,
			PortletKeys.PREFS_OWNER_TYPE_GROUP, new String[0],
			getSettingsKeys(MBGroupServiceSettings.class));
	}

}