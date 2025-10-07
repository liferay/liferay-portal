/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.rest.odata.entity.v1_0.provider.EntityModelProvider;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Map;

/**
 * @author Carlos Correa
 */
public class ObjectEntryRelatedObjectsResourceImpl
	extends BaseObjectEntryRelatedObjectsResourceImpl {

	public ObjectEntryRelatedObjectsResourceImpl(
		EntityModelProvider entityModelProvider,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectEntryManagerRegistry objectEntryManagerRegistry,
		ObjectRelatedModelsProviderRegistry objectRelatedModelsProviderRegistry,
		ObjectRelationshipLocalService objectRelationshipLocalService) {

		_entityModelProvider = entityModelProvider;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectEntryManagerRegistry = objectEntryManagerRegistry;
		_objectRelatedModelsProviderRegistry =
			objectRelatedModelsProviderRegistry;
		_objectRelationshipLocalService = objectRelationshipLocalService;
	}

	@Override
	public void
			deleteByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				String currentExternalReferenceCode,
				String objectRelationshipName,
				String relatedExternalReferenceCode)
		throws Exception {

		_deleteRelatedObjectEntry(
			null, currentExternalReferenceCode, objectRelationshipName,
			relatedExternalReferenceCode);
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

		if (objectRelationship.isEdge()) {
			defaultObjectEntryManager.deleteRelatedObjectEntry(
				relatedObjectEntryId, objectRelationship, currentObjectEntryId);

			return;
		}

		ObjectRelatedModelsProvider objectRelatedModelsProvider =
			_objectRelatedModelsProviderRegistry.getObjectRelatedModelsProvider(
				relatedObjectDefinition.getClassName(),
				contextCompany.getCompanyId(), objectRelationship.getType());

		objectRelatedModelsProvider.disassociateRelatedModels(
			contextUser.getUserId(),
			objectRelationship.getObjectRelationshipId(), currentObjectEntryId,
			relatedObjectEntryId);
	}

	@Override
	public void
			deleteScopeScopeKeyByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				String scopeKey, String currentExternalReferenceCode,
				String objectRelationshipName,
				String relatedExternalReferenceCode)
		throws Exception {

		_deleteRelatedObjectEntry(
			scopeKey, currentExternalReferenceCode, objectRelationshipName,
			relatedExternalReferenceCode);
	}

	@Override
	public Page<Object>
			getByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNamePage(
				String currentExternalReferenceCode,
				String objectRelationshipName, String search,
				Aggregation aggregation, Filter filter, Pagination pagination,
				Sort[] sorts)
		throws Exception {

		return _getRelatedObjectEntries(
			aggregation, currentExternalReferenceCode, objectRelationshipName,
			pagination, null, search, sorts);
	}

	@Override
	public Object
			getByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				String currentExternalReferenceCode,
				String objectRelationshipName,
				String relatedExternalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getRelatedObjectEntry(
			_getDTOConverterContext(null), currentExternalReferenceCode,
			_objectRelationshipLocalService.getObjectRelationship(
				_objectDefinition.getObjectDefinitionId(),
				objectRelationshipName),
			relatedExternalReferenceCode, null);
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
			defaultObjectEntryManager.getRelatedObjectEntries(
				_getDTOConverterContext(currentObjectEntryId),
				currentObjectEntryId, objectRelationship, pagination);

		return Page.of(
			page.getActions(),
			transform(
				page.getItems(),
				objectEntry -> _getRelatedObjectEntry(
					relatedObjectDefinition, objectEntry)),
			pagination, page.getTotalCount());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				_objectDefinition.getObjectDefinitionId(),
				GetterUtil.getString(
					multivaluedMap.getFirst("objectRelationshipName")));

		return _entityModelProvider.getEntityModel(
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2()));
	}

	@Override
	public Object getObjectEntryObjectRelationshipNameRelatedObjectEntry(
			Long currentObjectEntryId, String objectRelationshipName,
			Long relatedObjectEntryId)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getRelatedObjectEntry(
			_getDTOConverterContext(currentObjectEntryId), relatedObjectEntryId,
			_objectRelationshipLocalService.getObjectRelationship(
				_objectDefinition.getObjectDefinitionId(),
				objectRelationshipName),
			currentObjectEntryId);
	}

	@Override
	public Page<Object>
			getScopeScopeKeyByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNamePage(
				String scopeKey, String currentExternalReferenceCode,
				String objectRelationshipName, String search,
				Aggregation aggregation, Filter filter, Pagination pagination,
				Sort[] sorts)
		throws Exception {

		return _getRelatedObjectEntries(
			aggregation, currentExternalReferenceCode, objectRelationshipName,
			pagination, scopeKey, search, sorts);
	}

	@Override
	public Object
			getScopeScopeKeyByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				String scopeKey, String currentExternalReferenceCode,
				String objectRelationshipName,
				String relatedExternalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getRelatedObjectEntry(
			_getDTOConverterContext(null), currentExternalReferenceCode,
			_objectRelationshipLocalService.getObjectRelationship(
				_objectDefinition.getObjectDefinitionId(),
				objectRelationshipName),
			relatedExternalReferenceCode, scopeKey);
	}

	@Override
	public Object
			patchByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				String currentExternalReferenceCode, ObjectEntry objectEntry,
				String objectRelationshipName,
				String relatedExternalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.partialUpdateRelatedObjectEntry(
			_getDTOConverterContext(null), relatedExternalReferenceCode,
			objectEntry,
			_objectRelationshipLocalService.getObjectRelationship(
				_objectDefinition.getObjectDefinitionId(),
				objectRelationshipName),
			currentExternalReferenceCode, null);
	}

	@Override
	public Object patchCurrentObjectEntry(
			Long currentObjectEntryId, ObjectEntry objectEntry,
			String objectRelationshipName, Long relatedObjectEntryId)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.partialUpdateRelatedObjectEntry(
			_getDTOConverterContext(currentObjectEntryId), objectEntry,
			relatedObjectEntryId,
			_objectRelationshipLocalService.getObjectRelationship(
				_objectDefinition.getObjectDefinitionId(),
				objectRelationshipName),
			currentObjectEntryId);
	}

	@Override
	public Object
			patchScopeScopeKeyByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				String scopeKey, String currentExternalReferenceCode,
				ObjectEntry objectEntry, String objectRelationshipName,
				String relatedExternalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.partialUpdateRelatedObjectEntry(
			_getDTOConverterContext(null), relatedExternalReferenceCode,
			objectEntry,
			_objectRelationshipLocalService.getObjectRelationship(
				_objectDefinition.getObjectDefinitionId(),
				objectRelationshipName),
			currentExternalReferenceCode, scopeKey);
	}

	@Override
	public Object postByExternalReferenceCodeObjectEntryObjectRelationshipName(
			String currentExternalReferenceCode, ObjectEntry objectEntry,
			String objectRelationshipName)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.addRelatedObjectEntry(
			_getDTOConverterContext(null), currentExternalReferenceCode,
			objectEntry,
			_objectRelationshipLocalService.getObjectRelationship(
				_objectDefinition.getObjectDefinitionId(),
				objectRelationshipName),
			null);
	}

	@Override
	public Object postObjectEntryObjectRelationshipName(
			Long currentObjectEntryId, ObjectEntry objectEntry,
			String objectRelationshipName)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.addRelatedObjectEntry(
			_getDTOConverterContext(null), objectEntry, currentObjectEntryId,
			_objectRelationshipLocalService.getObjectRelationship(
				_objectDefinition.getObjectDefinitionId(),
				objectRelationshipName));
	}

	@Override
	public Object
			postScopeScopeKeyByExternalReferenceCodeObjectEntryObjectRelationshipName(
				String scopeKey, String currentExternalReferenceCode,
				ObjectEntry objectEntry, String objectRelationshipName)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.addRelatedObjectEntry(
			_getDTOConverterContext(null), currentExternalReferenceCode,
			objectEntry,
			_objectRelationshipLocalService.getObjectRelationship(
				_objectDefinition.getObjectDefinitionId(),
				objectRelationshipName),
			scopeKey);
	}

	@Override
	public Object
			putByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				String currentExternalReferenceCode,
				String objectRelationshipName,
				String relatedExternalReferenceCode)
		throws Exception {

		com.liferay.object.model.ObjectEntry currentObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				currentExternalReferenceCode,
				ObjectDefinitionConstants.GROUP_ID_DEFAULT,
				_objectDefinition.getObjectDefinitionId());

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				_objectDefinition.getObjectDefinitionId(),
				objectRelationshipName);

		ObjectDefinition relatedObjectDefinition =
			ObjectRelationshipUtil.getRelatedObjectDefinition(
				_objectDefinition, objectRelationship);

		com.liferay.object.model.ObjectEntry relatedObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				relatedExternalReferenceCode,
				ObjectDefinitionConstants.GROUP_ID_DEFAULT,
				relatedObjectDefinition.getObjectDefinitionId());

		return putCurrentObjectEntry(
			currentObjectEntry.getObjectEntryId(), objectRelationshipName,
			relatedObjectEntry.getObjectEntryId());
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

	@Override
	public Object
			putScopeScopeKeyByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				String scopeKey, String currentExternalReferenceCode,
				ObjectEntry objectEntry, String objectRelationshipName,
				String relatedExternalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.updateRelatedObjectEntry(
			_getDTOConverterContext(null), relatedExternalReferenceCode,
			objectEntry,
			_objectRelationshipLocalService.getObjectRelationship(
				_objectDefinition.getObjectDefinitionId(),
				objectRelationshipName),
			currentExternalReferenceCode, scopeKey);
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

	private void _deleteRelatedObjectEntry(
			String scopeKey, String currentExternalReferenceCode,
			String objectRelationshipName, String relatedExternalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.deleteRelatedObjectEntry(
			relatedExternalReferenceCode,
			_objectRelationshipLocalService.getObjectRelationship(
				_objectDefinition.getObjectDefinitionId(),
				objectRelationshipName),
			currentExternalReferenceCode, scopeKey);
	}

	private DefaultDTOConverterContext _getDTOConverterContext(
		Long objectEntryId) {

		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(), null, null,
			contextHttpServletRequest, objectEntryId,
			contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
			contextUser);
	}

	private String _getFilterString() {
		if (contextHttpServletRequest != null) {
			return ParamUtil.getString(contextHttpServletRequest, "filter");
		}

		if (contextUriInfo == null) {
			return null;
		}

		MultivaluedMap<String, String> queryParameters =
			contextUriInfo.getQueryParameters();

		return queryParameters.getFirst("filter");
	}

	private Page<Object> _getRelatedObjectEntries(
			Aggregation aggregation, String externalReferenceCode,
			String objectRelationshipName, Pagination pagination,
			String scopeKey, String search, Sort[] sorts)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				_objectDefinition.getObjectDefinitionId(),
				objectRelationshipName);

		Page<ObjectEntry> page =
			defaultObjectEntryManager.getRelatedObjectEntries(
				aggregation, _getDTOConverterContext(null),
				externalReferenceCode, _getFilterString(), objectRelationship,
				pagination, scopeKey, search, sorts);

		return Page.of(
			page.getActions(), page.getFacets(),
			transform(
				page.getItems(),
				objectEntry -> _getRelatedObjectEntry(
					_objectDefinitionLocalService.getObjectDefinition(
						objectRelationship.getObjectDefinitionId2()),
					objectEntry)),
			pagination, page.getTotalCount());
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

	private final EntityModelProvider _entityModelProvider;

	@Context
	private ObjectDefinition _objectDefinition;

	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectEntryManagerRegistry _objectEntryManagerRegistry;
	private final ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;

}