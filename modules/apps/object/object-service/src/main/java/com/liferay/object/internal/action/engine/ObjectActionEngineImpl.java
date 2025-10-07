/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.action.engine;

import com.liferay.dynamic.data.mapping.expression.CreateExpressionRequest;
import com.liferay.dynamic.data.mapping.expression.DDMExpression;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.object.action.engine.ObjectActionEngine;
import com.liferay.object.action.executor.ObjectActionExecutor;
import com.liferay.object.action.executor.ObjectActionExecutorRegistry;
import com.liferay.object.action.util.ObjectActionThreadLocal;
import com.liferay.object.constants.ObjectActionConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.dynamic.data.mapping.expression.ObjectEntryDDMExpressionFieldAccessor;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.exception.LockedObjectActionException;
import com.liferay.object.exception.ObjectActionExecutorKeyException;
import com.liferay.object.internal.action.util.ObjectEntryVariablesUtil;
import com.liferay.object.internal.dynamic.data.mapping.expression.ObjectEntryDDMExpressionParameterAccessor;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.scope.CompanyScoped;
import com.liferay.object.scope.ObjectDefinitionScoped;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
@Component(service = ObjectActionEngine.class)
public class ObjectActionEngineImpl implements ObjectActionEngine {

	@Override
	public void executeObjectAction(
			String objectActionName, String objectActionTriggerKey,
			long objectDefinitionId, JSONObject payloadJSONObject, long userId)
		throws Exception {

		ObjectAction objectAction = _objectActionLocalService.getObjectAction(
			objectDefinitionId, objectActionName, objectActionTriggerKey);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectDefinitionId);

		_updatePayloadJSONObject(
			objectDefinition, payloadJSONObject,
			_userLocalService.getUser(userId));

