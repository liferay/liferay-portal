/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntryOrganizationRelTable;
import com.liferay.account.model.AccountEntryTable;
import com.liferay.account.model.AccountEntryUserRelTable;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetTagGroupRel;
import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagGroupRelLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalService;
import com.liferay.asset.link.constants.AssetLinkConstants;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.dynamic.data.mapping.expression.CreateExpressionRequest;
import com.liferay.dynamic.data.mapping.expression.DDMExpression;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.dynamic.data.mapping.util.NumberUtil;
import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.list.type.exception.NoSuchListTypeEntryException;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.list.type.service.ListTypeEntryService;
import com.liferay.object.action.engine.ObjectActionEngine;
import com.liferay.object.action.util.ObjectActionThreadLocal;
import com.liferay.object.comment.ObjectEntryComment;
import com.liferay.object.configuration.ObjectConfiguration;
import com.liferay.object.configuration.ObjectEntryScheduleConfiguration;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectFieldValidationConstants;
import com.liferay.object.constants.ObjectFilterConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.definition.security.permission.resource.util.ObjectDefinitionResourcePermissionUtil;
import com.liferay.object.definition.setting.util.ObjectDefinitionSettingUtil;
import com.liferay.object.definition.util.ObjectDefinitionThreadLocal;
import com.liferay.object.definition.util.ObjectDefinitionUtil;
import com.liferay.object.entry.ObjectEntryContext;
import com.liferay.object.entry.contributor.ObjectEntryValuesContributor;
import com.liferay.object.entry.folder.subscription.util.ObjectEntryFolderSubscriptionUtil;
import com.liferay.object.entry.folder.util.ObjectEntryFolderUtil;
import com.liferay.object.entry.util.ObjectEntryPayloadUtil;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.entry.util.ObjectEntryValuesUtil;
import com.liferay.object.exception.DuplicateObjectEntryExternalReferenceCodeException;
import com.liferay.object.exception.NoSuchObjectDefinitionException;
import com.liferay.object.exception.ObjectDefinitionScopeException;
import com.liferay.object.exception.ObjectEntryDefaultLanguageIdException;
import com.liferay.object.exception.ObjectEntryExpirationDateException;
import com.liferay.object.exception.ObjectEntryFolderScopeException;
import com.liferay.object.exception.ObjectEntryGroupIdException;
import com.liferay.object.exception.ObjectEntryStatusException;
import com.liferay.object.exception.ObjectEntryValidationException;
import com.liferay.object.exception.ObjectEntryValidationException.ValidationError;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.exception.ObjectRelationshipDeletionTypeException;
import com.liferay.object.exception.ObjectValidationRuleEngineException;
import com.liferay.object.field.attachment.AttachmentManager;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.internal.entry.util.ObjectEntrySearchUtil;
import com.liferay.object.internal.filter.parser.CurrentUserObjectFilterParser;
import com.liferay.object.internal.filter.parser.DateRangeObjectFilterParser;
import com.liferay.object.internal.filter.parser.EqualityOperatorsObjectFilterParser;
import com.liferay.object.internal.filter.parser.InclusionOperatorsObjectFilterParser;
import com.liferay.object.internal.filter.parser.ObjectFilterParser;
import com.liferay.object.internal.sort.SortDSLQueryVisitor;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectFilter;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.model.ObjectState;
import com.liferay.object.model.ObjectStateFlow;
import com.liferay.object.model.bag.ObjectFieldBag;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionLocalizationTable;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionLocalizationTableFactory;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTable;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTableUtil;
import com.liferay.object.petra.sql.dsl.DynamicObjectRelationshipMappingTable;
import com.liferay.object.petra.sql.dsl.DynamicObjectRelationshipMappingTableFactory;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.scope.CompanyScoped;
import com.liferay.object.scope.ObjectDefinitionScoped;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.search.StrictObjectReindexThreadLocal;
import com.liferay.object.service.ObjectEntryVersionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectStateFlowLocalService;
import com.liferay.object.service.ObjectStateLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.service.base.ObjectEntryLocalServiceBaseImpl;
import com.liferay.object.service.persistence.ObjectDefinitionPersistence;
import com.liferay.object.service.persistence.ObjectDefinitionSettingPersistence;
import com.liferay.object.service.persistence.ObjectEntryFolderPersistence;
import com.liferay.object.service.persistence.ObjectEntryVersionPersistence;
import com.liferay.object.service.persistence.ObjectFieldPersistence;
import com.liferay.object.service.persistence.ObjectFieldSettingPersistence;
import com.liferay.object.service.persistence.ObjectRelationshipPersistence;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.ObjectDefinitionTreeFactory;
import com.liferay.object.tree.Tree;
import com.liferay.object.util.comparator.ObjectEntryVersionVersionComparator;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Alias;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.expression.ScalarDSLQueryAlias;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.spi.expression.Scalar;
import com.liferay.petra.sql.dsl.spi.query.QueryExpression;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.dao.jdbc.postgresql.PostgreSQLJDBCUtil;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.jdbc.CurrentConnection;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.OrganizationTable;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.model.Users_OrgsTable;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.ReindexCacheThreadLocal;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManagerUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.sharing.service.SharingEntryLocalService;
import com.liferay.subscription.service.SubscriptionLocalService;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.exception.TrashEntryException;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.model.TrashVersion;
import com.liferay.trash.service.TrashEntryLocalService;
import com.liferay.trash.service.TrashVersionLocalService;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;

import java.math.BigDecimal;

