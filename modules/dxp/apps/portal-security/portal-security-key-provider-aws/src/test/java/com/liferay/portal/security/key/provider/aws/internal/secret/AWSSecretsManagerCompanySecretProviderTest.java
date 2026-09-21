/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.secret;

import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Christopher Kian
 */
public class AWSSecretsManagerCompanySecretProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testIsAllowedCompany() {
		Assert.assertFalse(
			_awsSecretsManagerCompanySecretProvider.isAllowedCompany(
				CompanyConstants.SYSTEM));
		Assert.assertTrue(
			_awsSecretsManagerCompanySecretProvider.isAllowedCompany(
				RandomTestUtil.randomLong()));
	}

	private final AWSSecretsManagerCompanySecretProvider
		_awsSecretsManagerCompanySecretProvider =
			new AWSSecretsManagerCompanySecretProvider();

}