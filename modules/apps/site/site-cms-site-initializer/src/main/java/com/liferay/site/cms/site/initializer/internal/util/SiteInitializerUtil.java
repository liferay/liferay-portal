/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.site.initializer.SiteInitializer;

import java.util.List;

/**
 * @author Jürgen Kappler
 */
public class SiteInitializerUtil {

	public static void initialize(
			long companyId, SiteInitializer siteInitializer)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-17564")) {
			return;
		}

		Group group = GroupLocalServiceUtil.getGroup(
			companyId, GroupConstants.CMS);

		String friendlyURL = FriendlyURLNormalizerUtil.normalizeWithEncoding(
			"/dashboard");

		Layout defaultLayout = LayoutLocalServiceUtil.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, friendlyURL);

		if (defaultLayout != null) {
			return;
		}

		defaultLayout = LayoutLocalServiceUtil.fetchFirstLayout(
			group.getGroupId(), false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			false);

		if (defaultLayout != null) {
			return;
		}

		String name = PrincipalThreadLocal.getName();

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			User user = _getUser(companyId);

			PrincipalThreadLocal.setName(user.getUserId());

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			ServiceContextThreadLocal.pushServiceContext(new ServiceContext());

			siteInitializer.initialize(group.getGroupId());
		}
		finally {
			PrincipalThreadLocal.setName(name);

			PermissionThreadLocal.setPermissionChecker(permissionChecker);

			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private static User _getUser(long companyId) throws PortalException {
		Role role = RoleLocalServiceUtil.fetchRole(
			companyId, RoleConstants.ADMINISTRATOR);

		if (role == null) {
			return UserLocalServiceUtil.getGuestUser(companyId);
		}

		List<User> adminUsers = UserLocalServiceUtil.getRoleUsers(
			role.getRoleId(), 0, 1);

		if (adminUsers.isEmpty()) {
			return UserLocalServiceUtil.getGuestUser(companyId);
		}

		return adminUsers.get(0);
	}

}