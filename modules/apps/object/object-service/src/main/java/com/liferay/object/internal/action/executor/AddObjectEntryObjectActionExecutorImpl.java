/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.action.executor;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.object.action.executor.ObjectActionExecutor;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.internal.action.util.ObjectEntryVariablesUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
@Component(service = ObjectActionExecutor.class)
public class AddObjectEntryObjectActionExecutorImpl
	implements ObjectActionExecutor {

	@Override
	public void execute(
			long companyId, long objectActionId,
			UnicodeProperties parametersUnicodeProperties,
			JSONObject payloadJSONObject, long userId)
		throws Exception {

		ObjectDefinition sourceObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				payloadJSONObject.getLong("objectDefinitionId"));
		ObjectDefinition targetObjectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				GetterUtil.getLong(
					parametersUnicodeProperties.get("objectDefinitionId")));

		Map<String, Object> values = ObjectEntryVariablesUtil.getValues(
			_ddmExpressionFactory, targetObjectDefinition,
			parametersUnicodeProperties,
			ObjectEntryVariablesUtil.getVariables(
				_dtoConverterRegistry, sourceObjectDefinition,
				payloadJSONObject, _systemObjectDefinitionManagerRegistry));

		if (GetterUtil.getBoolean(
				parametersUnicodeProperties.get("relatedObjectEntries"))) {

			for (ObjectRelationship objectRelationship :
					_objectRelationshipLocalService.getObjectRelationships(
						sourceObjectDefinition.getObjectDefinitionId(),
						targetObjectDefinition.getObjectDefinitionId(),
						ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

				ObjectField objectField =
					_objectFieldLocalService.getObjectField(
						objectRelationship.getObjectFieldId2());

				values.put(
					objectField.getName(),
					payloadJSONObject.getLong("classPK"));
			}
		}

		long primaryKey = _execute(
			_getGroupId(
				companyId, payloadJSONObject, sourceObjectDefinition,
				targetObjectDefinition),
			targetObjectDefinition, _userLocalService.getUser(userId), values);

		if (!GetterUtil.getBoolean(
				parametersUnicodeProperties.get("relatedObjectEntries"))) {

			return;
		}

		for (ObjectRelationship objectRelationship :
				_objectRelationshipLocalService.getObjectRelationships(
					sourceObjectDefinition.getObjectDefinitionId(),
					targetObjectDefinition.getObjectDefinitionId(),
					ObjectRelationshipConstants.TYPE_MANY_TO_MANY)) {

			_objectRelationshipLocalService.
				addObjectRelationshipMappingTableValues(
					userId, objectRelationship.getObjectRelationshipId(),
					payloadJSONObject.getLong("classPK"), primaryKey,
					_getServiceContext(companyId, userId));
		}
	}

	@Override
	public String getKey() {
		return ObjectActionExecutorConstants.KEY_ADD_OBJECT_ENTRY;
	}

	private long _execute(
			long groupId, ObjectDefinition objectDefinition, User user,
			Map<String, Object> values)
		throws Exception {

		if (objectDefinition.isUnmodifiableSystemObject()) {
			SystemObjectDefinitionManager systemObjectDefinitionManager =
				_systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(
						objectDefinition.getName());

			return systemObjectDefinitionManager.addBaseModel(user, values);
		}

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				objectDefinition.getStorageType());

		ObjectEntry objectEntry = objectEntryManager.addObjectEntry(
			new DefaultDTOConverterContext(
				false, Collections.emptyMap(), _dtoConverterRegistry, null,
				user.getLocale(), null, user),
			objectDefinition,
			new ObjectEntry() {
				{
					setProperties(() -> values);
				}
			},
			String.valueOf(groupId));

		return objectEntry.getId();
	}

	private long _getGroupId(
			long companyId, JSONObject payloadJSONObject,
			ObjectDefinition sourceObjectDefinition,
			ObjectDefinition targetObjectDefinition)
		throws Exception {

		ObjectScopeProvider targetObjectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				targetObjectDefinition.getScope());

		if (!targetObjectScopeProvider.isGroupAware()) {
			return 0L;
		}

		ObjectScopeProvider sourceObjectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				sourceObjectDefinition.getScope());

		if (!sourceObjectScopeProvider.isGroupAware()) {
			Group companyGroup = _groupLocalService.fetchCompanyGroup(
				companyId);

			return companyGroup.getGroupId();
		}

		if (sourceObjectDefinition.isUnmodifiableSystemObject()) {
			return MapUtil.getLong(
				(Map<String, Object>)payloadJSONObject.get(
					"model" + sourceObjectDefinition.getName()),
				"groupId");
		}

		return MapUtil.getLong(
			(Map<String, Object>)payloadJSONObject.get("objectEntry"),
			"groupId");
	}

	private ServiceContext _getServiceContext(long companyId, long userId) {
		return new ServiceContext() {
			{
				setCompanyId(companyId);
				setUserId(userId);
			}
		};
	}

	@Reference
	private DDMExpressionFactory _ddmExpressionFactory;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Reference
	private UserLocalService _userLocalService;

}