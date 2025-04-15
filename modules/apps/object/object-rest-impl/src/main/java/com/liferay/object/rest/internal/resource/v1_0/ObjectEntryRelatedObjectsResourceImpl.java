/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.Context;

import java.util.Map;

/**
 * @author Carlos Correa
 */
public class ObjectEntryRelatedObjectsResourceImpl
	extends BaseObjectEntryRelatedObjectsResourceImpl {

	public ObjectEntryRelatedObjectsResourceImpl(
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryManagerRegistry objectEntryManagerRegistry,
		ObjectRelatedModelsProviderRegistry objectRelatedModelsProviderRegistry,
		ObjectRelationshipLocalService objectRelationshipLocalService) {

		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryManagerRegistry = objectEntryManagerRegistry;
		_objectRelatedModelsProviderRegistry =
			objectRelatedModelsProviderRegistry;
		_objectRelationshipLocalService = objectRelationshipLocalService;
	}

	@Override
	public void deleteCurrentObjectEntry(
			Long currentObjectEntryId, String objectRelationshipName,
			Long relatedObjectEntryId)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		_checkCurrentObjectEntry(
			defaultObjectEntryManager, currentObjectEntryId);

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				_objectDefinition.getObjectDefinitionId(),
				objectRelationshipName);

		ObjectDefinition relatedObjectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2());

		if (relatedObjectDefinition.isUnmodifiableSystemObject()) {
			_checkSystemObjectEntry(
				relatedObjectEntryId, relatedObjectDefinition);
		}
		else {
			_checkRelatedObjectEntry(
				defaultObjectEntryManager, objectRelationshipName,
				relatedObjectEntryId);
		}

		ObjectRelatedModelsProvider objectRelatedModelsProvider =
			_objectRelatedModelsProviderRegistry.getObjectRelatedModelsProvider(
				relatedObjectDefinition.getClassName(),
				relatedObjectDefinition.getCompanyId(),
				objectRelationship.getType());

		objectRelatedModelsProvider.disassociateRelatedModels(
			contextUser.getUserId(),
			objectRelationship.getObjectRelationshipId(), currentObjectEntryId,
			relatedObjectEntryId);
	}

	@Override
	public Page<Object> getCurrentObjectEntriesObjectRelationshipNamePage(
			Long currentObjectEntryId, String objectRelationshipName,
			Pagination pagination)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				_objectDefinition.getObjectDefinitionId(),
				objectRelationshipName);

		ObjectDefinition relatedObjectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2());

		if (relatedObjectDefinition.isUnmodifiableSystemObject()) {
			return defaultObjectEntryManager.getRelatedSystemObjectEntries(
				_objectDefinition, currentObjectEntryId, objectRelationshipName,
				pagination);
		}

		Page<ObjectEntry> page =
			defaultObjectEntryManager.getObjectEntryRelatedObjectEntries(
				_getDTOConverterContext(currentObjectEntryId),
				_objectDefinition, currentObjectEntryId, objectRelationshipName,
				pagination);

		return Page.of(
			page.getActions(),
			transform(
				page.getItems(),
				objectEntry -> _getRelatedObjectEntry(
					relatedObjectDefinition, objectEntry)),
			pagination, page.getTotalCount());
	}

	@Override
	public Object putCurrentObjectEntry(
			Long currentObjectEntryId, String objectRelationshipName,
			Long relatedObjectEntryId)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				_objectDefinition.getObjectDefinitionId(),
				objectRelationshipName);

		ObjectDefinition relatedObjectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2());

		if (relatedObjectDefinition.isUnmodifiableSystemObject()) {
			return defaultObjectEntryManager.
				addSystemObjectRelationshipMappingTableValues(
					relatedObjectDefinition, objectRelationship,
					currentObjectEntryId, relatedObjectEntryId);
		}

		return _getRelatedObjectEntry(
			relatedObjectDefinition,
			defaultObjectEntryManager.addObjectRelationshipMappingTableValues(
				_getDTOConverterContext(currentObjectEntryId),
				objectRelationship, currentObjectEntryId,
				relatedObjectEntryId));
	}

	private void _checkCurrentObjectEntry(
			DefaultObjectEntryManager defaultObjectEntryManager,
			long relatedObjectEntryId)
		throws Exception {

		defaultObjectEntryManager.getObjectEntry(
			_getDTOConverterContext(relatedObjectEntryId), _objectDefinition,
			relatedObjectEntryId);
	}

	private void _checkRelatedObjectEntry(
			DefaultObjectEntryManager defaultObjectEntryManager,
			String objectRelationshipName, long relatedObjectEntryId)
		throws Exception {

		defaultObjectEntryManager.getObjectEntry(
			_getDTOConverterContext(relatedObjectEntryId),
			ObjectRelationshipUtil.getRelatedObjectDefinition(
				_objectDefinition,
				_objectRelationshipLocalService.getObjectRelationship(
					_objectDefinition.getObjectDefinitionId(),
					objectRelationshipName)),
			relatedObjectEntryId);
	}

	private void _checkSystemObjectEntry(
			long objectEntryId, ObjectDefinition systemObjectDefinition)
		throws Exception {

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(
					systemObjectDefinition.getClassName());

		persistedModelLocalService.getPersistedModel(objectEntryId);
	}

	private DefaultDTOConverterContext _getDTOConverterContext(
		Long objectEntryId) {

		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(), null, null,
			contextHttpServletRequest, objectEntryId,
			contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
			contextUser);
	}

	private ObjectEntry _getRelatedObjectEntry(
		ObjectDefinition objectDefinition, ObjectEntry objectEntry) {

		Map<String, Map<String, String>> actions = objectEntry.getActions();

		for (Map.Entry<String, Map<String, String>> entry :
				actions.entrySet()) {

			Map<String, String> map = entry.getValue();

			if (map == null) {
				continue;
			}

			String href = map.get("href");

			map.put(
				"href",
				StringUtil.replace(
					href,
					StringUtil.lowerCaseFirstLetter(
						_objectDefinition.getPluralLabel(
							contextAcceptLanguage.getPreferredLocale())),
					StringUtil.lowerCaseFirstLetter(
						objectDefinition.getPluralLabel(
							contextAcceptLanguage.getPreferredLocale()))));
		}

		return objectEntry;
	}

	@Context
	private ObjectDefinition _objectDefinition;

	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryManagerRegistry _objectEntryManagerRegistry;
	private final ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;

}