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
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
public class MCPProfileToolUpgradeProcessTest {

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
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE_TOOL", _company.getCompanyId());

		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "restrictFields"));

		ObjectEntry mcpServerProfileToolObjectEntry =
			_addMCPServerProfileToolObjectEntry(objectDefinition);

		_upgrade();

		_assertUpgrade(mcpServerProfileToolObjectEntry, objectDefinition);

		_upgrade();

		_assertUpgrade(mcpServerProfileToolObjectEntry, objectDefinition);

		// The batch provisioning of the current bundle must accept the field
		// the upgrade added

		_processBatchEngineUnits(_getMCPServerRestImplBundle());

		_assertUpgrade(mcpServerProfileToolObjectEntry, objectDefinition);
	}

	private ObjectEntry _addMCPServerProfileToolObjectEntry(
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectDefinition mcpServerProfileObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE", _company.getCompanyId());

		ObjectEntry mcpServerProfileObjectEntry = _addObjectEntry(
			mcpServerProfileObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"description", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"profileStatus", "active"
			).build());

		return _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"r_mcpServerProfileToTools_l_mcpServerProfileId",
				mcpServerProfileObjectEntry.getObjectEntryId()
			).put(
				"toolName", "getMCPServerProfilesPage"
			).put(
				"toolSetName", "mcp-server-profiles"
			).build());
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition, Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, _user.getUserId(), objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values,
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _company.getGroupId(),
				_user.getUserId()));
	}

	private void _assertUpgrade(
			ObjectEntry mcpServerProfileToolObjectEntry,
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectField restrictFieldsObjectField =
			_objectFieldLocalService.getObjectField(
				objectDefinition.getObjectDefinitionId(), "restrictFields");

		Assert.assertEquals(
			ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT,
			restrictFieldsObjectField.getBusinessType());
		Assert.assertEquals(
			ObjectFieldConstants.DB_TYPE_CLOB,
			restrictFieldsObjectField.getDBType());
		Assert.assertFalse(restrictFieldsObjectField.isRequired());
		Assert.assertTrue(restrictFieldsObjectField.isSystem());

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			mcpServerProfileToolObjectEntry.getObjectEntryId());

		Assert.assertEquals("getMCPServerProfilesPage", values.get("toolName"));
		Assert.assertEquals("mcp-server-profiles", values.get("toolSetName"));

		String restrictFields = RandomTestUtil.randomString();

		values.put("restrictFields", restrictFields);

		_objectEntryLocalService.updateObjectEntry(
			_user.getUserId(),
			mcpServerProfileToolObjectEntry.getObjectEntryId(),
			mcpServerProfileToolObjectEntry.getObjectEntryFolderId(), values,
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _company.getGroupId(),
				_user.getUserId()));

		values = _objectEntryLocalService.getValues(
			mcpServerProfileToolObjectEntry.getObjectEntryId());

		Assert.assertEquals(restrictFields, values.get("restrictFields"));
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
			MCPProfileToolUpgradeProcessTest.class);

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

					if (!Objects.equals(fromSchemaVersionString, "1.2.0") ||
						!Objects.equals(toSchemaVersionString, "1.3.0")) {

						return;
					}

					for (UpgradeStep upgradeStep : upgradeSteps) {
						Class<?> clazz = upgradeStep.getClass();

						if (Objects.equals(
								clazz.getSimpleName(),
								"MCPProfileToolUpgradeProcess")) {

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
			MCPProfileToolUpgradeProcessTest.class);

		String dirName =
			"com/liferay/mcp/server/rest/internal/upgrade/test/dependencies" +
				"/tool/";

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
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

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