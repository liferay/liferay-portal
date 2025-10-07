/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2025-06
 */

package com.liferay.object.internal.trash;

import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryFolderService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.trash.BaseTrashHandler;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectEntryFolder",
	service = TrashHandler.class
)
public class ObjectEntryFolderTrashHandler extends BaseTrashHandler {

	@Override
	public void deleteTrashEntry(long classPK) throws PortalException {
		_objectEntryFolderService.deleteObjectEntryFolder(classPK);
	}

	@Override
	public String getClassName() {
		return ObjectEntryFolder.class.getName();
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException {

		_objectEntryFolderService.restoreObjectEntryFolderFromTrash(
			_objectEntryFolderLocalService.fetchObjectEntryFolder(classPK),
			ServiceContextThreadLocal.getServiceContext());
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException {

		return _modelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.object.model.ObjectEntryFolder)"
	)
	private volatile ModelResourcePermission<ObjectEntryFolder>
		_modelResourcePermission;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryFolderService _objectEntryFolderService;

}