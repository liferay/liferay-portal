/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.security.permission.contributor;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.security.permission.contributor.DepotRolePermission;
import com.liferay.depot.security.permission.contributor.DepotRolePermissionsContributor;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Georgel Pop
 */
@Component(service = DepotRolePermissionsContributor.class)
public class LayoutPageTemplateDepotRolePermissionsContributor
	implements DepotRolePermissionsContributor {

	@Override
	public List<DepotRolePermission> getDepotRolePermissions() {
		List<DepotRolePermission> depotRolePermissions = new ArrayList<>();

		_addDepotRolePermissions(
			depotRolePermissions,
			DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR);
		_addDepotRolePermissions(
			depotRolePermissions,
			DepotRolesConstants.DESIGN_LIBRARY_CONTENT_REVIEWER);
		_addDepotRolePermissions(
			depotRolePermissions, DepotRolesConstants.DESIGN_LIBRARY_OWNER);

		return depotRolePermissions;
	}

	private void _addDepotRolePermissions(
		List<DepotRolePermission> depotRolePermissions, String roleName) {

		depotRolePermissions.add(
			new DepotRolePermission(
				roleName, Layout.class.getName(), ActionKeys.UPDATE));
		depotRolePermissions.add(
			new DepotRolePermission(
				roleName, LayoutPageTemplateCollection.class.getName(),
				ActionKeys.DELETE, ActionKeys.UPDATE));
		depotRolePermissions.add(
			new DepotRolePermission(
				roleName, LayoutPageTemplateEntry.class.getName(),
				ActionKeys.DELETE, ActionKeys.UPDATE));
		depotRolePermissions.add(
			new DepotRolePermission(
				roleName, LayoutPageTemplateConstants.RESOURCE_NAME,
				LayoutPageTemplateActionKeys.
					ADD_LAYOUT_PAGE_TEMPLATE_COLLECTION,
				LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY));
	}

}