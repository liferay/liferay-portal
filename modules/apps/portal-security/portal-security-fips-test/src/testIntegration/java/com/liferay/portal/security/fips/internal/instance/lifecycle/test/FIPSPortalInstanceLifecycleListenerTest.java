/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.internal.instance.lifecycle.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.security.fips.constants.FIPSConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuele Castro
 */
@RunWith(Arquillian.class)
public class FIPSPortalInstanceLifecycleListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		Assume.assumeFalse(PropsValues.FIPS_ENABLED);

		Assert.assertNull(
			_passwordPolicyLocalService.fetchPasswordPolicy(
				TestPropsValues.getCompanyId(),
				FIPSConstants.PASSWORD_POLICY_NAME_CRYPTO_OFFICER));
		Assert.assertNull(
			_roleLocalService.fetchRole(
				TestPropsValues.getCompanyId(), RoleConstants.CRYPTO_OFFICER));

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			Company company = _companyLocalService.getCompany(
				TestPropsValues.getCompanyId());

			_portalInstanceLifecycleListener.portalInstanceRegistered(company);

			PasswordPolicy passwordPolicy =
				_passwordPolicyLocalService.fetchPasswordPolicy(
					company.getCompanyId(),
					FIPSConstants.PASSWORD_POLICY_NAME_CRYPTO_OFFICER);

			Assert.assertNotNull(passwordPolicy);

			Assert.assertEquals(10, passwordPolicy.getMaxFailure());
			Assert.assertFalse(passwordPolicy.isDefaultPolicy());
			Assert.assertTrue(passwordPolicy.isChangeable());
			Assert.assertTrue(passwordPolicy.isChangeRequired());
			Assert.assertTrue(passwordPolicy.isLockout());

			Assert.assertNotNull(
				_roleLocalService.fetchRole(
					TestPropsValues.getCompanyId(),
					RoleConstants.CRYPTO_OFFICER));
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private PasswordPolicyLocalService _passwordPolicyLocalService;

	@Inject(
		filter = "component.name=com.liferay.portal.security.fips.internal.instance.lifecycle.FIPSPortalInstanceLifecycleListener"
	)
	private PortalInstanceLifecycleListener _portalInstanceLifecycleListener;

	@Inject
	private RoleLocalService _roleLocalService;

}