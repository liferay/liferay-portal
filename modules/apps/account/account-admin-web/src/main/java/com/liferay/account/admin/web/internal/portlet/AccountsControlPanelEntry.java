/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.BaseControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_USERS_ADMIN
	},
	service = ControlPanelEntry.class
)
public class AccountsControlPanelEntry extends BaseControlPanelEntry {

	@Override
	protected boolean hasPermissionImplicitlyGranted(
			PermissionChecker permissionChecker, Group group, Portlet portlet)
		throws Exception {

		List<Organization> organizations =
			_organizationLocalService.getUserOrganizations(
				permissionChecker.getUserId(), true);

		for (Organization organization : organizations) {
			if (OrganizationPermissionUtil.contains(
					permissionChecker, organization,
					AccountActionKeys.MANAGE_ACCOUNTS) &&
				permissionChecker.hasPermission(
					organization.getGroupId(), portlet.getPortletId(), 0,
					ActionKeys.ACCESS_IN_CONTROL_PANEL)) {

				return true;
			}
		}

		return super.hasPermissionImplicitlyGranted(
			permissionChecker, group, portlet);
	}

	@Reference
	private OrganizationLocalService _organizationLocalService;

}