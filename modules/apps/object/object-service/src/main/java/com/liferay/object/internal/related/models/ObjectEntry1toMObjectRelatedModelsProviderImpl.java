/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.related.models;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.exception.RequiredObjectRelationshipException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.io.Serializable;

import java.util.List;
import java.util.Objects;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class ObjectEntry1toMObjectRelatedModelsProviderImpl
	implements ObjectRelatedModelsProvider<ObjectEntry> {

	public ObjectEntry1toMObjectRelatedModelsProviderImpl(
		ObjectDefinition objectDefinition,
		ObjectEntryService objectEntryService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService) {

		_objectEntryService = objectEntryService;
		_objectFieldLocalService = objectFieldLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;

		_className = objectDefinition.getClassName();
		_companyId = objectDefinition.getCompanyId();
	}

	@Override
	public void deleteRelatedModel(
			long userId, long groupId, long objectRelationshipId,
			long primaryKey, String deletionType)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		List<ObjectEntry> relatedModels = getRelatedModels(
			groupId, objectRelationshipId, null, false, primaryKey, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		if (relatedModels.isEmpty()) {
			return;
		}

		if (Objects.equals(
				deletionType,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE)) {

			for (ObjectEntry objectEntry : relatedModels) {
				_objectEntryService.deleteObjectEntry(
					objectEntry.getObjectEntryId());
			}
		}
		else if (Objects.equals(
					deletionType,
					ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE)) {

			ObjectField objectField = _objectFieldLocalService.getObjectField(
				objectRelationship.getObjectFieldId2());

			for (ObjectEntry objectEntry : relatedModels) {
				_objectEntryService.partialUpdateObjectEntry(
					objectEntry.getObjectEntryId(),
					objectEntry.getObjectEntryFolderId(),
					HashMapBuilder.<String, Serializable>put(
						objectField.getName(), 0
					).build(),
					new ServiceContext());
			}
		}
		else if (Objects.equals(
					deletionType,
					ObjectRelationshipConstants.DELETION_TYPE_PREVENT)) {

			throw new RequiredObjectRelationshipException(objectRelationship);
		}
	}

	@Override
	public void disassociateRelatedModels(
			long userId, long objectRelationshipId, long primaryKey1,
			long primaryKey2)
		throws PortalException {

		ObjectEntry objectEntry = _objectEntryService.getObjectEntry(
			primaryKey2);

		_objectEntryService.partialUpdateObjectEntry(
			objectEntry.getPrimaryKey(), objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				() -> {
					ObjectRelationship objectRelationship =
						_objectRelationshipLocalService.getObjectRelationship(
							objectRelationshipId);

					ObjectField objectField =
						_objectFieldLocalService.getObjectField(
							objectRelationship.getObjectFieldId2());

					return objectField.getName();
				},
				0
			).build(),
			new ServiceContext());
	}

	@Override
	public ObjectEntry fetchRelatedModel(
			long groupId, long objectRelationshipId, long primaryKey)
		throws PortalException {

		return _objectEntryService.fetchManyToOneObjectEntry(
			groupId, objectRelationshipId, primaryKey);
	}

	@Override
	public String getClassName() {
		return _className;
	}

	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public String getObjectRelationshipType() {
		return ObjectRelationshipConstants.TYPE_ONE_TO_MANY;
	}

	@Override
	public List<ObjectEntry> getRelatedModels(
			long groupId, long objectRelationshipId, Predicate predicate,
			boolean preferApproved, long primaryKey, String search, int start,
			int end, Sort[] sorts)
		throws PortalException {

		return _objectEntryService.getOneToManyObjectEntries(
			groupId, objectRelationshipId, predicate, preferApproved,
			primaryKey, true, search, start, end, sorts);
	}

	@Override
	public int getRelatedModelsCount(
			long groupId, long objectRelationshipId, Predicate predicate,
			long primaryKey, String search)
		throws PortalException {

		return _objectEntryService.getOneToManyObjectEntriesCount(
			groupId, objectRelationshipId, predicate, primaryKey, true, search);
	}

	@Override
	public List<ObjectEntry> getUnrelatedModels(
			long companyId, long groupId, ObjectDefinition objectDefinition,
			long objectEntryId, long objectRelationshipId, String search,
			int start, int end)
		throws PortalException {

		return _objectEntryService.getOneToManyObjectEntries(
			groupId, objectRelationshipId, null, false, objectEntryId, false,
			search, start, end, null);
	}

	@Override
	public int getUnrelatedModelsCount(
			long companyId, long groupId, ObjectDefinition objectDefinition,
			long objectEntryId, long objectRelationshipId, String search)
		throws PortalException {

		return _objectEntryService.getOneToManyObjectEntriesCount(
			groupId, objectRelationshipId, null, objectEntryId, false, search);
	}

	@Override
	public void moveRelatedModelToTrash(
			long userId, long groupId, long objectRelationshipId,
			long primaryKey, String deletionType)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		List<ObjectEntry> relatedModels = getRelatedModels(
			groupId, objectRelationshipId, null, false, primaryKey, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		if (relatedModels.isEmpty()) {
			return;
		}

		if (Objects.equals(
				deletionType,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE)) {

			for (ObjectEntry objectEntry : relatedModels) {
				_objectEntryService.moveObjectEntryToTrash(
					objectEntry, new ServiceContext());
			}
		}
		else if (Objects.equals(
					deletionType,
					ObjectRelationshipConstants.DELETION_TYPE_PREVENT)) {

			throw new RequiredObjectRelationshipException(objectRelationship);
		}
	}

	@Override
	public void restoreRelatedModelsFromTrash(
			long userId, long groupId, long objectRelationshipId,
			long primaryKey, String deletionType)
		throws PortalException {

		if (!Objects.equals(
				deletionType,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE)) {

			return;
		}

		for (ObjectEntry objectEntry :
				getRelatedModels(
					groupId, objectRelationshipId, null, false, primaryKey,
					null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			if (!objectEntry.isInTrash()) {
				continue;
			}

			_objectEntryService.restoreObjectEntryFromTrash(
				objectEntry, new ServiceContext());
		}
	}

	private final String _className;
	private final long _companyId;
	private final ObjectEntryService _objectEntryService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;

}