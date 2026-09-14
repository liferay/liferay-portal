/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.upgrade;

import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.definition.util.ObjectDefinitionThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectValidationRule;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jaime León
 */
public class MCPPromptUpgradeProcess extends UpgradeProcess {

	public MCPPromptUpgradeProcess(
		CompanyLocalService companyLocalService,
		ListTypeDefinitionLocalService listTypeDefinitionLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectFieldSettingLocalService objectFieldSettingLocalService,
		ObjectValidationRuleLocalService objectValidationRuleLocalService) {

		_companyLocalService = companyLocalService;
		_listTypeDefinitionLocalService = listTypeDefinitionLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_objectFieldSettingLocalService = objectFieldSettingLocalService;
		_objectValidationRuleLocalService = objectValidationRuleLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(this::_upgradeCompany);
	}

	private ListTypeDefinition _addListTypeDefinition(
			long companyId, long userId)
		throws PortalException {

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					"L_MCP_STATUS", companyId);

		if (listTypeDefinition != null) {
			return listTypeDefinition;
		}

		return _listTypeDefinitionLocalService.addListTypeDefinition(
			"L_MCP_STATUS", userId,
			Collections.singletonMap(LocaleUtil.US, "MCP Status"), true,
			Arrays.asList(
				_createListTypeEntry("ACTIVE", "active", "Active"),
				_createListTypeEntry("INACTIVE", "inactive", "Inactive")),
			new ServiceContext());
	}

	private void _addObjectFields(
			ListTypeDefinition listTypeDefinition,
			ObjectDefinition objectDefinition)
		throws PortalException {

		ObjectField identifierObjectField =
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "identifier");

		if (identifierObjectField == null) {
			_objectFieldLocalService.addSystemObjectField(
				null, objectDefinition.getUserId(), 0,
				objectDefinition.getObjectDefinitionId(),
				ObjectFieldConstants.BUSINESS_TYPE_TEXT, null, null,
				ObjectFieldConstants.DB_TYPE_STRING, null, true, false, "en_US",
				Collections.singletonMap(LocaleUtil.US, "Identifier"), false,
				"identifier", ObjectFieldConstants.READ_ONLY_FALSE, null, true,
				false,
				Collections.singletonList(
					_createObjectFieldSetting(
						ObjectFieldSettingConstants.NAME_UNIQUE_VALUES,
						"true")));
		}

		ObjectField promptStatusObjectField =
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "promptStatus");

		if (promptStatusObjectField == null) {
			_objectFieldLocalService.addSystemObjectField(
				null, objectDefinition.getUserId(),
				listTypeDefinition.getListTypeDefinitionId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectFieldConstants.BUSINESS_TYPE_PICKLIST, null, null,
				ObjectFieldConstants.DB_TYPE_STRING, null, true, false, null,
				Collections.singletonMap(LocaleUtil.US, "Status"), false,
				"promptStatus", ObjectFieldConstants.READ_ONLY_FALSE, null,
				true, false,
				Arrays.asList(
					_createObjectFieldSetting(
						ObjectFieldSettingConstants.NAME_DEFAULT_VALUE,
						"inactive"),
					_createObjectFieldSetting(
						ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE,
						ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE)));
		}
	}

	private void _addObjectValidationRule(ObjectDefinition objectDefinition)
		throws PortalException {

		ObjectValidationRule objectValidationRule =
			_objectValidationRuleLocalService.fetchObjectValidationRule(
				"L_MCP_SERVER_PROMPT_VALID_IDENTIFIER",
				objectDefinition.getObjectDefinitionId());

		if (objectValidationRule != null) {
			return;
		}

		_objectValidationRuleLocalService.addObjectValidationRule(
			"L_MCP_SERVER_PROMPT_VALID_IDENTIFIER",
			objectDefinition.getUserId(),
			objectDefinition.getObjectDefinitionId(), true,
			ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
			Collections.singletonMap(
				LocaleUtil.US,
				"Please enter a valid identifier (lowercase letters and " +
					"numbers separated by single hyphens)."),
			Collections.singletonMap(LocaleUtil.US, "Valid Identifier"),
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
			"match(identifier, '^[a-z0-9]+(-[a-z0-9]+)*$')", true,
			Collections.emptyList());
	}

	private ListTypeEntry _createListTypeEntry(
		String externalReferenceCode, String key, String name) {

		ListTypeEntry listTypeEntry = ListTypeEntryUtil.createListTypeEntry(
			externalReferenceCode, key,
			Collections.singletonMap(LocaleUtil.US, name));

		listTypeEntry.setSystem(true);

		return listTypeEntry;
	}

	private ObjectFieldSetting _createObjectFieldSetting(
		String name, String value) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0);

		objectFieldSetting.setName(name);
		objectFieldSetting.setValue(value);

		return objectFieldSetting;
	}

	private String _getUniqueIdentifier(Set<String> identifiers, String name) {
		String identifier = StringUtil.toLowerCase(name);

		identifier = identifier.replaceAll("[^a-z0-9]+", "-");
		identifier = identifier.replaceAll("^-+|-+$", "");

		if (Validator.isNull(identifier)) {
			identifier = "prompt";
		}

		String uniqueIdentifier = identifier;

		for (int i = 2; identifiers.contains(uniqueIdentifier); i++) {
			uniqueIdentifier = identifier + "-" + i;
		}

		identifiers.add(uniqueIdentifier);

		return uniqueIdentifier;
	}

	private void _updateObjectEntries(ObjectDefinition objectDefinition)
		throws PortalException {

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				0, objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		if (objectEntries.isEmpty()) {
			return;
		}

		Map<ObjectEntry, Map<String, Serializable>> valuesMap =
			new LinkedHashMap<>();

		Set<String> identifiers = new HashSet<>();

		for (ObjectEntry objectEntry : objectEntries) {
			Map<String, Serializable> values =
				_objectEntryLocalService.getValues(objectEntry);

			String identifier = GetterUtil.getString(values.get("identifier"));

			if (Validator.isNotNull(identifier)) {
				identifiers.add(identifier);
			}

			if (Validator.isNull(identifier) ||
				Validator.isNull(values.get("promptStatus"))) {

				valuesMap.put(objectEntry, values);
			}
		}

		for (Map.Entry<ObjectEntry, Map<String, Serializable>> entry :
				valuesMap.entrySet()) {

			Map<String, Serializable> values = entry.getValue();

			if (Validator.isNull(values.get("identifier"))) {
				values.put(
					"identifier",
					_getUniqueIdentifier(
						identifiers, GetterUtil.getString(values.get("name"))));
			}

			if (Validator.isNull(values.get("promptStatus"))) {
				values.put("promptStatus", "active");
			}

			ObjectEntry objectEntry = entry.getKey();

			_objectEntryLocalService.updateObjectEntry(
				objectEntry.getUserId(), objectEntry.getObjectEntryId(),
				objectEntry.getObjectEntryFolderId(), values,
				new ServiceContext());
		}
	}

	private void _upgradeCompany(long companyId) throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROMPT", companyId);

		if (objectDefinition == null) {
			return;
		}

		try (SafeCloseable safeCloseable =
				ObjectDefinitionThreadLocal.
					setSkipBundleAllowedCheckWithSafeCloseable(true)) {

			_addObjectFields(
				_addListTypeDefinition(companyId, objectDefinition.getUserId()),
				objectDefinition);
			_addObjectValidationRule(objectDefinition);
			_updateObjectEntries(objectDefinition);
		}
	}

	private final CompanyLocalService _companyLocalService;
	private final ListTypeDefinitionLocalService
		_listTypeDefinitionLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectFieldSettingLocalService
		_objectFieldSettingLocalService;
	private final ObjectValidationRuleLocalService
		_objectValidationRuleLocalService;

}