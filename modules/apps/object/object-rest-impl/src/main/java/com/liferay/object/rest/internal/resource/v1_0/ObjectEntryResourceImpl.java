/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0;

import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.object.exception.ObjectEntryValidationException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationshipModel;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.ValidationError;
import com.liferay.object.rest.dto.v1_0.ValidationRequest;
import com.liferay.object.rest.dto.v1_0.ValidationResponse;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.rest.odata.entity.v1_0.provider.EntityModelProvider;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectRelationshipService;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Javier Gamarra
 */
public class ObjectEntryResourceImpl
	extends BaseObjectEntryResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<ObjectEntry> {

	public ObjectEntryResourceImpl(
		DTOConverterRegistry dtoConverterRegistry,
		EntityModelProvider entityModelProvider,
		ObjectDefinition objectDefinition,
		Map<Long, ObjectDefinition> objectDefinitions,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectEntryManagerRegistry objectEntryManagerRegistry,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		ObjectRelationshipService objectRelationshipService,
		ObjectScopeProviderRegistry objectScopeProviderRegistry,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry) {

		_dtoConverterRegistry = dtoConverterRegistry;
		_entityModelProvider = entityModelProvider;
		_objectDefinition = objectDefinition;
		_objectDefinitions = objectDefinitions;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectEntryManagerRegistry = objectEntryManagerRegistry;
		_objectFieldLocalService = objectFieldLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_objectRelationshipService = objectRelationshipService;
		_objectScopeProviderRegistry = objectScopeProviderRegistry;
		_systemObjectDefinitionManagerRegistry =
			systemObjectDefinitionManagerRegistry;
	}

	@Override
	public void create(
			Collection<ObjectEntry> objectEntries,
			Map<String, Serializable> parameters)
		throws Exception {

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				_objectDefinition.getScope());

		if (objectScopeProvider.isGroupAware()) {
			UnsafeFunction<ObjectEntry, ObjectEntry, Exception>
				objectEntryUnsafeFunction = null;

			String createStrategy = (String)parameters.getOrDefault(
				"createStrategy", "INSERT");

			if (StringUtil.equalsIgnoreCase(createStrategy, "INSERT")) {
				objectEntryUnsafeFunction = objectEntry -> postScopeScopeKey(
					_getScopeKey(parameters), objectEntry);
			}

			if (StringUtil.equalsIgnoreCase(createStrategy, "UPSERT")) {
				objectEntryUnsafeFunction =
					objectEntry -> putScopeScopeKeyByExternalReferenceCode(
						_getScopeKey(parameters),
						objectEntry.getExternalReferenceCode(), objectEntry);
			}

			if (objectEntryUnsafeFunction == null) {
				throw new NotSupportedException(
					"Create strategy \"" + createStrategy +
						"\" is not supported");
			}

			contextBatchUnsafeBiConsumer.accept(
				objectEntries, objectEntryUnsafeFunction);
		}
		else {
			super.create(objectEntries, parameters);
		}
	}

	@Override
	public void delete(
			Collection<ObjectEntry> objectEntries,
			Map<String, Serializable> parameters)
		throws Exception {

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				_objectDefinition.getScope());

		if (objectScopeProvider.isGroupAware()) {
			UnsafeFunction<ObjectEntry, ObjectEntry, Exception>
				objectEntryUnsafeFunction = objectEntry -> {
					if (objectEntry.getId() != null) {
						try {
							deleteObjectEntry(objectEntry.getId());

							return objectEntry;
						}
						catch (Exception exception) {
							if (_log.isDebugEnabled()) {
								_log.debug(exception);
							}

							if (objectEntry.getExternalReferenceCode() !=
									null) {

								deleteScopeScopeKeyByExternalReferenceCode(
									_getScopeKey(parameters),
									objectEntry.getExternalReferenceCode());

								return objectEntry;
							}
						}
					}
					else if (objectEntry.getExternalReferenceCode() != null) {
						deleteScopeScopeKeyByExternalReferenceCode(
							_getScopeKey(parameters),
							objectEntry.getExternalReferenceCode());

						return objectEntry;
					}

					throw new UnsupportedOperationException(
						"Unable to delete by external reference code or ID");
				};

			if (contextBatchUnsafeBiConsumer != null) {
				contextBatchUnsafeBiConsumer.accept(
					objectEntries, objectEntryUnsafeFunction);
			}
			else if (contextBatchUnsafeConsumer != null) {
				contextBatchUnsafeConsumer.accept(
					objectEntries, objectEntryUnsafeFunction::apply);
			}
			else {
				for (ObjectEntry objectEntry : objectEntries) {
					objectEntryUnsafeFunction.apply(objectEntry);
				}
			}
		}
		else {
			super.delete(objectEntries, parameters);
		}
	}

	@Override
	public void deleteByExternalReferenceCode(String externalReferenceCode)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getStorageType());

		objectEntryManager.deleteObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, null);
	}

	@Override
	public void deleteByExternalReferenceCodeByVersion(
			String externalReferenceCode, Integer version)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.deleteObjectEntryByVersion(
			externalReferenceCode, _objectDefinition, null, version);
	}

	@Override
	public void deleteObjectEntry(Long objectEntryId) throws Exception {
		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.deleteObjectEntry(
			_objectDefinition, objectEntryId);
	}

	@Override
	public Response deleteObjectEntryBatch(String callbackURL, Object object)
		throws Exception {

		vulcanBatchEngineImportTaskResource.setTaskItemDelegateName(
			_objectDefinition.getName());

		return super.deleteObjectEntryBatch(callbackURL, object);
	}

	@Override
	public void deleteObjectEntryByVersion(Long objectEntryId, Integer version)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.deleteObjectEntryByVersion(
			_objectDefinition, objectEntryId, version);
	}

	@Override
	public void deleteScopeScopeKeyByExternalReferenceCode(
			String scopeKey, String externalReferenceCode)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getStorageType());

		objectEntryManager.deleteObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, scopeKey);
	}

	@Override
	public void deleteScopeScopeKeyByExternalReferenceCodeByVersion(
			String scopeKey, String externalReferenceCode, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.deleteObjectEntryByVersion(
			externalReferenceCode, _objectDefinition, scopeKey, version);
	}

	@Override
	public ObjectEntry getApprovedByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getApprovedObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, null);
	}

	@Override
	public Page<ObjectEntry> getApprovedPage(
			String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getApprovedObjectEntries(
			contextCompany.getCompanyId(), _objectDefinition, null, aggregation,
			_getDTOConverterContext(null), _getFilterString(), pagination,
			search, sorts);
	}

	@Override
	public ObjectEntry getByExternalReferenceCode(String externalReferenceCode)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getStorageType());

		return objectEntryManager.getObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, null);
	}

	@Override
	public ObjectEntry getByExternalReferenceCodeByVersion(
			String externalReferenceCode, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getObjectEntryByVersion(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, null, version);
	}

	@Override
	public Page<ObjectEntry> getByExternalReferenceCodeVersionsPage(
			String externalReferenceCode, Pagination pagination)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getVersionedObjectEntries(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, null, pagination);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		if (_objectDefinition != null) {
			return _entityModelProvider.getEntityModel(_objectDefinition);
		}

		return _entityModelProvider.getEntityModel(
			_objectDefinitions.get(contextCompany.getCompanyId()));
	}

	@Override
	public ExportImportDescriptor getExportImportDescriptor() {
		return new ExportImportDescriptor() {

			@Override
			public String getItemClassName() {
				return _objectDefinition.getClassName();
			}

			@Override
			public String getItemModelName() {
				return _objectDefinition.getShortName();
			}

			@Override
			public List<String> getNestedFields() {
				return transform(
					_objectRelationshipLocalService.
						getObjectRelationshipsByObjectDefinitionId2(
							_objectDefinition.getObjectDefinitionId()),
					ObjectRelationshipModel::getName);
			}

			@Override
			public String getPortletId() {
				return _objectDefinition.getPortletId();
			}

			@Override
			public Scope getScope() {
				if (StringUtil.equalsIgnoreCase(
						_objectDefinition.getScope(), "company")) {

					return Scope.COMPANY;
				}

				if (StringUtil.equalsIgnoreCase(
						_objectDefinition.getScope(), "depot")) {

					return Scope.DEPOT;
				}

				return Scope.SITE;
			}

		};
	}

	@Override
	public Page<ObjectEntry> getObjectEntriesPage(
			String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getStorageType());

		return objectEntryManager.getObjectEntries(
			contextCompany.getCompanyId(), _objectDefinition, null, aggregation,
			_getDTOConverterContext(null), _getFilterString(), pagination,
			search, sorts);
	}

	@Override
	public Page<ObjectEntry> getObjectEntriesVersionsPage(
			Long objectEntryId, Pagination pagination)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getVersionedObjectEntries(
			_getDTOConverterContext(objectEntryId), _objectDefinition,
			objectEntryId, pagination);
	}

	@Override
	public ObjectEntry getObjectEntry(Long objectEntryId) throws Exception {
		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getObjectEntry(
			_getDTOConverterContext(objectEntryId), _objectDefinition,
			objectEntryId);
	}

	@Override
	public ObjectEntry getObjectEntryByVersion(
			Long objectEntryId, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getObjectEntryByVersion(
			_getDTOConverterContext(objectEntryId), objectEntryId, version);
	}

	@Override
	public String getResourceName() {
		return _objectDefinition.getShortName();
	}

	@Override
	public ObjectEntry getScopeScopeKeyApprovedByExternalReferenceCode(
			String scopeKey, String externalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getApprovedObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, scopeKey);
	}

	@Override
	public Page<ObjectEntry> getScopeScopeKeyApprovedPage(
			String scopeKey, String search, Aggregation aggregation,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getApprovedObjectEntries(
			contextCompany.getCompanyId(), _objectDefinition, scopeKey,
			aggregation, _getDTOConverterContext(null), _getFilterString(),
			pagination, search, sorts);
	}

	@Override
	public ObjectEntry getScopeScopeKeyByExternalReferenceCode(
			String scopeKey, String externalReferenceCode)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getStorageType());

		return objectEntryManager.getObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, scopeKey);
	}

	@Override
	public ObjectEntry getScopeScopeKeyByExternalReferenceCodeByVersion(
			String scopeKey, String externalReferenceCode, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getObjectEntryByVersion(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, scopeKey, version);
	}

	@Override
	public Page<ObjectEntry>
			getScopeScopeKeyByExternalReferenceCodeVersionsPage(
				String scopeKey, String externalReferenceCode,
				Pagination pagination)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.getVersionedObjectEntries(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, scopeKey, pagination);
	}

	@Override
	public Page<ObjectEntry> getScopeScopeKeyPage(
			String scopeKey, String search, Aggregation aggregation,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getStorageType());

		return objectEntryManager.getObjectEntries(
			contextCompany.getCompanyId(), _objectDefinition, scopeKey,
			aggregation, _getDTOConverterContext(null), _getFilterString(),
			pagination, search, sorts);
	}

	@Override
	public ObjectEntry patchByExternalReferenceCode(
			String externalReferenceCode, ObjectEntry objectEntry)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getStorageType());

		return objectEntryManager.partialUpdateObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, objectEntry, null);
	}

	@Override
	public ObjectEntry patchObjectEntry(
			Long objectEntryId, ObjectEntry objectEntry)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.partialUpdateObjectEntry(
			_getDTOConverterContext(objectEntryId), _objectDefinition,
			objectEntryId, objectEntry);
	}

	@Override
	public ObjectEntry patchScopeScopeKeyByExternalReferenceCode(
			String scopeKey, String externalReferenceCode,
			ObjectEntry objectEntry)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getStorageType());

		return objectEntryManager.partialUpdateObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, objectEntry, scopeKey);
	}

	@Override
	public ObjectEntry postByExternalReferenceCodeByVersionCopy(
			String externalReferenceCode, Integer version)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.copyObjectEntryByVersion(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, null, version);
	}

	@Override
	public ObjectEntry postByExternalReferenceCodeByVersionExpire(
			String externalReferenceCode, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.expireObjectEntryByVersion(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, null, version);
	}

	@Override
	public void postByExternalReferenceCodeSubscribe(
			String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.subscribeObjectEntry(
			externalReferenceCode, _objectDefinition, null);
	}

	@Override
	public void postByExternalReferenceCodeUnsubscribe(
			String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.unsubscribeObjectEntry(
			externalReferenceCode, _objectDefinition, null);
	}

	@Override
	public Response postObjectEntriesPageExportBatch(
			String search, Filter filter, Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception {

		vulcanBatchEngineExportTaskResource.setTaskItemDelegateName(
			_objectDefinition.getName());

		return super.postObjectEntriesPageExportBatch(
			search, filter, sorts, callbackURL, contentType, fieldNames);
	}

	@Override
	public ObjectEntry postObjectEntry(ObjectEntry objectEntry)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getStorageType());

		return objectEntryManager.addObjectEntry(
			_getDTOConverterContext(null), _objectDefinition, objectEntry,
			null);
	}

	@Override
	public Response postObjectEntryBatch(String callbackURL, Object object)
		throws Exception {

		vulcanBatchEngineImportTaskResource.setTaskItemDelegateName(
			_objectDefinition.getName());

		return super.postObjectEntryBatch(callbackURL, object);
	}

	@Override
	public ObjectEntry postObjectEntryByVersionCopy(
			Long objectEntryId, Integer version)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.copyObjectEntryByVersion(
			_getDTOConverterContext(objectEntryId), _objectDefinition,
			objectEntryId, version);
	}

	@Override
	public ObjectEntry postObjectEntryByVersionExpire(
			Long objectEntryId, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.expireObjectEntryByVersion(
			_getDTOConverterContext(null), _objectDefinition, objectEntryId,
			version);
	}

	@Override
	public ObjectEntry postObjectEntryExpire(Long objectEntryId)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.expireObjectEntry(
			_getDTOConverterContext(objectEntryId), objectEntryId);
	}

	@Override
	public ObjectEntry postScopeScopeKey(
			String scopeKey, ObjectEntry objectEntry)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getStorageType());

		return objectEntryManager.addObjectEntry(
			_getDTOConverterContext(null), _objectDefinition, objectEntry,
			scopeKey);
	}

	@Override
	public ObjectEntry postScopeScopeKeyByExternalReferenceCodeByVersionCopy(
			String scopeKey, String externalReferenceCode, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.copyObjectEntryByVersion(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, scopeKey, version);
	}

	@Override
	public ObjectEntry postScopeScopeKeyByExternalReferenceCodeByVersionExpire(
			String scopeKey, String externalReferenceCode, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.expireObjectEntryByVersion(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, scopeKey, version);
	}

	@Override
	public ObjectEntry postScopeScopeKeyByExternalReferenceCodeExpire(
			String scopeKey, String externalReferenceCode)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.expireObjectEntry(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, scopeKey);
	}

	@Override
	public void postScopeScopeKeyByExternalReferenceCodeSubscribe(
			String scopeKey, String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.subscribeObjectEntry(
			externalReferenceCode, _objectDefinition, scopeKey);
	}

	@Override
	public void postScopeScopeKeyByExternalReferenceCodeUnsubscribe(
			String scopeKey, String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.unsubscribeObjectEntry(
			externalReferenceCode, _objectDefinition, scopeKey);
	}

	@Override
	public ValidationResponse postScopeScopeKeyValidate(
			String scopeKey, ValidationRequest validationRequest)
		throws Exception {

		return _validateObjectEntry(scopeKey, validationRequest);
	}

	@Override
	public ValidationResponse postValidate(ValidationRequest validationRequest)
		throws Exception {

		return _validateObjectEntry(null, validationRequest);
	}

	@Override
	public ObjectEntry putByExternalReferenceCode(
			String externalReferenceCode, ObjectEntry objectEntry)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getStorageType());

		return objectEntryManager.updateObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, objectEntry, null);
	}

	@Override
	public ObjectEntry putByExternalReferenceCodeByVersionRestore(
			String externalReferenceCode, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.restoreObjectEntryByVersion(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, null, version);
	}

	@Override
	public void putByExternalReferenceCodeObjectActionObjectActionName(
			String externalReferenceCode, String objectActionName)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.executeObjectAction(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, objectActionName, _objectDefinition, null);
	}

	@Override
	public ObjectEntry putByExternalReferenceCodeRestore(
			String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.restoreObjectEntry(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, null);
	}

	@Override
	public ObjectEntry putObjectEntry(
			Long objectEntryId, ObjectEntry objectEntry)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.updateObjectEntry(
			_getDTOConverterContext(objectEntryId), _objectDefinition,
			objectEntryId, objectEntry);
	}

	@Override
	public Response putObjectEntryBatch(String callbackURL, Object object)
		throws Exception {

		vulcanBatchEngineImportTaskResource.setTaskItemDelegateName(
			_objectDefinition.getName());

		return super.putObjectEntryBatch(callbackURL, object);
	}

	@Override
	public ObjectEntry putObjectEntryByVersionRestore(
			Long objectEntryId, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.restoreObjectEntryByVersion(
			_getDTOConverterContext(objectEntryId), _objectDefinition,
			objectEntryId, version);
	}

	@Override
	public void putObjectEntryObjectActionObjectActionName(
			Long objectEntryId, String objectActionName)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.executeObjectAction(
			_getDTOConverterContext(objectEntryId), objectActionName,
			_objectDefinition, objectEntryId);
	}

	@Override
	public ObjectEntry putScopeScopeKeyByExternalReferenceCode(
			String scopeKey, String externalReferenceCode,
			ObjectEntry objectEntry)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getStorageType());

		return objectEntryManager.updateObjectEntry(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, _objectDefinition, objectEntry, scopeKey);
	}

	@Override
	public ObjectEntry putScopeScopeKeyByExternalReferenceCodeByVersionRestore(
			String scopeKey, String externalReferenceCode, Integer version)
		throws Exception {

		if (!_objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.restoreObjectEntryByVersion(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, scopeKey, version);
	}

	@Override
	public void
			putScopeScopeKeyByExternalReferenceCodeObjectActionObjectActionName(
				String scopeKey, String externalReferenceCode,
				String objectActionName)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		defaultObjectEntryManager.executeObjectAction(
			contextCompany.getCompanyId(), _getDTOConverterContext(null),
			externalReferenceCode, objectActionName, _objectDefinition,
			scopeKey);
	}

	@Override
	public ObjectEntry putScopeScopeKeyByExternalReferenceCodeRestore(
			String scopeKey, String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		return defaultObjectEntryManager.restoreObjectEntry(
			_getDTOConverterContext(null), externalReferenceCode,
			_objectDefinition, scopeKey);
	}

	@Override
	public Page<ObjectEntry> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				_objectDefinition.getScope());

		if (objectScopeProvider.isGroupAware()) {
			return getScopeScopeKeyPage(
				_getScopeKey(parameters), search, null, filter, pagination,
				sorts);
		}

		return getObjectEntriesPage(search, null, filter, pagination, sorts);
	}

	public void setObjectDefinition(ObjectDefinition objectDefinition) {
		_objectDefinition = objectDefinition;
	}

	@Override
	protected String getApplicationPath() {
		String restContextPath = null;

		if (_objectDefinition != null) {
			restContextPath = _objectDefinition.getRESTContextPath();
		}
		else {
			ObjectDefinition objectDefinition = _objectDefinitions.get(
				contextCompany.getCompanyId());

			restContextPath = objectDefinition.getRESTContextPath();
		}

		return StringUtil.removeFirst(restContextPath, "/");
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		com.liferay.object.model.ObjectEntry objectEntry =
			_objectEntryLocalService.getObjectEntry(GetterUtil.getLong(id));

		return objectEntry.getGroupId();
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id)
		throws Exception {

		return _objectDefinition.getClassName();
	}

	@Override
	protected void preparePatch(
		ObjectEntry objectEntry, ObjectEntry existingObjectEntry) {

		if (objectEntry.getStatus() != null) {
			existingObjectEntry.setStatus(objectEntry::getStatus);
		}
	}

	private DefaultDTOConverterContext _getDTOConverterContext(
		Long objectEntryId) {

		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(), null,
			_dtoConverterRegistry, contextHttpServletRequest, objectEntryId,
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

	private String _getScopeKey(Map<String, Serializable> parameters) {
		if (parameters.containsKey("scopeKey")) {
			return String.valueOf(parameters.get("scopeKey"));
		}

		if (parameters.containsKey("siteExternalReferenceCode")) {
			return String.valueOf(parameters.get("siteExternalReferenceCode"));
		}

		if (parameters.containsKey("siteId")) {
			return String.valueOf(parameters.get("siteId"));
		}

		return null;
	}

	private ValidationResponse _validateObjectEntry(
			String scopeKey, ValidationRequest validationRequest)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					_objectDefinition.getStorageType()));

		try {
			defaultObjectEntryManager.validateObjectEntry(
				_getDTOConverterContext(null), _objectDefinition,
				validationRequest.getValues(),
				ListUtil.fromArray(
					validationRequest.
						getObjectValidationRuleExternalReferenceCodes()),
				scopeKey);
		}
		catch (ObjectEntryValidationException objectEntryValidationException) {
			return new ValidationResponse() {
				{
					setValidationErrors(
						() -> transformToArray(
							objectEntryValidationException.
								getValidationErrors(),
							validationError -> new ValidationError() {
								{
									setErrorMessage(
										validationError::getErrorMessage);
									setObjectFieldName(
										validationError::getObjectFieldName);
									setObjectValidationRuleExternalReferenceCode(
										validationError::
											getObjectValidationRuleExternalReferenceCode);
								}
							},
							ValidationError.class));
				}
			};
		}

		return new ValidationResponse();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryResourceImpl.class);

	private final DTOConverterRegistry _dtoConverterRegistry;
	private final EntityModelProvider _entityModelProvider;

	@Context
	private ObjectDefinition _objectDefinition;

	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final Map<Long, ObjectDefinition> _objectDefinitions;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectEntryManagerRegistry _objectEntryManagerRegistry;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final ObjectRelationshipService _objectRelationshipService;
	private final ObjectScopeProviderRegistry _objectScopeProviderRegistry;
	private final SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

}