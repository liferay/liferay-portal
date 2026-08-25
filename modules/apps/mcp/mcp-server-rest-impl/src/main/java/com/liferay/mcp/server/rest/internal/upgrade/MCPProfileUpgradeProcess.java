/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.upgrade;

import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.mcp.server.rest.internal.constants.MCPServerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.definition.util.ObjectDefinitionThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Alberto Javier Moreno Lage
 */
public class MCPProfileUpgradeProcess extends UpgradeProcess {

	public MCPProfileUpgradeProcess(
		CompanyLocalService companyLocalService,
		ListTypeDefinitionLocalService listTypeDefinitionLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectFieldSettingLocalService objectFieldSettingLocalService,
		ObjectFolderLocalService objectFolderLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService) {

		_companyLocalService = companyLocalService;
		_listTypeDefinitionLocalService = listTypeDefinitionLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_objectFieldSettingLocalService = objectFieldSettingLocalService;
		_objectFolderLocalService = objectFolderLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
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

		try {
			return _listTypeDefinitionLocalService.addListTypeDefinition(
				"L_MCP_STATUS", userId,
				Collections.singletonMap(LocaleUtil.US, "MCP Status"), true,
				Arrays.asList(
					_createListTypeEntry("ACTIVE", "active", "Active"),
					_createListTypeEntry("INACTIVE", "inactive", "Inactive")),
				new ServiceContext());
		}
		catch (PortalException portalException) {
			listTypeDefinition =
				_listTypeDefinitionLocalService.
					fetchListTypeDefinitionByExternalReferenceCode(
						"L_MCP_STATUS", companyId);

			if (listTypeDefinition == null) {
				throw portalException;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return listTypeDefinition;
		}
	}

	private void _addMCPServerProfileToolObjectEntries(
			ObjectDefinition mcpServerProfileObjectDefinition,
			ObjectRelationship objectRelationship)
		throws PortalException {

		for (ObjectEntry mcpServerProfileObjectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, mcpServerProfileObjectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> mcpServerProfileValues =
				_objectEntryLocalService.getValues(mcpServerProfileObjectEntry);

			String tools = GetterUtil.getString(
				mcpServerProfileValues.get("tools"));

			if (Validator.isNull(tools)) {
				continue;
			}

			Set<String> existingTools = new HashSet<>();

			for (ObjectEntry mcpServerProfileToolObjectEntry :
					_objectEntryLocalService.getOneToManyObjectEntries(
						0, objectRelationship.getObjectRelationshipId(), null,
						false, mcpServerProfileObjectEntry.getObjectEntryId(),
						true, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
						null)) {

				Map<String, Serializable> values =
					mcpServerProfileToolObjectEntry.getValues();

				existingTools.add(
					StringBundler.concat(
						values.get("toolSetName"), StringPool.SPACE,
						values.get("toolName")));
			}

			for (String tool : StringUtil.splitLines(tools)) {
				String[] tokens = StringUtil.split(tool, CharPool.SPACE);

				if (tokens.length != 2) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to migrate malformed profile tool \"",
								tool, "\""));
					}

					continue;
				}

				String normalizedTool =
					tokens[0] + StringPool.SPACE + tokens[1];

				if (!existingTools.add(normalizedTool)) {
					continue;
				}