		try {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

			_executeObjectAction(
				objectAction, objectDefinition, payloadJSONObject, userId,
				ObjectEntryVariablesUtil.getVariables(
					_dtoConverterRegistry, objectDefinition, payloadJSONObject,
					_systemObjectDefinitionManagerRegistry));
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(false);
		}
	}

	@Override
	public <E extends Exception> void executeObjectActions(
			String className, long companyId, String objectActionTriggerKey,
			UnsafeSupplier<JSONObject, E> payloadJSONObjectUnsafeSupplier,
			long userId)
		throws E {

		if ((companyId == 0) || (userId == 0)) {
			return;
		}

		User user = _userLocalService.fetchUser(userId);

		if ((user == null) || (companyId != user.getCompanyId())) {
			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				user.getCompanyId(), className);

		if (objectDefinition == null) {
			return;
		}

		List<ObjectAction> objectActions =
			_objectActionLocalService.getObjectActions(
				objectDefinition.getObjectDefinitionId(),
				objectActionTriggerKey);

		if (objectActions.isEmpty()) {
			return;
		}

		String name = PrincipalThreadLocal.getName();
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		boolean skipReadOnlyObjectFieldsValidation =
			ObjectEntryThreadLocal.isSkipReadOnlyObjectFieldsValidation();

		try {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);
			ObjectEntryThreadLocal.setSkipReadOnlyObjectFieldsValidation(true);
			PrincipalThreadLocal.setName(userId);
			PermissionThreadLocal.setPermissionChecker(
				_permissionCheckerFactory.create(user));

			JSONObject payloadJSONObject =
				payloadJSONObjectUnsafeSupplier.get();

			_updatePayloadJSONObject(objectDefinition, payloadJSONObject, user);

			Map<String, Object> variables =
				ObjectEntryVariablesUtil.getVariables(
					_dtoConverterRegistry, objectDefinition, payloadJSONObject,
					_systemObjectDefinitionManagerRegistry);

			for (ObjectAction objectAction : objectActions) {
				try {
					_executeObjectAction(
						objectAction, objectDefinition, payloadJSONObject,
						userId, variables);
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			}
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(false);
			ObjectEntryThreadLocal.setSkipReadOnlyObjectFieldsValidation(
				skipReadOnlyObjectFieldsValidation);

			PrincipalThreadLocal.setName(name);

			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	private boolean _evaluateConditionExpression(
			String conditionExpression, Map<String, Object> variables)
		throws Exception {

		if (Validator.isNull(conditionExpression)) {
			return true;
		}

		DDMExpression<Boolean> ddmExpression =
			_ddmExpressionFactory.createExpression(
				CreateExpressionRequest.Builder.newBuilder(
					conditionExpression
				).withDDMExpressionFieldAccessor(
					new ObjectEntryDDMExpressionFieldAccessor(
						(Map<String, Object>)variables.get("baseModel"))
				).withDDMExpressionParameterAccessor(
					new ObjectEntryDDMExpressionParameterAccessor(
						(Map<String, Object>)variables.get("originalBaseModel"))
				).build());

		ddmExpression.setVariables(
			(Map<String, Object>)variables.get("baseModel"));

		return ddmExpression.evaluate();
	}

	private void _executeObjectAction(
			ObjectAction objectAction, ObjectDefinition objectDefinition,
			JSONObject payloadJSONObject, long userId,
			Map<String, Object> variables)
		throws Exception {

		Map<Long, Set<Long>> objectEntryIdsMap =
			ObjectActionThreadLocal.getObjectEntryIdsMap();

		if (!StringUtil.equals(
				objectAction.getObjectActionTriggerKey(),
				ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE) &&
			objectEntryIdsMap.containsKey(objectAction.getObjectActionId())) {

			return;
		}

		long objectEntryId = _getObjectEntryId(
			objectDefinition, payloadJSONObject);

		if (StringUtil.equals(
				objectAction.getObjectActionTriggerKey(),
				ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE)) {

			Set<Long> objectEntryIds = objectEntryIdsMap.get(
				objectAction.getObjectActionId());

			if (SetUtil.isNotEmpty(objectEntryIds) &&
				objectEntryIds.contains(objectEntryId)) {

				return;
			}
		}

		try {
			if (!_evaluateConditionExpression(
					objectAction.getConditionExpression(), variables)) {

				return;
			}

			ObjectActionThreadLocal.addObjectEntryId(
				objectAction.getObjectActionId(), objectEntryId);

			ObjectActionExecutor objectActionExecutor =
				_objectActionExecutorRegistry.getObjectActionExecutor(
					objectAction.getCompanyId(),
					objectAction.getObjectActionExecutorKey());

			if (objectActionExecutor instanceof CompanyScoped) {
				CompanyScoped objectActionExecutorCompanyScoped =
					(CompanyScoped)objectActionExecutor;

				if (!objectActionExecutorCompanyScoped.isAllowedCompany(
						objectDefinition.getCompanyId())) {

					throw new ObjectActionExecutorKeyException(
						StringBundler.concat(
							"The object action executor key ",
							objectActionExecutor.getKey(),
							" is not allowed for company ",
							objectDefinition.getCompanyId()));
				}
			}

			if (objectActionExecutor instanceof ObjectDefinitionScoped) {
				ObjectDefinitionScoped
					objectActionExecutorObjectDefinitionScoped =
						(ObjectDefinitionScoped)objectActionExecutor;

				if (!objectActionExecutorObjectDefinitionScoped.
						isAllowedObjectDefinition(objectDefinition.getName())) {

					throw new ObjectActionExecutorKeyException(
						StringBundler.concat(
							"The object action executor key ",
							objectActionExecutor.getKey(),
							" is not allowed for object definition ",
							objectDefinition.getName()));
				}
			}

			objectActionExecutor.execute(
				objectDefinition.getCompanyId(),
				objectAction.getObjectActionId(),
				objectAction.getParametersUnicodeProperties(),
				payloadJSONObject, userId);

			_updateObjectActionStatus(
				objectAction, ObjectActionConstants.STATUS_SUCCESS);
		}
		catch (Exception exception) {
			_updateObjectActionStatus(
				objectAction, ObjectActionConstants.STATUS_FAILED);

			throw exception;
		}
	}

	private long _getObjectEntryId(
		ObjectDefinition objectDefinition, JSONObject payloadJSONObject) {

		if (objectDefinition.isUnmodifiableSystemObject()) {
			Map<String, Object> map =
				(Map<String, Object>)payloadJSONObject.get(
					"model" + objectDefinition.getName());

			if (map == null) {
				return 0L;
			}

			return (long)map.get(objectDefinition.getPKObjectFieldName());
		}

		Map<String, Object> map = (Map<String, Object>)payloadJSONObject.get(
			"objectEntry");

		return GetterUtil.getLong(
			map.get("id"), (long)map.get("objectEntryId"));
	}

	private void _updateObjectActionStatus(
			ObjectAction objectAction, int status)
		throws PortalException {

		if (objectAction.getStatus() == status) {
			return;
		}

		try {
			_objectActionLocalService.updateStatus(
				objectAction.getObjectActionId(), status);
		}
		catch (PortalException portalException) {
			if (!(portalException instanceof LockedObjectActionException)) {
				throw portalException;
			}
		}
	}

	private void _updatePayloadJSONObject(
		ObjectDefinition objectDefinition, JSONObject payloadJSONObject,
		User user) {

		payloadJSONObject.put(
			"companyId", objectDefinition.getCompanyId()
		).put(
			"objectDefinitionId", objectDefinition.getObjectDefinitionId()
		).put(
			"status", objectDefinition.getStatus()
		).put(
			"userExternalReferenceCode", user.getExternalReferenceCode()
		).put(
			"userId", user.getUserId()
		).put(
			"userName", user.getFullName()
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectActionEngineImpl.class);

	@Reference
	private DDMExpressionFactory _ddmExpressionFactory;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ObjectActionExecutorRegistry _objectActionExecutorRegistry;

	@Reference
	private ObjectActionLocalService _objectActionLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Reference
	private UserLocalService _userLocalService;

}