import java.security.Key;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import javax.crypto.spec.SecretKeySpec;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
@Component(
	configurationPid = "com.liferay.object.configuration.ObjectConfiguration",
	property = "model.class.name=com.liferay.object.model.ObjectEntry",
	service = AopService.class
)
public class ObjectEntryLocalServiceImpl
	extends ObjectEntryLocalServiceBaseImpl {

	@Override
	public ObjectEntry addLatestApprovedObjectEntry(
			String externalReferenceCode, long groupId, long userId,
			long headObjectEntryId, ObjectDefinition objectDefinition,
			long objectEntryFolderId, String defaultLanguageId, int version,
			Map<String, Serializable> values)
		throws PortalException {

		ObjectEntry objectEntry = _addObjectEntry(
			externalReferenceCode, groupId, userId, headObjectEntryId,
			objectDefinition.getObjectDefinitionId(), objectEntryFolderId,
			defaultLanguageId, version, WorkflowConstants.STATUS_APPROVED);

		_insertIntoLocalizationTable(
			new HashMap<>(), objectDefinition, objectEntry.getObjectEntryId(),
			null, false, values);
		_insertIntoTable(
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				false, objectDefinition, _objectFieldLocalService),
			new HashMap<>(), objectEntry.getObjectEntryId(), false, values);
		_insertIntoTable(
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				true, objectDefinition, _objectFieldLocalService),
			new HashMap<>(), objectEntry.getObjectEntryId(), false, values);

		return objectEntry;
	}

	@Override
	public ObjectEntry addObjectEntry(
			long groupId, long userId, long objectDefinitionId,
			long objectEntryFolderId, String defaultLanguageId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException {

		serviceContext.setStrictAdd(true);

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				serviceContext.setStrictAdd(false);

				return null;
			});

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		_validateGroupId(groupId, objectDefinition);

		_validateObjectEntryFolderId(groupId, objectEntryFolderId);

		int status = _getStatus(
			objectDefinition,
			GetterUtil.getInteger(serviceContext.getAttribute("status")));

		if (ExportImportThreadLocal.isImportInProcess()) {
			if (status == WorkflowConstants.STATUS_DRAFT) {
				serviceContext.setWorkflowAction(
					WorkflowConstants.ACTION_SAVE_DRAFT);
			}
			else {
				serviceContext.setWorkflowAction(
					WorkflowConstants.ACTION_PUBLISH);
			}
		}

		int workflowAction = serviceContext.getWorkflowAction();

		_validateWorkflowAction(
			objectDefinition.isEnableObjectEntryDraft(), objectDefinition, null,
			workflowAction);

		defaultLanguageId = _getDefaultLanguageId(defaultLanguageId, groupId);

		_fillDefaultValue(defaultLanguageId, objectDefinitionId, values);

		_contributeValues(groupId, objectDefinition, userId, values);

		Map<ObjectField, Set<DLFileEntry>> dlFileEntriesMap = new HashMap<>();
		long objectEntryId = counterLocalService.increment();
		User user = _userLocalService.getUser(userId);

		_validateValues(
			defaultLanguageId, dlFileEntriesMap, null, groupId,
			user.isGuestUser(), objectDefinition, null,
			_objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId()),
			false, serviceContext, null, userId, null, values);

		_addDLFileEntries(
			dlFileEntriesMap, groupId, objectDefinition, objectEntryId,
			serviceContext, userId, values);

		Map<String, Serializable> insertedValues = new HashMap<>();

		_insertIntoLocalizationTable(
			insertedValues, objectDefinition, objectEntryId, null, false,
			values);

		boolean dynamicObjectDefinitionStaticValues = _insertIntoTable(
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				false, objectDefinition, _objectFieldLocalService),
			insertedValues, objectEntryId, false, values);
		boolean extensionDynamicObjectDefinitionStaticValues = _insertIntoTable(
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				true, objectDefinition, _objectFieldLocalService),
			insertedValues, objectEntryId, false, values);

		ObjectEntry objectEntry = objectEntryPersistence.create(objectEntryId);

		objectEntry.setGroupId(groupId);
		objectEntry.setCompanyId(user.getCompanyId());
		objectEntry.setUserId(user.getUserId());
		objectEntry.setUserName(user.getFullName());
		objectEntry.setCreateDate(new Date());
		objectEntry.setHeadObjectEntryId(objectEntryId);
		objectEntry.setObjectDefinitionId(objectDefinitionId);
		objectEntry.setObjectEntryFolderId(objectEntryFolderId);
		objectEntry.setDefaultLanguageId(defaultLanguageId);
		objectEntry.setTreePath(objectEntry.buildTreePath());

		_setExternalReferenceCode(objectEntry, values);
		_setRootObjectEntryId(objectDefinition, objectEntry, values);
		_setDisplayDate(objectDefinition.getCompanyId(), objectEntry, values);

		if (ExportImportThreadLocal.isImportInProcess()) {
			if (status == WorkflowConstants.STATUS_EXPIRED) {
				objectEntry.setExpirationDate(
					(Date)values.get("expirationDate"));
			}
		}
		else {
			_setExpirationDate(
				objectDefinition.getCompanyId(), objectEntry, values);
		}

		_setReviewDate(objectDefinition.getCompanyId(), objectEntry, values);

		objectEntry.setStatus(status);
		objectEntry.setStatusByUserId(user.getUserId());
		objectEntry.setStatusDate(serviceContext.getModifiedDate(null));

		if (dynamicObjectDefinitionStaticValues &&
			extensionDynamicObjectDefinitionStaticValues) {

			_addObjectRelationshipERCFieldValue(
				_objectFieldLocalService.getObjectFields(
					objectEntry.getObjectDefinitionId()),
				insertedValues);

			objectEntry.setValues(insertedValues);
		}

		_addResourcePermissions(objectDefinition, objectEntry);

		if (ExportImportThreadLocal.isImportInProcess() ||
			objectEntry.isRootDescendantNode() ||
			(workflowAction == WorkflowConstants.ACTION_SAVE_DRAFT)) {

			try {
				if (workflowAction == WorkflowConstants.ACTION_SAVE_DRAFT) {
					ObjectEntryThreadLocal.setSkipObjectValidationRules(true);
				}

				objectEntry = objectEntryPersistence.update(objectEntry);
			}
			finally {
				ObjectEntryThreadLocal.setSkipObjectValidationRules(false);
			}

			_updateAsset(
				serviceContext.getUserId(), objectEntry,
				serviceContext.getAssetCategoryIds(),
				serviceContext.getAssetTagNames(),
				serviceContext.getAssetLinkEntryIds(),
				serviceContext.getAssetPriority(), serviceContext);
		}

		_addFriendlyURLEntry(
			objectDefinition, objectEntry, serviceContext, values);

		try (SafeCloseable safeCloseable =
				ObjectEntryThreadLocal.setObjectEntryFolderIdWithSafeCloseable(
					objectEntryFolderId)) {

			_startWorkflowInstance(userId, objectEntry, serviceContext, false);
		}

		_addOrUpdateComments(
			groupId, userId, objectDefinition, objectEntry, serviceContext);

		ObjectDefinitionResourcePermissionUtil.updateResourcePermissions(
			objectDefinition.getCompanyId(), objectEntry.getGroupId(),
			objectDefinition.getClassName(), objectEntry.getObjectEntryId(),
			serviceContext);

		_deleteTempFileEntries(dlFileEntriesMap);

		objectEntry = _addObjectEntryVersion(objectDefinition, objectEntry);

		boolean clearObjectEntryIdsMap =
			ObjectActionThreadLocal.isClearObjectEntryIdsMap();

		try {
			if (clearObjectEntryIdsMap) {
				ObjectActionThreadLocal.clearObjectEntryIdsMap();
			}

			ObjectActionThreadLocal.setClearObjectEntryIdsMap(false);

			_executeObjectActions(
				objectEntry.getCompanyId(),
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD, objectDefinition,
				objectEntry, null, serviceContext.getLanguageId(), user);
		}
		finally {
			ObjectActionThreadLocal.setClearObjectEntryIdsMap(
				clearObjectEntryIdsMap);
		}

		return objectEntry;
	}

	@Override
	public ObjectEntry addObjectEntry(
			String externalReferenceCode, long groupId, long userId,
			ObjectDefinition objectDefinition, long objectEntryFolderId)
		throws PortalException {

		return _addObjectEntry(
			externalReferenceCode, groupId, userId, 0,
			objectDefinition.getObjectDefinitionId(), objectEntryFolderId, null,
			0, WorkflowConstants.STATUS_DRAFT);
	}

	@Override
	public void addOrUpdateExtensionDynamicObjectDefinitionTableValues(
			long userId, ObjectDefinition objectDefinition, long primaryKey,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException {

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				true, objectDefinition, _objectFieldLocalService);

		int count = _count(dynamicObjectDefinitionTable, primaryKey);

		String defaultLanguageId = _language.getLanguageId(
			_portal.getSiteDefaultLocale(
				GroupConstants.DEFAULT_PARENT_GROUP_ID));
		Map<ObjectField, Set<DLFileEntry>> dlFileEntriesMap = new HashMap<>();
		User user = _userLocalService.getUser(userId);

		if (count > 0) {
			Map<String, Serializable> existingValues =
				getExtensionDynamicObjectDefinitionTableValues(
					objectDefinition, primaryKey);

			_validateValues(
				defaultLanguageId, dlFileEntriesMap, existingValues,
				GroupConstants.DEFAULT_PARENT_GROUP_ID, user.isGuestUser(),
				objectDefinition, null,
				dynamicObjectDefinitionTable.getObjectFields(), true,
				serviceContext, null, userId, null, values);

			_addDLFileEntries(
				dlFileEntriesMap, 0, objectDefinition, primaryKey,
				serviceContext, userId, values);

			_deleteFromLocalizationTable(objectDefinition, primaryKey);

			_insertIntoLocalizationTable(
				new HashMap<>(), objectDefinition, primaryKey, existingValues,
				true, values);

			_updateTable(
				dynamicObjectDefinitionTable, primaryKey, true, values);
		}
		else {
			_validateValues(
				defaultLanguageId, dlFileEntriesMap, null,
				GroupConstants.DEFAULT_PARENT_GROUP_ID, user.isGuestUser(),
				objectDefinition, null,
				dynamicObjectDefinitionTable.getObjectFields(), false,
				serviceContext, null, userId, null, values);

			_addDLFileEntries(
				dlFileEntriesMap, 0, objectDefinition, primaryKey,
				serviceContext, userId, values);

			_insertIntoLocalizationTable(
				new HashMap<>(), objectDefinition, primaryKey, null, false,
				values);

			_insertIntoTable(
				dynamicObjectDefinitionTable, new HashMap<>(), primaryKey,
				false, values);
		}

		_deleteTempFileEntries(dlFileEntriesMap);
	}

	@Override
	public ObjectEntry addOrUpdateObjectEntry(
			String externalReferenceCode, long groupId, long userId,
			long objectDefinitionId, long objectEntryFolderId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		if (groupId != 0) {
			Group group = _groupLocalService.getGroup(groupId);

			if (user.getCompanyId() != group.getCompanyId()) {
				throw new PrincipalException();
			}
		}

		ObjectEntry objectEntry = null;

		if (Validator.isNotNull(externalReferenceCode)) {
			objectEntry = objectEntryPersistence.fetchByERC_G_C_ODI(
				externalReferenceCode, groupId, user.getCompanyId(),
				objectDefinitionId);

			if (objectEntry != null) {
				return objectEntryLocalService.updateObjectEntry(
					userId, objectEntry.getObjectEntryId(), objectEntryFolderId,
					values, serviceContext);
			}
		}

		objectEntry = objectEntryLocalService.addObjectEntry(
			groupId, userId, objectDefinitionId, objectEntryFolderId, null,
			values, serviceContext);

		if (Validator.isNotNull(externalReferenceCode)) {
			objectEntry.setExternalReferenceCode(externalReferenceCode);

			try {
				if (serviceContext.getWorkflowAction() ==
						WorkflowConstants.ACTION_SAVE_DRAFT) {

					ObjectEntryThreadLocal.setSkipObjectValidationRules(true);
				}

				objectEntry = objectEntryPersistence.update(objectEntry);
			}
			finally {
				ObjectEntryThreadLocal.setSkipObjectValidationRules(false);
			}
		}

		_reindex(objectEntry);

		return objectEntry;
	}

	@Override
	public void checkObjectEntries(long companyId) throws PortalException {
		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			return;
		}

		Date date = new Date();

		_companyIdPreviousCheckDate.computeIfAbsent(
			companyId,
			key -> new Date(
				date.getTime() - _getObjectEntryCheckInterval(companyId)));

		_checkObjectEntriesByDisplayDate(companyId, date);

		_checkObjectEntriesByExpirationDate(companyId, date);

		_checkObjectEntriesByReviewDate(companyId, date);

		_companyIdPreviousCheckDate.put(companyId, date);
	}

	@Override
	public ObjectEntry copyObjectEntry(
			long userId, long objectEntryId, long objectEntryFolderId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException {

		ObjectEntry objectEntry = getObjectEntry(objectEntryId);

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderPersistence.findByPrimaryKey(objectEntryFolderId);

		_checkObjectDefinitionScope(
			objectEntry.getObjectDefinitionId(),
			objectEntryFolder.getGroupId());

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectEntry.getObjectDefinitionId());

		_removeInvalidAssetCategories(
			objectEntryFolder.getGroupId(), objectDefinition.getClassName(),
			objectEntryId, serviceContext);
		_removeInvalidAssetTags(
			objectEntryFolder.getGroupId(), objectDefinition.getClassName(),
			objectEntryId, serviceContext);

		List<ObjectField> objectFields =
			_objectFieldLocalService.getCustomObjectFields(
				objectDefinition.getObjectDefinitionId());

		for (ObjectField objectField : objectFields) {
			if (ObjectFieldUtil.isReadOnly(
					_ddmExpressionFactory, objectEntry, objectField, userId)) {

				values.remove(objectField.getName());
			}
		}

		if (objectDefinition.isEnableObjectEntryDraft()) {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		values.put("externalReferenceCode", null);

		return addObjectEntry(
			objectEntryFolder.getGroupId(), userId,
			objectDefinition.getObjectDefinitionId(), objectEntryFolderId,
			objectEntry.getDefaultLanguageId(), values, serviceContext);
	}

	@Override
	public void deleteExtensionDynamicObjectDefinitionTableValues(
			ObjectDefinition objectDefinition, long primaryKey)
		throws PortalException {

		Map<String, Serializable> extensionDynamicObjectDefinitionTableValues =
			getExtensionDynamicObjectDefinitionTableValues(
				objectDefinition, primaryKey);

		_deleteFromLocalizationTable(objectDefinition, primaryKey);

		_deleteFromTable(
			objectDefinition.getExtensionDBTableName(),
			objectDefinition.getPKObjectFieldDBColumnName(), primaryKey);

		deleteRelatedObjectEntries(
			0, objectDefinition.getObjectDefinitionId(), primaryKey);

		_deleteFileEntries(
			Collections.emptyMap(), objectDefinition.getObjectDefinitionId(),
			extensionDynamicObjectDefinitionTableValues);
	}

	@Override
	public ObjectEntry deleteObjectEntry(long objectEntryId)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryPersistence.findByPrimaryKey(
			objectEntryId);

		return objectEntryLocalService.deleteObjectEntry(objectEntry);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public ObjectEntry deleteObjectEntry(ObjectEntry objectEntry)
		throws PortalException {

		ObjectActionThreadLocal.clearObjectEntryIdsMap();

		objectEntry = objectEntryPersistence.remove(objectEntry);

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectEntry.getObjectDefinitionId());

		if (objectEntry.isRootDescendantNode()) {
			_startWorkflowInstance(
				PrincipalThreadLocal.getUserId(), objectEntry,
				new ServiceContext(), false);
		}
		else if (objectEntry.getStatus() != WorkflowConstants.STATUS_IN_TRASH) {
			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
				objectEntry.getCompanyId(), objectEntry.getNonzeroGroupId(),
				objectDefinition.getClassName(),
				objectEntry.getObjectEntryId());
		}

		_commentManager.deleteDiscussion(
			objectDefinition.getClassName(), objectEntry.getObjectEntryId());

		_dlFileEntryLocalService.deleteFileEntries(
			objectDefinition.getCompanyId(),
			_classNameLocalService.getClassNameId(
				objectDefinition.getClassName()),
			objectEntry.getObjectEntryId());

		if (!ObjectDefinitionThreadLocal.isDeleteObjectDefinitionId(
				objectDefinition.getObjectDefinitionId())) {

			_resourceLocalService.deleteResource(
				objectEntry.getCompanyId(), objectDefinition.getClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				objectEntry.getObjectEntryId());

			_assetEntryLocalService.deleteEntry(
				objectDefinition.getClassName(),
				objectEntry.getObjectEntryId());

			_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
				objectEntry.getNonzeroGroupId(),
				_classNameLocalService.getClassNameId(
					objectDefinition.getClassName()),
				objectEntry.getObjectEntryId());

			_objectEntryVersionLocalService.deleteObjectEntryVersions(
				objectEntry.getObjectEntryId());

			_sharingEntryLocalService.deleteSharingEntries(
				_classNameLocalService.getClassNameId(
					objectDefinition.getClassName()),
				objectEntry.getObjectEntryId());

			if (FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
				_subscriptionLocalService.deleteSubscriptions(
					objectEntry.getCompanyId(), objectEntry.getModelClassName(),
					objectEntry.getObjectEntryId());
				_trashEntryLocalService.deleteEntry(
					objectDefinition.getClassName(),
					objectEntry.getObjectEntryId());
			}

			_deleteFromLocalizationTable(
				objectDefinition, objectEntry.getObjectEntryId());

			_deleteFromTable(
				objectDefinition.getDBTableName(),
				objectDefinition.getPKObjectFieldDBColumnName(),
				objectEntry.getObjectEntryId());
			_deleteFromTable(
				objectDefinition.getExtensionDBTableName(),
				objectDefinition.getPKObjectFieldDBColumnName(),
				objectEntry.getObjectEntryId());
		}

		deleteRelatedObjectEntries(
			objectEntry.getGroupId(), objectDefinition.getObjectDefinitionId(),
			objectEntry.getPrimaryKey());

		if (!objectDefinition.isActive() ||
			!objectDefinition.isEnableIndexSearch()) {

			return objectEntry;
		}

		Indexer<ObjectEntry> indexer = IndexerRegistryUtil.getIndexer(
			objectDefinition.getClassName());

		indexer.delete(objectEntry);

		return objectEntry;
	}

	@Override
	public void deleteRelatedObjectEntries(
			long groupId, long objectDefinitionId, long primaryKey)
		throws PortalException {

		_deleteRelatedObjectEntries(
			groupId, false, objectDefinitionId, primaryKey);
	}

	@Override
	public ObjectEntry expireObjectEntry(
			long userId, long objectEntryId, ServiceContext serviceContext)
		throws PortalException {

		return updateStatus(
			userId, objectEntryPersistence.findByPrimaryKey(objectEntryId),
			WorkflowConstants.STATUS_EXPIRED, serviceContext);
	}

	@Override
	public ObjectEntry fetchManyToOneObjectEntry(
			long groupId, long objectRelationshipId, long primaryKey)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipPersistence.findByPrimaryKey(
				objectRelationshipId);

		if (!Objects.equals(
				objectRelationship.getType(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

			throw new UnsupportedOperationException();
		}

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable = null;

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId1());

		ObjectDefinition relatedObjectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId2());

		if (relatedObjectDefinition.isUnmodifiableSystemObject()) {
			dynamicObjectDefinitionTable =
				DynamicObjectDefinitionTableUtil.
					getDynamicObjectDefinitionTable(
						true, relatedObjectDefinition,
						_objectFieldLocalService);
		}
		else {
			dynamicObjectDefinitionTable =
				DynamicObjectDefinitionTableUtil.
					getDynamicObjectDefinitionTable(
						false, relatedObjectDefinition,
						_objectFieldLocalService);

			Column<DynamicObjectDefinitionTable, Long> column =
				(Column<DynamicObjectDefinitionTable, Long>)
					dynamicObjectDefinitionTable.getColumn(
						ObjectRelationshipUtil.getObjectRelationshipFieldName(
							objectDefinition, objectRelationship.getName()));

			if (column == null) {
				dynamicObjectDefinitionTable =
					DynamicObjectDefinitionTableUtil.
						getDynamicObjectDefinitionTable(
							true, relatedObjectDefinition,
							_objectFieldLocalService);
			}
		}

		DSLQuery dslQuery = _getFetchManyToOneObjectEntryDSLQuery(
			dynamicObjectDefinitionTable, groupId, objectRelationship,
			primaryKey, dynamicObjectDefinitionTable.getPrimaryKeyColumn());

		if (_log.isDebugEnabled()) {
			_log.debug("Get one to many related object entries: " + dslQuery);
		}

		List<ObjectEntry> objectEntries = objectEntryPersistence.dslQuery(
			dslQuery);

		if (objectEntries.isEmpty()) {
			return null;
		}

		return objectEntries.get(0);
	}

	@Override
	public ObjectEntry fetchObjectEntry(long groupId, long objectDefinitionId) {
		return objectEntryPersistence.fetchByG_ODI_First(
			groupId, objectDefinitionId, null);
	}

	@Override
	public ObjectEntry fetchObjectEntry(
		long groupId, ObjectDefinition objectDefinition, String urlTitle) {

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				objectDefinition.getScope());

		if (!objectScopeProvider.isGroupAware()) {
			Company company = _companyLocalService.fetchCompany(
				objectDefinition.getCompanyId());

			groupId = company.getGroupId();
		}

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				groupId,
				_classNameLocalService.getClassNameId(
					objectDefinition.getClassName()),
				urlTitle);

		if (friendlyURLEntry == null) {
			return null;
		}

		return fetchObjectEntry(friendlyURLEntry.getClassPK());
	}

	@Override
	public ObjectEntry fetchObjectEntry(
		String externalReferenceCode, long groupId, long objectDefinitionId) {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.fetchByPrimaryKey(objectDefinitionId);

		if (objectDefinition == null) {
			return null;
		}

		return objectEntryPersistence.fetchByERC_G_C_ODI(
			externalReferenceCode, groupId, objectDefinition.getCompanyId(),
			objectDefinitionId);
	}

	@Override
	public ObjectEntry fetchObjectEntryByHeadObjectEntryId(
		long headObjectEntryId) {

		return objectEntryPersistence.fetchByHeadObjectEntryId(
			headObjectEntryId, false);
	}

	@Override
	public Map<Object, Long> getAggregationCounts(
			long groupId, long objectDefinitionId, String aggregationTerm,
			Predicate predicate, boolean preferApproved, int start, int end)
		throws PortalException {

		Map<Object, Long> aggregationCounts = new HashMap<>();

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				false, objectDefinition, _objectFieldLocalService);
		DynamicObjectDefinitionTable extensionDynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				true, objectDefinition, _objectFieldLocalService);

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinitionId, aggregationTerm);
		Table table = _objectFieldLocalService.getTable(
			objectDefinitionId, aggregationTerm);

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			table.getColumn(objectField.getDBColumnName()),
			DSLFunctionFactoryUtil.countDistinct(
				dynamicObjectDefinitionTable.getPrimaryKeyColumn()
			).as(
				"aggregationCount"
			)
		).from(
			dynamicObjectDefinitionTable
		).innerJoinON(
			extensionDynamicObjectDefinitionTable,
			extensionDynamicObjectDefinitionTable.getPrimaryKeyColumn(
			).eq(
				dynamicObjectDefinitionTable.getPrimaryKeyColumn()
			)
		).innerJoinON(
			ObjectEntryTable.INSTANCE,
			ObjectEntryTable.INSTANCE.objectEntryId.eq(
				dynamicObjectDefinitionTable.getPrimaryKeyColumn())
		).where(
			ObjectEntryTable.INSTANCE.objectDefinitionId.eq(
				objectDefinitionId
			).and(
				Predicate.withParentheses(predicate)
			).and(
				_getHeadObjectEntryPredicate(preferApproved)
			).and(
				_getPermissionWherePredicate(
					dynamicObjectDefinitionTable, groupId, 0L)
			)
		).groupBy(
			table.getColumn(objectField.getDBColumnName())
		).limit(
			start, end
		);

		for (Object[] values : (List<Object[]>)dslQuery(dslQuery)) {
			aggregationCounts.put(
				GetterUtil.getObject(values[0]), GetterUtil.getLong(values[1]));
		}

		return aggregationCounts;
	}

	@Override
	public Map<String, Serializable>
			getExtensionDynamicObjectDefinitionTableValues(
				ObjectDefinition objectDefinition, long primaryKey)
		throws PortalException {

		DynamicObjectDefinitionTable extensionDynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				true, objectDefinition, _objectFieldLocalService);

		ObjectFieldBag objectFieldBag = objectDefinition.getObjectFieldBag();

		SystemObjectDefinitionManager systemObjectDefinitionManager = null;

		if (objectDefinition.isUnmodifiableSystemObject()) {
			systemObjectDefinitionManager =
				_systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(
						objectDefinition.getName());
		}

		Expression<?>[] selectExpressions = _getSelectExpressions(
			extensionDynamicObjectDefinitionTable, primaryKey, null,
			systemObjectDefinitionManager);

		List<Object[]> rows = _list(
			_getExtensionDynamicObjectDefinitionTableSelectDSLQuery(
				extensionDynamicObjectDefinitionTable, primaryKey,
				selectExpressions, systemObjectDefinitionManager),
			objectFieldBag, selectExpressions);

		Object[] row = null;

		if (!rows.isEmpty()) {
			row = rows.get(0);
		}

		Map<String, Serializable> values = _getValues(
			objectFieldBag, row, selectExpressions);

		values.remove(objectDefinition.getPKObjectFieldName());

		_addLocalizedObjectFieldValues(
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
			DynamicObjectDefinitionLocalizationTableFactory.create(
				objectDefinition, _objectFieldLocalService),
			objectFieldBag, primaryKey, values);
		_addObjectRelationshipERCFieldValue(
			_objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId()),
			values);

		return values;
	}

	@Override
	public Map<String, Serializable> getIndexedValues(ObjectEntry objectEntry)
		throws PortalException {

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		ObjectFieldBag objectFieldBag = objectDefinition.getObjectFieldBag();

		List<ObjectField> indexedObjectFields =
			objectFieldBag.getIndexedObjectFields();

		// Title object field is always needed during reindexing whether it is
		// indexed or not

		ObjectField titleObjectField = objectFieldBag.getObjectField(
			objectDefinition.getTitleObjectFieldId());

		if (!titleObjectField.isIndexed() &&
			!Objects.equals(
				titleObjectField.getName(), "externalReferenceCode") &&
			!Objects.equals(titleObjectField.getName(), "id")) {

			indexedObjectFields = new ArrayList<>(indexedObjectFields);

			indexedObjectFields.add(titleObjectField);
		}

		List<ObjectField> finalIndexedObjectFields = indexedObjectFields;

		Map<Long, Map<String, Serializable>> indexedValuesMap =
			ReindexCacheThreadLocal.getScopeReindexCache(
				ObjectEntryLocalServiceImpl.class.getName(),
				String.valueOf(objectDefinition.getObjectDefinitionId()),
				() -> objectEntryPersistence.dslQueryCount(
					DSLQueryFactoryUtil.count(
					).from(
						ObjectEntryTable.INSTANCE
					),
					false),
				() -> objectEntryPersistence.dslQueryCount(
					DSLQueryFactoryUtil.count(
					).from(
						ObjectEntryTable.INSTANCE
					).where(
						ObjectEntryTable.INSTANCE.objectDefinitionId.eq(
							objectDefinition.getObjectDefinitionId())
					),
					false),
				count -> _getIndexedValuesMap(
					null, objectDefinition, finalIndexedObjectFields));

		if (indexedValuesMap == null) {
			indexedValuesMap = _getIndexedValuesMap(
				objectEntry, objectDefinition, indexedObjectFields);
		}

		Map<String, Serializable> values = indexedValuesMap.get(
			objectEntry.getObjectEntryId());

		if (values == null) {
			return Collections.emptyMap();
		}

		_addLocalizedObjectFieldValues(
			objectEntry.getDefaultLanguageId(),
			DynamicObjectDefinitionLocalizationTableFactory.create(
				objectDefinition, indexedObjectFields),
			objectFieldBag, objectEntry.getObjectEntryId(), values);

		return values;
	}

	@Override
	public List<ObjectEntry> getManyToManyObjectEntries(
			long groupId, long objectRelationshipId, long primaryKey,
			boolean related, boolean reverse, String search, int start, int end)
		throws PortalException {

		DSLQuery dslQuery = _getManyToManyObjectEntriesGroupByStep(
			DSLQueryFactoryUtil.selectDistinct(ObjectEntryTable.INSTANCE),
			groupId, objectRelationshipId, primaryKey, related, reverse, search
		).orderBy(
			ObjectEntryTable.INSTANCE.objectEntryId.ascending()
		).limit(
			start, end
		);

		if (_log.isDebugEnabled()) {
			_log.debug("Get many to many related object entries: " + dslQuery);
		}

		return objectEntryPersistence.dslQuery(dslQuery);
	}

	@Override
	public int getManyToManyObjectEntriesCount(
			long groupId, long objectRelationshipId, long primaryKey,
			boolean related, boolean reverse, String search)
		throws PortalException {

		DSLQuery dslQuery = _getManyToManyObjectEntriesGroupByStep(
			DSLQueryFactoryUtil.countDistinct(
				ObjectEntryTable.INSTANCE.objectEntryId),
			groupId, objectRelationshipId, primaryKey, related, reverse,
			search);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Get many to many related object entries count: " + dslQuery);
		}

		return objectEntryPersistence.dslQueryCount(dslQuery);
	}

	@Override
	public List<ObjectEntry> getObjectEntries(
		long groupId, long objectDefinitionId, int start, int end) {

		return objectEntryPersistence.findByG_ODI(
			groupId, objectDefinitionId, start, end);
	}

	@Override
	public List<ObjectEntry> getObjectEntries(
		long groupId, long objectDefinitionId, int status, int start, int end) {

		return objectEntryPersistence.findByG_ODI_S(
			groupId, objectDefinitionId, status, start, end);
	}

	@Override
	public int getObjectEntriesCount(long objectDefinitionId) {
		return objectEntryPersistence.countByObjectDefinitionId(
			objectDefinitionId);
	}

	@Override
	public long getObjectEntriesCount(
			long userId, Date createDate, long objectDefinitionId)
		throws PortalException {

		return objectEntryPersistence.countByU_GtCD_ODI(
			userId, createDate, objectDefinitionId);
	}

	@Override
	public int getObjectEntriesCount(long groupId, long objectDefinitionId) {
		return objectEntryPersistence.countByG_ODI(groupId, objectDefinitionId);
	}

	@Override
	public long getObjectEntriesCount(
			long groupId, String languageId, ObjectDefinition objectDefinition,
			Predicate predicate)
		throws PortalException {

		DynamicObjectDefinitionLocalizationTable
			dynamicObjectDefinitionLocalizationTable =
				DynamicObjectDefinitionLocalizationTableFactory.create(
					objectDefinition, _objectFieldLocalService);
		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				false, objectDefinition, _objectFieldLocalService);
		DynamicObjectDefinitionTable extensionDynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				true, objectDefinition, _objectFieldLocalService);

		JoinStep joinStep = DSLQueryFactoryUtil.countDistinct(
			dynamicObjectDefinitionTable.getPrimaryKeyColumn()
		).from(
			dynamicObjectDefinitionTable
		).innerJoinON(
			extensionDynamicObjectDefinitionTable,
			extensionDynamicObjectDefinitionTable.getPrimaryKeyColumn(
			).eq(
				dynamicObjectDefinitionTable.getPrimaryKeyColumn()
			)
		).innerJoinON(
			ObjectEntryTable.INSTANCE,
			ObjectEntryTable.INSTANCE.objectEntryId.eq(
				dynamicObjectDefinitionTable.getPrimaryKeyColumn())
		).leftJoinOn(
			dynamicObjectDefinitionLocalizationTable,
			ObjectEntrySearchUtil.getLeftJoinLocalizationTablePredicate(
				dynamicObjectDefinitionLocalizationTable,
				dynamicObjectDefinitionTable,
				_getDefaultLanguageId(languageId, groupId))
		);

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				objectDefinition.getScope());

		predicate = predicate.and(_getHeadObjectEntryPredicate(false));

		if (!objectScopeProvider.isGroupAware()) {
			return dslQueryCount(joinStep.where(predicate));
		}

		return dslQueryCount(
			joinStep.where(
				predicate.and(ObjectEntryTable.INSTANCE.groupId.eq(groupId))));
	}

	@Override
	public ObjectEntry getObjectEntry(
			String externalReferenceCode, long groupId, long objectDefinitionId)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		return objectEntryPersistence.findByERC_G_C_ODI(
			externalReferenceCode, groupId, objectDefinition.getCompanyId(),
			objectDefinitionId);
	}

	@Override
	public List<ObjectEntry> getObjectEntryFolderObjectEntries(
		long groupId, long objectEntryFolderId, int start, int end) {

		return objectEntryPersistence.findByG_OEFI(
			groupId, objectEntryFolderId, start, end);
	}

	@Override
	public int getObjectEntryFolderObjectEntriesCount(
		long groupId, long objectEntryFolderId) {

		return objectEntryPersistence.countByG_OEFI(
			groupId, objectEntryFolderId);
	}

	@Override
	public Map<Object, Long> getOneToManyAggregationCounts(
			long groupId, long objectDefinitionId, long objectEntryId,
			long objectRelationshipId, String aggregationTerm,
			Predicate predicate, boolean related, String search, int start,
			int end)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				false, objectDefinition, _objectFieldLocalService);

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinitionId, aggregationTerm);
		Table table = _objectFieldLocalService.getTable(
			objectDefinitionId, aggregationTerm);

		DSLQuery dslQuery = _getOneToManyObjectEntriesGroupByStep(
			DSLQueryFactoryUtil.select(
				table.getColumn(objectField.getDBColumnName()),
				DSLFunctionFactoryUtil.countDistinct(
					dynamicObjectDefinitionTable.getPrimaryKeyColumn()
				).as(
					"aggregationCount"
				)),
			groupId, objectRelationshipId, predicate, false, objectEntryId,
			related, search
		).groupBy(
			table.getColumn(objectField.getDBColumnName())
		).limit(
			start, end
		);

		Map<Object, Long> aggregationCounts = new HashMap<>();

		for (Object[] values : (List<Object[]>)dslQuery(dslQuery)) {
			aggregationCounts.put(
				GetterUtil.getObject(values[0]), GetterUtil.getLong(values[1]));
		}

		return aggregationCounts;
	}

	@Override
	public List<ObjectEntry> getOneToManyObjectEntries(
			long groupId, long objectRelationshipId, Predicate predicate,
			boolean preferApproved, long primaryKey, boolean related,
			String search, int start, int end, Sort[] sorts)
		throws PortalException {

		DSLQuery dslQuery = _getOneToManyObjectEntriesGroupByStep(
			DSLQueryFactoryUtil.select(ObjectEntryTable.INSTANCE), groupId,
			objectRelationshipId, predicate, preferApproved, primaryKey,
			related, search
		).limit(
			start, end
		);

		if (sorts == null) {
			sorts = new Sort[] {new Sort("id", Sort.LONG_TYPE, false)};
		}

		ObjectRelationshipLocalService objectRelationshipLocalService =
			_objectRelationshipLocalServiceSnapshot.get();

		ObjectRelationship objectRelationship =
			objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		return objectEntryPersistence.dslQuery(
			_applyOrderBy(
				dslQuery,
				_objectDefinitionPersistence.findByPrimaryKey(
					objectRelationship.getObjectDefinitionId2()),
				start, end, sorts));
	}

	@Override
	public int getOneToManyObjectEntriesCount(
			long groupId, long objectRelationshipId, Predicate predicate,
			long primaryKey, boolean related, String search)
		throws PortalException {

		DSLQuery dslQuery = _getOneToManyObjectEntriesGroupByStep(
			DSLQueryFactoryUtil.countDistinct(
				ObjectEntryTable.INSTANCE.objectEntryId),
			groupId, objectRelationshipId, predicate, false, primaryKey,
			related, search);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Get one to many related object entries count: " + dslQuery);
		}

		return objectEntryPersistence.dslQueryCount(dslQuery);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Transactional(propagation = Propagation.REQUIRED)
	public ObjectEntry getOrAddEmptyObjectEntry(
			String externalReferenceCode, long groupId, long userId,
			long objectDefinitionId)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		ObjectEntry objectEntry = _emptyModelManager.getOrAddEmptyModel(
			objectDefinition.getClassName(), objectDefinition.getCompanyId(),
			() -> _addObjectEntry(
				externalReferenceCode, groupId, userId, 0, objectDefinitionId,
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null, 0, WorkflowConstants.STATUS_EMPTY),
			externalReferenceCode,
			(_externalReferenceCode, _groupId) -> fetchObjectEntry(
				_externalReferenceCode, _groupId, objectDefinitionId),
			(_externalReferenceCode, _groupId) -> getObjectEntry(
				_externalReferenceCode, _groupId, objectDefinitionId),
			groupId, objectDefinition.getShortName());

		_addResourcePermissions(objectDefinition, objectEntry);

		_insertIntoTable(
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				false, objectDefinition, _objectFieldLocalService),
			new HashMap<>(), objectEntry.getObjectEntryId(), false,
			new HashMap<>());
		_insertIntoTable(
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				true, objectDefinition, _objectFieldLocalService),
			new HashMap<>(), objectEntry.getObjectEntryId(), false,
			new HashMap<>());

		return objectEntry;
	}

	public List<Long> getPrimaryKeys(
			Long[] groupIds, long companyId, long userId,
			long objectDefinitionId, Predicate predicate,
			boolean preferApproved, String search, int start, int end,
			Sort[] sorts)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		DSLQuery dslQuery = _getObjectEntriesGroupByStep(
			groupIds,
			DSLQueryFactoryUtil.select(ObjectEntryTable.INSTANCE.objectEntryId),
			objectDefinition, predicate, preferApproved, search
		).limit(
			start, end
		);
		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				false, objectDefinition, _objectFieldLocalService);
		ObjectFieldBag objectFieldBag = objectDefinition.getObjectFieldBag();

		return TransformUtil.transform(
			objectEntryPersistence.dslQuery(
				_applyOrderBy(dslQuery, objectDefinition, start, end, sorts)),
			value -> (Long)_getResult(
				value, objectFieldBag,
				dynamicObjectDefinitionTable.getPrimaryKeyColumn()));
	}

	@Override
	public Map<String, Object> getSystemModelAttributes(
			ObjectDefinition objectDefinition, long primaryKey)
		throws PortalException {

		if (!objectDefinition.isUnmodifiableSystemObject()) {
			return new HashMap<>();
		}

		Map<String, Object> baseModelAttributes = new HashMap<>();

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(objectDefinition.getClassName());

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				false, objectDefinition, _objectFieldLocalService);

		Column<DynamicObjectDefinitionTable, Long> primaryKeyColumn =
			dynamicObjectDefinitionTable.getPrimaryKeyColumn();

		List<BaseModel<?>> baseModels = persistedModelLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
			).from(
				dynamicObjectDefinitionTable
			).where(
				primaryKeyColumn.eq(primaryKey)
			));

		if (!baseModels.isEmpty()) {
			BaseModel<?> baseModel = baseModels.get(0);

			baseModelAttributes = baseModel.getModelAttributes();
		}

		Map<String, Object> modelAttributes =
			HashMapBuilder.<String, Object>put(
				"createDate",
				GetterUtil.get(
					baseModelAttributes.get("createDate"), primaryKey)
			).put(
				"externalReferenceCode",
				GetterUtil.get(
					baseModelAttributes.get("externalReferenceCode"),
					primaryKey)
			).put(
				"modifiedDate",
				GetterUtil.get(
					baseModelAttributes.get("modifiedDate"), primaryKey)
			).put(
				"objectDefinitionId", objectDefinition.getObjectDefinitionId()
			).put(
				"uuid",
				GetterUtil.get(baseModelAttributes.get("uuid"), primaryKey)
			).build();

		for (ObjectField objectField :
				_objectFieldLocalService.getObjectFields(
					objectDefinition.getObjectDefinitionId())) {

			if (!objectField.isSystem()) {
				continue;
			}

			Object value = GetterUtil.getObject(
				baseModelAttributes.get(objectField.getDBColumnName()),
				primaryKey);

			if (value instanceof String) {
				value = _localization.getLocalization(
					(String)value, null, true);
			}

			modelAttributes.put(objectField.getName(), value);
		}

		modelAttributes.putAll(
			objectEntryLocalService.
				getExtensionDynamicObjectDefinitionTableValues(
					objectDefinition, primaryKey));

		return modelAttributes;
	}

	@Override
	public Map<String, Serializable> getSystemValues(ObjectEntry objectEntry)
		throws PortalException {

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		ObjectFieldBag objectFieldBag = objectDefinition.getObjectFieldBag();

		List<Object[]> rows = _list(
			DSLQueryFactoryUtil.select(
				_EXPRESSIONS
			).from(
				ObjectEntryTable.INSTANCE
			).where(
				ObjectEntryTable.INSTANCE.objectEntryId.eq(
					objectEntry.getObjectEntryId())
			),
			objectFieldBag, _EXPRESSIONS);

		return _getValues(objectFieldBag, rows.get(0), _EXPRESSIONS);
	}

	@Override
	public String getTitleValue(long objectDefinitionId, long primaryKey)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		if (!objectDefinition.isUnmodifiableSystemObject()) {
			ObjectEntry objectEntry = getObjectEntry(primaryKey);

			return objectEntry.getTitleValue();
		}

		ObjectField titleObjectField =
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getTitleObjectFieldId());

		if (Objects.isNull(titleObjectField)) {
			titleObjectField = _objectFieldLocalService.getObjectField(
				objectDefinitionId, "id");
		}

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(objectDefinition.getClassName());

		BaseModel<?> baseModel =
			(BaseModel<?>)persistedModelLocalService.getPersistedModel(
				primaryKey);

		Map<String, ?> attributeGetterFunctions =
			baseModel.getAttributeGetterFunctions();

		Function<Object, Object> function =
			(Function<Object, Object>)attributeGetterFunctions.get(
				titleObjectField.getDBColumnName());

		return String.valueOf(function.apply(baseModel));
	}

	@Override
	public Map<String, Serializable> getValues(long objectEntryId)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryPersistence.findByPrimaryKey(
			objectEntryId);

		return getValues(objectEntry);
	}

	@Override
	public Map<String, Serializable> getValues(ObjectEntry objectEntry)
		throws PortalException {

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				false, objectDefinition, _objectFieldLocalService);

		DynamicObjectDefinitionTable extensionDynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				true, objectDefinition, _objectFieldLocalService);

		Expression<?>[] extensionSelectExpressions = ArrayUtil.remove(
			_getSelectExpressions(
				extensionDynamicObjectDefinitionTable,
				objectEntry.getObjectEntryId(), null, null),
			extensionDynamicObjectDefinitionTable.getPrimaryKeyColumn());

		Predicate innerJoinPredicate = null;

		if (extensionSelectExpressions.length != 0) {
			innerJoinPredicate =
				dynamicObjectDefinitionTable.getPrimaryKeyColumn(
				).eq(
					extensionDynamicObjectDefinitionTable.getPrimaryKeyColumn()
				);
		}

		ObjectFieldBag objectFieldBag = objectDefinition.getObjectFieldBag();
		Expression<?>[] selectExpressions = ArrayUtil.append(
			_getSelectExpressions(
				dynamicObjectDefinitionTable, objectEntry.getObjectEntryId(),
				null, null),
			extensionSelectExpressions);

		List<Object[]> rows = _list(
			DSLQueryFactoryUtil.select(
				selectExpressions
			).from(
				dynamicObjectDefinitionTable
			).innerJoinON(
				extensionDynamicObjectDefinitionTable, innerJoinPredicate
			).where(
				dynamicObjectDefinitionTable.getPrimaryKeyColumn(
				).eq(
					objectEntry.getObjectEntryId()
				)
			),
			objectFieldBag, selectExpressions);

		if (ListUtil.isEmpty(rows)) {
			return Collections.emptyMap();
		}

		Map<String, Serializable> values = _getValues(
			objectFieldBag, rows.get(0), selectExpressions);

		_addLocalizedObjectFieldValues(
			objectEntry.getDefaultLanguageId(),
			DynamicObjectDefinitionLocalizationTableFactory.create(
				objectDefinition, _objectFieldLocalService),
			objectFieldBag, objectEntry.getObjectEntryId(), values);
		_addObjectRelationshipERCFieldValue(
			_objectFieldLocalService.getObjectFields(
				objectEntry.getObjectDefinitionId()),
			values);

		return values;
	}

	@Override
	public List<Map<String, Serializable>> getValuesList(
			long groupId, long companyId, long userId, long objectDefinitionId,
			Predicate predicate, String search, int start, int end,
			Sort[] sorts)
		throws PortalException {

		return TransformUtil.transform(
			getPrimaryKeys(
				new Long[] {groupId}, companyId, userId, objectDefinitionId,
				predicate, false, search, start, end, sorts),
			this::getValues);
	}

	public int getValuesListCount(
			Long[] groupIds, long companyId, long userId,
			long objectDefinitionId, Predicate predicate,
			boolean preferApproved, String search)
		throws PortalException {

		return objectEntryPersistence.dslQueryCount(
			_getObjectEntriesGroupByStep(
				groupIds,
				DSLQueryFactoryUtil.countDistinct(
					ObjectEntryTable.INSTANCE.objectEntryId),
				_objectDefinitionPersistence.findByPrimaryKey(
					objectDefinitionId),
				predicate, preferApproved, search));
	}

	@Override
	public void insertIntoOrUpdateExtensionTable(
			long userId, long objectDefinitionId, long primaryKey,
			Map<String, Serializable> values)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				true, objectDefinition, _objectFieldLocalService);

		int count = _count(dynamicObjectDefinitionTable, primaryKey);

		String defaultLanguageId = _language.getLanguageId(
			_portal.getSiteDefaultLocale(
				GroupConstants.DEFAULT_PARENT_GROUP_ID));

		User user = _userLocalService.getUser(userId);

		if (count > 0) {
			_validateValues(
				defaultLanguageId, Collections.emptyMap(),
				getExtensionDynamicObjectDefinitionTableValues(
					objectDefinition, primaryKey),
				GroupConstants.DEFAULT_PARENT_GROUP_ID, user.isGuestUser(),
				objectDefinition, null,
				dynamicObjectDefinitionTable.getObjectFields(), true,
				new ServiceContext(), null, userId, null, values);

			_updateTable(
				dynamicObjectDefinitionTable, primaryKey, true, values);
		}
		else {
			_validateValues(
				defaultLanguageId, Collections.emptyMap(), null,
				GroupConstants.DEFAULT_PARENT_GROUP_ID, user.isGuestUser(),
				objectDefinition, null,
				dynamicObjectDefinitionTable.getObjectFields(), false,
				new ServiceContext(), null, userId, null, values);

			_insertIntoTable(
				dynamicObjectDefinitionTable, new HashMap<>(), primaryKey,
				false, values);
		}
	}

	@Override
	public void moveObjectEntriesToTrash(
			long userId, ObjectEntryFolder objectEntryFolder,
			ServiceContext serviceContext)
		throws PortalException {

		for (ObjectEntry objectEntry :
				objectEntryPersistence.findByG_C_OEFI(
					objectEntryFolder.getGroupId(),
					objectEntryFolder.getCompanyId(),
					objectEntryFolder.getObjectEntryFolderId())) {

			if (objectEntry.isInTrash()) {
				continue;
			}

			_moveObjectEntryToTrash(
				objectEntry, objectEntry.getObjectEntryFolderId(),
				serviceContext, userId);
		}
	}

	@Override
	public ObjectEntry moveObjectEntry(
			long userId, long objectEntryId, long objectEntryFolderId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException {

		ObjectEntry objectEntry = getObjectEntry(objectEntryId);

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderPersistence.findByPrimaryKey(objectEntryFolderId);

		_checkObjectDefinitionScope(
			objectEntry.getObjectDefinitionId(),
			objectEntryFolder.getGroupId());

		if (objectEntryFolder.getGroupId() != objectEntry.getGroupId()) {
			objectEntry.setGroupId(objectEntryFolder.getGroupId());

			updateObjectEntry(objectEntry);
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectEntry.getObjectDefinitionId());

		_removeInvalidAssetCategories(
			objectEntryFolder.getGroupId(), objectDefinition.getClassName(),
			objectEntryId, serviceContext);
		_removeInvalidAssetTags(
			objectEntryFolder.getGroupId(), objectDefinition.getClassName(),
			objectEntryId, serviceContext);

		return updateObjectEntry(
			userId, objectEntryId, objectEntryFolderId, values, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectEntry moveObjectEntryToTrash(
			long userId, ObjectEntry objectEntry, ServiceContext serviceContext)
		throws PortalException {

		if (objectEntry.isInTrash()) {
			throw new TrashEntryException();
		}

		long objectEntryFolderId = objectEntry.getObjectEntryFolderId();

		objectEntry.setObjectEntryFolderId(
			ObjectEntryFolderUtil.getRootObjectEntryFolderId(
				objectEntryFolderId));

		return _moveObjectEntryToTrash(
			objectEntry, objectEntryFolderId, serviceContext, userId);
	}

	@Override
	public void moveRelatedObjectEntriesToTrash(
			long groupId, long objectDefinitionId, long primaryKey)
		throws PortalException {

		_deleteRelatedObjectEntries(
			groupId, true, objectDefinitionId, primaryKey);
	}

	@Override
	public ObjectEntry partialUpdateObjectEntry(
			long userId, long objectEntryId, long objectEntryFolderId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException {

		return _updateObjectEntry(
			objectEntryFolderId, objectEntryId, true, serviceContext, userId,
			values);
	}

	@Override
	public void restoreObjectEntriesFromTrash(
			long userId, ObjectEntryFolder objectEntryFolder,
			ServiceContext serviceContext)
		throws PortalException {

		for (ObjectEntry objectEntry :
				objectEntryPersistence.findByG_C_OEFI(
					objectEntryFolder.getGroupId(),
					objectEntryFolder.getCompanyId(),
					objectEntryFolder.getObjectEntryFolderId())) {

			if (!objectEntry.isInTrash()) {
				continue;
			}

			ObjectDefinition objectDefinition =
				_objectDefinitionPersistence.findByPrimaryKey(
					objectEntry.getObjectDefinitionId());

			_restoreObjectEntryFromTrash(
				objectDefinition, objectEntry, serviceContext,
				_trashEntryLocalService.getEntry(
					objectDefinition.getClassName(),
					objectEntry.getObjectEntryId()),
				userId);
		}
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectEntry restoreObjectEntryFromTrash(
			long userId, ObjectEntry objectEntry, ServiceContext serviceContext)
		throws PortalException {

		if (!objectEntry.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectEntry.getObjectDefinitionId());

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			objectDefinition.getClassName(), objectEntry.getObjectEntryId());

		objectEntry.setObjectEntryFolderId(
			ObjectEntryFolderUtil.getObjectEntryFolderId(
				objectEntry.getObjectEntryFolderId(),
				GetterUtil.getLong(
					trashEntry.getTypeSettingsProperty(
						"objectEntryFolderId"))));

		return _restoreObjectEntryFromTrash(
			objectDefinition, objectEntry, serviceContext, trashEntry, userId);
	}

	@Override
	public BaseModelSearchResult<ObjectEntry> searchObjectEntries(
			long groupId, long objectDefinitionId, String keywords, int cur,
			int delta)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				objectDefinition.getScope());

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		searchRequestBuilder.entryClassNames(
			objectDefinition.getClassName()
		).emptySearchEnabled(
			true
		).from(
			cur
		).size(
			delta
		).sorts(
			_sorts.field(Field.ENTRY_CLASS_PK, SortOrder.ASC)
		).withSearchContext(
			searchContext -> {
				searchContext.setAttribute(
					Field.STATUS, WorkflowConstants.STATUS_ANY);
				searchContext.setAttribute(
					"objectDefinitionId",
					objectDefinition.getObjectDefinitionId());
				searchContext.setCompanyId(objectDefinition.getCompanyId());

				if (objectScopeProvider.isGroupAware()) {
					searchContext.setGroupIds(new long[] {groupId});
				}
				else {
					searchContext.setGroupIds(new long[] {0});
				}

				searchContext.setKeywords(keywords);
			}
		);

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		SearchHits searchHits = searchResponse.getSearchHits();

		return new BaseModelSearchResult<>(
			(List<ObjectEntry>)TransformUtil.transform(
				searchHits.getSearchHits(),
				searchHit -> {
					Document document = searchHit.getDocument();

					return objectEntryPersistence.fetchByPrimaryKey(
						document.getLong(Field.ENTRY_CLASS_PK));
				}),
			searchResponse.getTotalHits());
	}

	@Override
	public void subscribeObjectEntry(
			long userId, long groupId, long objectEntryId)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryPersistence.findByPrimaryKey(
			objectEntryId);

		if (objectEntry.isRootDescendantNode() ||
			ObjectEntryFolderSubscriptionUtil.isSubscribedToObjectEntryFolder(
				objectEntry.getCompanyId(), groupId,
				objectEntry.getObjectEntryFolderId(), userId)) {

			throw new UnsupportedOperationException();
		}

		_subscriptionLocalService.addSubscription(
			userId, groupId, objectEntry.getModelClassName(), objectEntryId);
	}

	@Override
	public void unsubscribeObjectEntry(long userId, long objectEntryId)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryPersistence.findByPrimaryKey(
			objectEntryId);

		if (objectEntry.isRootDescendantNode()) {
			throw new UnsupportedOperationException();
		}

		_subscriptionLocalService.deleteSubscription(
			userId, objectEntry.getModelClassName(), objectEntryId);
	}

	@Override
	public void updateAsset(
			long userId, ObjectEntry objectEntry, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException {

		_updateAsset(
			userId, objectEntry, assetCategoryIds, assetTagNames,
			assetLinkEntryIds, priority, null);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectEntry updateModifiedDate(long objectEntryId, Date modifiedDate)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryPersistence.findByPrimaryKey(
			objectEntryId);

		objectEntry.setModifiedDate(modifiedDate);

		objectEntry = objectEntryPersistence.update(objectEntry);

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectEntry.getObjectDefinitionId());

		if (!objectDefinition.isEnableObjectEntryVersioning()) {
			return objectEntry;
		}

		int objectEntryVersionsCount =
			_objectEntryVersionLocalService.getObjectEntryVersionsCount(
				objectEntry.getObjectEntryId());

		if (objectEntryVersionsCount > 0) {
			_objectEntryVersionLocalService.
				updateLatestObjectEntryVersionModifiedDate(
					modifiedDate, objectEntry.getObjectEntryId());
		}

		return objectEntry;
	}

	@Override
	public ObjectEntry updateObjectEntry(
			long userId, long objectEntryId, long objectEntryFolderId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException {

		return _updateObjectEntry(
			objectEntryFolderId, objectEntryId, false, serviceContext, userId,
			values);
	}

	@Override
	public void updateRootObjectEntryIds(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2,
			ObjectRelationship objectRelationship)
		throws PortalException {

		Connection connection = _currentConnection.getConnection(
			objectEntryPersistence.getDataSource());

		AtomicBoolean objectDefinition1RootNode = new AtomicBoolean(false);
		ObjectField objectField = _objectFieldPersistence.findByPrimaryKey(
			objectRelationship.getObjectFieldId2());

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select ", objectDefinition2.getPKObjectFieldDBColumnName(),
					" from ", objectField.getDBTableName(), " where ",
					objectField.getDBColumnName(), " = ?"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update ObjectEntry set rootObjectEntryId = ? where " +
						"objectEntryId = ?");
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update ObjectEntry set rootObjectEntryId = ? where " +
						"rootObjectEntryId = ?")) {

			long[] objectDefinition2RootObjectDefinitionIds =
				objectDefinition2.getRootObjectDefinitionIds();

			_performActions(
				objectDefinition1.getObjectDefinitionId(),
				(ObjectEntry objectEntry) -> {
					try {
						_updateRootObjectEntryIds(
							objectDefinition1RootNode, objectDefinition2,
							objectDefinition2RootObjectDefinitionIds,
							objectEntry, preparedStatement1, preparedStatement2,
							preparedStatement3);
					}
					catch (SQLException sqlException) {
						throw new SystemException(sqlException);
					}
				});

			preparedStatement2.executeBatch();

			if (ArrayUtil.isNotEmpty(
					objectDefinition2RootObjectDefinitionIds)) {

				preparedStatement3.executeBatch();
			}
		}
		catch (SQLException sqlException) {
			throw new SystemException(sqlException);
		}

		long rootObjectDefinitionId =
			objectRelationship.getObjectDefinitionId2();

		if (objectDefinition1RootNode.get()) {
			rootObjectDefinitionId = objectDefinition1.getObjectDefinitionId();
		}

		ObjectDefinitionTreeFactory objectDefinitionTreeFactory =
			new ObjectDefinitionTreeFactory(
				_objectDefinitionPersistence,
				_objectRelationshipLocalServiceSnapshot.get());

		Tree objectDefinitionTree = objectDefinitionTreeFactory.create(
			rootObjectDefinitionId);

		Iterator<Node> iterator = objectDefinitionTree.iterator();

		while (iterator.hasNext()) {
			Node node = iterator.next();

			ObjectDefinition objectDefinition =
				_objectDefinitionPersistence.findByPrimaryKey(
					node.getPrimaryKey());

			Indexer<ObjectEntry> indexer = IndexerRegistryUtil.getIndexer(
				objectDefinition.getClassName());

			_performActions(
				objectDefinition.getObjectDefinitionId(),
				(ObjectEntry objectEntry) -> indexer.reindex(objectEntry));
		}

		objectEntryPersistence.clearCache();
	}

	@Override
	public ObjectEntry updateStatus(
			long userId, long objectEntryId, int status,
			ServiceContext serviceContext)
		throws PortalException {

		return updateStatus(
			userId, objectEntryPersistence.findByPrimaryKey(objectEntryId),
			status, serviceContext);
	}

	@Override
	public ObjectEntry updateStatus(
			long userId, ObjectEntry objectEntry, int status,
			ServiceContext serviceContext)
		throws PortalException {

		ObjectEntry originalObjectEntry = (ObjectEntry)objectEntry.clone();

		Date date = new Date();
		Date displayDate = objectEntry.getDisplayDate();

		if ((objectEntry.getStatus() == status) &&
			((displayDate == null) || displayDate.before(date))) {

			return objectEntry;
		}

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(displayDate != null) && date.before(displayDate)) {

			status = WorkflowConstants.STATUS_SCHEDULED;
		}

		Date expirationDate = objectEntry.getExpirationDate();

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(expirationDate != null) && expirationDate.before(date)) {

			objectEntry.setExpirationDate(null);
		}

		if ((status == WorkflowConstants.STATUS_EXPIRED) &&
			(expirationDate == null)) {

			objectEntry.setExpirationDate(date);
		}

		objectEntry.setStatus(status);

		User user = _userLocalService.getUser(userId);

		objectEntry.setStatusByUserId(user.getUserId());
		objectEntry.setStatusByUserName(user.getFullName());

		objectEntry.setStatusDate(serviceContext.getModifiedDate(null));

		if (_skipModelListeners.get()) {
			while (objectEntry instanceof ModelWrapper) {
				ModelWrapper<ObjectEntry> modelWrapper =
					(ModelWrapper<ObjectEntry>)objectEntry;

				objectEntry = modelWrapper.getWrappedModel();
			}

			objectEntry = objectEntryPersistence.updateImpl(objectEntry);
		}
		else {
			objectEntry = objectEntryPersistence.update(objectEntry);
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.fetchByPrimaryKey(
				objectEntry.getObjectDefinitionId());

		if (serviceContext.isStrictAdd()) {
			boolean indexingEnabled = serviceContext.isIndexingEnabled();

			serviceContext.setIndexingEnabled(false);

			try {
				_updateAsset(
					serviceContext.getUserId(), objectEntry,
					serviceContext.getAssetCategoryIds(),
					serviceContext.getAssetTagNames(),
					serviceContext.getAssetLinkEntryIds(),
					serviceContext.getAssetPriority(), serviceContext);
			}
			finally {
				serviceContext.setIndexingEnabled(indexingEnabled);
			}
		}
		else {
			_assetEntryLocalService.updateEntry(
				objectDefinition.getClassName(), objectEntry.getObjectEntryId(),
				null, null, true, objectEntry.isApproved());
		}

		_reindex(objectEntry);

		if ((status == WorkflowConstants.STATUS_EXPIRED) ||
			originalObjectEntry.isDraft() || originalObjectEntry.isPending()) {

			int count =
				_objectEntryVersionLocalService.getObjectEntryVersionsCount(
					objectEntry.getObjectEntryId());

			if (count > 0) {
				_updateLatestObjectEntryVersion(objectDefinition, objectEntry);
			}
		}
		else if (!objectEntry.isInTrash() && !originalObjectEntry.isInTrash()) {
			objectEntry = _addObjectEntryVersion(objectDefinition, objectEntry);
		}

		if (objectDefinition.isRootNode()) {
			_updateRootDescendantNodeObjectEntryStatus(
				userId, objectEntry, serviceContext);
		}

		if (!ObjectActionThreadLocal.isSkipObjectActionExecution()) {
			String objectActionTriggerKey =
				ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE;

			if (originalObjectEntry.isInTrash()) {
				objectActionTriggerKey =
					ObjectActionTriggerConstants.KEY_ON_AFTER_ADD;
			}
			else if (objectEntry.isInTrash()) {
				objectActionTriggerKey =
					ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE;
			}

			_executeObjectActions(
				objectEntry.getCompanyId(), objectActionTriggerKey,
				objectDefinition, objectEntry, originalObjectEntry,
				serviceContext.getLanguageId(), user);
		}

		return objectEntry;
	}

	@Override
	public void validate(
			long groupId, ObjectEntry objectEntry,
			List<String> objectValidationRuleExternalReferenceCodes,
			ServiceContext serviceContext, long userId)
		throws PortalException {

		List<ValidationError> validationErrors = new ArrayList<>();

		try {
			_objectValidationRuleLocalService.validate(
				objectValidationRuleExternalReferenceCodes, objectEntry,
				userId);
		}
		catch (ObjectValidationRuleEngineException
					objectValidationRuleEngineException) {

			ListUtil.isNotEmptyForEach(
				objectValidationRuleEngineException.
					getObjectValidationRuleResults(),
				objectValidationRuleResult -> validationErrors.add(
					new ValidationError(
						objectValidationRuleResult.getErrorMessage(),
						objectValidationRuleResult.getObjectFieldName(),
						objectValidationRuleResult.
							getExternalReferenceCode())));
		}

		_validateValues(
			objectEntry.getDefaultLanguageId(), Collections.emptyMap(),
			objectEntry.getValues(), groupId, false,
			_objectDefinitionPersistence.findByPrimaryKey(
				objectEntry.getObjectDefinitionId()),
			null,
			_objectFieldLocalService.getObjectFields(
				objectEntry.getObjectDefinitionId()),
			true, serviceContext, objectEntry.getStatus(),
			serviceContext.getUserId(), validationErrors,
			objectEntry.getValues());

		if (ListUtil.isNotEmpty(validationErrors)) {
			throw new ObjectEntryValidationException() {
				{
					setValidationErrors(validationErrors);
				}
			};
		}
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		modified(properties);

		_putObjectFilterParser(
			new CurrentUserObjectFilterParser(),
			ObjectFilterConstants.TYPE_CURRENT_USER);
		_putObjectFilterParser(
			new DateRangeObjectFilterParser(),
			ObjectFilterConstants.TYPE_DATE_RANGE);
		_putObjectFilterParser(
			new EqualityOperatorsObjectFilterParser(),
			ObjectFilterConstants.TYPE_EQUALS,
			ObjectFilterConstants.TYPE_NOT_EQUALS);
		_putObjectFilterParser(
			new InclusionOperatorsObjectFilterParser(),
			ObjectFilterConstants.TYPE_EXCLUDES,
			ObjectFilterConstants.TYPE_INCLUDES);

		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, ObjectEntryValuesContributor.class);
	}

	@Deactivate
	@Override
	protected void deactivate() {
		super.deactivate();

		_serviceTrackerList.close();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_objectConfiguration = ConfigurableUtil.createConfigurable(
			ObjectConfiguration.class, properties);
	}

	@Reference
	protected ConfigurationProvider configurationProvider;

	private void _addDLFileEntries(
			Map<ObjectField, Set<DLFileEntry>> dlFileEntriesMap, long groupId,
			ObjectDefinition objectDefinition, long objectEntryId,
			ServiceContext serviceContext, long userId,
			Map<String, Serializable> values)
		throws PortalException {

		for (Map.Entry<ObjectField, Set<DLFileEntry>> entry :
				dlFileEntriesMap.entrySet()) {

			for (DLFileEntry dlFileEntry : entry.getValue()) {
				_addDLFileEntry(
					dlFileEntry, groupId, objectDefinition, objectEntryId,
					entry.getKey(), serviceContext, userId, values);
			}
		}
	}

	private void _addDLFileEntry(
			DLFileEntry dlFileEntry, long groupId,
			ObjectDefinition objectDefinition, long objectEntryId,
			ObjectField objectField, ServiceContext serviceContext, long userId,
			Map<String, Serializable> values)
		throws PortalException {

		String fileSource = ObjectFieldSettingUtil.getValue(
			"fileSource", objectField.getObjectFieldSettings());

		if (Objects.equals(fileSource, "documentsAndMedia")) {
			return;
		}

		DLFolder dlFileEntryFolder = dlFileEntry.getFolder();

		if (groupId == 0) {
			groupId = dlFileEntry.getGroupId();
		}

		DLFolder dlFolder = _attachmentManager.getDLFolder(
			dlFileEntry.getCompanyId(), groupId, objectField.getObjectFieldId(),
			serviceContext, userId);

		if (Objects.equals(
				dlFileEntryFolder.getFolderId(), dlFolder.getFolderId())) {

			return;
		}

		String originalFileName = TempFileEntryUtil.getOriginalTempFileName(
			dlFileEntry.getFileName());

		serviceContext.setAttribute(
			"className", objectDefinition.getClassName());
		serviceContext.setAttribute("classPK", objectEntryId);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, userId, dlFolder.getRepositoryId(), dlFolder.getFolderId(),
			DLUtil.getUniqueFileName(
				dlFileEntry.getGroupId(), dlFolder.getFolderId(),
				originalFileName, true),
			dlFileEntry.getMimeType(),
			DLUtil.getUniqueTitle(
				dlFileEntry.getGroupId(), dlFolder.getFolderId(),
				FileUtil.stripExtension(originalFileName)),
			StringPool.BLANK, null, null, dlFileEntry.getContentStream(),
			dlFileEntry.getSize(), null, null, null, serviceContext);

		if (objectField.isLocalized()) {
			Map<String, Serializable> localizedValues =
				(Map<String, Serializable>)values.get(
					objectField.getI18nObjectFieldName());

			for (Map.Entry<String, Serializable> entry :
					localizedValues.entrySet()) {

				if (dlFileEntry.getFileEntryId() != GetterUtil.getLong(
						entry.getValue())) {

					continue;
				}

				entry.setValue(fileEntry.getFileEntryId());
			}
		}
		else {
			values.put(objectField.getName(), fileEntry.getFileEntryId());
		}
	}

	private void _addFriendlyURLEntry(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			ServiceContext serviceContext, Map<String, Serializable> values)
		throws PortalException {

		if (ObjectDefinitionUtil.isDefaultFriendlyURLSeparator(
				objectDefinition.getFriendlyURLSeparator())) {

			return;
		}

		long classNameId = _classNameLocalService.getClassNameId(
			objectDefinition.getClassName());

		Map<String, String> friendlyUrlMap = new HashMap<>();

		if (objectDefinition.isEnableFriendlyURLCustomization()) {
			friendlyUrlMap = (Map<String, String>)serviceContext.getAttribute(
				"friendlyUrlMap");
		}

		if (friendlyUrlMap == null) {
			friendlyUrlMap = new HashMap<>();
		}

		long groupId = objectEntry.getNonzeroGroupId();
		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectDefinition.getTitleObjectFieldId());
		Map<String, String> urlTitleMap = new HashMap<>();

		for (Map.Entry<String, String> entry : friendlyUrlMap.entrySet()) {
			if (Validator.isNull(entry.getValue())) {
				continue;
			}

			urlTitleMap.put(
				entry.getKey(),
				_friendlyURLEntryLocalService.getUniqueUrlTitle(
					groupId, classNameId, objectEntry.getObjectEntryId(),
					entry.getValue(), entry.getKey()));
		}

		urlTitleMap.computeIfAbsent(
			objectEntry.getDefaultLanguageId(),
			key -> _getUrlTitle(
				classNameId, groupId, objectEntry.getDefaultLanguageId(),
				objectEntry, objectField,
				HashMapBuilder.<String, Object>putAll(
					values
				).putAll(
					objectEntry.getModelAttributes()
				).build()));

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			groupId, classNameId, objectEntry.getObjectEntryId(),
			objectEntry.getDefaultLanguageId(), urlTitleMap, serviceContext);
	}

	private JoinStep _addInnerJoinON(
		Column<?, ?> column,
		DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
		DynamicObjectDefinitionTable extensionDynamicObjectDefinitionTable,
		JoinStep joinStep, Column<?, Long> primaryKeyColumn1,
		Set<String> tableNames) {

		Table<?> table = column.getTable();

		if (tableNames.contains(table.getName()) ||
			Objects.equals(
				table.getName(), ObjectEntryTable.INSTANCE.getName())) {

			return joinStep;
		}

		tableNames.add(table.getName());

		Column<?, Long> primaryKeyColumn2 = _getPrimaryKeyColumn(
			dynamicObjectDefinitionTable, extensionDynamicObjectDefinitionTable,
			table.getTableName());

		return joinStep.innerJoinON(
			table, primaryKeyColumn2.eq(primaryKeyColumn1));
	}

	private void _addLocalizedObjectFieldValues(
			String defaultLanguageId,
			DynamicObjectDefinitionLocalizationTable
				dynamicObjectDefinitionLocalizationTable,
			ObjectFieldBag objectFieldBag, long primaryKey,
			Map<String, Serializable> values)
		throws PortalException {

		if (dynamicObjectDefinitionLocalizationTable == null) {
			return;
		}

		Expression<?>[] selectExpressions = ArrayUtil.append(
			_getSelectExpressions(dynamicObjectDefinitionLocalizationTable),
			dynamicObjectDefinitionLocalizationTable.getLanguageIdColumn());

		List<Object[]> rows = _list(
			DSLQueryFactoryUtil.select(
				selectExpressions
			).from(
				dynamicObjectDefinitionLocalizationTable
			).where(
				dynamicObjectDefinitionLocalizationTable.getForeignKeyColumn(
				).eq(
					primaryKey
				)
			),
			objectFieldBag, selectExpressions);

		if (ListUtil.isEmpty(rows)) {
			return;
		}

		List<Column<DynamicObjectDefinitionLocalizationTable, ?>>
			objectFieldColumns =
				dynamicObjectDefinitionLocalizationTable.
					getObjectFieldColumns();

		for (int i = 0; i < objectFieldColumns.size(); i++) {
			Column<DynamicObjectDefinitionLocalizationTable, ?>
				objectFieldColumn = objectFieldColumns.get(i);

			Map<String, Serializable> localizedValues = new HashMap<>();

			for (Object[] row : rows) {
				Object localizedValue = row[i];

				if (!(localizedValue instanceof Long) &&
					Validator.isNull(localizedValue)) {

					continue;
				}

				_putValue(
					objectFieldColumn.getJavaType(),
					String.valueOf(row[objectFieldColumns.size()]),
					localizedValue, localizedValues);
			}

			_putLocalizedValues(
				objectFieldColumn.getName(), defaultLanguageId, localizedValues,
				values);
		}
	}

	private ObjectEntry _addObjectEntry(
			String externalReferenceCode, long groupId, long userId,
			long headObjectEntryId, long objectDefinitionId,
			long objectEntryFolderId, String defaultLanguageId, int version,
			int status)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryPersistence.create(
			counterLocalService.increment());

		objectEntry.setExternalReferenceCode(externalReferenceCode);
		objectEntry.setGroupId(groupId);

		User user = _userLocalService.getUser(userId);

		objectEntry.setCompanyId(user.getCompanyId());
		objectEntry.setUserId(user.getUserId());
		objectEntry.setUserName(user.getFullName());

		objectEntry.setHeadObjectEntryId(
			(headObjectEntryId == 0) ? objectEntry.getObjectEntryId() :
				headObjectEntryId);
		objectEntry.setObjectDefinitionId(objectDefinitionId);
		objectEntry.setObjectEntryFolderId(objectEntryFolderId);
		objectEntry.setDefaultLanguageId(defaultLanguageId);
		objectEntry.setTreePath(objectEntry.buildTreePath());
		objectEntry.setVersion(version);
		objectEntry.setStatus(status);
		objectEntry.setStatusDate(new Date());

		return objectEntryPersistence.updateImpl(objectEntry);
	}

	private ObjectEntry _addObjectEntryVersion(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws PortalException {

		if (!objectDefinition.isEnableObjectEntryVersioning()) {
			return objectEntry;
		}

		ObjectEntryVersion objectEntryVersion =
			_objectEntryVersionLocalService.addObjectEntryVersion(objectEntry);

		objectEntry.setVersion(objectEntryVersion.getVersion());

		return objectEntryPersistence.update(objectEntry);
	}

	private void _addObjectRelationshipERCFieldValue(
		List<ObjectField> objectFields, Map<String, Serializable> values) {

		for (ObjectField objectField : objectFields) {
			if (!Objects.equals(
					objectField.getRelationshipType(),
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

				continue;
			}

			long primaryKey = GetterUtil.getLong(
				values.get(objectField.getName()));

			if (primaryKey == 0) {
				continue;
			}

			ObjectRelationship objectRelationship =
				_objectRelationshipPersistence.fetchByObjectFieldId2(
					objectField.getObjectFieldId());

			ObjectDefinition objectDefinition =
				_objectDefinitionPersistence.fetchByPrimaryKey(
					objectRelationship.getObjectDefinitionId1());

			if (objectDefinition == null) {
				continue;
			}

			String objectRelationshipERCObjectFieldName =
				ObjectFieldSettingUtil.getValue(
					ObjectFieldSettingConstants.
						NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
					objectField);

			if (objectDefinition.isUnmodifiableSystemObject()) {
				SystemObjectDefinitionManager systemObjectDefinitionManager =
					_systemObjectDefinitionManagerRegistry.
						getSystemObjectDefinitionManager(
							objectDefinition.getName());

				try {
					values.put(
						objectRelationshipERCObjectFieldName,
						systemObjectDefinitionManager.
							getBaseModelExternalReferenceCode(primaryKey));
				}
				catch (PortalException portalException) {
					if (_log.isDebugEnabled()) {
						_log.debug(portalException);
					}
				}

				continue;
			}

			ObjectEntry objectEntry = objectEntryPersistence.fetchByPrimaryKey(
				primaryKey);

			String externalReferenceCode = StringPool.BLANK;

			if (objectEntry != null) {
				externalReferenceCode = objectEntry.getExternalReferenceCode();
			}

			values.put(
				objectRelationshipERCObjectFieldName, externalReferenceCode);
		}
	}

	private void _addOrUpdateComments(
			long groupId, long userId, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry, ServiceContext serviceContext)
		throws PortalException {

		List<ObjectEntryComment> objectEntryComments =
			(List<ObjectEntryComment>)serviceContext.getAttribute(
				"objectEntryComments");

		if (!objectDefinition.isEnableComments() ||
			ListUtil.isEmpty(objectEntryComments)) {

			return;
		}

		if (groupId == 0) {
			groupId = objectEntry.getNonzeroGroupId();
		}

		Map<String, Comment> comments = new HashMap<>();
		Set<Long> deleteCommentIds = new HashSet<>();

		for (Comment comment :
				_commentManager.getComments(
					objectDefinition.getClassName(),
					objectEntry.getObjectEntryId(),
					WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS)) {

			comments.put(comment.getExternalReferenceCode(), comment);

			if (!comment.isRoot()) {
				deleteCommentIds.add(comment.getCommentId());
			}
		}

		User user = _userLocalService.getUser(userId);

		for (ObjectEntryComment objectEntryComment : objectEntryComments) {
			Comment comment = comments.get(
				objectEntryComment.getExternalReferenceCode());

			if (comment == null) {
				_discussionPermission.checkAddPermission(
					PermissionThreadLocal.getPermissionChecker(),
					objectDefinition.getCompanyId(), groupId,
					objectDefinition.getClassName(),
					objectEntry.getObjectEntryId());

				if (Validator.isNotNull(
						objectEntryComment.
							getParentCommentExternalReferenceCode())) {

					Comment parentComment =
						_commentManager.getOrAddEmptyComment(
							objectEntryComment.
								getParentCommentExternalReferenceCode(),
							userId, groupId, objectDefinition.getClassName(),
							objectEntry.getObjectEntryId());

					_commentManager.addComment(
						objectEntryComment.getExternalReferenceCode(), userId,
						objectDefinition.getClassName(),
						objectEntry.getObjectEntryId(), user.getFullName(),
						parentComment.getCommentId(), null,
						objectEntryComment.getText(),
						_createServiceContextFunction());
				}
				else {
					_commentManager.addComment(
						objectEntryComment.getExternalReferenceCode(), userId,
						groupId, objectDefinition.getClassName(),
						objectEntry.getObjectEntryId(), user.getFullName(),
						null, objectEntryComment.getText(),
						_createServiceContextFunction());
				}

				continue;
			}

			_discussionPermission.checkUpdatePermission(
				PermissionThreadLocal.getPermissionChecker(),
				comment.getCommentId());

			_commentManager.updateComment(
				userId, objectDefinition.getClassName(),
				objectEntry.getObjectEntryId(), comment.getCommentId(),
				StringPool.BLANK, objectEntryComment.getText(),
				_createServiceContextFunction());

			deleteCommentIds.remove(comment.getCommentId());
		}

		for (Long deleteCommentId : deleteCommentIds) {
			_commentManager.deleteComment(deleteCommentId);
		}
	}

	private void _addResourcePermissions(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws PortalException {

		if (ListUtil.isEmpty(
				_resourceActions.getModelResourceActions(
					objectDefinition.getClassName()))) {

			return;
		}

		_resourcePermissionLocalService.addResourcePermissions(
			objectEntry.getCompanyId(), objectEntry.getGroupId(),
			objectEntry.getUserId(), objectDefinition.getClassName(),
			String.valueOf(objectEntry.getPrimaryKey()), false,
			new ServiceContext() {
				{
					setIndexingEnabled(false);
					setStrictAdd(true);
				}
			});
	}

	private DSLQuery _applyOrderBy(
			DSLQuery dslQuery, ObjectDefinition objectDefinition, int start,
			int end, Sort[] sorts)
		throws PortalException {

		if (((end != QueryUtil.ALL_POS) || (start != QueryUtil.ALL_POS)) &&
			(sorts == null)) {

			sorts = new Sort[] {new Sort("id", Sort.LONG_TYPE, false)};
		}

		if (sorts == null) {
			return dslQuery;
		}

		SortDSLQueryVisitor sortDSLQueryVisitor = new SortDSLQueryVisitor(
			_objectFieldLocalService,
			_objectRelationshipLocalServiceSnapshot.get());

		for (Sort sort : sorts) {
			dslQuery = sortDSLQueryVisitor.visit(
				dslQuery,
				new com.liferay.object.internal.sort.Sort(
					objectDefinition, sort));
		}

		return dslQuery;
	}

	private void _checkObjectDefinitionScope(
			long objectDefinitionId, long groupId)
		throws PortalException {

		ObjectDefinitionSetting acceptAllObjectDefinitionSetting =
			_objectDefinitionSettingPersistence.fetchByODI_N(
				objectDefinitionId,
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS);

		if ((acceptAllObjectDefinitionSetting != null) &&
			GetterUtil.getBoolean(
				acceptAllObjectDefinitionSetting.getValue())) {

			return;
		}

		ObjectDefinitionSetting acceptedGroupsObjectDefinitionSetting =
			_objectDefinitionSettingPersistence.fetchByODI_N(
				objectDefinitionId,
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS);

		if ((acceptedGroupsObjectDefinitionSetting != null) &&
			Validator.isNull(
				acceptedGroupsObjectDefinitionSetting.getValue())) {

			throw new ObjectDefinitionScopeException(
				StringBundler.concat(
					"The object definition ", objectDefinitionId,
					" requires explicit group IDs for accepted group IDs  ",
					"setting"));
		}
		else if (acceptedGroupsObjectDefinitionSetting != null) {
			String acceptedGroupIdsString =
				acceptedGroupsObjectDefinitionSetting.getValue();

			String[] acceptedGroupIds = StringUtil.split(
				acceptedGroupIdsString);

			if (!ArrayUtil.contains(
					acceptedGroupIds, String.valueOf(groupId))) {

				throw new ObjectDefinitionScopeException(
					StringBundler.concat(
						"The group ", groupId,
						" is not included in the object definition's accepted ",
						"group IDs setting"));
			}
		}
	}

	private void _checkObjectEntriesByDisplayDate(long companyId, Date date)
		throws PortalException {

		List<ObjectEntry> objectEntries = objectEntryPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				ObjectEntryTable.INSTANCE
			).from(
				ObjectEntryTable.INSTANCE
			).where(
				ObjectEntryTable.INSTANCE.companyId.eq(
					companyId
				).and(
					ObjectEntryTable.INSTANCE.displayDate.gte(
						_companyIdPreviousCheckDate.get(companyId))
				).and(
					ObjectEntryTable.INSTANCE.displayDate.lte(date)
				).and(
					ObjectEntryTable.INSTANCE.status.eq(
						WorkflowConstants.STATUS_SCHEDULED)
				)
			));

		for (ObjectEntry objectEntry : objectEntries) {
			updateStatus(
				objectEntry.getUserId(), objectEntry,
				WorkflowConstants.STATUS_APPROVED, new ServiceContext());
		}
	}

	private void _checkObjectEntriesByExpirationDate(long companyId, Date date)
		throws PortalException {

		List<ObjectEntry> objectEntries = objectEntryPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				ObjectEntryTable.INSTANCE
			).from(
				ObjectEntryTable.INSTANCE
			).where(
				ObjectEntryTable.INSTANCE.companyId.eq(
					companyId
				).and(
					ObjectEntryTable.INSTANCE.expirationDate.gte(
						_companyIdPreviousCheckDate.get(companyId))
				).and(
					ObjectEntryTable.INSTANCE.expirationDate.lte(date)
				).and(
					ObjectEntryTable.INSTANCE.status.notIn(
						new Integer[] {
							WorkflowConstants.STATUS_DRAFT,
							WorkflowConstants.STATUS_PENDING
						})
				)
			));

		for (ObjectEntry objectEntry : objectEntries) {
			expireObjectEntry(
				objectEntry.getUserId(), objectEntry.getObjectEntryId(),
				new ServiceContext());
		}
	}

	private void _checkObjectEntriesByReviewDate(long companyId, Date date)
		throws PortalException {

		List<ObjectEntry> objectEntries = objectEntryPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				ObjectEntryTable.INSTANCE
			).from(
				ObjectEntryTable.INSTANCE
			).where(
				ObjectEntryTable.INSTANCE.companyId.eq(
					companyId
				).and(
					ObjectEntryTable.INSTANCE.reviewDate.gte(
						_companyIdPreviousCheckDate.get(companyId))
				).and(
					ObjectEntryTable.INSTANCE.reviewDate.lte(date)
				)
			));

		for (ObjectEntry objectEntry : objectEntries) {
			ObjectDefinition objectDefinition =
				_objectDefinitionPersistence.fetchByPrimaryKey(
					objectEntry.getObjectDefinitionId());

			_userNotificationEventLocalService.sendUserNotificationEvents(
				objectEntry.getUserId(), objectDefinition.getPortletId(),
				UserNotificationDeliveryConstants.TYPE_WEBSITE, false,
				JSONUtil.put(
					"classPK", objectEntry.getObjectEntryId()
				).put(
					"notificationMessage",
					StringBundler.concat(
						"The object entry ", objectEntry.getTitleValue(),
						" has reached its review date.")
				));
		}
	}

	private void _contributeValues(
		long groupId, ObjectDefinition objectDefinition, long userId,
		Map<String, Serializable> values) {

		for (ObjectEntryValuesContributor objectEntryValuesContributor :
				_serviceTrackerList) {

			if (objectEntryValuesContributor instanceof CompanyScoped) {
				CompanyScoped companyScoped =
					(CompanyScoped)objectEntryValuesContributor;

				if (!companyScoped.isAllowedCompany(
						objectDefinition.getCompanyId())) {

					continue;
				}
			}

			if (objectEntryValuesContributor instanceof
					ObjectDefinitionScoped) {

				ObjectDefinitionScoped objectDefinitionScoped =
					(ObjectDefinitionScoped)objectEntryValuesContributor;

				if (!objectDefinitionScoped.isAllowedObjectDefinition(
						objectDefinition.getName())) {

					continue;
				}
			}

			objectEntryValuesContributor.contribute(
				new ObjectEntryContext(
					groupId, objectDefinition.getObjectDefinitionId(), userId,
					values));
		}
	}

	private int _count(
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
			long primaryKey)
		throws PortalException {

		/*int count = objectEntryPersistence.dslQueryCount(
			_getExtensionDynamicObjectDefinitionTableCountDSLQuery(
				dynamicObjectDefinitionTable, primaryKey));*/

		// TODO Temporary workaround for LPS-178639

		Connection connection = _currentConnection.getConnection(
			objectEntryPersistence.getDataSource());

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select count(*) from ",
					dynamicObjectDefinitionTable.getTableName(), " where ",
					dynamicObjectDefinitionTable.getPrimaryKeyColumnName(),
					" = ?"))) {

			preparedStatement.setLong(1, primaryKey);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				resultSet.next();

				return resultSet.getInt(1);
			}
		}
		catch (SQLException sqlException) {
			throw new SystemException(sqlException);
		}
	}

	private Function<String, ServiceContext> _createServiceContextFunction() {
		return className -> {
			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

			return serviceContext;
		};
	}

	private void _deleteFileEntries(
		Map<String, Serializable> newValues, long objectDefinitionId,
		Map<String, Serializable> oldValues) {

		List<ObjectField> objectFields =
			_objectFieldPersistence.findByObjectDefinitionId(
				objectDefinitionId);

		for (ObjectField objectField : objectFields) {
			if (objectField.isSystem() ||
				!Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

				continue;
			}

			ObjectFieldSetting objectFieldSetting =
				_objectFieldSettingPersistence.fetchByOFI_N(
					objectField.getObjectFieldId(), "fileSource");

			if (!Objects.equals(
					objectFieldSetting.getValue(), "userComputer")) {

				continue;
			}

			objectFieldSetting = _objectFieldSettingPersistence.fetchByOFI_N(
				objectField.getObjectFieldId(), "showFilesInDocumentsAndMedia");

			if ((objectFieldSetting != null) &&
				GetterUtil.getBoolean(objectFieldSetting.getValue())) {

				continue;
			}

			List<Long> orphanedFileEntryIds = new ArrayList<>();

			if (objectField.isLocalized()) {
				Map<String, Serializable> oldLocalizedValues =
					(Map<String, Serializable>)oldValues.get(
						objectField.getI18nObjectFieldName());

				if (oldLocalizedValues == null) {
					continue;
				}

				Map<String, Serializable> newLocalizedValues =
					(Map<String, Serializable>)newValues.getOrDefault(
						objectField.getI18nObjectFieldName(),
						(Serializable)Collections.emptyMap());

				Collection<Serializable> values = newLocalizedValues.values();

				for (Map.Entry<String, Serializable> entry :
						oldLocalizedValues.entrySet()) {

					if (values.contains(entry.getValue())) {
						continue;
					}

					orphanedFileEntryIds.add(
						GetterUtil.getLong(entry.getValue()));
				}
			}
			else {
				String objectFieldName = objectField.getName();

				if (Objects.equals(
						GetterUtil.getLong(newValues.get(objectFieldName)),
						GetterUtil.getLong(oldValues.get(objectFieldName)))) {

					continue;
				}

				orphanedFileEntryIds.add(
					GetterUtil.getLong(oldValues.get(objectFieldName)));
			}

			try {
				for (Long orphanedFileEntryId : orphanedFileEntryIds) {
					if (orphanedFileEntryId == 0) {
						continue;
					}

					_dlFileEntryLocalService.deleteFileEntry(
						orphanedFileEntryId);
				}
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}
	}

	private void _deleteFromLocalizationTable(
			ObjectDefinition objectDefinition, long objectEntryId)
		throws PortalException {

		DynamicObjectDefinitionLocalizationTable
			dynamicObjectDefinitionLocalizationTable =
				DynamicObjectDefinitionLocalizationTableFactory.create(
					objectDefinition, _objectFieldLocalService);

		if (dynamicObjectDefinitionLocalizationTable == null) {
			return;
		}

		_deleteFromTable(
			dynamicObjectDefinitionLocalizationTable.getTableName(),
			dynamicObjectDefinitionLocalizationTable.getForeignKeyColumnName(),
			objectEntryId);
	}

	private void _deleteFromTable(
			String dbTableName, String pkObjectFieldDBColumnName,
			long primaryKey)
		throws PortalException {

		Session session = objectEntryPersistence.openSession();

		try {
			session.apply(
				connection -> {
					try (PreparedStatement preparedStatement =
							connection.prepareStatement(
								StringBundler.concat(
									"delete from ", dbTableName, " where ",
									pkObjectFieldDBColumnName, " = ?"))) {

						preparedStatement.setLong(1, primaryKey);

						preparedStatement.executeUpdate();
					}
				});
		}
		finally {
			objectEntryPersistence.closeSession(session);
		}

		FinderCacheUtil.clearDSLQueryCache(dbTableName);
	}

	private void _deleteRelatedObjectEntries(
			long groupId, boolean moveToTrash, long objectDefinitionId,
			long primaryKey)
		throws PortalException {

		List<ObjectRelationship> objectRelationships =
			_objectRelationshipPersistence.findByObjectDefinitionId1(
				objectDefinitionId);

		for (ObjectRelationship objectRelationship : objectRelationships) {
			ObjectDefinition objectDefinition2 =
				_objectDefinitionPersistence.findByPrimaryKey(
					objectRelationship.getObjectDefinitionId2());

			if (WorkflowConstants.STATUS_DRAFT ==
					objectDefinition2.getStatus()) {

				continue;
			}

			ObjectRelatedModelsProvider objectRelatedModelsProvider =
				_objectRelatedModelsProviderRegistry.
					getObjectRelatedModelsProvider(
						objectDefinition2.getClassName(),
						objectDefinition2.getCompanyId(),
						objectRelationship.getType());

			try {
				ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(
					true);

				String deletionType = objectRelationship.getDeletionType();

				if (ObjectEntryThreadLocal.isDisassociateRelatedModels()) {
					deletionType =
						ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE;
				}

				if (moveToTrash) {
					objectRelatedModelsProvider.moveRelatedModelToTrash(
						PrincipalThreadLocal.getUserId(), groupId,
						objectRelationship.getObjectRelationshipId(),
						primaryKey, deletionType);
				}
				else {
					objectRelatedModelsProvider.deleteRelatedModel(
						PrincipalThreadLocal.getUserId(), groupId,
						objectRelationship.getObjectRelationshipId(),
						primaryKey, deletionType);
				}
			}
			catch (PrincipalException principalException) {
				throw new ObjectRelationshipDeletionTypeException(
					principalException.getMessage());
			}
			finally {
				ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(
					false);
			}
		}
	}

	private void _deleteTempFileEntries(
			Map<ObjectField, Set<DLFileEntry>> dlFileEntriesMap)
		throws PortalException {

		for (Set<DLFileEntry> dlFileEntries : dlFileEntriesMap.values()) {
			for (DLFileEntry dlFileEntry : dlFileEntries) {
				TempFileEntryUtil.deleteTempFileEntry(
					dlFileEntry.getFileEntryId());
			}
		}
	}

	private void _executeObjectActions(
		long companyId, String objectActionTriggerKey,
		ObjectDefinition objectDefinition, ObjectEntry objectEntry,
		ObjectEntry originalObjectEntry, String preferredLanguageId,
		User user) {

		ObjectActionEngine objectActionEngine =
			_objectActionEngineSnapshot.get();

		objectActionEngine.executeObjectActions(
			objectDefinition.getClassName(), companyId, objectActionTriggerKey,
			() -> {
				JSONObject payloadJSONObject =
					ObjectEntryPayloadUtil.getPayloadJSONObject(
						_dtoConverterRegistry, _jsonFactory,
						objectActionTriggerKey, objectDefinition, objectEntry,
						originalObjectEntry, preferredLanguageId, user);

				objectEntry.setValues(null);

				return payloadJSONObject;
			},
			user.getUserId());

		if (!FeatureFlagManagerUtil.isEnabled(
				objectDefinition.getCompanyId(), "LPD-34594") ||
			(!objectEntry.isRootDescendantNode() &&
			 (!objectDefinition.isRootNode() ||
			  StringUtil.equals(
				  objectActionTriggerKey,
				  ObjectActionTriggerConstants.KEY_ON_AFTER_ADD)))) {

			return;
		}

		ObjectEntry rootObjectEntry = fetchObjectEntry(
			objectEntry.getRootObjectEntryId());

		if (rootObjectEntry == null) {
			return;
		}

		objectActionEngine.executeObjectActions(
			rootObjectEntry.getModelClassName(), companyId,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ROOT_UPDATE,
			() -> {
				JSONObject payloadJSONObject =
					ObjectEntryPayloadUtil.getPayloadJSONObject(
						_dtoConverterRegistry, _jsonFactory,
						ObjectActionTriggerConstants.KEY_ON_AFTER_ROOT_UPDATE,
						_objectDefinitionPersistence.fetchByPrimaryKey(
							rootObjectEntry.getObjectDefinitionId()),
						rootObjectEntry, null, preferredLanguageId, user);

				rootObjectEntry.setValues(null);

				return payloadJSONObject;
			},
			user.getUserId());
	}

	private void _fillDefaultValue(
		String defaultLanguageId, long objectDefinitionId,
		Map<String, Serializable> values) {

		for (ObjectField objectField :
				_objectFieldPersistence.findByObjectDefinitionId(
					objectDefinitionId)) {

			Map<String, Object> localizedValues =
				(Map<String, Object>)values.getOrDefault(
					objectField.getI18nObjectFieldName(), new HashMap<>());

			if ((objectField.isLocalized() &&
				 (localizedValues.get(defaultLanguageId) != null)) ||
				(!objectField.isLocalized() &&
				 (values.get(objectField.getName()) != null))) {

				continue;
			}

			objectField.setObjectFieldSettings(
				_objectFieldSettingLocalService.
					getObjectFieldObjectFieldSettings(objectField));

			Object value = ObjectFieldSettingUtil.getDefaultValue(
				_ddmExpressionFactory, objectField, (Map)values);

			if (value != null) {
				values.put(objectField.getName(), (Serializable)value);

				if (!objectField.isLocalized()) {
					continue;
				}

				if (localizedValues.isEmpty()) {
					values.put(
						objectField.getI18nObjectFieldName(),
						HashMapBuilder.put(
							defaultLanguageId, value
						).build());
				}
				else {
					localizedValues.putIfAbsent(defaultLanguageId, value);
				}
			}
		}
	}

	private Predicate _fillPredicate(
			long objectDefinitionId, Predicate predicate, String search)
		throws PortalException {

		if (Validator.isNull(search)) {
			return predicate;
		}

		List<ObjectField> objectFields = _objectFieldPersistence.findByODI_I(
			objectDefinitionId, true);

		if (objectFields.isEmpty()) {
			return predicate;
		}

		Predicate searchPredicate = null;

		for (ObjectField objectField : objectFields) {
			Table<?> table = _objectFieldLocalService.getTable(
				objectDefinitionId, objectField.getName());

			Column<?, ?> column = table.getColumn(
				objectField.getDBColumnName());

			if (column == null) {
				continue;
			}

			Predicate objectFieldPredicate = null;

			if (Objects.equals(
					objectField.getRelationshipType(),
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

				objectFieldPredicate = _getRelationshipObjectFieldPredicate(
					column, objectField, search);
			}
			else {
				objectFieldPredicate =
					ObjectEntrySearchUtil.getObjectFieldPredicate(
						objectField.getBusinessType(), column,
						objectField.getDBType(), search);
			}

			if (objectFieldPredicate == null) {
				continue;
			}

			if (searchPredicate == null) {
				searchPredicate = objectFieldPredicate;
			}
			else {
				searchPredicate = searchPredicate.or(objectFieldPredicate);
			}
		}

		if (predicate == null) {
			if (searchPredicate == null) {
				return null;
			}

			return searchPredicate.withParentheses();
		}

		return Predicate.withParentheses(
			predicate
		).and(
			searchPredicate.withParentheses()
		);
	}

	private DSLQuery _getAccountEntriesDSLQuery(long companyId, long userId)
		throws PortalException {

		JoinStep joinStep = DSLQueryFactoryUtil.select(
			AccountEntryTable.INSTANCE.accountEntryId
		).from(
			AccountEntryTable.INSTANCE
		);

		if (_roleLocalService.hasUserRole(
				userId, companyId, RoleConstants.ADMINISTRATOR, true)) {

			return joinStep.where(
				AccountEntryTable.INSTANCE.companyId.eq(
					companyId
				).and(
					AccountEntryTable.INSTANCE.status.eq(
						WorkflowConstants.STATUS_APPROVED)
				));
		}

		Table<OrganizationTable> tempOrganizationTable =
			DSLQueryFactoryUtil.select(
				OrganizationTable.INSTANCE.companyId,
				OrganizationTable.INSTANCE.treePath
			).from(
				OrganizationTable.INSTANCE
			).innerJoinON(
				Users_OrgsTable.INSTANCE,
				Users_OrgsTable.INSTANCE.organizationId.eq(
					OrganizationTable.INSTANCE.organizationId)
			).where(
				Users_OrgsTable.INSTANCE.userId.eq(userId)
			).as(
				"tempOrganizationTable", OrganizationTable.INSTANCE
			);

		return joinStep.innerJoinON(
			AccountEntryOrganizationRelTable.INSTANCE,
			AccountEntryOrganizationRelTable.INSTANCE.accountEntryId.eq(
				AccountEntryTable.INSTANCE.accountEntryId)
		).where(
			AccountEntryOrganizationRelTable.INSTANCE.organizationId.in(
				DSLQueryFactoryUtil.selectDistinct(
					OrganizationTable.INSTANCE.organizationId
				).from(
					OrganizationTable.INSTANCE
				).innerJoinON(
					tempOrganizationTable,
					OrganizationTable.INSTANCE.companyId.eq(
						tempOrganizationTable.getColumn("companyId", Long.class)
					).and(
						OrganizationTable.INSTANCE.treePath.like(
							DSLFunctionFactoryUtil.concat(
								DSLFunctionFactoryUtil.castText(
									tempOrganizationTable.getColumn(
										"treePath", String.class)),
								new Scalar<>(StringPool.PERCENT)))
					)
				)
			).and(
				_getAccountEntryWherePredicate()
			)
		).union(
			joinStep.where(
				AccountEntryTable.INSTANCE.userId.eq(
					userId
				).and(
					_getAccountEntryWherePredicate()
				))
		).union(
			joinStep.innerJoinON(
				AccountEntryUserRelTable.INSTANCE,
				AccountEntryUserRelTable.INSTANCE.accountEntryId.eq(
					AccountEntryTable.INSTANCE.accountEntryId)
			).where(
				AccountEntryUserRelTable.INSTANCE.accountUserId.eq(
					userId
				).and(
					_getAccountEntryWherePredicate()
				)
			)
		);
	}

	private Predicate _getAccountEntryWherePredicate() {
		return AccountEntryTable.INSTANCE.parentAccountEntryId.eq(
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT
		).and(
			AccountEntryTable.INSTANCE.status.eq(
				WorkflowConstants.STATUS_APPROVED)
		).and(
			AccountEntryTable.INSTANCE.type.in(
				new String[] {
					AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
					AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON
				})
		);
	}

	private DSLQuery _getAggregationObjectFieldDSLQuery(
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
			ObjectDefinition objectDefinition,
			Map<String, Object> objectFieldSettingsValues,
			SystemObjectDefinitionManager systemObjectDefinitionManager)
		throws PortalException {

		ObjectRelationship objectRelationship =
			ObjectRelationshipUtil.getObjectRelationship(
				_objectRelationshipPersistence.findByODI1_N(
					objectDefinition.getObjectDefinitionId(),
					GetterUtil.getString(
						objectFieldSettingsValues.get(
							"objectRelationshipName"))));

		ObjectDefinition relatedObjectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId2());

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(
				relatedObjectDefinition.getObjectDefinitionId());

		DynamicObjectDefinitionTable relatedDynamicObjectDefinitionTable =
			new DynamicObjectDefinitionTable(
				relatedObjectDefinition, objectFields,
				relatedObjectDefinition.getDBTableName());
		DynamicObjectDefinitionTable
			relatedExtensionDynamicObjectDefinitionTable =
				new DynamicObjectDefinitionTable(
					relatedObjectDefinition, objectFields,
					relatedObjectDefinition.getExtensionDBTableName());

		if (objectRelationship.isSelf()) {
			relatedDynamicObjectDefinitionTable =
				relatedDynamicObjectDefinitionTable.as(
					"aliasDynamicObjectDefinitionTable");
			relatedExtensionDynamicObjectDefinitionTable =
				relatedExtensionDynamicObjectDefinitionTable.as(
					"aliasExtensionDynamicObjectDefinitionTable");
		}

		Column<?, ?> column =
			relatedDynamicObjectDefinitionTable.getPrimaryKeyColumn();

		String function = GetterUtil.getString(
			objectFieldSettingsValues.get(
				ObjectFieldSettingConstants.NAME_FUNCTION));

		if (!Objects.equals(
				function, ObjectFieldSettingConstants.VALUE_COUNT)) {

			column = _objectFieldLocalService.getColumn(
				relatedObjectDefinition.getObjectDefinitionId(),
				GetterUtil.getString(
					objectFieldSettingsValues.get(
						ObjectFieldSettingConstants.NAME_OBJECT_FIELD_NAME)));

			if (objectRelationship.isSelf()) {
				Table table = column.getTable();

				if (Objects.equals(
						table.getTableName(),
						relatedDynamicObjectDefinitionTable.getTableName())) {

					column = relatedDynamicObjectDefinitionTable.getColumn(
						column.getName());
				}
				else {
					column =
						relatedExtensionDynamicObjectDefinitionTable.getColumn(
							column.getName());
				}
			}
		}

		Table<?> table = column.getTable();

		JoinStep joinStep = DSLQueryFactoryUtil.select(
			_getFunctionExpression(column, function)
		).from(
			table
		);

		Column<?, Long> primaryKeyColumn = _getPrimaryKeyColumn(
			relatedDynamicObjectDefinitionTable,
			relatedExtensionDynamicObjectDefinitionTable, table.getTableName());

		Set<String> tableNames = SetUtil.fromArray(table.getName());

		Predicate predicate = null;

		if (Objects.equals(
				objectRelationship.getType(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY)) {

			DynamicObjectRelationshipMappingTable
				dynamicObjectRelationshipMappingTable =
					DynamicObjectRelationshipMappingTableFactory.create(
						objectRelationship.getDBTableName(), objectDefinition,
						relatedObjectDefinition);

			joinStep = joinStep.innerJoinON(
				dynamicObjectRelationshipMappingTable,
				dynamicObjectRelationshipMappingTable.getPrimaryKeyColumn2(
				).eq(
					primaryKeyColumn
				));

			if (systemObjectDefinitionManager != null) {
				predicate =
					dynamicObjectRelationshipMappingTable.getPrimaryKeyColumn1(
					).eq(
						systemObjectDefinitionManager.getPrimaryKeyColumn()
					);
			}
			else {
				predicate =
					dynamicObjectRelationshipMappingTable.getPrimaryKeyColumn1(
					).eq(
						dynamicObjectDefinitionTable.getPrimaryKeyColumn()
					);
			}
		}
		else if (Objects.equals(
					objectRelationship.getType(),
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

			ObjectField relationshipObjectField =
				_objectFieldLocalService.getObjectField(
					objectRelationship.getObjectFieldId2());

			Column<DynamicObjectDefinitionTable, Long>
				relationshipObjectFieldColumn =
					(Column<DynamicObjectDefinitionTable, Long>)
						_objectFieldLocalService.getColumn(
							relatedObjectDefinition.getObjectDefinitionId(),
							relationshipObjectField.getName());

			joinStep = _addInnerJoinON(
				relationshipObjectFieldColumn,
				relatedDynamicObjectDefinitionTable,
				relatedExtensionDynamicObjectDefinitionTable, joinStep,
				primaryKeyColumn, tableNames);

			if (systemObjectDefinitionManager != null) {
				predicate = relationshipObjectFieldColumn.eq(
					systemObjectDefinitionManager.getPrimaryKeyColumn());
			}
			else {
				predicate = relationshipObjectFieldColumn.eq(
					dynamicObjectDefinitionTable.getPrimaryKeyColumn());
			}
		}

		for (ObjectFilter objectFilter :
				(List<ObjectFilter>)objectFieldSettingsValues.get("filters")) {

			joinStep = _addInnerJoinON(
				_objectFieldLocalService.getColumn(
					relatedObjectDefinition.getObjectDefinitionId(),
					objectFilter.getFilterBy()),
				relatedDynamicObjectDefinitionTable,
				relatedExtensionDynamicObjectDefinitionTable, joinStep,
				primaryKeyColumn, tableNames);

			if (StringUtil.equals(
					objectFilter.getFilterType(),
					ObjectFilterConstants.TYPE_CURRENT_USER)) {

				objectFilter.setJSON(
					JSONUtil.put(
						"currentUserId", PrincipalThreadLocal.getUserId()
					).toString());
			}

			ObjectFilterParser objectFilterParser = _objectFilterParsers.get(
				objectFilter.getFilterType());

			predicate = predicate.and(
				_filterFactory.create(
					objectFilterParser.parse(objectFilter),
					relatedObjectDefinition));
		}

		joinStep = joinStep.innerJoinON(
			ObjectEntryTable.INSTANCE,
			ObjectEntryTable.INSTANCE.objectEntryId.eq(primaryKeyColumn));

		predicate = predicate.and(_getHeadObjectEntryPredicate(false));

		return joinStep.where(predicate);
	}

	private String _getAutoIncrementSortableValue(
		String prefix, String suffix, String value) {

		return StringUtil.removeLast(
			StringUtil.removeFirst(value, prefix), suffix);
	}

	private String _getAutoIncrementValue(
		String counterName, String initialValue, String prefix, String suffix,
		String valueString) {

		long currentId = counterLocalService.getCurrentId(counterName);

		long value = GetterUtil.getLong(
			_getAutoIncrementSortableValue(prefix, suffix, valueString));

		if ((currentId == 0) || (value > currentId)) {
			currentId = Math.max(value, GetterUtil.getLong(initialValue));

			counterLocalService.reset(counterName, currentId);
		}
		else if (value == 0) {
			currentId = counterLocalService.increment(counterName);
		}
		else {
			currentId = value;
		}

		StringBuilder sb = new StringBuilder(String.valueOf(currentId));

		while (sb.length() < initialValue.length()) {
			sb.insert(0, CharPool.NUMBER_0);
		}

		return StringBundler.concat(
			GetterUtil.getString(prefix), sb, GetterUtil.getString(suffix));
	}

	private Map<String, Object> _getColumns(
		DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
		ObjectDefinition objectDefinition) {

		Map<String, Object> columns = new HashMap<>();

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId());

		for (ObjectField objectField : objectFields) {
			if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_AGGREGATION) ||
				Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT) ||
				Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_FORMULA) ||
				Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT)) {

				continue;
			}

			if (Objects.equals(objectField.getName(), "id")) {
				columns.put(
					objectField.getName(),
					dynamicObjectDefinitionTable.getPrimaryKeyColumn());

				continue;
			}

			Column<?, Object> column =
				(Column<?, Object>)_objectFieldLocalService.getColumn(
					objectDefinition.getObjectDefinitionId(),
					objectField.getName());

			columns.put(objectField.getName(), column);
		}

		return columns;
	}

	private String _getDBType(ObjectField objectField) throws PortalException {
		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingPersistence.fetchByOFI_N(
				objectField.getObjectFieldId(), "output");

		ObjectFieldBusinessType objectFieldBusinessType =
			_objectFieldBusinessTypeRegistry.getObjectFieldBusinessType(
				objectFieldSetting.getValue());

		return objectFieldBusinessType.getDBType();
	}

	private String _getDefaultLanguageId(String defaultLanguageId, long groupId)
		throws PortalException {

		if (Validator.isNull(defaultLanguageId)) {
			return _language.getLanguageId(
				_portal.getSiteDefaultLocale(groupId));
		}

		if (_language.isAvailableLocale(defaultLanguageId)) {
			return defaultLanguageId;
		}

		throw new ObjectEntryDefaultLanguageIdException(
			"Language ID " + defaultLanguageId + " is not available");
	}

	private DSLQuery _getExtensionDynamicObjectDefinitionTableSelectDSLQuery(
			DynamicObjectDefinitionTable extensionDynamicObjectDefinitionTable,
			long primaryKey, Expression<?>[] selectExpressions,
			SystemObjectDefinitionManager systemObjectDefinitionManager)
		throws PortalException {

		if (systemObjectDefinitionManager != null) {
			return DSLQueryFactoryUtil.select(
				selectExpressions
			).from(
				systemObjectDefinitionManager.getTable()
			).leftJoinOn(
				extensionDynamicObjectDefinitionTable,
				systemObjectDefinitionManager.getPrimaryKeyColumn(
				).eq(
					extensionDynamicObjectDefinitionTable.getPrimaryKeyColumn()
				)
			).where(
				systemObjectDefinitionManager.getPrimaryKeyColumn(
				).eq(
					primaryKey
				)
			);
		}

		return DSLQueryFactoryUtil.select(
			selectExpressions
		).from(
			extensionDynamicObjectDefinitionTable
		).where(
			extensionDynamicObjectDefinitionTable.getPrimaryKeyColumn(
			).eq(
				primaryKey
			)
		);
	}

	private DSLQuery _getFetchManyToOneObjectEntryDSLQuery(
		DynamicObjectDefinitionTable dynamicObjectDefinitionTable, long groupId,
		ObjectRelationship objectRelationship, long primaryKey,
		Column<DynamicObjectDefinitionTable, Long> primaryKeyColumn) {

		FromStep fromStep = DSLQueryFactoryUtil.selectDistinct(
			ObjectEntryTable.INSTANCE);
		ObjectField objectField = _objectFieldPersistence.fetchByPrimaryKey(
			objectRelationship.getObjectFieldId2());

		return fromStep.from(
			dynamicObjectDefinitionTable
		).innerJoinON(
			ObjectEntryTable.INSTANCE,
			ObjectEntryTable.INSTANCE.objectEntryId.eq(
				(Expression<Long>)dynamicObjectDefinitionTable.getColumn(
					objectField.getDBColumnName()))
		).where(
			primaryKeyColumn.eq(
				primaryKey
			).and(
				ObjectEntryTable.INSTANCE.groupId.eq(groupId)
			)
		);
	}

	private Expression<?> _getFunctionExpression(
		Column<?, ?> column, String function) {

		if (function.equals("AVERAGE")) {
			return DSLFunctionFactoryUtil.avg(
				(Expression<? extends Number>)column);
		}

		if (function.equals("COUNT")) {
			return DSLFunctionFactoryUtil.count(column);
		}

		if (function.equals("MAX")) {
			return DSLFunctionFactoryUtil.max(
				(Expression<? extends Comparable>)column);
		}

		if (function.equals("MIN")) {
			return DSLFunctionFactoryUtil.min(
				(Expression<? extends Comparable>)column);
		}

		if (function.equals("SUM")) {
			return DSLFunctionFactoryUtil.sum(
				(Expression<? extends Number>)column);
		}

		throw new IllegalArgumentException("Invalid function " + function);
	}

	private Predicate _getHeadObjectEntryPredicate(boolean preferApproved) {
		if (preferApproved) {
			return ObjectEntryTable.INSTANCE.status.eq(
				WorkflowConstants.STATUS_APPROVED);
		}

		return ObjectEntryTable.INSTANCE.objectEntryId.eq(
			ObjectEntryTable.INSTANCE.headObjectEntryId);
	}

	private Map<Long, Map<String, Serializable>> _getIndexedValuesMap(
		ObjectEntry objectEntry, ObjectDefinition objectDefinition,
		List<ObjectField> indexedObjectFields) {

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				false, objectDefinition, indexedObjectFields);
		DynamicObjectDefinitionTable extensionDynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				true, objectDefinition, indexedObjectFields);
		Predicate innerJoinPredicate = null;

		List<Column<DynamicObjectDefinitionTable, ?>> selectColumns =
			new ArrayList<>(dynamicObjectDefinitionTable.getColumns());

		Collection<Column<DynamicObjectDefinitionTable, ?>> extensionColumns =
			extensionDynamicObjectDefinitionTable.getColumns();

		if (extensionColumns.size() > 1) {
			innerJoinPredicate =
				dynamicObjectDefinitionTable.getPrimaryKeyColumn(
				).eq(
					extensionDynamicObjectDefinitionTable.getPrimaryKeyColumn()
				);

			Column<?, ?> pkColumn =
				extensionDynamicObjectDefinitionTable.getPrimaryKeyColumn();

			for (Column<DynamicObjectDefinitionTable, ?> column :
					extensionColumns) {

				if (column == pkColumn) {
					continue;
				}

				selectColumns.add(column);
			}
		}

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			selectColumns.toArray(new Expression<?>[0])
		).from(
			dynamicObjectDefinitionTable
		).innerJoinON(
			extensionDynamicObjectDefinitionTable, innerJoinPredicate
		);

		if (objectEntry != null) {
			JoinStep joinStep = (JoinStep)dslQuery;

			dslQuery = joinStep.where(
				dynamicObjectDefinitionTable.getPrimaryKeyColumn(
				).eq(
					objectEntry.getObjectEntryId()
				));
		}

		List<Map<String, Serializable>> valuesList = _list(
			selectColumns, dslQuery);

		if (objectEntry == null) {
			Map<Long, Map<String, Serializable>> indexedValuesMap =
				new HashMap<>();

			for (Map<String, Serializable> values : valuesList) {
				indexedValuesMap.put(
					(Long)values.get(objectDefinition.getPKObjectFieldName()),
					values);
			}

			return indexedValuesMap;
		}

		if (valuesList.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, Serializable> values = valuesList.get(0);

		return Collections.singletonMap(
			(Long)values.get(objectDefinition.getPKObjectFieldName()), values);
	}

	private Predicate _getInnerJoinRootObjectDefinitionTablePredicate(
		DynamicObjectDefinitionTable dynamicObjectDefinitionTable) {

		if (dynamicObjectDefinitionTable == null) {
			return null;
		}

		return dynamicObjectDefinitionTable.getPrimaryKeyColumn(
		).eq(
			ObjectEntryTable.INSTANCE.rootObjectEntryId
		);
	}

	private Key _getKey() throws PortalException {
		return new SecretKeySpec(
			Base64.decode(PropsValues.OBJECT_ENCRYPTION_KEY),
			PropsValues.OBJECT_ENCRYPTION_ALGORITHM);
	}

	private Set<Locale> _getLocales(
		long companyId, List<ObjectField> objectFields,
		Map<String, Serializable> originalValues, boolean partialUpdate,
		Map<String, Serializable> values) {

		Set<Locale> locales = new HashSet<>();

		for (ObjectField objectField : objectFields) {
			Map<String, String> localizedValues =
				(Map<String, String>)values.get(
					objectField.getI18nObjectFieldName());

			if (MapUtil.isEmpty(localizedValues) && partialUpdate) {
				localizedValues = (Map<String, String>)originalValues.get(
					objectField.getI18nObjectFieldName());
			}

			if (MapUtil.isEmpty(localizedValues)) {
				continue;
			}

			for (String languageId : localizedValues.keySet()) {
				locales.add(LocaleUtil.fromLanguageId(languageId, true, false));
			}
		}

		return SetUtil.intersect(
			locales, _language.getCompanyAvailableLocales(companyId));
	}

	private Object _getLocalizedValue(
		String languageId, Map<String, Object> localizedValues) {

		if (localizedValues == null) {
			return null;
		}
		else if (!localizedValues.containsKey(languageId)) {
			return StringPool.BLANK;
		}

		return localizedValues.get(languageId);
	}

	private GroupByStep _getManyToManyObjectEntriesGroupByStep(
			FromStep fromStep, long groupId, long objectRelationshipId,
			long primaryKey, boolean related, boolean reverse, String search)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipPersistence.findByPrimaryKey(
				objectRelationshipId);

		long objectDefinitionId1 = objectRelationship.getObjectDefinitionId1();

		long objectDefinitionId2 = objectRelationship.getObjectDefinitionId2();

		ObjectDefinition objectDefinition2 =
			_objectDefinitionPersistence.fetchByPrimaryKey(objectDefinitionId2);

		DynamicObjectDefinitionLocalizationTable
			dynamicObjectDefinitionLocalizationTable =
				DynamicObjectDefinitionLocalizationTableFactory.create(
					objectDefinition2, _objectFieldLocalService);
		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				false, objectDefinition2, _objectFieldLocalService);

		DynamicObjectDefinitionTable extensionDynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				true, objectDefinition2, _objectFieldLocalService);

		Column<DynamicObjectDefinitionTable, Long>
			dynamicObjectDefinitionTablePrimaryKeyColumn =
				dynamicObjectDefinitionTable.getPrimaryKeyColumn();

		ObjectDefinition objectDefinition1 =
			_objectDefinitionPersistence.fetchByPrimaryKey(objectDefinitionId1);

		DynamicObjectRelationshipMappingTable
			dynamicObjectRelationshipMappingTable =
				DynamicObjectRelationshipMappingTableFactory.create(
					objectRelationship.getDBTableName(), objectDefinition1,
					objectDefinition2, reverse);

		Column<DynamicObjectRelationshipMappingTable, Long> primaryKeyColumn1 =
			dynamicObjectRelationshipMappingTable.getPrimaryKeyColumn1();
		Column<DynamicObjectRelationshipMappingTable, Long> primaryKeyColumn2 =
			dynamicObjectRelationshipMappingTable.getPrimaryKeyColumn2();

		return fromStep.from(
			dynamicObjectDefinitionTable
		).innerJoinON(
			ObjectEntryTable.INSTANCE,
			ObjectEntryTable.INSTANCE.objectEntryId.eq(
				dynamicObjectDefinitionTablePrimaryKeyColumn)
		).innerJoinON(
			extensionDynamicObjectDefinitionTable,
			extensionDynamicObjectDefinitionTable.getPrimaryKeyColumn(
			).eq(
				dynamicObjectDefinitionTablePrimaryKeyColumn
			)
		).leftJoinOn(
			dynamicObjectDefinitionLocalizationTable,
			ObjectEntrySearchUtil.getLeftJoinLocalizationTablePredicate(
				dynamicObjectDefinitionLocalizationTable,
				dynamicObjectDefinitionTable, null)
		).leftJoinOn(
			dynamicObjectRelationshipMappingTable,
			primaryKeyColumn2.eq(dynamicObjectDefinitionTablePrimaryKeyColumn)
		).where(
			ObjectEntryTable.INSTANCE.companyId.eq(
				objectRelationship.getCompanyId()
			).and(
				() -> {
					if (Objects.equals(
							objectDefinition2.getScope(),
							ObjectDefinitionConstants.SCOPE_COMPANY) ||
						(groupId == 0)) {

						return null;
					}

					return ObjectEntryTable.INSTANCE.groupId.eq(groupId);
				}
			).and(
				ObjectEntryTable.INSTANCE.objectDefinitionId.eq(
					objectDefinitionId2)
			).and(
				() -> {
					if (ObjectEntryThreadLocal.
							isSkipObjectEntryResourcePermission() ||
						(PermissionThreadLocal.getPermissionChecker() ==
							null)) {

						return null;
					}

					return _getPermissionWherePredicate(
						dynamicObjectDefinitionTable, groupId, 0L);
				}
			).and(
				() -> {
					if (related) {
						return primaryKeyColumn1.eq(primaryKey);
					}

					return dynamicObjectDefinitionTablePrimaryKeyColumn.notIn(
						DSLQueryFactoryUtil.select(
							primaryKeyColumn2
						).from(
							dynamicObjectRelationshipMappingTable
						).where(
							primaryKeyColumn1.eq(primaryKey)
						));
				}
			).and(
				() -> {
					if (objectDefinition1.getObjectDefinitionId() !=
							objectDefinition2.getObjectDefinitionId()) {

						return null;
					}

					return dynamicObjectDefinitionTablePrimaryKeyColumn.neq(
						primaryKey);
				}
			).and(
				_getHeadObjectEntryPredicate(false)
			).and(
				ObjectEntrySearchUtil.getRelatedModelsPredicate(
					objectDefinition2, _objectFieldLocalService, search,
					dynamicObjectDefinitionTable)
			)
		);
	}

	private GroupByStep _getObjectEntriesGroupByStep(
			Long[] groupIds, FromStep fromStep,
			ObjectDefinition objectDefinition, Predicate predicate,
			boolean preferApproved, String search)
		throws PortalException {

		DynamicObjectDefinitionLocalizationTable
			dynamicObjectDefinitionLocalizationTable =
				DynamicObjectDefinitionLocalizationTableFactory.create(
					objectDefinition, _objectFieldLocalService);
		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				false, objectDefinition, _objectFieldLocalService);
		DynamicObjectDefinitionTable extensionDynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				true, objectDefinition, _objectFieldLocalService);

		return fromStep.from(
			dynamicObjectDefinitionTable
		).innerJoinON(
			extensionDynamicObjectDefinitionTable,
			extensionDynamicObjectDefinitionTable.getPrimaryKeyColumn(
			).eq(
				dynamicObjectDefinitionTable.getPrimaryKeyColumn()
			)
		).innerJoinON(
			ObjectEntryTable.INSTANCE,
			ObjectEntryTable.INSTANCE.objectEntryId.eq(
				dynamicObjectDefinitionTable.getPrimaryKeyColumn())
		).leftJoinOn(
			dynamicObjectDefinitionLocalizationTable,
			ObjectEntrySearchUtil.getLeftJoinLocalizationTablePredicate(
				dynamicObjectDefinitionLocalizationTable,
				dynamicObjectDefinitionTable, null)
		).where(
			ObjectEntryTable.INSTANCE.objectDefinitionId.eq(
				objectDefinition.getObjectDefinitionId()
			).and(
				ObjectEntryTable.INSTANCE.rootObjectEntryId.eq(
					ObjectEntryTable.INSTANCE.objectEntryId
				).or(
					ObjectEntryTable.INSTANCE.rootObjectEntryId.eq(0L)
				).withParentheses()
			).and(
				ObjectEntryTable.INSTANCE.status.neq(
					WorkflowConstants.STATUS_IN_TRASH)
			).and(
				() -> {
					if (ArrayUtil.isEmpty(groupIds)) {
						return null;
					}

					return ObjectEntryTable.INSTANCE.groupId.in(groupIds);
				}
			).and(
				Predicate.withParentheses(
					_fillPredicate(
						objectDefinition.getObjectDefinitionId(), predicate,
						search))
			).and(
				_getHeadObjectEntryPredicate(preferApproved)
			).and(
				_getPermissionWherePredicate(
					dynamicObjectDefinitionTable, groupIds)
			)
		);
	}

	private long _getObjectEntryCheckInterval(long companyId) {
		try {
			ObjectEntryScheduleConfiguration objectEntryScheduleConfiguration =
				configurationProvider.getCompanyConfiguration(
					ObjectEntryScheduleConfiguration.class, companyId);

			return objectEntryScheduleConfiguration.checkInterval() *
				Time.MINUTE;
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private GroupByStep _getOneToManyObjectEntriesGroupByStep(
			FromStep fromStep, long groupId, long objectRelationshipId,
			Predicate predicate, boolean preferApproved, long primaryKey,
			boolean related, String search)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipPersistence.findByPrimaryKey(
				objectRelationshipId);

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId2());

		DynamicObjectDefinitionLocalizationTable
			dynamicObjectDefinitionLocalizationTable =
				DynamicObjectDefinitionLocalizationTableFactory.create(
					objectDefinition, _objectFieldLocalService);
		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				false, objectDefinition, _objectFieldLocalService);
		DynamicObjectDefinitionTable extensionDynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				true, objectDefinition, _objectFieldLocalService);

		ObjectEntry objectEntry = fetchObjectEntry(primaryKey);
		ObjectField objectField = _objectFieldPersistence.fetchByPrimaryKey(
			objectRelationship.getObjectFieldId2());
		Column<DynamicObjectDefinitionTable, Long> primaryKeyColumn =
			dynamicObjectDefinitionTable.getPrimaryKeyColumn();

		DynamicObjectDefinitionTable rootDynamicObjectDefinitionTable =
			_getRootDynamicObjectDefinitionTable(objectEntry);

		return fromStep.from(
			dynamicObjectDefinitionTable
		).innerJoinON(
			extensionDynamicObjectDefinitionTable,
			extensionDynamicObjectDefinitionTable.getPrimaryKeyColumn(
			).eq(
				primaryKeyColumn
			)
		).innerJoinON(
			ObjectEntryTable.INSTANCE,
			ObjectEntryTable.INSTANCE.objectEntryId.eq(primaryKeyColumn)
		).innerJoinON(
			rootDynamicObjectDefinitionTable,
			_getInnerJoinRootObjectDefinitionTablePredicate(
				rootDynamicObjectDefinitionTable)
		).leftJoinOn(
			dynamicObjectDefinitionLocalizationTable,
			ObjectEntrySearchUtil.getLeftJoinLocalizationTablePredicate(
				dynamicObjectDefinitionLocalizationTable,
				dynamicObjectDefinitionTable, null)
		).where(
			ObjectEntryTable.INSTANCE.companyId.eq(
				objectRelationship.getCompanyId()
			).and(
				() -> {
					if (!related && (groupId > 0)) {
						return ObjectEntryTable.INSTANCE.groupId.eq(groupId);
					}

					return null;
				}
			).and(
				ObjectEntryTable.INSTANCE.objectDefinitionId.eq(
					objectRelationship.getObjectDefinitionId2())
			).and(
				() -> {
					Column<?, Long> column =
						(Column<?, Long>)_objectFieldLocalService.getColumn(
							objectField.getObjectDefinitionId(),
							objectField.getName());

					return column.eq(related ? primaryKey : 0L);
				}
			).and(
				() ->
					objectRelationship.isSelf() ?
						primaryKeyColumn.neq(primaryKey) : null
			).and(
				_getHeadObjectEntryPredicate(preferApproved)
			).and(
				() -> {
					if (ObjectEntryThreadLocal.
							isSkipObjectEntryResourcePermission() ||
						(PermissionThreadLocal.getPermissionChecker() ==
							null)) {

						return null;
					}

					long rootObjectDefinitionId = 0L;

					if ((objectEntry != null) &&
						(objectEntry.getRootObjectEntryId() != 0)) {

						ObjectEntry rootObjectEntry = fetchObjectEntry(
							objectEntry.getRootObjectEntryId());

						rootObjectDefinitionId =
							rootObjectEntry.getObjectDefinitionId();
					}

					return _getPermissionWherePredicate(
						dynamicObjectDefinitionTable, groupId,
						rootObjectDefinitionId);
				}
			).and(
				Predicate.withParentheses(
					_fillPredicate(
						objectRelationship.getObjectDefinitionId2(), predicate,
						search))
			)
		);
	}

	private Predicate _getPermissionWherePredicate(
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
			long groupId, long rootObjectDefinitionId)
		throws PortalException {

		ObjectDefinition objectDefinition =
			dynamicObjectDefinitionTable.getObjectDefinition();

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((permissionChecker == null) ||
			!_inlineSQLHelper.isEnabled(
				objectDefinition.getCompanyId(), groupId)) {

			return null;
		}

		Column<?, Long> primaryKeyColumn =
			ObjectEntryTable.INSTANCE.headObjectEntryId;

		if (rootObjectDefinitionId != 0L) {
			objectDefinition = _objectDefinitionPersistence.findByPrimaryKey(
				rootObjectDefinitionId);

			primaryKeyColumn = ObjectEntryTable.INSTANCE.rootObjectEntryId;
		}

		Predicate individualScopePredicate =
			_inlineSQLHelper.getPermissionWherePredicate(
				objectDefinition.getClassName(), primaryKeyColumn, groupId);

		if (individualScopePredicate == null) {
			return null;
		}

		if (!objectDefinition.isAccountEntryRestricted()) {
			return individualScopePredicate;
		}

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinition.getAccountEntryRestrictedObjectFieldId());

		Table<?> table = _objectFieldLocalService.getTable(
			objectDefinition.getObjectDefinitionId(), objectField.getName());

		Column<?, Long> column = (Column<?, Long>)table.getColumn(
			objectField.getDBColumnName());

		return individualScopePredicate.or(
			column.in(
				_getAccountEntriesDSLQuery(
					objectDefinition.getCompanyId(),
					permissionChecker.getUserId())
			).withParentheses()
		).withParentheses();
	}

	private Predicate _getPermissionWherePredicate(
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
			Long[] groupIds)
		throws PortalException {

		Predicate permissionWherePredicate = null;

		for (Long groupId : groupIds) {
			if (permissionWherePredicate == null) {
				permissionWherePredicate = _getPermissionWherePredicate(
					dynamicObjectDefinitionTable, groupId, 0L);
			}
			else {
				permissionWherePredicate = permissionWherePredicate.or(
					_getPermissionWherePredicate(
						dynamicObjectDefinitionTable, groupId, 0L));
			}
		}

		return permissionWherePredicate;
	}

	private Column<?, Long> _getPrimaryKeyColumn(
		DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
		DynamicObjectDefinitionTable extensionDynamicObjectDefinitionTable,
		String tableName) {

		if (tableName.equals(dynamicObjectDefinitionTable.getTableName())) {
			return dynamicObjectDefinitionTable.getPrimaryKeyColumn();
		}
		else if (tableName.equals(
					extensionDynamicObjectDefinitionTable.getTableName())) {

			return extensionDynamicObjectDefinitionTable.getPrimaryKeyColumn();
		}

		return ObjectEntryTable.INSTANCE.objectEntryId;
	}

	private Map<String, Object> _getQueryExpressions(
			ObjectDefinition objectDefinition2, long primaryKey, String script)
		throws PortalException {

		Map<String, Object> queryExpressions = new HashMap<>();

		for (ObjectRelationship objectRelationship :
				_objectRelationshipPersistence.findByODI2_R_T(
					objectDefinition2.getObjectDefinitionId(), false,
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

			ObjectField relationshipObjectField =
				_objectFieldLocalService.fetchObjectField(
					objectRelationship.getObjectFieldId2());

			if (!script.contains(
					relationshipObjectField.getName() + StringPool.UNDERLINE)) {

				continue;
			}

			ObjectDefinition objectDefinition1 =
				_objectDefinitionPersistence.findByPrimaryKey(
					objectRelationship.getObjectDefinitionId1());

			Table<?> objectDefinition2Table = _objectFieldLocalService.getTable(
				objectDefinition2.getObjectDefinitionId(),
				relationshipObjectField.getName());

			Column<?, Long> objectDefinition2PrimaryKeyColumn =
				ObjectEntrySearchUtil.getPrimaryKeyColumn(
					objectDefinition2.getPKObjectFieldDBColumnName(),
					objectDefinition2Table);

			for (ObjectField objectField :
					_objectFieldLocalService.getObjectFields(
						objectDefinition1.getObjectDefinitionId())) {

				String key =
					relationshipObjectField.getName() + StringPool.UNDERLINE +
						objectField.getName();

				if (!script.contains(key)) {
					continue;
				}

				Table<?> objectDefinition1Table =
					_objectFieldLocalService.getTable(
						objectDefinition1.getObjectDefinitionId(),
						objectField.getName());

				Column<?, Long> objectDefinition1PrimaryKeyColumn =
					ObjectEntrySearchUtil.getPrimaryKeyColumn(
						objectDefinition1.getPKObjectFieldDBColumnName(),
						objectDefinition1Table);

				Predicate predicate = null;

				if (objectField.isLocalized()) {
					DynamicObjectDefinitionLocalizationTable
						dynamicObjectDefinitionLocalizationTable =
							(DynamicObjectDefinitionLocalizationTable)
								objectDefinition1Table;

					predicate =
						dynamicObjectDefinitionLocalizationTable.
							getLanguageIdColumn(
							).eq(
								ObjectEntrySearchUtil.getLanguageId()
							);
				}

				queryExpressions.put(
					key,
					new QueryExpression<>(
						DSLQueryFactoryUtil.select(
							objectDefinition1Table.getColumn(
								objectField.getDBColumnName())
						).from(
							objectDefinition1Table
						).innerJoinON(
							objectDefinition2Table,
							objectDefinition1PrimaryKeyColumn.eq(
								(Column<?, Long>)
									objectDefinition2Table.getColumn(
										relationshipObjectField.getName()))
						).where(
							objectDefinition2PrimaryKeyColumn.eq(
								primaryKey
							).and(
								predicate
							)
						)));
			}
		}

		return queryExpressions;
	}

	private Predicate _getRelationshipObjectFieldPredicate(
			Column<?, ?> column, ObjectField objectField, String search)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipPersistence.fetchByObjectFieldId2(
				objectField.getObjectFieldId());

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId1());

		ObjectField titleObjectField =
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getTitleObjectFieldId());

		Table<?> table = _objectFieldLocalService.getTable(
			objectDefinition.getObjectDefinitionId(),
			titleObjectField.getName());

		if (Objects.equals(
				table.getColumn(titleObjectField.getDBColumnName()),
				ObjectEntryTable.INSTANCE.objectEntryId)) {

			return ObjectEntrySearchUtil.getObjectFieldPredicate(
				titleObjectField.getBusinessType(), column,
				objectField.getDBType(), search);
		}

		Predicate relatedModelsPredicate =
			ObjectEntrySearchUtil.getRelatedModelsPredicate(
				objectDefinition, _objectFieldLocalService, search, table);

		if (relatedModelsPredicate == null) {
			return null;
		}

		Column<?, Long> primaryKeyColumn =
			ObjectEntrySearchUtil.getPrimaryKeyColumn(
				objectDefinition.getPKObjectFieldDBColumnName(), table);

		return column.in(
			DSLQueryFactoryUtil.select(
				table.getColumn(primaryKeyColumn.getName())
			).from(
				table
			).where(
				relatedModelsPredicate
			));
	}

	private Object _getResult(
			Object entryValues, ObjectFieldBag objectFieldBag,
			Expression<?> selectExpression)
		throws PortalException {

		Object result = null;

		if (selectExpression instanceof Alias) {
			Alias<?> alias = (Alias<?>)selectExpression;

			result = _getValue(
				entryValues,
				DynamicObjectDefinitionTableUtil.getSQLType(
					_getDBType(
						objectFieldBag.getObjectField(alias.getName()))));
		}
		else if (selectExpression instanceof Column) {
			Column<?, ?> column = (Column<?, ?>)selectExpression;

			result = _getValue(entryValues, column.getSQLType());
		}
		else if (selectExpression instanceof ScalarDSLQueryAlias) {
			ScalarDSLQueryAlias scalarDSLQueryAlias =
				(ScalarDSLQueryAlias)selectExpression;

			result = _getValue(entryValues, scalarDSLQueryAlias.getSQLType());

			if (result == null) {
				result = "0";
			}
			else {
				BigDecimal bigDecimal = new BigDecimal(result.toString());

				result = String.valueOf(
					BigDecimalUtil.stripTrailingZeros(bigDecimal));
			}
		}

		return result;
	}

	private DynamicObjectDefinitionTable _getRootDynamicObjectDefinitionTable(
			ObjectEntry objectEntry)
		throws PortalException {

		if ((objectEntry == null) ||
			(objectEntry.getRootObjectEntryId() == 0)) {

			return null;
		}

		ObjectEntry rootObjectEntry = getObjectEntry(
			objectEntry.getRootObjectEntryId());

		ObjectDefinition rootObjectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				rootObjectEntry.getObjectDefinitionId());

		if (!rootObjectDefinition.isAccountEntryRestricted()) {
			return null;
		}

		ObjectField objectField = _objectFieldPersistence.findByPrimaryKey(
			rootObjectDefinition.getAccountEntryRestrictedObjectFieldId());

		if (Objects.equals(
				objectField.getDBTableName(),
				rootObjectDefinition.getDBTableName())) {

			return DynamicObjectDefinitionTableUtil.
				getDynamicObjectDefinitionTable(
					false, rootObjectDefinition, _objectFieldLocalService);
		}

		return DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
			true, rootObjectDefinition, _objectFieldLocalService);
	}

	private Expression<?>[] _getSelectExpressions(
		DynamicObjectDefinitionLocalizationTable
			dynamicObjectDefinitionLocalizationTable) {

		if (dynamicObjectDefinitionLocalizationTable == null) {
			return new Expression<?>[0];
		}

		List<Expression<?>> selectExpressions = new ArrayList<>(
			dynamicObjectDefinitionLocalizationTable.getObjectFieldColumns());

		return selectExpressions.toArray(new Expression<?>[0]);
	}

	private Expression<?>[] _getSelectExpressions(
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
			long primaryKey, String[] selectedObjectFieldNames,
			SystemObjectDefinitionManager systemObjectDefinitionManager)
		throws PortalException {

		List<Expression<?>> selectExpressions = new ArrayList<>();

		for (Column<DynamicObjectDefinitionTable, ?> column :
				dynamicObjectDefinitionTable.getColumns()) {

			if ((selectedObjectFieldNames != null) &&
				!ArrayUtil.contains(
					selectedObjectFieldNames,
					StringUtil.removeLast(
						column.getName(), StringPool.UNDERLINE)) &&
				!Objects.equals(
					column.getName(),
					dynamicObjectDefinitionTable.getPrimaryKeyColumnName())) {

				continue;
			}

			selectExpressions.add(column);
		}

		Map<String, Object> columns = null;

		for (ObjectField objectField :
				dynamicObjectDefinitionTable.getObjectFields()) {

			if (!objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_AGGREGATION) &&
				!objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_FORMULA)) {

				continue;
			}

			if ((selectedObjectFieldNames != null) &&
				!ArrayUtil.contains(
					selectedObjectFieldNames, objectField.getName())) {

				continue;
			}

			Map<String, Object> objectFieldSettingsValues = new HashMap<>();

			List<ObjectFieldSetting> objectFieldSettings =
				_objectFieldSettingLocalService.
					getObjectFieldObjectFieldSettings(objectField);

			for (ObjectFieldSetting objectFieldSetting : objectFieldSettings) {
				if (StringUtil.equals(
						objectFieldSetting.getName(), "filters")) {

					objectFieldSettingsValues.put(
						objectFieldSetting.getName(),
						objectFieldSetting.getObjectFilters());
				}
				else {
					objectFieldSettingsValues.put(
						objectFieldSetting.getName(),
						objectFieldSetting.getValue());
				}
			}

			ObjectDefinition objectDefinition =
				dynamicObjectDefinitionTable.getObjectDefinition();

			if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_AGGREGATION)) {

				selectExpressions.add(
					DSLQueryFactoryUtil.scalarSubDSLQuery(
						_getAggregationObjectFieldDSLQuery(
							dynamicObjectDefinitionTable, objectDefinition,
							objectFieldSettingsValues,
							systemObjectDefinitionManager),
						DynamicObjectDefinitionTableUtil.getJavaClass(
							objectField.getDBType()),
						objectField.getName(),
						DynamicObjectDefinitionTableUtil.getSQLType(
							objectField.getDBType())));
			}
			else if (objectField.compareBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_FORMULA)) {

				Object script = objectFieldSettingsValues.get("script");

				if (script == null) {
					continue;
				}

				if (columns == null) {
					columns = _getColumns(
						dynamicObjectDefinitionTable, objectDefinition);
				}

				DDMExpression<Expression<?>> ddmExpression =
					_ddmExpressionFactory.createExpression(
						CreateExpressionRequest.Builder.newBuilder(
							String.valueOf(script)
						).build());

				ddmExpression.setVariables(columns);
				ddmExpression.setVariables(
					_getQueryExpressions(
						objectDefinition, primaryKey, String.valueOf(script)));

				try {
					Expression<?> expression = ddmExpression.getDSLExpression();

					String output = GetterUtil.getString(
						objectFieldSettingsValues.get("output"));

					if (Objects.equals(output, "Integer")) {
						expression = DSLFunctionFactoryUtil.castLong(
							expression);
					}

					selectExpressions.add(expression.as(objectField.getName()));
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			}
		}

		return selectExpressions.toArray(new Expression<?>[0]);
	}

	private int _getStatus(int status) {
		if (status == WorkflowConstants.STATUS_PENDING) {
			return WorkflowConstants.STATUS_DRAFT;
		}

		return status;
	}

	private int _getStatus(ObjectDefinition objectDefinition, int status) {
		if (!ExportImportThreadLocal.isImportInProcess()) {
			return WorkflowConstants.STATUS_DRAFT;
		}

		if (status == WorkflowConstants.STATUS_EMPTY) {
			return status;
		}

		if (objectDefinition.isEnableObjectEntryDraft() &&
			((status == WorkflowConstants.STATUS_DRAFT) ||
			 (status == WorkflowConstants.STATUS_PENDING))) {

			return WorkflowConstants.STATUS_DRAFT;
		}

		if (objectDefinition.isEnableObjectEntrySchedule() &&
			((status == WorkflowConstants.STATUS_EXPIRED) ||
			 (status == WorkflowConstants.STATUS_SCHEDULED))) {

			return status;
		}

		return WorkflowConstants.STATUS_APPROVED;
	}

	private List<ObjectValuePair<Long, Integer>> _getStatusOVPs(
		List<ObjectEntryVersion> objectEntryVersions) {

		return TransformUtil.transform(
			objectEntryVersions,
			objectEntryVersion -> new ObjectValuePair<>(
				objectEntryVersion.getObjectEntryVersionId(),
				_getStatus(objectEntryVersion.getStatus())));
	}

	private String _getUrlTitle(
		long classNameId, long groupId, String languageId,
		ObjectEntry objectEntry, ObjectField objectField,
		Map<String, Object> values) {

		String urlTitle = GetterUtil.getString(
			ObjectEntryValuesUtil.getValue(languageId, objectField, values));

		if (Validator.isNull(urlTitle)) {
			urlTitle = objectEntry.getExternalReferenceCode();

			if (Validator.isNull(urlTitle)) {
				urlTitle = objectEntry.getUuid();
			}
		}

		return _friendlyURLEntryLocalService.getUniqueUrlTitle(
			groupId, classNameId, objectEntry.getObjectEntryId(), urlTitle,
			languageId);
	}

	/**
	 * @see com.liferay.portal.upgrade.util.Table#getValue
	 */
	private Object _getValue(Object object, int sqlType) {
		Function<Object, Object> function = _getValueFunctions.get(sqlType);

		if (function == null) {
			throw new IllegalArgumentException(
				"Unable to get value with SQL type " + sqlType);
		}

		return function.apply(object);
	}

	private Map<String, Serializable> _getValues(
			ObjectFieldBag objectFieldBag, Object[] objects,
			Expression<?>[] selectExpressions)
		throws PortalException {

		Map<String, Serializable> values = new HashMap<>();

		for (int i = 0; i < selectExpressions.length; i++) {
			Expression<?> selectExpression = selectExpressions[i];

			String columnName = null;
			Class<?> javaTypeClass = null;

			Object object = null;

			if (objects != null) {
				object = objects[i];
			}

			if (selectExpression instanceof Alias) {
				Alias<?> alias = (Alias<?>)selectExpression;

				columnName = alias.getName();

				javaTypeClass = DynamicObjectDefinitionTableUtil.getJavaClass(
					_getDBType(objectFieldBag.getObjectField(alias.getName())));
			}
			else if (selectExpression instanceof Column) {
				Column<?, ?> column = (Column<?, ?>)selectExpressions[i];

				columnName = column.getName();
				javaTypeClass = column.getJavaType();
			}
			else if (selectExpression instanceof ScalarDSLQueryAlias) {
				ScalarDSLQueryAlias scalarDSLQueryAlias =
					(ScalarDSLQueryAlias)selectExpressions[i];

				columnName = scalarDSLQueryAlias.getName();
				javaTypeClass = scalarDSLQueryAlias.getJavaType();
			}

			if (columnName.endsWith(StringPool.UNDERLINE)) {
				String[] parts = StringUtil.split(
					columnName, StringPool.UNDERLINE);

				if ((parts.length == 2) &&
					(Objects.equals(parts[0], "classNameId") ||
					 Objects.equals(parts[0], "classPK"))) {

					_putValue(
						javaTypeClass, parts[0], object,
						(Map<String, Serializable>)values.computeIfAbsent(
							parts[1], key -> new HashMap<>()));

					continue;
				}

				columnName = columnName.substring(0, columnName.length() - 1);

				ObjectField objectField = objectFieldBag.getObjectField(
					columnName);

				if ((object != null) && (objectField != null) &&
					objectField.compareBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_ENCRYPTED)) {

					try {
						object = _encryptor.decrypt(_getKey(), (String)object);
					}
					catch (IllegalArgumentException illegalArgumentException) {
						throw new IllegalArgumentException(
							"Please insert an encryption key or remove the " +
								"object's encryption field to recover these " +
									"entries.",
							illegalArgumentException);
					}
					catch (Exception exception) {
						throw new PortalException(exception);
					}
				}
			}

			_putValue(javaTypeClass, columnName, object, values);
		}

		return values;
	}

	private void _handle(
			ObjectEntryValuesException objectEntryValuesException,
			List<ValidationError> validationErrors)
		throws ObjectEntryValuesException {

		if (validationErrors == null) {
			throw objectEntryValuesException;
		}

		validationErrors.add(
			new ValidationError(objectEntryValuesException.getMessage()));
	}

	private void _insertIntoLocalizationTable(
			Map<String, Serializable> insertedValues,
			ObjectDefinition objectDefinition, long objectEntryId,
			Map<String, Serializable> originalValues, boolean partialUpdate,
			Map<String, Serializable> values)
		throws PortalException {

		DynamicObjectDefinitionLocalizationTable
			dynamicObjectDefinitionLocalizationTable =
				DynamicObjectDefinitionLocalizationTableFactory.create(
					objectDefinition, _objectFieldLocalService);

		if (dynamicObjectDefinitionLocalizationTable == null) {
			return;
		}

		List<ObjectField> objectFields =
			dynamicObjectDefinitionLocalizationTable.getObjectFields();

		if (objectFields.isEmpty()) {
			return;
		}

		List<String> columnNames = new ArrayList<>();

		columnNames.add(
			dynamicObjectDefinitionLocalizationTable.getForeignKeyColumnName());
		columnNames.add("languageId");

		int count = 2;

		StringBundler sb = new StringBundler();

		sb.append("insert into ");
		sb.append(dynamicObjectDefinitionLocalizationTable.getName());
		sb.append(" (");
		sb.append(
			dynamicObjectDefinitionLocalizationTable.getForeignKeyColumnName());
		sb.append(", languageId");

		for (ObjectField objectField : objectFields) {
			columnNames.add(objectField.getDBColumnName());

			count++;

			sb.append(", ");
			sb.append(objectField.getDBColumnName());
		}

		Set<Locale> locales = _getLocales(
			objectDefinition.getCompanyId(), objectFields, originalValues,
			partialUpdate, values);

		if (locales.isEmpty()) {
			return;
		}

		sb.append(") values (?");

		for (int i = 1; i < count; i++) {
			sb.append(", ?");
		}

		sb.append(")");

		String sql = sb.toString();

		if (_log.isDebugEnabled()) {
			_log.debug("SQL: " + sql);
		}

		Connection connection = _currentConnection.getConnection(
			objectEntryPersistence.getDataSource());

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			for (Locale locale : locales) {
				String languageId = LocaleUtil.toLanguageId(locale);

				int index = 1;

				_setColumn(
					columnNames, index++, insertedValues, preparedStatement,
					Types.BIGINT, objectEntryId);
				_setColumn(
					columnNames, index++, new HashMap<>(), preparedStatement,
					Types.VARCHAR, languageId);

				for (ObjectField objectField : objectFields) {
					Column<?, ?> column =
						dynamicObjectDefinitionLocalizationTable.getColumn(
							objectField.getDBColumnName());

					Map<String, Serializable> insertedLocalizedValue =
						new HashMap<>(1);

					Object localizedValue = _getLocalizedValue(
						languageId,
						(Map<String, Object>)values.get(
							objectField.getI18nObjectFieldName()));

					if (localizedValue == null) {
						if (partialUpdate) {
							localizedValue = _getLocalizedValue(
								languageId,
								(Map<String, Object>)originalValues.get(
									objectField.getI18nObjectFieldName()));
						}

						localizedValue = GetterUtil.getObject(
							localizedValue, StringPool.BLANK);
					}

					_setColumn(
						column, columnNames, index++, insertedLocalizedValue,
						objectField, preparedStatement, localizedValue);

					Map<String, Serializable> localizedValues =
						(Map<String, Serializable>)insertedValues.getOrDefault(
							column.getName() + "i18n", new HashMap<>());

					localizedValues.put(
						languageId,
						insertedLocalizedValue.get(
							StringUtil.removeLast(
								column.getName(), StringPool.UNDERLINE)));

					_putLocalizedValues(
						column.getName(), objectField.getDefaultLanguageId(),
						localizedValues, insertedValues);
				}

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();

			FinderCacheUtil.clearDSLQueryCache(
				dynamicObjectDefinitionLocalizationTable.getTableName());
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	private boolean _insertIntoTable(
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
			Map<String, Serializable> insertedValues, long objectEntryId,
			boolean partialUpdate, Map<String, Serializable> values)
		throws PortalException {

		List<String> columnNames = new ArrayList<>();

		Column<DynamicObjectDefinitionTable, Long> primaryKeyColumn =
			dynamicObjectDefinitionTable.getPrimaryKeyColumn();

		columnNames.add(primaryKeyColumn.getName());

		int count = 1;
		List<ObjectField> objectFields =
			dynamicObjectDefinitionTable.getObjectFields();

		StringBundler sb = new StringBundler();

		sb.append("insert into ");
		sb.append(dynamicObjectDefinitionTable.getName());
		sb.append(" (");
		sb.append(primaryKeyColumn.getName());

		boolean staticValues = true;

		for (ObjectField objectField : objectFields) {
			if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_AGGREGATION) ||
				objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_FORMULA)) {

				staticValues = false;
			}

			if (!objectField.hasInsertValues() || objectField.isLocalized()) {
				continue;
			}

			if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_AUTO_INCREMENT)) {

				_validateAutoIncrementValue(
					objectField,
					GetterUtil.getString(values.get(objectField.getName())));

				sb.append(", ");
				sb.append(objectField.getDBColumnName());
				sb.append(", ");
				sb.append(objectField.getSortableDBColumnName());

				columnNames.add(objectField.getDBColumnName());
				columnNames.add(objectField.getSortableDBColumnName());

				count += 2;

				continue;
			}

			if (!values.containsKey(objectField.getName()) &&
				!_processMissingObjectField(objectField, partialUpdate)) {

				continue;
			}

			if (Objects.equals(
					objectField.getRelationshipType(),
					ObjectRelationshipConstants.TYPE_ONE_TO_ONE)) {

				_validateOneToOneInsert(
					objectField.getDBColumnName(),
					GetterUtil.getLong(values.get(objectField.getName())),
					dynamicObjectDefinitionTable);
			}

			for (String dbColumnName : objectField.getDBColumnNames()) {
				columnNames.add(dbColumnName);

				count++;

				sb.append(", ");
				sb.append(dbColumnName);
			}
		}

		sb.append(") values (?");

		for (int i = 1; i < count; i++) {
			sb.append(", ?");
		}

		sb.append(")");

		String sql = sb.toString();

		if (_log.isDebugEnabled()) {
			_log.debug("SQL: " + sql);
		}

		Connection connection = _currentConnection.getConnection(
			objectEntryPersistence.getDataSource());

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			int index = 1;

			_setColumn(
				columnNames, index++, insertedValues, preparedStatement,
				Types.BIGINT, objectEntryId);

			for (ObjectField objectField : objectFields) {
				if (!objectField.hasInsertValues() ||
					objectField.isLocalized()) {

					continue;
				}

				if (objectField.compareBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_AUTO_INCREMENT)) {

					Column<?, ?> column =
						dynamicObjectDefinitionTable.getColumn(
							objectField.getDBColumnName());

					String prefix = ObjectFieldSettingUtil.getValue(
						ObjectFieldSettingConstants.NAME_PREFIX, objectField);
					String suffix = ObjectFieldSettingUtil.getValue(
						ObjectFieldSettingConstants.NAME_SUFFIX, objectField);

					String value = _getAutoIncrementValue(
						ObjectFieldUtil.getCounterName(objectField),
						ObjectFieldSettingUtil.getValue(
							ObjectFieldSettingConstants.NAME_INITIAL_VALUE,
							objectField),
						prefix, suffix,
						GetterUtil.getString(
							values.get(objectField.getName())));

					_setColumn(
						columnNames, index++, insertedValues, preparedStatement,
						column.getSQLType(), value);

					column = dynamicObjectDefinitionTable.getColumn(
						objectField.getSortableDBColumnName());

					_setColumn(
						columnNames, index++, insertedValues, preparedStatement,
						column.getSQLType(),
						_getAutoIncrementSortableValue(prefix, suffix, value));

					continue;
				}

				if (!values.containsKey(objectField.getName()) &&
					!_processMissingObjectField(objectField, partialUpdate)) {

					continue;
				}

				for (String dbColumnName : objectField.getDBColumnNames()) {
					_setColumn(
						dynamicObjectDefinitionTable.getColumn(dbColumnName),
						columnNames, index++, insertedValues, objectField,
						preparedStatement, values.get(objectField.getName()));
				}
			}

			preparedStatement.executeUpdate();

			FinderCacheUtil.clearDSLQueryCache(
				dynamicObjectDefinitionTable.getTableName());
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}

		return staticValues;
	}

	private List<Object[]> _list(
			DSLQuery dslQuery, ObjectFieldBag objectFieldBag,
			Expression<?>[] selectExpressions)
		throws PortalException {

		List<Object> entriesValues = objectEntryPersistence.dslQuery(dslQuery);

		List<Object[]> results = new ArrayList<>(entriesValues.size());

		for (Object entryValues : entriesValues) {
			Object[] result = new Object[selectExpressions.length];

			if (selectExpressions.length == 1) {
				result[0] = _getResult(
					entryValues, objectFieldBag, selectExpressions[0]);
			}
			else {
				for (int i = 0; i < selectExpressions.length; i++) {
					result[i] = _getResult(
						((Object[])entryValues)[i], objectFieldBag,
						selectExpressions[i]);
				}
			}

			results.add(result);
		}

		return results;
	}

	private List<Map<String, Serializable>> _list(
		List<Column<DynamicObjectDefinitionTable, ?>> columns,
		DSLQuery dslQuery) {

		List<Object> entriesValues = objectEntryPersistence.dslQuery(
			dslQuery, false);

		List<Map<String, Serializable>> results = new ArrayList<>(
			entriesValues.size());

		for (Object entryValues : entriesValues) {
			Map<String, Serializable> result = new HashMap<>();

			if (columns.size() == 1) {
				Column<DynamicObjectDefinitionTable, ?> column = columns.get(0);

				_putValue(
					_getValue(entryValues, column.getSQLType()), column,
					result);
			}
			else {
				for (int i = 0; i < columns.size(); i++) {
					Column<DynamicObjectDefinitionTable, ?> column =
						columns.get(i);

					_putValue(
						_getValue(
							((Object[])entryValues)[i], column.getSQLType()),
						column, result);
				}
			}

			results.add(result);
		}

		return results;
	}

	private ObjectEntry _moveObjectEntryToTrash(
			ObjectEntry objectEntry, long objectEntryFolderId,
			ServiceContext serviceContext, long userId)
		throws PortalException {

		List<ObjectValuePair<Long, Integer>> statusOVPs = new ArrayList<>();

		List<ObjectEntryVersion> objectEntryVersions =
			_objectEntryVersionLocalService.getObjectEntryVersions(
				objectEntry.getObjectEntryId());

		if (ListUtil.isNotEmpty(objectEntryVersions)) {
			statusOVPs = _getStatusOVPs(
				ListUtil.sort(
					objectEntryVersions,
					ObjectEntryVersionVersionComparator.getInstance(false)));
		}

		int oldStatus = objectEntry.getStatus();

		objectEntry = updateStatus(
			userId, objectEntry, WorkflowConstants.STATUS_IN_TRASH,
			serviceContext);

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectEntry.getObjectDefinitionId());

		_trashEntryLocalService.addTrashEntry(
			userId, objectEntry.getGroupId(), objectDefinition.getClassName(),
			objectEntry.getObjectEntryId(), objectEntry.getUuid(), null,
			_getStatus(oldStatus), statusOVPs,
			UnicodePropertiesBuilder.put(
				"objectEntryFolderId", objectEntryFolderId
			).put(
				"title", objectEntry.getObjectEntryId()
			).build());

		_updateLatestApprovedObjectEntry(
			objectEntry.getObjectEntryId(), WorkflowConstants.STATUS_IN_TRASH);

		for (ObjectEntryVersion objectEntryVersion :
				_objectEntryVersionLocalService.getObjectEntryVersions(
					objectEntry.getObjectEntryId())) {

			if (objectEntryVersion.getStatus() ==
					WorkflowConstants.STATUS_IN_TRASH) {

				continue;
			}

			objectEntryVersion.setStatus(WorkflowConstants.STATUS_IN_TRASH);

			_objectEntryVersionLocalService.updateObjectEntryVersion(
				objectEntryVersion);
		}

		if (objectDefinition.isEnableComments()) {
			_commentManager.moveDiscussionToTrash(
				objectDefinition.getClassName(),
				objectEntry.getObjectEntryId());
		}

		if (oldStatus == WorkflowConstants.STATUS_PENDING) {
			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
				objectEntry.getCompanyId(), objectEntry.getNonzeroGroupId(),
				objectDefinition.getClassName(),
				objectEntry.getObjectEntryId());
		}

		moveRelatedObjectEntriesToTrash(
			objectEntry.getGroupId(), objectEntry.getObjectDefinitionId(),
			objectEntry.getObjectEntryId());

		return objectEntry;
	}

	private void _performActions(
			long objectDefinitionId,
			ActionableDynamicQuery.PerformActionMethod<?> performActionMethod)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property objectDefinitionIdProperty =
					PropertyFactoryUtil.forName("objectDefinitionId");

				dynamicQuery.add(
					objectDefinitionIdProperty.eq(objectDefinitionId));
			});
		actionableDynamicQuery.setParallel(true);
		actionableDynamicQuery.setPerformActionMethod(performActionMethod);

		actionableDynamicQuery.performActions();
	}

	private boolean _processMissingObjectField(
		ObjectField objectField, boolean partialUpdate) {

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

			return false;
		}

		if (_log.isDebugEnabled()) {
			String logMessage =
				"No value was provided for object field \"" +
					objectField.getName() + "\"";

			if (!partialUpdate) {
				logMessage += ". The field is set as null.";
			}

			_log.debug(logMessage);
		}

		return !partialUpdate;
	}

	private void _putInsertedValue(
		Map<String, Serializable> insertedValues, String key,
		Serializable serializable) {

		if (!key.isEmpty() &&
			(key.charAt(key.length() - 1) == CharPool.UNDERLINE)) {

			key = key.substring(0, key.length() - 1);
		}

		insertedValues.put(key, serializable);
	}

	private void _putLocalizedValues(
		String columnName, String defaultLanguageId,
		Map<String, Serializable> localizedValues,
		Map<String, Serializable> values) {

		values.put(columnName + "i18n", (Serializable)localizedValues);
		values.put(
			StringUtil.removeLast(columnName, StringPool.UNDERLINE),
			localizedValues.getOrDefault(defaultLanguageId, StringPool.BLANK));
	}

	private void _putObjectFilterParser(
		ObjectFilterParser objectFilterParser, String... types) {

		for (String type : types) {
			_objectFilterParsers.put(type, objectFilterParser);
		}
	}

	private void _putValue(
		Class<?> javaTypeClass, String name, Object object,
		Map<String, Serializable> values) {

		Function<Object, Serializable> function = _putValueFunctions.get(
			javaTypeClass);

		if (function == null) {
			throw new IllegalArgumentException(
				"Unable to put value with class " + javaTypeClass.getName());
		}

		values.put(name, function.apply(object));
	}

	private void _putValue(
		Object object, Column<DynamicObjectDefinitionTable, ?> column,
		Map<String, Serializable> values) {

		String columnName = column.getName();
		Class<?> javaTypeClass = column.getJavaType();

		if (columnName.endsWith(StringPool.UNDERLINE)) {
			if (columnName.startsWith("class")) {
				String[] parts = StringUtil.split(
					columnName, StringPool.UNDERLINE);

				if ((parts.length == 2) &&
					(Objects.equals(parts[0], "classNameId") ||
					 Objects.equals(parts[0], "classPK"))) {

					_putValue(
						javaTypeClass, parts[0], object,
						(Map<String, Serializable>)values.computeIfAbsent(
							parts[1], key -> new HashMap<>()));

					return;
				}
			}

			columnName = columnName.substring(0, columnName.length() - 1);
		}

		_putValue(javaTypeClass, columnName, object, values);
	}

	private void _reindex(ObjectEntry objectEntry) throws PortalException {
		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectEntry.getObjectDefinitionId());

		if (!objectDefinition.isEnableIndexSearch()) {
			return;
		}

		Indexer<ObjectEntry> indexer = IndexerRegistryUtil.getIndexer(
			objectDefinition.getClassName());

		try (SafeCloseable safeCloseable =
				StrictObjectReindexThreadLocal.
					setStrictObjectReindexWithSafeCloseable(true)) {

			indexer.reindex(objectEntry);
		}
	}

	private void _removeInvalidAssetCategories(
			long groupId, String className, long objectEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			className, objectEntryId);

		if (assetEntry == null) {
			return;
		}

		long[] assetCategoryIds = assetEntry.getCategoryIds();

		long[] assetVocabularyIds = ArrayUtil.unique(
			ArrayUtil.append(
				TransformUtil.transformToLongArray(
					_assetVocabularyGroupRelLocalService.
						getAssetVocabularyGroupRelsByGroupId(groupId),
					AssetVocabularyGroupRel::getVocabularyId),
				TransformUtil.transformToLongArray(
					_assetVocabularyGroupRelLocalService.
						getAssetVocabularyGroupRelsByGroupId(-1),
					AssetVocabularyGroupRel::getVocabularyId)));

		for (long assetCategoryId : assetEntry.getCategoryIds()) {
			AssetCategory assetCategory =
				_assetCategoryLocalService.getAssetCategory(assetCategoryId);

			if (!ArrayUtil.contains(
					assetVocabularyIds, assetCategory.getVocabularyId())) {

				assetCategoryIds = ArrayUtil.remove(
					assetCategoryIds, assetCategory.getCategoryId());
			}
		}

		serviceContext.setAssetCategoryIds(assetCategoryIds);
	}

	private void _removeInvalidAssetTags(
			long groupId, String className, long objectEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			className, objectEntryId);

		if (assetEntry == null) {
			return;
		}

		String[] assetTagNames = assetEntry.getTagNames();

		long[] tagIds = ArrayUtil.unique(
			ArrayUtil.append(
				TransformUtil.transformToLongArray(
					_assetTagGroupRelLocalService.
						getAssetTagGroupRelsByGroupyId(groupId),
					AssetTagGroupRel::getTagId),
				TransformUtil.transformToLongArray(
					_assetTagGroupRelLocalService.
						getAssetTagGroupRelsByGroupyId(-1),
					AssetTagGroupRel::getTagId)));

		for (AssetTag assetTag : assetEntry.getTags()) {
			if (!ArrayUtil.contains(tagIds, assetTag.getTagId())) {
				assetTagNames = ArrayUtil.remove(
					assetTagNames, assetTag.getName());
			}
		}

		serviceContext.setAssetTagNames(assetTagNames);
	}

	private ObjectEntry _restoreObjectEntryFromTrash(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			ServiceContext serviceContext, TrashEntry trashEntry, long userId)
		throws PortalException {

		objectEntry = updateStatus(
			userId, objectEntry, trashEntry.getStatus(), serviceContext);

		_updateLatestApprovedObjectEntry(
			objectEntry.getObjectEntryId(), WorkflowConstants.STATUS_APPROVED);

		for (TrashVersion trashVersion :
				_trashVersionLocalService.getVersions(
					trashEntry.getEntryId())) {

			ObjectEntryVersion objectEntryVersion =
				_objectEntryVersionPersistence.findByPrimaryKey(
					trashVersion.getClassPK());

			objectEntryVersion.setStatus(trashVersion.getStatus());

			_objectEntryVersionPersistence.update(objectEntryVersion);
		}

		_trashEntryLocalService.deleteEntry(
			objectDefinition.getClassName(), objectEntry.getObjectEntryId());

		if (objectDefinition.isEnableComments()) {
			_commentManager.restoreDiscussionFromTrash(
				objectDefinition.getClassName(),
				objectEntry.getObjectEntryId());
		}

		try {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

			_restoreRelatedModelsFromTrash(
				objectEntry.getGroupId(), objectEntry.getObjectDefinitionId(),
				objectEntry.getObjectEntryId());
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(false);
		}

		return objectEntry;
	}

	private void _restoreRelatedModelsFromTrash(
			long groupId, long objectDefinitionId, long objectEntryId)
		throws PortalException {

		for (ObjectRelationship objectRelationship :
				_objectRelationshipPersistence.findByObjectDefinitionId1(
					objectDefinitionId)) {

			ObjectDefinition objectDefinition2 =
				_objectDefinitionPersistence.findByPrimaryKey(
					objectRelationship.getObjectDefinitionId2());

			if (!objectDefinition2.isApproved()) {
				continue;
			}

			ObjectRelatedModelsProvider objectRelatedModelsProvider =
				_objectRelatedModelsProviderRegistry.
					getObjectRelatedModelsProvider(
						objectDefinition2.getClassName(),
						objectDefinition2.getCompanyId(),
						objectRelationship.getType());

			objectRelatedModelsProvider.restoreRelatedModelsFromTrash(
				PrincipalThreadLocal.getUserId(), groupId,
				objectRelationship.getObjectRelationshipId(), objectEntryId,
				objectRelationship.getDeletionType());
		}
	}

	private void _setColumn(
			Column<?, ?> column, List<String> columnNames, int index,
			Map<String, Serializable> insertedValues, ObjectField objectField,
			PreparedStatement preparedStatement, Object value)
		throws Exception {

		if (value == null) {
			_setColumn(
				columnNames, index, insertedValues, preparedStatement,
				column.getSQLType(), value);
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE)) {

			String columnName = StringUtil.extractFirst(
				column.getName(), StringPool.UNDERLINE);

			columnNames.set(index - 1, columnName);

			_setColumn(
				columnNames, index,
				(Map<String, Serializable>)insertedValues.computeIfAbsent(
					objectField.getName(), key -> new HashMap<>()),
				preparedStatement, column.getSQLType(),
				MapUtil.getLong((Map<String, Serializable>)value, columnName));
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_ENCRYPTED)) {

			_setColumn(
				columnNames, index, insertedValues, preparedStatement,
				column.getSQLType(),
				_encryptor.encrypt(_getKey(), (String)value));
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST)) {

			String valueString = String.valueOf(value);

			// Remove the first [ and the last ] in
			// "[pickListEntryKey1, pickListEntryKey2, pickListEntryKey3]"

			if (StringUtil.endsWith(valueString, StringPool.CLOSE_BRACKET) &&
				StringUtil.startsWith(valueString, StringPool.OPEN_BRACKET)) {

				valueString = valueString.substring(
					1, valueString.length() - 1);
			}

			_setColumn(
				columnNames, index, insertedValues, preparedStatement,
				column.getSQLType(), valueString);
		}
		else {
			_setColumn(
				columnNames, index, insertedValues, preparedStatement,
				column.getSQLType(), value);
		}
	}

	/**
	 * @see com.liferay.portal.upgrade.util.Table#setColumn
	 */
	private void _setColumn(
			List<String> columnNames, int index,
			Map<String, Serializable> insertedValues,
			PreparedStatement preparedStatement, int sqlType, Object value)
		throws Exception {

		if (sqlType == Types.BIGINT) {
			Long longValue = GetterUtil.getLong(value);

			preparedStatement.setLong(index, longValue);

			_putInsertedValue(
				insertedValues, columnNames.get(index - 1), longValue);
		}
		else if (sqlType == Types.BLOB) {
			if (PostgreSQLJDBCUtil.isPGStatement(preparedStatement)) {
				PostgreSQLJDBCUtil.setLargeObject(
					preparedStatement, index, (byte[])value);
			}
			else {
				preparedStatement.setBytes(index, (byte[])value);
			}

			_putInsertedValue(
				insertedValues, columnNames.get(index - 1), (byte[])value);
		}
		else if (sqlType == Types.BOOLEAN) {
			Boolean booleanValue = GetterUtil.getBoolean(value);

			preparedStatement.setBoolean(index, booleanValue);

			_putInsertedValue(
				insertedValues, columnNames.get(index - 1), booleanValue);
		}
		else if (sqlType == Types.CLOB) {
			String valueString = null;

			if (value != null) {
				valueString = String.valueOf(value);
			}

			if (Validator.isBlank(valueString) ||
				(DBManagerUtil.getDBType() == DBType.POSTGRESQL)) {

				preparedStatement.setString(index, valueString);
			}
			else {
				preparedStatement.setClob(
					index, new StringReader(valueString), valueString.length());
			}

			_putInsertedValue(
				insertedValues, columnNames.get(index - 1), valueString);
		}
		else if ((sqlType == Types.DATE) || (sqlType == Types.TIMESTAMP)) {
			Timestamp timestamp = null;

			String valueString = GetterUtil.getString(value);

			if (value instanceof Date) {
				Date date = (Date)value;

				timestamp = new Timestamp(date.getTime());

				preparedStatement.setTimestamp(index, timestamp);
			}
			else if (value instanceof LocalDate) {
				LocalDate localDate = (LocalDate)value;

				Date date = Date.from(
					localDate.atStartOfDay(
						ZoneId.systemDefault()
					).toInstant());

				timestamp = new Timestamp(date.getTime());

				preparedStatement.setTimestamp(index, timestamp);
			}
			else if (value instanceof LocalDateTime) {
				LocalDateTime localDateTime = (LocalDateTime)value;

				timestamp = Timestamp.valueOf(localDateTime);

				preparedStatement.setTimestamp(index, timestamp);
			}
			else if (Validator.isNull(valueString)) {
				preparedStatement.setTimestamp(index, null);
			}
			else {
				Date date = DateUtil.parseDate(
					ObjectFieldUtil.getDateTimePattern(valueString),
					valueString, LocaleUtil.getSiteDefault());

				timestamp = new Timestamp(date.getTime());

				preparedStatement.setTimestamp(index, timestamp);
			}

			_putInsertedValue(
				insertedValues, columnNames.get(index - 1), timestamp);
		}
		else if (sqlType == Types.DECIMAL) {
			if (Validator.isNull(String.valueOf(value))) {
				value = BigDecimal.ZERO;
			}

			BigDecimal bigDecimal = new BigDecimal(
				_toPeriodSeparator(String.valueOf(value)));

			preparedStatement.setBigDecimal(index, bigDecimal);

			_putInsertedValue(
				insertedValues, columnNames.get(index - 1),
				BigDecimalUtil.stripTrailingZeros(bigDecimal));
		}
		else if (sqlType == Types.DOUBLE) {
			Double doubleValue = GetterUtil.getDouble(
				_toPeriodSeparator(String.valueOf(value)));

			preparedStatement.setDouble(index, doubleValue);

			_putInsertedValue(
				insertedValues, columnNames.get(index - 1), doubleValue);
		}
		else if (sqlType == Types.INTEGER) {
			Integer integer = GetterUtil.getInteger(value);

			preparedStatement.setInt(index, integer);

			_putInsertedValue(
				insertedValues, columnNames.get(index - 1), integer);
		}
		else if (sqlType == Types.VARCHAR) {
			String string = null;

			if (value != null) {
				string = String.valueOf(value);
			}

			preparedStatement.setString(index, string);

			_putInsertedValue(
				insertedValues, columnNames.get(index - 1), string);
		}
		else {
			throw new IllegalArgumentException(
				"Unable to set column with SQL type " + sqlType);
		}
	}

	private void _setDisplayDate(
		long companyId, ObjectEntry objectEntry,
		Map<String, Serializable> values) {

		if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-17564")) {
			objectEntry.setDisplayDate((Date)values.get("displayDate"));
		}
	}

	private void _setExpirationDate(
			long companyId, ObjectEntry objectEntry,
			Map<String, Serializable> values)
		throws PortalException {

		if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-17564")) {
			Date expirationDate = (Date)values.get("expirationDate");

			if ((expirationDate != null) && expirationDate.before(new Date())) {
				throw new ObjectEntryExpirationDateException(
					"Expiration date must be a future date",
					"expiration-date-must-be-a-future-date");
			}

			objectEntry.setExpirationDate(expirationDate);
		}
	}

	private void _setExternalReferenceCode(
		ObjectEntry objectEntry, Map<String, Serializable> values) {

		for (Map.Entry<String, Serializable> entry : values.entrySet()) {
			if (StringUtil.equals(entry.getKey(), "externalReferenceCode")) {
				String externalReferenceCode = String.valueOf(entry.getValue());

				if (Validator.isNull(externalReferenceCode)) {
					externalReferenceCode = objectEntry.getUuid();
				}

				_validateExternalReferenceCode(
					externalReferenceCode, objectEntry.getGroupId(),
					objectEntry.getCompanyId(),
					objectEntry.getObjectDefinitionId(),
					objectEntry.getObjectEntryId());

				objectEntry.setExternalReferenceCode(externalReferenceCode);
			}
		}
	}

	private void _setReviewDate(
		long companyId, ObjectEntry objectEntry,
		Map<String, Serializable> values) {

		if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-17564")) {
			objectEntry.setReviewDate((Date)values.get("reviewDate"));
		}
	}

	private void _setRootObjectEntryId(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			Map<String, Serializable> values)
		throws PortalException {

		if (ArrayUtil.isEmpty(objectDefinition.getRootObjectDefinitionIds())) {
			objectEntry.setRootObjectEntryId(0);

			return;
		}

		if (objectDefinition.isRootNode()) {
			objectEntry.setRootObjectEntryId(objectEntry.getObjectEntryId());

			return;
		}

		long parentObjectEntryId = 0;

		List<ObjectRelationship> objectRelationships =
			_objectRelationshipPersistence.findByODI2_E(
				objectDefinition.getObjectDefinitionId(), true);

		for (ObjectRelationship objectRelationship : objectRelationships) {
			ObjectField objectField = _objectFieldLocalService.getObjectField(
				objectRelationship.getObjectFieldId2());

			parentObjectEntryId = MapUtil.getLong(
				values, objectField.getName());

			if (parentObjectEntryId != 0) {
				break;
			}
		}

		if (parentObjectEntryId == 0) {
			objectEntry.setRootObjectEntryId(0);

			return;
		}

		ObjectEntry parentObjectEntry = getObjectEntry(parentObjectEntryId);

		objectEntry.setRootObjectEntryId(
			parentObjectEntry.getRootObjectEntryId());
	}

	private void _startWorkflowInstance(
			long userId, ObjectEntry objectEntry, ServiceContext serviceContext,
			boolean skipModelListener)
		throws PortalException {

		if (ExportImportThreadLocal.isImportInProcess() ||
			objectEntry.isInTrash()) {

			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectEntry.getObjectDefinitionId());
		boolean skipObjectActionExecution =
			ObjectActionThreadLocal.isSkipObjectActionExecution();
		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		try {
			WorkflowThreadLocal.setEnabled(true);

			if (objectEntry.isRootDescendantNode()) {
				ObjectEntry rootObjectEntry =
					objectEntryPersistence.fetchByPrimaryKey(
						objectEntry.getRootObjectEntryId());

				if (rootObjectEntry == null) {
					return;
				}

				ObjectDefinition rootObjectDefinition =
					_objectDefinitionPersistence.fetchByPrimaryKey(
						rootObjectEntry.getObjectDefinitionId());

				if (serviceContext.getWorkflowAction() ==
						WorkflowConstants.ACTION_SAVE_DRAFT) {

					WorkflowInstanceLink workflowInstanceLink =
						_workflowInstanceLinkLocalService.
							fetchWorkflowInstanceLink(
								rootObjectDefinition.getCompanyId(),
								rootObjectEntry.getNonzeroGroupId(),
								rootObjectDefinition.getClassName(),
								rootObjectEntry.getObjectEntryId());

					if (workflowInstanceLink != null) {
						WorkflowInstance workflowInstance =
							WorkflowInstanceManagerUtil.getWorkflowInstance(
								objectDefinition.getCompanyId(),
								workflowInstanceLink.getWorkflowInstanceId());

						if (!workflowInstance.isComplete()) {
							throw new ObjectEntryStatusException(
								"Draft root descendant nodes cannot be added " +
									"when the root node has incomplete " +
										"workflow instance");
						}
					}

					if (rootObjectEntry.getStatus() !=
							WorkflowConstants.STATUS_DRAFT) {

						rootObjectEntry.setStatus(
							WorkflowConstants.STATUS_DRAFT);

						rootObjectEntry = updateObjectEntry(rootObjectEntry);
					}
				}

				ServiceContext workflowServiceContext =
					(ServiceContext)serviceContext.clone();

				workflowServiceContext.setStrictAdd(false);

				if (rootObjectEntry.getStatus() ==
						WorkflowConstants.STATUS_DRAFT) {

					workflowServiceContext.setWorkflowAction(
						WorkflowConstants.ACTION_SAVE_DRAFT);
				}

				int originalStatus = rootObjectEntry.getStatus();

				_startWorkflowInstance(
					userId, rootObjectDefinition.getClassName(),
					rootObjectEntry, workflowServiceContext);

				if (originalStatus == rootObjectEntry.getStatus()) {
					_updateRootDescendantNodeObjectEntryStatus(
						userId, rootObjectEntry, workflowServiceContext);
				}
			}
			else {
				_skipModelListeners.set(skipModelListener);

				ObjectActionThreadLocal.setSkipObjectActionExecution(true);

				_startWorkflowInstance(
					userId, objectDefinition.getClassName(), objectEntry,
					serviceContext);
			}
		}
		finally {
			_skipModelListeners.set(false);

			ObjectActionThreadLocal.setSkipObjectActionExecution(
				skipObjectActionExecution);
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
	}

	private void _startWorkflowInstance(
			long userId, String className, ObjectEntry objectEntry,
			ServiceContext serviceContext)
		throws PortalException {

		WorkflowHandlerRegistryUtil.startWorkflowInstance(
			objectEntry.getCompanyId(), objectEntry.getNonzeroGroupId(), userId,
			className, objectEntry.getObjectEntryId(), objectEntry,
			serviceContext);
	}

	private String _toPeriodSeparator(String value) {
		if (Validator.isNull(value) || !NumberUtil.hasDecimalSeparator(value)) {
			return value;
		}

		return StringUtil.replace(
			value, value.charAt(NumberUtil.getDecimalSeparatorIndex(value)),
			'.');
	}

	private void _updateAsset(
			long userId, ObjectEntry objectEntry, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority,
			ServiceContext serviceContext)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectEntry.getObjectDefinitionId());

		if (!objectDefinition.isEnableCategorization()) {
			assetCategoryIds = null;
			assetTagNames = null;
		}

		String mimeType = ContentTypes.TEXT_PLAIN;
		String title = StringPool.BLANK;

		Map<Locale, String> titleMap = objectEntry.getTitleMap();

		if (MapUtil.isNotEmpty(titleMap)) {
			title = _localization.getXml(
				LocalizedMapUtil.getLanguageIdMap(titleMap),
				objectEntry.getDefaultLanguageId(), "title");

			if (Validator.isXml(title)) {
				mimeType = ContentTypes.TEXT_HTML;
			}
		}
		else {
			try {
				title = objectEntry.getTitleValue();
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(portalException);
				}
			}
		}

		if (objectDefinition.isCMS()) {
			Group group = _groupLocalService.fetchGroup(
				objectEntry.getCompanyId(), GroupConstants.CMS);

			if (group != null) {
				serviceContext.setAttribute(
					"assetTagScopeGroupId", group.getGroupId());
			}
		}

		AssetEntry assetEntry = _assetEntryLocalService.updateEntry(
			userId, objectEntry.getNonzeroGroupId(),
			objectEntry.getCreateDate(), objectEntry.getModifiedDate(),
			objectDefinition.getClassName(), objectEntry.getObjectEntryId(),
			objectEntry.getUuid(), 0, assetCategoryIds, assetTagNames, true,
			objectEntry.isApproved(), null, null, null, null, mimeType, title,
			String.valueOf(objectEntry.getObjectEntryId()), null, null, null, 0,
			0, priority, serviceContext);

		if (assetLinkEntryIds != null) {
			_assetLinkLocalService.updateLinks(
				userId, assetEntry.getEntryId(), assetLinkEntryIds,
				AssetLinkConstants.TYPE_RELATED);
		}
	}

	private void _updateLatestApprovedObjectEntry(
		long objectEntryId, int status) {

		ObjectEntry objectEntry = fetchObjectEntryByHeadObjectEntryId(
			objectEntryId);

		if (objectEntry == null) {
			return;
		}

		objectEntry.setStatus(status);

		objectEntryLocalService.updateObjectEntry(objectEntry);
	}

	private void _updateLatestObjectEntryVersion(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws PortalException {

		if (!objectDefinition.isEnableObjectEntryVersioning()) {
			return;
		}

		_objectEntryVersionLocalService.updateLatestObjectEntryVersion(
			objectEntry);
	}

	private ObjectEntry _updateObjectEntry(
			long objectEntryFolderId, long objectEntryId, boolean partialUpdate,
			ServiceContext serviceContext, long userId,
			Map<String, Serializable> values)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		ObjectEntry objectEntry = objectEntryPersistence.findByPrimaryKey(
			objectEntryId);

		_validateObjectEntryFolderId(
			objectEntry.getGroupId(), objectEntryFolderId);

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectEntry.getObjectDefinitionId());

		if (!partialUpdate) {
			_fillDefaultValue(
				objectEntry.getDefaultLanguageId(),
				objectDefinition.getObjectDefinitionId(), values);
		}

		_contributeValues(
			objectEntry.getGroupId(), objectDefinition, userId, values);

		Map<ObjectField, Set<DLFileEntry>> dlFileEntriesMap = new HashMap<>();

		_validateValues(
			objectEntry.getDefaultLanguageId(), dlFileEntriesMap,
			objectEntry.getValues(), objectEntry.getGroupId(),
			user.isGuestUser(), objectDefinition,
			objectEntry.getObjectEntryId(),
			_objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId()),
			partialUpdate, serviceContext, objectEntry.getStatus(), userId,
			null, values);

		_addDLFileEntries(
			dlFileEntriesMap, objectEntry.getGroupId(), objectDefinition,
			objectEntryId, serviceContext, userId, values);

		int workflowAction = serviceContext.getWorkflowAction();

		_validateWorkflowAction(
			objectDefinition.isEnableObjectEntryDraft(), objectDefinition,
			objectEntry.getStatus(), workflowAction);

		Map<String, Serializable> transientValues = objectEntry.getValues();

		_deleteFromLocalizationTable(objectDefinition, objectEntryId);

		_insertIntoLocalizationTable(
			new HashMap<>(), objectDefinition, objectEntryId, transientValues,
			partialUpdate, values);

		_updateTable(
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				false, objectDefinition, _objectFieldLocalService),
			objectEntryId, partialUpdate, values);
		_updateTable(
			DynamicObjectDefinitionTableUtil.getDynamicObjectDefinitionTable(
				true, objectDefinition, _objectFieldLocalService),
			objectEntryId, partialUpdate, values);

		objectEntryPersistence.clearCache(SetUtil.fromArray(objectEntryId));

		objectEntry = objectEntryPersistence.findByPrimaryKey(objectEntryId);

		_setExternalReferenceCode(objectEntry, values);

		objectEntry.setModifiedDate(serviceContext.getModifiedDate(null));
		objectEntry.setObjectEntryFolderId(objectEntryFolderId);

		_setDisplayDate(objectDefinition.getCompanyId(), objectEntry, values);
		_setExpirationDate(
			objectDefinition.getCompanyId(), objectEntry, values);
		_setReviewDate(objectDefinition.getCompanyId(), objectEntry, values);

		if ((workflowAction == WorkflowConstants.ACTION_SAVE_DRAFT) &&
			!objectEntry.isPending()) {

			objectEntry.setStatus(WorkflowConstants.STATUS_DRAFT);
			objectEntry.setStatusByUserId(user.getUserId());
			objectEntry.setStatusDate(serviceContext.getModifiedDate(null));
		}

		objectEntry.setTransientValues(transientValues);

		ObjectEntry originalObjectEntry = objectEntry.cloneWithOriginalValues();

		try {
			if (workflowAction == WorkflowConstants.ACTION_SAVE_DRAFT) {
				ObjectEntryThreadLocal.setSkipObjectValidationRules(true);
			}

			objectEntry = objectEntryPersistence.update(objectEntry);
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectValidationRules(false);
		}

		_updateAsset(
			serviceContext.getUserId(), objectEntry,
			serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority(), serviceContext);

		_addFriendlyURLEntry(
			objectDefinition, objectEntry, serviceContext, values);

		_addOrUpdateComments(
			objectEntry.getGroupId(), userId, objectDefinition, objectEntry,
			serviceContext);

		_startWorkflowInstance(userId, objectEntry, serviceContext, true);

		ObjectDefinitionResourcePermissionUtil.updateResourcePermissions(
			objectDefinition.getCompanyId(), objectEntry.getGroupId(),
			objectDefinition.getClassName(), objectEntry.getObjectEntryId(),
			serviceContext);

		if (!objectDefinition.isEnableObjectEntryVersioning()) {
			_deleteFileEntries(
				objectEntry.getValues(), objectEntry.getObjectDefinitionId(),
				transientValues);
		}

		_deleteTempFileEntries(dlFileEntriesMap);

		if (!(objectEntry.isApproved() && originalObjectEntry.isScheduled()) &&
			!objectEntry.isScheduled()) {

			if (objectEntry.isPending() || originalObjectEntry.isDraft() ||
				originalObjectEntry.isExpired()) {

				_updateLatestObjectEntryVersion(objectDefinition, objectEntry);
			}
			else {
				objectEntry = _addObjectEntryVersion(
					objectDefinition, objectEntry);
			}
		}

		_executeObjectActions(
			objectEntry.getCompanyId(),
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE, objectDefinition,
			objectEntry, originalObjectEntry, serviceContext.getLanguageId(),
			user);

		return objectEntry;
	}

	private void _updateRootDescendantNodeObjectEntryStatus(
			long userId, ObjectEntry objectEntry, ServiceContext serviceContext)
		throws PortalException {

		boolean skipModelListener = _skipModelListeners.get();
		boolean skipObjectActionExecution =
			ObjectActionThreadLocal.isSkipObjectActionExecution();

		try {
			_skipModelListeners.set(true);

			ObjectActionThreadLocal.setSkipObjectActionExecution(true);

			for (ObjectEntry rootDescendantNodeObjectEntry :
					objectEntryPersistence.findByROEI_NotS(
						objectEntry.getObjectEntryId(),
						objectEntry.getStatus())) {

				updateStatus(
					userId, rootDescendantNodeObjectEntry,
					objectEntry.getStatus(), serviceContext);
			}
		}
		finally {
			_skipModelListeners.set(skipModelListener);

			ObjectActionThreadLocal.setSkipObjectActionExecution(
				skipObjectActionExecution);
		}
	}

	private void _updateRootObjectEntryIds(
			AtomicBoolean objectDefinition1RootNode,
			ObjectDefinition objectDefinition2,
			long[] objectDefinition2RootObjectDefinitionIds,
			ObjectEntry objectEntry, PreparedStatement preparedStatement1,
			PreparedStatement preparedStatement2,
			PreparedStatement preparedStatement3)
		throws SQLException {

		long rootObjectEntryId = objectEntry.getRootObjectEntryId();

		if (rootObjectEntryId == 0) {
			rootObjectEntryId = objectEntry.getObjectEntryId();

			preparedStatement2.setLong(1, rootObjectEntryId);

			preparedStatement2.setLong(2, objectEntry.getObjectEntryId());

			preparedStatement2.addBatch();

			objectDefinition1RootNode.set(true);
		}

		preparedStatement1.setLong(1, objectEntry.getObjectEntryId());

		try (ResultSet resultSet = preparedStatement1.executeQuery()) {
			while (resultSet.next()) {
				long relatedObjectEntryId = resultSet.getLong(
					objectDefinition2.getPKObjectFieldDBColumnName());

				if (ArrayUtil.isEmpty(
						objectDefinition2RootObjectDefinitionIds)) {

					preparedStatement2.setLong(1, rootObjectEntryId);
					preparedStatement2.setLong(2, relatedObjectEntryId);

					preparedStatement2.addBatch();

					continue;
				}

				preparedStatement3.setLong(1, rootObjectEntryId);
				preparedStatement3.setLong(2, relatedObjectEntryId);

				preparedStatement3.addBatch();
			}
		}
	}

	private void _updateTable(
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
			long objectEntryId, boolean partialUpdate,
			Map<String, Serializable> values)
		throws PortalException {

		List<String> columnNames = new ArrayList<>();
		int count = 0;

		StringBundler sb = new StringBundler();

		sb.append("update ");
		sb.append(dynamicObjectDefinitionTable.getName());
		sb.append(" set ");

		List<ObjectField> objectFields =
			dynamicObjectDefinitionTable.getObjectFields();

		for (ObjectField objectField : objectFields) {
			if (!objectField.hasUpdateValues() || objectField.isLocalized()) {
				continue;
			}

			if (!values.containsKey(objectField.getName()) &&
				!_processMissingObjectField(objectField, partialUpdate)) {

				continue;
			}

			if (Objects.equals(
					objectField.getRelationshipType(),
					ObjectRelationshipConstants.TYPE_ONE_TO_ONE)) {

				_validateOneToOneUpdate(
					objectField.getDBColumnName(),
					GetterUtil.getLong(values.get(objectField.getName())),
					dynamicObjectDefinitionTable, objectEntryId);
			}

			for (String dbColumnName : objectField.getDBColumnNames()) {
				columnNames.add(dbColumnName);

				count++;

				if (count > 1) {
					sb.append(", ");
				}

				sb.append(dbColumnName);
				sb.append(" = ?");
			}
		}

		if (count == 0) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"No values were provided for object entry " +
						objectEntryId);
			}

			return;
		}

		Column<DynamicObjectDefinitionTable, Long> primaryKeyColumn =
			dynamicObjectDefinitionTable.getPrimaryKeyColumn();

		columnNames.add(primaryKeyColumn.getName());

		Map<String, Serializable> insertedValues = new HashMap<>();

		sb.append(" where ");
		sb.append(primaryKeyColumn.getName());
		sb.append(" = ?");

		String sql = sb.toString();

		if (_log.isDebugEnabled()) {
			_log.debug("SQL: " + sql);
		}

		Connection connection = _currentConnection.getConnection(
			objectEntryPersistence.getDataSource());

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			int index = 1;

			for (ObjectField objectField : objectFields) {
				if (!objectField.hasUpdateValues() ||
					objectField.isLocalized()) {

					continue;
				}

				if (!values.containsKey(objectField.getName()) &&
					!_processMissingObjectField(objectField, partialUpdate)) {

					continue;
				}

				for (String dbColumnName : objectField.getDBColumnNames()) {
					_setColumn(
						dynamicObjectDefinitionTable.getColumn(dbColumnName),
						columnNames, index++, insertedValues, objectField,
						preparedStatement, values.get(objectField.getName()));
				}
			}

			_setColumn(
				columnNames, index++, insertedValues, preparedStatement,
				Types.BIGINT, objectEntryId);

			preparedStatement.executeUpdate();

			FinderCacheUtil.clearDSLQueryCache(
				dynamicObjectDefinitionTable.getTableName());
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	private void _validateAutoIncrementValue(
			ObjectField objectField, String value)
		throws PortalException {

		if (Validator.isNull(value)) {
			return;
		}

		String prefix = ObjectFieldSettingUtil.getValue(
			ObjectFieldSettingConstants.NAME_PREFIX, objectField);
		String suffix = ObjectFieldSettingUtil.getValue(
			ObjectFieldSettingConstants.NAME_SUFFIX, objectField);

		if ((Validator.isNotNull(prefix) &&
			 !StringUtil.startsWith(value, prefix)) ||
			(Validator.isNotNull(suffix) &&
			 !StringUtil.endsWith(value, suffix))) {

			throw new ObjectEntryValuesException.InvalidValue(
				objectField.getName());
		}

		String initialValue = ObjectFieldSettingUtil.getValue(
			ObjectFieldSettingConstants.NAME_INITIAL_VALUE, objectField);
		String sortableValue = _getAutoIncrementSortableValue(
			prefix, suffix, value);

		if ((initialValue.length() > sortableValue.length()) ||
			((initialValue.length() < sortableValue.length()) &&
			 StringUtil.startsWith(sortableValue, CharPool.NUMBER_0))) {

			throw new ObjectEntryValuesException.InvalidValue(
				objectField.getName());
		}

		long parsedValue = 0;

		try {
			parsedValue = Long.parseUnsignedLong(sortableValue);
		}
		catch (NumberFormatException numberFormatException) {
			if (_log.isDebugEnabled()) {
				_log.debug(numberFormatException);
			}
		}

		if (parsedValue < GetterUtil.getLong(initialValue)) {
			throw new ObjectEntryValuesException.InvalidValue(
				objectField.getName());
		}
	}

	private void _validateExternalReferenceCode(
		String externalReferenceCode, long groupId, long companyId,
		long objectDefinitionId, long objectEntryId) {

		ObjectEntry objectEntry = objectEntryPersistence.fetchByERC_G_C_ODI(
			externalReferenceCode, groupId, companyId, objectDefinitionId);

		if ((objectEntry != null) &&
			(objectEntry.getObjectEntryId() != objectEntryId)) {

			throw new DuplicateObjectEntryExternalReferenceCodeException(
				StringBundler.concat(
					"Duplicate object entry with external reference code ",
					externalReferenceCode, ", group ID ", groupId,
					", and object definition ID ", objectDefinitionId));
		}
	}

	private void _validateFileExtension(
			String fileExtension, long objectFieldId, String objectFieldName,
			List<ValidationError> validationErrors)
		throws PortalException {

		String[] acceptedFileExtensions =
			_attachmentManager.getAcceptedFileExtensions(objectFieldId);

		if (!ArrayUtil.contains(acceptedFileExtensions, StringPool.STAR) &&
			!ArrayUtil.contains(acceptedFileExtensions, fileExtension, true)) {

			_handle(
				new ObjectEntryValuesException.InvalidFileExtension(
					fileExtension, objectFieldName),
				validationErrors);
		}
	}

	private void _validateFileSize(
			boolean guestUser, long fileSize, long objectFieldId,
			String objectFieldName, List<ValidationError> validationErrors)
		throws PortalException {

		long maximumFileSize = _attachmentManager.getMaximumFileSize(
			objectFieldId, !guestUser);

		if ((maximumFileSize > 0) && (fileSize > maximumFileSize)) {
			_handle(
				new ObjectEntryValuesException.ExceedsMaxFileSize(
					maximumFileSize / (1024 * 1024), objectFieldName),
				validationErrors);
		}
	}

	private void _validateGroupId(
			long groupId, ObjectDefinition objectDefinition)
		throws PortalException {

		String scope = objectDefinition.getScope();

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(scope);

		if (!objectScopeProvider.isValidGroupId(groupId)) {
			throw new ObjectEntryGroupIdException.InvalidGroupIdForScope(
				groupId, scope);
		}

		if (!StringUtil.equals(scope, ObjectDefinitionConstants.SCOPE_DEPOT) ||
			GetterUtil.getBoolean(
				ObjectDefinitionSettingUtil.getValue(
					ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
					objectDefinition.getObjectDefinitionSettings()))) {

			return;
		}

		String acceptedGroupIds = ObjectDefinitionSettingUtil.getValue(
			ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS,
			objectDefinition.getObjectDefinitionSettings());

		if (Validator.isNull(acceptedGroupIds) ||
			!ArrayUtil.exists(
				acceptedGroupIds.split("\\s*,\\s*"),
				acceptedGroupId -> acceptedGroupId.equals(
					String.valueOf(groupId)))) {

			throw new NoSuchObjectDefinitionException();
		}
	}

	private void _validateListTypeEntryKey(
			String listTypeEntryKey, ObjectField objectField,
			List<ValidationError> validationErrors)
		throws PortalException {

		if (Validator.isNull(listTypeEntryKey)) {
			return;
		}

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.fetchListTypeEntry(
				objectField.getListTypeDefinitionId(), listTypeEntryKey);

		if (listTypeEntry != null) {
			return;
		}

		try {
			_listTypeEntryService.getOrAddEmptyListTypeEntry(
				objectField.getUserId(), objectField.getListTypeDefinitionId(),
				listTypeEntryKey);
		}
		catch (NoSuchListTypeEntryException noSuchListTypeEntryException) {
			if (Validator.isNotNull(listTypeEntryKey)) {
				_handle(
					new ObjectEntryValuesException.ListTypeEntry(
						objectField.getName()),
					validationErrors);
			}

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchListTypeEntryException);
			}
		}
	}

	private void _validateObjectEntryFolderId(
			long groupId, long objectEntryFolderId)
		throws PortalException {

		if (objectEntryFolderId ==
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT) {

			return;
		}

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderPersistence.findByPrimaryKey(objectEntryFolderId);

		if (objectEntryFolder.getGroupId() != groupId) {
			throw new ObjectEntryFolderScopeException(
				StringBundler.concat(
					"Group ID ", groupId,
					" does not match parent object entry folder group ID ",
					objectEntryFolder.getGroupId()));
		}
	}

	private void _validateObjectStateTransition(
			Map<String, Serializable> existingValues, long listTypeDefinitionId,
			ObjectField objectField, long userId,
			List<ValidationError> validationErrors, Serializable value)
		throws PortalException {

		Serializable existingValue = existingValues.get(objectField.getName());

		if (existingValue == null) {
			return;
		}

		ListTypeEntry originalListTypeEntry =
			_listTypeEntryLocalService.getListTypeEntry(
				listTypeDefinitionId, String.valueOf(existingValue));

		ObjectStateFlow objectStateFlow =
			_objectStateFlowLocalService.fetchObjectFieldObjectStateFlow(
				objectField.getObjectFieldId());

		ObjectState sourceObjectState =
			_objectStateLocalService.getObjectStateFlowObjectState(
				originalListTypeEntry.getListTypeEntryId(),
				objectStateFlow.getObjectStateFlowId());

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.getListTypeEntry(
				listTypeDefinitionId, String.valueOf(value));

		ObjectState targetObjectState =
			_objectStateLocalService.getObjectStateFlowObjectState(
				listTypeEntry.getListTypeEntryId(),
				objectStateFlow.getObjectStateFlowId());

		if (sourceObjectState.getObjectStateId() ==
				targetObjectState.getObjectStateId()) {

			return;
		}

		boolean invalidObjectStateTransition = true;

		for (ObjectState nextObjectState :
				_objectStateLocalService.getNextObjectStates(
					sourceObjectState.getObjectStateId())) {

			if (nextObjectState.getListTypeEntryId() ==
					targetObjectState.getListTypeEntryId()) {

				invalidObjectStateTransition = false;
			}
		}

		if (invalidObjectStateTransition) {
			User user = _userLocalService.getUser(userId);

			_handle(
				new ObjectEntryValuesException.InvalidObjectStateTransition(
					originalListTypeEntry.getName(user.getLocale()),
					sourceObjectState, listTypeEntry.getName(user.getLocale()),
					targetObjectState),
				validationErrors);
		}
	}

	private void _validateOneToOneInsert(
			String dbColumnName, long dbColumnValue,
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable)
		throws PortalException {

		if (dbColumnValue == 0) {
			return;
		}

		int count = 0;

		Connection connection = _currentConnection.getConnection(
			objectEntryPersistence.getDataSource());

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select count(*) from ",
					dynamicObjectDefinitionTable.getTableName(), " where ",
					dbColumnName, " = ?"))) {

			preparedStatement.setLong(1, dbColumnValue);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				resultSet.next();

				count = resultSet.getInt(1);
			}
		}
		catch (SQLException sqlException) {
			throw new SystemException(sqlException);
		}

		if (count > 0) {
			throw new ObjectEntryValuesException.OneToOneConstraintViolation(
				dbColumnName, dbColumnValue,
				dynamicObjectDefinitionTable.getTableName());
		}
	}

	private void _validateOneToOneUpdate(
			String dbColumnName, long dbColumnValue,
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
			long objectEntryId)
		throws PortalException {

		if (dbColumnValue == 0) {
			return;
		}

		int count = 0;

		Connection connection = _currentConnection.getConnection(
			objectEntryPersistence.getDataSource());

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select count(*) from ",
					dynamicObjectDefinitionTable.getTableName(), " where ",
					dynamicObjectDefinitionTable.getPrimaryKeyColumnName(),
					" != ? and ", dbColumnName, " = ?"))) {

			preparedStatement.setLong(1, objectEntryId);
			preparedStatement.setLong(2, dbColumnValue);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				resultSet.next();

				count = resultSet.getInt(1);
			}
		}
		catch (SQLException sqlException) {
			throw new SystemException(sqlException);
		}

		if (count > 0) {
			throw new ObjectEntryValuesException.OneToOneConstraintViolation(
				dbColumnName, dbColumnValue,
				dynamicObjectDefinitionTable.getTableName());
		}
	}

	private void _validateRequiredValues(
			String defaultLanguageId, Map<String, Serializable> existingValues,
			ObjectField objectField, boolean partialUpdate,
			ServiceContext serviceContext, Integer status,
			List<ValidationError> validationErrors,
			Map<String, Serializable> values)
		throws PortalException {

		try {
			_validateRequiredValues(
				defaultLanguageId, existingValues, objectField, partialUpdate,
				serviceContext, status, values);
		}
		catch (ObjectEntryValuesException.Required objectEntryValuesException) {
			if (!objectField.isLocalized()) {
				_handle(objectEntryValuesException, validationErrors);
			}

			_handle(
				new ObjectEntryValuesException.RequiredLanguageId(
					defaultLanguageId, objectField.getName()),
				validationErrors);
		}
	}

	private void _validateRequiredValues(
			String defaultLanguageId, Map<String, Serializable> existingValues,
			ObjectField objectField, boolean partialUpdate,
			ServiceContext serviceContext, Integer status,
			Map<String, Serializable> values)
		throws PortalException {

		if (!objectField.isRequired() ||
			(serviceContext.getWorkflowAction() ==
				WorkflowConstants.ACTION_SAVE_DRAFT)) {

			return;
		}

		String name = objectField.getName();

		if (objectField.isLocalized()) {
			name = objectField.getI18nObjectFieldName();
		}

		if (!values.containsKey(name)) {
			if ((existingValues == null) || !partialUpdate ||
				(status.equals(WorkflowConstants.STATUS_DRAFT) &&
				 Validator.isNull(MapUtil.getString(existingValues, name)))) {

				throw new ObjectEntryValuesException.Required(
					objectField.getName());
			}

			return;
		}

		Serializable value = values.get(name);

		if (objectField.isLocalized()) {
			Map<String, Serializable> localizedValues =
				(Map<String, Serializable>)value;

			if (!localizedValues.containsKey(defaultLanguageId)) {
				throw new ObjectEntryValuesException.Required(
					objectField.getName());
			}

			value = localizedValues.get(defaultLanguageId);
		}

		if (Validator.isNull(value)) {
			throw new ObjectEntryValuesException.Required(
				objectField.getName());
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE)) {

			if (MapUtil.isEmpty((Map<String, Serializable>)value)) {
				throw new ObjectEntryValuesException.Required(
					objectField.getName());
			}
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN)) {

			if (!GetterUtil.getBoolean(value)) {
				throw new ObjectEntryValuesException.Required(
					objectField.getName());
			}
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST)) {

			List<String> listTypeEntryKeys = null;

			if (value instanceof List) {
				listTypeEntryKeys = (List<String>)value;
			}
			else {
				listTypeEntryKeys = ListUtil.fromString(
					GetterUtil.getString(String.valueOf(value)),
					StringPool.COMMA_AND_SPACE);
			}

			if (listTypeEntryKeys.isEmpty()) {
				throw new ObjectEntryValuesException.Required(
					objectField.getName());
			}
		}
	}

	private void _validateTextMaxLength(
			int defaultMaxLength, String objectEntryValue, long objectFieldId,
			String objectFieldName, List<ValidationError> validationErrors)
		throws PortalException {

		int maxLength;

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingPersistence.fetchByOFI_N(
				objectFieldId, "maxLength");

		if (objectFieldSetting != null) {
			maxLength = GetterUtil.getInteger(objectFieldSetting.getValue());
		}
		else {
			maxLength = defaultMaxLength;
		}

		if (objectEntryValue.length() > maxLength) {
			_handle(
				new ObjectEntryValuesException.ExceedsTextMaxLength(
					maxLength, objectFieldName),
				validationErrors);
		}
	}

	private void _validateTextMaxLength280(
			ObjectField objectField, List<ValidationError> validationErrors,
			String value)
		throws PortalException {

		_validateTextMaxLength(
			280, value, objectField.getObjectFieldId(), objectField.getName(),
			validationErrors);
	}

	private void _validateUniqueValues(
			long groupId, ObjectDefinition objectDefinition, Long objectEntryId,
			ObjectField objectField, long userId,
			List<ValidationError> validationErrors, Object value,
			String valueLanguageId)
		throws PortalException {

		String valueString = GetterUtil.getString(value);

		if (Validator.isBlank(valueString.trim())) {
			return;
		}

		long objectEntriesCount = 0;
		Table<?> table = null;

		try {
			table = _objectFieldLocalService.getTable(
				objectDefinition.getObjectDefinitionId(),
				objectField.getName());

			Column<?, Object> column = (Column<?, Object>)table.getColumn(
				objectField.getDBColumnName());

			Predicate predicate =
				ObjectEntrySearchUtil.getUniqueCompositeKeyObjectFieldPredicate(
					column, objectField.getDBType(), value);

			if (objectEntryId != null) {
				predicate = predicate.and(
					ObjectEntryTable.INSTANCE.objectEntryId.neq(objectEntryId));
			}

			objectEntriesCount = getObjectEntriesCount(
				groupId, valueLanguageId, objectDefinition, predicate);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}

		if (objectEntriesCount == 0) {
			return;
		}

		User user = _userLocalService.getUser(userId);
		Table<?> finalTable = table;

		_handle(
			new ObjectEntryValuesException.UniqueValueConstraintViolation(
				objectField.getDBColumnName(), (Serializable)value,
				objectField.getLabel(user.getLocale()),
				finalTable.getTableName(), null),
			validationErrors);
	}

	private void _validateValues(
			Map<ObjectField, Set<DLFileEntry>> dlFileEntriesMap,
			Map<String, Serializable> existingValues, long groupId,
			boolean guestUser, ObjectDefinition objectDefinition,
			Long objectEntryId, ObjectField objectField, long userId,
			List<ValidationError> validationErrors, Serializable value,
			String valueLanguageId)
		throws PortalException {

		if (StringUtil.equals(
				objectField.getBusinessType(),
				ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE)) {

			return;
		}
		else if (StringUtil.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

			DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
				GetterUtil.getLong(value));

			if ((dlFileEntry != null) &&
				(dlFileEntry.getCompanyId() == objectField.getCompanyId())) {

				_validateFileExtension(
					dlFileEntry.getExtension(), objectField.getObjectFieldId(),
					objectField.getName(), validationErrors);
				_validateFileSize(
					guestUser, dlFileEntry.getSize(),
					objectField.getObjectFieldId(), objectField.getName(),
					validationErrors);

				if ((existingValues != null) &&
					(dlFileEntry.getFileEntryId() == GetterUtil.getLong(
						existingValues.get(objectField.getName())))) {

					return;
				}

				Set<DLFileEntry> dlFileEntries =
					dlFileEntriesMap.computeIfAbsent(
						objectField, key -> new HashSet<>());

				dlFileEntries.add(dlFileEntry);

				return;
			}

			if (Validator.isNotNull(value)) {
				_handle(
					new ObjectEntryValuesException.InvalidValue(
						objectField.getName()),
					validationErrors);
			}
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_ENCRYPTED)) {

			_validateTextMaxLength280(
				objectField, validationErrors, GetterUtil.getString(value));
		}
		else if (StringUtil.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT)) {

			_validateTextMaxLength(
				65000, GetterUtil.getString(value),
				objectField.getObjectFieldId(), objectField.getName(),
				validationErrors);
		}
		else if (StringUtil.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

			if (StringUtil.equals(
					objectDefinition.getScope(),
					ObjectDefinitionConstants.SCOPE_DEPOT)) {

				ObjectEntry relatedObjectEntry = fetchObjectEntry(
					GetterUtil.getLong(value));

				if ((relatedObjectEntry != null) &&
					(groupId != relatedObjectEntry.getGroupId())) {

					_handle(
						new ObjectEntryValuesException.InvalidValue(
							objectField.getName()),
						validationErrors);
				}
			}

			if (existingValues == null) {
				return;
			}

			ObjectRelationship objectRelationship =
				_objectRelationshipPersistence.fetchByObjectFieldId2(
					objectField.getObjectFieldId());

			if (objectRelationship.isEdge() &&
				!Objects.equals(
					existingValues.get(objectField.getName()), value)) {

				_handle(
					new ObjectEntryValuesException.InvalidValue(
						objectField.getName()),
					validationErrors);
			}

			if (!objectDefinition.isAccountEntryRestricted() ||
				!Objects.equals(
					objectField.getObjectFieldId(),
					objectDefinition.
						getAccountEntryRestrictedObjectFieldId())) {

				return;
			}
		}
		else if (StringUtil.equals(
					objectField.getDBType(),
					ObjectFieldConstants.DB_TYPE_INTEGER)) {

			String entryValueString = String.valueOf(value);

			if (!entryValueString.isEmpty()) {
				int entryValueInteger = GetterUtil.getInteger(entryValueString);

				if ((entryValueInteger == 0) &&
					!StringUtil.equals(
						String.valueOf(entryValueInteger), entryValueString)) {

					_handle(
						new ObjectEntryValuesException.ExceedsIntegerSize(
							9, objectField.getName()),
						validationErrors);
				}
			}
		}
		else if (StringUtil.equals(
					objectField.getDBType(),
					ObjectFieldConstants.DB_TYPE_LONG)) {

			String entryValueString = String.valueOf(value);

			if (!entryValueString.isEmpty()) {
				long entryValueLong = GetterUtil.getLong(entryValueString);

				if ((entryValueLong == 0) &&
					!StringUtil.equals(
						String.valueOf(entryValueLong), entryValueString)) {

					_handle(
						new ObjectEntryValuesException.ExceedsLongSize(
							16, objectField.getName()),
						validationErrors);
				}
				else if (entryValueLong > ObjectFieldValidationConstants.
							BUSINESS_TYPE_LONG_VALUE_MAX) {

					_handle(
						new ObjectEntryValuesException.ExceedsLongMaxSize(
							ObjectFieldValidationConstants.
								BUSINESS_TYPE_LONG_VALUE_MAX,
							objectField.getName()),
						validationErrors);
				}
				else if (entryValueLong < ObjectFieldValidationConstants.
							BUSINESS_TYPE_LONG_VALUE_MIN) {

					_handle(
						new ObjectEntryValuesException.ExceedsLongMinSize(
							ObjectFieldValidationConstants.
								BUSINESS_TYPE_LONG_VALUE_MIN,
							objectField.getName()),
						validationErrors);
				}
			}
		}
		else if (StringUtil.equals(
					objectField.getDBType(),
					ObjectFieldConstants.DB_TYPE_STRING)) {

			_validateTextMaxLength(
				DynamicObjectDefinitionTableUtil.getMaxLength(
					objectField.getBusinessType()),
				GetterUtil.getString(value), objectField.getObjectFieldId(),
				objectField.getName(), validationErrors);
		}

		if (objectField.getListTypeDefinitionId() != 0) {
			if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST)) {

				List<String> listTypeEntryKeys = null;

				if (value instanceof List) {
					listTypeEntryKeys = (List<String>)value;
				}
				else if (value instanceof Object[]) {
					listTypeEntryKeys = TransformUtil.transformToList(
						(Object[])value,
						currentValue -> GetterUtil.getString(
							String.valueOf(currentValue)));
				}
				else {
					listTypeEntryKeys = ListUtil.fromString(
						GetterUtil.getString(String.valueOf(value)),
						StringPool.COMMA_AND_SPACE);
				}

				for (String listTypeEntryKey : listTypeEntryKeys) {
					_validateListTypeEntryKey(
						listTypeEntryKey, objectField, validationErrors);
				}
			}
			else {
				_validateListTypeEntryKey(
					String.valueOf(value), objectField, validationErrors);

				if ((existingValues != null) && objectField.isState()) {
					_validateObjectStateTransition(
						existingValues, objectField.getListTypeDefinitionId(),
						objectField, userId, validationErrors, value);
				}
			}
		}

		if (objectField.hasUniqueValues()) {
			_validateUniqueValues(
				groupId, objectDefinition, objectEntryId, objectField, userId,
				validationErrors, value, valueLanguageId);
		}
	}

	private void _validateValues(
			String defaultLanguageId,
			Map<ObjectField, Set<DLFileEntry>> dlFileEntriesMap,
			Map<String, Serializable> existingValues, long groupId,
			boolean guestUser, ObjectDefinition objectDefinition,
			Long objectEntryId, List<ObjectField> objectFields,
			boolean partialUpdate, ServiceContext serviceContext,
			Integer status, long userId, List<ValidationError> validationErrors,
			Map<String, Serializable> values)
		throws PortalException {

		for (ObjectField objectField : objectFields) {
			_validateRequiredValues(
				defaultLanguageId, existingValues, objectField, partialUpdate,
				serviceContext, status, validationErrors, values);

			if (!objectField.isLocalized() &&
				(values.get(objectField.getName()) != null)) {

				_validateValues(
					dlFileEntriesMap, existingValues, groupId, guestUser,
					objectDefinition, objectEntryId, objectField, userId,
					validationErrors, values.get(objectField.getName()),
					StringPool.BLANK);
			}

			Map<String, Serializable> localizedValues =
				(Map<String, Serializable>)values.get(
					objectField.getI18nObjectFieldName());

			if (MapUtil.isEmpty(localizedValues)) {
				continue;
			}

			for (Map.Entry<String, Serializable> entry :
					localizedValues.entrySet()) {

				_validateValues(
					dlFileEntriesMap, existingValues, groupId, guestUser,
					objectDefinition, objectEntryId, objectField, userId,
					validationErrors, entry.getValue(), entry.getKey());
			}
		}
	}

	private void _validateWorkflowAction(
			boolean enableObjectEntryDraft, ObjectDefinition objectDefinition,
			Integer status, Integer workflowAction)
		throws PortalException {

		if (workflowAction != WorkflowConstants.ACTION_SAVE_DRAFT) {
			return;
		}

		if (!enableObjectEntryDraft ||
			((status != null) && (status != WorkflowConstants.STATUS_DRAFT) &&
			 !objectDefinition.isEnableObjectEntryVersioning())) {

			throw new ObjectEntryStatusException("Draft status is not allowed");
		}
	}

	private static final Expression<?>[] _EXPRESSIONS = {
		ObjectEntryTable.INSTANCE.objectEntryId,
		ObjectEntryTable.INSTANCE.userName,
		ObjectEntryTable.INSTANCE.createDate,
		ObjectEntryTable.INSTANCE.modifiedDate,
		ObjectEntryTable.INSTANCE.externalReferenceCode,
		ObjectEntryTable.INSTANCE.status
	};

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryLocalServiceImpl.class);

	private static final Map<Integer, Function<Object, Object>>
		_getValueFunctions =
			HashMapBuilder.<Integer, Function<Object, Object>>put(
				Types.BIGINT,
				value -> {
					if (value instanceof Long) {
						return value;
					}

					return GetterUtil.getLong(value);
				}
			).put(
				Types.BOOLEAN,
				value -> {
					if (value instanceof Boolean) {
						return value;
					}

					return GetterUtil.getBoolean(value);
				}
			).put(
				Types.CLOB,
				value -> {
					if (value instanceof String s) {
						return s.trim();
					}

					if (value == null) {
						return StringPool.BLANK;
					}

					return GetterUtil.getString(value);
				}
			).put(
				Types.DATE,
				value -> {
					if ((value == null) || (value instanceof Timestamp)) {
						return value;
					}

					Date date = (Date)value;

					return new Timestamp(date.getTime());
				}
			).put(
				Types.DECIMAL, Function.identity()
			).put(
				Types.DOUBLE,
				value -> {
					if (value instanceof Double) {
						return value;
					}

					return GetterUtil.getDouble(value);
				}
			).put(
				Types.INTEGER,
				value -> {
					if (value instanceof Integer) {
						return value;
					}

					return GetterUtil.getInteger(value);
				}
			).put(
				Types.TIMESTAMP,
				value -> {
					if ((value == null) || (value instanceof Timestamp)) {
						return value;
					}

					Date date = (Date)value;

					return new Timestamp(date.getTime());
				}
			).put(
				Types.VARCHAR, Function.identity()
			).build();

	private static final Snapshot<ObjectActionEngine>
		_objectActionEngineSnapshot = new Snapshot<>(
			ObjectEntryLocalServiceImpl.class, ObjectActionEngine.class, null);
	private static final Snapshot<ObjectRelationshipLocalService>
		_objectRelationshipLocalServiceSnapshot = new Snapshot<>(
			ObjectEntryLocalServiceImpl.class,
			ObjectRelationshipLocalService.class, null);

	private static final Map<Class<?>, Function<Object, Serializable>>
		_putValueFunctions =
			HashMapBuilder.<Class<?>, Function<Object, Serializable>>put(
				BigDecimal.class,
				value -> BigDecimalUtil.stripTrailingZeros((BigDecimal)value)
			).put(
				Blob.class,
				value -> {
					if (value instanceof byte[] bytes) {
						return bytes;
					}

					if (value instanceof Blob blob) {

						// Hypersonic

						try {
							return blob.getBytes(1, (int)blob.length());
						}
						catch (SQLException sqlException) {
							throw new SystemException(sqlException);
						}
					}

					return null;
				}
			).put(
				Boolean.class,
				value -> {
					if (value instanceof Boolean booleanValue) {
						return booleanValue;
					}

					if (value instanceof Byte byteValue) {
						if (byteValue.intValue() == 0) {
							return Boolean.FALSE;
						}

						return Boolean.TRUE;
					}

					return Boolean.FALSE;
				}
			).put(
				Clob.class,
				value -> {
					if (value instanceof Clob clob) {
						try {
							return StreamUtil.toString(
								clob.getAsciiStream(), StringPool.UTF8);
						}
						catch (IOException | SQLException exception) {
							throw new SystemException(exception);
						}
					}

					if (value instanceof String s) {
						return s;
					}

					return StringPool.BLANK;
				}
			).put(
				Date.class, value -> (Date)value
			).put(
				Double.class,
				value -> {
					if (value instanceof Double doubleValue) {
						return doubleValue;
					}

					if (value instanceof Number number) {
						return number.doubleValue();
					}

					return 0D;
				}
			).put(
				Integer.class,
				value -> {
					if (value instanceof Integer intValue) {
						return intValue;
					}

					if (value instanceof Number number) {
						return number.intValue();
					}

					return 0;
				}
			).put(
				Long.class,
				value -> {
					if (value instanceof Long longValue) {
						return longValue;
					}

					if (value instanceof Number number) {
						return number.longValue();
					}

					return 0L;
				}
			).put(
				String.class,
				value -> {
					if (value instanceof String s) {
						return s;
					}

					return StringPool.BLANK;
				}
			).put(
				Timestamp.class, value -> (Timestamp)value
			).build();

	private static final ThreadLocal<Boolean> _skipModelListeners =
		new CentralizedThreadLocal<>(
			ObjectEntryLocalServiceImpl.class + "._skipModelListeners",
			() -> false);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private AssetTagGroupRelLocalService _assetTagGroupRelLocalService;

	@Reference
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

	@Reference
	private AttachmentManager _attachmentManager;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommentManager _commentManager;

	private final Map<Long, Date> _companyIdPreviousCheckDate =
		new ConcurrentHashMap<>();

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CurrentConnection _currentConnection;

	@Reference
	private DDMExpressionFactory _ddmExpressionFactory;

	@Reference
	private DiscussionPermission _discussionPermission;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private EmptyModelManager _emptyModelManager;

	@Reference
	private Encryptor _encryptor;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private InlineSQLHelper _inlineSQLHelper;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ListTypeEntryService _listTypeEntryService;

	@Reference
	private Localization _localization;

	private volatile ObjectConfiguration _objectConfiguration;

	@Reference
	private ObjectDefinitionPersistence _objectDefinitionPersistence;

	@Reference
	private ObjectDefinitionSettingPersistence
		_objectDefinitionSettingPersistence;

	@Reference
	private ObjectEntryFolderPersistence _objectEntryFolderPersistence;

	@Reference
	private ObjectEntryVersionLocalService _objectEntryVersionLocalService;

	@Reference
	private ObjectEntryVersionPersistence _objectEntryVersionPersistence;

	@Reference
	private ObjectFieldBusinessTypeRegistry _objectFieldBusinessTypeRegistry;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFieldPersistence _objectFieldPersistence;

	@Reference
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Reference
	private ObjectFieldSettingPersistence _objectFieldSettingPersistence;

	private final Map<String, ObjectFilterParser> _objectFilterParsers =
		new HashMap<>();

	@Reference
	private ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;

	@Reference
	private ObjectRelationshipPersistence _objectRelationshipPersistence;

	@Reference
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

	@Reference
	private ObjectStateFlowLocalService _objectStateFlowLocalService;

	@Reference
	private ObjectStateLocalService _objectStateLocalService;

	@Reference
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceActions _resourceActions;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	private ServiceTrackerList<ObjectEntryValuesContributor>
		_serviceTrackerList;

	@Reference
	private SharingEntryLocalService _sharingEntryLocalService;

	@Reference
	private Sorts _sorts;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

	@Reference
	private TrashVersionLocalService _trashVersionLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}