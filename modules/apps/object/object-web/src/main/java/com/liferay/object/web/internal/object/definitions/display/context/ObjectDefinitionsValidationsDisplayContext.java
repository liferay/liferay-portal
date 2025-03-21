/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.learn.LearnMessageUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectValidationRule;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.validation.rule.ObjectValidationRuleEngineRegistry;
import com.liferay.object.web.internal.object.definitions.display.context.util.ObjectCodeEditorUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.security.script.management.configuration.helper.ScriptManagementConfigurationHelper;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Selton Guedes
 */
public class ObjectDefinitionsValidationsDisplayContext
	extends BaseObjectDefinitionsDisplayContext {

	public ObjectDefinitionsValidationsDisplayContext(
		HttpServletRequest httpServletRequest,
		ModelResourcePermission<ObjectDefinition>
			objectDefinitionModelResourcePermission,
		ObjectFolderLocalService objectFolderLocalService,
		ObjectValidationRuleEngineRegistry objectValidationRuleEngineRegistry,
		ScriptManagementConfigurationHelper
			scriptManagementConfigurationHelper) {

		super(
			httpServletRequest, objectDefinitionModelResourcePermission,
			objectFolderLocalService);

		_objectValidationRuleEngineRegistry =
			objectValidationRuleEngineRegistry;
		_scriptManagementConfigurationHelper =
			scriptManagementConfigurationHelper;
	}

	public String getEditObjectValidationURL() throws Exception {
		return PortletURLBuilder.create(
			getPortletURL()
		).setMVCRenderCommandName(
			"/object_definitions/edit_object_validation_rule"
		).setParameter(
			"objectValidationRuleId", "{id}"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		boolean hasUpdatePermission = hasUpdateObjectDefinitionPermission();

		return Arrays.asList(
			new FDSActionDropdownItem(
				getEditObjectValidationURL(),
				hasUpdatePermission ? "pencil" : "view",
				hasUpdatePermission ? "edit" : "view",
				LanguageUtil.get(
					objectRequestHelper.getRequest(),
					hasUpdatePermission ? "edit" : "view"),
				"get", null, "sidePanel"),
			new FDSActionDropdownItem(
				"/o/object-admin/v1.0/object-validation-rules/{id}", "trash",
				"delete",
				LanguageUtil.get(objectRequestHelper.getRequest(), "delete"),
				"delete", "delete", "async"));
	}

	public List<Map<String, String>> getObjectValidationRuleEngines() {
		ObjectDefinition objectDefinition = getObjectDefinition();

		return ListUtil.sort(
			TransformUtil.transform(
				_objectValidationRuleEngineRegistry.
					getObjectValidationRuleEngines(
						objectDefinition.getCompanyId(),
						objectDefinition.getName()),
				objectValidationRuleEngine -> HashMapBuilder.put(
					"key", objectValidationRuleEngine.getKey()
				).put(
					"label",
					objectValidationRuleEngine.getLabel(
						objectRequestHelper.getLocale())
				).build()),
			Comparator.comparing(item -> item.get("label")));
	}

	public Map<String, Object> getProps(
			ObjectValidationRule objectValidationRule)
		throws PortalException {

		ObjectDefinition objectDefinition = getObjectDefinition();

		return HashMapBuilder.<String, Object>put(
			"allowScriptContentToBeExecutedOrIncluded",
			isAllowScriptContentToBeExecutedOrIncluded()
		).put(
			"creationLanguageId", objectDefinition.getDefaultLanguageId()
		).put(
			"learnResources",
			LearnMessageUtil.getReactDataJSONObject("object-web")
		).put(
			"objectDefinitionExternalReferenceCode",
			objectDefinition.getExternalReferenceCode()
		).put(
			"objectDefinitionId", objectDefinition.getObjectDefinitionId()
		).put(
			"objectValidationRuleElements",
			_createObjectValidationRuleElements(
				objectValidationRule.getEngine())
		).put(
			"objectValidationRuleEngines", getObjectValidationRuleEngines()
		).put(
			"objectValidationRuleId",
			objectValidationRule.getObjectValidationRuleId()
		).put(
			"readOnly", !hasUpdateObjectDefinitionPermission()
		).build();
	}

	public String getScriptManagementConfigurationPortletURL()
		throws PortalException {

		return _scriptManagementConfigurationHelper.
			getScriptManagementConfigurationPortletURL();
	}

	public boolean isAllowScriptContentToBeExecutedOrIncluded() {
		return _scriptManagementConfigurationHelper.
			isAllowScriptContentToBeExecutedOrIncluded();
	}

	@Override
	protected String getAPIURI() {
		return "/object-validation-rules";
	}

	@Override
	protected UnsafeConsumer<DropdownItem, Exception>
		getCreationMenuDropdownItemUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref("addObjectValidation");
			dropdownItem.setLabel(
				LanguageUtil.get(
					objectRequestHelper.getRequest(), "add-object-validation"));
			dropdownItem.setTarget("event");
		};
	}

	private List<Map<String, Object>> _createObjectValidationRuleElements(
			String engine)
		throws PortalException {

		boolean includeDDMExpressionBuilderElements = false;

		if (engine.equals(ObjectValidationRuleConstants.ENGINE_TYPE_DDM)) {
			includeDDMExpressionBuilderElements = true;
		}

		return ObjectCodeEditorUtil.getCodeEditorElements(
			includeDDMExpressionBuilderElements, true, true,
			objectRequestHelper.getLocale(), getObjectDefinitionId(),
			objectField -> !objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_AGGREGATION));
	}

	private final ObjectValidationRuleEngineRegistry
		_objectValidationRuleEngineRegistry;
	private final ScriptManagementConfigurationHelper
		_scriptManagementConfigurationHelper;

}