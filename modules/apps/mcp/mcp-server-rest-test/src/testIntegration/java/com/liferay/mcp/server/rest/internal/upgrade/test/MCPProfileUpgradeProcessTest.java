/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.unit.BatchEngineUnit;
import com.liferay.batch.engine.unit.BatchEngineUnitConfiguration;
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.batch.engine.unit.BatchEngineUnitReader;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.net.URL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.After;
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
 * @author Alberto Javier Moreno Lage
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-63311"))
@RunWith(Arquillian.class)
public class MCPProfileUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = CompanyTestUtil.addCompany();
		_originalCompanyId = CompanyThreadLocal.getCompanyId();
		_originalName = PrincipalThreadLocal.getName();
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		CompanyThreadLocal.setCompanyId(_company.getCompanyId());

		_user = UserTestUtil.getAdminUser(_company.getCompanyId());

		UserTestUtil.setUser(_user);

		_deleteObjectDefinition("L_MCP_SERVER_PROFILE");
		_deleteObjectDefinition("L_MCP_SERVER_PROFILE_TOOL");

		Bundle bundle = _installBundle();

		try {
			_processBatchEngineUnits(bundle);
		}
		finally {
			bundle.uninstall();
		}
	}

	@After
	public void tearDown() {
		CompanyThreadLocal.setCompanyId(_originalCompanyId);
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testUpgrade() throws Exception {
		ObjectDefinition objectDefinition = _addObjectEntries();

		_upgrade();

		_assertUpgrade(objectDefinition);

		_upgrade();

		_assertUpgrade(objectDefinition);
	}

	@Test
	public void testUpgradeAfterFailedBatchProvisioning() throws Exception {
		ObjectDefinition objectDefinition = _addObjectEntries();

		try {
			_processBatchEngineUnits(_getMCPServerRestImplBundle());

			Assert.fail();
		}
		catch (Exception exception) {
			String message = exception.getMessage();

			Assert.assertTrue(
				message,
				message.contains(
					"02-object-definition.batch-engine-data.json"));
		}

		Assert.assertNotNull(
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "tools"));

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			_objectEntry1.getObjectEntryId());

		Assert.assertEquals(
			"mcp-server-profiles getMCPServerProfilesPage\nmcp-server-" +
				"profiles postMCPServerProfile",
			values.get("tools"));

		_upgrade();

		_assertUpgrade(objectDefinition);

		_processBatchEngineUnits(_getMCPServerRestImplBundle());

		_assertUpgrade(objectDefinition);
	}

	private ObjectDefinition _addObjectEntries() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE", _company.getCompanyId());

		_objectEntry1 = _addObjectEntry(
			null, objectDefinition,
			"mcp-server-profiles getMCPServerProfilesPage\nmcp-server-" +
				"profiles postMCPServerProfile");
		_objectEntry2 = _addObjectEntry(
			null, objectDefinition,
			"malformed\nmcp-server-v1.0 getToolSetsPage");
		_objectEntry3 = _addObjectEntry(
			"L_MCP_SERVER_DEFAULT_PROFILE", objectDefinition,
			StringBundler.concat(
				"mcp-server-v1.0 getToolSetsPage\nmcp-server-v1.0 ",
				"getToolSetToolSetNameToolSummariesPage\nmcp-server-v1.0 ",
				"getToolSetToolSetNameTool\nmcp-server-v1.0 ",
				"postToolSetToolSetNameToolInvoke"));

		return objectDefinition;
	}

	private ObjectEntry _addObjectEntry(
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String tools)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, _user.getUserId(), objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"description", RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", () -> externalReferenceCode
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"tools", tools
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _company.getGroupId(),
				_user.getUserId()));
	}

	private void _assertActiveProfileStatus(ObjectEntry objectEntry)
		throws Exception {

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			objectEntry.getObjectEntryId());

		Assert.assertEquals("active", values.get("profileStatus"));
	}

	private void _assertMCPServerProfileToolObjectEntries(
			ObjectEntry mcpServerProfileObjectEntry,
			ObjectRelationship objectRelationship, String... expectedTools)
		throws Exception {

		List<ObjectEntry> mcpServerProfileToolObjectEntries =
			_objectEntryLocalService.getOneToManyObjectEntries(
				0, objectRelationship.getObjectRelationshipId(), null, false,
				mcpServerProfileObjectEntry.getObjectEntryId(), true, null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			mcpServerProfileToolObjectEntries.toString(), expectedTools.length,
			mcpServerProfileToolObjectEntries.size());

		Set<String> tools = new HashSet<>();

		for (ObjectEntry mcpServerProfileToolObjectEntry :
				mcpServerProfileToolObjectEntries) {

			Map<String, Serializable> values =
				mcpServerProfileToolObjectEntry.getValues();

			tools.add(
				values.get("toolSetName") + StringPool.SPACE +
					values.get("toolName"));
		}

		for (String expectedTool : expectedTools) {
			Assert.assertTrue(tools.toString(), tools.contains(expectedTool));
		}
	}

	private void _assertMCPServerProfileToolObjectEntry(
		String externalReferenceCode, ObjectDefinition objectDefinition) {

		Assert.assertNotNull(
			externalReferenceCode,
			_objectEntryLocalService.fetchObjectEntry(
				externalReferenceCode, 0,
				objectDefinition.getObjectDefinitionId()));
	}

	private void _assertUpgrade(ObjectDefinition objectDefinition)
		throws Exception {

		ObjectDefinition mcpServerProfileToolObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE_TOOL", _company.getCompanyId());

		Assert.assertTrue(mcpServerProfileToolObjectDefinition.isApproved());
		Assert.assertTrue(mcpServerProfileToolObjectDefinition.isModifiable());
		Assert.assertTrue(mcpServerProfileToolObjectDefinition.isSystem());

		ObjectField toolNameObjectField =
			_objectFieldLocalService.getObjectField(
				mcpServerProfileToolObjectDefinition.getObjectDefinitionId(),
				"toolName");

		Assert.assertTrue(toolNameObjectField.isRequired());

		ObjectField toolSetNameObjectField =
			_objectFieldLocalService.getObjectField(
				mcpServerProfileToolObjectDefinition.getObjectDefinitionId(),
				"toolSetName");

		Assert.assertTrue(toolSetNameObjectField.isRequired());

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				fetchObjectRelationshipByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE_TO_L_MCP_SERVER_PROFILE_TOOL",
					_company.getCompanyId(),
					objectDefinition.getObjectDefinitionId());

		Assert.assertEquals(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			objectRelationship.getDeletionType());
		Assert.assertTrue(objectRelationship.isSystem());

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			mcpServerProfileToolObjectDefinition.getObjectDefinitionId(),
			"r_mcpServerProfileToTools_l_mcpServerProfileId");

		Assert.assertTrue(objectField.isRequired());

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				objectField.getObjectFieldId(),
				"objectRelationshipERCObjectFieldName");

		Assert.assertEquals(
			"r_mcpServerProfileToTools_l_mcpServerProfileERC",
			objectFieldSetting.getValue());

		_assertMCPServerProfileToolObjectEntries(
			_objectEntry1, objectRelationship,
			"mcp-server-profiles getMCPServerProfilesPage",
			"mcp-server-profiles postMCPServerProfile");
		_assertMCPServerProfileToolObjectEntries(
			_objectEntry2, objectRelationship,
			"mcp-server-v1.0 getToolSetsPage");
		_assertMCPServerProfileToolObjectEntries(
			_objectEntry3, objectRelationship,
			"mcp-server-v1.0 getToolSetsPage",
			"mcp-server-v1.0 getToolSetToolSetNameToolSummariesPage",
			"mcp-server-v1.0 getToolSetToolSetNameTool",
			"mcp-server-v1.0 postToolSetToolSetNameToolInvoke");

		_assertMCPServerProfileToolObjectEntry(
			"L_MCP_SERVER_DEFAULT_PROFILE_TOOL_GET_TOOL",
			mcpServerProfileToolObjectDefinition);
		_assertMCPServerProfileToolObjectEntry(
			"L_MCP_SERVER_DEFAULT_PROFILE_TOOL_GET_TOOL_SETS_PAGE",
			mcpServerProfileToolObjectDefinition);
		_assertMCPServerProfileToolObjectEntry(
			"L_MCP_SERVER_DEFAULT_PROFILE_TOOL_GET_TOOL_SUMMARIES_PAGE",
			mcpServerProfileToolObjectDefinition);
		_assertMCPServerProfileToolObjectEntry(
			"L_MCP_SERVER_DEFAULT_PROFILE_TOOL_INVOKE",
			mcpServerProfileToolObjectDefinition);

		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "tools"));

		ObjectField profileStatusObjectField =
			_objectFieldLocalService.getObjectField(
				objectDefinition.getObjectDefinitionId(), "profileStatus");

		Assert.assertEquals(
			ObjectFieldConstants.BUSINESS_TYPE_PICKLIST,
			profileStatusObjectField.getBusinessType());
		Assert.assertTrue(profileStatusObjectField.isRequired());
		Assert.assertTrue(profileStatusObjectField.isSystem());

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.getListTypeDefinition(
				profileStatusObjectField.getListTypeDefinitionId());

		Assert.assertEquals(
			"L_MCP_STATUS", listTypeDefinition.getExternalReferenceCode());

		ObjectFieldSetting defaultValueObjectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				profileStatusObjectField.getObjectFieldId(), "defaultValue");

		Assert.assertEquals(
			"inactive", defaultValueObjectFieldSetting.getValue());

		_assertActiveProfileStatus(_objectEntry1);
		_assertActiveProfileStatus(_objectEntry2);
		_assertActiveProfileStatus(_objectEntry3);
	}

	private void _deleteObjectDefinition(String externalReferenceCode)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, _company.getCompanyId());

		if (objectDefinition != null) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	private Bundle _getMCPServerRestImplBundle() {
		Bundle bundle = FrameworkUtil.getBundle(
			MCPProfileUpgradeProcessTest.class);

		return BundleUtil.getBundle(
			bundle.getBundleContext(), "com.liferay.mcp.server.rest.impl");
	}

	private UpgradeStep _getVersionedUpgradeStep() {
		List<UpgradeStep> versionedUpgradeSteps = new ArrayList<>();

		_upgradeStepRegistrator.register(
			new UpgradeStepRegistrator.Registry() {

				@Override
				public void register(
					String fromSchemaVersionString,
					String toSchemaVersionString, UpgradeStep... upgradeSteps) {

					if (!Objects.equals(fromSchemaVersionString, "1.1.0") ||
						!Objects.equals(toSchemaVersionString, "1.2.0")) {

						return;
					}

					for (UpgradeStep upgradeStep : upgradeSteps) {
						Class<?> clazz = upgradeStep.getClass();

						if (Objects.equals(
								clazz.getSimpleName(),
								"MCPProfileUpgradeProcess")) {

							versionedUpgradeSteps.add(upgradeStep);
						}
					}
				}

				@Override
				public void registerReleaseCreationUpgradeSteps(
					UpgradeStep... upgradeSteps) {
				}

			});

		return versionedUpgradeSteps.get(0);
	}

	private Bundle _installBundle() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			MCPProfileUpgradeProcessTest.class);

		String dirName =
			"com/liferay/mcp/server/rest/internal/upgrade/test/dependencies" +
				"/profile/";

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

	private void _processBatchEngineUnits(Bundle bundle) throws Exception {
		_batchEngineUnitProcessor.processBatchEngineUnits(
			TransformUtil.transform(
				_batchEngineUnitReader.getBatchEngineUnits(bundle),
				batchEngineUnit -> _toCompanyBatchEngineUnit(batchEngineUnit)));
	}

	private BatchEngineUnit _toCompanyBatchEngineUnit(
		BatchEngineUnit batchEngineUnit) {

		return new BatchEngineUnit() {

			@Override
			public BatchEngineUnitConfiguration
					getBatchEngineUnitConfiguration()
				throws IOException {

				BatchEngineUnitConfiguration batchEngineUnitConfiguration =
					batchEngineUnit.getBatchEngineUnitConfiguration();

				batchEngineUnitConfiguration.setCompanyId(
					_company.getCompanyId());
				batchEngineUnitConfiguration.setUserId(_user.getUserId());

				return batchEngineUnitConfiguration;
			}

			@Override
			public InputStream getConfigurationInputStream()
				throws IOException {

				return batchEngineUnit.getConfigurationInputStream();
			}

			@Override
			public String getDataFileName() {
				return batchEngineUnit.getDataFileName();
			}

			@Override
			public InputStream getDataInputStream() throws IOException {
				return batchEngineUnit.getDataInputStream();
			}

			@Override
			public String getFileName() {
				return batchEngineUnit.getFileName();
			}

			@Override
			public boolean isValid() {
				return batchEngineUnit.isValid();
			}

		};
	}

	private void _upgrade() throws Exception {
		UpgradeStep upgradeStep = _getVersionedUpgradeStep();

		upgradeStep.upgrade();

		// UpgradeExecutor clears the caches after a bundle's upgrade steps

		CacheRegistryUtil.clear();
	}

	@Inject
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	@Inject
	private BatchEngineUnitReader _batchEngineUnitReader;

	@DeleteAfterTestRun
	private Company _company;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectEntry _objectEntry1;
	private ObjectEntry _objectEntry2;
	private ObjectEntry _objectEntry3;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private long _originalCompanyId;
	private String _originalName;
	private PermissionChecker _originalPermissionChecker;

	@Inject(
		filter = "component.name=com.liferay.mcp.server.rest.internal.upgrade.registry.MCPServerRestUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

	private User _user;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}