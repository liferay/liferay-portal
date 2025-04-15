/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.configuration.icon;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.documentlibrary.constants.DLConstants;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
	service = PortletConfigurationIcon.class
)
public class PermissionsPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getIconCssClass() {
		return "password-policies";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(
			getLocale(portletRequest), "home-folder-permissions");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return PermissionsURLTag.doTag(
				StringPool.BLANK, "com.liferay.document.library",
				themeDisplay.getScopeGroupName(), null,
				String.valueOf(themeDisplay.getScopeGroupId()),
				LiferayWindowState.POP_UP.toString(), null,
				themeDisplay.getRequest());
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	@Override
	public double getWeight() {
		return 130;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		String navigation = ParamUtil.getString(portletRequest, "navigation");

		if (Validator.isNotNull(navigation)) {
			return false;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		if (user.isGuestUser()) {
			return false;
		}

		return _portletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), themeDisplay.getScopeGroup(),
			ActionKeys.PERMISSIONS);
	}

	@Override
	public boolean isUseDialog() {
		return true;
	}

	@Reference
	private Language _language;

	@Reference(target = "(resource.name=" + DLConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

}