/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.application.list;

import com.liferay.application.list.BasePanelCategory;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.site.dsr.site.initializer.internal.constants.DSRConstants;
import com.liferay.site.dsr.site.initializer.internal.util.DSRUtil;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = {
		"panel.category.key=" + PanelCategoryKeys.COMMERCE,
		"panel.category.order:Integer=360"
	},
	service = PanelCategory.class
)
public class DigitalSalesRoomManagementPanelCategory extends BasePanelCategory {

	@Override
	public String getKey() {
		return _KEY;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "digital-sales-room-management");
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		group = _groupLocalService.fetchGroup(
			permissionChecker.getCompanyId(), GroupConstants.DSR);

		if ((group == null) || DSRUtil.isExpired()) {
			return false;
		}

		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, DSRConstants.DSR_HOME_FRIENDLY_URL);

		if ((layout == null) ||
			!LayoutPermissionUtil.contains(
				permissionChecker, layout, ActionKeys.VIEW)) {

			return false;
		}

		return PortalPermissionUtil.contains(
			permissionChecker, ActionKeys.VIEW_CONTROL_PANEL);
	}

	private static final String _KEY = "commerce.digital_sales_room_management";

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

}