/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
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
 * @author Alejandro Tardín
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@FeatureFlags(featureFlags = @FeatureFlag("LPD-63311"))
@RunWith(Arquillian.class)
public class MCPProfilePathUpgradeProcessTest {

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
					"L_MCP_SERVER_PROFILE", TestPropsValues.getCompanyId());

		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "profilePath"));

		_defaultMCPServerProfileObjectEntry = _addObjectEntry(
			"L_MCP_SERVER_DEFAULT_PROFILE", objectDefinition, "Default");
		_objectEntry1 = _addObjectEntry(null, objectDefinition, "My Profile/1");
		_objectEntry2 = _addObjectEntry(null, objectDefinition, "My Profile 2");

		ObjectEntry objectEntry = _addObjectEntry(
			null, objectDefinition, "My-Profile-2");

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.mcp.server.rest.internal.upgrade." +
				"MCPProfilePathUpgradeProcess");

		try {
			upgradeProcess.upgrade();

			Assert.fail();
		}
		catch (Exception exception) {
			String message = exception.getMessage();

			Assert.assertTrue(
				message, message.contains("Unique value constraint violation"));
		}

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		upgradeProcess.upgrade();

		_assertUpgrade(objectDefinition);

		upgradeProcess.upgrade();

		_assertUpgrade(objectDefinition);

		MCPServerTestUtil.processBatchEngineUnits();

		_assertUpgrade(objectDefinition);
	}

	private ObjectEntry _addObjectEntry(
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String name)
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
				"name", name
			).put(
				"profileStatus", "inactive"
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertProfilePath(
			ObjectEntry objectEntry, String expectedProfilePath)
		throws Exception {

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			objectEntry.getObjectEntryId());

		Assert.assertEquals(expectedProfilePath, values.get("profilePath"));
	}

	private void _assertUpgrade(ObjectDefinition objectDefinition)
		throws Exception {

		ObjectField profilePathObjectField =
			_objectFieldLocalService.getObjectField(
				objectDefinition.getObjectDefinitionId(), "profilePath");

		Assert.assertEquals(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			profilePathObjectField.getBusinessType());
		Assert.assertFalse(profilePathObjectField.isRequired());
		Assert.assertTrue(profilePathObjectField.isSystem());

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				profilePathObjectField.getObjectFieldId(),
				ObjectFieldSettingConstants.NAME_UNIQUE_VALUES);

		Assert.assertEquals("true", objectFieldSetting.getValue());

		Assert.assertNotNull(
			_objectValidationRuleLocalService.fetchObjectValidationRule(
				"L_MCP_SERVER_PROFILE_VALID_PROFILE_PATH",
				objectDefinition.getObjectDefinitionId()));

		_assertProfilePath(
			_defaultMCPServerProfileObjectEntry, StringPool.BLANK);
		_assertProfilePath(_objectEntry1, "my-profile-1");
		_assertProfilePath(_objectEntry2, "my-profile-2");
	}

	private ObjectEntry _defaultMCPServerProfileObjectEntry;

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