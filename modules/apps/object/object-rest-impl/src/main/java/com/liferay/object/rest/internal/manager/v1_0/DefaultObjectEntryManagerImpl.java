/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.manager.v1_0;

import com.liferay.account.exception.NoSuchGroupException;
import com.liferay.batch.engine.attachment.BatchEngineAttachmentManager;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.object.action.engine.ObjectActionEngine;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.entry.util.ObjectEntryDTOConverterUtil;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.field.attachment.AttachmentManager;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.rest.dto.v1_0.FileEntry;
import com.liferay.object.rest.dto.v1_0.Folder;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Scope;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.rest.filter.parser.ObjectDefinitionFilterParser;
import com.liferay.object.rest.internal.resource.v1_0.ObjectEntryRelatedObjectsResourceImpl;
import com.liferay.object.rest.internal.resource.v1_0.ObjectEntryResourceImpl;
import com.liferay.object.rest.internal.util.ObjectEntryValuesUtil;
import com.liferay.object.rest.internal.util.ServiceContextUtil;
import com.liferay.object.rest.manager.v1_0.BaseObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.rest.manager.v1_0.ObjectRelationshipElementsParser;
import com.liferay.object.rest.manager.v1_0.ObjectRelationshipElementsParserRegistry;
import com.liferay.object.rest.manager.v1_0.util.ObjectEntryManagerUtil;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectEntryVersionService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectRelationshipService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.filter.expression.Expression;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.FilterAggregation;
import com.liferay.portal.search.aggregation.bucket.NestedAggregation;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.aggregation.Facet;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.permission.ModelPermissionsUtil;
import com.liferay.portal.vulcan.util.ActionUtil;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

import java.io.IOException;
import java.io.Serializable;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier de Arcos
 */
