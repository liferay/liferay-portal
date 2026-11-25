/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.util;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.site.initializer.SiteInitializer;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class SiteInitializerUtil {

	public static void initialize(
			Group group, SiteInitializer siteInitializer, String version)
		throws Exception {

		String name = PrincipalThreadLocal.getName();
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		User user = _getUser(group.getCompanyId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());

		ServiceContextThreadLocal.pushServiceContext(new ServiceContext());

		try {
			siteInitializer.initialize(group.getGroupId());

			group = GroupLocalServiceUtil.getGroup(group.getGroupId());

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			typeSettingsUnicodeProperties.setProperty(
				"siteInitializerKey", siteInitializer.getKey());
			typeSettingsUnicodeProperties.setProperty(
				"siteInitializerVersion", version);

			GroupLocalServiceUtil.updateGroup(
				group.getGroupId(), typeSettingsUnicodeProperties.toString());
		}
		finally {
			PrincipalThreadLocal.setName(name);

			PermissionThreadLocal.setPermissionChecker(permissionChecker);

			ServiceContextThreadLocal.popServiceContext();
		}
	}

	public static boolean isInitialized(
		Group group, SiteInitializer siteInitializer, String version) {

		if (version.startsWith("0.")) {
			return false;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		if (Objects.equals(
				typeSettingsUnicodeProperties.getProperty("siteInitializerKey"),
				siteInitializer.getKey()) &&
			Objects.equals(
				typeSettingsUnicodeProperties.getProperty(
					"siteInitializerVersion"),
				version)) {

			return true;
		}

		return false;
	}

	private static User _getUser(long companyId) throws Exception {
		Role role = RoleLocalServiceUtil.fetchRole(
			companyId, RoleConstants.ADMINISTRATOR);

		if (role == null) {
			return UserLocalServiceUtil.getGuestUser(companyId);
		}

		List<User> users = UserLocalServiceUtil.getRoleUsers(
			role.getRoleId(), 0, 1);

		if (users.isEmpty()) {
			return UserLocalServiceUtil.getGuestUser(companyId);
		}

		return users.get(0);
	}

}