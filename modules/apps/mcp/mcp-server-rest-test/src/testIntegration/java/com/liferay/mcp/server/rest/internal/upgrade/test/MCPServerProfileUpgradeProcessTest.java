/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.mcp.server.rest.test.util.MCPServerTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.io.Serializable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Javier Moreno Lage
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@FeatureFlags(featureFlags = @FeatureFlag("LPD-63311"))
@RunWith(Arquillian.class)
public class MCPServerProfileUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_setUpObjectDefinitions();
	}

	@After
	public void tearDown() throws Exception {
		for (ObjectEntry objectEntry :
				new ObjectEntry[] {
					_objectEntry1, _objectEntry2, _objectEntry4
				}) {

			if (objectEntry == null) {
				continue;
			}

			_objectEntryLocalService.deleteObjectEntry(
				objectEntry.getObjectEntryId());
		}

		MCPServerTestUtil.processBatchEngineUnits();
	}

	@Test
	public void testUpgrade() throws Exception {
		ObjectDefinition objectDefinition = _addObjectEntries();

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.mcp.server.rest.internal.upgrade." +
				"MCPServerProfileUpgradeProcess");

		upgradeProcess.upgrade();

		_assertUpgrade(objectDefinition);

		upgradeProcess.upgrade();

		_assertUpgrade(objectDefinition);

		_setUpObjectDefinitions();

		objectDefinition = _addObjectEntries();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.ERROR)) {

			try {
				MCPServerTestUtil.processBatchEngineUnits();

				Assert.fail();
			}
			catch (Exception exception) {
				String message = exception.getMessage();

				Assert.assertTrue(
					message.contains(
						"02-object-definition.batch-engine-data.json"));
			}

			List<LogEntry> logEntries = logCapture.getLogEntries();

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"The tools object field cannot be deleted because the ",
					"profile tool \"mcp-server-profiles ",
					"getMCPServerProfilesPage\" of profile \"",
					_objectEntry1.getExternalReferenceCode(),
					"\" was not migrated to an MCPServerProfileTool object ",
					"entry"),
				logEntry.getMessage());
		}

		Assert.assertNotNull(
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "tools"));

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			_objectEntry1.getObjectEntryId());

		Assert.assertEquals(
			"mcp-server-profiles getMCPServerProfilesPage\nmcp-server-" +
				"profiles postMCPServerProfile",
			values.get("tools"));

		CacheRegistryUtil.clear();

		upgradeProcess.upgrade();

		_assertUpgrade(objectDefinition);

		MCPServerTestUtil.processBatchEngineUnits();

		_assertUpgrade(objectDefinition);
	}

	private ObjectDefinition _addObjectEntries() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE", TestPropsValues.getCompanyId());

		_objectEntry1 = _addObjectEntry(
			null, objectDefinition,
			"mcp-server-profiles getMCPServerProfilesPage\nmcp-server-" +
				"profiles postMCPServerProfile");
		_objectEntry2 = _addObjectEntry(
			null, objectDefinition,
			"malformed\nmcp-server-v1.0 getToolSetsPage");
		_objectEntry3 = _addObjectEntry(
			"L_MCP_SERVER_DEFAULT_PROFILE", objectDefinition,
			StringBundler.concat(
				"mcp-server-v1.0 getToolSetsPage\nmcp-server-v1.0 ",
				"getToolSetToolSetNameToolSummariesPage\nmcp-server-v1.0 ",
				"getToolSetToolSetNameTool\nmcp-server-v1.0 ",
				"postToolSetToolSetNameToolInvoke"));
		_objectEntry4 = _addObjectEntry(null, objectDefinition, "malformed");

		return objectDefinition;
	}

	private ObjectEntry _addObjectEntry(
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String tools)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"description", RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", () -> externalReferenceCode
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"tools", tools
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertMCPServerProfileToolObjectEntries(
			ObjectEntry mcpServerProfileObjectEntry,
			ObjectRelationship objectRelationship, String... expectedTools)
		throws Exception {

		List<ObjectEntry> mcpServerProfileToolObjectEntries =
			_objectEntryLocalService.getOneToManyObjectEntries(
				0, objectRelationship.getObjectRelationshipId(), null, false,
				mcpServerProfileObjectEntry.getObjectEntryId(), true, null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			mcpServerProfileToolObjectEntries.toString(), expectedTools.length,
			mcpServerProfileToolObjectEntries.size());

		Set<String> tools = new HashSet<>();

		for (ObjectEntry mcpServerProfileToolObjectEntry :
				mcpServerProfileToolObjectEntries) {

			Map<String, Serializable> values =
				mcpServerProfileToolObjectEntry.getValues();

			tools.add(
				values.get("toolSetName") + StringPool.SPACE +
					values.get("toolName"));
		}

		for (String expectedTool : expectedTools) {
			Assert.assertTrue(tools.toString(), tools.contains(expectedTool));
		}
	}

	private void _assertMCPServerProfileToolObjectEntry(
		String externalReferenceCode, ObjectDefinition objectDefinition) {

		Assert.assertNotNull(
			externalReferenceCode,
			_objectEntryLocalService.fetchObjectEntry(
				externalReferenceCode, 0,
				objectDefinition.getObjectDefinitionId()));
	}

	private void _assertProfileStatus(
			String expectedProfileStatus, ObjectEntry objectEntry)
		throws Exception {

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			objectEntry.getObjectEntryId());

		Assert.assertEquals(expectedProfileStatus, values.get("profileStatus"));
	}

	private void _assertUpgrade(ObjectDefinition objectDefinition)
		throws Exception {

		ObjectDefinition mcpServerProfileToolObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE_TOOL",
					TestPropsValues.getCompanyId());

		Assert.assertTrue(mcpServerProfileToolObjectDefinition.isApproved());
		Assert.assertTrue(mcpServerProfileToolObjectDefinition.isModifiable());
		Assert.assertTrue(mcpServerProfileToolObjectDefinition.isSystem());

		ObjectField toolNameObjectField =
			_objectFieldLocalService.getObjectField(
				mcpServerProfileToolObjectDefinition.getObjectDefinitionId(),
				"toolName");

		Assert.assertTrue(toolNameObjectField.isRequired());

		ObjectField toolSetNameObjectField =
			_objectFieldLocalService.getObjectField(
				mcpServerProfileToolObjectDefinition.getObjectDefinitionId(),
				"toolSetName");

		Assert.assertTrue(toolSetNameObjectField.isRequired());

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				fetchObjectRelationshipByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE_TO_L_MCP_SERVER_PROFILE_TOOL",
					TestPropsValues.getCompanyId(),
					objectDefinition.getObjectDefinitionId());

		Assert.assertEquals(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			objectRelationship.getDeletionType());
		Assert.assertTrue(objectRelationship.isSystem());

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			mcpServerProfileToolObjectDefinition.getObjectDefinitionId(),
			"r_mcpServerProfileToTools_l_mcpServerProfileId");

		Assert.assertTrue(objectField.isRequired());

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				objectField.getObjectFieldId(),
				"objectRelationshipERCObjectFieldName");

		Assert.assertEquals(
			"r_mcpServerProfileToTools_l_mcpServerProfileERC",
			objectFieldSetting.getValue());

		_assertMCPServerProfileToolObjectEntries(
			_objectEntry1, objectRelationship,
			"mcp-server-profiles getMCPServerProfilesPage",
			"mcp-server-profiles postMCPServerProfile");
		_assertMCPServerProfileToolObjectEntries(
			_objectEntry2, objectRelationship,
			"mcp-server-v1.0 getToolSetsPage");
		_assertMCPServerProfileToolObjectEntries(
			_objectEntry3, objectRelationship,
			"mcp-server-v1.0 getToolSetsPage",
			"mcp-server-v1.0 getToolSetToolSetNameToolSummariesPage",
			"mcp-server-v1.0 getToolSetToolSetNameTool",
			"mcp-server-v1.0 postToolSetToolSetNameToolInvoke");

		_assertMCPServerProfileToolObjectEntry(
			"L_MCP_SERVER_DEFAULT_PROFILE_TOOL_GET_TOOL",
			mcpServerProfileToolObjectDefinition);
		_assertMCPServerProfileToolObjectEntry(
			"L_MCP_SERVER_DEFAULT_PROFILE_TOOL_GET_TOOL_SETS_PAGE",
			mcpServerProfileToolObjectDefinition);
		_assertMCPServerProfileToolObjectEntry(
			"L_MCP_SERVER_DEFAULT_PROFILE_TOOL_GET_TOOL_SUMMARIES_PAGE",
			mcpServerProfileToolObjectDefinition);
		_assertMCPServerProfileToolObjectEntry(
			"L_MCP_SERVER_DEFAULT_PROFILE_TOOL_INVOKE",
			mcpServerProfileToolObjectDefinition);

		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "tools"));

		ObjectField profileStatusObjectField =
			_objectFieldLocalService.getObjectField(
				objectDefinition.getObjectDefinitionId(), "profileStatus");

		Assert.assertEquals(
			ObjectFieldConstants.BUSINESS_TYPE_PICKLIST,
			profileStatusObjectField.getBusinessType());
		Assert.assertTrue(profileStatusObjectField.isRequired());
		Assert.assertTrue(profileStatusObjectField.isSystem());

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.getListTypeDefinition(
				profileStatusObjectField.getListTypeDefinitionId());

		Assert.assertEquals(
			"L_MCP_STATUS", listTypeDefinition.getExternalReferenceCode());

		ObjectFieldSetting defaultValueObjectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				profileStatusObjectField.getObjectFieldId(), "defaultValue");

		Assert.assertEquals(
			"inactive", defaultValueObjectFieldSetting.getValue());

		_assertProfileStatus("active", _objectEntry1);
		_assertProfileStatus("active", _objectEntry2);
		_assertProfileStatus("active", _objectEntry3);
		_assertProfileStatus("inactive", _objectEntry4);
	}

	private void _setUpObjectDefinitions() throws Exception {
		MCPServerTestUtil.deleteObjectDefinition("L_MCP_SERVER_PROFILE");
		MCPServerTestUtil.deleteObjectDefinition("L_MCP_SERVER_PROFILE_TOOL");

		MCPServerTestUtil.processBatchEngineUnits("profile");
	}

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectEntry _objectEntry1;
	private ObjectEntry _objectEntry2;
	private ObjectEntry _objectEntry3;
	private ObjectEntry _objectEntry4;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject(
		filter = "component.name=com.liferay.mcp.server.rest.internal.upgrade.registry.MCPServerRestUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}