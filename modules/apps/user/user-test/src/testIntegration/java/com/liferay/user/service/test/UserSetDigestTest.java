/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Calendar;
import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jesse Yeh
 */
@RunWith(Arquillian.class)
public class UserSetDigestTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddUserWithWorkflowWithPrerequisites() throws Exception {
		_testAddUserWithWorkflowHelper(
			RandomTestUtil.randomString(), _generateRandomEmailAddress());
	}

	@Test(expected = UserEmailAddressException.class)
	public void testAddUserWithWorkflowWithoutEmailAddress() throws Exception {
		_testAddUserWithWorkflowHelper(RandomTestUtil.randomString(), null);
	}

	@Test(expected = PortalException.class)
	public void testAddUserWithWorkflowWithoutPrerequisites() throws Exception {
		_testAddUserWithWorkflowHelper(null, null);
	}

	@Test(expected = UserScreenNameException.class)
	public void testAddUserWithWorkflowWithoutScreenName() throws Exception {
		_testAddUserWithWorkflowHelper(null, _generateRandomEmailAddress());
	}

	@Test
	public void testDigestIsEmptyAfterCreatingUser() throws Exception {
		User user = _testAddUserWithWorkflowHelper(
			RandomTestUtil.randomString(), _generateRandomEmailAddress());

		String digest = user.getDigest();

		Assert.assertTrue(digest.isEmpty());
	}

	private String _generateRandomEmailAddress() {
		return StringBundler.concat(
			RandomTestUtil.randomString(), RandomTestUtil.nextLong(), "@",
			RandomTestUtil.randomString(), ".com");
	}

	private User _testAddUserWithWorkflowHelper(
			String screenName, String emailAddress)
		throws Exception {

		long creatorUserId = 0;

		String randomString = RandomTestUtil.randomString();

		boolean autoPassword = false;
		String password1 = randomString;
		String password2 = randomString;

		boolean autoScreenName = false;
		Locale locale = LocaleUtil.getDefault();
		String firstName = RandomTestUtil.randomString();
		String middleName = RandomTestUtil.randomString();
		String lastName = RandomTestUtil.randomString();
		long prefixListTypeId = 0;
		long suffixListTypeId = 0;
		boolean male = true;
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = StringPool.BLANK;
		long[] groupIds = null;
		long[] organizationIds = null;
		long[] roleIds = null;
		long[] userGroupIds = null;
		boolean sendEmail = false;

		return _userLocalService.addUserWithWorkflow(
			creatorUserId, TestPropsValues.getCompanyId(), autoPassword,
			password1, password2, autoScreenName, screenName, emailAddress,
			locale, firstName, middleName, lastName, prefixListTypeId,
			suffixListTypeId, male, birthdayMonth, birthdayDay, birthdayYear,
			jobTitle, UserConstants.TYPE_REGULAR, groupIds, organizationIds,
			roleIds, userGroupIds, sendEmail, new ServiceContext());
	}

	@Inject
	private UserLocalService _userLocalService;

}