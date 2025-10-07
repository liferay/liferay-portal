/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wing Shun Chan
 * @author José Manuel Navarro
 * @author Drew Brokke
 */
@RunWith(Arquillian.class)
public class UserServiceWhenAddingUserWithNumericScreenNameTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testShouldAddUser() throws Exception {
		long numericScreenName = RandomTestUtil.nextLong();

		_user = UserTestUtil.addUser(String.valueOf(numericScreenName));

		Assert.assertEquals(
			String.valueOf(numericScreenName), _user.getScreenName());
	}

	@Test
	public void testShouldAddUserWhenScreenNameMatchesExistingGroupId()
		throws Exception {

		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser(String.valueOf(_group.getGroupId()));

		Assert.assertEquals(
			String.valueOf(_group.getGroupId()), _user.getScreenName());
	}

	@Test(expected = UserScreenNameException.MustNotBeNumeric.class)
	public void testShouldThrowException() throws Exception {
		try {
			PropsValues.USERS_SCREEN_NAME_ALLOW_NUMERIC = false;

			UserTestUtil.addUser(String.valueOf(RandomTestUtil.nextLong()));
		}
		finally {
			PropsValues.USERS_SCREEN_NAME_ALLOW_NUMERIC = GetterUtil.getBoolean(
				PropsUtil.get(PropsKeys.USERS_SCREEN_NAME_ALLOW_NUMERIC));
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _user;

}