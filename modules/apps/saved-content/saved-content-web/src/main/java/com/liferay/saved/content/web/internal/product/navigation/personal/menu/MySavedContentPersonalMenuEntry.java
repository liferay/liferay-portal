/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saved.content.web.internal.product.navigation.personal.menu;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.personal.menu.BasePersonalMenuEntry;
import com.liferay.product.navigation.personal.menu.PersonalMenuEntry;
import com.liferay.saved.content.constants.MySavedContentPortletKeys;
import com.liferay.saved.content.security.permission.SavedContentPermission;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(
	property = {
		"product.navigation.personal.menu.entry.order:Integer=500",
		"product.navigation.personal.menu.group:Integer=300"
	},
	service = PersonalMenuEntry.class
)
public class MySavedContentPersonalMenuEntry extends BasePersonalMenuEntry {

	@Override
	public String getPortletId() {
		return MySavedContentPortletKeys.MY_SAVED_CONTENT;
	}

	@Override
	public boolean isActive(PortletRequest portletRequest, String portletId) {
		if (!FeatureFlagManagerUtil.isEnabled("LPS-197909")) {
			return false;
		}

		return super.isActive(portletRequest, portletId);
	}

	@Override
	public boolean isShow(
			PortletRequest portletRequest, PermissionChecker permissionChecker)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled("LPS-197909")) {
			return false;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (_savedContentPermission.contains(
				permissionChecker, themeDisplay.getScopeGroupId(),
				ActionKeys.ADD_ENTRY)) {

			return true;
		}

		return super.isShow(portletRequest, permissionChecker);
	}

	@Reference
	private SavedContentPermission _savedContentPermission;

}