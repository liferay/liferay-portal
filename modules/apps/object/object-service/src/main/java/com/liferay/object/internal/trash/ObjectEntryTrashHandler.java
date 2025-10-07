/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2025-06
 */

package com.liferay.object.internal.trash;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.trash.BaseTrashHandler;

import jakarta.portlet.PortletRequest;

/**
 * @author Yuri Monteiro
 */
public class ObjectEntryTrashHandler extends BaseTrashHandler {

	public ObjectEntryTrashHandler(
		ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryService objectEntryService,
		SystemEventLocalService systemEventLocalService) {

		_objectDefinition = objectDefinition;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryService = objectEntryService;
		_systemEventLocalService = systemEventLocalService;
	}

	@Override
	public SystemEvent addDeletionSystemEvent(
			long userId, long groupId, long classPK, String classUuid,
			String referrerClassName)
		throws PortalException {

		ObjectEntry objectEntry = _objectEntryService.getObjectEntry(classPK);

		return _systemEventLocalService.addSystemEvent(
			userId, groupId, objectEntry.getExternalReferenceCode(),
			getSystemEventClassName(), classPK, classUuid, referrerClassName,
			SystemEventConstants.TYPE_DELETE, StringPool.BLANK);
	}

	@Override
	public void deleteTrashEntry(long classPK) throws PortalException {
		_objectEntryService.deleteObjectEntry(classPK);
	}

	@Override
	public String getClassName() {
		return _objectDefinition.getClassName();
	}

	@Override
	public String getRestoreMessage(PortletRequest portletRequest, long classPK)
		throws PortalException {

		return null;
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException {
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException {

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			_objectEntryService.getModelResourcePermission(
				_objectDefinition.getObjectDefinitionId());

		return modelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	private final ObjectDefinition _objectDefinition;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryService _objectEntryService;
	private final SystemEventLocalService _systemEventLocalService;

}