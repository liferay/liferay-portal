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
public class ObjectEntry1to1ObjectRelatedModelsProviderImpl
	implements ObjectRelatedModelsProvider<ObjectEntry> {

	public ObjectEntry1to1ObjectRelatedModelsProviderImpl(
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
			groupId, objectRelationshipId, null, false, primaryKey, null, 0, 1,
			null);

		if (relatedModels.isEmpty()) {
			return;
		}

		ObjectEntry objectEntry = relatedModels.get(0);

		if (Objects.equals(
				deletionType,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE)) {

			_objectEntryService.deleteObjectEntry(
				objectEntry.getObjectEntryId());
		}
		else if (Objects.equals(
					deletionType,
					ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE)) {

			_objectEntryService.partialUpdateObjectEntry(
				objectEntry.getObjectEntryId(),
				objectEntry.getObjectEntryFolderId(),
				HashMapBuilder.<String, Serializable>put(
					() -> {
						ObjectField objectField =
							_objectFieldLocalService.getObjectField(
								objectRelationship.getObjectFieldId2());

						return objectField.getName();
					},
					0
				).build(),
				new ServiceContext());
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
			primaryKey1);

		_objectEntryService.updateObjectEntry(
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
	public String getClassName() {
		return _className;
	}

	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public String getObjectRelationshipType() {
		return ObjectRelationshipConstants.TYPE_ONE_TO_ONE;
	}

	@Override
	public List<ObjectEntry> getRelatedModels(
			long groupId, long objectRelationshipId, Predicate predicate,
			boolean preferApproved, long primaryKey, String search, int start,
			int end, Sort[] sorts)
		throws PortalException {

		return _objectEntryService.getOneToManyObjectEntries(
			groupId, objectRelationshipId, predicate, false, primaryKey, true,
			search, 0, 1, sorts);
	}

	@Override
	public int getRelatedModelsCount(
			long groupId, long objectRelationshipId, Predicate predicate,
			long primaryKey, String search)
		throws PortalException {

		List<ObjectEntry> relatedModels = getRelatedModels(
			groupId, objectRelationshipId, predicate, false, primaryKey, search,
			0, 1, null);

		return relatedModels.size();
	}

	private final String _className;
	private final long _companyId;
	private final ObjectEntryService _objectEntryService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;

}