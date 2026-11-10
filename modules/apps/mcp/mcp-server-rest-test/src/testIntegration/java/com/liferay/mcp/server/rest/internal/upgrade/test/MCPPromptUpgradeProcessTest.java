/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.mcp.server.rest.test.util.MCPServerTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
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
 * @author Jaime León
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class MCPPromptUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		MCPServerTestUtil.deleteObjectDefinition("L_MCP_SERVER_PROFILE_TOOL");
		MCPServerTestUtil.deleteObjectDefinition("L_MCP_SERVER_PROFILE");
		MCPServerTestUtil.deleteObjectDefinition("L_MCP_SERVER_PROMPT");

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					"L_MCP_STATUS", TestPropsValues.getCompanyId());

		if (listTypeDefinition != null) {
			_listTypeDefinitionLocalService.deleteListTypeDefinition(
				listTypeDefinition);
		}

		MCPServerTestUtil.processBatchEngineUnits("batch");
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
					"L_MCP_SERVER_PROMPT", TestPropsValues.getCompanyId());

		_objectEntry1 = _addObjectEntry(objectDefinition, "My Prompt");
		_objectEntry2 = _addObjectEntry(objectDefinition, "My- Prompt!");

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.mcp.server.rest.internal.upgrade." +
				"MCPPromptUpgradeProcess");

		upgradeProcess.upgrade();

		_assertUpgrade(objectDefinition);

		upgradeProcess.upgrade();

		_assertUpgrade(objectDefinition);
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition, String name)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"description", RandomTestUtil.randomString()
			).put(
				"name", name
			).put(
				"prompt", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertUpgrade(ObjectDefinition objectDefinition)
		throws Exception {

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.
				getListTypeDefinitionByExternalReferenceCode(
					"L_MCP_STATUS", TestPropsValues.getCompanyId());

		Assert.assertTrue(listTypeDefinition.isSystem());

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.getListTypeEntry(
				listTypeDefinition.getListTypeDefinitionId(), "active");

		Assert.assertEquals("ACTIVE", listTypeEntry.getExternalReferenceCode());
		Assert.assertTrue(listTypeEntry.isSystem());

		listTypeEntry = _listTypeEntryLocalService.getListTypeEntry(
			listTypeDefinition.getListTypeDefinitionId(), "inactive");

		Assert.assertEquals(
			"INACTIVE", listTypeEntry.getExternalReferenceCode());
		Assert.assertTrue(listTypeEntry.isSystem());

		ObjectField identifierObjectField =
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "identifier");

		Assert.assertTrue(identifierObjectField.isRequired());

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				identifierObjectField.getObjectFieldId(),
				ObjectFieldSettingConstants.NAME_UNIQUE_VALUES);

		Assert.assertEquals("true", objectFieldSetting.getValue());

		ObjectField promptStatusObjectField =
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "promptStatus");

		Assert.assertEquals(
			ObjectFieldConstants.BUSINESS_TYPE_PICKLIST,
			promptStatusObjectField.getBusinessType());
		Assert.assertEquals(
			listTypeDefinition.getListTypeDefinitionId(),
			promptStatusObjectField.getListTypeDefinitionId());
		Assert.assertTrue(promptStatusObjectField.isRequired());

		Assert.assertNotNull(
			_objectValidationRuleLocalService.fetchObjectValidationRule(
				"L_MCP_SERVER_PROMPT_VALID_IDENTIFIER",
				objectDefinition.getObjectDefinitionId()));

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			_objectEntry1.getObjectEntryId());

		Assert.assertEquals("my-prompt", values.get("identifier"));
		Assert.assertEquals("active", values.get("promptStatus"));

		values = _objectEntryLocalService.getValues(
			_objectEntry2.getObjectEntryId());

		Assert.assertEquals("my-prompt-2", values.get("identifier"));
		Assert.assertEquals("active", values.get("promptStatus"));
	}

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private ObjectEntry _objectEntry1;

	@DeleteAfterTestRun
	private ObjectEntry _objectEntry2;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

	@Inject(
		filter = "component.name=com.liferay.mcp.server.rest.internal.upgrade.registry.MCPServerRestUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}