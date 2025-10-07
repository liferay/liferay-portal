/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.security.permission.resource;

import com.liferay.object.constants.ObjectConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.BasePortletResourcePermissionWrapper;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermissionLogic;

import org.osgi.service.component.annotations.Component;

/**
 * @author Feliphe Marinho
 * @author Mario Gomes
 */
@Component(
	property = "resource.name=" + ObjectConstants.RESOURCE_NAME_OBJECT_ENTRY_FOLDER,
	service = PortletResourcePermission.class
)
public class ObjectEntryFolderPortletResourcePermissionWrapper
	extends BasePortletResourcePermissionWrapper {

	@Override
	protected PortletResourcePermission doGetPortletResourcePermission() {
		return PortletResourcePermissionFactory.create(
			ObjectConstants.RESOURCE_NAME_OBJECT_ENTRY_FOLDER,
			new ObjectEntryFolderPortletResourcePermissionLogic());
	}

	private static class ObjectEntryFolderPortletResourcePermissionLogic
		implements PortletResourcePermissionLogic {

		@Override
		public Boolean contains(
			PermissionChecker permissionChecker, String name, Group group,
			String actionId) {

			return permissionChecker.hasPermission(group, name, 0, actionId);
		}

	}

}