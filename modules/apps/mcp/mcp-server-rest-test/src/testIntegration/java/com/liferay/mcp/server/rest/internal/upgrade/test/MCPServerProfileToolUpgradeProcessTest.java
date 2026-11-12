/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mcp.server.rest.test.util.MCPServerTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.io.Serializable;

import java.util.Map;

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
public class MCPServerProfileToolUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		MCPServerTestUtil.deleteObjectDefinition("L_MCP_SERVER_PROFILE");
		MCPServerTestUtil.deleteObjectDefinition("L_MCP_SERVER_PROFILE_TOOL");

		MCPServerTestUtil.processBatchEngineUnits("tool");
	}

	@After
	public void tearDown() {
		MCPServerTestUtil.processBatchEngineUnits();
	}

	@Test
	public void testUpgrade() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE_TOOL",
					TestPropsValues.getCompanyId());

		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "restrictFields"));

		ObjectEntry mcpServerProfileToolObjectEntry =
			_addMCPServerProfileToolObjectEntry(objectDefinition);

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.mcp.server.rest.internal.upgrade." +
				"MCPServerProfileToolUpgradeProcess");

		upgradeProcess.upgrade();

		_assertUpgrade(mcpServerProfileToolObjectEntry, objectDefinition);

		upgradeProcess.upgrade();

		_assertUpgrade(mcpServerProfileToolObjectEntry, objectDefinition);

		MCPServerTestUtil.processBatchEngineUnits();

		_assertUpgrade(mcpServerProfileToolObjectEntry, objectDefinition);
	}

	private ObjectEntry _addMCPServerProfileToolObjectEntry(
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectDefinition mcpServerProfileObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE", TestPropsValues.getCompanyId());

		ObjectEntry mcpServerProfileObjectEntry = _addObjectEntry(
			mcpServerProfileObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"description", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"profileStatus", "inactive"
			).build());

		return _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"r_mcpServerProfileToTools_l_mcpServerProfileId",
				mcpServerProfileObjectEntry.getObjectEntryId()
			).put(
				"toolName", "getMCPServerProfilesPage"
			).put(
				"toolSetName", "mcp-server-profiles"
			).build());
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition, Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values, ServiceContextTestUtil.getServiceContext());
	}

	private void _assertUpgrade(
			ObjectEntry mcpServerProfileToolObjectEntry,
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectField restrictFieldsObjectField =
			_objectFieldLocalService.getObjectField(
				objectDefinition.getObjectDefinitionId(), "restrictFields");

		Assert.assertEquals(
			ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT,
			restrictFieldsObjectField.getBusinessType());
		Assert.assertEquals(
			ObjectFieldConstants.DB_TYPE_CLOB,
			restrictFieldsObjectField.getDBType());
		Assert.assertFalse(restrictFieldsObjectField.isRequired());
		Assert.assertTrue(restrictFieldsObjectField.isSystem());

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			mcpServerProfileToolObjectEntry.getObjectEntryId());

		Assert.assertEquals("getMCPServerProfilesPage", values.get("toolName"));
		Assert.assertEquals("mcp-server-profiles", values.get("toolSetName"));

		String restrictFields = RandomTestUtil.randomString();

		values.put("restrictFields", restrictFields);

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(),
			mcpServerProfileToolObjectEntry.getObjectEntryId(),
			mcpServerProfileToolObjectEntry.getObjectEntryFolderId(), values,
			ServiceContextTestUtil.getServiceContext());

		values = _objectEntryLocalService.getValues(
			mcpServerProfileToolObjectEntry.getObjectEntryId());

		Assert.assertEquals(restrictFields, values.get("restrictFields"));
	}

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject(
		filter = "component.name=com.liferay.mcp.server.rest.internal.upgrade.registry.MCPServerRestUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}