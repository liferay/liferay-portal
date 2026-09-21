/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.internal.crypto.officer.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.service.PasswordPolicyRelLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalInstances;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuele Castro
 */
@RunWith(Arquillian.class)
public class RoleModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddUserRoles() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			Company company = _companyLocalService.getCompany(
				TestPropsValues.getCompanyId());

			_portalInstanceLifecycleListener.portalInstanceRegistered(company);

			Role role = _roleLocalService.fetchRole(
				company.getCompanyId(), RoleConstants.CRYPTO_OFFICER);

			_testAddUserRoles(false, role, UserTestUtil.addUser(company));
			_testAddUserRoles(
				true, role,
				UserTestUtil.addUser(company, RandomTestUtil.randomString()));

			Assert.assertThrows(
				ModelListenerException.class,
				() -> _roleLocalService.addUserRoles(
					TestPropsValues.getUserId(),
					new long[] {role.getRoleId()}));

			User adminUser = TestPropsValues.getUser();

			Assert.assertFalse(
				_roleLocalService.hasUserRole(
					adminUser.getUserId(), role.getRoleId()));
		}
	}

	@Test
	public void testDeleteRole() throws Exception {
		_testDeleteRole();

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			_portalInstanceLifecycleListener.portalInstanceRegistered(
				_companyLocalService.getCompany(
					TestPropsValues.getCompanyId()));

			Assert.assertThrows(
				ModelListenerException.class, this::_testDeleteRole);

			Assert.assertNotNull(
				_roleLocalService.fetchRole(
					TestPropsValues.getCompanyId(),
					RoleConstants.CRYPTO_OFFICER));

			try (SafeCloseable safeCloseable2 =
					PortalInstances.
						setCompanyInDeletionProcessWithSafeCloseable(
							TestPropsValues.getCompanyId())) {

				_testDeleteRole();
			}
		}
	}

	@Test
	public void testUnsetUserRoles() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			_portalInstanceLifecycleListener.portalInstanceRegistered(
				_companyLocalService.getCompany(
					TestPropsValues.getCompanyId()));

			String password = RandomTestUtil.randomString();
			Role role = _roleLocalService.fetchRole(
				TestPropsValues.getCompanyId(), RoleConstants.CRYPTO_OFFICER);

			User user = _userLocalService.addUser(
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
				false, password, password, false, RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				LocaleUtil.getDefault(), RandomTestUtil.randomString(),
				StringPool.BLANK, RandomTestUtil.randomString(), 0, 0, true, 1,
				1, 1970, StringPool.BLANK, UserConstants.TYPE_REGULAR,
				new long[] {TestPropsValues.getGroupId()}, null,
				new long[] {role.getRoleId()}, null, false,
				ServiceContextTestUtil.getServiceContext());

			Assert.assertTrue(user.isPasswordReset());

			PasswordPolicy passwordPolicy =
				_passwordPolicyLocalService.fetchPasswordPolicyByUserId(
					user.getUserId());

			Assert.assertEquals(10, passwordPolicy.getMaxFailure());
			Assert.assertFalse(passwordPolicy.isDefaultPolicy());

			Assert.assertNotNull(
				_passwordPolicyRelLocalService.fetchPasswordPolicyRel(
					User.class.getName(), user.getUserId()));

			_roleLocalService.unsetUserRoles(
				user.getUserId(), new long[] {role.getRoleId()});

			Assert.assertNull(
				_passwordPolicyRelLocalService.fetchPasswordPolicyRel(
					User.class.getName(), user.getUserId()));
		}
	}

	private void _testAddUserRoles(
			boolean expectedPasswordReset, Role role, User user)
		throws Exception {

		Assert.assertFalse(user.isPasswordReset());

		_roleLocalService.addUserRoles(
			user.getUserId(), new long[] {role.getRoleId()});

		user = _userLocalService.getUser(user.getUserId());

		Assert.assertEquals(expectedPasswordReset, user.isPasswordReset());

		PasswordPolicy passwordPolicy =
			_passwordPolicyLocalService.fetchPasswordPolicyByUserId(
				user.getUserId());

		Assert.assertEquals(10, passwordPolicy.getMaxFailure());
		Assert.assertFalse(passwordPolicy.isDefaultPolicy());
		Assert.assertTrue(passwordPolicy.isChangeable());
		Assert.assertTrue(passwordPolicy.isChangeRequired());
		Assert.assertTrue(passwordPolicy.isLockout());
	}

	private void _testDeleteRole() throws Exception {
		Role role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(), RoleConstants.CRYPTO_OFFICER);

		if (role == null) {
			role = _roleLocalService.addRole(
				null, TestPropsValues.getUserId(), null, 0,
				RoleConstants.CRYPTO_OFFICER, null, null,
				RoleConstants.TYPE_REGULAR, null, null);
		}

		_roleLocalService.deleteRole(role);

		Assert.assertNull(
			_roleLocalService.fetchRole(
				TestPropsValues.getCompanyId(), RoleConstants.CRYPTO_OFFICER));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private PasswordPolicyLocalService _passwordPolicyLocalService;

	@Inject
	private PasswordPolicyRelLocalService _passwordPolicyRelLocalService;

	@Inject(
		filter = "component.name=com.liferay.portal.security.fips.internal.instance.lifecycle.FIPSPortalInstanceLifecycleListener"
	)
	private PortalInstanceLifecycleListener _portalInstanceLifecycleListener;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}