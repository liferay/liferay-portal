/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mcp.server.rest.test.util.MCPServerTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import java.util.ArrayList;
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
 * @author Jose Luis Navarro
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-63311"))
@RunWith(Arquillian.class)
public class MCPServerProfileObjectEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		MCPServerTestUtil.processBatchEngineUnits();
	}

	@Test
	public void testOnAfterCreate() throws Exception {
		ObjectEntry mcpServerProfileObjectEntry =
			MCPServerTestUtil.fetchMCPServerProfileObjectEntry("default");

		Assert.assertEquals(
			_SYSTEM_DATA_MASK_EXTERNAL_REFERENCE_CODES.length,
			_getMCPServerProfileDataMasksCount(
				mcpServerProfileObjectEntry.getExternalReferenceCode()));

		MCPServerTestUtil.addDataMaskObjectEntry(
			"\\d{4}", RandomTestUtil.randomString(), "[REDACTED]");

		mcpServerProfileObjectEntry =
			MCPServerTestUtil.addMCPServerProfileObjectEntry(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				"mcp-server-profiles getMCPServerProfilesPage");

		List<ObjectEntry> mcpServerProfileDataMaskObjectEntries =
			_getMCPServerProfileDataMaskObjectEntries(
				mcpServerProfileObjectEntry.getExternalReferenceCode());

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

	@Test
	public void testOnBeforeRemove() throws Exception {
		ObjectEntry mcpServerProfileObjectEntry =
			MCPServerTestUtil.addMCPServerProfileObjectEntry(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				"mcp-server-profiles getMCPServerProfilesPage");

		String mcpServerProfileObjectEntryExternalReferenceCode =
			mcpServerProfileObjectEntry.getExternalReferenceCode();

		Assert.assertEquals(
			_SYSTEM_DATA_MASK_EXTERNAL_REFERENCE_CODES.length,
			_getMCPServerProfileDataMasksCount(
				mcpServerProfileObjectEntryExternalReferenceCode));

		ObjectEntry dataMaskObjectEntry =
			MCPServerTestUtil.addDataMaskObjectEntry(
				"\\d{4}", RandomTestUtil.randomString(), "[REDACTED]");

		ObjectEntry mcpServerProfileDataMaskObjectEntry =
			MCPServerTestUtil.addMCPServerProfileDataMaskObjectEntry(
				dataMaskObjectEntry.getObjectEntryId(), 1,
				mcpServerProfileObjectEntryExternalReferenceCode);

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.portal.security.audit.router.configuration." +
						"PersistentAuditMessageProcessorConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"bufferSize", 1
					).put(
						"enabled", true
					).build())) {

			PermissionChecker originalPermissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			try {
				PermissionThreadLocal.setPermissionChecker(
					PermissionCheckerFactoryUtil.create(
						TestPropsValues.getUser()));

				_objectEntryLocalService.deleteObjectEntry(
					mcpServerProfileObjectEntry);
			}
			finally {
				PermissionThreadLocal.setPermissionChecker(
					originalPermissionChecker);
			}

			Assert.assertEquals(
				0,
				_getMCPServerProfileDataMasksCount(
					mcpServerProfileObjectEntryExternalReferenceCode));

			Assert.assertEquals(
				"MCP server profile was deleted.",
				MCPServerTestUtil.getAuditedDeleteReason(
					mcpServerProfileDataMaskObjectEntry));
		}
	}

	private List<ObjectEntry> _getMCPServerProfileDataMaskObjectEntries(
			String mcpServerProfileExternalReferenceCode)
		throws Exception {

		List<ObjectEntry> mcpServerProfileDataMaskObjectEntries =
			new ArrayList<>();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE_DATA_MASK",
					TestPropsValues.getCompanyId());

		for (ObjectEntry objectEntry :
				_objectEntryLocalService.getObjectEntries(
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

	private int _getMCPServerProfileDataMasksCount(
			String mcpServerProfileExternalReferenceCode)
		throws Exception {

		List<ObjectEntry> mcpServerProfileDataMaskObjectEntries =
			_getMCPServerProfileDataMaskObjectEntries(
				mcpServerProfileExternalReferenceCode);

		return mcpServerProfileDataMaskObjectEntries.size();
	}

	private static final String[] _SYSTEM_DATA_MASK_EXTERNAL_REFERENCE_CODES = {
		"L_DATA_MASK_IBAN", "L_DATA_MASK_CREDIT_CARD_NUMBER",
		"L_DATA_MASK_EMAIL_ADDRESS", "L_DATA_MASK_IPV4", "L_DATA_MASK_IPV6",
		"L_DATA_MASK_NATIONAL_ID_BSN", "L_DATA_MASK_NATIONAL_ID_DNI_NIF",
		"L_DATA_MASK_NATIONAL_ID_SSN", "L_DATA_MASK_PHONE_NUMBER"
	};

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}