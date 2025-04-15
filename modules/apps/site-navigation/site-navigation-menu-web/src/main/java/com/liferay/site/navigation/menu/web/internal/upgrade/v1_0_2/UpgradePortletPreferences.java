/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.web.internal.upgrade.v1_0_2;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.display.template.upgrade.BaseUpgradePortletPreferences;
import com.liferay.site.navigation.constants.SiteNavigationMenuPortletKeys;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import jakarta.portlet.PortletPreferences;

/**
 * @author Javier Moral
 */
public class UpgradePortletPreferences extends BaseUpgradePortletPreferences {

	public UpgradePortletPreferences(
		SiteNavigationMenuItemLocalService siteNavigationMenuItemLocalService,
		SiteNavigationMenuLocalService siteNavigationMenuLocalService) {

		_siteNavigationMenuItemLocalService =
			siteNavigationMenuItemLocalService;
		_siteNavigationMenuLocalService = siteNavigationMenuLocalService;
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {
			SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU + "%"
		};
	}

	@Override
	protected void upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, PortletPreferences portletPreferences)
		throws Exception {

		long siteNavigationMenuId = GetterUtil.getLong(
			portletPreferences.getValue("siteNavigationMenuId", null));

		if (siteNavigationMenuId == 0) {
			return;
		}

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.fetchSiteNavigationMenu(
				siteNavigationMenuId);

		if (siteNavigationMenu == null) {
			return;
		}

		portletPreferences.setValue(
			"siteNavigationMenuExternalReferenceCode",
			siteNavigationMenu.getExternalReferenceCode());

		String scopeExternalReferenceCode = getScopeExternalReferenceCode(
			companyId, ownerId, ownerType, plid,
			siteNavigationMenu.getGroupId());

		if (Validator.isNotNull(scopeExternalReferenceCode)) {
			portletPreferences.setValue(
				"siteNavigationMenuGroupExternalReferenceCode",
				scopeExternalReferenceCode);
		}

		long rootMenuItemId = GetterUtil.getLong(
			portletPreferences.getValue("rootMenuItemId", null));

		if (rootMenuItemId == 0) {
			return;
		}

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.fetchSiteNavigationMenuItem(
				rootMenuItemId);

		if (siteNavigationMenuItem == null) {
			return;
		}

		portletPreferences.setValue(
			"rootMenuItemExternalReferenceCode",
			siteNavigationMenuItem.getExternalReferenceCode());
	}

	private final SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;
	private final SiteNavigationMenuLocalService
		_siteNavigationMenuLocalService;

}