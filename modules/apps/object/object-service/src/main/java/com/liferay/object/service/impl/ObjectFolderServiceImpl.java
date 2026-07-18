/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectConstants;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.base.ObjectFolderServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Murilo Stodolni
 */
@Component(
	property = {
		"json.web.service.context.name=object",
		"json.web.service.context.path=ObjectFolder"
	},
	service = AopService.class
)
public class ObjectFolderServiceImpl extends ObjectFolderServiceBaseImpl {

	@Override
	public ObjectFolder addObjectFolder(
			String externalReferenceCode, Map<Locale, String> labelMap,
			String name)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null, ObjectActionKeys.ADD_OBJECT_FOLDER);

		return objectFolderLocalService.addObjectFolder(
			externalReferenceCode, getUserId(), labelMap, name);
	}

	@Override
	public ObjectFolder deleteObjectFolder(long objectFolderId)
		throws PortalException {

		_objectFolderModelResourcePermission.check(
			getPermissionChecker(), objectFolderId, ActionKeys.DELETE);

		return objectFolderLocalService.deleteObjectFolder(objectFolderId);
	}

	@Override
	public ObjectFolder getObjectFolder(long objectFolderId)
		throws PortalException {

		_objectFolderModelResourcePermission.check(
			getPermissionChecker(), objectFolderId, ActionKeys.VIEW);

		return objectFolderPersistence.findByPrimaryKey(objectFolderId);
	}

	@Override
	public ObjectFolder getObjectFolderByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		ObjectFolder objectFolder = objectFolderPersistence.findByERC_C(
			externalReferenceCode, companyId);

		_objectFolderModelResourcePermission.check(
			getPermissionChecker(), objectFolder, ActionKeys.VIEW);

		return objectFolder;
	}

	@Override
	public ObjectFolder updateObjectFolder(
			String externalReferenceCode, long objectFolderId,
			Map<Locale, String> labelMap)
		throws PortalException {

		_objectFolderModelResourcePermission.check(
			getPermissionChecker(), objectFolderId, ActionKeys.UPDATE);

		return objectFolderLocalService.updateObjectFolder(
			externalReferenceCode, objectFolderId, labelMap);
	}

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectFolder)"
	)
	private ModelResourcePermission<ObjectFolder>
		_objectFolderModelResourcePermission;

	@Reference(target = "(resource.name=" + ObjectConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

}