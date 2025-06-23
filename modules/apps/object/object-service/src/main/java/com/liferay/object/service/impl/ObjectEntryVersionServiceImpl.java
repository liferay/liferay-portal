/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.base.ObjectEntryVersionServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(
	property = {
		"json.web.service.context.name=object",
		"json.web.service.context.path=ObjectEntryVersion"
	},
	service = AopService.class
)
public class ObjectEntryVersionServiceImpl
	extends ObjectEntryVersionServiceBaseImpl {

	@Override
	public ObjectEntryVersion deleteObjectEntryVersion(
			long objectEntryId, int version)
		throws PortalException {

		_checkModelResourcePermission(objectEntryId, ActionKeys.DELETE);

		return objectEntryVersionLocalService.deleteObjectEntryVersion(
			objectEntryId, version);
	}

	@Override
	public ObjectEntryVersion expireObjectEntryVersion(
			ObjectEntry objectEntry, ServiceContext serviceContext, long userId,
			int version)
		throws PortalException {

		_objectEntryService.checkModelResourcePermission(
			objectEntry.getObjectDefinitionId(), objectEntry.getObjectEntryId(),
			ActionKeys.UPDATE);

		return objectEntryVersionLocalService.expireObjectEntryVersion(
			userId, objectEntry, version, serviceContext);
	}

	@Override
	public ObjectEntryVersion getObjectEntryVersion(
			long objectEntryId, int version)
		throws PortalException {

		_checkModelResourcePermission(objectEntryId, ActionKeys.UPDATE);

		return objectEntryVersionLocalService.getObjectEntryVersion(
			objectEntryId, version);
	}

	@Override
	public List<ObjectEntryVersion> getObjectEntryVersions(
			long objectEntryId, int start, int end)
		throws PortalException {

		_checkModelResourcePermission(objectEntryId, ActionKeys.UPDATE);

		return objectEntryVersionLocalService.getObjectEntryVersions(
			objectEntryId, start, end);
	}

	@Override
	public int getObjectEntryVersionsCount(long objectEntryId)
		throws PortalException {

		_checkModelResourcePermission(objectEntryId, ActionKeys.UPDATE);

		return objectEntryVersionLocalService.getObjectEntryVersionsCount(
			objectEntryId);
	}

	private void _checkModelResourcePermission(
			long objectEntryId, String actionId)
		throws PortalException {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		_objectEntryService.checkModelResourcePermission(
			objectEntry.getObjectDefinitionId(), objectEntryId, actionId);
	}

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

}