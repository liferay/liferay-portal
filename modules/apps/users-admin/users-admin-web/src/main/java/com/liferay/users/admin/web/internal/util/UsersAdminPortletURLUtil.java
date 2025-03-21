/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.users.admin.constants.UserScreenNavigationEntryConstants;

import jakarta.portlet.RenderResponse;
import jakarta.portlet.RenderURL;

/**
 * @author Samuel Trong Tran
 */
public class UsersAdminPortletURLUtil {

	public static String createOrganizationViewTreeURL(
		long organizationId, RenderResponse renderResponse) {

		RenderURL renderURL = PortletURLBuilder.createRenderURL(
			renderResponse
		).setParameter(
			"screenNavigationCategoryKey",
			UserScreenNavigationEntryConstants.CATEGORY_KEY_ORGANIZATIONS
		).buildRenderURL();

		if (organizationId ==
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

			renderURL.setParameter("mvcRenderCommandName", "/users_admin/view");
			renderURL.setParameter(
				"usersListView", UserConstants.LIST_VIEW_FLAT_ORGANIZATIONS);
		}
		else {
			renderURL.setParameter(
				"mvcRenderCommandName", "/users_admin/organizations_view_tree");
			renderURL.setParameter(
				"organizationId", String.valueOf(organizationId));
			renderURL.setParameter(
				"usersListView", UserConstants.LIST_VIEW_TREE);
		}

		return String.valueOf(renderURL);
	}

	public static String createParentOrganizationViewTreeURL(
			long organizationId, RenderResponse renderResponse)
		throws PortalException {

		return createParentOrganizationViewTreeURL(
			OrganizationLocalServiceUtil.fetchOrganization(organizationId),
			renderResponse);
	}

	public static String createParentOrganizationViewTreeURL(
			Organization organization, RenderResponse renderResponse)
		throws PortalException {

		if ((organization != null) && !organization.isRoot()) {
			long parentOrganizationId = organization.getParentOrganizationId();

			if (OrganizationPermissionUtil.contains(
					PermissionThreadLocal.getPermissionChecker(),
					parentOrganizationId, ActionKeys.VIEW)) {

				return createOrganizationViewTreeURL(
					parentOrganizationId, renderResponse);
			}
		}

		return createOrganizationViewTreeURL(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			renderResponse);
	}

}