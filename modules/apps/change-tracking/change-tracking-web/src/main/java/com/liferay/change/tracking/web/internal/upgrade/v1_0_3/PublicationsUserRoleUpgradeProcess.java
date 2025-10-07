/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.upgrade.v1_0_3;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Samuel Trong Tran
 */
public class PublicationsUserRoleUpgradeProcess extends UpgradeProcess {

	public PublicationsUserRoleUpgradeProcess(
		CompanyLocalService companyLocalService,
		ResourceActions resourceActions,
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService, UserLocalService userLocalService) {

		_companyLocalService = companyLocalService;
		_resourceActions = resourceActions;
		_resourcePermissionLocalService = resourcePermissionLocalService;
		_roleLocalService = roleLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_resourceActions.populatePortletResources(
			PublicationsUserRoleUpgradeProcess.class.getClassLoader(),
			"resource-actions/default.xml");

		_companyLocalService.forEachCompanyId(
			companyId -> {
				Role role = _roleLocalService.fetchRole(
					companyId, RoleConstants.PUBLICATIONS_USER);

				if (role == null) {
					role = _roleLocalService.addRole(
						null, _userLocalService.getGuestUserId(companyId), null,
						0, RoleConstants.PUBLICATIONS_USER, null,
						HashMapBuilder.put(
							LocaleUtil.fromLanguageId(
								UpgradeProcessUtil.getDefaultLanguageId(
									companyId)),
							PropsUtil.get(
								StringBundler.concat(
									"system.role.",
									StringUtil.replace(
										RoleConstants.PUBLICATIONS_USER,
										CharPool.SPACE, CharPool.PERIOD),
									".description"))
						).build(),
						RoleConstants.TYPE_REGULAR, null, null);
				}

				_resourcePermissionLocalService.addResourcePermission(
					companyId, PortletKeys.PORTAL,
					ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
					role.getRoleId(), ActionKeys.VIEW_CONTROL_PANEL);
				_resourcePermissionLocalService.addResourcePermission(
					companyId, CTPortletKeys.PUBLICATIONS,
					ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
					role.getRoleId(), ActionKeys.ACCESS_IN_CONTROL_PANEL);
				_resourcePermissionLocalService.addResourcePermission(
					companyId, CTPortletKeys.PUBLICATIONS,
					ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
					role.getRoleId(), ActionKeys.VIEW);
			});
	}

	private final CompanyLocalService _companyLocalService;
	private final ResourceActions _resourceActions;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final RoleLocalService _roleLocalService;
	private final UserLocalService _userLocalService;

}