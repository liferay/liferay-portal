/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.unit.BatchEngineUnitThreadLocal;
import com.liferay.mcp.server.rest.test.util.MCPServerTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

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
			_getMCPServerProfileDataMaskObjectEntriesCount(
				mcpServerProfileObjectEntry.getExternalReferenceCode()));

		MCPServerTestUtil.addDataMaskObjectEntry(
			"\\d{4}", RandomTestUtil.randomString(), "[REDACTED]");

		mcpServerProfileObjectEntry =
			MCPServerTestUtil.addMCPServerProfileObjectEntry(
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(),
				"mcp-server-profiles getMCPServerProfilesPage");

		List<ObjectEntry> mcpServerProfileDataMaskObjectEntries =
			MCPServerTestUtil.getMCPServerProfileDataMaskObjectEntries(
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
	public void testOnBeforeCreate() throws Exception {
		_assertNoAssociatedToolsFailure(
			() -> _addMCPServerProfileObjectEntry());

		BatchEngineUnitThreadLocal.setFileName(
			"com.liferay.mcp.server.rest.impl_1.0.0 [1]");

		try {
			ObjectEntry mcpServerProfileObjectEntry =
				_addMCPServerProfileObjectEntry();

			Assert.assertEquals(
				"active",
				MapUtil.getString(
					mcpServerProfileObjectEntry.getValues(), "profileStatus"));
		}
		finally {
			BatchEngineUnitThreadLocal.setFileName(StringPool.BLANK);
		}
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		ObjectEntry mcpServerProfileObjectEntry =
			MCPServerTestUtil.addMCPServerProfileObjectEntry(
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(),
				"mcp-server-profiles getMCPServerProfilesPage");

		String mcpServerProfileObjectEntryExternalReferenceCode =
			mcpServerProfileObjectEntry.getExternalReferenceCode();

		Assert.assertEquals(
			_SYSTEM_DATA_MASK_EXTERNAL_REFERENCE_CODES.length,
			_getMCPServerProfileDataMaskObjectEntriesCount(
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
				_getMCPServerProfileDataMaskObjectEntriesCount(
					mcpServerProfileObjectEntryExternalReferenceCode));

			Assert.assertEquals(
				"MCP server profile was deleted.",
				MCPServerTestUtil.getAuditedDeleteReason(
					mcpServerProfileDataMaskObjectEntry));
		}
	}

	@Test
	public void testOnBeforeUpdate() throws Exception {
		ObjectEntry mcpServerProfileObjectEntry =
			MCPServerTestUtil.addMCPServerProfileObjectEntry(
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString());

		Assert.assertEquals(
			"inactive",
			MapUtil.getString(
				mcpServerProfileObjectEntry.getValues(), "profileStatus"));

		_assertNoAssociatedToolsFailure(
			() -> MCPServerTestUtil.updateMCPServerProfileStatus(
				mcpServerProfileObjectEntry, "active"));
	}

	private ObjectEntry _addMCPServerProfileObjectEntry() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE", TestPropsValues.getCompanyId());

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
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

	private void _assertNoAssociatedToolsFailure(
		UnsafeRunnable<Exception> unsafeRunnable) {

		AssertUtils.assertFailure(
			ModelListenerException.class,
			StringBundler.concat(
				"com.liferay.object.exception.ObjectEntryValuesException$",
				"InvalidObjectField: MCP server profile has no associated ",
				"tools"),
			unsafeRunnable);
	}

	private int _getMCPServerProfileDataMaskObjectEntriesCount(
			String mcpServerProfileExternalReferenceCode)
		throws Exception {

		List<ObjectEntry> mcpServerProfileDataMaskObjectEntries =
			MCPServerTestUtil.getMCPServerProfileDataMaskObjectEntries(
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