/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.web.internal.upgrade.v1_0_5;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.display.template.upgrade.BaseUpgradePortletPreferences;
import com.liferay.site.navigation.constants.SiteNavigationMenuPortletKeys;

import jakarta.portlet.PortletPreferences;

/**
 * @author Jonathan McCann
 */
public class UpgradePortletPreferences extends BaseUpgradePortletPreferences {

	public UpgradePortletPreferences(LayoutLocalService layoutLocalService) {
		_layoutLocalService = layoutLocalService;
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

		String rootMenuItemExternalReferenceCode = portletPreferences.getValue(
			"rootMenuItemExternalReferenceCode", null);

		if (Validator.isNull(rootMenuItemExternalReferenceCode)) {
			return;
		}

		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			return;
		}

		Layout rootLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			rootMenuItemExternalReferenceCode, layout.getGroupId(), false);

		if (rootLayout == null) {
			rootLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
				rootMenuItemExternalReferenceCode, layout.getGroupId(), true);
		}

		if (rootLayout == null) {
			return;
		}

		portletPreferences.setValue(
			"rootMenuItemExternalReferenceCode",
			rootLayout.getExternalReferenceCode());
	}

	private final LayoutLocalService _layoutLocalService;

}