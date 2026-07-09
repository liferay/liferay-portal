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
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

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
			_SYSTEM_MASK_COUNT,
			_getMCPServerProfileDataMasksCount(
				mcpServerProfileObjectEntry.getExternalReferenceCode()));

		mcpServerProfileObjectEntry =
			MCPServerTestUtil.addMCPServerProfileObjectEntry(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				"mcp-server-profiles getMCPServerProfilesPage");

		Assert.assertEquals(
			_SYSTEM_MASK_COUNT,
			_getMCPServerProfileDataMasksCount(
				mcpServerProfileObjectEntry.getExternalReferenceCode()));
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
			_SYSTEM_MASK_COUNT,
			_getMCPServerProfileDataMasksCount(
				mcpServerProfileObjectEntryExternalReferenceCode));

		ObjectEntry dataMaskObjectEntry =
			MCPServerTestUtil.addDataMaskObjectEntry(
				RandomTestUtil.randomString(), "\\d{4}", "[REDACTED]");

		ObjectEntry mcpServerProfileDataMaskObjectEntry =
			MCPServerTestUtil.addMCPServerProfileDataMaskObjectEntry(
				mcpServerProfileObjectEntryExternalReferenceCode,
				dataMaskObjectEntry.getObjectEntryId(), 1);

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.portal.security.audit.router.configuration." +
						"PersistentAuditMessageProcessorConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", true
					).put(
						"flushInterval", 1
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
				"Profile deleted.",
				MCPServerTestUtil.getAuditedDeleteReason(
					mcpServerProfileDataMaskObjectEntry));
		}
	}

	private int _getMCPServerProfileDataMasksCount(
			String mcpServerProfileExternalReferenceCode)
		throws Exception {

		ObjectDefinition profileDataMaskObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE_DATA_MASK",
					TestPropsValues.getCompanyId());

		int count = 0;

		for (ObjectEntry profileDataMaskObjectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, profileDataMaskObjectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values =
				profileDataMaskObjectEntry.getValues();

			if (Objects.equals(
					mcpServerProfileExternalReferenceCode,
					values.get("mcpServerProfileExternalReferenceCode"))) {

				count++;
			}
		}

		return count;
	}

	private static final int _SYSTEM_MASK_COUNT = 9;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}