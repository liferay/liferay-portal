/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.avalara.connector.internal;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Caio Farias
 */
public class CommerceAvalaraConnectorImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetLicenseKey() {
		long companyId = RandomTestUtil.randomLong();
		String keyReferenceString = KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				KeyReference.Type.SECRET));
		String licenseKey = RandomTestUtil.randomString();

		SecretResolver secretResolver = Mockito.mock(SecretResolver.class);

		Mockito.when(
			secretResolver.resolve(Mockito.anyLong(), Mockito.any())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
		);

		Mockito.when(
			secretResolver.resolve(companyId, keyReferenceString)
		).thenReturn(
			licenseKey
		);

		CommerceAvalaraConnectorImpl commerceAvalaraConnectorImpl =
			new CommerceAvalaraConnectorImpl();

		ReflectionTestUtil.setFieldValue(
			commerceAvalaraConnectorImpl, "_secretResolver", secretResolver);

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			Assert.assertEquals(
				licenseKey,
				_getLicenseKey(
					commerceAvalaraConnectorImpl, keyReferenceString));
			Assert.assertEquals(
				licenseKey,
				_getLicenseKey(commerceAvalaraConnectorImpl, licenseKey));
		}
	}

	private String _getLicenseKey(
		CommerceAvalaraConnectorImpl commerceAvalaraConnectorImpl,
		String licenseKey) {

		return ReflectionTestUtil.invoke(
			commerceAvalaraConnectorImpl, "_getLicenseKey",
			new Class<?>[] {String.class}, licenseKey);
	}

}