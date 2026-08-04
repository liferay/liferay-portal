/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.batch.engine.unit.BatchEngineUnitReader;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Jaime León
 */
@RunWith(Arquillian.class)
public class MCPPromptUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROMPT", TestPropsValues.getCompanyId());

		if (objectDefinition != null) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					"L_MCP_STATUS", TestPropsValues.getCompanyId());

		if (listTypeDefinition != null) {
			_listTypeDefinitionLocalService.deleteListTypeDefinition(
				listTypeDefinition);
		}

		Bundle bundle = _installBundle();

		try {
			_batchEngineUnitProcessor.processBatchEngineUnits(
				_batchEngineUnitReader.getBatchEngineUnits(bundle));
		}
		finally {
			bundle.uninstall();
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROMPT", TestPropsValues.getCompanyId());

		_objectEntry1 = _addObjectEntry(objectDefinition, "My Prompt");
		_objectEntry2 = _addObjectEntry(objectDefinition, "My- Prompt!");

		UpgradeStep upgradeStep = _getReleaseCreationUpgradeStep();

		upgradeStep.upgrade();

		_assertUpgrade(objectDefinition);

		upgradeStep.upgrade();

		_assertUpgrade(objectDefinition);
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition, String name)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"description", RandomTestUtil.randomString()
			).put(
				"name", name
			).put(
				"prompt", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertUpgrade(ObjectDefinition objectDefinition)
		throws Exception {

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.
				getListTypeDefinitionByExternalReferenceCode(
					"L_MCP_STATUS", TestPropsValues.getCompanyId());

		Assert.assertTrue(listTypeDefinition.isSystem());

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.getListTypeEntry(
				listTypeDefinition.getListTypeDefinitionId(), "active");

		Assert.assertEquals("ACTIVE", listTypeEntry.getExternalReferenceCode());
		Assert.assertTrue(listTypeEntry.isSystem());

		listTypeEntry = _listTypeEntryLocalService.getListTypeEntry(
			listTypeDefinition.getListTypeDefinitionId(), "inactive");

		Assert.assertEquals(
			"INACTIVE", listTypeEntry.getExternalReferenceCode());
		Assert.assertTrue(listTypeEntry.isSystem());

		ObjectField identifierObjectField =
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "identifier");

		Assert.assertTrue(identifierObjectField.isRequired());

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				identifierObjectField.getObjectFieldId(),
				ObjectFieldSettingConstants.NAME_UNIQUE_VALUES);

		Assert.assertEquals("true", objectFieldSetting.getValue());

		ObjectField promptStatusObjectField =
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "promptStatus");

		Assert.assertEquals(
			ObjectFieldConstants.BUSINESS_TYPE_PICKLIST,
			promptStatusObjectField.getBusinessType());
		Assert.assertEquals(
			listTypeDefinition.getListTypeDefinitionId(),
			promptStatusObjectField.getListTypeDefinitionId());
		Assert.assertTrue(promptStatusObjectField.isRequired());

		Assert.assertNotNull(
			_objectValidationRuleLocalService.fetchObjectValidationRule(
				"L_MCP_SERVER_PROMPT_VALID_IDENTIFIER",
				objectDefinition.getObjectDefinitionId()));

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			_objectEntry1.getObjectEntryId());

		Assert.assertEquals("my-prompt", values.get("identifier"));
		Assert.assertEquals("active", values.get("promptStatus"));

		values = _objectEntryLocalService.getValues(
			_objectEntry2.getObjectEntryId());

		Assert.assertEquals("my-prompt-2", values.get("identifier"));
		Assert.assertEquals("active", values.get("promptStatus"));
	}

	private UpgradeStep _getReleaseCreationUpgradeStep() {
		List<UpgradeStep> releaseCreationUpgradeSteps = new ArrayList<>();

		_upgradeStepRegistrator.register(
			new UpgradeStepRegistrator.Registry() {

				@Override
				public void register(
					String fromSchemaVersionString,
					String toSchemaVersionString, UpgradeStep... upgradeSteps) {
				}

				@Override
				public void registerReleaseCreationUpgradeSteps(
					UpgradeStep... upgradeSteps) {

					Collections.addAll(
						releaseCreationUpgradeSteps, upgradeSteps);
				}

			});

		return releaseCreationUpgradeSteps.get(0);
	}

	private Bundle _installBundle() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			MCPPromptUpgradeProcessTest.class);

		String dirName =
			"com/liferay/mcp/server/rest/internal/upgrade/test/dependencies" +
				"/batch/";

		Enumeration<URL> enumeration = bundle.findEntries(dirName, "*", true);

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				String urlString = url.getPath();

				if (urlString.endsWith(StringPool.SLASH)) {
					continue;
				}

				String name = urlString.substring(dirName.length());

				if (name.startsWith(StringPool.SLASH)) {
					name = name.substring(1);
				}

				try (InputStream inputStream = url.openStream()) {
					zipWriter.addEntry(name, inputStream);
				}
			}
		}

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.installBundle(
			RandomTestUtil.randomString(),
			new FileInputStream(zipWriter.getFile()));
	}

	@Inject
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	@Inject
	private BatchEngineUnitReader _batchEngineUnitReader;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private ObjectEntry _objectEntry1;

	@DeleteAfterTestRun
	private ObjectEntry _objectEntry2;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

	@Inject(
		filter = "component.name=com.liferay.mcp.server.rest.internal.upgrade.registry.MCPServerRestUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}