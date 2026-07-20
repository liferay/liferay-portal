/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.password.policy.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.security.ldap.test.util.configuration.LDAPAuthConfigurationProviderTemporarySwapper;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christopher Kian
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class PasswordPolicyLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testFetchPasswordPolicyByUserIdWithLDAPPasswordPolicy()
		throws Exception {

		try (LDAPAuthConfigurationProviderTemporarySwapper
				ldapAuthConfigurationProviderTemporarySwapper =
					new LDAPAuthConfigurationProviderTemporarySwapper(
						TestPropsValues.getCompanyId(), true)) {

			User user = _addUser(true);

			Assert.assertNull(
				_passwordPolicyLocalService.fetchPasswordPolicyByUserId(
					user.getUserId()));

			user = _addUser(false);

			Assert.assertNotNull(
				_passwordPolicyLocalService.fetchPasswordPolicyByUserId(
					user.getUserId()));
		}
	}

	@Test
	public void testFetchPasswordPolicyByUserIdWithoutLDAPPasswordPolicy()
		throws Exception {

		try (LDAPAuthConfigurationProviderTemporarySwapper
				ldapAuthConfigurationProviderTemporarySwapper =
					new LDAPAuthConfigurationProviderTemporarySwapper(
						TestPropsValues.getCompanyId(), false)) {

			User user = _addUser(true);

			Assert.assertNotNull(
				_passwordPolicyLocalService.fetchPasswordPolicyByUserId(
					user.getUserId()));

			user = _addUser(false);

			Assert.assertNotNull(
				_passwordPolicyLocalService.fetchPasswordPolicyByUserId(
					user.getUserId()));
		}
	}

	@Test
	public void testFetchPasswordPolicyByUserWithLDAPPasswordPolicy()
		throws Exception {

		try (LDAPAuthConfigurationProviderTemporarySwapper
				ldapAuthConfigurationProviderTemporarySwapper =
					new LDAPAuthConfigurationProviderTemporarySwapper(
						TestPropsValues.getCompanyId(), true)) {

			Assert.assertNull(
				_passwordPolicyLocalService.fetchPasswordPolicyByUser(
					_addUser(true)));
			Assert.assertNotNull(
				_passwordPolicyLocalService.fetchPasswordPolicyByUser(
					_addUser(false)));
		}
	}

	@Test
	public void testFetchPasswordPolicyByUserWithoutLDAPPasswordPolicy()
		throws Exception {

		try (LDAPAuthConfigurationProviderTemporarySwapper
				ldapAuthConfigurationProviderTemporarySwapper =
					new LDAPAuthConfigurationProviderTemporarySwapper(
						TestPropsValues.getCompanyId(), false)) {

			Assert.assertNotNull(
				_passwordPolicyLocalService.fetchPasswordPolicyByUser(
					_addUser(true)));
			Assert.assertNotNull(
				_passwordPolicyLocalService.fetchPasswordPolicyByUser(
					_addUser(false)));
		}
	}

	@Test
	public void testGetDefaultPasswordPolicyWithLDAPPasswordPolicy()
		throws Exception {

		try (LDAPAuthConfigurationProviderTemporarySwapper
				ldapAuthConfigurationProviderTemporarySwapper =
					new LDAPAuthConfigurationProviderTemporarySwapper(
						TestPropsValues.getCompanyId(), true)) {

			Assert.assertNotNull(
				_passwordPolicyLocalService.getDefaultPasswordPolicy(
					TestPropsValues.getCompanyId()));
		}
	}

	@Test
	public void testGetPasswordPolicyWithLDAPPasswordPolicy1()
		throws Exception {

		try (LDAPAuthConfigurationProviderTemporarySwapper
				ldapAuthConfigurationProviderTemporarySwapper =
					new LDAPAuthConfigurationProviderTemporarySwapper(
						TestPropsValues.getCompanyId(), true)) {

			Assert.assertNotNull(
				_passwordPolicyLocalService.getDefaultPasswordPolicy(
					TestPropsValues.getCompanyId()));
		}
	}

	@Test
	public void testGetPasswordPolicyWithLDAPPasswordPolicy2()
		throws Exception {

		try (LDAPAuthConfigurationProviderTemporarySwapper
				ldapAuthConfigurationProviderTemporarySwapper =
					new LDAPAuthConfigurationProviderTemporarySwapper(
						TestPropsValues.getCompanyId(), true)) {

			Assert.assertNotNull(
				_passwordPolicyLocalService.getPasswordPolicy(
					TestPropsValues.getCompanyId(), new long[0]));
		}
	}

	private User _addUser(boolean ldapUser) throws Exception {
		User user = UserTestUtil.addUser();

		user.setLdapServerId(ldapUser ? 1 : -1);

		return _userLocalService.updateUser(user);
	}

	@Inject
	private PasswordPolicyLocalService _passwordPolicyLocalService;

	@Inject
	private UserLocalService _userLocalService;

}