@Component(
	property = "object.entry.manager.storage.type=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
	service = ObjectEntryManager.class
)
public class DefaultObjectEntryManagerImpl
	extends BaseObjectEntryManager implements DefaultObjectEntryManager {

	@Override
	public ObjectEntry addObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			String scopeKey)
		throws Exception {

		validateReadOnlyObjectFields(null, objectDefinition, objectEntry);

		long groupId = getGroupId(objectDefinition, scopeKey);

		ServiceContext serviceContext = _createServiceContext(
			dtoConverterContext, objectDefinition, objectEntry, scopeKey);

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.addObjectEntry(
				groupId, objectDefinition.getObjectDefinitionId(),
				_getObjectEntryFolderId(
					objectDefinition.getCompanyId(), groupId, objectEntry),
				objectEntry.getDefaultLanguageId(),
				_toObjectValues(
					dtoConverterContext.getLocale(), objectDefinition,
					objectEntry, scopeKey, serviceContext),
				serviceContext);

		return _toObjectEntry(
			dtoConverterContext, objectDefinition,
			_addOrUpdateNestedObjectEntries(
				dtoConverterContext, objectDefinition, objectEntry,
				_getObjectRelationships(objectDefinition, objectEntry),
				serviceBuilderObjectEntry, scopeKey));
	}

	@Override
	public ObjectEntry addObjectRelationshipMappingTableValues(
			DTOConverterContext dtoConverterContext,
			ObjectRelationship objectRelationship, long primaryKey1,
			long primaryKey2)
		throws Exception {

		_objectRelationshipService.addObjectRelationshipMappingTableValues(
			objectRelationship.getObjectRelationshipId(), primaryKey1,
			primaryKey2, ServiceContextUtil.createServiceContext(primaryKey2));

		return getObjectEntry(
			dtoConverterContext,
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2()),
			primaryKey2);
	}

	@Override
	public Object addSystemObjectRelationshipMappingTableValues(
			ObjectDefinition objectDefinition,
			ObjectRelationship objectRelationship, long primaryKey1,
			long primaryKey2)
		throws Exception {

		_objectRelationshipService.addObjectRelationshipMappingTableValues(
			objectRelationship.getObjectRelationshipId(), primaryKey1,
			primaryKey2, new ServiceContext());

		SystemObjectDefinitionManager systemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager(objectDefinition.getName());

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(
					systemObjectDefinitionManager.getModelClassName());

		return _toDTO(
			(BaseModel<?>)persistedModelLocalService.getPersistedModel(
				primaryKey2),
			_objectEntryService.getObjectEntry(primaryKey1),
			systemObjectDefinitionManager);
	}

	@Override
	public ObjectEntry copyObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId, int version)
		throws Exception {

		if (!objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = getObjectEntryByVersion(
			dtoConverterContext, objectEntryId, version);

		return _copyVersionedObjectEntry(
			dtoConverterContext, objectDefinition, objectEntry);
	}

	@Override
	public ObjectEntry copyObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			int version)
		throws Exception {

		if (!objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = getObjectEntryByVersion(
			dtoConverterContext, externalReferenceCode, objectDefinition,
			version);

		return _copyVersionedObjectEntry(
			dtoConverterContext, objectDefinition, objectEntry);
	}

	@Override
	public void deleteObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(
				externalReferenceCode, companyId,
				getGroupId(objectDefinition, scopeKey));

		_checkObjectEntryObjectDefinitionId(
			objectDefinition, serviceBuilderObjectEntry);

		_objectEntryService.deleteObjectEntry(
			serviceBuilderObjectEntry.getObjectEntryId());
	}

	@Override
	public void deleteObjectEntry(
			ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception {

		_checkObjectEntryObjectDefinitionId(
			objectDefinition,
			_objectEntryService.getObjectEntry(objectEntryId));

		_objectEntryService.deleteObjectEntry(objectEntryId);
	}

	@Override
	public void deleteObjectEntryByVersion(
			ObjectDefinition objectDefinition, long objectEntryId, int version)
		throws Exception {

		if (!objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		_checkObjectEntryObjectDefinitionId(
			objectDefinition,
			_objectEntryService.getObjectEntry(objectEntryId));

		_objectEntryVersionService.deleteObjectEntryVersion(
			objectEntryId, version);
	}

	@Override
	public void deleteObjectEntryByVersion(
			String externalReferenceCode, ObjectDefinition objectDefinition,
			int version)
		throws Exception {

		if (!objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(
				externalReferenceCode, objectDefinition.getCompanyId(),
				getGroupId(objectDefinition, null));

		_checkObjectEntryObjectDefinitionId(
			objectDefinition, serviceBuilderObjectEntry);

		_objectEntryVersionService.deleteObjectEntryVersion(
			serviceBuilderObjectEntry.getObjectEntryId(), version);
	}

	@Override
	public void disassociateRelatedModels(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition,
			ObjectRelationship objectRelationship, long primaryKey,
			ObjectDefinition relatedObjectDefinition, long userId)
		throws Exception {

		long[] relatedPrimaryKeys = TransformUtil.transformToLongArray(
			_getRelatedModels(
				objectDefinition, objectRelationship, primaryKey,
				relatedObjectDefinition),
			BaseModel::getPrimaryKeyObj);

		if (relatedPrimaryKeys.length > 0) {
			_disassociateRelatedModels(
				objectDefinition, objectRelationship, primaryKey,
				relatedPrimaryKeys, relatedObjectDefinition, userId);
		}
	}

	@Override
	public void executeObjectAction(
			DTOConverterContext dtoConverterContext, String objectActionName,
			ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception {

		_executeObjectAction(
			dtoConverterContext, objectActionName, objectDefinition,
			objectEntryLocalService.getObjectEntry(objectEntryId));
	}

	@Override
	public void executeObjectAction(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, String objectActionName,
			ObjectDefinition objectDefinition, String scopeKey)
		throws Exception {

		_executeObjectAction(
			dtoConverterContext, objectActionName, objectDefinition,
			objectEntryLocalService.getObjectEntry(
				externalReferenceCode, companyId,
				getGroupId(objectDefinition, scopeKey)));
	}

	@Override
	public ObjectEntry expireObjectEntry(
			DTOConverterContext dtoConverterContext, long objectEntryId)
		throws Exception {

		com.liferay.object.model.ObjectEntry objectEntry =
			_objectEntryService.expireObjectEntry(
				dtoConverterContext.getUserId(), objectEntryId,
				ServiceContextUtil.createServiceContext(objectEntryId));

		return _objectEntryDTOConverter.toDTO(dtoConverterContext, objectEntry);
	}

	@Override
	public ObjectEntry expireObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId, int version)
		throws Exception {

		return _expireObjectEntryVersion(
			dtoConverterContext, objectDefinition,
			_objectEntryService.getObjectEntry(objectEntryId), version);
	}

	@Override
	public ObjectEntry expireObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			int version)
		throws Exception {

		return _expireObjectEntryVersion(
			dtoConverterContext, objectDefinition,
			_objectEntryService.getObjectEntry(
				externalReferenceCode, objectDefinition.getCompanyId(),
				getGroupId(objectDefinition, null)),
			version);
	}

	@Override
	public ObjectEntry fetchObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.fetchObjectEntry(objectEntryId);

		if (serviceBuilderObjectEntry == null) {
			return null;
		}

		if (objectDefinition == null) {
			objectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					serviceBuilderObjectEntry.getObjectDefinitionId());
		}

		return _toObjectEntry(
			dtoConverterContext, objectDefinition, serviceBuilderObjectEntry);
	}

	@Override
	public ObjectEntry fetchRelatedManyToOneObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, Long objectEntryId,
			String objectRelationshipName)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				getObjectRelationshipByObjectDefinitionId(
					objectDefinition.getObjectDefinitionId(),
					objectRelationshipName);

		ObjectDefinition relatedObjectDefinition = _getRelatedObjectDefinition(
			objectDefinition, objectRelationship);

		ObjectRelatedModelsProvider objectRelatedModelsProvider =
			_objectRelatedModelsProviderRegistry.getObjectRelatedModelsProvider(
				relatedObjectDefinition.getClassName(),
				relatedObjectDefinition.getCompanyId(),
				objectRelationship.getType());

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			(com.liferay.object.model.ObjectEntry)
				objectRelatedModelsProvider.fetchRelatedModel(
					GroupThreadLocal.getGroupId(),
					objectRelationship.getObjectRelationshipId(),
					objectEntryId);

		if (serviceBuilderObjectEntry == null) {
			return null;
		}

		return _toObjectEntry(
			dtoConverterContext, relatedObjectDefinition,
			serviceBuilderObjectEntry);
	}

	@Override
	public Page<ObjectEntry> getObjectEntries(
			long companyId, ObjectDefinition objectDefinition, String scopeKey,
			Aggregation aggregation, DTOConverterContext dtoConverterContext,
			Expression filterExpression, Pagination pagination, String search,
			Sort[] sorts)
		throws Exception {

		Predicate predicate = _filterFactory.create(
			filterExpression, objectDefinition);

		long groupId = getGroupId(objectDefinition, scopeKey);

		int start = _getStartPosition(pagination);
		int end = _getEndPosition(pagination);

		List<Facet> facets = new ArrayList<>();

		if ((aggregation != null) &&
			(aggregation.getAggregationTerms() != null)) {

			Map<String, String> aggregationTerms =
				aggregation.getAggregationTerms();

			for (Map.Entry<String, String> entry1 :
					aggregationTerms.entrySet()) {

				List<Facet.FacetValue> facetValues = new ArrayList<>();

				Map<Object, Long> aggregationCounts =
					objectEntryLocalService.getAggregationCounts(
						groupId, objectDefinition.getObjectDefinitionId(),
						entry1.getKey(), predicate, start, end);

				for (Map.Entry<Object, Long> entry2 :
						aggregationCounts.entrySet()) {

					Long value = entry2.getValue();

					facetValues.add(
						new Facet.FacetValue(
							value.intValue(), String.valueOf(entry2.getKey())));
				}

				facets.add(new Facet(entry1.getKey(), facetValues));
			}
		}

		return Page.of(
			HashMapBuilder.put(
				"create",
				ActionUtil.addAction(
					"ADD_OBJECT_ENTRY", ObjectEntryResourceImpl.class, 0L,
					"postObjectEntry", null, objectDefinition.getUserId(),
					objectDefinition.getResourceName(), groupId,
					dtoConverterContext.getUriInfo())
			).put(
				"createBatch",
				ActionUtil.addAction(
					"ADD_OBJECT_ENTRY", ObjectEntryResourceImpl.class, 0L,
					"postObjectEntryBatch", null, objectDefinition.getUserId(),
					objectDefinition.getResourceName(), groupId,
					dtoConverterContext.getUriInfo())
			).put(
				"deleteBatch",
				ActionUtil.addAction(
					ActionKeys.DELETE, ObjectEntryResourceImpl.class, null,
					"deleteObjectEntryBatch", null,
					objectDefinition.getUserId(),
					objectDefinition.getResourceName(), groupId,
					dtoConverterContext.getUriInfo())
			).put(
				"get",
				ActionUtil.addAction(
					ActionKeys.VIEW, ObjectEntryResourceImpl.class, 0L,
					"getObjectEntriesPage", null, objectDefinition.getUserId(),
					objectDefinition.getResourceName(), groupId,
					dtoConverterContext.getUriInfo())
			).put(
				"updateBatch",
				ActionUtil.addAction(
					ActionKeys.UPDATE, ObjectEntryResourceImpl.class, null,
					"putObjectEntryBatch", null, objectDefinition.getUserId(),
					objectDefinition.getResourceName(), groupId,
					dtoConverterContext.getUriInfo())
			).build(),
			facets,
			TransformUtil.transform(
				objectEntryLocalService.getPrimaryKeys(
					groupId, companyId, dtoConverterContext.getUserId(),
					objectDefinition.getObjectDefinitionId(), predicate, search,
					start, end, sorts),
				primaryKey -> _getObjectEntry(
					dtoConverterContext, objectDefinition, primaryKey)),
			pagination,
			objectEntryLocalService.getValuesListCount(
				groupId, companyId, dtoConverterContext.getUserId(),
				objectDefinition.getObjectDefinitionId(), predicate, search));
	}

	@Override
	public Page<ObjectEntry> getObjectEntries(
			long companyId, ObjectDefinition objectDefinition, String scopeKey,
			Aggregation aggregation, DTOConverterContext dtoConverterContext,
			Filter filter, Pagination pagination, String search, Sort[] sorts)
		throws Exception {

		long groupId = getGroupId(objectDefinition, scopeKey);

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				ActionUtil.addAction(
					"ADD_OBJECT_ENTRY", ObjectEntryResourceImpl.class, 0L,
					"postObjectEntry", null, objectDefinition.getUserId(),
					objectDefinition.getResourceName(), groupId,
					dtoConverterContext.getUriInfo())
			).put(
				"get",
				ActionUtil.addAction(
					ActionKeys.VIEW, ObjectEntryResourceImpl.class, 0L,
					"getObjectEntriesPage", null, objectDefinition.getUserId(),
					objectDefinition.getResourceName(), groupId,
					dtoConverterContext.getUriInfo())
			).build(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(
						"objectDefinitionId",
						String.valueOf(
							objectDefinition.getObjectDefinitionId())),
					BooleanClauseOccur.MUST);
			},
			filter, objectDefinition.getClassName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setAttribute(
					Field.STATUS, WorkflowConstants.STATUS_ANY);
				searchContext.setAttribute(
					"objectDefinitionId",
					objectDefinition.getObjectDefinitionId());

				UriInfo uriInfo = dtoConverterContext.getUriInfo();

				if (uriInfo != null) {
					MultivaluedMap<String, String> queryParameters =
						uriInfo.getQueryParameters();

					searchContext.setAttribute(
						"searchByObjectView",
						queryParameters.containsKey("searchByObjectView"));
				}

				searchContext.setCompanyId(companyId);
				searchContext.setGroupIds(new long[] {groupId});

				SearchRequestBuilder searchRequestBuilder =
					_searchRequestBuilderFactory.builder(searchContext);

				_processVulcanAggregation(
					_aggregations, _queries, searchRequestBuilder, aggregation);
			},
			sorts,
			document -> getObjectEntry(
				dtoConverterContext, objectDefinition,
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public Page<ObjectEntry> getObjectEntries(
			long companyId, ObjectDefinition objectDefinition, String scopeKey,
			Aggregation aggregation, DTOConverterContext dtoConverterContext,
			String filterString, Pagination pagination, String search,
			Sort[] sorts)
		throws Exception {

		return getObjectEntries(
			companyId, objectDefinition, scopeKey, aggregation,
			dtoConverterContext,
			_objectDefinitionFilterParser.parse(filterString, objectDefinition),
			pagination, search, sorts);
	}

	@Override
	public ObjectEntry getObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(objectEntryId);

		_checkObjectEntryObjectDefinitionId(
			objectDefinition, serviceBuilderObjectEntry);

		return _toObjectEntry(
			dtoConverterContext, objectDefinition, serviceBuilderObjectEntry);
	}

	@Override
	public ObjectEntry getObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(
				externalReferenceCode, companyId,
				getGroupId(objectDefinition, scopeKey));

		_checkObjectEntryObjectDefinitionId(
			objectDefinition, serviceBuilderObjectEntry);

		return _toObjectEntry(
			dtoConverterContext, objectDefinition, serviceBuilderObjectEntry);
	}

	@Override
	public ObjectEntry getObjectEntryByVersion(
			DTOConverterContext dtoConverterContext, Long objectEntryId,
			int version)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(objectEntryId);

		dtoConverterContext.setAttribute(
			"objectEntryVersion",
			_objectEntryVersionService.getObjectEntryVersion(
				objectEntryId, version));

		return _objectEntryDTOConverter.toDTO(
			dtoConverterContext, serviceBuilderObjectEntry);
	}

	@Override
	public ObjectEntry getObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			int version)
		throws Exception {

		ObjectEntry objectEntry = getObjectEntry(
			objectDefinition.getCompanyId(), dtoConverterContext,
			externalReferenceCode, objectDefinition, null);

		return getObjectEntryByVersion(
			dtoConverterContext, objectEntry.getId(), version);
	}

	@Override
	public Page<ObjectEntry> getObjectEntryRelatedObjectEntries(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, Long objectEntryId,
			String objectRelationshipName, Pagination pagination)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectDefinition.getObjectDefinitionId(),
				objectRelationshipName);

		ObjectDefinition relatedObjectDefinition = _getRelatedObjectDefinition(
			objectDefinition, objectRelationship);

		ObjectRelatedModelsProvider objectRelatedModelsProvider =
			_objectRelatedModelsProviderRegistry.getObjectRelatedModelsProvider(
				relatedObjectDefinition.getClassName(),
				relatedObjectDefinition.getCompanyId(),
				objectRelationship.getType());

		if (objectDefinition.isUnmodifiableSystemObject()) {
			return _getSystemObjectRelatedObjectEntries(
				dtoConverterContext, objectDefinition, objectEntryId,
				objectRelationship, objectRelatedModelsProvider, pagination);
		}

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(objectEntryId);

		long groupId = serviceBuilderObjectEntry.getGroupId();

		if (Objects.equals(
				relatedObjectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_COMPANY)) {

			groupId = 0;
		}

		return Page.of(
			HashMapBuilder.put(
				"get",
				ActionUtil.addAction(
					ActionKeys.VIEW,
					ObjectEntryRelatedObjectsResourceImpl.class, objectEntryId,
					"getCurrentObjectEntriesObjectRelationshipNamePage", null,
					serviceBuilderObjectEntry.getUserId(),
					objectDefinition.getClassName(), groupId,
					dtoConverterContext.getUriInfo())
			).build(),
			_toObjectEntries(
				dtoConverterContext,
				objectRelatedModelsProvider.getRelatedModels(
					groupId, objectRelationship.getObjectRelationshipId(),
					serviceBuilderObjectEntry.getPrimaryKey(), null,
					_getStartPosition(pagination),
					_getEndPosition(pagination))),
			pagination,
			objectRelatedModelsProvider.getRelatedModelsCount(
				groupId, objectRelationship.getObjectRelationshipId(),
				serviceBuilderObjectEntry.getPrimaryKey(), null));
	}

	@Override
	public Page<Object> getRelatedSystemObjectEntries(
			ObjectDefinition objectDefinition, Long objectEntryId,
			String objectRelationshipName, Pagination pagination)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipService.getObjectRelationship(
				objectDefinition.getObjectDefinitionId(),
				objectRelationshipName);

		ObjectDefinition relatedObjectDefinition = _getRelatedObjectDefinition(
			objectDefinition, objectRelationship);

		ObjectRelatedModelsProvider objectRelatedModelsProvider =
			_objectRelatedModelsProviderRegistry.getObjectRelatedModelsProvider(
				relatedObjectDefinition.getClassName(),
				relatedObjectDefinition.getCompanyId(),
				objectRelationship.getType());

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(objectEntryId);

		return Page.of(
			TransformUtil.transform(
				(List<BaseModel<?>>)
					objectRelatedModelsProvider.getRelatedModels(
						serviceBuilderObjectEntry.getGroupId(),
						objectRelationship.getObjectRelationshipId(),
						serviceBuilderObjectEntry.getPrimaryKey(), null,
						_getStartPosition(pagination),
						_getEndPosition(pagination)),
				baseModel -> _toDTO(
					baseModel, serviceBuilderObjectEntry,
					_systemObjectDefinitionManagerRegistry.
						getSystemObjectDefinitionManager(
							relatedObjectDefinition.getName()))),
			pagination,
			objectRelatedModelsProvider.getRelatedModelsCount(
				serviceBuilderObjectEntry.getGroupId(),
				objectRelationship.getObjectRelationshipId(),
				serviceBuilderObjectEntry.getPrimaryKey(), null));
	}

	@Override
	public String getStorageLabel(Locale locale) {
		return language.get(
			locale, ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT);
	}

	@Override
	public String getStorageType() {
		return ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT;
	}

	@Override
	public Object getSystemObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long primaryKey)
		throws Exception {

		if (!objectDefinition.isUnmodifiableSystemObject()) {
			return null;
		}

		SystemObjectDefinitionManager systemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager(objectDefinition.getName());

		return ObjectEntryDTOConverterUtil.toDTO(
			systemObjectDefinitionManager.getBaseModelByExternalReferenceCode(
				systemObjectDefinitionManager.getBaseModelExternalReferenceCode(
					primaryKey),
				objectDefinition.getCompanyId()),
			_dtoConverterRegistry, systemObjectDefinitionManager,
			dtoConverterContext.getUser());
	}

	@Override
	public Page<ObjectEntry> getVersionedObjectEntries(
			DTOConverterContext dtoConverterContext, long objectEntryId,
			Pagination pagination)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			objectEntryLocalService.getObjectEntry(objectEntryId);

		return Page.of(
			TransformUtil.transform(
				_objectEntryVersionService.getObjectEntryVersions(
					objectEntryId, _getStartPosition(pagination),
					_getEndPosition(pagination)),
				objectEntryVersion -> {
					dtoConverterContext.setAttribute(
						"objectEntryVersion", objectEntryVersion);

					return _objectEntryDTOConverter.toDTO(
						dtoConverterContext, serviceBuilderObjectEntry);
				}),
			pagination,
			_objectEntryVersionService.getObjectEntryVersionsCount(
				objectEntryId));
	}

	@Override
	public Page<ObjectEntry> getVersionedObjectEntries(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			Pagination pagination)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			objectEntryLocalService.getObjectEntry(
				externalReferenceCode,
				objectDefinition.getObjectDefinitionId());

		return getVersionedObjectEntries(
			dtoConverterContext, serviceBuilderObjectEntry.getObjectEntryId(),
			pagination);
	}

	@Override
	public ObjectEntry partialUpdateObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId,
			ObjectEntry objectEntry)
		throws Exception {

		ObjectEntry existingObjectEntry =
			ObjectEntryManagerUtil.partialUpdateObjectEntry(
				getObjectEntry(
					dtoConverterContext, objectDefinition, objectEntryId),
				objectDefinition.getObjectDefinitionId(), objectEntry);

		return _updateObjectEntry(
			dtoConverterContext, objectDefinition, existingObjectEntry,
			objectEntryId, true);
	}

	@Override
	public ObjectEntry restoreObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId, int version)
		throws Exception {

		return _restoreVersionedObjectEntry(
			dtoConverterContext, objectDefinition,
			getObjectEntryByVersion(
				dtoConverterContext, objectEntryId, version));
	}

	@Override
	public ObjectEntry restoreObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			int version)
		throws Exception {

		return _restoreVersionedObjectEntry(
			dtoConverterContext, objectDefinition,
			getObjectEntryByVersion(
				dtoConverterContext, externalReferenceCode, objectDefinition,
				version));
	}

	@Override
	public ObjectEntry updateObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId,
			ObjectEntry objectEntry)
		throws Exception {

		return _updateObjectEntry(
			dtoConverterContext, objectDefinition, objectEntry, objectEntryId,
			false);
	}

	@Override
	public ObjectEntry updateObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry, String scopeKey)
		throws Exception {

		validateReadOnlyObjectFields(
			externalReferenceCode, objectDefinition, objectEntry);

		ServiceContext serviceContext = _createServiceContext(
			dtoConverterContext, objectDefinition, objectEntry, scopeKey);

		serviceContext.setCompanyId(companyId);

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.addOrUpdateObjectEntry(
				externalReferenceCode, getGroupId(objectDefinition, scopeKey),
				objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				_toObjectValues(
					dtoConverterContext.getLocale(), objectDefinition,
					objectEntry, scopeKey, serviceContext),
				serviceContext);

		return _toObjectEntry(
			dtoConverterContext, objectDefinition,
			_addOrUpdateNestedObjectEntries(
				dtoConverterContext, objectDefinition, objectEntry,
				_getObjectRelationships(objectDefinition, objectEntry),
				serviceBuilderObjectEntry, scopeKey));
	}

	@Override
	public void validateObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			List<String> objectValidationRuleExternalReferenceCodes,
			String scopeKey)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			objectEntryLocalService.createObjectEntry(0L);

		serviceBuilderObjectEntry.setObjectDefinitionId(
			objectDefinition.getObjectDefinitionId());
		serviceBuilderObjectEntry.setDefaultLanguageId(
			_language.getLanguageId(
				_portal.getSiteDefaultLocale(GetterUtil.getLong(scopeKey))));
		serviceBuilderObjectEntry.setStatus(WorkflowConstants.STATUS_APPROVED);
		serviceBuilderObjectEntry.setValues(
			_toObjectValues(
				dtoConverterContext.getLocale(), objectDefinition, objectEntry,
				scopeKey,
				_createServiceContext(
					dtoConverterContext, objectDefinition, objectEntry,
					scopeKey)));

		_objectEntryService.validate(
			getGroupId(objectDefinition, scopeKey), serviceBuilderObjectEntry,
			objectValidationRuleExternalReferenceCodes,
			_createServiceContext(
				dtoConverterContext, objectDefinition, objectEntry, scopeKey));
	}

	private Map<String, String> _addAction(
			String actionName, String methodName,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry,
			HashMap<String, String> templateParameterMap, UriInfo uriInfo)
		throws Exception {

		return ActionUtil.addAction(
			actionName, ObjectEntryResourceImpl.class,
			serviceBuilderObjectEntry.getObjectEntryId(), methodName, null,
			_objectEntryService.getModelResourcePermission(
				serviceBuilderObjectEntry.getObjectDefinitionId()),
			templateParameterMap, uriInfo);
	}

	private Map<String, String> _addAction(
			String actionName, String methodName,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry,
			UriInfo uriInfo)
		throws Exception {

		return _addAction(
			actionName, methodName, serviceBuilderObjectEntry, null, uriInfo);
	}

	private com.liferay.object.model.ObjectEntry
			_addOrUpdateNestedObjectEntries(
				DTOConverterContext dtoConverterContext,
				ObjectDefinition objectDefinition, ObjectEntry objectEntry,
				Map<String, ObjectRelationship> objectRelationships,
				com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry,
				String scopeKey)
		throws Exception {

		if (objectRelationships.isEmpty()) {
			return serviceBuilderObjectEntry;
		}

		Map<String, Object> properties = objectEntry.getProperties();

		for (Map.Entry<String, ObjectRelationship> entry :
				objectRelationships.entrySet()) {

			ObjectRelationship objectRelationship = objectRelationships.get(
				entry.getKey());

			ObjectDefinition relatedObjectDefinition =
				_getRelatedObjectDefinition(
					objectDefinition, objectRelationship);

			ObjectRelationshipElementsParser objectRelationshipElementsParser =
				_objectRelationshipElementsParserRegistry.
					getObjectRelationshipElementsParser(
						relatedObjectDefinition.getClassName(),
						relatedObjectDefinition.getCompanyId(),
						objectRelationship.getType());

			List<?> nestedObjectEntries =
				objectRelationshipElementsParser.parse(
					objectRelationship, properties.get(entry.getKey()));

			List<String> nestedExternalReferenceCodes = new ArrayList<>();

			if (relatedObjectDefinition.isUnmodifiableSystemObject()) {
				SystemObjectDefinitionManager systemObjectDefinitionManager =
					_systemObjectDefinitionManagerRegistry.
						getSystemObjectDefinitionManager(
							relatedObjectDefinition.getName());

				for (Object item : nestedObjectEntries) {
					Map<String, Object> nestedObjectEntry =
						(Map<String, Object>)item;

					long nestedObjectEntryId =
						systemObjectDefinitionManager.upsertBaseModel(
							String.valueOf(
								nestedObjectEntry.get("externalReferenceCode")),
							relatedObjectDefinition.getCompanyId(),
							dtoConverterContext.getUser(), nestedObjectEntry);

					_relateNestedObjectEntry(
						objectDefinition, objectRelationship,
						serviceBuilderObjectEntry.getPrimaryKey(),
						nestedObjectEntryId, new ServiceContext());

					nestedExternalReferenceCodes.add(
						systemObjectDefinitionManager.
							getBaseModelExternalReferenceCode(
								nestedObjectEntryId));
				}
			}
			else {
				ObjectEntryManager objectEntryManager =
					_objectEntryManagerRegistry.getObjectEntryManager(
						relatedObjectDefinition.getStorageType());

				boolean manyToOneObjectRelationship =
					_isManyToOneObjectRelationship(
						objectDefinition, objectRelationship,
						relatedObjectDefinition);

				for (Object item : nestedObjectEntries) {
					ObjectEntry nestedObjectEntry = (ObjectEntry)item;

					if (manyToOneObjectRelationship) {
						Map<String, Object> nestedObjectEntryProperties =
							nestedObjectEntry.getProperties();

						nestedObjectEntryProperties.put(
							ObjectRelationshipUtil.
								getObjectRelationshipFieldName(
									objectDefinition,
									objectRelationship.getName()),
							serviceBuilderObjectEntry.getPrimaryKey());
					}

					try {
						nestedObjectEntry =
							objectEntryManager.updateObjectEntry(
								objectDefinition.getCompanyId(),
								dtoConverterContext,
								nestedObjectEntry.getExternalReferenceCode(),
								relatedObjectDefinition, nestedObjectEntry,
								scopeKey);
					}
					catch (ObjectEntryValuesException.Required
								objectEntryValuesException) {

						if (!LazyReferencingThreadLocal.isEnabled()) {
							throw objectEntryValuesException;
						}

						long groupId = 0;

						if ((serviceBuilderObjectEntry.getGroupId() > 0) &&
							Objects.equals(
								relatedObjectDefinition.getScope(),
								ObjectDefinitionConstants.SCOPE_SITE)) {

							groupId = serviceBuilderObjectEntry.getGroupId();
						}

						nestedObjectEntry = _toObjectEntry(
							dtoConverterContext, relatedObjectDefinition,
							objectEntryLocalService.
								getOrAddIncompleteObjectEntry(
									nestedObjectEntry.
										getExternalReferenceCode(),
									groupId, dtoConverterContext.getUserId(),
									relatedObjectDefinition.
										getObjectDefinitionId()));
					}

					if (!manyToOneObjectRelationship) {
						_relateNestedObjectEntry(
							objectDefinition, objectRelationship,
							serviceBuilderObjectEntry.getPrimaryKey(),
							nestedObjectEntry.getId(),
							ServiceContextUtil.createServiceContext(
								objectDefinition.getCompanyId(),
								getGroupId(objectDefinition, scopeKey),
								nestedObjectEntry,
								dtoConverterContext.getUserId()));
					}

					nestedExternalReferenceCodes.add(
						nestedObjectEntry.getExternalReferenceCode());
				}
			}

			long[] toDisassociatePrimaryKeys =
				TransformUtil.transformToLongArray(
					_getRelatedModels(
						objectDefinition, objectRelationship,
						serviceBuilderObjectEntry.getPrimaryKey(),
						relatedObjectDefinition),
					relatedModel -> {
						ExternalReferenceCodeModel externalReferenceCodeModel =
							(ExternalReferenceCodeModel)relatedModel;

						if (nestedExternalReferenceCodes.contains(
								externalReferenceCodeModel.
									getExternalReferenceCode())) {

							return null;
						}

						return relatedModel.getPrimaryKeyObj();
					});

			if (toDisassociatePrimaryKeys.length > 0) {
				_disassociateRelatedModels(
					objectDefinition, objectRelationship,
					serviceBuilderObjectEntry.getPrimaryKey(),
					toDisassociatePrimaryKeys, relatedObjectDefinition,
					dtoConverterContext.getUserId());
			}

			if (properties.containsKey(entry.getKey())) {
				NestedFieldsSupplier.addNestedField(entry.getKey());
			}
		}

		return objectEntryLocalService.getObjectEntry(
			serviceBuilderObjectEntry.getPrimaryKey());
	}

	private void _checkObjectEntryObjectDefinitionId(
			ObjectDefinition objectDefinition,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry)
		throws Exception {

		if (objectDefinition.getObjectDefinitionId() !=
				serviceBuilderObjectEntry.getObjectDefinitionId()) {

			throw new NoSuchObjectEntryException();
		}
	}

	private ObjectEntry _copyVersionedObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws Exception {

		objectEntry.setExternalReferenceCode(() -> null);
		objectEntry.setId(() -> null);

		_removeReadOnlyProperties(objectDefinition, objectEntry);

		return addObjectEntry(
			dtoConverterContext, objectDefinition, objectEntry,
			objectEntry.getScopeKey());
	}

	private ServiceContext _createServiceContext(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			String scopeKey)
		throws Exception {

		ModelPermissions modelPermissions =
			ModelPermissionsUtil.toModelPermissions(
				objectDefinition.getCompanyId(), objectEntry.getPermissions(),
				GetterUtil.getLong(objectEntry.getId()),
				objectDefinition.getClassName(), _resourceActionLocalService,
				_resourcePermissionLocalService, _roleLocalService);

		return ServiceContextUtil.createServiceContext(
			objectDefinition.getCompanyId(),
			getGroupId(objectDefinition, scopeKey),
			dtoConverterContext.getLocale(), modelPermissions, objectEntry,
			dtoConverterContext.getUserId());
	}

	private byte[] _decode(String fileBase64) {
		try {
			return Base64.decode(fileBase64);
		}
		catch (Exception exception) {
			throw new IllegalArgumentException(
				"Unable to decode Base64 file", exception);
		}
	}

	private void _disassociateRelatedModels(
			ObjectDefinition objectDefinition,
			ObjectRelationship objectRelationship, long primaryKey1,
			long[] primaryKeys2, ObjectDefinition relatedObjectDefinition,
			long userId)
		throws Exception {

		ObjectRelatedModelsProvider<?> objectRelatedModelsProvider = null;

		if (_isManyToOneObjectRelationship(
				relatedObjectDefinition, objectRelationship,
				objectDefinition)) {

			objectRelatedModelsProvider =
				_objectRelatedModelsProviderRegistry.
					getObjectRelatedModelsProvider(
						objectDefinition.getClassName(),
						objectDefinition.getCompanyId(),
						objectRelationship.getType());

			objectRelatedModelsProvider.disassociateRelatedModels(
				userId, objectRelationship.getObjectRelationshipId(),
				primaryKeys2[0], primaryKey1);
		}
		else {
			objectRelatedModelsProvider =
				_objectRelatedModelsProviderRegistry.
					getObjectRelatedModelsProvider(
						relatedObjectDefinition.getClassName(),
						relatedObjectDefinition.getCompanyId(),
						objectRelationship.getType());

			if ((objectRelationship.getObjectDefinitionId1() !=
					objectDefinition.getObjectDefinitionId()) &&
				Objects.equals(
					ObjectRelationshipConstants.TYPE_MANY_TO_MANY,
					objectRelationship.getType())) {

				objectRelationship =
					_objectRelationshipLocalService.getObjectRelationship(
						objectDefinition.getObjectDefinitionId(),
						objectRelationship.getName());
			}

			for (long primaryKey2 : primaryKeys2) {
				objectRelatedModelsProvider.disassociateRelatedModels(
					userId, objectRelationship.getObjectRelationshipId(),
					primaryKey1, primaryKey2);
			}
		}
	}

	private void _executeObjectAction(
			DTOConverterContext dtoConverterContext, String objectActionName,
			ObjectDefinition objectDefinition,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry)
		throws Exception {

		_objectEntryService.checkModelResourcePermission(
			objectDefinition.getObjectDefinitionId(),
			serviceBuilderObjectEntry.getObjectEntryId(), objectActionName);

		_objectActionEngine.executeObjectAction(
			objectActionName, ObjectActionTriggerConstants.KEY_STANDALONE,
			objectDefinition.getObjectDefinitionId(),
			JSONUtil.put(
				"classPK", serviceBuilderObjectEntry.getObjectEntryId()
			).put(
				"objectEntry",
				HashMapBuilder.putAll(
					serviceBuilderObjectEntry.getModelAttributes()
				).put(
					"values", serviceBuilderObjectEntry.getValues()
				).build()
			).put(
				"objectEntryDTO" + objectDefinition.getShortName(),
				() -> {
					dtoConverterContext.setAttribute(
						"addActions", Boolean.FALSE);

					JSONObject jsonObject = _jsonFactory.createJSONObject(
						_jsonFactory.looseSerializeDeep(
							_toObjectEntry(
								dtoConverterContext, objectDefinition,
								serviceBuilderObjectEntry)));

					return jsonObject.toMap();
				}
			),
			dtoConverterContext.getUserId());
	}

	private ObjectEntry _expireObjectEntryVersion(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry,
			int version)
		throws Exception {

		_checkObjectEntryObjectDefinitionId(
			objectDefinition, serviceBuilderObjectEntry);

		_objectEntryVersionService.expireObjectEntryVersion(
			serviceBuilderObjectEntry,
			ServiceContextUtil.createServiceContext(
				serviceBuilderObjectEntry.getObjectEntryId()),
			dtoConverterContext.getUserId(), version);

		dtoConverterContext.setAttribute(
			"objectEntryVersion",
			_objectEntryVersionService.getObjectEntryVersion(
				serviceBuilderObjectEntry.getObjectEntryId(), version));

		return _objectEntryDTOConverter.toDTO(
			dtoConverterContext, serviceBuilderObjectEntry);
	}

	private String _getDateString(Date date) {
		if (date == null) {
			return StringPool.BLANK;
		}

		return DateUtil.getDate(date, "yyyy-MM-dd HH:mm", LocaleUtil.US);
	}

	private int _getEndPosition(Pagination pagination) {
		if (pagination != null) {
			return pagination.getEndPosition();
		}

		return QueryUtil.ALL_POS;
	}

	private long _getFileEntryGroupId(
		String groupExternalReferenceCode, ObjectDefinition objectDefinition,
		String scopeKey) {

		if (Validator.isNotNull(groupExternalReferenceCode)) {
			Group group = groupLocalService.fetchGroupByExternalReferenceCode(
				groupExternalReferenceCode, objectDefinition.getCompanyId());

			if (group != null) {
				return group.getGroupId();
			}
		}

		return getGroupId(objectDefinition, scopeKey, true);
	}

	private BaseModel<ExternalReferenceCodeModel> _getManyToOneRelatedModel(
			ObjectRelationship objectRelationship, long primaryKey,
			ObjectDefinition relatedObjectDefinition)
		throws Exception {

		if (relatedObjectDefinition.isUnmodifiableSystemObject()) {
			ObjectRelatedModelsProvider objectRelatedModelsProvider =
				_objectRelatedModelsProviderRegistry.
					getObjectRelatedModelsProvider(
						relatedObjectDefinition.getClassName(),
						relatedObjectDefinition.getCompanyId(),
						objectRelationship.getType());

			return objectRelatedModelsProvider.fetchRelatedModel(
				relatedObjectDefinition.getCompanyId(),
				objectRelationship.getObjectRelationshipId(), primaryKey);
		}

		ObjectRelatedModelsProvider objectRelatedModelsProvider =
			_objectRelatedModelsProviderRegistry.getObjectRelatedModelsProvider(
				relatedObjectDefinition.getClassName(),
				relatedObjectDefinition.getCompanyId(),
				objectRelationship.getType());

		return objectRelatedModelsProvider.fetchRelatedModel(
			GroupThreadLocal.getGroupId(),
			objectRelationship.getObjectRelationshipId(), primaryKey);
	}

	private ObjectEntry _getObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(objectEntryId);

		_checkObjectEntryObjectDefinitionId(
			objectDefinition, serviceBuilderObjectEntry);

		return _toObjectEntry(
			dtoConverterContext, objectDefinition, serviceBuilderObjectEntry);
	}

	private long _getObjectEntryFolderId(
		long companyId, long groupId, ObjectEntry objectEntry) {

		String objectEntryFolderExternalReferenceCode =
			objectEntry.getObjectEntryFolderExternalReferenceCode();

		if (Validator.isNull(objectEntryFolderExternalReferenceCode)) {
			return ObjectEntryFolderConstants.
				PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT;
		}

		try {
			ObjectEntryFolder objectEntryFolder =
				_objectEntryFolderLocalService.
					getObjectEntryFolderByExternalReferenceCode(
						objectEntryFolderExternalReferenceCode, groupId,
						companyId);

			return objectEntryFolder.getObjectEntryFolderId();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT;
	}

	private Map<String, ObjectRelationship> _getObjectRelationships(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws Exception {

		Map<String, ObjectRelationship> objectRelationships =
			new LinkedHashMap<>();

		Map<String, Object> properties = objectEntry.getProperties();

		for (String key : properties.keySet()) {
			ObjectRelationship objectRelationship =
				_objectRelationshipLocalService.
					fetchObjectRelationshipByObjectDefinitionId(
						objectDefinition.getObjectDefinitionId(), key);

			if (objectRelationship != null) {
				objectRelationships.put(key, objectRelationship);
			}
		}

		return objectRelationships;
	}

	private List<? extends BaseModel<?>> _getRelatedModels(
			ObjectDefinition objectDefinition,
			ObjectRelationship objectRelationship, long primaryKey,
			ObjectDefinition relatedObjectDefinition)
		throws Exception {

		if (_isManyToOneObjectRelationship(
				relatedObjectDefinition, objectRelationship,
				objectDefinition)) {

			BaseModel<?> baseModel = _getManyToOneRelatedModel(
				objectRelationship, primaryKey, relatedObjectDefinition);

			if (baseModel == null) {
				return new ArrayList<>();
			}

			return Collections.singletonList(baseModel);
		}

		ObjectRelatedModelsProvider<?> objectRelatedModelsProvider =
			_objectRelatedModelsProviderRegistry.getObjectRelatedModelsProvider(
				relatedObjectDefinition.getClassName(),
				relatedObjectDefinition.getCompanyId(),
				objectRelationship.getType());

		if ((objectRelationship.getObjectDefinitionId1() !=
				objectDefinition.getObjectDefinitionId()) &&
			Objects.equals(
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY,
				objectRelationship.getType())) {

			objectRelationship =
				_objectRelationshipLocalService.getObjectRelationship(
					objectDefinition.getObjectDefinitionId(),
					objectRelationship.getName());
		}

		return objectRelatedModelsProvider.getRelatedModels(
			GroupThreadLocal.getGroupId(),
			objectRelationship.getObjectRelationshipId(), primaryKey, null, -1,
			-1);
	}

	private ObjectDefinition _getRelatedObjectDefinition(
			ObjectDefinition objectDefinition,
			ObjectRelationship objectRelationship)
		throws Exception {

		ObjectDefinition relatedObjectDefinition =
			ObjectRelationshipUtil.getRelatedObjectDefinition(
				objectDefinition, objectRelationship);

		if (!relatedObjectDefinition.isActive()) {
			throw new BadRequestException(
				"Object definition " +
					relatedObjectDefinition.getObjectDefinitionId() +
						" is inactive");
		}

		return relatedObjectDefinition;
	}

	private int _getStartPosition(Pagination pagination) {
		if (pagination != null) {
			return pagination.getStartPosition();
		}

		return QueryUtil.ALL_POS;
	}

	private Page<ObjectEntry> _getSystemObjectRelatedObjectEntries(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId,
			ObjectRelationship objectRelationship,
			ObjectRelatedModelsProvider objectRelatedModelsProvider,
			Pagination pagination)
		throws Exception {

		long groupId = GroupThreadLocal.getGroupId();

		SystemObjectDefinitionManager systemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager(objectDefinition.getName());

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(
					systemObjectDefinitionManager.getModelClassName());

		PersistedModel persistedModel =
			persistedModelLocalService.getPersistedModel(objectEntryId);

		if (Objects.equals(
				systemObjectDefinitionManager.getScope(),
				ObjectDefinitionConstants.SCOPE_SITE) &&
			(persistedModel instanceof GroupedModel)) {

			GroupedModel groupedModel = (GroupedModel)persistedModel;

			groupId = groupedModel.getGroupId();
		}

		return Page.of(
			Collections.emptyMap(),
			_toObjectEntries(
				dtoConverterContext,
				objectRelatedModelsProvider.getRelatedModels(
					groupId, objectRelationship.getObjectRelationshipId(),
					objectEntryId, null, _getStartPosition(pagination),
					_getEndPosition(pagination))),
			pagination,
			objectRelatedModelsProvider.getRelatedModelsCount(
				groupId, objectRelationship.getObjectRelationshipId(),
				objectEntryId, null));
	}

	private Serializable _getValue(
		Locale locale, ObjectField objectField, Object value) {

		if (Objects.equals(
				objectField.getDBType(), ObjectFieldConstants.DB_TYPE_DATE)) {

			return _toDate(locale, String.valueOf(value));
		}

		return (Serializable)value;
	}

	private boolean _isManyToOneObjectRelationship(
		ObjectDefinition objectDefinition,
		ObjectRelationship objectRelationship,
		ObjectDefinition relatedObjectDefinition) {

		if (Objects.equals(
				objectRelationship.getType(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY) &&
			(objectRelationship.getObjectDefinitionId1() ==
				objectDefinition.getObjectDefinitionId()) &&
			(objectRelationship.getObjectDefinitionId2() ==
				relatedObjectDefinition.getObjectDefinitionId())) {

			return true;
		}

		return false;
	}

	private boolean _isObjectEntryDraft(Status status) {
		if ((status != null) &&
			(status.getCode() == WorkflowConstants.STATUS_DRAFT)) {

			return true;
		}

		return false;
	}

	private void _processAttachment(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			ObjectField objectField, String scopeKey,
			ServiceContext serviceContext)
		throws Exception {

		if (objectField.isLocalized()) {
			Object propertyValue = objectEntry.getPropertyValue(
				objectField.getI18nObjectFieldName());

			if ((propertyValue == null) ||
				!(propertyValue instanceof Map<?, ?>)) {

				return;
			}

			Map<String, Serializable> localizedValues =
				(Map<String, Serializable>)propertyValue;

			for (Map.Entry<String, Serializable> entry :
					localizedValues.entrySet()) {

				long fileEntryId = _processAttachment(
					objectDefinition, objectField, entry.getValue(), scopeKey,
					serviceContext);

				if (fileEntryId > 0) {
					entry.setValue(fileEntryId);
				}
			}

			return;
		}

		long fileEntryId = _processAttachment(
			objectDefinition, objectField,
			objectEntry.getPropertyValue(objectField.getName()), scopeKey,
			serviceContext);

		if (fileEntryId > 0) {
			Map<String, Object> properties = objectEntry.getProperties();

			properties.put(objectField.getName(), fileEntryId);
		}
	}

	private long _processAttachment(
			ObjectDefinition objectDefinition, ObjectField objectField,
			Object propertyValue, String scopeKey,
			ServiceContext serviceContext)
		throws Exception {

		if (propertyValue == null) {
			return 0;
		}

		FileEntry fileEntry = ObjectMapperUtil.readValue(
			FileEntry.class, propertyValue);

		if ((fileEntry == null) ||
			((fileEntry.getExternalReferenceCode() == null) &&
			 (fileEntry.getFileBase64() == null) &&
			 (fileEntry.getFileURL() == null))) {

			return 0;
		}

		String fileSource = ObjectFieldSettingUtil.getValue(
			ObjectFieldSettingConstants.NAME_FILE_SOURCE, objectField);

		if (!StringUtil.equals(
				fileSource, ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA) &&
			!StringUtil.equals(
				fileSource, ObjectFieldSettingConstants.VALUE_USER_COMPUTER)) {

			throw new UnsupportedOperationException(
				"File source " + fileSource + " is not supported");
		}

		byte[] fileContent = {};

		if (fileEntry.getFileBase64() != null) {
			fileContent = _decode(fileEntry.getFileBase64());
		}
		else if ((fileEntry.getFileURL() != null) &&
				 FeatureFlagManagerUtil.isEnabled("LPD-39967")) {

			try {
				URL url = _batchEngineAttachmentManager.getURL(
					fileEntry.getFileURL());

				if (Objects.equals(url.getProtocol(), "file")) {
					throw new UnsupportedOperationException(
						StringBundler.concat(
							"Unable to download file from ",
							fileEntry.getFileURL(), ", unsupported protocol: ",
							url.getProtocol()));
				}

				URLConnection urlConnection = url.openConnection();

				if ((urlConnection instanceof
						HttpURLConnection httpURLConnection) &&
					(httpURLConnection.getResponseCode() !=
						HttpURLConnection.HTTP_OK)) {

					throw new IllegalArgumentException(
						StringBundler.concat(
							"Unable to download file from ",
							fileEntry.getFileURL(), ", unexpected HTTP code: ",
							httpURLConnection.getResponseCode()));
				}

				fileContent = StreamUtil.toByteArray(
					urlConnection.getInputStream());
			}
			catch (IOException ioException) {
				_log.error(ioException);

				throw new IllegalArgumentException(
					"Unable to download file from " + fileEntry.getFileURL(),
					ioException);
			}
		}

		com.liferay.portal.kernel.repository.model.FileEntry
			serviceBuilderFileEntry = null;

		String groupExternalReferenceCode = null;

		Scope scope = fileEntry.getScope();

		if (scope != null) {
			groupExternalReferenceCode = scope.getExternalReferenceCode();
		}

		if (StringUtil.equals(
				fileSource, ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA)) {

			Folder folder = fileEntry.getFolder();

			String folderExternalReferenceCode = null;
			long folderGroupId = 0;

			if ((folder == null) || Validator.isNull(folder.getSiteId())) {
				folderGroupId = _getFileEntryGroupId(
					groupExternalReferenceCode, objectDefinition, scopeKey);
			}
			else {
				folderExternalReferenceCode = folder.getExternalReferenceCode();

				Group group = groupLocalService.getGroup(folder.getSiteId());

				if (group.getCompanyId() != objectField.getCompanyId()) {
					throw new NoSuchGroupException();
				}

				folderGroupId = group.getGroupId();
			}

			serviceBuilderFileEntry = _attachmentManager.getOrAddFileEntry(
				objectField.getCompanyId(),
				fileEntry.getExternalReferenceCode(), fileContent,
				fileEntry.getName(), folderExternalReferenceCode, folderGroupId,
				objectField.getObjectFieldId(), serviceContext);
		}
		else if (StringUtil.equals(
					fileSource,
					ObjectFieldSettingConstants.VALUE_USER_COMPUTER)) {

			serviceBuilderFileEntry = _attachmentManager.getOrAddFileEntry(
				objectField.getCompanyId(),
				fileEntry.getExternalReferenceCode(), fileContent,
				fileEntry.getName(),
				_getFileEntryGroupId(
					groupExternalReferenceCode, objectDefinition, scopeKey),
				objectField.getObjectFieldId(), serviceContext);
		}

		return serviceBuilderFileEntry.getFileEntryId();
	}

	private void _processVulcanAggregation(
		Aggregations aggregations, Queries queries,
		SearchRequestBuilder searchRequestBuilder,
		Aggregation vulcanAggregation) {

		if (vulcanAggregation == null) {
			return;
		}

		Map<String, String> aggregationTerms =
			vulcanAggregation.getAggregationTerms();

		for (Map.Entry<String, String> entry : aggregationTerms.entrySet()) {
			String value = entry.getValue();

			if (!value.startsWith("nestedFieldArray")) {
				continue;
			}

			NestedAggregation nestedAggregation = aggregations.nested(
				entry.getKey(), "nestedFieldArray");

			String[] valueParts = value.split(StringPool.POUND);

			FilterAggregation filterAggregation = aggregations.filter(
				"filterAggregation",
				queries.term("nestedFieldArray.fieldName", valueParts[1]));

			filterAggregation.addChildAggregation(
				aggregations.terms(entry.getKey(), valueParts[0]));

			nestedAggregation.addChildAggregation(filterAggregation);

			searchRequestBuilder.addAggregation(nestedAggregation);
		}
	}

	private void _relateNestedObjectEntry(
			ObjectDefinition objectDefinition,
			ObjectRelationship objectRelationship, long primaryKey,
			long relatedPrimaryKey, ServiceContext serviceContext)
		throws Exception {

		long primaryKey1 = relatedPrimaryKey;
		long primaryKey2 = primaryKey;

		if (objectDefinition.getObjectDefinitionId() ==
				objectRelationship.getObjectDefinitionId1()) {

			primaryKey1 = primaryKey;
			primaryKey2 = relatedPrimaryKey;
		}

		_objectRelationshipService.addObjectRelationshipMappingTableValues(
			objectRelationship.getObjectRelationshipId(), primaryKey1,
			primaryKey2, serviceContext);
	}

	private void _removeReadOnlyProperties(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws Exception {

		Map<String, Object> properties = objectEntry.getProperties();

		List<ObjectField> objectFields =
			_objectFieldLocalService.getCustomObjectFields(
				objectDefinition.getObjectDefinitionId());

		for (ObjectField objectField : objectFields) {
			if (ObjectFieldUtil.isReadOnly(
					ddmExpressionFactory, objectField,
					objectEntry.getProperties())) {

				properties.remove(objectField.getName());
			}
		}
	}

	private ObjectEntry _restoreVersionedObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws Exception {

		_removeReadOnlyProperties(objectDefinition, objectEntry);

		return updateObjectEntry(
			dtoConverterContext, objectDefinition, objectEntry.getId(),
			objectEntry);
	}

	private Date _toDate(Locale locale, String valueString) {
		if (Validator.isNull(valueString)) {
			return null;
		}

		try {
			return DateUtil.parseDate(
				"yyyy-MM-dd'T'HH:mm:ss'Z'", valueString, locale);
		}
		catch (ParseException parseException1) {
			if (_log.isDebugEnabled()) {
				_log.debug(parseException1);
			}

			try {
				return DateUtil.parseDate("yyyy-MM-dd", valueString, locale);
			}
			catch (ParseException parseException2) {
				throw new BadRequestException(
					"Unable to parse date that does not conform to ISO-8601",
					parseException2);
			}
		}
	}

	private Object _toDTO(
			BaseModel<?> baseModel,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry,
			SystemObjectDefinitionManager systemObjectDefinitionManager)
		throws Exception {

		return ObjectEntryDTOConverterUtil.toDTO(
			baseModel, _dtoConverterRegistry, systemObjectDefinitionManager,
			_userLocalService.getUser(serviceBuilderObjectEntry.getUserId()));
	}

	private List<ObjectEntry> _toObjectEntries(
		DTOConverterContext dtoConverterContext,
		List<com.liferay.object.model.ObjectEntry>
			serviceBuilderObjectEntries) {

		return TransformUtil.transform(
			serviceBuilderObjectEntries,
			serviceBuilderObjectEntry -> _toObjectEntry(
				dtoConverterContext,
				_objectDefinitionLocalService.getObjectDefinition(
					serviceBuilderObjectEntry.getObjectDefinitionId()),
				serviceBuilderObjectEntry));
	}

	private ObjectEntry _toObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry)
		throws Exception {

		Map<String, Map<String, String>> actions =
			dtoConverterContext.getActions();

		if (GetterUtil.getBoolean(
				dtoConverterContext.getAttribute("addActions"), true)) {

			if (actions == null) {
				actions = Collections.emptyMap();
			}

			actions = HashMapBuilder.create(
				actions
			).<String, Map<String, String>>put(
				"delete",
				_addAction(
					ActionKeys.DELETE, "deleteObjectEntry",
					serviceBuilderObjectEntry, dtoConverterContext.getUriInfo())
			).put(
				"expire",
				() -> {
					if (!FeatureFlagManagerUtil.isEnabled(
							objectDefinition.getCompanyId(), "LPD-17564") ||
						serviceBuilderObjectEntry.isDraft() ||
						serviceBuilderObjectEntry.isPending()) {

						return null;
					}

					return _addAction(
						ActionKeys.UPDATE, "postObjectEntryExpire",
						serviceBuilderObjectEntry,
						dtoConverterContext.getUriInfo());
				}
			).put(
				"get",
				_addAction(
					ActionKeys.VIEW, "getObjectEntry",
					serviceBuilderObjectEntry, dtoConverterContext.getUriInfo())
			).put(
				"permissions",
				_addAction(
					ActionKeys.PERMISSIONS, "getObjectEntryPermissionsPage",
					serviceBuilderObjectEntry, dtoConverterContext.getUriInfo())
			).put(
				"replace",
				_addAction(
					ActionKeys.UPDATE, "putObjectEntry",
					serviceBuilderObjectEntry, dtoConverterContext.getUriInfo())
			).put(
				"update",
				_addAction(
					ActionKeys.UPDATE, "patchObjectEntry",
					serviceBuilderObjectEntry, dtoConverterContext.getUriInfo())
			).build();

			String methodName = null;

			boolean scopeSite = Objects.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_SITE);

			if (scopeSite) {
				methodName =
					"putScopeScopeKeyByExternalReferenceCodeObjectAction" +
						"ObjectActionName";
			}
			else {
				methodName =
					"putByExternalReferenceCodeObjectActionObjectActionName";
			}

			for (ObjectAction objectAction :
					_objectActionLocalService.getObjectActions(
						objectDefinition.getObjectDefinitionId(),
						ObjectActionTriggerConstants.KEY_STANDALONE)) {

				actions.put(
					objectAction.getName(),
					_addAction(
						objectAction.getName(), methodName,
						serviceBuilderObjectEntry,
						HashMapBuilder.put(
							() -> {
								if (scopeSite) {
									return "scopeKey";
								}

								return null;
							},
							() -> {
								if (!scopeSite) {
									return null;
								}

								return String.valueOf(
									serviceBuilderObjectEntry.getGroupId());
							}
						).put(
							"externalReferenceCode",
							serviceBuilderObjectEntry.getExternalReferenceCode()
						).put(
							"objectActionName", objectAction.getName()
						).build(),
						dtoConverterContext.getUriInfo()));
			}
		}

		DefaultDTOConverterContext defaultDTOConverterContext =
			new DefaultDTOConverterContext(
				dtoConverterContext.isAcceptAllLanguages(), actions,
				dtoConverterContext.getDTOConverterRegistry(),
				dtoConverterContext.getHttpServletRequest(),
				serviceBuilderObjectEntry.getObjectEntryId(),
				dtoConverterContext.getLocale(),
				dtoConverterContext.getUriInfo(),
				dtoConverterContext.getUser());

		defaultDTOConverterContext.setAttribute(
			"objectDefinition", objectDefinition);

		return _objectEntryDTOConverter.toDTO(
			defaultDTOConverterContext, serviceBuilderObjectEntry);
	}

	private Map<String, Serializable> _toObjectValues(
			Locale locale, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry, String scopeKey,
			ServiceContext serviceContext)
		throws Exception {

		Map<String, Serializable> values = new HashMap<>();

		Map<String, Object> properties = HashMapBuilder.<String, Object>putAll(
			objectEntry.getProperties()
		).put(
			"displayDate", _getDateString(objectEntry.getDisplayDate())
		).put(
			"expirationDate", _getDateString(objectEntry.getExpirationDate())
		).put(
			"reviewDate", _getDateString(objectEntry.getReviewDate())
		).build();

		for (ObjectField objectField :
				objectFieldLocalService.getObjectFields(
					objectDefinition.getObjectDefinitionId())) {

			if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

				_processAttachment(
					objectDefinition, objectEntry, objectField, scopeKey,
					serviceContext);
			}

			Object value = ObjectEntryValuesUtil.getValue(
				objectEntry.getScopeId(), _objectDefinitionLocalService,
				objectEntryLocalService, objectField,
				_objectFieldBusinessTypeRegistry, serviceContext.getUserId(),
				properties);

			if (Objects.equals(
					objectField.getName(), "externalReferenceCode") &&
				Validator.isNull(value) &&
				Validator.isNotNull(objectEntry.getExternalReferenceCode())) {

				values.put(
					objectField.getName(),
					(Serializable)objectEntry.getExternalReferenceCode());

				continue;
			}

			if (objectField.isLocalized()) {
				ObjectFieldBusinessType objectFieldBusinessType =
					_objectFieldBusinessTypeRegistry.getObjectFieldBusinessType(
						objectField.getBusinessType());

				Map<String, Object> localizedValues =
					objectFieldBusinessType.getLocalizedValues(
						objectField, serviceContext.getUserId(), properties);

				if (localizedValues != null) {
					values.put(
						objectField.getI18nObjectFieldName(),
						(Serializable)localizedValues);
				}
				else if (value != null) {
					String defaultLanguageId =
						objectEntry.getDefaultLanguageId();

					if (Validator.isNull(defaultLanguageId)) {
						defaultLanguageId = _language.getLanguageId(
							_portal.getSiteDefaultLocale(
								GetterUtil.getLong(scopeKey)));
					}

					values.put(
						objectField.getI18nObjectFieldName(),
						HashMapBuilder.put(
							defaultLanguageId,
							_getValue(locale, objectField, value)
						).build());
				}

				continue;
			}

			if (!Objects.equals(
					objectField.getDBType(),
					ObjectFieldConstants.DB_TYPE_DATE_TIME) &&
				(value == null) &&
				(!objectField.isRequired() ||
				 _isObjectEntryDraft(objectEntry.getStatus()))) {

				continue;
			}

			values.put(
				objectField.getName(), _getValue(locale, objectField, value));
		}

		return values;
	}

	private ObjectEntry _updateObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			long objectEntryId, boolean partialUpdate)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(objectEntryId);

		_checkObjectEntryObjectDefinitionId(
			objectDefinition, serviceBuilderObjectEntry);

		validateReadOnlyObjectFields(
			serviceBuilderObjectEntry.getExternalReferenceCode(),
			objectDefinition, objectEntry);

		String scopeKey = String.valueOf(
			serviceBuilderObjectEntry.getGroupId());

		ServiceContext serviceContext = _createServiceContext(
			dtoConverterContext, objectDefinition, objectEntry, scopeKey);

		if (partialUpdate) {
			serviceBuilderObjectEntry =
				_objectEntryService.partialUpdateObjectEntry(
					objectEntryId,
					_toObjectValues(
						dtoConverterContext.getLocale(), objectDefinition,
						objectEntry, scopeKey, serviceContext),
					serviceContext);
		}
		else {
			serviceBuilderObjectEntry = _objectEntryService.updateObjectEntry(
				objectEntryId,
				_toObjectValues(
					dtoConverterContext.getLocale(), objectDefinition,
					objectEntry, scopeKey, serviceContext),
				serviceContext);
		}

		return _toObjectEntry(
			dtoConverterContext, objectDefinition,
			_addOrUpdateNestedObjectEntries(
				dtoConverterContext, objectDefinition, objectEntry,
				_getObjectRelationships(objectDefinition, objectEntry),
				serviceBuilderObjectEntry, scopeKey));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultObjectEntryManagerImpl.class);

	@Reference
	private Aggregations _aggregations;

	@Reference
	private AttachmentManager _attachmentManager;

	@Reference
	private BatchEngineAttachmentManager _batchEngineAttachmentManager;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private ObjectActionEngine _objectActionEngine;

	@Reference
	private ObjectActionLocalService _objectActionLocalService;

	@Reference
	private ObjectDefinitionFilterParser _objectDefinitionFilterParser;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(
		target = "(component.name=com.liferay.object.rest.internal.dto.v1_0.converter.ObjectEntryDTOConverter)"
	)
	private DTOConverter<com.liferay.object.model.ObjectEntry, ObjectEntry>
		_objectEntryDTOConverter;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private ObjectEntryVersionService _objectEntryVersionService;

	@Reference
	private ObjectFieldBusinessTypeRegistry _objectFieldBusinessTypeRegistry;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;

	@Reference
	private ObjectRelationshipElementsParserRegistry
		_objectRelationshipElementsParserRegistry;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ObjectRelationshipService _objectRelationshipService;

	@Reference
	private Portal _portal;

	@Reference
	private Queries _queries;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Reference
	private UserLocalService _userLocalService;

}