/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.action.executor;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.object.action.executor.BaseObjectActionExecutor;
import com.liferay.object.action.executor.ObjectActionExecutor;
import com.liferay.object.action.util.ObjectActionThreadLocal;
import com.liferay.object.constants.ObjectActionConstants;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.internal.action.util.ObjectEntryVariablesUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(service = ObjectActionExecutor.class)
public class UpdateObjectEntryObjectActionExecutorImpl
	extends BaseObjectActionExecutor {

	@Override
	public String getKey() {
		return ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY;
	}

	@Override
	protected void doExecute(
			long companyId, long objectActionId,
			UnicodeProperties parametersUnicodeProperties,
			JSONObject payloadJSONObject, long userId)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				payloadJSONObject.getLong("objectDefinitionId"));

		ObjectActionThreadLocal.setSkipObjectActionExecution(false);

		_execute(
			objectActionId, objectDefinition,
			GetterUtil.getLong(payloadJSONObject.getLong("classPK")),
			_userLocalService.getUser(userId),
			_getValues(
				objectDefinition, parametersUnicodeProperties,
				ObjectEntryVariablesUtil.getVariables(
					_dtoConverterRegistry, objectDefinition, payloadJSONObject,
					_systemObjectDefinitionManagerRegistry)));
	}

	private void _execute(
			long objectActionId, ObjectDefinition objectDefinition,
			long primaryKey, User user, Map<String, Object> values)
		throws Exception {

		if (objectDefinition.isUnmodifiableSystemObject()) {
			SystemObjectDefinitionManager systemObjectDefinitionManager =
				_systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(
						objectDefinition.getName());

			systemObjectDefinitionManager.updateBaseModel(
				primaryKey, user, values);

			return;
		}

		boolean skipObjectEntryResourcePermission =
			ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission();

		try {
			ObjectActionThreadLocal.setClearObjectEntryIdsMap(false);
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);
			ObjectEntryThreadLocal.setSkipReadOnlyObjectFieldsValidation(true);

			DefaultObjectEntryManager defaultObjectEntryManager =
				DefaultObjectEntryManagerProvider.provide(
					_objectEntryManagerRegistry.getObjectEntryManager(
						objectDefinition.getStorageType()));

			defaultObjectEntryManager.partialUpdateObjectEntry(
				new DefaultDTOConverterContext(
					false, Collections.emptyMap(), _dtoConverterRegistry, null,
					user.getLocale(), null, user),
				objectDefinition, primaryKey,
				new ObjectEntry() {
					{
						setProperties(() -> values);
						setStatus(
							() -> new Status() {
								{
									setCode(
										() -> {
											com.liferay.object.model.ObjectEntry
												serviceBuilderObjectEntry =
													_objectEntryService.
														getObjectEntry(
															primaryKey);

											return serviceBuilderObjectEntry.
												getStatus();
										});
								}
							});
					}
				});
		}
		catch (Exception exception) {
			_objectActionLocalService.updateStatus(
				objectActionId, ObjectActionConstants.STATUS_FAILED);

			throw exception;
		}
		finally {
			ObjectActionThreadLocal.setClearObjectEntryIdsMap(true);
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(
				skipObjectEntryResourcePermission);
			ObjectEntryThreadLocal.setSkipReadOnlyObjectFieldsValidation(false);
		}
	}

	private Map<String, Object> _getValues(
			ObjectDefinition objectDefinition,
			UnicodeProperties parametersUnicodeProperties,
			Map<String, Object> variables)
		throws Exception {

		Map<String, Object> values = ObjectEntryVariablesUtil.getValues(
			_ddmExpressionFactory, parametersUnicodeProperties, variables);

		if (!objectDefinition.isUnmodifiableSystemObject()) {
			return values;
		}

		Map<String, Object> baseModel = (Map<String, Object>)variables.get(
			"baseModel");

		for (ObjectField objectField :
				_objectFieldLocalService.getObjectFields(
					objectDefinition.getObjectDefinitionId(),
					objectDefinition.isUnmodifiableSystemObject())) {

			if (_readOnlyObjectFieldNames.contains(objectField.getName()) ||
				values.containsKey(objectField.getName())) {

				continue;
			}

			values.put(
				objectField.getName(), baseModel.get(objectField.getName()));
		}

		return values;
	}

	@Reference
	private DDMExpressionFactory _ddmExpressionFactory;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ObjectActionLocalService _objectActionLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectEntryLocalService _objectEntryService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	private final Set<String> _readOnlyObjectFieldNames = SetUtil.fromArray(
		new String[] {"creator", "createDate", "id", "modifiedDate", "status"});

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Reference
	private UserLocalService _userLocalService;

}