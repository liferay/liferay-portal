/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.portlet.action;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.math.BigInteger;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Manuele Castro
 */
public class UpdateCertificateMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_actionCommand = new UpdateCertificateMVCActionCommand();
	}

	@After
	public void tearDown() throws Exception {
		if (_autoCloseable != null) {
			_autoCloseable.close();

			_autoCloseable = null;
		}
	}

	@Test
	public void testIsValidX509Certificate() {
		Assert.assertTrue(
			_isValidX509Certificate(_mockCertificateWithRSAKey(1024)));

		X509Certificate x509Certificate = Mockito.mock(X509Certificate.class);

		Mockito.when(
			x509Certificate.getPublicKey()
		).thenReturn(
			Mockito.mock(DSAPublicKey.class)
		);

		Assert.assertTrue(_isValidX509Certificate(x509Certificate));

		_autoCloseable = ReflectionTestUtil.setFieldValueWithAutoCloseable(
			PropsValues.class, "FIPS_ENABLED", true);

		Assert.assertFalse(
			_isValidX509Certificate(_mockCertificateWithRSAKey(1024)));
		Assert.assertTrue(
			_isValidX509Certificate(_mockCertificateWithRSAKey(2048)));
		Assert.assertTrue(
			_isValidX509Certificate(_mockCertificateWithRSAKey(4096)));

		Assert.assertFalse(_isValidX509Certificate(x509Certificate));

		Mockito.when(
			x509Certificate.getPublicKey()
		).thenReturn(
			Mockito.mock(PublicKey.class)
		);

		Assert.assertFalse(_isValidX509Certificate(x509Certificate));
	}

	private boolean _isValidX509Certificate(X509Certificate x509Certificate) {
		return ReflectionTestUtil.invoke(
			_actionCommand, "_isValidX509Certificate",
			new Class<?>[] {X509Certificate.class}, x509Certificate);
	}

	private X509Certificate _mockCertificateWithRSAKey(int keySize) {
		X509Certificate x509Certificate = Mockito.mock(X509Certificate.class);

		RSAPublicKey rsaPublicKey = Mockito.mock(RSAPublicKey.class);

		Mockito.when(
			rsaPublicKey.getModulus()
		).thenReturn(
			BigInteger.ONE.shiftLeft(keySize - 1)
		);

		Mockito.when(
			x509Certificate.getPublicKey()
		).thenReturn(
			rsaPublicKey
		);

		return x509Certificate;
	}

	private UpdateCertificateMVCActionCommand _actionCommand;
	private AutoCloseable _autoCloseable;

}