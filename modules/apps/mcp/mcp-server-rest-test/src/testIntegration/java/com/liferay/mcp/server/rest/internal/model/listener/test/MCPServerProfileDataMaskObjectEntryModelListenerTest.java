/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mcp.server.rest.test.util.MCPServerTestUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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
public class MCPServerProfileDataMaskObjectEntryModelListenerTest {

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

		ObjectEntry dataMaskObjectEntry =
			MCPServerTestUtil.fetchDataMaskObjectEntry("Email Address");

		ObjectEntry mcpServerProfileDataMaskObjectEntry =
			MCPServerTestUtil.addMCPServerProfileDataMaskObjectEntry(
				mcpServerProfileObjectEntry.getExternalReferenceCode(),
				dataMaskObjectEntry.getObjectEntryId(), 1);

		AssertUtils.assertFailure(
			ModelListenerException.class,
			"jakarta.validation.ValidationException: Unable to remove a " +
				"profile data mask without a delete reason",
			() -> _objectEntryLocalService.deleteObjectEntry(
				mcpServerProfileDataMaskObjectEntry.getObjectEntryId()));

		Assert.assertNotNull(
			_objectEntryLocalService.fetchObjectEntry(
				mcpServerProfileDataMaskObjectEntry.getObjectEntryId()));

		MCPServerTestUtil.deleteMCPServerProfileDataMaskObjectEntry(
			mcpServerProfileDataMaskObjectEntry, RandomTestUtil.randomString());

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				mcpServerProfileDataMaskObjectEntry.getObjectEntryId()));
	}

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}