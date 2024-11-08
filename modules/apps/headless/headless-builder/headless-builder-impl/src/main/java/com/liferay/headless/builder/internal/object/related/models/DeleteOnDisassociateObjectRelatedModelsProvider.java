/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.internal.object.related.models;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;

/**
 * @author Carlos Correa
 */
public class DeleteOnDisassociateObjectRelatedModelsProvider
	implements ObjectRelatedModelsProvider<ObjectEntry> {

	public DeleteOnDisassociateObjectRelatedModelsProvider(
		ObjectEntryLocalService objectEntryLocalService,
		ObjectRelatedModelsProvider<ObjectEntry> objectRelatedModelsProvider,
		ObjectRelationshipLocalService objectRelationshipLocalService) {

		_objectEntryLocalService = objectEntryLocalService;
		_objectRelatedModelsProvider = objectRelatedModelsProvider;
		_objectRelationshipLocalService = objectRelationshipLocalService;
	}

	@Override
	public void deleteRelatedModel(
			long userId, long groupId, long objectRelationshipId,
			long primaryKey, String deletionType)
		throws PortalException {

		_objectRelatedModelsProvider.deleteRelatedModel(
			userId, groupId, objectRelationshipId, primaryKey, deletionType);
	}

	@Override
	public void disassociateRelatedModels(
			long userId, long objectRelationshipId, long primaryKey1,
			long primaryKey2)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		if (StringUtil.equals(
				objectRelationship.getName(), "apiSchemaToAPIProperties")) {

			_objectEntryLocalService.deleteObjectEntry(primaryKey2);
		}
		else {
			_objectRelatedModelsProvider.disassociateRelatedModels(
				userId, objectRelationshipId, primaryKey1, primaryKey2);
		}
	}

	@Override
	public ObjectEntry fetchRelatedModel(
			long groupId, long objectRelationshipId, long primaryKey)
		throws PortalException {

		return _objectRelatedModelsProvider.fetchRelatedModel(
			groupId, objectRelationshipId, primaryKey);
	}

	@Override
	public String getClassName() {
		return _objectRelatedModelsProvider.getClassName();
	}

	@Override
	public long getCompanyId() {
		return _objectRelatedModelsProvider.getCompanyId();
	}

	@Override
	public String getObjectRelationshipType() {
		return _objectRelatedModelsProvider.getObjectRelationshipType();
	}

	@Override
	public List<ObjectEntry> getRelatedModels(
			long groupId, long objectRelationshipId, long primaryKey,
			String search, int start, int end)
		throws PortalException {

		return _objectRelatedModelsProvider.getRelatedModels(
			groupId, objectRelationshipId, primaryKey, search, start, end);
	}

	@Override
	public int getRelatedModelsCount(
			long groupId, long objectRelationshipId, long primaryKey,
			String search)
		throws PortalException {

		return _objectRelatedModelsProvider.getRelatedModelsCount(
			groupId, objectRelationshipId, primaryKey, search);
	}

	@Override
	public List<ObjectEntry> getUnrelatedModels(
			long companyId, long groupId, ObjectDefinition objectDefinition,
			long objectEntryId, long objectRelationshipId, String search,
			int start, int end)
		throws PortalException {

		return _objectRelatedModelsProvider.getUnrelatedModels(
			companyId, groupId, objectDefinition, objectEntryId,
			objectRelationshipId, search, start, end);
	}

	@Override
	public int getUnrelatedModelsCount(
			long companyId, long groupId, ObjectDefinition objectDefinition,
			long objectEntryId, long objectRelationshipId, String search)
		throws PortalException {

		return _objectRelatedModelsProvider.getUnrelatedModelsCount(
			companyId, groupId, objectDefinition, objectEntryId,
			objectRelationshipId, search);
	}

	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectRelatedModelsProvider<ObjectEntry>
		_objectRelatedModelsProvider;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;

}