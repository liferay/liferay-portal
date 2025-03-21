/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.upgrade.v1_0_4;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

/**
 * @author Sergio González
 */
public class UpgradePortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	@Override
	protected String[] getPortletIds() {
		return new String[] {
			AssetPublisherPortletKeys.ASSET_PUBLISHER + "_INSTANCE_%"
		};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		String enableSocialBookmarks = portletPreferences.getValue(
			"enableSocialBookmarks", Boolean.TRUE.toString());
		String socialBookmarksTypes = portletPreferences.getValue(
			"socialBookmarksTypes", StringPool.BLANK);

		if (!enableSocialBookmarks.equals(Boolean.TRUE.toString())) {
			portletPreferences.setValue(
				"socialBookmarksTypes", StringPool.BLANK);
		}
		else if (Validator.isNull(socialBookmarksTypes)) {
			portletPreferences.setValue(
				"socialBookmarksTypes", "facebook,twitter");
		}

		portletPreferences.reset("enableSocialBookmarks");
		portletPreferences.reset("socialBookmarksDisplayPosition");

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

}