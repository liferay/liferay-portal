/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.role.type.contributor.util;

import com.liferay.roles.admin.constants.RolesAdminWebKeys;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Drew Brokke
 */
public class RoleTypeContributorRetrieverUtil {

	public static RoleTypeContributor getCurrentRoleTypeContributor(
		HttpServletRequest httpServletRequest) {

		return (RoleTypeContributor)httpServletRequest.getAttribute(
			RolesAdminWebKeys.CURRENT_ROLE_TYPE);
	}

	public static RoleTypeContributor getCurrentRoleTypeContributor(
		PortletRequest portletRequest) {

		return (RoleTypeContributor)portletRequest.getAttribute(
			RolesAdminWebKeys.CURRENT_ROLE_TYPE);
	}

	public static List<RoleTypeContributor> getRoleTypeContributors(
		HttpServletRequest httpServletRequest) {

		return (List<RoleTypeContributor>)httpServletRequest.getAttribute(
			RolesAdminWebKeys.ROLE_TYPES);
	}

	public static List<RoleTypeContributor> getRoleTypeContributors(
		PortletRequest portletRequest) {

		return (List<RoleTypeContributor>)portletRequest.getAttribute(
			RolesAdminWebKeys.ROLE_TYPES);
	}

}