/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.base.ObjectEntryFolderServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"json.web.service.context.name=object",
		"json.web.service.context.path=ObjectEntryFolder"
	},
	service = AopService.class
)
public class ObjectEntryFolderServiceImpl
	extends ObjectEntryFolderServiceBaseImpl {

	@Override
	public ObjectEntryFolder addObjectEntryFolder(
			String externalReferenceCode, long groupId,
			long parentObjectEntryFolderId, String description,
			Map<Locale, String> labelMap, String name,
			ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_modelResourcePermission, getPermissionChecker(), groupId,
			parentObjectEntryFolderId,
			ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER);

		return objectEntryFolderLocalService.addObjectEntryFolder(
			externalReferenceCode, groupId, getUserId(),
			parentObjectEntryFolderId, description, labelMap, name,
			serviceContext);
	}

	@Override
	public ObjectEntryFolder deleteObjectEntryFolder(long objectEntryFolderId)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), objectEntryFolderId, ActionKeys.DELETE);

		return objectEntryFolderLocalService.deleteObjectEntryFolder(
			objectEntryFolderId);
	}

	@Override
	public ObjectEntryFolder deleteObjectEntryFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId, long companyId)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder =
			objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					externalReferenceCode, groupId, companyId);

		ModelResourcePermissionUtil.check(
			_modelResourcePermission, getPermissionChecker(), groupId,
			objectEntryFolder.getObjectEntryFolderId(), ActionKeys.DELETE);

		return objectEntryFolderLocalService.deleteObjectEntryFolder(
			objectEntryFolder);
	}

	@Override
	public ObjectEntryFolder fetchObjectEntryFolder(long objectEntryFolderId)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder =
			objectEntryFolderLocalService.fetchObjectEntryFolder(
				objectEntryFolderId);

		if (objectEntryFolder != null) {
			_modelResourcePermission.check(
				getPermissionChecker(), objectEntryFolder, ActionKeys.VIEW);
		}

		return objectEntryFolder;
	}

	@Override
	public ObjectEntryFolder fetchObjectEntryFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId, long companyId)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder =
			objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					externalReferenceCode, groupId, companyId);

		if (objectEntryFolder == null) {
			return null;
		}

		ModelResourcePermissionUtil.check(
			_modelResourcePermission, getPermissionChecker(), groupId,
			objectEntryFolder.getObjectEntryFolderId(), ActionKeys.VIEW);

		return objectEntryFolder;
	}

	@Override
	public ObjectEntryFolder getObjectEntryFolder(long objectEntryFolderId)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder =
			objectEntryFolderLocalService.getObjectEntryFolder(
				objectEntryFolderId);

		_modelResourcePermission.check(
			getPermissionChecker(), objectEntryFolder, ActionKeys.VIEW);

		return objectEntryFolder;
	}

	@Override
	public ObjectEntryFolder getObjectEntryFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId, long companyId)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder =
			objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					externalReferenceCode, groupId, companyId);

		ModelResourcePermissionUtil.check(
			_modelResourcePermission, getPermissionChecker(), groupId,
			objectEntryFolder.getObjectEntryFolderId(), ActionKeys.VIEW);

		return objectEntryFolder;
	}

	@Override
	public List<ObjectEntryFolder> getObjectEntryFolders(
			long groupId, long companyId, long parentObjectEntryFolderId,
			int start, int end)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_modelResourcePermission, getPermissionChecker(), groupId,
			parentObjectEntryFolderId, ActionKeys.VIEW);

		return objectEntryFolderLocalService.getObjectEntryFolders(
			groupId, companyId, parentObjectEntryFolderId, start, end);
	}

	@Override
	public int getObjectEntryFoldersCount(
			long groupId, long companyId, long parentObjectEntryFolderId)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_modelResourcePermission, getPermissionChecker(), groupId,
			parentObjectEntryFolderId, ActionKeys.VIEW);

		return objectEntryFolderLocalService.getObjectEntryFoldersCount(
			groupId, companyId, parentObjectEntryFolderId);
	}

	@Override
	public ObjectEntryFolder getOrAddEmptyObjectEntryFolder(
			String externalReferenceCode, long groupId, long companyId,
			ServiceContext serviceContext)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder =
			fetchObjectEntryFolderByExternalReferenceCode(
				externalReferenceCode, groupId, companyId);

		if (objectEntryFolder != null) {
			return objectEntryFolder;
		}

		ModelResourcePermissionUtil.check(
			_modelResourcePermission, getPermissionChecker(), groupId,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER);

		return objectEntryFolderLocalService.getOrAddEmptyObjectEntryFolder(
			externalReferenceCode, groupId, companyId, getUserId(),
			serviceContext);
	}

	@Override
	public ObjectEntryFolder moveObjectEntryFolderToTrash(
			ObjectEntryFolder objectEntryFolder, ServiceContext serviceContext)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), objectEntryFolder.getObjectEntryFolderId(),
			ActionKeys.DELETE);

		return objectEntryFolderLocalService.moveObjectEntryFolderToTrash(
			getUserId(), objectEntryFolder, serviceContext);
	}

	@Override
	public ObjectEntryFolder restoreObjectEntryFolderFromTrash(
			ObjectEntryFolder objectEntryFolder, ServiceContext serviceContext)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), objectEntryFolder.getObjectEntryFolderId(),
			ActionKeys.DELETE);

		return objectEntryFolderLocalService.restoreObjectEntryFolderFromTrash(
			getUserId(), objectEntryFolder, serviceContext);
	}

	@Override
	public void subscribeObjectEntryFolder(
			long groupId, long objectEntryFolderId)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_modelResourcePermission, getPermissionChecker(), groupId,
			objectEntryFolderId, ActionKeys.SUBSCRIBE);

		objectEntryFolderLocalService.subscribeObjectEntryFolder(
			getUserId(), groupId, objectEntryFolderId);
	}

	@Override
	public void unsubscribeObjectEntryFolder(
			long groupId, long objectEntryFolderId)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_modelResourcePermission, getPermissionChecker(), groupId,
			objectEntryFolderId, ActionKeys.SUBSCRIBE);

		objectEntryFolderLocalService.unsubscribeObjectEntryFolder(
			getUserId(), groupId, objectEntryFolderId);
	}

	@Override
	public ObjectEntryFolder updateObjectEntryFolder(
			long objectEntryFolderId, long parentObjectEntryFolderId,
			String description, Map<Locale, String> labelMap, String name,
			ServiceContext serviceContext)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), objectEntryFolderId, ActionKeys.UPDATE);

		return objectEntryFolderLocalService.updateObjectEntryFolder(
			getUserId(), objectEntryFolderId, parentObjectEntryFolderId,
			description, labelMap, name, serviceContext);
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.object.model.ObjectEntryFolder)"
	)
	private volatile ModelResourcePermission<ObjectEntryFolder>
		_modelResourcePermission;

}