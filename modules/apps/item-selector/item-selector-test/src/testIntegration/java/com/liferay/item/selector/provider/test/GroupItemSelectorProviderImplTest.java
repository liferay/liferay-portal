/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.item.selector.provider.GroupItemSelectorProvider;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class GroupItemSelectorProviderImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();
	}

	@Test
	@TestInfo("LPD-110411")
	public void testGetGroups() throws Exception {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, permissionChecker)) {

			List<Group> groups = _groupItemSelectorProvider.getGroups(
				_group.getCompanyId(), _group.getGroupId(), null, 0, 20);

			Company company = CompanyLocalServiceUtil.getCompany(
				_group.getCompanyId());

			Assert.assertTrue(groups.contains(company.getGroup()));
		}

		List<Group> groups = _groupItemSelectorProvider.getGroups(
			_group.getCompanyId(), _group.getGroupId(), null, 0, 20);

		int originalGroupsSize = groups.size();

		Assert.assertEquals(
			groups.toString(), originalGroupsSize, groups.size());

		Assert.assertEquals(
			originalGroupsSize,
			_groupItemSelectorProvider.getGroupsCount(
				_group.getCompanyId(), _group.getGroupId(), null));

		_childGroup = GroupTestUtil.addGroup(_group.getGroupId());

		groups = _groupItemSelectorProvider.getGroups(
			_group.getCompanyId(), _group.getGroupId(), null, 0, 20);

		Assert.assertEquals(
			groups.toString(), originalGroupsSize + 1, groups.size());

		Assert.assertEquals(
			originalGroupsSize + 1,
			_groupItemSelectorProvider.getGroupsCount(
				_group.getCompanyId(), _group.getGroupId(), null));

		_childGroup.setActive(false);

		_childGroup = _groupLocalService.updateGroup(_childGroup);

		groups = _groupItemSelectorProvider.getGroups(
			_group.getCompanyId(), _group.getGroupId(), null, 0, 20);

		Assert.assertEquals(
			groups.toString(), originalGroupsSize, groups.size());

		Assert.assertEquals(
			originalGroupsSize,
			_groupItemSelectorProvider.getGroupsCount(
				_group.getCompanyId(), _group.getGroupId(), null));
	}

	@Test
	public void testGetIcon() {
		Assert.assertEquals("sites", _groupItemSelectorProvider.getIcon());
	}

	@Test
	public void testGetLabel() {
		Assert.assertEquals(
			"Site", _groupItemSelectorProvider.getLabel(LocaleUtil.US));
	}

	@DeleteAfterTestRun
	private Group _childGroup;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.item.selector.internal.provider.GroupItemSelectorProviderImpl"
	)
	private GroupItemSelectorProvider _groupItemSelectorProvider;

	@Inject
	private GroupLocalService _groupLocalService;

	private User _user;

}