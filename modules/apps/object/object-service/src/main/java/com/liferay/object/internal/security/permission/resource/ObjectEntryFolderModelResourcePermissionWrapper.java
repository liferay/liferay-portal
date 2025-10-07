/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.security.permission.resource;

import com.liferay.object.constants.ObjectConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.kernel.security.permission.resource.BaseModelResourcePermissionWrapper;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.sharing.security.permission.resource.SharingModelResourcePermissionConfigurator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectEntryFolder",
	service = ModelResourcePermission.class
)
public class ObjectEntryFolderModelResourcePermissionWrapper
	extends BaseModelResourcePermissionWrapper<ObjectEntryFolder> {

	@Override
	protected ModelResourcePermission<ObjectEntryFolder>
		doGetModelResourcePermission() {

		return ModelResourcePermissionFactory.create(
			ObjectEntryFolder.class, ObjectEntryFolder::getObjectEntryFolderId,
			_objectEntryFolderLocalService::getObjectEntryFolder,
			_portletResourcePermission,
			_sharingModelResourcePermissionConfigurator::configure);
	}

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference(
		target = "(resource.name=" + ObjectConstants.RESOURCE_NAME_OBJECT_ENTRY_FOLDER + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private SharingModelResourcePermissionConfigurator
		_sharingModelResourcePermissionConfigurator;

}