/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.batch.engine.unit.BatchEngineUnitReader;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.cms.site.initializer.internal.display.context.test.BaseDisplayContextTestCase;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.File;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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
 * @author Alicia García
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class ObjectEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		if (_isCMSSiteInitialized()) {
			return;
		}

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {

			// These tests require the instance to be created with the feature
			// flag LPD-17564 enabled. On CI, feature flags are enabled on
			// demand for each test, but not during instance initialization.
			// Until the feature flag LPD-17564 is removed, run the instance
			// lifecycle initializer manually so that the role is created.

			SiteInitializer siteInitializer =
				_siteInitializerRegistry.getSiteInitializer(
					"com.liferay.site.initializer.cms");

			siteInitializer.initialize(_group.getGroupId());

			Bundle testBundle = FrameworkUtil.getBundle(
				BaseDisplayContextTestCase.class);

			BundleContext bundleContext = testBundle.getBundleContext();

			for (Bundle bundle : bundleContext.getBundles()) {
				if (Objects.equals(
						bundle.getSymbolicName(),
						"com.liferay.site.initializer.cms")) {

					_deleteFile(bundle, "00.list.type.definition");
					_deleteFile(bundle, "01.object.folder");
					_deleteFile(bundle, "02.object.definition");

					CompletableFuture<Void> completableFuture =
						_batchEngineUnitProcessor.processBatchEngineUnits(
							_batchEngineUnitReader.getBatchEngineUnits(bundle));

					completableFuture.join();
				}
			}
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testPublishSystemObjectDefinition() throws Exception {
		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				_getObjectFolderExternalReferenceCode(), _group.getCompanyId());

		ObjectDefinition systemObjectDefinition =
			ObjectDefinitionLocalServiceUtil.addSystemObjectDefinition(
				null, TestPropsValues.getUserId(),
				objectFolder.getObjectFolderId(), null, null, false, true,
				false, true, true, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, "Test", null, null, null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				WorkflowConstants.STATUS_DRAFT, Collections.emptyList(),
				List.of(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())),
				Collections.emptyList());

		systemObjectDefinition =
			_objectDefinitionLocalService.publishSystemObjectDefinition(
				TestPropsValues.getUserId(),
				systemObjectDefinition.getObjectDefinitionId());

		_assertHasResourcePermissionScopeCompany(systemObjectDefinition);
		_assertHasResourcePermissionScopeIndividual(systemObjectDefinition);

		_objectDefinitionLocalService.deleteObjectDefinition(
			systemObjectDefinition.getObjectDefinitionId());
	}

	private void _assertHasResourcePermissionScopeCompany(
			ObjectDefinition objectDefinition)
		throws Exception {

		Role role = _roleLocalService.getRole(
			objectDefinition.getCompanyId(), RoleConstants.CMS_ADMINISTRATOR);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(objectDefinition.getCompanyId()),
				role.getRoleId(), ActionKeys.DELETE));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(objectDefinition.getCompanyId()),
				role.getRoleId(), ActionKeys.PERMISSIONS));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(objectDefinition.getCompanyId()),
				role.getRoleId(), ActionKeys.UPDATE));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(objectDefinition.getCompanyId()),
				role.getRoleId(), ActionKeys.VIEW));
	}

	private void _assertHasResourcePermissionScopeIndividual(
			ObjectDefinition objectDefinition)
		throws Exception {

		Role role = _roleLocalService.getRole(
			objectDefinition.getCompanyId(), RoleConstants.GUEST);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				ObjectDefinition.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectDefinition.getObjectDefinitionId()),
				role.getRoleId(), ActionKeys.VIEW));

		role = _roleLocalService.getRole(
			objectDefinition.getCompanyId(), RoleConstants.USER);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				ObjectDefinition.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectDefinition.getObjectDefinitionId()),
				role.getRoleId(), ActionKeys.VIEW));
	}

	private void _deleteFile(Bundle bundle, String fileName) {
		File file = bundle.getDataFile(
			".com.liferay.site.initializer.cms.internal.batch." + fileName +
				".batch.engine.data.json.0.processed");

		if ((file != null) && file.exists()) {
			file.delete();
		}
	}

	private String _getObjectFolderExternalReferenceCode() {
		if (RandomTestUtil.randomBoolean()) {
			return ObjectFolderConstants.
				EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES;
		}

		return ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES;
	}

	private boolean _isCMSSiteInitialized() {
		ObjectFolder objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES,
				_group.getCompanyId());

		if (objectFolder != null) {
			return true;
		}

		return false;
	}

	@Inject
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	@Inject
	private BatchEngineUnitReader _batchEngineUnitReader;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

}