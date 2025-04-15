/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.upgrade.v1_0_5;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;

import jakarta.portlet.PortletPreferences;

/**
 * @author Balázs Sáfrány-Kovalik
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

		String anyClassType = portletPreferences.getValue(
			"anyClassType", Boolean.TRUE.toString());
		String anyClassTypeDLFileEntryAssetRendererFactory =
			portletPreferences.getValue(
				"anyClassTypeDLFileEntryAssetRendererFactory",
				Boolean.TRUE.toString());
		String anyClassTypeJournalArticleAssetRendererFactory =
			portletPreferences.getValue(
				"anyClassTypeJournalArticleAssetRendererFactory",
				Boolean.TRUE.toString());

		if (anyClassType.equals("select-more-than-one")) {
			portletPreferences.setValue("anyClassType", "false");
		}

		if (anyClassTypeDLFileEntryAssetRendererFactory.equals(
				"select-more-than-one")) {

			portletPreferences.setValue(
				"anyClassTypeDLFileEntryAssetRendererFactory", "false");
		}

		if (anyClassTypeJournalArticleAssetRendererFactory.equals(
				"select-more-than-one")) {

			portletPreferences.setValue(
				"anyClassTypeJournalArticleAssetRendererFactory", "false");
		}

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

}