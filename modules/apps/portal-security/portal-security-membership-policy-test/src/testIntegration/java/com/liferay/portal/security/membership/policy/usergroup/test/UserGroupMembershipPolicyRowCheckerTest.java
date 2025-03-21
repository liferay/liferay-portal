/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membership.policy.usergroup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.security.membership.policy.usergroup.BaseUserGroupMembershipPolicyTestCase;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.RenderResponseFactory;
import com.liferay.user.groups.admin.search.SetUserUserGroupChecker;
import com.liferay.user.groups.admin.search.UnsetUserUserGroupChecker;

import jakarta.portlet.RenderResponse;
import jakarta.portlet.filter.RenderResponseWrapper;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class UserGroupMembershipPolicyRowCheckerTest
	extends BaseUserGroupMembershipPolicyTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testIsCheckerDisabledWhenSettingForbiddenUserGroupToUser()
		throws Exception {

		long forbiddenUserGroupId = addForbiddenUserGroups()[0];

		UserGroup forbiddenUserGroup = UserGroupLocalServiceUtil.getUserGroup(
			forbiddenUserGroupId);

		SetUserUserGroupChecker setUserUserGroupChecker =
			new SetUserUserGroupChecker(
				_renderResponse, forbiddenUserGroup.getUserGroupId());

		User user = UserTestUtil.addUser();

		Assert.assertTrue(setUserUserGroupChecker.isDisabled(user));
	}

	@Test
	public void testIsCheckerDisabledWhenSettingRequiredUserGroupToUser()
		throws Exception {

		long requiredUserGroupId = addRequiredUserGroups()[0];

		UserGroup requiredUserGroup = UserGroupLocalServiceUtil.getUserGroup(
			requiredUserGroupId);

		SetUserUserGroupChecker setUserUserGroupChecker =
			new SetUserUserGroupChecker(
				_renderResponse, requiredUserGroup.getUserGroupId());

		User user = UserTestUtil.addUser();

		Assert.assertFalse(setUserUserGroupChecker.isDisabled(user));
	}

	@Test
	public void testIsCheckerDisabledWhenUnsettingForbiddenUserGroupToUser()
		throws Exception {

		long forbiddenUserGroupId = addForbiddenUserGroups()[0];

		UserGroup forbiddenUserGroup = UserGroupLocalServiceUtil.getUserGroup(
			forbiddenUserGroupId);

		UnsetUserUserGroupChecker setUserUserGroupChecker =
			new UnsetUserUserGroupChecker(_renderResponse, forbiddenUserGroup);

		User user = UserTestUtil.addUser();

		UserGroupLocalServiceUtil.addUserUserGroup(
			user.getUserId(), forbiddenUserGroupId);

		Assert.assertFalse(setUserUserGroupChecker.isDisabled(user));
	}

	@Test
	public void testIsCheckerDisabledWhenUnsettingRequiredUserGroupToUser()
		throws Exception {

		long requiredUserGroupId = addRequiredUserGroups()[0];

		UserGroup requiredUserGroup = UserGroupLocalServiceUtil.getUserGroup(
			requiredUserGroupId);

		UnsetUserUserGroupChecker setUserUserGroupChecker =
			new UnsetUserUserGroupChecker(_renderResponse, requiredUserGroup);

		User user = UserTestUtil.addUser();

		UserGroupLocalServiceUtil.addUserUserGroup(
			user.getUserId(), requiredUserGroupId);

		Assert.assertTrue(setUserUserGroupChecker.isDisabled(user));
	}

	private static final RenderResponse _renderResponse =
		new RenderResponseWrapper(RenderResponseFactory.create()) {

			@Override
			public String getNamespace() {
				return RandomTestUtil.randomString();
			}

		};

}