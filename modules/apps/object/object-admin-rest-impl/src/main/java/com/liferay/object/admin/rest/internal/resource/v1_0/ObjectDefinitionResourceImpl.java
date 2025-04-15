/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.resource.v1_0;

import com.liferay.account.model.AccountEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.admin.rest.dto.v1_0.ObjectAction;
import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.dto.v1_0.ObjectField;
import com.liferay.object.admin.rest.dto.v1_0.ObjectLayout;
import com.liferay.object.admin.rest.dto.v1_0.ObjectRelationship;
import com.liferay.object.admin.rest.dto.v1_0.ObjectValidationRule;
import com.liferay.object.admin.rest.dto.v1_0.ObjectView;
import com.liferay.object.admin.rest.dto.v1_0.Status;
import com.liferay.object.admin.rest.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.object.admin.rest.internal.dto.v1_0.util.ObjectDefinitionSettingUtil;
import com.liferay.object.admin.rest.internal.dto.v1_0.util.ObjectFieldSettingUtil;
import com.liferay.object.admin.rest.internal.dto.v1_0.util.ObjectFieldUtil;
import com.liferay.object.admin.rest.internal.odata.entity.v1_0.ObjectDefinitionEntityModel;
import com.liferay.object.admin.rest.resource.v1_0.ObjectActionResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectLayoutResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectRelationshipResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectValidationRuleResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectViewResource;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.definition.util.ObjectDefinitionUtil;
import com.liferay.object.exception.ObjectDefinitionStatusException;
import com.liferay.object.exception.ObjectDefinitionStorageTypeException;
import com.liferay.object.model.ObjectActionModel;
import com.liferay.object.model.ObjectFieldModel;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectRelationshipModel;
import com.liferay.object.model.ObjectValidationRuleModel;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectActionService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectFilterLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectLayoutLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.service.ObjectViewLocalService;
import com.liferay.object.service.ObjectViewService;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.mass.delete.MassDeleteCacheThreadLocal;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/object-definition.properties",
	scope = ServiceScope.PROTOTYPE, service = ObjectDefinitionResource.class
)
public class ObjectDefinitionResourceImpl
	extends BaseObjectDefinitionResourceImpl {

	@Override
	public void deleteObjectDefinition(Long objectDefinitionId)
		throws Exception {

		long startTime = 0;

		if (_log.isInfoEnabled()) {
			_log.info("Deleting object definition " + objectDefinitionId);

			startTime = System.currentTimeMillis();
		}

		try (SafeCloseable safeCloseable1 = SearchContext.openBatchMode();
			SafeCloseable safeCloseable2 =
				MassDeleteCacheThreadLocal.openMassDeleteMode()) {

			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					_objectDefinitionService.deleteObjectDefinition(
						objectDefinitionId);

					return null;
				});
		}
		catch (Throwable throwable) {
			throw new Exception(throwable);
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Deleted object definition ", objectDefinitionId, " in ",
					System.currentTimeMillis() - startTime, "ms"));
		}
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public ObjectDefinition getObjectDefinition(Long objectDefinitionId)
		throws Exception {

		return _toObjectDefinition(
			_objectDefinitionService.getObjectDefinition(objectDefinitionId));
	}

	@Override
	public ObjectDefinition getObjectDefinitionByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		return _toObjectDefinition(
			_objectDefinitionService.getObjectDefinitionByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId()));
	}

	@Override
	public Page<ObjectDefinition> getObjectDefinitionsPage(
			String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					ObjectActionKeys.ADD_OBJECT_DEFINITION,
					"postObjectDefinition", ObjectConstants.RESOURCE_NAME,
					contextCompany.getCompanyId())
			).put(
				"createBatch",
				addAction(
					ObjectActionKeys.ADD_OBJECT_DEFINITION,
					"postObjectDefinitionBatch", ObjectConstants.RESOURCE_NAME,
					contextCompany.getCompanyId())
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteObjectDefinitionBatch",
					ObjectConstants.RESOURCE_NAME, null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getObjectDefinitionsPage",
					ObjectConstants.RESOURCE_NAME,
					contextCompany.getCompanyId())
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putObjectDefinitionBatch",
					ObjectConstants.RESOURCE_NAME, null)
			).build(),
			booleanQuery -> {
			},
			filter, com.liferay.object.model.ObjectDefinition.class.getName(),
			search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setAttribute(Field.NAME, search);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toObjectDefinition(
				_objectDefinitionService.getObjectDefinition(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public ObjectDefinition postObjectDefinition(
			ObjectDefinition objectDefinition)
		throws Exception {

		if (!Validator.isBlank(objectDefinition.getStorageType()) &&
			!FeatureFlagManagerUtil.isEnabled("LPS-135430")) {

			throw new ObjectDefinitionStorageTypeException();
		}

		_addListTypeDefinition(objectDefinition);

		com.liferay.object.model.ObjectDefinition
			serviceBuilderObjectDefinition;

		if (GetterUtil.getBoolean(objectDefinition.getSystem())) {
			serviceBuilderObjectDefinition =
				_objectDefinitionService.addSystemObjectDefinition(
					objectDefinition.getExternalReferenceCode(),
					contextUser.getUserId(),
					_getObjectFolderId(
						objectDefinition.
							getObjectFolderExternalReferenceCode()),
					objectDefinition.getClassName(),
					GetterUtil.getBoolean(objectDefinition.getEnableComments()),
					GetterUtil.getBoolean(
						objectDefinition.getEnableFriendlyURLCustomization()),
					GetterUtil.getBoolean(
						objectDefinition.getEnableIndexSearch()),
					GetterUtil.getBoolean(
						objectDefinition.getEnableLocalization(),
						FeatureFlagManagerUtil.isEnabled(
							contextUser.getCompanyId(), "LPD-32050")),
					GetterUtil.getBoolean(
						objectDefinition.getEnableObjectEntryDraft()),
					GetterUtil.getBoolean(
						objectDefinition.getEnableObjectEntryVersioning()),
					LocalizedMapUtil.populateLocalizedMap(
						objectDefinition.getDefaultLanguageId(),
						objectDefinition.getLabel()),
					objectDefinition.getName(),
					objectDefinition.getPanelAppOrder(),
					objectDefinition.getPanelCategoryKey(),
					LocalizedMapUtil.populateLocalizedMap(
						objectDefinition.getDefaultLanguageId(),
						objectDefinition.getPluralLabel()),
					GetterUtil.getBoolean(objectDefinition.getPortlet()),
					objectDefinition.getScope(),
					ObjectDefinitionSettingUtil.toObjectDefinitionSettings(
						contextUser.getCompanyId(), _groupLocalService,
						objectDefinition.getObjectDefinitionSettings(),
						_objectDefinitionSettingLocalService),
					transformToList(
						objectDefinition.getObjectFields(),
						objectField -> ObjectFieldUtil.toObjectField(
							objectDefinition.getDefaultLanguageId(),
							_listTypeDefinitionLocalService, objectField,
							_objectFieldLocalService,
							_objectFieldSettingLocalService,
							_objectFilterLocalService)));
		}
		else {
			serviceBuilderObjectDefinition =
				_objectDefinitionService.addCustomObjectDefinition(
					_getObjectFolderId(
						objectDefinition.
							getObjectFolderExternalReferenceCode()),
					objectDefinition.getClassName(),
					GetterUtil.getBoolean(objectDefinition.getEnableComments()),
					GetterUtil.getBoolean(
						objectDefinition.getEnableFriendlyURLCustomization()),
					GetterUtil.getBoolean(
						objectDefinition.getEnableIndexSearch(), true),
					GetterUtil.getBoolean(
						objectDefinition.getEnableLocalization(),
						FeatureFlagManagerUtil.isEnabled(
							contextUser.getCompanyId(), "LPD-32050")),
					GetterUtil.getBoolean(
						objectDefinition.getEnableObjectEntryDraft()),
					GetterUtil.getBoolean(
						objectDefinition.getEnableObjectEntryVersioning()),
					LocalizedMapUtil.populateLocalizedMap(
						objectDefinition.getDefaultLanguageId(),
						objectDefinition.getLabel()),
					objectDefinition.getName(),
					objectDefinition.getPanelAppOrder(),
					objectDefinition.getPanelCategoryKey(),
					LocalizedMapUtil.populateLocalizedMap(
						objectDefinition.getDefaultLanguageId(),
						objectDefinition.getPluralLabel()),
					GetterUtil.getBoolean(objectDefinition.getPortlet(), true),
					objectDefinition.getScope(),
					objectDefinition.getStorageType(),
					ObjectDefinitionSettingUtil.toObjectDefinitionSettings(
						contextUser.getCompanyId(), _groupLocalService,
						objectDefinition.getObjectDefinitionSettings(),
						_objectDefinitionSettingLocalService),
					transformToList(
						ArrayUtil.filter(
							objectDefinition.getObjectFields(),
							objectField ->
								!StringUtil.equals(
									objectField.getBusinessTypeAsString(),
									ObjectFieldConstants.
										BUSINESS_TYPE_AGGREGATION) &&
								!StringUtil.equals(
									objectField.getBusinessTypeAsString(),
									ObjectFieldConstants.
										BUSINESS_TYPE_RELATIONSHIP)),
						objectField -> ObjectFieldUtil.toObjectField(
							objectDefinition.getDefaultLanguageId(),
							_listTypeDefinitionLocalService, objectField,
							_objectFieldLocalService,
							_objectFieldSettingLocalService,
							_objectFilterLocalService)));
		}

		if (!Validator.isBlank(objectDefinition.getExternalReferenceCode())) {
			serviceBuilderObjectDefinition =
				_objectDefinitionService.updateExternalReferenceCode(
					serviceBuilderObjectDefinition.getObjectDefinitionId(),
					objectDefinition.getExternalReferenceCode());
		}

		com.liferay.object.model.ObjectField serviceBuilderObjectField =
			_objectFieldLocalService.fetchObjectField(
				serviceBuilderObjectDefinition.getObjectDefinitionId(),
				objectDefinition.getTitleObjectFieldName());

		if (serviceBuilderObjectField != null) {
			serviceBuilderObjectDefinition =
				_objectDefinitionService.updateTitleObjectFieldId(
					serviceBuilderObjectDefinition.getObjectDefinitionId(),
					serviceBuilderObjectField.getObjectFieldId());
		}

		if (objectDefinition.getObjectFields() != null) {
			for (ObjectField objectField : objectDefinition.getObjectFields()) {
				if (StringUtil.equals(
						objectField.getBusinessTypeAsString(),
						ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

					com.liferay.object.model.ObjectRelationship
						objectRelationship = _addObjectRelationship(
							objectDefinition.getDefaultLanguageId(),
							objectField, serviceBuilderObjectDefinition);

					if (objectDefinition.getAccountEntryRestricted() &&
						StringUtil.equals(
							objectDefinition.
								getAccountEntryRestrictedObjectFieldName(),
							objectField.getName())) {

						_objectDefinitionLocalService.
							enableAccountEntryRestricted(objectRelationship);
					}
				}
			}
		}

		_addObjectDefinitionResources(
			Collections.emptySet(), objectDefinition.getDefaultLanguageId(),
			objectDefinition.getObjectActions(),
			serviceBuilderObjectDefinition.getObjectDefinitionId(),
			objectDefinition.getObjectLayouts(),
			objectDefinition.getObjectRelationships(),
			objectDefinition.getObjectValidationRules(),
			objectDefinition.getObjectViews());

		for (com.liferay.object.model.ObjectField
				aggregationServiceBuilderObjectField :
					transformToList(
						ArrayUtil.filter(
							objectDefinition.getObjectFields(),
							objectField -> StringUtil.equals(
								objectField.getBusinessTypeAsString(),
								ObjectFieldConstants.
									BUSINESS_TYPE_AGGREGATION)),
						objectField -> ObjectFieldUtil.toObjectField(
							objectDefinition.getDefaultLanguageId(),
							_listTypeDefinitionLocalService, objectField,
							_objectFieldLocalService,
							_objectFieldSettingLocalService,
							_objectFilterLocalService))) {

			_objectFieldLocalService.addCustomObjectField(
				aggregationServiceBuilderObjectField.getExternalReferenceCode(),
				GuestOrUserUtil.getUserId(),
				aggregationServiceBuilderObjectField.getListTypeDefinitionId(),
				serviceBuilderObjectDefinition.getObjectDefinitionId(),
				aggregationServiceBuilderObjectField.getBusinessType(),
				aggregationServiceBuilderObjectField.getDBType(),
				aggregationServiceBuilderObjectField.isIndexed(),
				aggregationServiceBuilderObjectField.isIndexedAsKeyword(),
				aggregationServiceBuilderObjectField.getIndexedLanguageId(),
				aggregationServiceBuilderObjectField.getLabelMap(),
				aggregationServiceBuilderObjectField.isLocalized(),
				aggregationServiceBuilderObjectField.getName(),
				aggregationServiceBuilderObjectField.getReadOnly(),
				aggregationServiceBuilderObjectField.
					getReadOnlyConditionExpression(),
				aggregationServiceBuilderObjectField.isRequired(),
				aggregationServiceBuilderObjectField.isState(),
				aggregationServiceBuilderObjectField.getObjectFieldSettings());
		}

		Status status = objectDefinition.getStatus();

		if ((status != null) &&
			(status.getCode() == WorkflowConstants.STATUS_APPROVED)) {

			postObjectDefinitionPublish(
				serviceBuilderObjectDefinition.getObjectDefinitionId());

			serviceBuilderObjectDefinition =
				_objectDefinitionService.
					fetchObjectDefinitionByExternalReferenceCode(
						serviceBuilderObjectDefinition.
							getExternalReferenceCode(),
						serviceBuilderObjectDefinition.getCompanyId());
		}

		return _toObjectDefinition(serviceBuilderObjectDefinition);
	}

	@Override
	public ObjectDefinition postObjectDefinitionPublish(Long objectDefinitionId)
		throws Exception {

		com.liferay.object.model.ObjectDefinition
			serviceBuilderObjectDefinition =
				_objectDefinitionService.getObjectDefinition(
					objectDefinitionId);

		if (GetterUtil.getBoolean(serviceBuilderObjectDefinition.isSystem())) {
			return _toObjectDefinition(
				_objectDefinitionService.publishSystemObjectDefinition(
					objectDefinitionId));
		}

		return _toObjectDefinition(
			_objectDefinitionService.publishCustomObjectDefinition(
				objectDefinitionId));
	}

	@Override
	public ObjectDefinition putObjectDefinition(
			Long objectDefinitionId, ObjectDefinition objectDefinition)
		throws Exception {

		// TODO Move logic to service

		if (!Validator.isBlank(objectDefinition.getStorageType()) &&
			!FeatureFlagManagerUtil.isEnabled("LPS-135430")) {

			throw new ObjectDefinitionStorageTypeException();
		}

		com.liferay.object.model.ObjectDefinition
			serviceBuilderObjectDefinition =
				_objectDefinitionService.getObjectDefinition(
					objectDefinitionId);

		Status objectDefinitionStatus = objectDefinition.getStatus();
		int serviceBuilderObjectDefinitionStatus =
			serviceBuilderObjectDefinition.getStatus();

		if ((objectDefinitionStatus != null) &&
			(objectDefinitionStatus.getCode() !=
				WorkflowConstants.STATUS_APPROVED) &&
			(serviceBuilderObjectDefinitionStatus ==
				WorkflowConstants.STATUS_APPROVED)) {

			throw new ObjectDefinitionStatusException(
				"Modifying the status of a published object definition is " +
					"not allowed");
		}

		_addListTypeDefinition(objectDefinition);

		long accountEntryRestrictedObjectFieldId = 0;

		com.liferay.object.model.ObjectField
			accountEntryRestrictedServiceBuilderObjectField =
				_objectFieldLocalService.fetchObjectField(
					objectDefinitionId,
					objectDefinition.
						getAccountEntryRestrictedObjectFieldName());

		if (accountEntryRestrictedServiceBuilderObjectField != null) {
			accountEntryRestrictedObjectFieldId =
				accountEntryRestrictedServiceBuilderObjectField.
					getObjectFieldId();
		}

		int statusInt = serviceBuilderObjectDefinitionStatus;

		if (objectDefinitionStatus != null) {
			statusInt = objectDefinitionStatus.getCode();
		}

		if (serviceBuilderObjectDefinition.isUnmodifiableSystemObject()) {
			serviceBuilderObjectDefinition =
				_objectDefinitionService.updateSystemObjectDefinition(
					objectDefinition.getExternalReferenceCode(),
					objectDefinitionId,
					_getObjectFolderId(
						objectDefinition.
							getObjectFolderExternalReferenceCode()),
					0,
					ObjectDefinitionSettingUtil.toObjectDefinitionSettings(
						contextUser.getCompanyId(), _groupLocalService,
						objectDefinition.getObjectDefinitionSettings(),
						_objectDefinitionSettingLocalService));
		}
		else {
			serviceBuilderObjectDefinition =
				_objectDefinitionService.updateCustomObjectDefinition(
					objectDefinition.getExternalReferenceCode(),
					objectDefinitionId,
					GetterUtil.getLong(accountEntryRestrictedObjectFieldId), 0,
					_getObjectFolderId(
						objectDefinition.
							getObjectFolderExternalReferenceCode()),
					0,
					GetterUtil.getBoolean(
						objectDefinition.getAccountEntryRestricted()),
					GetterUtil.getBoolean(
						objectDefinition.getActive(),
						serviceBuilderObjectDefinition.isActive()),
					objectDefinition.getClassName(),
					GetterUtil.getBoolean(
						objectDefinition.getEnableCategorization(), true),
					GetterUtil.getBoolean(objectDefinition.getEnableComments()),
					GetterUtil.getBoolean(
						objectDefinition.getEnableFriendlyURLCustomization()),
					GetterUtil.getBoolean(
						objectDefinition.getEnableIndexSearch()),
					GetterUtil.getBoolean(
						objectDefinition.getEnableLocalization(),
						FeatureFlagManagerUtil.isEnabled(
							serviceBuilderObjectDefinition.getCompanyId(),
							"LPD-32050")),
					GetterUtil.getBoolean(
						objectDefinition.getEnableObjectEntryDraft()),
					GetterUtil.getBoolean(
						objectDefinition.getEnableObjectEntryHistory()),
					GetterUtil.getBoolean(
						objectDefinition.getEnableObjectEntryVersioning()),
					LocalizedMapUtil.populateLocalizedMap(
						objectDefinition.getDefaultLanguageId(),
						objectDefinition.getLabel()),
					objectDefinition.getName(),
					objectDefinition.getPanelAppOrder(),
					objectDefinition.getPanelCategoryKey(),
					GetterUtil.getBoolean(objectDefinition.getPortlet()),
					LocalizedMapUtil.populateLocalizedMap(
						objectDefinition.getDefaultLanguageId(),
						objectDefinition.getPluralLabel()),
					objectDefinition.getScope(), statusInt,
					ObjectDefinitionSettingUtil.toObjectDefinitionSettings(
						contextUser.getCompanyId(), _groupLocalService,
						objectDefinition.getObjectDefinitionSettings(),
						_objectDefinitionSettingLocalService));
		}

		List<ObjectAction> objectActions = ListUtil.fromArray(
			objectDefinition.getObjectActions());
		List<ObjectField> objectFields = ListUtil.fromArray(
			objectDefinition.getObjectFields());
		List<ObjectRelationship> objectRelationships = ListUtil.fromArray(
			objectDefinition.getObjectRelationships());
		List<ObjectValidationRule> objectValidationRules = ListUtil.fromArray(
			objectDefinition.getObjectValidationRules());

		List<com.liferay.object.model.ObjectAction>
			serviceBuilderObjectActions = new ArrayList<>(
				_objectActionLocalService.getObjectActions(objectDefinitionId));
		List<com.liferay.object.model.ObjectField> serviceBuilderObjectFields =
			new ArrayList<>(
				_objectFieldLocalService.getObjectFields(objectDefinitionId));
		List<com.liferay.object.model.ObjectRelationship>
			serviceBuilderObjectRelationships = new ArrayList<>(
				_objectRelationshipLocalService.getObjectRelationships(
					objectDefinitionId));
		List<com.liferay.object.model.ObjectValidationRule>
			serviceBuilderObjectValidationRules = new ArrayList<>(
				_objectValidationRuleLocalService.getObjectValidationRules(
					objectDefinitionId));

		if (serviceBuilderObjectDefinition.isModifiableAndSystem() &&
			ObjectDefinitionUtil.isInvokerBundleAllowed()) {

			objectActions.removeIf(
				objectAction -> !GetterUtil.getBoolean(
					objectAction.getSystem()));
			objectFields.removeIf(
				objectField -> !GetterUtil.getBoolean(objectField.getSystem()));
			objectRelationships.removeIf(
				objectRelationship -> !GetterUtil.getBoolean(
					objectRelationship.getSystem()));
			objectValidationRules.removeIf(
				objectValidationRule -> !GetterUtil.getBoolean(
					objectValidationRule.getSystem()));

			serviceBuilderObjectActions.removeIf(
				serviceBuilderObjectAction ->
					!serviceBuilderObjectAction.isSystem());
			serviceBuilderObjectFields.removeIf(
				serviceBuilderObjectField ->
					serviceBuilderObjectField.isMetadata() ||
					!serviceBuilderObjectField.isSystem());
			serviceBuilderObjectRelationships.removeIf(
				serviceBuilderObjectRelationship ->
					!serviceBuilderObjectRelationship.isSystem());
			serviceBuilderObjectValidationRules.removeIf(
				serviceBuilderObjectValidationRule ->
					!serviceBuilderObjectValidationRule.isSystem());
		}
		else {
			objectActions.removeIf(
				objectAction -> GetterUtil.getBoolean(
					objectAction.getSystem()));
			objectFields.removeIf(
				objectField -> GetterUtil.getBoolean(objectField.getSystem()));
			objectRelationships.removeIf(
				objectRelationship -> GetterUtil.getBoolean(
					objectRelationship.getSystem()));
			objectValidationRules.removeIf(
				objectValidationRule -> GetterUtil.getBoolean(
					objectValidationRule.getSystem()));

			serviceBuilderObjectActions.removeIf(ObjectActionModel::isSystem);
			serviceBuilderObjectFields.removeIf(ObjectFieldModel::isSystem);
			serviceBuilderObjectRelationships.removeIf(
				ObjectRelationshipModel::isSystem);
			serviceBuilderObjectValidationRules.removeIf(
				ObjectValidationRuleModel::isSystem);
		}

		for (ObjectField objectField : objectFields) {
			long listTypeDefinitionId = ObjectFieldUtil.getListTypeDefinitionId(
				serviceBuilderObjectDefinition.getCompanyId(),
				_listTypeDefinitionLocalService, objectField);

			if (objectField.getBusinessType() ==
					ObjectField.BusinessType.RELATIONSHIP) {

				com.liferay.object.model.ObjectField existingObjectField =
					_objectFieldLocalService.fetchObjectField(
						objectField.getExternalReferenceCode(),
						objectDefinitionId);

				if (existingObjectField == null) {
					_addObjectRelationship(
						objectDefinition.getDefaultLanguageId(), objectField,
						serviceBuilderObjectDefinition);

					continue;
				}
			}

			_objectFieldLocalService.updateObjectField(
				objectField.getExternalReferenceCode(),
				GetterUtil.getLong(objectField.getId()),
				contextUser.getUserId(), listTypeDefinitionId,
				objectDefinitionId, objectField.getBusinessTypeAsString(), null,
				null, objectField.getDBTypeAsString(),
				GetterUtil.getBoolean(objectField.getIndexed()),
				GetterUtil.getBoolean(objectField.getIndexedAsKeyword()),
				objectField.getIndexedLanguageId(),
				LocalizedMapUtil.populateLocalizedMap(
					objectDefinition.getDefaultLanguageId(),
					objectField.getLabel(), objectField.getName()),
				GetterUtil.getBoolean(objectField.getLocalized()),
				objectField.getName(), objectField.getReadOnlyAsString(),
				objectField.getReadOnlyConditionExpression(),
				GetterUtil.getBoolean(objectField.getRequired()),
				GetterUtil.getBoolean(objectField.getState()),
				GetterUtil.getBoolean(objectField.getSystem()),
				ObjectFieldSettingUtil.toObjectFieldSettings(
					listTypeDefinitionId, objectField,
					_objectFieldSettingLocalService,
					_objectFilterLocalService));

			serviceBuilderObjectFields.removeIf(
				serviceBuilderObjectField -> Objects.equals(
					serviceBuilderObjectField.getName(),
					objectField.getName()));
		}

		for (com.liferay.object.model.ObjectField serviceBuilderObjectField :
				serviceBuilderObjectFields) {

			if (Objects.equals(
					serviceBuilderObjectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

				continue;
			}

			_objectFieldLocalService.deleteObjectField(
				serviceBuilderObjectField);
		}

		com.liferay.object.model.ObjectField titleServiceBuilderObjectField =
			_objectFieldLocalService.fetchObjectField(
				objectDefinitionId, objectDefinition.getTitleObjectFieldName());

		if (titleServiceBuilderObjectField != null) {
			serviceBuilderObjectDefinition =
				_objectDefinitionService.updateTitleObjectFieldId(
					serviceBuilderObjectDefinition.getObjectDefinitionId(),
					titleServiceBuilderObjectField.getObjectFieldId());
		}

		Set<String> deleteObjectActionsERCs = SetUtil.asymmetricDifference(
			transform(
				serviceBuilderObjectActions,
				ObjectActionModel::getExternalReferenceCode),
			transform(objectActions, ObjectAction::getExternalReferenceCode));

		for (String deleteObjectActionsERC : deleteObjectActionsERCs) {
			_objectActionLocalService.deleteObjectAction(
				_objectActionLocalService.fetchObjectAction(
					deleteObjectActionsERC, objectDefinitionId));
		}

		ObjectLayout[] objectLayouts = objectDefinition.getObjectLayouts();

		if (objectLayouts != null) {
			_objectLayoutLocalService.deleteObjectLayouts(objectDefinitionId);
		}

		Set<String> accountEntryRestrictedObjectRelationshipsNames =
			_getAccountEntryRestrictedObjectRelationshipsNames(
				serviceBuilderObjectDefinition, objectRelationships);

		Set<String> deleteObjectRelationshipsNames =
			SetUtil.asymmetricDifference(
				transform(
					serviceBuilderObjectRelationships,
					com.liferay.object.model.ObjectRelationship::getName),
				transform(objectRelationships, ObjectRelationship::getName));

		for (String deleteObjectRelationshipsName :
				deleteObjectRelationshipsNames) {

			com.liferay.object.model.ObjectRelationship
				serviceBuilderObjectRelationship =
					_objectRelationshipLocalService.
						fetchObjectRelationshipByObjectDefinitionId(
							objectDefinitionId, deleteObjectRelationshipsName);

			_objectRelationshipLocalService.deleteObjectRelationship(
				serviceBuilderObjectRelationship.getObjectRelationshipId());
		}

		Set<String> deleteObjectValidationRulesERCs =
			SetUtil.asymmetricDifference(
				transform(
					serviceBuilderObjectValidationRules,
					com.liferay.object.model.ObjectValidationRule::
						getExternalReferenceCode),
				transform(
					objectValidationRules,
					ObjectValidationRule::getExternalReferenceCode));

		for (String deleteObjectValidationRulesERC :
				deleteObjectValidationRulesERCs) {

			_objectValidationRuleLocalService.deleteObjectValidationRule(
				_objectValidationRuleLocalService.fetchObjectValidationRule(
					deleteObjectValidationRulesERC, objectDefinitionId));
		}

		ObjectView[] objectViews = objectDefinition.getObjectViews();

		if (objectViews != null) {
			_objectViewLocalService.deleteObjectViews(objectDefinitionId);
		}

		_addObjectDefinitionResources(
			accountEntryRestrictedObjectRelationshipsNames,
			objectDefinition.getDefaultLanguageId(),
			objectActions.toArray(new ObjectAction[0]), objectDefinitionId,
			objectLayouts,
			objectRelationships.toArray(new ObjectRelationship[0]),
			objectValidationRules.toArray(new ObjectValidationRule[0]),
			objectViews);

		if ((statusInt != WorkflowConstants.STATUS_APPROVED) ||
			serviceBuilderObjectDefinition.isApproved()) {

			return _toObjectDefinition(serviceBuilderObjectDefinition);
		}

		return _toObjectDefinition(
			_objectDefinitionService.publishCustomObjectDefinition(
				serviceBuilderObjectDefinition.getObjectDefinitionId()));
	}

	@Override
	public ObjectDefinition putObjectDefinitionByExternalReferenceCode(
			String externalReferenceCode, ObjectDefinition objectDefinition)
		throws Exception {

		com.liferay.object.model.ObjectDefinition
			serviceBuilderObjectDefinition =
				_objectDefinitionService.
					fetchObjectDefinitionByExternalReferenceCode(
						externalReferenceCode, contextCompany.getCompanyId());

		objectDefinition.setExternalReferenceCode(() -> externalReferenceCode);

		if (serviceBuilderObjectDefinition != null) {
			return putObjectDefinition(
				serviceBuilderObjectDefinition.getObjectDefinitionId(),
				objectDefinition);
		}

		return postObjectDefinition(objectDefinition);
	}

	@Override
	protected void preparePatch(
		ObjectDefinition objectDefinition,
		ObjectDefinition existingObjectDefinition) {

		existingObjectDefinition.setObjectDefinitionSettings(
			objectDefinition::getObjectDefinitionSettings);
	}

	private void _addListTypeDefinition(ObjectDefinition objectDefinition)
		throws Exception {

		if (objectDefinition.getObjectFields() == null) {
			return;
		}

		for (ObjectField objectField : objectDefinition.getObjectFields()) {
			long listTypeDefinitionId = ObjectFieldUtil.addListTypeDefinition(
				contextUser.getCompanyId(), _listTypeDefinitionLocalService,
				_listTypeEntryLocalService, objectField,
				contextUser.getUserId());

			objectField.setListTypeDefinitionId(() -> listTypeDefinitionId);
		}
	}

	private void _addObjectDefinitionResources(
			Set<String> accountEntryRestrictedObjectRelationshipsNames,
			String defaultLanguageId, ObjectAction[] objectActions,
			long objectDefinitionId, ObjectLayout[] objectLayouts,
			ObjectRelationship[] objectRelationships,
			ObjectValidationRule[] objectValidationRules,
			ObjectView[] objectViews)
		throws Exception {

		if (objectActions != null) {
			ObjectActionResource.Builder builder =
				_objectActionResourceFactory.create();

			ObjectActionResource objectActionResource = builder.user(
				contextUser
			).build();

			for (ObjectAction objectAction : objectActions) {
				com.liferay.object.model.ObjectAction
					serviceBuilderObjectAction =
						_objectActionLocalService.fetchObjectAction(
							objectAction.getExternalReferenceCode(),
							objectDefinitionId);

				if (serviceBuilderObjectAction != null) {
					objectActionResource.putObjectAction(
						serviceBuilderObjectAction.getObjectActionId(),
						objectAction);

					continue;
				}

				Map<String, String> i18nMap = objectAction.getLabel();

				objectAction.setLabel(
					() -> LocalizedMapUtil.populateI18nMap(
						defaultLanguageId, i18nMap, objectAction.getName()));

				objectActionResource.postObjectDefinitionObjectAction(
					objectDefinitionId, objectAction);
			}
		}

		// Object relationship must be created before object layout

		if (objectRelationships != null) {
			ObjectRelationshipResource.Builder builder =
				_objectRelationshipResourceFactory.create();

			ObjectRelationshipResource objectRelationshipResource =
				builder.user(
					contextUser
				).build();

			Set<String> updateReverseObjectRelationshipNames = new HashSet<>();

			for (ObjectRelationship objectRelationship : objectRelationships) {
				com.liferay.object.model.ObjectRelationship
					serviceBuilderObjectRelationship =
						_objectRelationshipLocalService.
							fetchObjectRelationshipByExternalReferenceCode(
								objectRelationship.getExternalReferenceCode(),
								contextCompany.getCompanyId(),
								objectDefinitionId);

				if (serviceBuilderObjectRelationship == null) {
					serviceBuilderObjectRelationship =
						_objectRelationshipLocalService.
							fetchObjectRelationshipByObjectDefinitionId1(
								objectDefinitionId,
								objectRelationship.getName());
				}

				if (serviceBuilderObjectRelationship != null) {
					if (updateReverseObjectRelationshipNames.contains(
							serviceBuilderObjectRelationship.getName())) {

						serviceBuilderObjectRelationship =
							_objectRelationshipLocalService.
								fetchReverseObjectRelationship(
									serviceBuilderObjectRelationship, true);
					}

					objectRelationshipResource.putObjectRelationship(
						serviceBuilderObjectRelationship.
							getObjectRelationshipId(),
						objectRelationship);

					if (Objects.equals(
							serviceBuilderObjectRelationship.getType(),
							ObjectRelationshipConstants.TYPE_MANY_TO_MANY) &&
						serviceBuilderObjectRelationship.isSelf()) {

						updateReverseObjectRelationshipNames.add(
							serviceBuilderObjectRelationship.getName());
					}

					continue;
				}

				objectRelationship =
					objectRelationshipResource.
						postObjectDefinitionObjectRelationship(
							objectDefinitionId, objectRelationship);

				if (Objects.equals(
						objectRelationship.getTypeAsString(),
						ObjectRelationshipConstants.TYPE_MANY_TO_MANY) &&
					Objects.equals(
						objectRelationship.getObjectDefinitionId1(),
						objectRelationship.getObjectDefinitionId2())) {

					updateReverseObjectRelationshipNames.add(
						objectRelationship.getName());
				}

				if (accountEntryRestrictedObjectRelationshipsNames.contains(
						objectRelationship.getName())) {

					_objectDefinitionLocalService.enableAccountEntryRestricted(
						_objectRelationshipLocalService.getObjectRelationship(
							objectRelationship.getId()));
				}
			}
		}

		if (objectLayouts != null) {
			ObjectLayoutResource.Builder builder =
				_objectLayoutResourceFactory.create();

			ObjectLayoutResource objectLayoutResource = builder.user(
				contextUser
			).build();

			for (ObjectLayout objectLayout : objectLayouts) {
				objectLayoutResource.postObjectDefinitionObjectLayout(
					objectDefinitionId, objectLayout);
			}
		}

		if (objectValidationRules != null) {
			ObjectValidationRuleResource.Builder builder =
				_objectValidationRuleResourceFactory.create();

			ObjectValidationRuleResource objectValidationRuleResource =
				builder.user(
					contextUser
				).build();

			for (ObjectValidationRule objectValidationRule :
					objectValidationRules) {

				com.liferay.object.model.ObjectValidationRule
					serviceBuilderObjectValidationRule =
						_objectValidationRuleLocalService.
							fetchObjectValidationRule(
								objectValidationRule.getExternalReferenceCode(),
								objectDefinitionId);

				if (serviceBuilderObjectValidationRule != null) {
					objectValidationRuleResource.putObjectValidationRule(
						serviceBuilderObjectValidationRule.
							getObjectValidationRuleId(),
						objectValidationRule);

					continue;
				}

				objectValidationRuleResource.
					postObjectDefinitionObjectValidationRule(
						objectDefinitionId, objectValidationRule);
			}
		}

		if (objectViews != null) {
			ObjectViewResource.Builder builder =
				_objectViewResourceFactory.create();

			ObjectViewResource objectViewResource = builder.user(
				contextUser
			).build();

			for (ObjectView objectView : objectViews) {
				objectViewResource.postObjectDefinitionObjectView(
					objectDefinitionId, objectView);
			}
		}
	}

	private com.liferay.object.model.ObjectRelationship _addObjectRelationship(
			String defaultLanguageId, ObjectField objectField,
			com.liferay.object.model.ObjectDefinition
				serviceBuilderObjectDefinition2)
		throws Exception {

		String objectDefinitionExternalReferenceCode1 =
			objectField.getObjectDefinitionExternalReferenceCode1();

		if (Validator.isNull(objectDefinitionExternalReferenceCode1) ||
			StringUtil.equals(
				objectDefinitionExternalReferenceCode1,
				serviceBuilderObjectDefinition2.getExternalReferenceCode())) {

			return null;
		}

		com.liferay.object.model.ObjectDefinition
			serviceBuilderObjectDefinition1 =
				_objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						objectDefinitionExternalReferenceCode1,
						serviceBuilderObjectDefinition2.getCompanyId());

		if (serviceBuilderObjectDefinition1 == null) {
			serviceBuilderObjectDefinition1 =
				_objectDefinitionLocalService.addObjectDefinition(
					objectDefinitionExternalReferenceCode1,
					contextUser.getUserId(),
					serviceBuilderObjectDefinition2.getObjectFolderId(), true,
					ObjectDefinitionConstants.SCOPE_COMPANY, false);
		}

		com.liferay.object.model.ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				fetchObjectRelationshipByExternalReferenceCode(
					objectField.getObjectRelationshipExternalReferenceCode(),
					serviceBuilderObjectDefinition1.getObjectDefinitionId());

		if (objectRelationship != null) {
			return objectRelationship;
		}

		return _objectRelationshipLocalService.addObjectRelationship(
			objectField.getObjectRelationshipExternalReferenceCode(),
			contextUser.getUserId(),
			serviceBuilderObjectDefinition1.getObjectDefinitionId(),
			serviceBuilderObjectDefinition2.getObjectDefinitionId(),
			ObjectFieldUtil.toObjectField(
				defaultLanguageId, _listTypeDefinitionLocalService, objectField,
				_objectFieldLocalService, _objectFieldSettingLocalService,
				_objectFilterLocalService));
	}

	private Set<String> _getAccountEntryRestrictedObjectRelationshipsNames(
		com.liferay.object.model.ObjectDefinition objectDefinition1,
		List<ObjectRelationship> objectRelationships) {

		if ((objectDefinition1 == null) ||
			!objectDefinition1.isUnmodifiableSystemObject() ||
			(objectRelationships == null) ||
			!StringUtil.equals(
				objectDefinition1.getClassName(),
				AccountEntry.class.getName())) {

			return Collections.emptySet();
		}

		Set<String> accountEntryRestrictedObjectRelationshipsNames =
			new HashSet<>();

		for (ObjectRelationship objectRelationship : objectRelationships) {
			if (!StringUtil.equals(
					objectRelationship.getTypeAsString(),
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

				continue;
			}

			com.liferay.object.model.ObjectDefinition objectDefinition2 = null;

			long objectDefinitionId2 = GetterUtil.getLong(
				objectRelationship.getObjectDefinitionId2());

			if (objectDefinitionId2 > 0) {
				objectDefinition2 =
					_objectDefinitionLocalService.fetchObjectDefinition(
						objectRelationship.getObjectDefinitionId2());
			}

			if (objectDefinition2 == null) {
				objectDefinition2 =
					_objectDefinitionLocalService.
						fetchObjectDefinitionByExternalReferenceCode(
							objectRelationship.
								getObjectDefinitionExternalReferenceCode2(),
							contextCompany.getCompanyId());
			}

			if ((objectDefinition2 == null) ||
				!objectDefinition2.isAccountEntryRestricted()) {

				continue;
			}

			com.liferay.object.model.ObjectRelationship
				serviceBuilderObjectRelationship =
					_objectRelationshipLocalService.
						fetchObjectRelationshipByObjectDefinitionId1(
							objectDefinition1.getObjectDefinitionId(),
							objectRelationship.getName());

			if (serviceBuilderObjectRelationship == null) {
				continue;
			}

			if (serviceBuilderObjectRelationship.getObjectFieldId2() ==
					objectDefinition2.
						getAccountEntryRestrictedObjectFieldId()) {

				accountEntryRestrictedObjectRelationshipsNames.add(
					objectRelationship.getName());
			}
		}

		return accountEntryRestrictedObjectRelationshipsNames;
	}

	private long _getObjectFolderId(String objectFolderExternalReferenceCode)
		throws Exception {

		if (Validator.isNull(objectFolderExternalReferenceCode)) {
			return 0;
		}

		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				objectFolderExternalReferenceCode,
				contextCompany.getCompanyId());

		return objectFolder.getObjectFolderId();
	}

	private ObjectDefinition _toObjectDefinition(
			com.liferay.object.model.ObjectDefinition
				serviceBuilderObjectDefinition)
		throws Exception {

		if (serviceBuilderObjectDefinition == null) {
			return null;
		}

		String permissionName =
			com.liferay.object.model.ObjectDefinition.class.getName();

		return _objectDefinitionDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				false,
				HashMapBuilder.put(
					"delete",
					() -> {
						if (serviceBuilderObjectDefinition.isSystem()) {
							return null;
						}

						return addAction(
							ActionKeys.DELETE, "deleteObjectDefinition",
							permissionName,
							serviceBuilderObjectDefinition.
								getObjectDefinitionId());
					}
				).put(
					"exportBoundObjectDefinitions",
					() -> {
						if (!FeatureFlagManagerUtil.isEnabled(
								contextCompany.getCompanyId(), "LPD-34594") ||
							!serviceBuilderObjectDefinition.isRootNode()) {

							return null;
						}

						return addAction(
							ActionKeys.VIEW, "getObjectDefinitionsPage",
							permissionName,
							serviceBuilderObjectDefinition.
								getObjectDefinitionId());
					}
				).put(
					"exportObjectDefinition",
					() -> {
						if (serviceBuilderObjectDefinition.
								isRootDescendantNode() ||
							serviceBuilderObjectDefinition.isRootNode()) {

							return null;
						}

						return addAction(
							ActionKeys.VIEW, "getObjectDefinition",
							permissionName,
							serviceBuilderObjectDefinition.
								getObjectDefinitionId());
					}
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, "getObjectDefinition", permissionName,
						serviceBuilderObjectDefinition.getObjectDefinitionId())
				).put(
					"permissions",
					addAction(
						ActionKeys.PERMISSIONS, "patchObjectDefinition",
						permissionName,
						serviceBuilderObjectDefinition.getObjectDefinitionId())
				).put(
					"publish",
					() -> {
						if (serviceBuilderObjectDefinition.isApproved()) {
							return null;
						}

						return addAction(
							ActionKeys.UPDATE, "postObjectDefinitionPublish",
							permissionName,
							serviceBuilderObjectDefinition.
								getObjectDefinitionId());
					}
				).put(
					"update",
					addAction(
						ActionKeys.UPDATE, "putObjectDefinition",
						permissionName,
						serviceBuilderObjectDefinition.getObjectDefinitionId())
				).build(),
				null, null, contextAcceptLanguage.getPreferredLocale(), null,
				null),
			serviceBuilderObjectDefinition);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectDefinitionResourceImpl.class);

	private static final EntityModel _entityModel =
		new ObjectDefinitionEntityModel();
	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private Localization _localization;

	@Reference
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Reference
	private ObjectActionLocalService _objectActionLocalService;

	@Reference
	private ObjectActionResource.Factory _objectActionResourceFactory;

	@Reference
	private ObjectActionService _objectActionService;

	@Reference(target = DTOConverterConstants.OBJECT_DEFINITION_DTO_CONVERTER)
	private DTOConverter
		<com.liferay.object.model.ObjectDefinition, ObjectDefinition>
			_objectDefinitionDTOConverter;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Reference(target = DTOConverterConstants.OBJECT_FIELD_DTO_CONVERTER)
	private DTOConverter<com.liferay.object.model.ObjectField, ObjectField>
		_objectFieldDTOConverter;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Reference
	private ObjectFilterLocalService _objectFilterLocalService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference
	private ObjectLayoutLocalService _objectLayoutLocalService;

	@Reference
	private ObjectLayoutResource.Factory _objectLayoutResourceFactory;

	@Reference(target = DTOConverterConstants.OBJECT_RELATIONSHIP_DTO_CONVERTER)
	private DTOConverter
		<com.liferay.object.model.ObjectRelationship, ObjectRelationship>
			_objectRelationshipDTOConverter;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ObjectRelationshipResource.Factory
		_objectRelationshipResourceFactory;

	@Reference(
		target = DTOConverterConstants.OBJECT_VALIDATION_RULE_DTO_CONVERTER
	)
	private DTOConverter
		<com.liferay.object.model.ObjectValidationRule, ObjectValidationRule>
			_objectValidationRuleDTOConverter;

	@Reference
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

	@Reference
	private ObjectValidationRuleResource.Factory
		_objectValidationRuleResourceFactory;

	@Reference(target = DTOConverterConstants.OBJECT_VIEW_DTO_CONVERTER)
	private DTOConverter<com.liferay.object.model.ObjectView, ObjectView>
		_objectViewDTOConverter;

	@Reference
	private ObjectViewLocalService _objectViewLocalService;

	@Reference
	private ObjectViewResource.Factory _objectViewResourceFactory;

	@Reference
	private ObjectViewService _objectViewService;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

}