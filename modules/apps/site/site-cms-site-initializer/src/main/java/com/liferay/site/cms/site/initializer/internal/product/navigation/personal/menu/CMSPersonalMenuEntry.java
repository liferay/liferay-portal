/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.product.navigation.personal.menu;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.personal.menu.PersonalMenuEntry;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"product.navigation.personal.menu.entry.order:Integer=110",
		"product.navigation.personal.menu.group:Integer=100"
	},
	service = PersonalMenuEntry.class
)
public class CMSPersonalMenuEntry implements PersonalMenuEntry {

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "my-cms-homepage");
	}

	@Override
	public String getPortletURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = _groupLocalService.getGroup(
			themeDisplay.getCompanyId(), GroupConstants.CMS);

		return group.getDisplayURL(themeDisplay);
	}

	@Override
	public boolean isShow(
			PortletRequest portletRequest, PermissionChecker permissionChecker)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			return false;
		}

		Group group = _groupLocalService.fetchGroup(
			permissionChecker.getCompanyId(), GroupConstants.CMS);

		if (group == null) {
			return false;
		}

		if (permissionChecker.hasPermission(
				group, Group.class.getName(), group.getGroupId(),
				ActionKeys.VIEW)) {

			return true;
		}

		User user = permissionChecker.getUser();

		List<Role> roles = user.getRoles();

		Role role = _roleLocalService.fetchRole(
			permissionChecker.getCompanyId(), RoleConstants.CMS_ADMINISTRATOR);

		if ((role != null) && roles.contains(role)) {
			return true;
		}

		List<Group> groups = user.getGroups();

		for (Group curGroup : groups) {
			if (!curGroup.isDepot()) {
				continue;
			}

			DepotEntry depotEntry =
				_depotEntryLocalService.fetchGroupDepotEntry(
					curGroup.getGroupId());

			if (depotEntry == null) {
				continue;
			}

			if (depotEntry.getType() == DepotConstants.TYPE_SPACE) {
				return true;
			}
		}

		return false;
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private RoleLocalService _roleLocalService;

}