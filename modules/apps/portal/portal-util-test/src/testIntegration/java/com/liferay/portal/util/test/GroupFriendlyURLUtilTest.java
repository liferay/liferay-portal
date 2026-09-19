/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.GroupFriendlyURLUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dante Wang
 */
@RunWith(Arquillian.class)
public class GroupFriendlyURLUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testFetchFriendlyURLGroupWithGroupFriendlyURL()
		throws Exception {

		_group = GroupTestUtil.addGroup();

		Group group = GroupFriendlyURLUtil.fetchFriendlyURLGroup(
			TestPropsValues.getCompanyId(), _group.getFriendlyURL());

		Assert.assertEquals(_group.getGroupId(), group.getGroupId());
	}

	@Test
	public void testFetchFriendlyURLGroupWithNullFriendlyURL()
		throws Exception {

		Assert.assertNull(
			GroupFriendlyURLUtil.fetchFriendlyURLGroup(
				TestPropsValues.getCompanyId(), null));
	}

	@Test
	public void testFetchFriendlyURLGroupWithUserScreenName() throws Exception {
		_user = UserTestUtil.addUser();

		Group userGroup = _user.getGroup();

		_groupLocalService.updateFriendlyURL(
			userGroup.getGroupId(),
			StringPool.SLASH + RandomTestUtil.randomString());

		Group group = GroupFriendlyURLUtil.fetchFriendlyURLGroup(
			TestPropsValues.getCompanyId(),
			StringPool.SLASH + _user.getScreenName());

		Assert.assertEquals(userGroup.getGroupId(), group.getGroupId());
	}

	@Test
	public void testFetchFriendlyURLGroupWithoutMatchingGroupOrUser()
		throws Exception {

		Assert.assertNull(
			GroupFriendlyURLUtil.fetchFriendlyURLGroup(
				TestPropsValues.getCompanyId(),
				StringPool.SLASH + RandomTestUtil.randomString()));
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private User _user;

}