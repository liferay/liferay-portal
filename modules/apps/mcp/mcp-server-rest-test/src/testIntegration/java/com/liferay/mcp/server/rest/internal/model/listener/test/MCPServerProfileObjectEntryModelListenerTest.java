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
	public void setUp() throws Exception {
		String dataMaskingPrefix =
			".com.liferay.headless.data.mask.internal.batch.";

		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.headless.data.mask.impl",
			MCPServerProfileObjectEntryModelListenerTest.class,
			new String[] {
				dataMaskingPrefix + "01.list.type.definition",
				dataMaskingPrefix + "02.object.definition",
				dataMaskingPrefix + "03.object.entry"
			});

		String prefix = ".com.liferay.mcp.server.rest.internal.batch.";

		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.mcp.server.rest.impl",
			MCPServerProfileObjectEntryModelListenerTest.class,
			new String[] {
				prefix + "01.object.definition",
				prefix + "02.object.definition",
				prefix + "03.object.definition", prefix + "04.object.entry"
			});
	}

	@Test
	public void testOnAfterCreate() throws Exception {

		// The system masks are seeded on the default profile

		ObjectEntry defaultProfileObjectEntry = _findProfile("default");

		Assert.assertEquals(
			_SYSTEM_MASK_COUNT,
			_countProfileDataMasks(
				defaultProfileObjectEntry.getExternalReferenceCode()));

		// The system masks are auto attached on profile create

		ObjectEntry profileObjectEntry = MCPServerDataMaskTestUtil.addProfile(
			RandomTestUtil.randomString(), "no PII here",
			"mcp-server-profiles getMCPServerProfilesPage");

		Assert.assertEquals(
			_SYSTEM_MASK_COUNT,
			_countProfileDataMasks(
				profileObjectEntry.getExternalReferenceCode()));
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		ObjectEntry profileObjectEntry = MCPServerDataMaskTestUtil.addProfile(
			RandomTestUtil.randomString(), "no PII here",
			"mcp-server-profiles getMCPServerProfilesPage");

		String mcpServerProfileExternalReferenceCode =
			profileObjectEntry.getExternalReferenceCode();

		Assert.assertEquals(
			_SYSTEM_MASK_COUNT,
			_countProfileDataMasks(mcpServerProfileExternalReferenceCode));

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

			_objectEntryLocalService.deleteObjectEntry(profileObjectEntry);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}

		Assert.assertEquals(
			0, _countProfileDataMasks(mcpServerProfileExternalReferenceCode));
	}

	private int _countProfileDataMasks(
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

	private ObjectEntry _findProfile(String name) throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE", TestPropsValues.getCompanyId());

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

	private static final int _SYSTEM_MASK_COUNT = 9;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}