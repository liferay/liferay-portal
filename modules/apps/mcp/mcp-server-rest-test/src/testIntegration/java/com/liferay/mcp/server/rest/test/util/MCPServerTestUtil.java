/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.test.util;

import com.liferay.batch.engine.test.util.BatchEngineTestUtil;
import com.liferay.batch.engine.unit.BatchEngineUnitThreadLocal;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalServiceUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Jose Luis Navarro
 */
public class MCPServerTestUtil {

	public static ObjectEntry addDataMaskObjectEntry(
			String detectionRegex, String name, String replacementValue)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_MASK", TestPropsValues.getCompanyId());

		return ObjectEntryLocalServiceUtil.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"detectionRegex", detectionRegex
			).put(
				"maskType", "custom"
			).put(
				"name", name
			).put(
				"replacementValue", replacementValue
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	public static ObjectEntry addMCPServerProfileDataMaskObjectEntry(
			long dataMaskObjectEntryId, int executionOrder,
			String mcpServerProfileExternalReferenceCode)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE_DATA_MASK",
					TestPropsValues.getCompanyId());

		return ObjectEntryLocalServiceUtil.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"dataMaskExternalReferenceCode",
				ObjectEntryLocalServiceUtil.fetchObjectEntry(
					dataMaskObjectEntryId
				).getExternalReferenceCode()
			).put(
				"executionOrder", executionOrder
			).put(
				"mcpServerProfileExternalReferenceCode",
				mcpServerProfileExternalReferenceCode
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	public static ObjectEntry addMCPServerProfileObjectEntry(
			String description, String name, String... tools)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE", TestPropsValues.getCompanyId());

		ObjectEntry mcpServerProfileObjectEntry =
			ObjectEntryLocalServiceUtil.addObjectEntry(
				0, TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"description", description
				).put(
					"name", name
				).put(
					"profileStatus", "active"
				).build(),
				ServiceContextTestUtil.getServiceContext());

		for (String tool : tools) {
			String[] parts = tool.split(StringPool.SPACE);

			addMCPServerProfileToolObjectEntry(
				mcpServerProfileObjectEntry.getExternalReferenceCode(),
				parts[1], parts[0]);
		}

		return mcpServerProfileObjectEntry;
	}

	public static ObjectEntry addMCPServerProfileToolObjectEntry(
			String mcpServerProfileExternalReferenceCode, String toolName,
			String toolSetName)
		throws Exception {

		ObjectDefinition mcpServerProfileObjectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE", TestPropsValues.getCompanyId());

		ObjectEntry mcpServerProfileObjectEntry =
			ObjectEntryLocalServiceUtil.getObjectEntry(
				mcpServerProfileExternalReferenceCode, 0,
				mcpServerProfileObjectDefinition.getObjectDefinitionId());

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE_TOOL",
					TestPropsValues.getCompanyId());

		return ObjectEntryLocalServiceUtil.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"r_mcpServerProfileToTools_l_mcpServerProfileId",
				mcpServerProfileObjectEntry.getObjectEntryId()
			).put(
				"toolName", toolName
			).put(
				"toolSetName", toolSetName
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	public static ObjectEntry addMCPServerPromptObjectEntry(
			String description, String identifier, String name, String prompt,
			String promptStatus)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROMPT", TestPropsValues.getCompanyId());

		return ObjectEntryLocalServiceUtil.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"description", description
			).put(
				"identifier", identifier
			).put(
				"name", name
			).put(
				"prompt", prompt
			).put(
				"promptStatus", () -> promptStatus
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	public static ObjectEntry addSystemDataMaskObjectEntry(
			String detectionRegex, String externalReferenceCode, String name,
			String replacementValue)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_MASK", TestPropsValues.getCompanyId());

		String fileName = BatchEngineUnitThreadLocal.getFileName();

		BatchEngineUnitThreadLocal.setFileName(_DATA_MASK_BATCH_FILE_NAME);

		try {
			return ObjectEntryLocalServiceUtil.addObjectEntry(
				0, TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"detectionRegex", detectionRegex
				).put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"maskType", "system"
				).put(
					"name", name
				).put(
					"replacementValue", replacementValue
				).build(),
				ServiceContextTestUtil.getServiceContext());
		}
		finally {
			BatchEngineUnitThreadLocal.setFileName(fileName);
		}
	}

	public static void deleteMCPServerProfileDataMaskObjectEntry(
			String deleteReason, ObjectEntry objectEntry)
		throws Exception {

		ObjectEntryLocalServiceUtil.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(), 0,
			HashMapBuilder.<String, Serializable>putAll(
				objectEntry.getValues()
			).put(
				"deleteReason", deleteReason
			).build(),
			ServiceContextTestUtil.getServiceContext());

		ObjectEntryLocalServiceUtil.deleteObjectEntry(
			objectEntry.getObjectEntryId());
	}

	public static void deleteSystemDataMaskObjectEntry(ObjectEntry objectEntry)
		throws Exception {

		String fileName = BatchEngineUnitThreadLocal.getFileName();

		BatchEngineUnitThreadLocal.setFileName(_DATA_MASK_BATCH_FILE_NAME);

		try {
			ObjectEntryLocalServiceUtil.deleteObjectEntry(
				objectEntry.getObjectEntryId());
		}
		finally {
			BatchEngineUnitThreadLocal.setFileName(fileName);
		}
	}

	public static ObjectEntry fetchDataMaskObjectEntry(String name)
		throws Exception {

		return _fetchObjectEntry(name, "L_DATA_MASK");
	}

	public static ObjectEntry fetchMCPServerProfileObjectEntry(String name)
		throws Exception {

		return _fetchObjectEntry(name, "L_MCP_SERVER_PROFILE");
	}

	public static String getAuditedDeleteReason(ObjectEntry objectEntry)
		throws Exception {

		List<AuditEvent> auditEvents =
			AuditEventLocalServiceUtil.getAuditEvents(
				0, 0, 0, null, null, null, null, null,
				String.valueOf(objectEntry.getObjectEntryId()), null, null,
				null, EventTypes.DELETE, null, 0, null, true, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		if (auditEvents.isEmpty()) {
			return null;
		}

		AuditEvent auditEvent = auditEvents.get(0);

		return JSONFactoryUtil.createJSONObject(
			auditEvent.getAdditionalInfo()
		).getString(
			"deleteReason"
		);
	}

	public static int getMCPServerProfileDataMaskExecutionOrder(
			String dataMaskExternalReferenceCode,
			String mcpServerProfileExternalReferenceCode)
		throws Exception {

		for (ObjectEntry mcpServerProfileDataMaskObjectEntry :
				getMCPServerProfileDataMaskObjectEntries(
					mcpServerProfileExternalReferenceCode)) {

			Map<String, Serializable> values =
				mcpServerProfileDataMaskObjectEntry.getValues();

			if (Objects.equals(
					dataMaskExternalReferenceCode,
					values.get("dataMaskExternalReferenceCode"))) {

				return MapUtil.getInteger(values, "executionOrder");
			}
		}

		return -1;
	}

	public static List<ObjectEntry> getMCPServerProfileDataMaskObjectEntries(
			String mcpServerProfileExternalReferenceCode)
		throws Exception {

		List<ObjectEntry> mcpServerProfileDataMaskObjectEntries =
			new ArrayList<>();

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE_DATA_MASK",
					TestPropsValues.getCompanyId());

		for (ObjectEntry objectEntry :
				ObjectEntryLocalServiceUtil.getObjectEntries(
					0, objectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values = objectEntry.getValues();

			if (Objects.equals(
					mcpServerProfileExternalReferenceCode,
					values.get("mcpServerProfileExternalReferenceCode"))) {

				mcpServerProfileDataMaskObjectEntries.add(objectEntry);
			}
		}

		return mcpServerProfileDataMaskObjectEntries;
	}

	public static void processBatchEngineUnits() {
		String prefix = ".com.liferay.mcp.server.rest.internal.batch.";

		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.mcp.server.rest.impl", MCPServerTestUtil.class,
			new String[] {
				prefix + "00.list.type.definition",
				prefix + "01.object.definition",
				prefix + "02.object.definition",
				prefix + "03.object.definition",
				prefix + "04.object.definition", prefix + "05.object.entry",
				prefix + "06.object.entry"
			});

		prefix = ".com.liferay.headless.data.mask.internal.batch.";

		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.headless.data.mask.impl", MCPServerTestUtil.class,
			new String[] {
				prefix + "01.list.type.definition",
				prefix + "02.object.definition", prefix + "03.object.entry"
			});
	}

	private static ObjectEntry _fetchObjectEntry(
			String name, String objectDefinitionExternalReferenceCode)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode,
					TestPropsValues.getCompanyId());

		if (objectDefinition == null) {
			return null;
		}

		for (ObjectEntry objectEntry :
				ObjectEntryLocalServiceUtil.getObjectEntries(
					0, objectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values = objectEntry.getValues();

			if (name.equals(values.get("name"))) {
				return objectEntry;
			}
		}

		return null;
	}

	private static final String _DATA_MASK_BATCH_FILE_NAME =
		"com.liferay.headless.data.mask.impl_test";

}