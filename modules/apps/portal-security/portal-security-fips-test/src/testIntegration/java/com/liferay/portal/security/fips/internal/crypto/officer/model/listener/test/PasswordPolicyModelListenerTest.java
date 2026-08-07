/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.internal.crypto.officer.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.security.fips.constants.FIPSConstants;
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
public class PasswordPolicyModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDeletePasswordPolicy() throws Exception {
		_testDeletePasswordPolicy();

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			_portalInstanceLifecycleListener.portalInstanceRegistered(
				_companyLocalService.getCompany(
					TestPropsValues.getCompanyId()));

			Assert.assertThrows(
				ModelListenerException.class, this::_testDeletePasswordPolicy);

			Assert.assertNotNull(
				_passwordPolicyLocalService.fetchPasswordPolicy(
					TestPropsValues.getCompanyId(),
					FIPSConstants.PASSWORD_POLICY_NAME_CRYPTO_OFFICER));

			try (SafeCloseable safeCloseable2 =
					PortalInstances.
						setCompanyInDeletionProcessWithSafeCloseable(
							TestPropsValues.getCompanyId())) {

				_testDeletePasswordPolicy();
			}
		}
	}

	private void _testDeletePasswordPolicy() throws Exception {
		PasswordPolicy passwordPolicy =
			_passwordPolicyLocalService.fetchPasswordPolicy(
				TestPropsValues.getCompanyId(),
				FIPSConstants.PASSWORD_POLICY_NAME_CRYPTO_OFFICER);

		if (passwordPolicy == null) {
			PasswordPolicy defaultPasswordPolicy =
				_passwordPolicyLocalService.getDefaultPasswordPolicy(
					TestPropsValues.getCompanyId());

			passwordPolicy = _passwordPolicyLocalService.addPasswordPolicy(
				TestPropsValues.getUserId(), false,
				FIPSConstants.PASSWORD_POLICY_NAME_CRYPTO_OFFICER,
				FIPSConstants.PASSWORD_POLICY_NAME_CRYPTO_OFFICER, true, true,
				defaultPasswordPolicy.getMinAge(),
				defaultPasswordPolicy.isCheckSyntax(),
				defaultPasswordPolicy.isAllowDictionaryWords(),
				defaultPasswordPolicy.getMinAlphanumeric(),
				defaultPasswordPolicy.getMinLength(),
				defaultPasswordPolicy.getMinLowerCase(),
				defaultPasswordPolicy.getMinNumbers(),
				defaultPasswordPolicy.getMinSymbols(),
				defaultPasswordPolicy.getMinUpperCase(),
				defaultPasswordPolicy.getRegex(),
				defaultPasswordPolicy.isHistory(),
				defaultPasswordPolicy.getHistoryCount(),
				defaultPasswordPolicy.isExpireable(),
				defaultPasswordPolicy.getMaxAge(),
				defaultPasswordPolicy.getWarningTime(),
				defaultPasswordPolicy.getGraceLimit(), true, 10,
				defaultPasswordPolicy.getLockoutDuration(),
				defaultPasswordPolicy.getResetFailureCount(),
				defaultPasswordPolicy.getResetTicketMaxAge(),
				new ServiceContext());
		}

		_passwordPolicyLocalService.deletePasswordPolicy(passwordPolicy);

		Assert.assertNull(
			_passwordPolicyLocalService.fetchPasswordPolicy(
				TestPropsValues.getCompanyId(),
				FIPSConstants.PASSWORD_POLICY_NAME_CRYPTO_OFFICER));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private PasswordPolicyLocalService _passwordPolicyLocalService;

	@Inject(
		filter = "component.name=com.liferay.portal.security.fips.internal.instance.lifecycle.FIPSPortalInstanceLifecycleListener"
	)
	private PortalInstanceLifecycleListener _portalInstanceLifecycleListener;

}