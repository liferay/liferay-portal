/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.test.util.BatchEngineTestUtil;
import com.liferay.mcp.server.rest.test.util.MCPServerDataMaskTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import java.util.Map;

import org.hamcrest.CoreMatchers;

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
	public void setUp() throws Exception {
		String dataMaskingPrefix =
			".com.liferay.headless.data.mask.internal.batch.";

		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.headless.data.mask.impl",
			MCPServerProfileDataMaskObjectEntryModelListenerTest.class,
			new String[] {
				dataMaskingPrefix + "01.list.type.definition",
				dataMaskingPrefix + "02.object.definition",
				dataMaskingPrefix + "03.object.entry"
			});

		String prefix = ".com.liferay.mcp.server.rest.internal.batch.";

		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.mcp.server.rest.impl",
			MCPServerProfileDataMaskObjectEntryModelListenerTest.class,
			new String[] {
				prefix + "01.object.definition",
				prefix + "02.object.definition",
				prefix + "03.object.definition", prefix + "04.object.entry"
			});
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		ObjectEntry profileObjectEntry = MCPServerDataMaskTestUtil.addProfile(
			RandomTestUtil.randomString(), "no PII here",
			"mcp-server-profiles getMCPServerProfilesPage");

		ObjectEntry emailMaskObjectEntry = _findSystemMask("Email Address");

		ObjectEntry profileDataMaskObjectEntry =
			MCPServerDataMaskTestUtil.addProfileDataMask(
				profileObjectEntry.getExternalReferenceCode(),
				emailMaskObjectEntry.getObjectEntryId(), 1);

		try {
			_objectEntryLocalService.deleteObjectEntry(
				profileDataMaskObjectEntry.getObjectEntryId());

			Assert.fail(
				"Removing a profile data mask without a delete reason should " +
					"have thrown");
		}
		catch (Exception exception) {
			Assert.assertThat(
				exception.getMessage(),
				CoreMatchers.containsString("delete reason"));
		}

		Assert.assertNotNull(
			_objectEntryLocalService.fetchObjectEntry(
				profileDataMaskObjectEntry.getObjectEntryId()));

		MCPServerDataMaskTestUtil.removeProfileDataMask(
			profileDataMaskObjectEntry, RandomTestUtil.randomString());

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				profileDataMaskObjectEntry.getObjectEntryId()));
	}

	private ObjectEntry _findSystemMask(String name) throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_MASK", TestPropsValues.getCompanyId());

		if (objectDefinition == null) {
			return null;
		}

		for (ObjectEntry objectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, objectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values = objectEntry.getValues();

			if (name.equals(values.get("name"))) {
				return objectEntry;
			}
		}

		return null;
	}

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}