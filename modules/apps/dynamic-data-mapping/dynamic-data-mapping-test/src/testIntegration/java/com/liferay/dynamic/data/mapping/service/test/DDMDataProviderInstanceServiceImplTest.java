/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceService;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mateus Xavier
 */
@RunWith(Arquillian.class)
public class DDMDataProviderInstanceServiceImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);
		serviceContext.setModelPermissions(
			ModelPermissionsFactory.create(
				new String[] {ActionKeys.VIEW}, null));

		_ddmDataProviderInstance =
			_ddmDataProviderInstanceLocalService.addDataProviderInstance(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomLocaleStringMap(), null,
				DDMFormValuesTestUtil.createDDMFormValuesWithRandomValues(
					DDMFormTestUtil.createDDMForm("password", "username")),
				"rest", serviceContext);
	}

	@Test
	@TestInfo("LPD-110121")
	public void testGetDataProviderInstance() throws Exception {
		_testGetDataProviderInstance();
		_testGetDataProviderInstanceWithoutUpdatePermission();
	}

	@Test
	@TestInfo("LPD-110121")
	public void testGetDataProviderInstances() throws Exception {
		_testGetDataProviderInstances();
		_testGetDataProviderInstancesWithoutUpdatePermission();
	}

	@Test
	@TestInfo("LPD-110121")
	public void testSearch() throws Exception {
		_testSearch();
		_testSearchWithoutUpdatePermission();
	}

	private void _assertDDMDataProviderInstance(
			boolean containsAuthenticationData,
			DDMDataProviderInstance ddmDataProviderInstance)
		throws Exception {

		JSONObject definitionJSONObject = JSONFactoryUtil.createJSONObject(
			ddmDataProviderInstance.getDefinition());

		Set<String> names = JSONUtil.toStringSet(
			definitionJSONObject.getJSONArray("fieldValues"), "name");

		Assert.assertEquals(
			containsAuthenticationData, names.contains("password"));
		Assert.assertEquals(
			containsAuthenticationData, names.contains("username"));
	}

	private void _assertDDMDataProviderInstances(
			boolean containsAuthenticationData,
			List<DDMDataProviderInstance> ddmDataProviderInstances)
		throws Exception {

		AssertUtils.assertEquals(
			Collections.singletonList(_ddmDataProviderInstance),
			ddmDataProviderInstances);

		DDMDataProviderInstance ddmDataProviderInstance =
			ddmDataProviderInstances.get(0);

		_assertDDMDataProviderInstance(
			containsAuthenticationData, ddmDataProviderInstance);
	}

	private void _testGetDataProviderInstance() throws Exception {
		User user = UserTestUtil.addGroupAdminUser(_group);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertDDMDataProviderInstance(
				true,
				_ddmDataProviderInstanceService.fetchDataProviderInstance(
					_ddmDataProviderInstance.getDataProviderInstanceId()));
			_assertDDMDataProviderInstance(
				true,
				_ddmDataProviderInstanceService.getDataProviderInstance(
					_ddmDataProviderInstance.getDataProviderInstanceId()));
		}
	}

	private void _testGetDataProviderInstanceWithoutUpdatePermission()
		throws Exception {

		User user = UserTestUtil.addGroupUser(
			_group, RoleConstants.SITE_MEMBER);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertDDMDataProviderInstance(
				false,
				_ddmDataProviderInstanceService.fetchDataProviderInstance(
					_ddmDataProviderInstance.getDataProviderInstanceId()));
			_assertDDMDataProviderInstance(
				false,
				_ddmDataProviderInstanceService.getDataProviderInstance(
					_ddmDataProviderInstance.getDataProviderInstanceId()));
		}

		_assertDDMDataProviderInstance(
			true,
			_ddmDataProviderInstanceLocalService.getDDMDataProviderInstance(
				_ddmDataProviderInstance.getDataProviderInstanceId()));
	}

	private void _testGetDataProviderInstances() throws Exception {
		User user = UserTestUtil.addGroupAdminUser(_group);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertDDMDataProviderInstances(
				true,
				_ddmDataProviderInstanceService.getDataProviderInstances(
					_group.getCompanyId(), new long[] {_group.getGroupId()},
					QueryUtil.ALL_POS, QueryUtil.ALL_POS));
		}
	}

	private void _testGetDataProviderInstancesWithoutUpdatePermission()
		throws Exception {

		User user = UserTestUtil.addGroupUser(
			_group, RoleConstants.SITE_MEMBER);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertDDMDataProviderInstances(
				false,
				_ddmDataProviderInstanceService.getDataProviderInstances(
					_group.getCompanyId(), new long[] {_group.getGroupId()},
					QueryUtil.ALL_POS, QueryUtil.ALL_POS));
		}
	}

	private void _testSearch() throws Exception {
		User user = UserTestUtil.addGroupAdminUser(_group);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertDDMDataProviderInstances(
				true,
				_ddmDataProviderInstanceService.search(
					_group.getCompanyId(), new long[] {_group.getGroupId()},
					null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));
			_assertDDMDataProviderInstances(
				true,
				_ddmDataProviderInstanceService.search(
					_group.getCompanyId(), new long[] {_group.getGroupId()},
					null, null, true, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null));
		}
	}

	private void _testSearchWithoutUpdatePermission() throws Exception {
		User user = UserTestUtil.addGroupUser(
			_group, RoleConstants.SITE_MEMBER);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_assertDDMDataProviderInstances(
				false,
				_ddmDataProviderInstanceService.search(
					_group.getCompanyId(), new long[] {_group.getGroupId()},
					null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));
			_assertDDMDataProviderInstances(
				false,
				_ddmDataProviderInstanceService.search(
					_group.getCompanyId(), new long[] {_group.getGroupId()},
					null, null, true, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null));
		}
	}

	@DeleteAfterTestRun
	private DDMDataProviderInstance _ddmDataProviderInstance;

	@Inject
	private DDMDataProviderInstanceLocalService
		_ddmDataProviderInstanceLocalService;

	@Inject
	private DDMDataProviderInstanceService _ddmDataProviderInstanceService;

	@DeleteAfterTestRun
	private Group _group;

}