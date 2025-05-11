/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.service.persistence.CompanyUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.PrefsPropsTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
public class UserServiceWhenCompanySecurityStrangersWithMXDisabledTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_safeCloseable = PrefsPropsTestUtil.swapWithSafeCloseable(
			TestPropsValues.getCompanyId(),
			PropsKeys.COMPANY_SECURITY_STRANGERS_WITH_MX,
			Boolean.FALSE.toString());
	}

	@AfterClass
	public static void tearDownClass() {
		_safeCloseable.close();

		CompanyUtil.clearCache();
	}

	@Before
	public void setUp() {
		CompanyUtil.clearCache();
	}

	@After
	public void tearDown() {
		CompanyUtil.clearCache();
	}

	@Test(expected = UserEmailAddressException.MustNotUseCompanyMx.class)
	public void testShouldNotAddUser() throws Exception {
		String name = PrincipalThreadLocal.getName();

		try {
			PrincipalThreadLocal.setName(0);

			_user = UserTestUtil.addUser(true);
		}
		finally {
			PrincipalThreadLocal.setName(name);
		}
	}

	@Test(expected = UserEmailAddressException.MustNotUseCompanyMx.class)
	public void testShouldNotUpdateEmailAddress() throws Exception {
		String name = PrincipalThreadLocal.getName();

		try {
			_updateEmailAddress();
		}
		finally {
			PrincipalThreadLocal.setName(name);
		}
	}

	@Test(expected = UserEmailAddressException.MustNotUseCompanyMx.class)
	public void testShouldNotUpdateUser() throws Exception {
		String name = PrincipalThreadLocal.getName();

		try {
			_updateUser();
		}
		finally {
			PrincipalThreadLocal.setName(name);
		}
	}

	@Test
	public void testShouldUpdateEmailAddress() throws Exception {
		String name = PrincipalThreadLocal.getName();

		try (SafeCloseable safeCloseable =
				PrefsPropsTestUtil.swapWithSafeCloseable(
					TestPropsValues.getCompanyId(),
					PropsKeys.COMPANY_SECURITY_STRANGERS,
					Boolean.FALSE.toString())) {

			_updateEmailAddress();
		}
		finally {
			PrincipalThreadLocal.setName(name);
		}
	}

	@Test
	public void testShouldUpdateUser() throws Exception {
		String name = PrincipalThreadLocal.getName();

		try (SafeCloseable safeCloseable =
				PrefsPropsTestUtil.swapWithSafeCloseable(
					TestPropsValues.getCompanyId(),
					PropsKeys.COMPANY_SECURITY_STRANGERS,
					Boolean.FALSE.toString())) {

			_updateUser();
		}
		finally {
			PrincipalThreadLocal.setName(name);
		}
	}

	private void _updateEmailAddress() throws Exception {
		_user = UserTestUtil.addUser(false);

		PrincipalThreadLocal.setName(_user.getUserId());

		String emailAddress =
			"UserServiceTest." + RandomTestUtil.nextLong() + "@liferay.com";

		_userService.updateEmailAddress(
			_user.getUserId(), _user.getPassword(), emailAddress, emailAddress,
			new ServiceContext());
	}

	private void _updateUser() throws Exception {
		_user = UserTestUtil.addUser(false);

		PrincipalThreadLocal.setName(_user.getUserId());

		UserTestUtil.updateUser(_user);
	}

	private static SafeCloseable _safeCloseable;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private UserService _userService;

}