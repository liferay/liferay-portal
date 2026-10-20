/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.upgrade.v1_0_0;

import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.wiki.configuration.WikiGroupServiceConfiguration;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.web.internal.configuration.WikiPortletInstanceConfiguration;

/**
 * @author Iván Zaera
 */
public class UpgradePortletSettings
	extends com.liferay.portal.upgrade.v7_0_0.UpgradePortletSettings {

	@Override
	protected void doUpgrade() throws Exception {
		upgradeMainPortlet(
			WikiPortletKeys.WIKI, WikiConstants.SERVICE_NAME,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
			getKeys(WikiPortletInstanceConfiguration.class),
			getKeys(WikiGroupServiceConfiguration.class));
	}

}