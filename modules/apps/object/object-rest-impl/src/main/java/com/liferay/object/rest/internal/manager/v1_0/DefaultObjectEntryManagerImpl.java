/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.manager.v1_0;

import com.liferay.account.exception.NoSuchGroupException;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryModel;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.exportimport.attachment.ExportImportAttachmentManager;
import com.liferay.object.action.engine.ObjectActionEngine;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.entry.folder.subscription.util.ObjectEntryFolderSubscriptionUtil;
import com.liferay.object.entry.util.ObjectEntryDTOConverterUtil;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.field.attachment.AttachmentManager;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.model.ObjectRelationshipModel;
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
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectEntryVersionLocalService;
import com.liferay.object.service.ObjectEntryVersionService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectRelationshipService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
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
import com.liferay.portal.kernel.model.role.RoleConstants;
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
import com.liferay.portal.kernel.service.RoleService;
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
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UniqueUtil;
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
import com.liferay.portal.vulcan.permission.Permission;
import com.liferay.portal.vulcan.util.ActionUtil;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;
import com.liferay.roles.admin.role.type.contributor.provider.RoleTypeContributorProvider;
import com.liferay.sharing.configuration.SharingConfiguration;
import com.liferay.sharing.configuration.SharingConfigurationFactory;
import com.liferay.subscription.service.SubscriptionLocalService;
import com.liferay.trash.TrashHelper;

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

		validateReadOnlyObjectFields(
			null, getGroupId(objectDefinition, scopeKey), objectDefinition,
			objectEntry);

		ServiceContext serviceContext = _createServiceContext(
			dtoConverterContext, objectDefinition, objectEntry, scopeKey);

		Map<String, Serializable> values = _toObjectValues(
			0L, dtoConverterContext.getLocale(), objectDefinition, objectEntry,
			scopeKey, serviceContext);

		return _addObjectEntry(
			dtoConverterContext, objectDefinition, objectEntry, scopeKey,
			serviceContext, values);
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
	public ObjectEntry addRelatedObjectEntry(
			DTOConverterContext dtoConverterContext, ObjectEntry objectEntry,
			long objectEntryId, ObjectRelationship objectRelationship)
		throws Exception {

		return _addRelatedObjectEntry(
			dtoConverterContext, objectEntry, objectEntryId, objectRelationship,
			null);
	}

	@Override
	public ObjectEntry addRelatedObjectEntry(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectEntry objectEntry,
			ObjectRelationship objectRelationship, String scopeKey)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId1());

		com.liferay.object.model.ObjectEntry parentObjectEntry =
			_objectEntryService.getObjectEntry(
				externalReferenceCode, getGroupId(objectDefinition, scopeKey),
				objectDefinition.getObjectDefinitionId());

		return _addRelatedObjectEntry(
			dtoConverterContext, objectEntry,
			parentObjectEntry.getObjectEntryId(), objectRelationship, scopeKey);
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

		ObjectEntry objectEntry = _getObjectEntryByVersion(
			dtoConverterContext, objectEntryId, version);

		return _copyVersionedObjectEntry(
			dtoConverterContext, objectDefinition, objectEntry,
			_objectEntryService.getObjectEntry(objectEntryId));
	}

	@Override
	public ObjectEntry copyObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey, int version)
		throws Exception {

		if (!objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = _getObjectEntryByVersion(
			dtoConverterContext, externalReferenceCode, objectDefinition,
			scopeKey, version);

		return _copyVersionedObjectEntry(
			dtoConverterContext, objectDefinition, objectEntry,
			_objectEntryService.getObjectEntry(
				externalReferenceCode, getGroupId(objectDefinition, scopeKey),
				objectDefinition.getObjectDefinitionId()));
	}

	@Override
	public void deleteObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(
				externalReferenceCode, getGroupId(objectDefinition, scopeKey),
				objectDefinition.getObjectDefinitionId());

		_deleteObjectEntry(objectDefinition, serviceBuilderObjectEntry);
	}

	@Override
	public void deleteObjectEntry(
			ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(objectEntryId);

		_deleteObjectEntry(objectDefinition, serviceBuilderObjectEntry);
	}

	@Override
	public void deleteObjectEntryByVersion(
			ObjectDefinition objectDefinition, long objectEntryId, int version)
		throws Exception {

		if (!objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(objectEntryId);

		_checkHeadObjectEntry(serviceBuilderObjectEntry);
		_checkObjectEntryObjectDefinitionId(
			objectDefinition, serviceBuilderObjectEntry);
		_checkObjectEntryStatus(serviceBuilderObjectEntry);

		_objectEntryVersionService.deleteObjectEntryVersion(
			objectEntryId, version);
	}

	@Override
	public void deleteObjectEntryByVersion(
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey, int version)
		throws Exception {

		if (!objectDefinition.isEnableObjectEntryVersioning()) {
			throw new UnsupportedOperationException();
		}

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(
				externalReferenceCode, getGroupId(objectDefinition, scopeKey),
				objectDefinition.getObjectDefinitionId());

		_checkHeadObjectEntry(serviceBuilderObjectEntry);
		_checkObjectEntryObjectDefinitionId(
			objectDefinition, serviceBuilderObjectEntry);
		_checkObjectEntryStatus(serviceBuilderObjectEntry);

		_objectEntryVersionService.deleteObjectEntryVersion(
			serviceBuilderObjectEntry.getObjectEntryId(), version);
	}

	@Override
	public void deleteRelatedObjectEntry(
			long objectEntryId, ObjectRelationship objectRelationship,
			long parentObjectEntryId)
		throws Exception {

		_deleteRelateObjectEntry(
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2()),
			objectRelationship,
			_objectEntryService.getObjectEntry(objectEntryId),
			parentObjectEntryId);
	}

	@Override
	public void deleteRelatedObjectEntry(
			String externalReferenceCode, ObjectRelationship objectRelationship,
			String parentExternalReferenceCode, String scopeKey)
		throws Exception {

		ObjectDefinition objectDefinition1 =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId1());

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(
				parentExternalReferenceCode,
				getGroupId(objectDefinition1, scopeKey),
				objectDefinition1.getObjectDefinitionId());

		ObjectDefinition objectDefinition2 =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2());

		_deleteRelateObjectEntry(
			objectDefinition2, objectRelationship,
			_objectEntryService.getObjectEntry(
				externalReferenceCode, getGroupId(objectDefinition2, scopeKey),
				objectDefinition2.getObjectDefinitionId()),
			serviceBuilderObjectEntry.getObjectEntryId());
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
				externalReferenceCode, getGroupId(objectDefinition, scopeKey),
				objectDefinition.getObjectDefinitionId()));
	}

	@Override
	public ObjectEntry expireObjectEntry(
			DTOConverterContext dtoConverterContext, long objectEntryId)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.expireObjectEntry(
				objectEntryId,
				ServiceContextUtil.createServiceContext(objectEntryId));

		_checkObjectEntryStatus(serviceBuilderObjectEntry);

		_objectEntryVersionService.expireObjectEntryVersions(
			serviceBuilderObjectEntry,
			ServiceContextUtil.createServiceContext(objectEntryId));

		return _objectEntryDTOConverter.toDTO(
			dtoConverterContext, serviceBuilderObjectEntry);
	}

	@Override
	public ObjectEntry expireObjectEntry(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(
				externalReferenceCode, getGroupId(objectDefinition, scopeKey),
				objectDefinition.getObjectDefinitionId());

		_checkObjectEntryStatus(serviceBuilderObjectEntry);

		return expireObjectEntry(
			dtoConverterContext, serviceBuilderObjectEntry.getObjectEntryId());
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
			String scopeKey, int version)
		throws Exception {

		return _expireObjectEntryVersion(
			dtoConverterContext, objectDefinition,
			_objectEntryService.getObjectEntry(
				externalReferenceCode, getGroupId(objectDefinition, scopeKey),
				objectDefinition.getObjectDefinitionId()),
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
	public Page<ObjectEntry> getApprovedObjectEntries(
			long companyId, ObjectDefinition objectDefinition, String scopeKey,
			Aggregation aggregation, DTOConverterContext dtoConverterContext,
			String filterString, Pagination pagination, String search,
			Sort[] sorts)
		throws Exception {

		dtoConverterContext.setAttribute("preferApproved", Boolean.TRUE);

		return getObjectEntries(
			companyId, objectDefinition, scopeKey, aggregation,
			dtoConverterContext,
			_objectDefinitionFilterParser.parse(filterString, objectDefinition),
			pagination, search, sorts);
	}

	@Override
	public ObjectEntry getApprovedObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey)
		throws Exception {

		dtoConverterContext.setAttribute("preferApproved", Boolean.TRUE);

		return getObjectEntry(
			companyId, dtoConverterContext, externalReferenceCode,
			objectDefinition, scopeKey);
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

		Long[] groupIds = null;

		if (StringUtil.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_DEPOT)) {

			groupIds = TransformUtil.transformToArray(
				_depotEntryLocalService.getGroupConnectedDepotEntries(
					groupId, DepotConstants.TYPE_ANY, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS),
				DepotEntryModel::getGroupId, Long.class);
		}
		else {
			groupIds = new Long[] {groupId};
		}

		int start = _getStartPosition(pagination);
		int end = _getEndPosition(pagination);

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
					ActionKeys.DELETE, ObjectEntryResourceImpl.class, 0L,
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
					ActionKeys.UPDATE, ObjectEntryResourceImpl.class, 0L,
					"putObjectEntryBatch", null, objectDefinition.getUserId(),
					objectDefinition.getResourceName(), groupId,
					dtoConverterContext.getUriInfo())
			).build(),
			_getFacets(
				aggregation,
				aggregationTerm -> objectEntryLocalService.getAggregationCounts(
					groupId, objectDefinition.getObjectDefinitionId(),
					aggregationTerm, predicate,
					GetterUtil.getBoolean(
						dtoConverterContext.getAttribute("preferApproved")),
					start, end)),
			TransformUtil.transform(
				objectEntryLocalService.getPrimaryKeys(
					groupIds, companyId, dtoConverterContext.getUserId(),
					objectDefinition.getObjectDefinitionId(), predicate,
					GetterUtil.getBoolean(
						dtoConverterContext.getAttribute("preferApproved")),
					search, start, end, sorts),
				primaryKey -> _getObjectEntry(
					dtoConverterContext, objectDefinition, primaryKey)),
			pagination,
			objectEntryLocalService.getValuesListCount(
				groupIds, companyId, dtoConverterContext.getUserId(),
				objectDefinition.getObjectDefinitionId(), predicate,
				GetterUtil.getBoolean(
					dtoConverterContext.getAttribute("preferApproved")),
				search));
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

		return _getObjectEntry(
			dtoConverterContext, objectDefinition,
			_objectEntryService.getObjectEntry(objectEntryId));
	}

	@Override
	public ObjectEntry getObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey)
		throws Exception {

		return _getObjectEntry(
			dtoConverterContext, objectDefinition,
			_objectEntryService.getObjectEntry(
				externalReferenceCode, getGroupId(objectDefinition, scopeKey),
				objectDefinition.getObjectDefinitionId()));
	}

	@Override
	public ObjectEntry getObjectEntryByVersion(
			DTOConverterContext dtoConverterContext, Long objectEntryId,
			int version)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(objectEntryId);

		ObjectEntryVersion objectEntryVersion =
			_objectEntryVersionService.getObjectEntryVersion(
				objectEntryId, version);

		dtoConverterContext.setAttribute(
			"objectEntryVersion", objectEntryVersion);

		return _objectEntryDTOConverter.toDTO(
			_getObjectEntryVersionDTOConverterContext(
				dtoConverterContext, objectEntryVersion,
				serviceBuilderObjectEntry),
			serviceBuilderObjectEntry);
	}

	@Override
	public ObjectEntry getObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey, int version)
		throws Exception {

		ObjectEntry objectEntry = getObjectEntry(
			objectDefinition.getCompanyId(), dtoConverterContext,
			externalReferenceCode, objectDefinition, scopeKey);

		return getObjectEntryByVersion(
			dtoConverterContext, objectEntry.getId(), version);
	}

	@Override
	public Page<ObjectEntry> getRelatedObjectEntries(
			Aggregation aggregation, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, String filterString,
			ObjectRelationship objectRelationship, Pagination pagination,
			String scopeKey, String search, Sort[] sorts)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId1());

		return _getRelatedObjectEntries(
			aggregation, dtoConverterContext, filterString,
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2()),
			objectRelationship, pagination, objectDefinition, search,
			_objectEntryService.getObjectEntry(
				externalReferenceCode, getGroupId(objectDefinition, scopeKey),
				objectDefinition.getObjectDefinitionId()),
			sorts);
	}

	@Override
	public Page<ObjectEntry> getRelatedObjectEntries(
			DTOConverterContext dtoConverterContext, long objectEntryId,
			ObjectRelationship objectRelationship, Pagination pagination)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId1());

		ObjectDefinition relatedObjectDefinition = _getRelatedObjectDefinition(
			objectDefinition, objectRelationship);

		if (objectDefinition.isUnmodifiableSystemObject()) {
			return _getSystemObjectRelatedObjectEntries(
				dtoConverterContext, objectDefinition, objectEntryId,
				objectRelationship,
				_objectRelatedModelsProviderRegistry.
					getObjectRelatedModelsProvider(
						relatedObjectDefinition.getClassName(),
						relatedObjectDefinition.getCompanyId(),
						objectRelationship.getType()),
				pagination);
		}

		return _getRelatedObjectEntries(
			null, dtoConverterContext, null, relatedObjectDefinition,
			objectRelationship, pagination, objectDefinition, null,
			_objectEntryService.getObjectEntry(objectEntryId), null);
	}

	@Override
	public ObjectEntry getRelatedObjectEntry(
			DTOConverterContext dtoConverterContext, long objectEntryId,
			ObjectRelationship objectRelationship, long parentObjectEntryId)
		throws Exception {

		return _getRelatedObjectEntry(
			dtoConverterContext,
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2()),
			objectRelationship,
			_objectEntryService.getObjectEntry(objectEntryId),
			_objectEntryService.getObjectEntry(parentObjectEntryId));
	}

	@Override
	public ObjectEntry getRelatedObjectEntry(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectRelationship objectRelationship,
			String parentExternalReferenceCode, String scopeKey)
		throws Exception {

		ObjectDefinition objectDefinition1 =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId1());

		ObjectDefinition objectDefinition2 =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2());

		return _getRelatedObjectEntry(
			dtoConverterContext, objectDefinition2, objectRelationship,
			_objectEntryService.getObjectEntry(
				externalReferenceCode, getGroupId(objectDefinition2, scopeKey),
				objectDefinition2.getObjectDefinitionId()),
			_objectEntryService.getObjectEntry(
				parentExternalReferenceCode,
				getGroupId(objectDefinition1, scopeKey),
				objectDefinition1.getObjectDefinitionId()));
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
						objectRelationship.getObjectRelationshipId(), null,
						false, serviceBuilderObjectEntry.getPrimaryKey(), null,
						_getStartPosition(pagination),
						_getEndPosition(pagination), null),
				baseModel -> _toDTO(
					baseModel, serviceBuilderObjectEntry,
					_systemObjectDefinitionManagerRegistry.
						getSystemObjectDefinitionManager(
							relatedObjectDefinition.getName()))),
			pagination,
			objectRelatedModelsProvider.getRelatedModelsCount(
				serviceBuilderObjectEntry.getGroupId(),
				objectRelationship.getObjectRelationshipId(), null,
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
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId,
			Pagination pagination)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			objectEntryLocalService.getObjectEntry(objectEntryId);

		_checkHeadObjectEntry(serviceBuilderObjectEntry);
		_checkObjectEntryObjectDefinitionId(
			objectDefinition, serviceBuilderObjectEntry);

		return Page.of(
			TransformUtil.transform(
				_objectEntryVersionService.getObjectEntryVersions(
					objectEntryId, _getStartPosition(pagination),
					_getEndPosition(pagination)),
				objectEntryVersion -> _objectEntryDTOConverter.toDTO(
					_getObjectEntryVersionDTOConverterContext(
						dtoConverterContext, objectEntryVersion,
						serviceBuilderObjectEntry),
					serviceBuilderObjectEntry)),
			pagination,
			_objectEntryVersionService.getObjectEntryVersionsCount(
				objectEntryId));
	}

	@Override
	public Page<ObjectEntry> getVersionedObjectEntries(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey, Pagination pagination)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			objectEntryLocalService.getObjectEntry(
				externalReferenceCode, getGroupId(objectDefinition, scopeKey),
				objectDefinition.getObjectDefinitionId());

		return getVersionedObjectEntries(
			dtoConverterContext, objectDefinition,
			serviceBuilderObjectEntry.getObjectEntryId(), pagination);
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
			0L, dtoConverterContext, objectDefinition, existingObjectEntry,
			objectEntryId, true, false);
	}

	@Override
	public ObjectEntry partialUpdateRelatedObjectEntry(
			DTOConverterContext dtoConverterContext, ObjectEntry objectEntry,
			long objectEntryId, ObjectRelationship objectRelationship,
			long parentObjectEntryId)
		throws Exception {

		ObjectDefinition objectDefinition2 =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2());

		return updateRelatedObjectEntry(
			dtoConverterContext,
			ObjectEntryManagerUtil.partialUpdateObjectEntry(
				getRelatedObjectEntry(
					dtoConverterContext, objectEntryId, objectRelationship,
					parentObjectEntryId),
				objectDefinition2.getObjectDefinitionId(), objectEntry),
			objectEntryId, objectRelationship, parentObjectEntryId);
	}

	@Override
	public ObjectEntry partialUpdateRelatedObjectEntry(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectEntry objectEntry,
			ObjectRelationship objectRelationship,
			String parentExternalReferenceCode, String scopeKey)
		throws Exception {

		ObjectDefinition objectDefinition2 =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2());

		return updateRelatedObjectEntry(
			dtoConverterContext, externalReferenceCode,
			ObjectEntryManagerUtil.partialUpdateObjectEntry(
				getRelatedObjectEntry(
					dtoConverterContext, externalReferenceCode,
					objectRelationship, parentExternalReferenceCode, scopeKey),
				objectDefinition2.getObjectDefinitionId(), objectEntry),
			objectRelationship, parentExternalReferenceCode, scopeKey);
	}

	@Override
	public ObjectEntry restoreObjectEntry(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(
				externalReferenceCode, getGroupId(objectDefinition, scopeKey),
				objectDefinition.getObjectDefinitionId());

		return _toObjectEntry(
			dtoConverterContext, objectDefinition,
			_objectEntryService.restoreObjectEntryFromTrash(
				serviceBuilderObjectEntry,
				ServiceContextUtil.createServiceContext(
					serviceBuilderObjectEntry.getObjectEntryId())));
	}

	@Override
	public ObjectEntry restoreObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId, int version)
		throws Exception {

		return _restoreVersionedObjectEntry(
			dtoConverterContext, objectDefinition,
			_getObjectEntryByVersion(
				dtoConverterContext, objectEntryId, version),
			version);
	}

	@Override
	public ObjectEntry restoreObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey, int version)
		throws Exception {

		return _restoreVersionedObjectEntry(
			dtoConverterContext, objectDefinition,
			_getObjectEntryByVersion(
				dtoConverterContext, externalReferenceCode, objectDefinition,
				scopeKey, version),
			version);
	}

	@Override
	public void subscribeObjectEntry(
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(
				externalReferenceCode, getGroupId(objectDefinition, scopeKey),
				objectDefinition.getObjectDefinitionId());

		_objectEntryService.subscribeObjectEntry(
			getGroupId(objectDefinition, scopeKey),
			serviceBuilderObjectEntry.getObjectEntryId());
	}

	@Override
	public void unsubscribeObjectEntry(
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(
				externalReferenceCode, getGroupId(objectDefinition, scopeKey),
				objectDefinition.getObjectDefinitionId());

		_objectEntryService.unsubscribeObjectEntry(
			serviceBuilderObjectEntry.getObjectEntryId());
	}

	@Override
	public ObjectEntry updateObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId,
			ObjectEntry objectEntry)
		throws Exception {

		return _updateObjectEntry(
			0L, dtoConverterContext, objectDefinition, objectEntry,
			objectEntryId, false, false);
	}

	@Override
	public ObjectEntry updateObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry, String scopeKey)
		throws Exception {

		long groupId = getGroupId(objectDefinition, scopeKey);

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			objectEntryLocalService.fetchObjectEntry(
				externalReferenceCode, groupId,
				objectDefinition.getObjectDefinitionId());

		if (serviceBuilderObjectEntry != null) {
			_checkObjectEntryStatus(serviceBuilderObjectEntry);
		}

		validateReadOnlyObjectFields(
			externalReferenceCode, groupId, objectDefinition, objectEntry);

		ServiceContext serviceContext = _createServiceContext(
			dtoConverterContext, objectDefinition, objectEntry, scopeKey);

		serviceContext.setCompanyId(companyId);

		return _toObjectEntry(
			dtoConverterContext, objectDefinition,
			_addOrUpdateNestedObjectEntries(
				dtoConverterContext, objectDefinition, objectEntry,
				_getObjectRelationships(objectDefinition, objectEntry),
				_objectEntryService.addOrUpdateObjectEntry(
					externalReferenceCode, groupId,
					objectDefinition.getObjectDefinitionId(),
					_getObjectEntryFolderId(
						objectDefinition.getCompanyId(), groupId, objectEntry,
						serviceContext),
					_toObjectValues(
						0L, dtoConverterContext.getLocale(), objectDefinition,
						objectEntry, scopeKey, serviceContext),
					serviceContext),
				scopeKey));
	}

	public ObjectEntry updateRelatedObjectEntry(
			DTOConverterContext dtoConverterContext, ObjectEntry objectEntry,
			long objectEntryId, ObjectRelationship objectRelationship,
			long parentObjectEntryId)
		throws Exception {

		return _updateRelatedObjectEntry(
			dtoConverterContext, objectEntry, objectEntryId, objectRelationship,
			parentObjectEntryId);
	}

	@Override
	public ObjectEntry updateRelatedObjectEntry(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectEntry objectEntry,
			ObjectRelationship objectRelationship,
			String parentExternalReferenceCode, String scopeKey)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(
				externalReferenceCode,
				getGroupId(
					_objectDefinitionLocalService.getObjectDefinition(
						objectRelationship.getObjectDefinitionId2()),
					scopeKey),
				objectRelationship.getObjectDefinitionId2());
		com.liferay.object.model.ObjectEntry parentServiceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(
				parentExternalReferenceCode,
				getGroupId(
					_objectDefinitionLocalService.getObjectDefinition(
						objectRelationship.getObjectDefinitionId1()),
					scopeKey),
				objectRelationship.getObjectDefinitionId1());

		return _updateRelatedObjectEntry(
			dtoConverterContext, objectEntry,
			serviceBuilderObjectEntry.getObjectEntryId(), objectRelationship,
			parentServiceBuilderObjectEntry.getObjectEntryId());
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
				0L, dtoConverterContext.getLocale(), objectDefinition,
				objectEntry, scopeKey,
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
			Map<String, String> templateParameterMap, UriInfo uriInfo)
		throws Exception {

		return ActionUtil.addAction(
			actionName, ObjectEntryResourceImpl.class,
			serviceBuilderObjectEntry.getHeadObjectEntryId(), methodName, null,
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

	private Map<String, String> _addAction(
			String actionName, String[] methodNames,
			ObjectDefinition objectDefinition,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry,
			Map<String, String> templateParameterMap, UriInfo uriInfo)
		throws Exception {

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				objectDefinition.getScope());

		if (!objectScopeProvider.isGroupAware()) {
			return _addAction(
				actionName, methodNames[0], serviceBuilderObjectEntry,
				HashMapBuilder.put(
					"externalReferenceCode",
					serviceBuilderObjectEntry.getExternalReferenceCode()
				).putAll(
					templateParameterMap
				).build(),
				uriInfo);
		}

		return _addAction(
			actionName, methodNames[1], serviceBuilderObjectEntry,
			HashMapBuilder.put(
				"externalReferenceCode",
				serviceBuilderObjectEntry.getExternalReferenceCode()
			).put(
				"scopeKey",
				String.valueOf(serviceBuilderObjectEntry.getGroupId())
			).putAll(
				templateParameterMap
			).build(),
			uriInfo);
	}

	private ObjectEntry _addObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			String scopeKey, ServiceContext serviceContext,
			Map<String, Serializable> values)
		throws Exception {

		validateReadOnlyObjectFields(
			null, getGroupId(objectDefinition, scopeKey), objectDefinition,
			objectEntry);

		long groupId = getGroupId(objectDefinition, scopeKey);

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.addObjectEntry(
				groupId, objectDefinition.getObjectDefinitionId(),
				_getObjectEntryFolderId(
					objectDefinition.getCompanyId(), groupId, objectEntry,
					serviceContext),
				objectEntry.getDefaultLanguageId(), values, serviceContext);

		return _toObjectEntry(
			dtoConverterContext, objectDefinition,
			_addOrUpdateNestedObjectEntries(
				dtoConverterContext, objectDefinition, objectEntry,
				_getObjectRelationships(objectDefinition, objectEntry),
				serviceBuilderObjectEntry, scopeKey));
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

					long nestedObjectEntryId = 0;

					try {
						nestedObjectEntryId =
							systemObjectDefinitionManager.upsertBaseModel(
								String.valueOf(
									nestedObjectEntry.get(
										"externalReferenceCode")),
								relatedObjectDefinition.getCompanyId(),
								dtoConverterContext.getUser(),
								nestedObjectEntry);
					}
					catch (PortalException portalException) {
						if (!LazyReferencingThreadLocal.isEnabled()) {
							throw portalException;
						}

						BaseModel<?> baseModel =
							systemObjectDefinitionManager.
								getOrAddEmptyBaseModel(
									String.valueOf(
										nestedObjectEntry.get(
											"externalReferenceCode")),
									dtoConverterContext.getUser());

						nestedObjectEntryId =
							(long)baseModel.getPrimaryKeyObj();
					}

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

				boolean oneToManyObjectRelationship =
					_isOneToManyObjectRelationship(
						objectDefinition, objectRelationship,
						relatedObjectDefinition);

				for (Object item : nestedObjectEntries) {
					ObjectEntry nestedObjectEntry = (ObjectEntry)item;

					if (oneToManyObjectRelationship) {
						Map<String, Object> nestedObjectEntryProperties =
							nestedObjectEntry.getProperties();

						nestedObjectEntryProperties.put(
							ObjectRelationshipUtil.
								getObjectRelationshipFieldName(
									objectDefinition,
									objectRelationship.getName()),
							serviceBuilderObjectEntry.getPrimaryKey());
					}

					long groupId = 0;
					String nestedScopeKey = scopeKey;

					if (!Objects.equals(
							relatedObjectDefinition.getScope(),
							ObjectDefinitionConstants.SCOPE_COMPANY)) {

						if (Objects.equals(
								objectDefinition.getScope(),
								ObjectDefinitionConstants.SCOPE_COMPANY)) {

							nestedScopeKey = MapUtil.getString(
								nestedObjectEntry.getProperties(), "scopeKey",
								null);

							groupId = getGroupId(
								relatedObjectDefinition, nestedScopeKey);
						}
						else {
							groupId = serviceBuilderObjectEntry.getGroupId();
						}
					}

					if (LazyReferencingThreadLocal.isEnabled()) {
						nestedObjectEntry = _toObjectEntry(
							dtoConverterContext, relatedObjectDefinition,
							_objectEntryService.getOrAddEmptyObjectEntry(
								nestedObjectEntry.getExternalReferenceCode(),
								groupId,
								relatedObjectDefinition.
									getObjectDefinitionId()));
					}
					else {
						nestedObjectEntry =
							objectEntryManager.updateObjectEntry(
								objectDefinition.getCompanyId(),
								dtoConverterContext,
								nestedObjectEntry.getExternalReferenceCode(),
								relatedObjectDefinition, nestedObjectEntry,
								nestedScopeKey);
					}

					if (!oneToManyObjectRelationship) {
						_relateNestedObjectEntry(
							objectDefinition, objectRelationship,
							serviceBuilderObjectEntry.getPrimaryKey(),
							nestedObjectEntry.getId(),
							ServiceContextUtil.createServiceContext(
								objectDefinition.getCompanyId(), groupId,
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

	private ObjectEntry _addRelatedObjectEntry(
			DTOConverterContext dtoConverterContext, ObjectEntry objectEntry,
			long objectEntryId, ObjectRelationship objectRelationship,
			String scopeKey)
		throws Exception {

		if (!objectRelationship.isEdge()) {
			throw new UnsupportedOperationException();
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2());

		long groupId = getGroupId(objectDefinition, scopeKey);

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		Map<String, Object> properties = objectEntry.getProperties();

		properties.put(objectField.getName(), objectEntryId);

		ServiceContext serviceContext = _createServiceContext(
			dtoConverterContext, objectDefinition, objectEntry, scopeKey);

		return _toObjectEntry(
			dtoConverterContext, objectDefinition,
			_addOrUpdateNestedObjectEntries(
				dtoConverterContext, objectDefinition, objectEntry,
				_getObjectRelationships(objectDefinition, objectEntry),
				_objectEntryService.addObjectEntry(
					groupId, objectDefinition.getObjectDefinitionId(),
					_getObjectEntryFolderId(
						objectDefinition.getCompanyId(), groupId, objectEntry,
						serviceContext),
					objectEntry.getDefaultLanguageId(),
					_toObjectValues(
						objectField.getObjectFieldId(),
						dtoConverterContext.getLocale(), objectDefinition,
						objectEntry, scopeKey, serviceContext),
					serviceContext),
				scopeKey));
	}

	private void _checkApprovedObjectEntry(
			boolean preferApproved,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry)
		throws Exception {

		if (!preferApproved) {
			_checkHeadObjectEntry(serviceBuilderObjectEntry);

			return;
		}

		if (serviceBuilderObjectEntry.isApproved()) {
			return;
		}

		throw new NoSuchObjectEntryException();
	}

	private void _checkHeadObjectEntry(
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry)
		throws Exception {

		if (serviceBuilderObjectEntry.isHead()) {
			return;
		}

		throw new NoSuchObjectEntryException();
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

	private void _checkObjectEntryStatus(
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry)
		throws Exception {

		if (serviceBuilderObjectEntry.isInTrash()) {
			throw new NoSuchObjectEntryException(
				StringBundler.concat(
					"No ObjectEntry exists with the key {objectEntryId=",
					serviceBuilderObjectEntry.getObjectEntryId(), ", status=",
					WorkflowConstants.STATUS_IN_TRASH, "}"));
		}
	}

	private void _checkRootDescendantNode(
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry,
			boolean skipCheckRootDescendantNode)
		throws Exception {

		if (serviceBuilderObjectEntry.isRootDescendantNode() &&
			!skipCheckRootDescendantNode) {

			throw new NoSuchObjectEntryException(
				StringBundler.concat(
					"No ObjectEntry exists with the key {objectEntryId=",
					serviceBuilderObjectEntry.getObjectEntryId(),
					", rootObjectEntryId=0}"));
		}
	}

	private ObjectEntry _copyVersionedObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry)
		throws Exception {

		_checkObjectEntryStatus(serviceBuilderObjectEntry);

		objectEntry.setExpirationDate(() -> null);
		objectEntry.setExternalReferenceCode(() -> null);
		objectEntry.setId(() -> null);

		_removeReadOnlyProperties(objectDefinition, objectEntry);

		String scopeKey = objectEntry.getScopeKey();

		ServiceContext serviceContext = _createServiceContext(
			dtoConverterContext, objectDefinition, objectEntry, scopeKey);

		if (objectDefinition.isEnableObjectEntryDraft()) {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		Map<String, Serializable> values = _toObjectValues(
			0L, dtoConverterContext.getLocale(), objectDefinition, objectEntry,
			scopeKey, serviceContext);

		ObjectField titleObjectField =
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getTitleObjectFieldId());

		_replaceValues(objectDefinition, scopeKey, titleObjectField, values);

		return _addObjectEntry(
			dtoConverterContext, objectDefinition, objectEntry,
			objectEntry.getScopeKey(), serviceContext, values);
	}

	private ServiceContext _createServiceContext(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			String scopeKey)
		throws Exception {

		if (objectEntry.getPermissions() == null) {
			return ServiceContextUtil.createServiceContext(
				objectDefinition.getCompanyId(),
				getGroupId(objectDefinition, scopeKey),
				dtoConverterContext.getLocale(), null, objectEntry,
				dtoConverterContext.getUserId());
		}

		if (LazyReferencingThreadLocal.isEnabled()) {
			for (Permission permission : objectEntry.getPermissions()) {
				if (Validator.isNull(
						permission.getRoleExternalReferenceCode())) {

					continue;
				}

				String className = StringPool.BLANK;

				RoleTypeContributor roleTypeContributor =
					_roleTypeContributorProvider.getRoleTypeContributor(
						RoleConstants.getLabelType(permission.getRoleType()));

				if (roleTypeContributor != null) {
					className = roleTypeContributor.getClassName();
				}

				_roleService.getOrAddEmptyRole(
					permission.getRoleExternalReferenceCode(), className, 0,
					permission.getRoleName(),
					RoleConstants.getLabelType(permission.getRoleType()));
			}
		}

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

	private void _deleteObjectEntry(
			ObjectDefinition objectDefinition,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry)
		throws Exception {

		_checkHeadObjectEntry(serviceBuilderObjectEntry);
		_checkObjectEntryObjectDefinitionId(
			objectDefinition, serviceBuilderObjectEntry);
		_checkRootDescendantNode(serviceBuilderObjectEntry, false);

		DepotEntry depotEntry = _depotEntryLocalService.fetchGroupDepotEntry(
			serviceBuilderObjectEntry.getGroupId());

		if ((depotEntry != null) &&
			_trashHelper.isTrashEnabled(
				serviceBuilderObjectEntry.getGroupId()) &&
			(serviceBuilderObjectEntry.getStatus() !=
				WorkflowConstants.STATUS_IN_TRASH) &&
			FeatureFlagManagerUtil.isEnabled("LPD-17564")) {

			_objectEntryService.moveObjectEntryToTrash(
				serviceBuilderObjectEntry,
				ServiceContextUtil.createServiceContext(
					serviceBuilderObjectEntry.getObjectEntryId()));
		}
		else {
			_objectEntryService.deleteObjectEntry(
				serviceBuilderObjectEntry.getObjectEntryId());
		}
	}

	private void _deleteRelateObjectEntry(
			ObjectDefinition objectDefinition,
			ObjectRelationship objectRelationship,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry,
			long parentObjectEntryId)
		throws Exception {

		_checkHeadObjectEntry(serviceBuilderObjectEntry);
		_checkObjectEntryObjectDefinitionId(
			objectDefinition, serviceBuilderObjectEntry);

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		if (!Objects.equals(
				MapUtil.getLong(
					serviceBuilderObjectEntry.getValues(),
					objectField.getName()),
				parentObjectEntryId)) {

			throw new NoSuchObjectEntryException(
				StringBundler.concat(
					"No ObjectEntry exists with the key {",
					objectField.getName(), "=", parentObjectEntryId,
					", objectEntryId=",
					serviceBuilderObjectEntry.getObjectEntryId(), "}"));
		}

		_objectEntryService.deleteObjectEntry(
			serviceBuilderObjectEntry.getObjectEntryId());
	}

	private void _disassociateRelatedModels(
			ObjectDefinition objectDefinition,
			ObjectRelationship objectRelationship, long primaryKey1,
			long[] primaryKeys2, ObjectDefinition relatedObjectDefinition,
			long userId)
		throws Exception {

		ObjectRelatedModelsProvider<?> objectRelatedModelsProvider = null;

		if (_isOneToManyObjectRelationship(
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

		_checkHeadObjectEntry(serviceBuilderObjectEntry);
		_checkObjectEntryObjectDefinitionId(
			objectDefinition, serviceBuilderObjectEntry);
		_checkObjectEntryStatus(serviceBuilderObjectEntry);

		if (serviceBuilderObjectEntry.getVersion() == version) {
			_objectEntryService.expireObjectEntry(
				serviceBuilderObjectEntry.getObjectEntryId(),
				ServiceContextUtil.createServiceContext(
					serviceBuilderObjectEntry.getObjectEntryId()));
		}

		_objectEntryVersionService.expireObjectEntryVersion(
			serviceBuilderObjectEntry,
			ServiceContextUtil.createServiceContext(
				serviceBuilderObjectEntry.getObjectEntryId()),
			version);

		ObjectEntryVersion objectEntryVersion =
			_objectEntryVersionService.getObjectEntryVersion(
				serviceBuilderObjectEntry.getObjectEntryId(), version);

		dtoConverterContext.setAttribute(
			"objectEntryVersion", objectEntryVersion);

		return _objectEntryDTOConverter.toDTO(
			_getObjectEntryVersionDTOConverterContext(
				dtoConverterContext, objectEntryVersion,
				serviceBuilderObjectEntry),
			serviceBuilderObjectEntry);
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

	private List<Facet> _getFacets(
			Aggregation aggregation,
			UnsafeFunction<String, Map<Object, Long>, Exception> unsafeFunction)
		throws Exception {

		List<Facet> facets = new ArrayList<>();

		if ((aggregation == null) ||
			(aggregation.getAggregationTerms() == null)) {

			return facets;
		}

		Map<String, String> aggregationTerms =
			aggregation.getAggregationTerms();

		for (Map.Entry<String, String> entry1 : aggregationTerms.entrySet()) {
			List<Facet.FacetValue> facetValues = new ArrayList<>();

			Map<Object, Long> aggregationCounts = unsafeFunction.apply(
				entry1.getKey());

			for (Map.Entry<Object, Long> entry2 :
					aggregationCounts.entrySet()) {

				Long value = entry2.getValue();

				facetValues.add(
					new Facet.FacetValue(
						value.intValue(), String.valueOf(entry2.getKey())));
			}

			facets.add(new Facet(entry1.getKey(), facetValues));
		}

		return facets;
	}

	private long _getFileEntryGroupId(
			String groupExternalReferenceCode,
			ObjectDefinition objectDefinition, String scopeKey)
		throws Exception {

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

	private String _getNewValue(
			long groupId, ObjectDefinition objectDefinition,
			ObjectField objectField, String value)
		throws Exception {

		Table<?> table = _objectFieldLocalService.getTable(
			objectDefinition.getObjectDefinitionId(), objectField.getName());

		Column<?, String> objectFieldColumn =
			(Column<?, String>)table.getColumn(objectField.getDBColumnName());

		return UniqueUtil.getCopyValue(
			copyValue -> {
				long count = objectEntryLocalService.getValuesListCount(
					new Long[] {groupId}, objectDefinition.getCompanyId(),
					objectDefinition.getUserId(),
					objectDefinition.getObjectDefinitionId(),
					objectFieldColumn.eq(copyValue), false, null);

				if (count == 0) {
					return true;
				}

				return false;
			},
			value);
	}

	private ObjectEntry _getObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(objectEntryId);

		_checkApprovedObjectEntry(
			GetterUtil.getBoolean(
				dtoConverterContext.getAttribute("preferApproved")),
			serviceBuilderObjectEntry);
		_checkObjectEntryObjectDefinitionId(
			objectDefinition, serviceBuilderObjectEntry);

		return _toObjectEntry(
			dtoConverterContext, objectDefinition, serviceBuilderObjectEntry);
	}

	private ObjectEntry _getObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry)
		throws Exception {

		_checkApprovedObjectEntry(
			GetterUtil.getBoolean(
				dtoConverterContext.getAttribute("preferApproved")),
			serviceBuilderObjectEntry);
		_checkObjectEntryObjectDefinitionId(
			objectDefinition, serviceBuilderObjectEntry);
		_checkRootDescendantNode(serviceBuilderObjectEntry, false);

		return _toObjectEntry(
			dtoConverterContext, objectDefinition, serviceBuilderObjectEntry);
	}

	private ObjectEntry _getObjectEntryByVersion(
			DTOConverterContext dtoConverterContext, Long objectEntryId,
			int version)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(objectEntryId);

		return _objectEntryDTOConverter.toDTO(
			_getObjectEntryVersionDTOConverterContext(
				dtoConverterContext,
				_objectEntryVersionService.getObjectEntryVersion(
					objectEntryId, version),
				serviceBuilderObjectEntry),
			serviceBuilderObjectEntry);
	}

	private ObjectEntry _getObjectEntryByVersion(
			DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey, int version)
		throws Exception {

		ObjectEntry objectEntry = getObjectEntry(
			objectDefinition.getCompanyId(), dtoConverterContext,
			externalReferenceCode, objectDefinition, scopeKey);

		return _getObjectEntryByVersion(
			dtoConverterContext, objectEntry.getId(), version);
	}

	private long _getObjectEntryFolderId(
		long companyId, long groupId, ObjectEntry objectEntry,
		ServiceContext serviceContext) {

		String objectEntryFolderExternalReferenceCode =
			objectEntry.getObjectEntryFolderExternalReferenceCode();

		if (Validator.isNull(objectEntryFolderExternalReferenceCode)) {
			return GetterUtil.getLong(objectEntry.getObjectEntryFolderId());
		}

		try {
			ObjectEntryFolder objectEntryFolder =
				_objectEntryFolderService.getOrAddEmptyObjectEntryFolder(
					objectEntryFolderExternalReferenceCode, groupId, companyId,
					serviceContext);

			return objectEntryFolder.getObjectEntryFolderId();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT;
	}

	private DTOConverterContext _getObjectEntryVersionDTOConverterContext(
			DTOConverterContext dtoConverterContext,
			ObjectEntryVersion objectEntryVersion,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry)
		throws Exception {

		Map<String, Map<String, String>> actions = GetterUtil.getObject(
			dtoConverterContext.getActions(), Collections::emptyMap);

		if (GetterUtil.getBoolean(
				dtoConverterContext.getAttribute("addActions"), true)) {

			boolean latestObjectEntryVersion =
				_objectEntryVersionLocalService.isLatestObjectEntryVersion(
					serviceBuilderObjectEntry.getObjectEntryId(),
					objectEntryVersion.getVersion());
			Map<String, String> templateParameterMap = HashMapBuilder.put(
				"version", String.valueOf(objectEntryVersion.getVersion())
			).build();

			actions = HashMapBuilder.create(
				actions
			).put(
				"copy",
				() -> {
					if (!FeatureFlagManagerUtil.isEnabled(
							serviceBuilderObjectEntry.getCompanyId(),
							"LPD-17564") ||
						(!objectEntryVersion.isApproved() &&
						 !objectEntryVersion.isDraft())) {

						return null;
					}

					return _addAction(
						ActionKeys.ADD_ENTRY, "postObjectEntryByVersionCopy",
						serviceBuilderObjectEntry, templateParameterMap,
						dtoConverterContext.getUriInfo());
				}
			).put(
				"delete",
				() -> {
					if (latestObjectEntryVersion) {
						return null;
					}

					return _addAction(
						ActionKeys.DELETE, "deleteObjectEntryByVersion",
						serviceBuilderObjectEntry, templateParameterMap,
						dtoConverterContext.getUriInfo());
				}
			).put(
				"expire",
				() -> {
					if (!FeatureFlagManagerUtil.isEnabled(
							serviceBuilderObjectEntry.getCompanyId(),
							"LPD-17564") ||
						objectEntryVersion.isExpired() ||
						objectEntryVersion.isDraft() ||
						objectEntryVersion.isPending()) {

						return null;
					}

					return _addAction(
						ActionKeys.UPDATE, "postObjectEntryByVersionExpire",
						serviceBuilderObjectEntry, templateParameterMap,
						dtoConverterContext.getUriInfo());
				}
			).put(
				"get",
				_addAction(
					ActionKeys.VIEW, "getObjectEntryByVersion",
					serviceBuilderObjectEntry, templateParameterMap,
					dtoConverterContext.getUriInfo())
			).put(
				"restore",
				() -> {
					if (latestObjectEntryVersion ||
						objectEntryVersion.isExpired()) {

						return null;
					}

					return _addAction(
						ActionKeys.UPDATE, "putObjectEntryByVersionRestore",
						serviceBuilderObjectEntry, templateParameterMap,
						dtoConverterContext.getUriInfo());
				}
			).build();
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
			"objectEntryVersion", objectEntryVersion);

		return defaultDTOConverterContext;
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

		if (_isOneToManyObjectRelationship(
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
			objectRelationship.getObjectRelationshipId(), null, false,
			primaryKey, null, -1, -1, null);
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

	private Page<ObjectEntry> _getRelatedObjectEntries(
			Aggregation aggregation, DTOConverterContext dtoConverterContext,
			String filterString, ObjectDefinition objectDefinition,
			ObjectRelationship objectRelationship, Pagination pagination,
			ObjectDefinition parentObjectDefinition, String search,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry,
			Sort[] sorts)
		throws Exception {

		long groupId = getGroupId(
			objectDefinition,
			String.valueOf(serviceBuilderObjectEntry.getGroupId()));

		ObjectRelatedModelsProvider objectRelatedModelsProvider =
			_objectRelatedModelsProviderRegistry.getObjectRelatedModelsProvider(
				objectDefinition.getClassName(),
				objectDefinition.getCompanyId(), objectRelationship.getType());

		Predicate predicate = _filterFactory.create(
			_objectDefinitionFilterParser.parse(filterString, objectDefinition),
			objectDefinition);

		int start = _getStartPosition(pagination);
		int end = _getEndPosition(pagination);

		return Page.of(
			HashMapBuilder.put(
				"get",
				ActionUtil.addAction(
					ActionKeys.VIEW,
					ObjectEntryRelatedObjectsResourceImpl.class,
					serviceBuilderObjectEntry.getObjectEntryId(),
					"getCurrentObjectEntriesObjectRelationshipNamePage", null,
					serviceBuilderObjectEntry.getUserId(),
					parentObjectDefinition.getClassName(), groupId,
					dtoConverterContext.getUriInfo())
			).build(),
			_getFacets(
				aggregation,
				aggregationTerm ->
					objectEntryLocalService.getOneToManyAggregationCounts(
						groupId, objectDefinition.getObjectDefinitionId(),
						serviceBuilderObjectEntry.getObjectEntryId(),
						objectRelationship.getObjectRelationshipId(),
						aggregationTerm, predicate, true, search, start, end)),
			_toObjectEntries(
				dtoConverterContext,
				objectRelatedModelsProvider.getRelatedModels(
					groupId, objectRelationship.getObjectRelationshipId(),
					predicate, false, serviceBuilderObjectEntry.getPrimaryKey(),
					search, start, end, sorts)),
			pagination,
			objectRelatedModelsProvider.getRelatedModelsCount(
				groupId, objectRelationship.getObjectRelationshipId(),
				predicate, serviceBuilderObjectEntry.getPrimaryKey(), search));
	}

	private ObjectEntry _getRelatedObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition,
			ObjectRelationship objectRelationship,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry,
			com.liferay.object.model.ObjectEntry
				serviceBuilderParentObjectEntry)
		throws Exception {

		if (!objectRelationship.isEdge()) {
			throw new UnsupportedOperationException();
		}

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		if (!Objects.equals(
				MapUtil.getLong(
					serviceBuilderObjectEntry.getValues(),
					objectField.getName()),
				serviceBuilderParentObjectEntry.getObjectEntryId())) {

			throw new NoSuchObjectEntryException(
				StringBundler.concat(
					"No ObjectEntry exists with the key {",
					objectField.getName(), "=",
					serviceBuilderParentObjectEntry.getObjectEntryId(),
					", objectEntryId=",
					serviceBuilderObjectEntry.getObjectEntryId(), "}"));
		}

		return _toObjectEntry(
			dtoConverterContext, objectDefinition, serviceBuilderObjectEntry);
	}

	private int _getStartPosition(Pagination pagination) {
		if (pagination != null) {
			return pagination.getStartPosition();
		}

		return QueryUtil.ALL_POS;
	}

	private Map<String, Map<String, String>> _getSubscriptionActions(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564") ||
			!objectDefinition.isEnableObjectEntrySubscription() ||
			ObjectEntryFolderSubscriptionUtil.isSubscribedToObjectEntryFolder(
				serviceBuilderObjectEntry.getCompanyId(),
				serviceBuilderObjectEntry.getGroupId(),
				serviceBuilderObjectEntry.getObjectEntryFolderId(),
				dtoConverterContext.getUserId())) {

			return Collections.emptyMap();
		}

		if (!_subscriptionLocalService.isSubscribed(
				serviceBuilderObjectEntry.getCompanyId(),
				dtoConverterContext.getUserId(),
				serviceBuilderObjectEntry.getModelClassName(),
				serviceBuilderObjectEntry.getObjectEntryId())) {

			return Collections.singletonMap(
				"subscribe",
				_addAction(
					ActionKeys.SUBSCRIBE,
					new String[] {
						"postByExternalReferenceCodeSubscribe",
						"postScopeScopeKeyByExternalReferenceCodeSubscribe"
					},
					objectDefinition, serviceBuilderObjectEntry, null,
					dtoConverterContext.getUriInfo()));
		}

		return Collections.singletonMap(
			"unsubscribe",
			_addAction(
				ActionKeys.SUBSCRIBE,
				new String[] {
					"postByExternalReferenceCodeUnsubscribe",
					"postScopeScopeKeyByExternalReferenceCodeUnsubscribe"
				},
				objectDefinition, serviceBuilderObjectEntry, null,
				dtoConverterContext.getUriInfo()));
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
					groupId, objectRelationship.getObjectRelationshipId(), null,
					false, objectEntryId, null, _getStartPosition(pagination),
					_getEndPosition(pagination), null)),
			pagination,
			objectRelatedModelsProvider.getRelatedModelsCount(
				groupId, objectRelationship.getObjectRelationshipId(), null,
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

	private boolean _isObjectEntryDraft(Status status) {
		if ((status != null) &&
			(status.getCode() == WorkflowConstants.STATUS_DRAFT)) {

			return true;
		}

		return false;
	}

	private boolean _isOneToManyObjectRelationship(
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
		else if (fileEntry.getFileURL() != null) {
			try {
				URL url = _exportImportAttachmentManager.getURL(
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

	private void _processAttachment(
			ObjectDefinition objectDefinition, ObjectField objectField,
			String scopeKey, ServiceContext serviceContext,
			Map<String, Object> values)
		throws Exception {

		if (objectField.isLocalized()) {
			Object value = values.get(objectField.getI18nObjectFieldName());

			if ((value == null) || !(value instanceof Map<?, ?>)) {
				return;
			}

			Map<String, Serializable> localizedValues =
				(Map<String, Serializable>)value;

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
			objectDefinition, objectField, values.get(objectField.getName()),
			scopeKey, serviceContext);

		if (fileEntryId > 0) {
			values.put(objectField.getName(), fileEntryId);
		}
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

	private void _replaceValues(
			ObjectDefinition objectDefinition, String scopeKey,
			ObjectField objectField, Map<String, Serializable> values)
		throws Exception {

		if ((objectField == null) ||
			!Objects.equals(
				objectField.getBusinessType(),
				ObjectFieldConstants.BUSINESS_TYPE_TEXT)) {

			return;
		}

		long groupId = getGroupId(objectDefinition, scopeKey);

		if (objectField.isLocalized()) {
			Map<String, Object> i18nValues = (Map<String, Object>)values.get(
				objectField.getI18nObjectFieldName());

			String languageId = LocaleUtil.toLanguageId(
				LocaleUtil.getSiteDefault());

			String value = GetterUtil.getString(i18nValues.get(languageId));

			i18nValues.put(
				languageId,
				_getNewValue(groupId, objectDefinition, objectField, value));
		}
		else {
			String value = GetterUtil.getString(
				values.get(objectField.getName()));

			values.put(
				objectField.getName(),
				_getNewValue(groupId, objectDefinition, objectField, value));
		}
	}

	private ObjectEntry _restoreVersionedObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			int version)
		throws Exception {

		_removeReadOnlyProperties(objectDefinition, objectEntry);

		return updateObjectEntry(
			_getObjectEntryVersionDTOConverterContext(
				dtoConverterContext,
				_objectEntryVersionService.getObjectEntryVersion(
					objectEntry.getId(), version),
				_objectEntryService.getObjectEntry(objectEntry.getId())),
			objectDefinition, objectEntry.getId(), objectEntry);
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

		Map<String, Map<String, String>> actions = GetterUtil.getObject(
			dtoConverterContext.getActions(), Collections::emptyMap);

		if (GetterUtil.getBoolean(
				dtoConverterContext.getAttribute("addActions"), true)) {

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
						serviceBuilderObjectEntry.isExpired() ||
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
				"restore",
				() -> {
					if (!FeatureFlagManagerUtil.isEnabled("LPD-17564") ||
						!serviceBuilderObjectEntry.isInTrash()) {

						return null;
					}

					return _addAction(
						ActionKeys.DELETE,
						new String[] {
							"putByExternalReferenceCodeRestore",
							"putScopeScopeKeyByExternalReferenceCodeRestore"
						},
						objectDefinition, serviceBuilderObjectEntry, null,
						dtoConverterContext.getUriInfo());
				}
			).put(
				"share",
				() -> {
					if (!FeatureFlagManagerUtil.isEnabled(
							objectDefinition.getCompanyId(), "LPD-17564")) {

						return null;
					}

					Group group = groupLocalService.fetchGroup(
						serviceBuilderObjectEntry.getGroupId());

					if (group == null) {
						return null;
					}

					SharingConfiguration sharingConfiguration =
						_sharingConfigurationFactory.
							getGroupSharingConfiguration(group);

					if (!sharingConfiguration.isEnabled()) {
						return null;
					}

					return _addAction(
						ActionKeys.VIEW, "getObjectEntry",
						serviceBuilderObjectEntry,
						dtoConverterContext.getUriInfo());
				}
			).put(
				"update",
				_addAction(
					ActionKeys.UPDATE, "patchObjectEntry",
					serviceBuilderObjectEntry, dtoConverterContext.getUriInfo())
			).put(
				"versions",
				_addAction(
					ActionKeys.VIEW, "getObjectEntriesVersionsPage",
					serviceBuilderObjectEntry, dtoConverterContext.getUriInfo())
			).putAll(
				_getSubscriptionActions(
					dtoConverterContext, objectDefinition,
					serviceBuilderObjectEntry)
			).build();

			for (ObjectAction objectAction :
					_objectActionLocalService.getObjectActions(
						objectDefinition.getObjectDefinitionId(),
						ObjectActionTriggerConstants.KEY_STANDALONE)) {

				actions.put(
					objectAction.getName(),
					_addAction(
						objectAction.getName(),
						new String[] {
							"putByExternalReferenceCodeObjectActionObject" +
								"ActionName",
							"putScopeScopeKeyByExternalReferenceCodeObject" +
								"ActionObjectActionName"
						},
						objectDefinition, serviceBuilderObjectEntry,
						Collections.singletonMap(
							"objectActionName", objectAction.getName()),
						dtoConverterContext.getUriInfo()));
			}
		}

		return _objectEntryDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				dtoConverterContext.isAcceptAllLanguages(), actions,
				HashMapBuilder.<String, Object>put(
					"objectDefinition", objectDefinition
				).putAll(
					dtoConverterContext.getAttributes()
				).build(),
				dtoConverterContext.getDTOConverterRegistry(),
				dtoConverterContext.getHttpServletRequest(),
				serviceBuilderObjectEntry.getObjectEntryId(),
				dtoConverterContext.getLocale(),
				dtoConverterContext.getUriInfo(),
				dtoConverterContext.getUser()),
			serviceBuilderObjectEntry);
	}

	private Map<String, Serializable> _toObjectValues(
			long allowedRelationshipObjectFieldId, Locale locale,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			String scopeKey, ServiceContext serviceContext)
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

		List<Long> relationshipObjectFieldIds = TransformUtil.transform(
			_objectRelationshipLocalService.
				getObjectRelationshipsByObjectDefinitionId2(
					objectDefinition.getObjectDefinitionId(), true),
			ObjectRelationshipModel::getObjectFieldId2);

		relationshipObjectFieldIds.remove(allowedRelationshipObjectFieldId);

		for (ObjectField objectField :
				objectFieldLocalService.getObjectFields(
					objectDefinition.getObjectDefinitionId())) {

			if (relationshipObjectFieldIds.contains(
					objectField.getObjectFieldId())) {

				continue;
			}

			if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

				_processAttachment(
					objectDefinition, objectField, scopeKey, serviceContext,
					properties);
			}

			Object value = ObjectEntryValuesUtil.getValue(
				getGroupId(objectDefinition, scopeKey),
				_objectDefinitionLocalService, objectEntryLocalService,
				objectField, _objectFieldBusinessTypeRegistry,
				serviceContext.getUserId(), properties);

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
			long allowedRelationshipObjectFieldId,
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			long objectEntryId, boolean partialUpdate,
			boolean skipCheckRootDescendantNode)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(objectEntryId);

		_checkObjectEntryObjectDefinitionId(
			objectDefinition, serviceBuilderObjectEntry);
		_checkObjectEntryStatus(serviceBuilderObjectEntry);
		_checkRootDescendantNode(
			serviceBuilderObjectEntry, skipCheckRootDescendantNode);

		validateReadOnlyObjectFields(
			serviceBuilderObjectEntry.getExternalReferenceCode(),
			serviceBuilderObjectEntry.getGroupId(), objectDefinition,
			objectEntry);

		String scopeKey = String.valueOf(
			serviceBuilderObjectEntry.getGroupId());

		ServiceContext serviceContext = _createServiceContext(
			dtoConverterContext, objectDefinition, objectEntry, scopeKey);

		Map<String, Serializable> values = _toObjectValues(
			allowedRelationshipObjectFieldId, dtoConverterContext.getLocale(),
			objectDefinition, objectEntry, scopeKey, serviceContext);

		if (partialUpdate) {
			serviceBuilderObjectEntry =
				_objectEntryService.partialUpdateObjectEntry(
					objectEntryId,
					_getObjectEntryFolderId(
						serviceBuilderObjectEntry.getCompanyId(),
						serviceBuilderObjectEntry.getGroupId(), objectEntry,
						serviceContext),
					values, serviceContext);
		}
		else {
			serviceBuilderObjectEntry = _objectEntryService.updateObjectEntry(
				objectEntryId,
				_getObjectEntryFolderId(
					serviceBuilderObjectEntry.getCompanyId(),
					serviceBuilderObjectEntry.getGroupId(), objectEntry,
					serviceContext),
				values, serviceContext);
		}

		return _toObjectEntry(
			dtoConverterContext, objectDefinition,
			_addOrUpdateNestedObjectEntries(
				dtoConverterContext, objectDefinition, objectEntry,
				_getObjectRelationships(objectDefinition, objectEntry),
				serviceBuilderObjectEntry, scopeKey));
	}

	private ObjectEntry _updateRelatedObjectEntry(
			DTOConverterContext dtoConverterContext, ObjectEntry objectEntry,
			long objectEntryId, ObjectRelationship objectRelationship,
			long parentObjectEntryId)
		throws Exception {

		Map<String, Object> properties = objectEntry.getProperties();

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		properties.put(objectField.getName(), parentObjectEntryId);

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryService.getObjectEntry(objectEntryId);

		if (!Objects.equals(
				MapUtil.getLong(
					serviceBuilderObjectEntry.getValues(),
					objectField.getName()),
				parentObjectEntryId)) {

			throw new NoSuchObjectEntryException(
				String.format(
					"No ObjectEntry exists with the key {%s=%s, " +
						"objectEntryId=%s}",
					objectField.getName(), parentObjectEntryId, objectEntryId));
		}

		return _updateObjectEntry(
			objectRelationship.getObjectFieldId2(), dtoConverterContext,
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2()),
			objectEntry, objectEntryId, false, true);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultObjectEntryManagerImpl.class);

	@Reference
	private Aggregations _aggregations;

	@Reference
	private AttachmentManager _attachmentManager;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ExportImportAttachmentManager _exportImportAttachmentManager;

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
	private ObjectEntryFolderService _objectEntryFolderService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private ObjectEntryVersionLocalService _objectEntryVersionLocalService;

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
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

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
	private RoleService _roleService;

	@Reference
	private RoleTypeContributorProvider _roleTypeContributorProvider;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private SharingConfigurationFactory _sharingConfigurationFactory;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private UserLocalService _userLocalService;

}