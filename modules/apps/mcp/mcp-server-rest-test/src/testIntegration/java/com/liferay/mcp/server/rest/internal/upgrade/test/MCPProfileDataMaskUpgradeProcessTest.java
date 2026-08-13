/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mcp.server.rest.test.util.MCPServerTestUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.ArrayUtil;
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
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Javier Moreno Lage
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-63311"))
@RunWith(Arquillian.class)
public class MCPProfileDataMaskUpgradeProcessTest {

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
		_mcpServerProfileObjectEntry =
			MCPServerTestUtil.addMCPServerProfileObjectEntry(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				"mcp-server-profiles getMCPServerProfilesPage");

		String mcpServerProfileExternalReferenceCode =
			_mcpServerProfileObjectEntry.getExternalReferenceCode();

		_deleteMCPServerProfileDataMaskObjectEntries(
			_SYSTEM_DATA_MASK_EXTERNAL_REFERENCE_CODES,
			mcpServerProfileExternalReferenceCode);

		List<ObjectEntry> mcpServerProfileDataMaskObjectEntries =
			MCPServerTestUtil.getMCPServerProfileDataMaskObjectEntries(
				mcpServerProfileExternalReferenceCode);

		Assert.assertEquals(
			mcpServerProfileDataMaskObjectEntries.toString(), 0,
			mcpServerProfileDataMaskObjectEntries.size());

		ObjectEntry defaultMCPServerProfileObjectEntry =
			MCPServerTestUtil.fetchMCPServerProfileObjectEntry("default");

		UpgradeStep upgradeStep = _getUpgradeStep();

		upgradeStep.upgrade();

		_assertUpgrade(mcpServerProfileExternalReferenceCode);
		_assertUpgrade(
			defaultMCPServerProfileObjectEntry.getExternalReferenceCode());

		upgradeStep.upgrade();

		_assertUpgrade(mcpServerProfileExternalReferenceCode);
		_assertUpgrade(
			defaultMCPServerProfileObjectEntry.getExternalReferenceCode());

		String[] dataMaskExternalReferenceCodes = {
			"L_DATA_MASK_IBAN", "L_DATA_MASK_IPV4", "L_DATA_MASK_IPV6"
		};

		_deleteMCPServerProfileDataMaskObjectEntries(
			dataMaskExternalReferenceCodes,
			mcpServerProfileExternalReferenceCode);

		upgradeStep.upgrade();

		mcpServerProfileDataMaskObjectEntries =
			MCPServerTestUtil.getMCPServerProfileDataMaskObjectEntries(
				mcpServerProfileExternalReferenceCode);

		Assert.assertEquals(
			mcpServerProfileDataMaskObjectEntries.toString(),
			_SYSTEM_DATA_MASK_EXTERNAL_REFERENCE_CODES.length,
			mcpServerProfileDataMaskObjectEntries.size());

		int executionOrder = _SYSTEM_DATA_MASK_EXTERNAL_REFERENCE_CODES.length;

		for (String dataMaskExternalReferenceCode :
				dataMaskExternalReferenceCodes) {

			executionOrder++;

			Assert.assertEquals(
				executionOrder,
				MCPServerTestUtil.getMCPServerProfileDataMaskExecutionOrder(
					dataMaskExternalReferenceCode,
					mcpServerProfileExternalReferenceCode));
		}
	}

	private void _assertUpgrade(String mcpServerProfileExternalReferenceCode)
		throws Exception {

		List<ObjectEntry> mcpServerProfileDataMaskObjectEntries =
			MCPServerTestUtil.getMCPServerProfileDataMaskObjectEntries(
				mcpServerProfileExternalReferenceCode);

		Assert.assertEquals(
			mcpServerProfileDataMaskObjectEntries.toString(),
			_SYSTEM_DATA_MASK_EXTERNAL_REFERENCE_CODES.length,
			mcpServerProfileDataMaskObjectEntries.size());

		for (ObjectEntry mcpServerProfileDataMaskObjectEntry :
				mcpServerProfileDataMaskObjectEntries) {

			Map<String, Serializable> values =
				mcpServerProfileDataMaskObjectEntry.getValues();

			int executionOrder = MapUtil.getInteger(values, "executionOrder");

			Assert.assertEquals(
				_SYSTEM_DATA_MASK_EXTERNAL_REFERENCE_CODES[executionOrder - 1],
				values.get("dataMaskExternalReferenceCode"));
		}
	}

	private void _deleteMCPServerProfileDataMaskObjectEntries(
			String[] dataMaskExternalReferenceCodes,
			String mcpServerProfileExternalReferenceCode)
		throws Exception {

		for (ObjectEntry mcpServerProfileDataMaskObjectEntry :
				MCPServerTestUtil.getMCPServerProfileDataMaskObjectEntries(
					mcpServerProfileExternalReferenceCode)) {

			if (ArrayUtil.contains(
					dataMaskExternalReferenceCodes,
					MapUtil.getString(
						mcpServerProfileDataMaskObjectEntry.getValues(),
						"dataMaskExternalReferenceCode"))) {

				MCPServerTestUtil.deleteMCPServerProfileDataMaskObjectEntry(
					"Deleted by test.", mcpServerProfileDataMaskObjectEntry);
			}
		}
	}

	private UpgradeStep _getUpgradeStep() {
		List<UpgradeStep> versionedUpgradeSteps = new ArrayList<>();

		_upgradeStepRegistrator.register(
			new UpgradeStepRegistrator.Registry() {

				@Override
				public void register(
					String fromSchemaVersionString,
					String toSchemaVersionString, UpgradeStep... upgradeSteps) {

					if (Objects.equals(fromSchemaVersionString, "1.0.0") &&
						Objects.equals(toSchemaVersionString, "1.1.0")) {

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

	private static final String[] _SYSTEM_DATA_MASK_EXTERNAL_REFERENCE_CODES = {
		"L_DATA_MASK_IBAN", "L_DATA_MASK_CREDIT_CARD_NUMBER",
		"L_DATA_MASK_EMAIL_ADDRESS", "L_DATA_MASK_IPV4", "L_DATA_MASK_IPV6",
		"L_DATA_MASK_NATIONAL_ID_BSN", "L_DATA_MASK_NATIONAL_ID_DNI_NIF",
		"L_DATA_MASK_NATIONAL_ID_SSN", "L_DATA_MASK_PHONE_NUMBER"
	};

	@DeleteAfterTestRun
	private ObjectEntry _mcpServerProfileObjectEntry;

	@Inject(
		filter = "component.name=com.liferay.mcp.server.rest.internal.upgrade.registry.MCPServerRestUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}