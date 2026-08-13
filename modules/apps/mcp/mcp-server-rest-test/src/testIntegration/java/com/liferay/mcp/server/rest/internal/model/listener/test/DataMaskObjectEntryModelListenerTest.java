/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mcp.server.rest.test.util.MCPServerTestUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

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
public class DataMaskObjectEntryModelListenerTest {

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
		ObjectEntry defaultMCPServerProfileObjectEntry =
			MCPServerTestUtil.fetchMCPServerProfileObjectEntry("default");

		String defaultMCPServerProfileExternalReferenceCode =
			defaultMCPServerProfileObjectEntry.getExternalReferenceCode();

		List<ObjectEntry> mcpServerProfileDataMaskObjectEntries =
			MCPServerTestUtil.getMCPServerProfileDataMaskObjectEntries(
				defaultMCPServerProfileExternalReferenceCode);

		int mcpServerProfileDataMasksCount =
			mcpServerProfileDataMaskObjectEntries.size();

		_customDataMaskObjectEntry = MCPServerTestUtil.addDataMaskObjectEntry(
			"\\d{4}", RandomTestUtil.randomString(), "[REDACTED]");

		mcpServerProfileDataMaskObjectEntries =
			MCPServerTestUtil.getMCPServerProfileDataMaskObjectEntries(
				defaultMCPServerProfileExternalReferenceCode);

		Assert.assertEquals(
			mcpServerProfileDataMaskObjectEntries.toString(),
			mcpServerProfileDataMasksCount,
			mcpServerProfileDataMaskObjectEntries.size());

		String dataMaskExternalReferenceCode = RandomTestUtil.randomString();

		ObjectEntry systemDataMaskObjectEntry =
			MCPServerTestUtil.addSystemDataMaskObjectEntry(
				"\\d{4}", dataMaskExternalReferenceCode,
				RandomTestUtil.randomString(), "[REDACTED]");

		try {
			Assert.assertEquals(
				mcpServerProfileDataMasksCount + 1,
				MCPServerTestUtil.getMCPServerProfileDataMaskExecutionOrder(
					dataMaskExternalReferenceCode,
					defaultMCPServerProfileExternalReferenceCode));
		}
		finally {
			_deleteSystemDataMaskObjectEntry(systemDataMaskObjectEntry);
		}
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		ObjectEntry customDataMaskObjectEntry =
			MCPServerTestUtil.addDataMaskObjectEntry(
				"\\d{4}", RandomTestUtil.randomString(), "[REDACTED]");
		ObjectEntry mcpServerProfileObjectEntry =
			MCPServerTestUtil.addMCPServerProfileObjectEntry(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				"mcp-server-profiles getMCPServerProfilesPage");

		ObjectEntry mcpServerProfileDataMaskObjectEntry =
			MCPServerTestUtil.addMCPServerProfileDataMaskObjectEntry(
				customDataMaskObjectEntry.getObjectEntryId(), 1,
				mcpServerProfileObjectEntry.getExternalReferenceCode());

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
					customDataMaskObjectEntry.getObjectEntryId());
			}
			finally {
				PermissionThreadLocal.setPermissionChecker(
					originalPermissionChecker);
			}

			Assert.assertEquals(
				"Data mask was deleted.",
				MCPServerTestUtil.getAuditedDeleteReason(
					mcpServerProfileDataMaskObjectEntry));
			Assert.assertNull(
				_objectEntryLocalService.fetchObjectEntry(
					customDataMaskObjectEntry.getObjectEntryId()));
			Assert.assertNull(
				_objectEntryLocalService.fetchObjectEntry(
					mcpServerProfileDataMaskObjectEntry.getObjectEntryId()));
		}
	}

	private void _deleteSystemDataMaskObjectEntry(ObjectEntry objectEntry)
		throws Exception {

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

			MCPServerTestUtil.deleteSystemDataMaskObjectEntry(objectEntry);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@DeleteAfterTestRun
	private ObjectEntry _customDataMaskObjectEntry;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}