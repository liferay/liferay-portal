/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.product.navigation.personal.menu.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.batch.engine.unit.BatchEngineUnitReader;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.product.navigation.personal.menu.PersonalMenuEntry;
import com.liferay.site.cms.site.initializer.internal.display.context.test.BaseDisplayContextTestCase;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.File;

import java.util.Collections;
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
 * @author Jürgen Kappler
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class CMSPersonalMenuEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_SPACE,
			new ServiceContext() {
				{
					setCompanyId(TestPropsValues.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		_group = _getGroup();
	}

	@Test
	public void testIsShow() throws Exception {
		_testIsShowAdminUser();
		_testIsShowCMSAdminRole();
		_testIsShowDepotEntryMemberUser();
		_testIsShowWithoutPermissions();
	}

	private void _deleteFile(Bundle bundle, String fileName) {
		File file = bundle.getDataFile(
			".com.liferay.site.initializer.cms.internal.batch." + fileName +
				".batch.engine.data.json.0.processed");

		if ((file != null) && file.exists()) {
			file.delete();
		}
	}

	private Group _getGroup() throws Exception {
		Group group = _groupLocalService.fetchGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		if (group != null) {
			return group;
		}

		group = GroupTestUtil.addGroup();

		group.setGroupKey(GroupConstants.CMS);

		group = _groupLocalService.updateGroup(group);

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		try {

			// These tests require the instance to be created with the feature
			// flag LPD-17564 enabled. On CI, feature flags are enabled on
			// demand for each test, but not during instance initialization.
			// Until the feature flag LPD-17564 is removed, run the instance
			// lifecycle initializer manually so that the role is created.

			SiteInitializer siteInitializer =
				_siteInitializerRegistry.getSiteInitializer(
					"com.liferay.site.initializer.cms");

			siteInitializer.initialize(group.getGroupId());

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

		return group;
	}

	private void _testIsShowAdminUser() throws Exception {
		User user = UserTestUtil.getAdminUser(TestPropsValues.getCompanyId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Assert.assertTrue(
				_personalMenuEntry.isShow(null, permissionChecker));
		}
	}

	private void _testIsShowCMSAdminRole() throws Exception {
		User user = UserTestUtil.addUser();

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.CMS_ADMINISTRATOR);

		_userLocalService.addRoleUser(role.getRoleId(), user);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Assert.assertTrue(
				_personalMenuEntry.isShow(null, permissionChecker));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	private void _testIsShowDepotEntryMemberUser() throws Exception {
		User user = UserTestUtil.addUser();

		_groupLocalService.addUserGroup(
			user.getUserId(), _depotEntry.getGroup());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Assert.assertTrue(
				_personalMenuEntry.isShow(null, permissionChecker));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	private void _testIsShowWithoutPermissions() throws Exception {
		User user = UserTestUtil.addUser();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Assert.assertFalse(
				_personalMenuEntry.isShow(null, permissionChecker));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Inject
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	@Inject
	private BatchEngineUnitReader _batchEngineUnitReader;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.product.navigation.personal.menu.CMSPersonalMenuEntry"
	)
	private PersonalMenuEntry _personalMenuEntry;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

	@Inject
	private UserLocalService _userLocalService;

}