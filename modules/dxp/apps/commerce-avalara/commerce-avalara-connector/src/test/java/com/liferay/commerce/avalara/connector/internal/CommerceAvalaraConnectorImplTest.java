/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.avalara.connector.internal;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import net.avalara.avatax.rest.client.AvaTaxClient;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedConstruction;
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
	public void testGetAvaTaxClient() {
		CommerceAvalaraConnectorImpl commerceAvalaraConnectorImpl =
			new CommerceAvalaraConnectorImpl();

		SecretResolver secretResolver = Mockito.mock(SecretResolver.class);

		long companyId = RandomTestUtil.randomLong();
		String keyReferenceString = KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				KeyReference.Type.SECRET));
		String licenseKey = RandomTestUtil.randomString();

		Mockito.when(
			secretResolver.resolve(companyId, keyReferenceString)
		).thenReturn(
			licenseKey
		);

		ReflectionTestUtil.setFieldValue(
			commerceAvalaraConnectorImpl, "_secretResolver", secretResolver);

		try (MockedConstruction<AvaTaxClient> avaTaxClientMockedConstruction =
				Mockito.mockConstruction(AvaTaxClient.class);
			SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			String accountNumber = RandomTestUtil.randomString();

			ReflectionTestUtil.invoke(
				commerceAvalaraConnectorImpl, "_getAvaTaxClient",
				new Class<?>[] {String.class, String.class, String.class},
				accountNumber, keyReferenceString,
				RandomTestUtil.randomString());

			List<AvaTaxClient> avaTaxClients =
				avaTaxClientMockedConstruction.constructed();

			String securityHeader = StringBundler.concat(
				accountNumber, StringPool.COLON, licenseKey);

			Mockito.verify(
				avaTaxClients.get(0)
			).withSecurity(
				Base64.encode(securityHeader.getBytes())
			);
		}
	}

}