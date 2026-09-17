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
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jose Luis Navarro
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-63311"))
@RunWith(Arquillian.class)
public class MCPProfileStatusUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		MCPServerTestUtil.processBatchEngineUnits();
	}

	@Test
	public void testUpgrade() throws Exception {
		_activeMCPServerProfileObjectEntry =
			MCPServerTestUtil.addMCPServerProfileObjectEntry(
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(),
				"mcp-server-profiles getMCPServerProfilesPage");
		_activeMCPServerProfileWithoutToolsObjectEntry =
			_addActiveMCPServerProfileWithoutToolsObjectEntry();
		_inactiveMCPServerProfileObjectEntry =
			MCPServerTestUtil.addMCPServerProfileObjectEntry(
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString());

		Assert.assertEquals(
			"active",
			_getProfileStatus(_activeMCPServerProfileWithoutToolsObjectEntry));

		UpgradeStep upgradeStep = _getUpgradeStep();

		upgradeStep.upgrade();

		_assertUpgrade();

		upgradeStep.upgrade();

		_assertUpgrade();
	}

	private ObjectEntry _addActiveMCPServerProfileWithoutToolsObjectEntry()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE", TestPropsValues.getCompanyId());

		BatchEngineUnitThreadLocal.setFileName(
			"com.liferay.mcp.server.rest.impl_1.0.0 [1]");

		try {
			return _objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"description", RandomTestUtil.randomString()
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"profileStatus", "active"
				).build(),
				ServiceContextTestUtil.getServiceContext());
		}
		finally {
			BatchEngineUnitThreadLocal.setFileName(StringPool.BLANK);
		}
	}

	private void _assertUpgrade() throws Exception {
		Assert.assertEquals(
			"active", _getProfileStatus(_activeMCPServerProfileObjectEntry));
		Assert.assertEquals(
			"inactive",
			_getProfileStatus(_activeMCPServerProfileWithoutToolsObjectEntry));
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

	private UpgradeStep _getUpgradeStep() {
		List<UpgradeStep> versionedUpgradeSteps = new ArrayList<>();

		_upgradeStepRegistrator.register(
			new UpgradeStepRegistrator.Registry() {

				@Override
				public void register(
					String fromSchemaVersionString,
					String toSchemaVersionString, UpgradeStep... upgradeSteps) {

					if (Objects.equals(fromSchemaVersionString, "1.3.0") &&
						Objects.equals(toSchemaVersionString, "1.4.0")) {

						Collections.addAll(versionedUpgradeSteps, upgradeSteps);
					}
				}

				@Override
				public void registerReleaseCreationUpgradeSteps(
					UpgradeStep... upgradeSteps) {
				}

			});

		return versionedUpgradeSteps.get(0);
	}

	@DeleteAfterTestRun
	private ObjectEntry _activeMCPServerProfileObjectEntry;

	@DeleteAfterTestRun
	private ObjectEntry _activeMCPServerProfileWithoutToolsObjectEntry;

	@DeleteAfterTestRun
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