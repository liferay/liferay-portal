/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.password.policies.admin.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Dictionary;

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
	public void testPasswordPolicyGettersWithLDAPUser() throws Exception {
		boolean ldapPasswordPolicyEnabled = _updateLDAPPasswordPolicyEnabled(
			false);

		User user = UserTestUtil.addUser();

		user.setLdapServerId(1);

		user = _userLocalService.updateUser(user);

		try {
			Assert.assertNotNull(
				_passwordPolicyLocalService.getDefaultPasswordPolicy(
					TestPropsValues.getCompanyId()));
			Assert.assertNotNull(
				_passwordPolicyLocalService.getPasswordPolicy(
					TestPropsValues.getCompanyId(), true));
			Assert.assertNotNull(
				_passwordPolicyLocalService.getPasswordPolicy(
					TestPropsValues.getCompanyId(), new long[0]));
			Assert.assertNotNull(
				_passwordPolicyLocalService.getPasswordPolicyByUser(user));
			Assert.assertNotNull(
				_passwordPolicyLocalService.getPasswordPolicyByUserId(
					user.getUserId()));
		}
		finally {
			_updateLDAPPasswordPolicyEnabled(ldapPasswordPolicyEnabled);
		}
	}

	@Test
	public void testPasswordPolicyGettersWithLDAPUserAndLDAPPasswordPolicy()
		throws Exception {

		boolean ldapPasswordPolicyEnabled = _updateLDAPPasswordPolicyEnabled(
			true);

		User user = UserTestUtil.addUser();

		user.setLdapServerId(1);

		user = _userLocalService.updateUser(user);

		try {
			Assert.assertNotNull(
				_passwordPolicyLocalService.getDefaultPasswordPolicy(
					TestPropsValues.getCompanyId()));
			Assert.assertNotNull(
				_passwordPolicyLocalService.getPasswordPolicy(
					TestPropsValues.getCompanyId(), true));
			Assert.assertNotNull(
				_passwordPolicyLocalService.getPasswordPolicy(
					TestPropsValues.getCompanyId(), new long[0]));
			Assert.assertNull(
				_passwordPolicyLocalService.getPasswordPolicyByUser(user));
			Assert.assertNull(
				_passwordPolicyLocalService.getPasswordPolicyByUserId(
					user.getUserId()));
		}
		finally {
			_updateLDAPPasswordPolicyEnabled(ldapPasswordPolicyEnabled);
		}
	}

	@Test
	public void testPasswordPolicyGettersWithPortalUserAndLDAPPasswordPolicy()
		throws Exception {

		boolean ldapPasswordPolicyEnabled = _updateLDAPPasswordPolicyEnabled(
			true);

		User user = UserTestUtil.addUser();

		try {
			Assert.assertNotNull(
				_passwordPolicyLocalService.getDefaultPasswordPolicy(
					TestPropsValues.getCompanyId()));
			Assert.assertNotNull(
				_passwordPolicyLocalService.getPasswordPolicy(
					TestPropsValues.getCompanyId(), true));
			Assert.assertNotNull(
				_passwordPolicyLocalService.getPasswordPolicy(
					TestPropsValues.getCompanyId(), new long[0]));
			Assert.assertNotNull(
				_passwordPolicyLocalService.getPasswordPolicyByUser(user));
			Assert.assertNotNull(
				_passwordPolicyLocalService.getPasswordPolicyByUserId(
					user.getUserId()));
		}
		finally {
			_updateLDAPPasswordPolicyEnabled(ldapPasswordPolicyEnabled);
		}
	}

	private boolean _updateLDAPPasswordPolicyEnabled(
			boolean passwordPolicyEnabled)
		throws PortalException {

		Dictionary<String, Object> configurations =
			_ldapAuthConfigurationProvider.getConfigurationProperties(
				TestPropsValues.getCompanyId());

		boolean existingValue = GetterUtil.getBoolean(
			configurations.put("passwordPolicyEnabled", passwordPolicyEnabled));

		_ldapAuthConfigurationProvider.updateProperties(
			TestPropsValues.getCompanyId(), configurations);

		return existingValue;
	}

	@Inject(
		filter = "factoryPid=com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration"
	)
	private ConfigurationProvider<LDAPAuthConfiguration>
		_ldapAuthConfigurationProvider;

	@Inject
	private PasswordPolicyLocalService _passwordPolicyLocalService;

	@Inject
	private UserLocalService _userLocalService;

}