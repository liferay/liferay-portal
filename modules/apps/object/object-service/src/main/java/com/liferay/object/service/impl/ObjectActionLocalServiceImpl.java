/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.dynamic.data.mapping.expression.CreateExpressionRequest;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.action.executor.ObjectActionExecutor;
import com.liferay.object.action.executor.ObjectActionExecutorRegistry;
import com.liferay.object.constants.ObjectActionConstants;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.definition.util.ObjectDefinitionUtil;
import com.liferay.object.exception.DuplicateObjectActionExternalReferenceCodeException;
import com.liferay.object.exception.LockedObjectActionException;
import com.liferay.object.exception.ObjectActionActiveException;
import com.liferay.object.exception.ObjectActionConditionExpressionException;
import com.liferay.object.exception.ObjectActionErrorMessageException;
import com.liferay.object.exception.ObjectActionExecutorKeyException;
import com.liferay.object.exception.ObjectActionNameException;
import com.liferay.object.exception.ObjectActionParametersException;
import com.liferay.object.exception.ObjectActionSystemException;
import com.liferay.object.exception.ObjectActionTriggerKeyException;
import com.liferay.object.internal.action.trigger.util.ObjectActionTriggerUtil;
import com.liferay.object.internal.security.permission.resource.util.ObjectDefinitionResourcePermissionUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.scope.CompanyScoped;
import com.liferay.object.scope.ObjectDefinitionScoped;
import com.liferay.object.scripting.exception.ObjectScriptingException;
import com.liferay.object.scripting.validator.ObjectScriptingValidator;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.base.ObjectActionLocalServiceBaseImpl;
import com.liferay.object.service.persistence.ObjectDefinitionPersistence;
import com.liferay.object.tree.ObjectDefinitionTreeFactory;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.lock.LockManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.security.script.management.configuration.helper.ScriptManagementConfigurationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectAction",
	service = AopService.class
)
public class ObjectActionLocalServiceImpl
	extends ObjectActionLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectAction addObjectAction(
			String externalReferenceCode, long userId, long objectDefinitionId,
			boolean active, String conditionExpression, String description,
			Map<Locale, String> errorMessageMap, Map<Locale, String> labelMap,
			String name, String objectActionExecutorKey,
			String objectActionTriggerKey,
			UnicodeProperties parametersUnicodeProperties, boolean system)
		throws PortalException {

		_validateInvokerBundle(
			"Only allowed bundles can add system object actions", system);

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		_validateExternalReferenceCode(
			externalReferenceCode, 0, objectDefinition.getCompanyId(),
			objectDefinitionId);

		_validateErrorMessage(errorMessageMap, objectActionTriggerKey);
		_validateName(0, objectDefinitionId, name);
		_validateObjectActionExecutorKey(
			objectActionExecutorKey, objectDefinition);
		_validateObjectActionTriggerKey(
			conditionExpression, objectActionTriggerKey, objectDefinition);

		User user = _userLocalService.getUser(userId);

		_validateParametersUnicodeProperties(
			user.getCompanyId(), userId, conditionExpression,
			objectActionExecutorKey, objectActionTriggerKey,
			parametersUnicodeProperties);

		ObjectAction objectAction = objectActionPersistence.create(
			counterLocalService.increment());

		if (Validator.isNull(externalReferenceCode)) {
			externalReferenceCode = objectAction.getUuid();
		}

		objectAction.setExternalReferenceCode(externalReferenceCode);
		objectAction.setCompanyId(user.getCompanyId());
		objectAction.setUserId(user.getUserId());
		objectAction.setUserName(user.getFullName());
		objectAction.setObjectDefinitionId(objectDefinitionId);
		objectAction.setActive(active);
		objectAction.setConditionExpression(conditionExpression);
		objectAction.setDescription(description);
		objectAction.setErrorMessageMap(
			errorMessageMap, LocaleUtil.getSiteDefault());
		objectAction.setLabelMap(
			_populateLabelMap(
				labelMap, name, objectDefinition.getDefaultLocale()),
			objectDefinition.getDefaultLocale());
		objectAction.setName(name);
		objectAction.setObjectActionExecutorKey(objectActionExecutorKey);
		objectAction.setObjectActionTriggerKey(objectActionTriggerKey);
		objectAction.setParameters(parametersUnicodeProperties.toString());
		objectAction.setSystem(system);
		objectAction.setStatus(ObjectActionConstants.STATUS_NEVER_RAN);

		objectAction = objectActionPersistence.update(objectAction);

		if (objectDefinition.isApproved() &&
			Objects.equals(
				objectAction.getObjectActionTriggerKey(),
				ObjectActionTriggerConstants.KEY_STANDALONE)) {

			try {
				if (objectDefinition.isRootDescendantNode()) {
					objectDefinition =
						_objectDefinitionPersistence.findByPrimaryKey(
							objectDefinition.getRootObjectDefinitionId());
				}

				ObjectDefinitionResourcePermissionUtil.populateResourceActions(
					objectActionLocalService, objectDefinition, null,
					_objectDefinitionPersistence,
					new ObjectDefinitionTreeFactory(
						_objectDefinitionPersistence,
						_objectRelationshipLocalService),
					_portletLocalService, _resourceActions, null);
			}
			catch (Exception exception) {
				ReflectionUtil.throwException(exception);
			}
		}

		return objectAction;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectAction addOrUpdateObjectAction(
			String externalReferenceCode, long objectActionId, long userId,
			long objectDefinitionId, boolean active, String conditionExpression,
			String description, Map<Locale, String> errorMessageMap,
			Map<Locale, String> labelMap, String name,
			String objectActionExecutorKey, String objectActionTriggerKey,
			UnicodeProperties parametersUnicodeProperties, boolean system)
		throws PortalException {

		ObjectAction existingObjectAction = null;

		if (objectActionId > 0) {
			existingObjectAction = objectActionPersistence.fetchByPrimaryKey(
				objectActionId);
		}

		if ((existingObjectAction == null) &&
			Validator.isNotNull(externalReferenceCode)) {

			ObjectDefinition objectDefinition =
				_objectDefinitionPersistence.findByPrimaryKey(
					objectDefinitionId);

			existingObjectAction = objectActionPersistence.fetchByERC_C_ODI(
				externalReferenceCode, objectDefinition.getCompanyId(),
				objectDefinitionId);
		}

		if (existingObjectAction != null) {
			return updateObjectAction(
				externalReferenceCode, existingObjectAction.getObjectActionId(),
				active, conditionExpression, description, errorMessageMap,
				labelMap, name, objectActionExecutorKey, objectActionTriggerKey,
				parametersUnicodeProperties);
		}

		return addObjectAction(
			externalReferenceCode, userId, objectDefinitionId, active,
			conditionExpression, description, errorMessageMap, labelMap, name,
			objectActionExecutorKey, objectActionTriggerKey,
			parametersUnicodeProperties, system);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public ObjectAction deleteObjectAction(long objectActionId)
		throws PortalException {

		ObjectAction objectAction = objectActionPersistence.findByPrimaryKey(
			objectActionId);

		return deleteObjectAction(objectAction);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public ObjectAction deleteObjectAction(ObjectAction objectAction)
		throws PortalException {

		_validateInvokerBundle(
			"Only allowed bundles can delete system object actions",
			objectAction.isSystem());

		objectAction = objectActionPersistence.remove(objectAction);

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.fetchByPrimaryKey(
				objectAction.getObjectDefinitionId());

		if (objectDefinition.isApproved() &&
			Objects.equals(
				objectAction.getObjectActionTriggerKey(),
				ObjectActionTriggerConstants.KEY_STANDALONE)) {

			_resourceActions.removeModelResource(
				objectDefinition.getClassName(), objectAction.getName());
		}

		return objectAction;
	}

	@Override
	public void deleteObjectActions(long objectDefinitionId)
		throws PortalException {

		for (ObjectAction objectAction :
				objectActionPersistence.findByObjectDefinitionId(
					objectDefinitionId)) {

			objectActionLocalService.deleteObjectAction(objectAction);
		}
	}

	@Override
	public ObjectAction fetchObjectAction(
		String externalReferenceCode, long objectDefinitionId) {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.fetchByPrimaryKey(objectDefinitionId);

		if (objectDefinition == null) {
			return null;
		}

		return objectActionPersistence.fetchByERC_C_ODI(
			externalReferenceCode, objectDefinition.getCompanyId(),
			objectDefinitionId);
	}

	@Override
	public ObjectAction getObjectAction(
			long objectDefinitionId, String name, String objectActionTriggerKey)
		throws PortalException {

		return objectActionPersistence.findByODI_A_N_OATK(
			objectDefinitionId, true, name, objectActionTriggerKey);
	}

	@Override
	public List<ObjectAction> getObjectActions(
		boolean active, String objectActionExecutorKey) {

		return objectActionPersistence.findByA_OAEK(
			active, objectActionExecutorKey);
	}

	@Override
	public List<ObjectAction> getObjectActions(long objectDefinitionId) {
		return objectActionPersistence.findByObjectDefinitionId(
			objectDefinitionId);
	}

	@Override
	public List<ObjectAction> getObjectActions(
		long objectDefinitionId, String objectActionTriggerKey) {

		return objectActionPersistence.findByO_A_OATK(
			objectDefinitionId, true, objectActionTriggerKey);
	}

	@Override
	public Map<Long, List<ObjectAction>> getObjectActionsMap(
		long companyId, boolean active, String objectActionTriggerKey) {

		Map<Long, List<ObjectAction>> objectActionsMap = new HashMap<>();

		for (ObjectAction objectAction :
				objectActionPersistence.findByC_A_OATK(
					companyId, active, objectActionTriggerKey)) {

			List<ObjectAction> objectActions = objectActionsMap.computeIfAbsent(
				objectAction.getObjectDefinitionId(),
				objectDefinitionId -> new ArrayList<>());

			objectActions.add(objectAction);
		}

		return objectActionsMap;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectAction updateObjectAction(
			String externalReferenceCode, long objectActionId, boolean active,
			String conditionExpression, String description,
			Map<Locale, String> errorMessageMap, Map<Locale, String> labelMap,
			String name, String objectActionExecutorKey,
			String objectActionTriggerKey,
			UnicodeProperties parametersUnicodeProperties)
		throws PortalException {

		ObjectAction objectAction = objectActionPersistence.findByPrimaryKey(
			objectActionId);

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectAction.getObjectDefinitionId());

		if (objectAction.isSystem() &&
			!ObjectDefinitionUtil.isInvokerBundleAllowed()) {

			objectAction.setLabelMap(
				_populateLabelMap(
					labelMap, objectAction.getName(),
					objectDefinition.getDefaultLocale()),
				objectDefinition.getDefaultLocale());

			return objectActionPersistence.update(objectAction);
		}

		_validateExternalReferenceCode(
			externalReferenceCode, objectAction.getObjectActionId(),
			objectAction.getCompanyId(), objectAction.getObjectDefinitionId());
		_validateActive(active, objectAction, objectDefinition);
		_validateErrorMessage(errorMessageMap, objectActionTriggerKey);
		_validateObjectActionExecutorKey(
			objectActionExecutorKey, objectDefinition);
		_validateParametersUnicodeProperties(
			objectAction.getCompanyId(), objectAction.getUserId(),
			conditionExpression, objectActionExecutorKey,
			objectActionTriggerKey, parametersUnicodeProperties);

		if (Validator.isNotNull(externalReferenceCode)) {
			objectAction.setExternalReferenceCode(externalReferenceCode);
		}

		objectAction.setActive(active);
		objectAction.setConditionExpression(conditionExpression);
		objectAction.setDescription(description);
		objectAction.setErrorMessageMap(
			errorMessageMap, LocaleUtil.getSiteDefault());

		if (objectDefinition.isApproved()) {
			objectAction.setLabelMap(
				_populateLabelMap(
					labelMap, objectAction.getName(),
					objectDefinition.getDefaultLocale()),
				objectDefinition.getDefaultLocale());
		}

		objectAction.setObjectActionExecutorKey(objectActionExecutorKey);
		objectAction.setParameters(parametersUnicodeProperties.toString());
		objectAction.setStatus(ObjectActionConstants.STATUS_NEVER_RAN);

		if (objectDefinition.isApproved()) {
			return objectActionPersistence.update(objectAction);
		}

		_validateName(
			objectActionId, objectDefinition.getObjectDefinitionId(), name);
		_validateObjectActionTriggerKey(
			conditionExpression, objectActionTriggerKey, objectDefinition);

		objectAction.setLabelMap(
			_populateLabelMap(
				labelMap, name, objectDefinition.getDefaultLocale()),
			objectDefinition.getDefaultLocale());
		objectAction.setName(name);
		objectAction.setObjectActionTriggerKey(objectActionTriggerKey);

		return objectActionPersistence.update(objectAction);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public synchronized ObjectAction updateStatus(
			long objectActionId, int status)
		throws PortalException {

		boolean locked = LockManagerUtil.isLocked(
			ObjectAction.class.getName(), objectActionId);

		if (locked) {
			throw new LockedObjectActionException(
				String.format(
					"Unable to update the status of object action %d because " +
						"it is being updated by another thread",
					objectActionId));
		}

		try {
			LockManagerUtil.lock(
				ObjectAction.class.getName(), String.valueOf(objectActionId),
				PortalUUIDUtil.generate());

			ObjectAction objectAction =
				objectActionPersistence.findByPrimaryKey(objectActionId);

			objectAction.setStatus(status);

			return objectActionPersistence.update(objectAction);
		}
		finally {
			LockManagerUtil.unlock(
				ObjectAction.class.getName(), objectActionId);
		}
	}

	private boolean _isUsePreferredLanguageForGuestsSupported(
			String objectActionExecutorKey, String objectActionTriggerKey,
			UnicodeProperties parametersUnicodeProperties)
		throws PortalException {

		if (!Objects.equals(
				objectActionExecutorKey,
				ObjectActionExecutorConstants.KEY_NOTIFICATION)) {

			return false;
		}

		NotificationTemplate notificationTemplate =
			_notificationTemplateLocalService.getNotificationTemplate(
				GetterUtil.getLong(
					parametersUnicodeProperties.get("notificationTemplateId")));

		if (Objects.equals(
				notificationTemplate.getType(),
				NotificationConstants.TYPE_EMAIL) &&
			(Objects.equals(
				objectActionTriggerKey,
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD) ||
			 Objects.equals(
				 objectActionTriggerKey,
				 ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE))) {

			return true;
		}

		return false;
	}

	private Map<Locale, String> _populateLabelMap(
		Map<Locale, String> labelMap, String name, Locale locale) {

		if ((labelMap == null) || labelMap.isEmpty()) {
			return HashMapBuilder.put(
				locale, name
			).build();
		}

		if (Validator.isNotNull(labelMap.get(locale))) {
			return labelMap;
		}

		if (labelMap.size() == 1) {
			for (Map.Entry<Locale, String> entry : labelMap.entrySet()) {
				labelMap.put(locale, entry.getValue());
			}

			return labelMap;
		}

		labelMap.put(locale, name);

		return labelMap;
	}

	private void _validateActive(
			boolean active, ObjectAction objectAction,
			ObjectDefinition objectDefinition)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-34594") ||
			objectDefinition.isRootNode()) {

			return;
		}

		if (active &&
			StringUtil.equals(
				objectAction.getObjectActionTriggerKey(),
				ObjectActionTriggerConstants.KEY_ON_AFTER_ROOT_UPDATE)) {

			throw new ObjectActionActiveException(
				"Object action trigger is " +
					ObjectActionTriggerConstants.KEY_ON_AFTER_ROOT_UPDATE +
						" but object definition is not a root node");
		}
	}

	private void _validateErrorMessage(
			Map<Locale, String> errorMessageMap, String objectActionTriggerKey)
		throws PortalException {

		Locale locale = LocaleUtil.getSiteDefault();

		if (Objects.equals(
				objectActionTriggerKey,
				ObjectActionTriggerConstants.KEY_STANDALONE) &&
			((errorMessageMap == null) ||
			 Validator.isNull(errorMessageMap.get(locale)))) {

			throw new ObjectActionErrorMessageException(
				"Error message is null for locale " + locale.getDisplayName());
		}
	}

	private void _validateExternalReferenceCode(
			String externalReferenceCode, long objectActionId, long companyId,
			long objectDefinitionId)
		throws PortalException {

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		ObjectAction objectAction = objectActionPersistence.fetchByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);

		if ((objectAction != null) &&
			(objectAction.getObjectActionId() != objectActionId)) {

			throw new DuplicateObjectActionExternalReferenceCodeException();
		}
	}

	private void _validateInvokerBundle(String message, boolean system)
		throws PortalException {

		if (!system || ObjectDefinitionUtil.isInvokerBundleAllowed()) {
			return;
		}

		throw new ObjectActionSystemException(message);
	}

	private void _validateName(
			long objectActionId, long objectDefinitionId, String name)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new ObjectActionNameException.MustNotBeNull();
		}

		char[] nameCharArray = name.toCharArray();

		for (char c : nameCharArray) {
			if (!Validator.isChar(c) && !Validator.isDigit(c)) {
				throw new ObjectActionNameException.
					MustOnlyContainLettersAndDigits();
			}
		}

		if (nameCharArray.length > 41) {
			throw new ObjectActionNameException.MustBeLessThan41Characters();
		}

		ObjectAction objectAction = objectActionPersistence.fetchByODI_N(
			objectDefinitionId, name);

		if ((objectAction != null) &&
			(objectAction.getObjectActionId() != objectActionId)) {

			throw new ObjectActionNameException.MustNotBeDuplicate(name);
		}
	}

	private void _validateObjectActionExecutorKey(
			String objectActionExecutorKey, ObjectDefinition objectDefinition)
		throws PortalException {

		if (!_objectActionExecutorRegistry.hasObjectActionExecutor(
				objectActionExecutorKey)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"No object action executor is registered with " +
						objectActionExecutorKey);
			}

			return;
		}

		if (Objects.equals(
				ObjectActionExecutorConstants.KEY_GROOVY,
				objectActionExecutorKey) &&
			!_scriptManagementConfigurationHelper.
				isAllowScriptContentToBeExecutedOrIncluded()) {

			throw new ObjectActionExecutorKeyException(
				"Groovy script based object actions are not allowed");
		}

		ObjectActionExecutor objectActionExecutor =
			_objectActionExecutorRegistry.getObjectActionExecutor(
				objectDefinition.getCompanyId(), objectActionExecutorKey);

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
			ObjectDefinitionScoped objectActionExecutorObjectDefinitionScoped =
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
	}

	private void _validateObjectActionTriggerKey(
			String conditionExpression, String objectActionTriggerKey,
			ObjectDefinition objectDefinition)
		throws PortalException {

		if (FeatureFlagManagerUtil.isEnabled(
				objectDefinition.getCompanyId(), "LPD-34594") &&
			StringUtil.equals(
				objectActionTriggerKey,
				ObjectActionTriggerConstants.KEY_ON_AFTER_ROOT_UPDATE) &&
			!objectDefinition.isRootNode()) {

			throw new ObjectActionTriggerKeyException(
				StringBundler.concat(
					"The object action trigger key ",
					ObjectActionTriggerConstants.KEY_ON_AFTER_ROOT_UPDATE,
					" can only be used by a root object definition"));
		}

		if (StringUtil.equals(
				objectActionTriggerKey,
				ObjectActionTriggerConstants.KEY_STANDALONE)) {

			if (objectDefinition.isUnmodifiableSystemObject()) {
				throw new ObjectActionTriggerKeyException(
					StringBundler.concat(
						"The object action trigger key ",
						ObjectActionTriggerConstants.KEY_STANDALONE,
						" cannot be used by a system object definition"));
			}
			else if (Validator.isNotNull(conditionExpression)) {
				throw new ObjectActionTriggerKeyException(
					StringBundler.concat(
						"The object action trigger key ",
						ObjectActionTriggerConstants.KEY_STANDALONE,
						" cannot have a condition expression"));
			}
		}

		if (!ListUtil.exists(
				ObjectActionTriggerUtil.getDefaultObjectActionTriggers(),
				objectActionTrigger -> StringUtil.equals(
					objectActionTrigger.getKey(), objectActionTriggerKey))) {

			if (_messageBus.getDestination(objectActionTriggerKey) == null) {
				throw new ObjectActionTriggerKeyException();
			}

			if (StringUtil.equals(
					objectActionTriggerKey,
					DestinationNames.COMMERCE_ORDER_STATUS) ||
				StringUtil.equals(
					objectActionTriggerKey,
					DestinationNames.COMMERCE_PAYMENT_STATUS) ||
				StringUtil.equals(
					objectActionTriggerKey,
					DestinationNames.COMMERCE_SHIPMENT_STATUS)) {

				return;
			}

			if (Validator.isNotNull(conditionExpression)) {
				throw new ObjectActionConditionExpressionException();
			}
		}
	}

	private void _validateParametersUnicodeProperties(
			long companyId, long userId, String conditionExpression,
			String objectActionExecutorKey, String objectActionTriggerKey,
			UnicodeProperties parametersUnicodeProperties)
		throws PortalException {

		Map<String, Object> errorMessageKeys = new HashMap<>();

		if (Validator.isNotNull(conditionExpression)) {
			try {
				_ddmExpressionFactory.createExpression(
					CreateExpressionRequest.Builder.newBuilder(
						conditionExpression
					).build());
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				errorMessageKeys.put("conditionExpression", "syntax-error");
			}
		}

		if (Objects.equals(
				objectActionExecutorKey,
				ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY) &&
			Objects.equals(
				objectActionTriggerKey,
				ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE)) {

			throw new ObjectActionTriggerKeyException(
				StringBundler.concat(
					"The object action executor key ",
					ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY,
					" cannot be associated with the object action trigger key",
					ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE));
		}

		if (Objects.equals(
				objectActionExecutorKey,
				ObjectActionExecutorConstants.KEY_ADD_OBJECT_ENTRY) ||
			Objects.equals(
				objectActionExecutorKey,
				ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY)) {

			ObjectDefinition objectDefinition = null;

			String objectDefinitionExternalReferenceCode = GetterUtil.getString(
				parametersUnicodeProperties.remove(
					"objectDefinitionExternalReferenceCode"));

			if (Validator.isNotNull(objectDefinitionExternalReferenceCode)) {
				objectDefinition = _objectDefinitionPersistence.fetchByERC_C(
					objectDefinitionExternalReferenceCode, companyId);

				if (objectDefinition == null) {
					ObjectFolder defaultObjectFolder =
						_objectFolderLocalService.getOrAddDefaultObjectFolder(
							companyId);

					objectDefinition =
						ObjectDefinitionLocalServiceUtil.addObjectDefinition(
							objectDefinitionExternalReferenceCode, userId,
							defaultObjectFolder.getObjectFolderId(), true,
							ObjectDefinitionConstants.SCOPE_COMPANY, false);
				}

				parametersUnicodeProperties.put(
					"objectDefinitionId",
					String.valueOf(objectDefinition.getObjectDefinitionId()));
			}
			else {
				objectDefinition =
					_objectDefinitionPersistence.fetchByPrimaryKey(
						GetterUtil.getLong(
							parametersUnicodeProperties.get(
								"objectDefinitionId")));
			}

			if (objectDefinition == null) {
				errorMessageKeys.put("objectDefinitionId", "invalid");
			}
			else {
				_validatePredefinedValues(
					errorMessageKeys, objectActionExecutorKey,
					objectDefinition.getObjectDefinitionId(),
					_jsonFactory.createJSONArray(
						parametersUnicodeProperties.get("predefinedValues")));
			}
		}
		else if (Objects.equals(
					objectActionExecutorKey,
					ObjectActionExecutorConstants.KEY_GROOVY)) {

			String script = parametersUnicodeProperties.get("script");

			if (Validator.isNotNull(script)) {
				try {
					_objectScriptingValidator.validate("groovy", script);
				}
				catch (ObjectScriptingException objectScriptingException) {
					errorMessageKeys.put(
						"script", objectScriptingException.getMessageKey());
				}
			}
		}
		else if (Objects.equals(
					objectActionExecutorKey,
					ObjectActionExecutorConstants.KEY_NOTIFICATION)) {

			String notificationTemplateExternalReferenceCode =
				GetterUtil.getString(
					parametersUnicodeProperties.remove(
						"notificationTemplateExternalReferenceCode"));

			if (Validator.isNotNull(
					notificationTemplateExternalReferenceCode)) {

				NotificationTemplate existingNotificationTemplate =
					_notificationTemplateLocalService.
						fetchNotificationTemplateByExternalReferenceCode(
							notificationTemplateExternalReferenceCode,
							companyId);

				String type = GetterUtil.getString(
					parametersUnicodeProperties.remove("type"));

				if ((existingNotificationTemplate == null) && (type != null)) {
					existingNotificationTemplate =
						_notificationTemplateLocalService.
							addNotificationTemplate(
								notificationTemplateExternalReferenceCode,
								userId, type);
				}

				if (existingNotificationTemplate != null) {
					parametersUnicodeProperties.put(
						"notificationTemplateId",
						String.valueOf(
							existingNotificationTemplate.
								getNotificationTemplateId()));
				}
			}

			if (Objects.isNull(
					parametersUnicodeProperties.get(
						"usePreferredLanguageForGuests")) &&
				_isUsePreferredLanguageForGuestsSupported(
					objectActionExecutorKey, objectActionTriggerKey,
					parametersUnicodeProperties)) {

				parametersUnicodeProperties.put(
					"usePreferredLanguageForGuests", "true");
			}
		}
		else if (Objects.equals(
					objectActionExecutorKey,
					ObjectActionExecutorConstants.KEY_WEBHOOK)) {

			if (Validator.isNull(parametersUnicodeProperties.get("url"))) {
				errorMessageKeys.put("url", "required");
			}
		}

		if (Objects.nonNull(
				parametersUnicodeProperties.get(
					"usePreferredLanguageForGuests")) &&
			!_isUsePreferredLanguageForGuestsSupported(
				objectActionExecutorKey, objectActionTriggerKey,
				parametersUnicodeProperties)) {

			throw new ObjectActionParametersException(
				"The parameter \"usePreferredLanguageForGuests\" is invalid " +
					"for this object action");
		}

		if (MapUtil.isNotEmpty(errorMessageKeys)) {
			throw new ObjectActionParametersException(errorMessageKeys);
		}
	}

	private void _validatePredefinedValues(
		Map<String, Object> errorMessageKeys, String objectActionExecutorKey,
		long objectDefinitionId, JSONArray predefinedValuesJSONArray) {

		Map<String, String> predefinedValuesErrorMessageKeys = new HashMap<>();

		Map<String, String> predefinedValuesMap = new HashMap<>();

		for (int i = 0; i < predefinedValuesJSONArray.length(); i++) {
			JSONObject predefinedValueJSONObject =
				predefinedValuesJSONArray.getJSONObject(i);

			String name = predefinedValueJSONObject.getString("name");

			ObjectField objectField = _objectFieldLocalService.fetchObjectField(
				objectDefinitionId, name);

			if (objectField == null) {
				continue;
			}

			if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_AUTO_INCREMENT)) {

				predefinedValuesErrorMessageKeys.put(name, "invalid");

				continue;
			}

			if (objectField.isLocalized()) {
				predefinedValuesErrorMessageKeys.put(
					objectField.getName(),
					"localized-object-fields-must-not-be-used-in-object-" +
						"actions");
			}

			String value = predefinedValueJSONObject.getString("value");

			predefinedValuesMap.put(name, value);

			if (Objects.equals(
					objectActionExecutorKey,
					ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY) &&
				objectField.isRequired() && Validator.isNull(value)) {

				predefinedValuesErrorMessageKeys.put(
					objectField.getName(), "required");
			}

			if (Validator.isNull(value) ||
				predefinedValueJSONObject.getBoolean("inputAsValue")) {

				continue;
			}

			try {
				_ddmExpressionFactory.createExpression(
					CreateExpressionRequest.Builder.newBuilder(
						value
					).build());
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				predefinedValuesErrorMessageKeys.put(name, "syntax-error");
			}
		}

		if (Objects.equals(
				objectActionExecutorKey,
				ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY)) {

			return;
		}

		for (ObjectField objectField :
				_objectFieldLocalService.getObjectFields(objectDefinitionId)) {

			if (!objectField.isRequired() ||
				Validator.isNotNull(
					predefinedValuesMap.get(objectField.getName())) ||
				StringUtil.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

				continue;
			}

			predefinedValuesErrorMessageKeys.put(
				objectField.getName(), "required");
		}

		if (MapUtil.isNotEmpty(predefinedValuesErrorMessageKeys)) {
			errorMessageKeys.put(
				"predefinedValues", predefinedValuesErrorMessageKeys);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectActionLocalServiceImpl.class);

	@Reference
	private DDMExpressionFactory _ddmExpressionFactory;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private MessageBus _messageBus;

	@Reference
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Reference
	private ObjectActionExecutorRegistry _objectActionExecutorRegistry;

	@Reference
	private ObjectDefinitionPersistence _objectDefinitionPersistence;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ObjectScriptingValidator _objectScriptingValidator;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private ResourceActions _resourceActions;

	@Reference
	private ScriptManagementConfigurationHelper
		_scriptManagementConfigurationHelper;

	@Reference
	private UserLocalService _userLocalService;

}