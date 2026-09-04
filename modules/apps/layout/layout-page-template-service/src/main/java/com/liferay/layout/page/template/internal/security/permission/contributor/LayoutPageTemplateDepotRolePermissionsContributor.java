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
		return List.of(
			new DepotRolePermission(
				DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR,
				LayoutPageTemplateConstants.RESOURCE_NAME,
				LayoutPageTemplateActionKeys.
					ADD_LAYOUT_PAGE_TEMPLATE_COLLECTION,
				LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY),
			new DepotRolePermission(
				DepotRolesConstants.DESIGN_LIBRARY_CONTENT_REVIEWER,
				LayoutPageTemplateConstants.RESOURCE_NAME,
				LayoutPageTemplateActionKeys.
					ADD_LAYOUT_PAGE_TEMPLATE_COLLECTION,
				LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY),
			new DepotRolePermission(
				DepotRolesConstants.DESIGN_LIBRARY_OWNER,
				LayoutPageTemplateConstants.RESOURCE_NAME,
				LayoutPageTemplateActionKeys.
					ADD_LAYOUT_PAGE_TEMPLATE_COLLECTION,
				LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY));
	}

}