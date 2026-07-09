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
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
	public void testOnBeforeRemove() throws Exception {
		ObjectEntry mcpServerProfileObjectEntry =
			MCPServerTestUtil.addMCPServerProfileObjectEntry(
				RandomTestUtil.randomString(), "no PII here",
				"mcp-server-profiles getMCPServerProfilesPage");

		ObjectEntry customDataMaskObjectEntry =
			MCPServerTestUtil.addDataMaskObjectEntry(
				RandomTestUtil.randomString(), "\\d{4}", "[REDACTED]");

		ObjectEntry mcpServerProfileDataMaskObjectEntry =
			MCPServerTestUtil.addMCPServerProfileDataMaskObjectEntry(
				mcpServerProfileObjectEntry.getExternalReferenceCode(),
				customDataMaskObjectEntry.getObjectEntryId(), 1);

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				MCPServerTestUtil.enableAuditPersistence()) {

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
				"Data mask deleted.",
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

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}