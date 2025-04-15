/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.action.executor.ObjectActionExecutor;
import com.liferay.object.action.executor.ObjectActionExecutorRegistry;
import com.liferay.object.action.trigger.ObjectActionTrigger;
import com.liferay.object.action.trigger.ObjectActionTriggerRegistry;
import com.liferay.object.admin.rest.dto.v1_0.util.ObjectActionUtil;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.web.internal.object.definitions.display.context.util.ObjectCodeEditorUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.script.management.configuration.helper.ScriptManagementConfigurationHelper;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Marco Leo
 */
public class ObjectDefinitionsActionsDisplayContext
	extends BaseObjectDefinitionsDisplayContext {

	public ObjectDefinitionsActionsDisplayContext(
		HttpServletRequest httpServletRequest, JSONFactory jsonFactory,
		NotificationTemplateLocalService notificationTemplateLocalService,
		ObjectActionExecutorRegistry objectActionExecutorRegistry,
		ObjectActionTriggerRegistry objectActionTriggerRegistry,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ModelResourcePermission<ObjectDefinition>
			objectDefinitionModelResourcePermission,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectFolderLocalService objectFolderLocalService,
		ScriptManagementConfigurationHelper
			scriptManagementConfigurationHelper) {

		super(
			httpServletRequest, objectDefinitionModelResourcePermission,
			objectFolderLocalService);

		_jsonFactory = jsonFactory;
		_notificationTemplateLocalService = notificationTemplateLocalService;
		_objectActionExecutorRegistry = objectActionExecutorRegistry;
		_objectActionTriggerRegistry = objectActionTriggerRegistry;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_scriptManagementConfigurationHelper =
			scriptManagementConfigurationHelper;
	}

	public String getEditObjectActionURL() throws Exception {
		return PortletURLBuilder.create(
			getPortletURL()
		).setMVCRenderCommandName(
			"/object_definitions/edit_object_action"
		).setParameter(
			"objectActionId", "{id}"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		boolean hasUpdatePermission = hasUpdateObjectDefinitionPermission();

		return Arrays.asList(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					getPortletURL()
				).setMVCRenderCommandName(
					"/object_definitions/edit_object_action"
				).setParameter(
					"objectActionId", "{id}"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				hasUpdatePermission ? "pencil" : "view",
				hasUpdatePermission ? "edit" : "view",
				LanguageUtil.get(
					objectRequestHelper.getRequest(),
					hasUpdatePermission ? "edit" : "view"),
				"get", null, "sidePanel"),
			new FDSActionDropdownItem(
				"/o/object-admin/v1.0/object-actions/{id}", "trash", "delete",
				LanguageUtil.get(objectRequestHelper.getRequest(), "delete"),
				"delete", "delete", "async"));
	}

	public ObjectAction getObjectAction() {
		HttpServletRequest httpServletRequest =
			objectRequestHelper.getRequest();

		return (ObjectAction)httpServletRequest.getAttribute(
			ObjectWebKeys.OBJECT_ACTION);
	}

	public List<Map<String, Object>> getObjectActionCodeEditorElements()
		throws PortalException {

		return ObjectCodeEditorUtil.getCodeEditorElements(
			true, true, false, objectRequestHelper.getLocale(),
			getObjectDefinitionId(),
			objectField -> !objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_AGGREGATION));
	}

	public ObjectActionExecutor getObjectActionExecutor() {
		ObjectAction objectAction = getObjectAction();

		return _objectActionExecutorRegistry.getObjectActionExecutor(
			objectAction.getCompanyId(),
			objectAction.getObjectActionExecutorKey());
	}

	public JSONArray getObjectActionExecutorsJSONArray() {
		JSONArray objectActionExecutorsJSONArray =
			_jsonFactory.createJSONArray();

		ObjectDefinition objectDefinition = getObjectDefinition();

		for (ObjectActionExecutor objectActionExecutor :
				_objectActionExecutorRegistry.getObjectActionExecutors(
					objectDefinition.getCompanyId(),
					objectDefinition.getName())) {

			if (StringUtil.equals(objectDefinition.getName(), "Organization") &&
				StringUtil.equals(
					objectActionExecutor.getKey(),
					ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY)) {

				continue;
			}

			objectActionExecutorsJSONArray.put(
				JSONUtil.put(
					"description",
					LanguageUtil.get(
						objectRequestHelper.getLocale(),
						"object-action-executor-help[" +
							objectActionExecutor.getKey() + "]")
				).put(
					"label",
					LanguageUtil.get(
						objectRequestHelper.getLocale(),
						"object-action-executor[" +
							objectActionExecutor.getKey() + "]")
				).put(
					"value", objectActionExecutor.getKey()
				));
		}

		return objectActionExecutorsJSONArray;
	}

	public JSONObject getObjectActionJSONObject(ObjectAction objectAction) {
		return JSONUtil.put(
			"active", objectAction.isActive()
		).put(
			"conditionExpression",
			() -> {
				String conditionExpression =
					objectAction.getConditionExpression();

				if (StringPool.BLANK.equals(conditionExpression)) {
					return null;
				}

				return conditionExpression;
			}
		).put(
			"description", objectAction.getDescription()
		).put(
			"errorMessage", objectAction.getErrorMessageMap()
		).put(
			"id", objectAction.getObjectActionId()
		).put(
			"label", objectAction.getLabelMap()
		).put(
			"name", objectAction.getName()
		).put(
			"objectActionExecutorKey", objectAction.getObjectActionExecutorKey()
		).put(
			"objectActionTriggerKey", objectAction.getObjectActionTriggerKey()
		).put(
			"objectDefinitionId", objectAction.getObjectDefinitionId()
		).put(
			"parameters",
			ObjectActionUtil.toParameters(
				_notificationTemplateLocalService,
				_objectDefinitionLocalService,
				objectAction.getParametersUnicodeProperties())
		).put(
			"system", objectAction.isSystem()
		);
	}

	public JSONArray getObjectActionTriggersJSONArray() {
		JSONArray objectActionTriggersJSONArray =
			_jsonFactory.createJSONArray();

		ObjectDefinition objectDefinition = getObjectDefinition();

		for (ObjectActionTrigger objectActionTrigger :
				_objectActionTriggerRegistry.getObjectActionTriggers(
					objectDefinition.getClassName())) {

			if ((StringUtil.equals(
					objectActionTrigger.getKey(),
					ObjectActionTriggerConstants.KEY_ON_AFTER_ROOT_UPDATE) &&
				 !objectDefinition.isRootNode()) ||
				(StringUtil.equals(
					objectActionTrigger.getKey(),
					ObjectActionTriggerConstants.KEY_STANDALONE) &&
				 objectDefinition.isUnmodifiableSystemObject())) {

				continue;
			}

			objectActionTriggersJSONArray.put(
				JSONUtil.put(
					"description",
					LanguageUtil.get(
						objectRequestHelper.getLocale(),
						"object-action-trigger-help[" +
							objectActionTrigger.getKey() + "]")
				).put(
					"label",
					LanguageUtil.get(
						objectRequestHelper.getLocale(),
						"object-action-trigger[" +
							objectActionTrigger.getKey() + "]")
				).put(
					"value", objectActionTrigger.getKey()
				));
		}

		return objectActionTriggersJSONArray;
	}

	@Override
	public ObjectDefinition getObjectDefinition() {
		HttpServletRequest httpServletRequest =
			objectRequestHelper.getRequest();

		return (ObjectDefinition)httpServletRequest.getAttribute(
			ObjectWebKeys.OBJECT_DEFINITION);
	}

	public String getObjectDefinitionsRelationshipsURL() {
		return ResourceURLBuilder.createResourceURL(
			objectRequestHelper.getLiferayPortletResponse()
		).setParameter(
			"objectDefinitionExternalReferenceCode",
			getObjectDefinitionExternalReferenceCode()
		).setResourceID(
			"/object_definitions/get_object_definitions_relationships"
		).buildString();
	}

	public List<ObjectField> getObjectFields() {
		return _objectFieldLocalService.getObjectFields(
			getObjectDefinitionId());
	}

	public String getScriptManagementConfigurationPortletURL()
		throws PortalException {

		return _scriptManagementConfigurationHelper.
			getScriptManagementConfigurationPortletURL();
	}

	public String getValidateExpressionURL() {
		return ResourceURLBuilder.createResourceURL(
			objectRequestHelper.getLiferayPortletResponse()
		).setResourceID(
			"/object_definitions/validate_expression"
		).buildString();
	}

	public boolean isAllowScriptContentToBeExecutedOrIncluded() {
		return _scriptManagementConfigurationHelper.
			isAllowScriptContentToBeExecutedOrIncluded();
	}

	@Override
	protected String getAPIURI() {
		return "/object-actions";
	}

	@Override
	protected UnsafeConsumer<DropdownItem, Exception>
		getCreationMenuDropdownItemUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.create(
					getPortletURL()
				).setMVCRenderCommandName(
					"/object_definitions/add_object_action"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(
					objectRequestHelper.getRequest(), "add-object-action"));
			dropdownItem.setTarget("sidePanel");
		};
	}

	private final JSONFactory _jsonFactory;
	private final NotificationTemplateLocalService
		_notificationTemplateLocalService;
	private final ObjectActionExecutorRegistry _objectActionExecutorRegistry;
	private final ObjectActionTriggerRegistry _objectActionTriggerRegistry;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ScriptManagementConfigurationHelper
		_scriptManagementConfigurationHelper;

}