				_addMCPServerProfileToolObjectEntry(
					mcpServerProfileObjectEntry, normalizedTool,
					objectRelationship, tokens[1], tokens[0]);
			}
		}
	}

	private void _addMCPServerProfileToolObjectEntry(
			ObjectEntry mcpServerProfileObjectEntry, String normalizedTool,
			ObjectRelationship objectRelationship, String toolName,
			String toolSetName)
		throws PortalException {

		try {
			_objectEntryLocalService.addObjectEntry(
				0, mcpServerProfileObjectEntry.getUserId(),
				objectRelationship.getObjectDefinitionId2(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"externalReferenceCode",
					() -> {
						if (Objects.equals(
								mcpServerProfileObjectEntry.
									getExternalReferenceCode(),
								"L_MCP_SERVER_DEFAULT_PROFILE")) {

							return _defaultProfileToolExternalReferenceCodes.
								get(normalizedTool);
						}

						return null;
					}
				).put(
					"r_mcpServerProfileToTools_l_mcpServerProfileId",
					mcpServerProfileObjectEntry.getObjectEntryId()
				).put(
					"toolName", toolName
				).put(
					"toolSetName", toolSetName
				).build(),
				new ServiceContext());
		}
		catch (PortalException portalException) {
			for (ObjectEntry mcpServerProfileToolObjectEntry :
					_objectEntryLocalService.getOneToManyObjectEntries(
						0, objectRelationship.getObjectRelationshipId(), null,
						false, mcpServerProfileObjectEntry.getObjectEntryId(),
						true, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
						null)) {

				Map<String, Serializable> values =
					mcpServerProfileToolObjectEntry.getValues();

				if (Objects.equals(
						normalizedTool,
						StringBundler.concat(
							values.get("toolSetName"), StringPool.SPACE,
							values.get("toolName")))) {

					if (_log.isDebugEnabled()) {
						_log.debug(portalException);
					}

					return;
				}
			}

			throw portalException;
		}
	}

	private void _addProfileStatusObjectField(ObjectDefinition objectDefinition)
		throws PortalException {

		ObjectField profileStatusObjectField =
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "profileStatus");

		if (profileStatusObjectField != null) {
			return;
		}

		ListTypeDefinition listTypeDefinition = _addListTypeDefinition(
			objectDefinition.getCompanyId(), objectDefinition.getUserId());

		try {
			_objectFieldLocalService.addSystemObjectField(
				null, objectDefinition.getUserId(),
				listTypeDefinition.getListTypeDefinitionId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectFieldConstants.BUSINESS_TYPE_PICKLIST, null, null,
				ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
				Collections.singletonMap(LocaleUtil.US, "Status"), false,
				"profileStatus", ObjectFieldConstants.READ_ONLY_FALSE, null,
				true, false,
				Arrays.asList(
					_createObjectFieldSetting(
						ObjectFieldSettingConstants.NAME_DEFAULT_VALUE,
						"inactive"),
					_createObjectFieldSetting(
						ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE,
						ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE)));
		}
		catch (PortalException portalException) {
			profileStatusObjectField =
				_objectFieldLocalService.fetchObjectField(
					objectDefinition.getObjectDefinitionId(), "profileStatus");

			if (profileStatusObjectField == null) {
				throw portalException;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}
	}

	private ListTypeEntry _createListTypeEntry(
		String externalReferenceCode, String key, String name) {

		ListTypeEntry listTypeEntry = ListTypeEntryUtil.createListTypeEntry(
			externalReferenceCode, key,
			Collections.singletonMap(LocaleUtil.US, name));

		listTypeEntry.setSystem(true);

		return listTypeEntry;
	}

	private ObjectField _createObjectField(String label, String name) {
		ObjectField objectField = _objectFieldLocalService.createObjectField(0);

		objectField.setBusinessType(ObjectFieldConstants.BUSINESS_TYPE_TEXT);
		objectField.setDBType(ObjectFieldConstants.DB_TYPE_STRING);
		objectField.setIndexed(true);
		objectField.setIndexedAsKeyword(false);
		objectField.setIndexedLanguageId("en_US");
		objectField.setLabelMap(Collections.singletonMap(LocaleUtil.US, label));
		objectField.setName(name);
		objectField.setRequired(true);
		objectField.setSystem(true);

		return objectField;
	}

	private ObjectFieldSetting _createObjectFieldSetting(
		String name, String value) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0);

		objectFieldSetting.setName(name);
		objectFieldSetting.setValue(value);

		return objectFieldSetting;
	}

	private void _deleteToolsObjectField(
			ObjectDefinition mcpServerProfileObjectDefinition,
			ObjectField toolsObjectField)
		throws Exception {

		try {
			_objectFieldLocalService.deleteObjectField(toolsObjectField);
		}
		catch (Exception exception) {
			toolsObjectField = _objectFieldLocalService.fetchObjectField(
				mcpServerProfileObjectDefinition.getObjectDefinitionId(),
				"tools");

			if (toolsObjectField != null) {
				throw exception;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private ObjectDefinition _getOrAddMCPServerProfileToolObjectDefinition(
			long companyId, long userId)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE_TOOL,
					companyId);

		if (objectDefinition == null) {
			ObjectFolder objectFolder =
				_objectFolderLocalService.getOrAddDefaultObjectFolder(
					companyId);

			try {
				objectDefinition =
					_objectDefinitionLocalService.addSystemObjectDefinition(
						MCPServerConstants.
							EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE_TOOL,
						userId, objectFolder.getObjectFolderId(),
						"com.liferay.object.model.ObjectDefinition#T4L8", null,
						true, false, true, false, false, false, false, false,
						false, false, null,
						Collections.singletonMap(
							LocaleUtil.US, "MCP Server Profile Tool"),
						true, "MCPServerProfileTool", null,
						"control_panel.object", null, null,
						Collections.singletonMap(
							LocaleUtil.US, "MCP Server Profile Tools"),
						false, ObjectDefinitionConstants.SCOPE_COMPANY,
						"toolName", 1, WorkflowConstants.STATUS_DRAFT,
						Collections.emptyList(),
						Arrays.asList(
							_createObjectField("Tool Name", "toolName"),
							_createObjectField("Tool Set Name", "toolSetName")),
						Collections.emptyList());
			}
			catch (PortalException portalException) {
				objectDefinition =
					_objectDefinitionLocalService.
						fetchObjectDefinitionByExternalReferenceCode(
							MCPServerConstants.
								EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE_TOOL,
							companyId);

				if (objectDefinition == null) {
					throw portalException;
				}

				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		if (objectDefinition.isApproved()) {
			return objectDefinition;
		}

		try {
			return _objectDefinitionLocalService.publishSystemObjectDefinition(
				userId, objectDefinition.getObjectDefinitionId());
		}
		catch (PortalException portalException) {
			objectDefinition =
				_objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						MCPServerConstants.
							EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE_TOOL,
						companyId);

			if ((objectDefinition == null) || !objectDefinition.isApproved()) {
				throw portalException;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return objectDefinition;
		}
	}

	private ObjectRelationship _getOrAddObjectRelationship(
			ObjectDefinition mcpServerProfileObjectDefinition,
			ObjectDefinition mcpServerProfileToolObjectDefinition)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				fetchObjectRelationshipByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE_TO_L_MCP_SERVER_PROFILE_TOOL",
					mcpServerProfileObjectDefinition.getCompanyId(),
					mcpServerProfileObjectDefinition.getObjectDefinitionId());

		if (objectRelationship != null) {
			return objectRelationship;
		}

		ObjectField objectField = _objectFieldLocalService.createObjectField(0);

		objectField.setLabelMap(
			Collections.singletonMap(
				LocaleUtil.US,
				"MCP Server Profile to MCP Server Profile Tools"));
		objectField.setRequired(true);

		try {
			return _objectRelationshipLocalService.addObjectRelationship(
				"L_MCP_SERVER_PROFILE_TO_L_MCP_SERVER_PROFILE_TOOL",
				mcpServerProfileObjectDefinition.getUserId(),
				mcpServerProfileObjectDefinition.getObjectDefinitionId(),
				mcpServerProfileToolObjectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				Collections.singletonMap(
					LocaleUtil.US,
					"MCP Server Profile to MCP Server Profile Tools"),
				"mcpServerProfileToTools", true,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, objectField);
		}
		catch (PortalException portalException) {
			objectRelationship =
				_objectRelationshipLocalService.
					fetchObjectRelationshipByExternalReferenceCode(
						"L_MCP_SERVER_PROFILE_TO_L_MCP_SERVER_PROFILE_TOOL",
						mcpServerProfileObjectDefinition.getCompanyId(),
						mcpServerProfileObjectDefinition.
							getObjectDefinitionId());

			if (objectRelationship == null) {
				throw portalException;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return objectRelationship;
		}
	}

	private void _updateObjectEntries(ObjectDefinition objectDefinition)
		throws PortalException {

		for (ObjectEntry objectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, objectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values =
				_objectEntryLocalService.getValues(objectEntry);

			if (Validator.isNotNull(values.get("profileStatus"))) {
				continue;
			}

			values.put("profileStatus", "active");

			_objectEntryLocalService.updateObjectEntry(
				objectEntry.getUserId(), objectEntry.getObjectEntryId(),
				objectEntry.getObjectEntryFolderId(), values,
				new ServiceContext());
		}
	}

	private void _upgradeCompany(long companyId) throws Exception {
		ObjectDefinition mcpServerProfileObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE,
					companyId);

		if (mcpServerProfileObjectDefinition == null) {
			return;
		}

		try (SafeCloseable safeCloseable =
				ObjectDefinitionThreadLocal.
					setSkipBundleAllowedCheckWithSafeCloseable(true)) {

			_addProfileStatusObjectField(mcpServerProfileObjectDefinition);

			ObjectField toolsObjectField =
				_objectFieldLocalService.fetchObjectField(
					mcpServerProfileObjectDefinition.getObjectDefinitionId(),
					"tools");

			if (toolsObjectField != null) {
				_addMCPServerProfileToolObjectEntries(
					mcpServerProfileObjectDefinition,
					_getOrAddObjectRelationship(
						mcpServerProfileObjectDefinition,
						_getOrAddMCPServerProfileToolObjectDefinition(
							companyId,
							mcpServerProfileObjectDefinition.getUserId())));

				_deleteToolsObjectField(
					mcpServerProfileObjectDefinition, toolsObjectField);
			}

			// Deleting the "tools" object field evicts the object field caches.
			// Updating the object entries against a stale field list silently
			// drops the "profileStatus" value.

			_updateObjectEntries(mcpServerProfileObjectDefinition);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MCPProfileUpgradeProcess.class);

	private static final Map<String, String>
		_defaultProfileToolExternalReferenceCodes = HashMapBuilder.put(
			"mcp-server-v1.0 getToolSetsPage",
			"L_MCP_SERVER_DEFAULT_PROFILE_TOOL_GET_TOOL_SETS_PAGE"
		).put(
			"mcp-server-v1.0 getToolSetToolSetNameTool",
			"L_MCP_SERVER_DEFAULT_PROFILE_TOOL_GET_TOOL"
		).put(
			"mcp-server-v1.0 getToolSetToolSetNameToolSummariesPage",
			"L_MCP_SERVER_DEFAULT_PROFILE_TOOL_GET_TOOL_SUMMARIES_PAGE"
		).put(
			"mcp-server-v1.0 postToolSetToolSetNameToolInvoke",
			"L_MCP_SERVER_DEFAULT_PROFILE_TOOL_INVOKE"
		).build();

	private final CompanyLocalService _companyLocalService;
	private final ListTypeDefinitionLocalService
		_listTypeDefinitionLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectFieldSettingLocalService
		_objectFieldSettingLocalService;
	private final ObjectFolderLocalService _objectFolderLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;

}