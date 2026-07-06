/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.system.model.listener;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.object.action.engine.ObjectActionEngine;
import com.liferay.object.action.util.ObjectActionThreadLocal;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.entry.util.ObjectEntryPayloadUtil;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.extension.EntityExtensionThreadLocal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Brian Wing Shun Chan
 */
public class SystemObjectDefinitionManagerModelListener<T extends BaseModel<T>>
	extends BaseModelListener<T> {

	public SystemObjectDefinitionManagerModelListener(
		DDMExpressionFactory ddmExpressionFactory,
		DTOConverterRegistry dtoConverterRegistry, JSONFactory jsonFactory,
		Class<T> modelClass, ObjectActionEngine objectActionEngine,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectValidationRuleLocalService objectValidationRuleLocalService,
		SystemObjectDefinitionManager systemObjectDefinitionManager,
		UserLocalService userLocalService) {

		_ddmExpressionFactory = ddmExpressionFactory;
		_dtoConverterRegistry = dtoConverterRegistry;
		_jsonFactory = jsonFactory;
		_modelClass = modelClass;
		_objectActionEngine = objectActionEngine;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_objectValidationRuleLocalService = objectValidationRuleLocalService;
		_systemObjectDefinitionManager = systemObjectDefinitionManager;
		_userLocalService = userLocalService;
	}

	@Override
	public Class<?> getModelClass() {
		return _modelClass;
	}

	@Override
	public void onAfterCreate(T baseModel) throws ModelListenerException {
		ObjectActionThreadLocal.setSkipObjectActionExecution(true);

		boolean clearObjectEntryIdsMap =
			ObjectActionThreadLocal.isClearObjectEntryIdsMap();

		try {
			if (clearObjectEntryIdsMap) {
				ObjectActionThreadLocal.clearObjectEntryIdsMap();
			}

			ObjectActionThreadLocal.setClearObjectEntryIdsMap(false);

			_executeObjectActions(
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD, null,
				(T)baseModel.clone());
		}
		finally {
			ObjectActionThreadLocal.setClearObjectEntryIdsMap(
				clearObjectEntryIdsMap);
		}

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				ObjectActionThreadLocal.setSkipObjectActionExecution(false);

				return null;
			});
	}

	@Override
	public void onAfterRemove(T baseModel) throws ModelListenerException {
		ObjectActionThreadLocal.clearObjectEntryIdsMap();

		_executeObjectActions(
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE, baseModel,
			baseModel);
	}

	@Override
	public void onAfterUpdate(T originalBaseModel, T baseModel)
		throws ModelListenerException {

		if (ObjectActionThreadLocal.isSkipObjectActionExecution()) {
			return;
		}

		_executeObjectActions(
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE, originalBaseModel,
			(T)baseModel.clone());
	}

	@Override
	public void onBeforeCreate(T model) throws ModelListenerException {
		_validateSystemObject(null, model);
	}

	@Override
	public void onBeforeRemove(T baseModel) throws ModelListenerException {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
					_getCompanyId(baseModel), _modelClass.getName());

			if (objectDefinition == null) {
				return;
			}

			long groupId = 0;

			if (Objects.equals(
					ObjectDefinitionConstants.SCOPE_SITE,
					objectDefinition.getScope()) &&
				(baseModel instanceof GroupedModel)) {

				GroupedModel groupedModel = (GroupedModel)baseModel;

				groupId = groupedModel.getGroupId();
			}

			long primaryKey = _getPrimaryKey(baseModel);

			_objectEntryLocalService.deleteRelatedObjectEntries(
				groupId, objectDefinition.getObjectDefinitionId(), primaryKey);

			EntityExtensionThreadLocal.setExtendedProperties(
				HashMapBuilder.putAll(
					_objectEntryLocalService.
						getExtensionDynamicObjectDefinitionTableValues(
							objectDefinition, primaryKey)
				).putAll(
					EntityExtensionThreadLocal.getExtendedProperties()
				).build());

			_objectEntryLocalService.
				deleteExtensionDynamicObjectDefinitionTableValues(
					objectDefinition, primaryKey);
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	@Override
	public void onBeforeUpdate(T originalModel, T model)
		throws ModelListenerException {

		_validateSystemObject(originalModel, model);
	}

	private void _executeObjectActions(
			String objectActionTriggerKey, T originalBaseModel, T baseModel)
		throws ModelListenerException {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
					_getCompanyId(baseModel), _modelClass.getName());

			if (objectDefinition == null) {
				return;
			}

			long userId = _getUserId(baseModel);

			_objectActionEngine.executeObjectActions(
				_modelClass.getName(), _getCompanyId(baseModel),
				objectActionTriggerKey,
				() -> ObjectEntryPayloadUtil.getPayloadJSONObject(
					baseModel, _dtoConverterRegistry, _jsonFactory,
					objectActionTriggerKey, objectDefinition, originalBaseModel,
					_systemObjectDefinitionManager, userId),
				userId);
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	private long _getCompanyId(T baseModel) {
		Map<String, Function<Object, Object>> functions =
			(Map<String, Function<Object, Object>>)
				(Map<String, ?>)baseModel.getAttributeGetterFunctions();

		Function<Object, Object> function = functions.get("companyId");

		if (function == null) {
			throw new IllegalArgumentException(
				"Base model does not have a company ID column");
		}

		return (Long)function.apply(baseModel);
	}

	private long _getPrimaryKey(T baseModel) {
		Map<String, Function<Object, Object>> functions =
			(Map<String, Function<Object, Object>>)
				(Map<String, ?>)baseModel.getAttributeGetterFunctions();

		Column<?, Long> column =
			_systemObjectDefinitionManager.getPrimaryKeyColumn();

		Function<Object, Object> function = functions.get(column.getName());

		if (function == null) {
			throw new IllegalArgumentException(
				"Base model does not have column: " + column.getName());
		}

		return (Long)function.apply(baseModel);
	}

	private long _getUserId(T baseModel) {
		long userId = PrincipalThreadLocal.getUserId();

		if (userId != 0) {
			return userId;
		}

		Map<String, Function<Object, Object>> functions =
			(Map<String, Function<Object, Object>>)
				(Map<String, ?>)baseModel.getAttributeGetterFunctions();

		Function<Object, Object> function = functions.get("userId");

		if (function == null) {
			throw new IllegalArgumentException(
				"Base model does not have a user ID column");
		}

		return (Long)function.apply(baseModel);
	}

	private void _validateReadOnlyObjectFields(
			T originalModel, T model, ObjectDefinition objectDefinition)
		throws PortalException {

		if (EntityExtensionThreadLocal.getExtendedProperties() == null) {
			return;
		}

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId());
		Map<String, Object> extendedProperties = new HashMap<>(
			EntityExtensionThreadLocal.getExtendedProperties());

		if (originalModel == null) {
			ObjectFieldUtil.validateReadOnlyObjectFields(
				_ddmExpressionFactory, new HashMap<>(), objectFields,
				extendedProperties);

			ObjectEntryThreadLocal.setSkipReadOnlyObjectFieldsValidation(true);

			return;
		}

		ObjectFieldUtil.validateReadOnlyObjectFields(
			_ddmExpressionFactory,
			HashMapBuilder.putAll(
				originalModel.getModelAttributes()
			).putAll(
				_objectEntryLocalService.
					getExtensionDynamicObjectDefinitionTableValues(
						objectDefinition,
						GetterUtil.getLong(model.getPrimaryKeyObj()))
			).build(),
			objectFields, extendedProperties);

		ObjectEntryThreadLocal.setSkipReadOnlyObjectFieldsValidation(true);
	}

	private void _validateSystemObject(T originalModel, T model)
		throws ModelListenerException {

		try {
			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
					_getCompanyId(model), _modelClass.getName());

			if (objectDefinition == null) {
				return;
			}

			long userId = _getUserId(model);

			_validateReadOnlyObjectFields(
				originalModel, model, objectDefinition);

			int count =
				_objectValidationRuleLocalService.getObjectValidationRulesCount(
					objectDefinition.getObjectDefinitionId(), true);

			if (count > 0) {
				_objectValidationRuleLocalService.validate(
					model, objectDefinition.getObjectDefinitionId(),
					ObjectEntryPayloadUtil.getPayloadJSONObject(
						model, _dtoConverterRegistry, _jsonFactory, null,
						objectDefinition, originalModel,
						_systemObjectDefinitionManager, userId),
					userId);
			}
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	private final DDMExpressionFactory _ddmExpressionFactory;
	private final DTOConverterRegistry _dtoConverterRegistry;
	private final JSONFactory _jsonFactory;
	private final Class<?> _modelClass;
	private final ObjectActionEngine _objectActionEngine;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectValidationRuleLocalService
		_objectValidationRuleLocalService;
	private final SystemObjectDefinitionManager _systemObjectDefinitionManager;
	private final UserLocalService _userLocalService;

}