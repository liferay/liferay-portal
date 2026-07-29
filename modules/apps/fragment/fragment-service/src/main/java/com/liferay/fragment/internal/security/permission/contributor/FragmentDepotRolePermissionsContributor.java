/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.security.permission.contributor;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.security.permission.contributor.DepotRolePermission;
import com.liferay.depot.security.permission.contributor.DepotRolePermissionsContributor;
import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentConstants;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Thiago Buarque
 */
@Component(service = DepotRolePermissionsContributor.class)
public class FragmentDepotRolePermissionsContributor
	implements DepotRolePermissionsContributor {

	@Override
	public List<DepotRolePermission> getDepotRolePermissions() {
		return List.of(
			new DepotRolePermission(
				DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR,
				FragmentConstants.RESOURCE_NAME,
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES),
			new DepotRolePermission(
				DepotRolesConstants.DESIGN_LIBRARY_CONTENT_REVIEWER,
				FragmentConstants.RESOURCE_NAME,
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES),
			new DepotRolePermission(
				DepotRolesConstants.DESIGN_LIBRARY_OWNER,
				FragmentConstants.RESOURCE_NAME,
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES));
	}

}