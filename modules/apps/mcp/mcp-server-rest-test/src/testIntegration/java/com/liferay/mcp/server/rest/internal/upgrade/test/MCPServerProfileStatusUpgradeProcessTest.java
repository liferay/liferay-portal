/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.unit.BatchEngineUnitThreadLocal;
import com.liferay.mcp.server.rest.test.util.MCPServerTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
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
 * @author Jose Luis Navarro
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@FeatureFlags(featureFlags = @FeatureFlag("LPD-63311"))
@RunWith(Arquillian.class)
public class MCPServerProfileStatusUpgradeProcessTest {

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
	public void tearDown() throws Exception {
		for (ObjectEntry objectEntry :
				new ObjectEntry[] {
					_activeMCPServerProfileObjectEntry,
					_activeMCPServerProfileWithoutToolsObjectEntry,
					_inactiveMCPServerProfileObjectEntry
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
		_activeMCPServerProfileObjectEntry = _addMCPServerProfileObjectEntry(
			RandomTestUtil.randomString(), "active");

		_addObjectEntry(
			"L_MCP_SERVER_PROFILE_TOOL",
			HashMapBuilder.<String, Serializable>put(
				"r_mcpServerProfileToTools_l_mcpServerProfileId",
				_activeMCPServerProfileObjectEntry.getObjectEntryId()
			).put(
				"toolName", "getMCPServerProfilesPage"
			).put(
				"toolSetName", "mcp-server-profiles"
			).build());

		_activeMCPServerProfileWithoutToolsObjectEntry =
			_addMCPServerProfileObjectEntry("My Profile", "active");
		_inactiveMCPServerProfileObjectEntry = _addMCPServerProfileObjectEntry(
			RandomTestUtil.randomString(), "inactive");

		MCPServerTestUtil.processBatchEngineUnits();

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.mcp.server.rest.internal.upgrade." +
				"MCPServerProfileStatusUpgradeProcess");

		upgradeProcess.upgrade();

		_assertUpgrade();

		upgradeProcess.upgrade();

		_assertUpgrade();
	}

	private ObjectEntry _addMCPServerProfileObjectEntry(
			String name, String profileStatus)
		throws Exception {

		return _addObjectEntry(
			"L_MCP_SERVER_PROFILE",
			HashMapBuilder.<String, Serializable>put(
				"description", RandomTestUtil.randomString()
			).put(
				"name", name
			).put(
				"profileStatus", profileStatus
			).build());
	}

	private ObjectEntry _addObjectEntry(
			String externalReferenceCode, Map<String, Serializable> values)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, TestPropsValues.getCompanyId());

		BatchEngineUnitThreadLocal.setFileName(
			"com.liferay.mcp.server.rest.impl_1.0.0 [1]");

		try {
			return _objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null, values, ServiceContextTestUtil.getServiceContext());
		}
		finally {
			BatchEngineUnitThreadLocal.setFileName(StringPool.BLANK);
		}
	}

	private void _assertUpgrade() throws Exception {
		Assert.assertEquals(
			"active", _getProfileStatus(_activeMCPServerProfileObjectEntry));

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			_activeMCPServerProfileWithoutToolsObjectEntry.getObjectEntryId());

		Assert.assertEquals("my-profile", values.get("profilePath"));
		Assert.assertEquals("inactive", values.get("profileStatus"));

		Assert.assertEquals(
			"inactive",
			_getProfileStatus(_inactiveMCPServerProfileObjectEntry));
	}

	private String _getProfileStatus(ObjectEntry mcpServerProfileObjectEntry)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			mcpServerProfileObjectEntry.getObjectEntryId());

		return MapUtil.getString(objectEntry.getValues(), "profileStatus");
	}

	private ObjectEntry _activeMCPServerProfileObjectEntry;
	private ObjectEntry _activeMCPServerProfileWithoutToolsObjectEntry;
	private ObjectEntry _inactiveMCPServerProfileObjectEntry;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.mcp.server.rest.internal.upgrade.registry.MCPServerRestUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}