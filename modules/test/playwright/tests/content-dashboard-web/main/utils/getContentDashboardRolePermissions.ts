/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {RolePermission} from '../../../../utils/createUserWithPermissions';

export default function getContentDashboardRolePermissions(
	companyId: string
): RolePermission[] {
	return [
		{
			actionIds: ['ACCESS_IN_CONTROL_PANEL'],
			primaryKey: companyId,
			resourceName:
				'com_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet',
			scope: 1,
		},
		{
			actionIds: ['VIEW_CONTROL_PANEL'],
			primaryKey: companyId,
			resourceName: '90',
			scope: 1,
		},
		{
			actionIds: ['VIEW_SITE_ADMINISTRATION'],
			primaryKey: companyId,
			resourceName: 'com.liferay.portal.kernel.model.Group',
			scope: 1,
		},
	];
}
