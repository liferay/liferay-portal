/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.portlet.configuration.icon;

import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.web.internal.portlet.action.ActionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN,
		"path=/message_boards/view_category"
	},
	service = PortletConfigurationIcon.class
)
public class CategoryPermissionsPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "permissions");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		String url = StringPool.BLANK;

		try {
			MBCategory category = ActionUtil.getCategory(portletRequest);

			String modelResource = MBCategory.class.getName();
			String modelResourceDescription = category.getName();
			String resourcePrimKey = String.valueOf(category.getCategoryId());

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			url = PermissionsURLTag.doTag(
				StringPool.BLANK, modelResource, modelResourceDescription, null,
				resourcePrimKey, LiferayWindowState.POP_UP.toString(), null,
				themeDisplay.getRequest());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return url;
	}

	@Override
	public double getWeight() {
		return 102;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		if (user.isGuestUser()) {
			return false;
		}

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		try {
			if (!_categoryModelResourcePermission.contains(
					permissionChecker, ActionUtil.getCategory(portletRequest),
					ActionKeys.PERMISSIONS)) {

				return false;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}

		return true;
	}

	@Override
	public boolean isUseDialog() {
		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CategoryPermissionsPortletConfigurationIcon.class);

	@Reference(
		target = "(model.class.name=com.liferay.message.boards.model.MBCategory)"
	)
	private ModelResourcePermission<MBCategory>
		_categoryModelResourcePermission;

	@Reference
	private Language _language;

}