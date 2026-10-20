/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.upgrade.v2_0_1;

import com.liferay.knowledge.base.configuration.KBGroupServiceConfiguration;
import com.liferay.knowledge.base.constants.KBConstants;
import com.liferay.portal.kernel.util.PortletKeys;

/**
 * @author Alejandro Tardín
 */
public class UpgradePortletSettings
	extends com.liferay.portal.upgrade.v7_0_0.UpgradePortletSettings {

	@Override
	protected void doUpgrade() throws Exception {
		upgradeMainPortlet(
			KBGroupServiceConfiguration.class.getName(),
			KBConstants.SERVICE_NAME, PortletKeys.PREFS_OWNER_TYPE_GROUP,
			new String[0], getKeys(KBGroupServiceConfiguration.class));
	}

}